package com.studyfocus.controller;

import com.studyfocus.service.CameraService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class VideoController {

    private final CameraService cameraService;

    public VideoController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping(value = "/video", produces = "multipart/x-mixed-replace; boundary=frame")
    public void stream(HttpServletResponse response) throws IOException {

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("multipart/x-mixed-replace; boundary=frame");

        ServletOutputStream out = response.getOutputStream();

        try {
            while (true) {
                Mat frame = cameraService.getFrame();
                if (frame.empty()) continue;

                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".jpg", frame, buffer);

                out.write(("--frame\r\n").getBytes());
                out.write("Content-Type: image/jpeg\r\n\r\n".getBytes());
                out.write(buffer.toArray());
                out.write("\r\n".getBytes());

                out.flush();
                Thread.sleep(100); // ~10 FPS
            }
        } catch (Exception e) {
            System.out.println("📴 Stream stopped");
        }
    }
}
