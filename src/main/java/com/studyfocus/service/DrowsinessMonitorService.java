package com.studyfocus.service;

import jakarta.annotation.PostConstruct;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

        faceDetector = new CascadeClassifier(
                "src/main/resources/haarcascade/haarcascade_frontalface_default.xml"
        );
        eyeDetector = new CascadeClassifier(
                "src/main/resources/haarcascade/haarcascade_eye.xml"
        );

        System.out.println("✅ Face cascade loaded");
        System.out.println("✅ Eye cascade loaded");
    }

    // ================= BACKGROUND THREAD =================
    @PostConstruct
    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                analyzeFrame();
                try { Thread.sleep(300); } catch (Exception ignored) {}
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ================= CORE CV =================
    private void analyzeFrame() {
        try {
            Mat frame = cameraService.getFrame();
            if (frame.empty()) return;

            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);

            MatOfRect faces = new MatOfRect();
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
                    (int)(face.height * 0.45)
            );

            Mat eyeROI = gray.submat(eyeRegion);

            MatOfRect eyes = new MatOfRect();
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

