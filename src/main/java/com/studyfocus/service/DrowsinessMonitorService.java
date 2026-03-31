package com.studyfocus.service;

import jakarta.annotation.PostConstruct;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


@Service
public class DrowsinessMonitorService {

    // ===== SHARED STATE =====
    private volatile boolean faceDetected = false;
    private volatile boolean drowsy = false;

    // ===== FRAME COUNTERS =====
    private int noFaceFrames = 0;
    private int closedEyeFrames = 0;

    private static final int NO_FACE_THRESHOLD = 8;   // ~2.5 sec
    private static final int DROWSY_THRESHOLD = 6;    // ~2 sec

    private final CameraService cameraService;
    private final CascadeClassifier faceDetector;
    private final CascadeClassifier eyeDetector;

    @Autowired
    public DrowsinessMonitorService(CameraService cameraService) {
        this.cameraService = cameraService;

        faceDetector = new CascadeClassifier(resolveCascade("haarcascade/haarcascade_frontalface_default.xml"));
        eyeDetector  = new CascadeClassifier(resolveCascade("haarcascade/haarcascade_eye.xml"));

        if (faceDetector.empty()) System.err.println("⚠ Face cascade failed to load!");
        else System.out.println("✅ Face cascade loaded");

        if (eyeDetector.empty()) System.err.println("⚠ Eye cascade failed to load!");
        else System.out.println("✅ Eye cascade loaded");
    }

    /**
     * Resolves a classpath resource to an absolute temp file path so OpenCV can load it.
     * Works in both IDE (file system) and JAR (classpath) deployments.
     */
    private String resolveCascade(String resourcePath) {
        try {
            ClassPathResource res = new ClassPathResource(resourcePath);
            Path tmpFile = Files.createTempFile("cascade_", ".xml");
            tmpFile.toFile().deleteOnExit();
            Files.copy(res.getInputStream(), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            return tmpFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cascade: " + resourcePath, e);
        }
    }

    // ================= BACKGROUND THREAD =================
    @PostConstruct
    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                analyzeFrame();
                try { Thread.sleep(250); } catch (Exception ignored) {}
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ================= CORE CV =================
    private void analyzeFrame() {
        Mat frame = cameraService.getFrame();
        if (frame == null || frame.empty()) return;

        Mat gray = new Mat();
        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();
        Mat eyeROI = null;

        try {
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);

            faceDetector.detectMultiScale(
                    gray, faces, 1.1, 5, 0,
                    new Size(80, 80), new Size()
            );

            Rect[] faceArray = faces.toArray();

            // ---------- NO FACE ----------
            if (faceArray.length == 0) {
                noFaceFrames++;
                closedEyeFrames = 0;
                if (noFaceFrames >= NO_FACE_THRESHOLD) {
                    faceDetected = false;
                    drowsy = false;
                }
                return;
            }

            // ---------- FACE FOUND ----------
            faceDetected = true;
            noFaceFrames = 0;

            // Take largest face
            Rect face = faceArray[0];
            for (Rect r : faceArray)
                if (r.area() > face.area()) face = r;

            // ---------- EYE REGION (TOP HALF) ----------
            Rect eyeRegion = new Rect(
                    face.x,
                    face.y,
                    face.width,
                    (int)(face.height * 0.55)
            );

            eyeROI = gray.submat(eyeRegion);

            eyeDetector.detectMultiScale(
                    eyeROI, eyes, 1.1, 5, 0,
                    new Size(25, 15), new Size()
            );

            Rect[] eyeArray = eyes.toArray();

            if (eyeArray.length == 0) {
                closedEyeFrames++;
            } else {
                closedEyeFrames = 0;
            }

            drowsy = closedEyeFrames >= DROWSY_THRESHOLD;

        } catch (Exception e) {
            System.out.println("⚠️ CV skip: " + e.getMessage());
        } finally {
            // CRITICAL: Explicitly release native memory
            if (gray != null) gray.release();
            if (faces != null) faces.release();
            if (eyes != null) eyes.release();
            if (eyeROI != null) eyeROI.release();
        }
    }

    // ================= READERS =================
    public boolean isFaceDetected() {
        return faceDetected;
    }

    public boolean isDrowsy() {
        return drowsy;
    }
}

