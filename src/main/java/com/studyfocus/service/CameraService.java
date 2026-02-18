//package com.studyfocus.service;
//
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CameraService {
//
//    private VideoCapture camera;
//    private Mat frame;
//
//    public CameraService() {
//        camera = new VideoCapture(0); // 0 = default webcam
//        frame = new Mat();
//
//        if (!camera.isOpened()) {
//            System.out.println("❌ Camera not opened");
//        } else {
//            System.out.println("✅ Camera opened successfully");
//        }
//    }
//
//    // Get latest frame from webcam
//    public Mat getFrame() {
//        if (camera.isOpened()) {
//            camera.read(frame);
//        }
//        return frame;
//    }
//}

package com.studyfocus.service;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

@Service
public class CameraService {

    private final VideoCapture camera;

    public CameraService() {
        camera = new VideoCapture(0); // default webcam
        if (!camera.isOpened()) {
            throw new RuntimeException("❌ Cannot open camera");
        }
        System.out.println("📷 Camera opened");
    }

    public synchronized Mat getFrame() {
        Mat frame = new Mat();
        camera.read(frame);
        return frame;
    }
}

