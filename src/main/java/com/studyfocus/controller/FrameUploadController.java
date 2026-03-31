package com.studyfocus.controller;

import com.studyfocus.service.CameraService;
import com.studyfocus.service.NoiseMonitorService;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

@RestController
public class FrameUploadController {

    private final CameraService cameraService;
    private final NoiseMonitorService noiseMonitorService;

    public FrameUploadController(CameraService cameraService, NoiseMonitorService noiseMonitorService) {
        this.cameraService = cameraService;
        this.noiseMonitorService = noiseMonitorService;
    }

    @PostMapping("/api/camera/frame")
    public void uploadFrame(@RequestBody Map<String, Object> payload) {
        try {
            // Handle Noise Status
            if (payload.containsKey("isNoisy")) {
                boolean isNoisy = (boolean) payload.get("isNoisy");
                noiseMonitorService.updateNoise(isNoisy);
            }

            // Handle Camera Frame
            String base64Image = (String) payload.get("image");
            if (base64Image == null || base64Image.isEmpty()) return;

            // Strip data:image/jpeg;base64, if present
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Mat frame = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
            
            if (frame != null && !frame.empty()) {
                cameraService.updateFrame(frame);
            }
        } catch (Exception e) {
            // Silently fail to avoid flooding logs with frame conversion errors
            // System.err.println("Frame upload failed: " + e.getMessage());
        }
    }
}
