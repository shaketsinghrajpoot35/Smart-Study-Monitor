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
import org.springframework.stereotype.Service;

@Service
public class CameraService {

    private volatile Mat currentFrame;

    public CameraService() {
        // Initialize with an empty frame
        currentFrame = new Mat();
        System.out.println("📷 CameraService initialized (Remote mode)");
    }

    /**
     * Updates the current frame with a new one received from the frontend.
     * Releases previous frame memory immediately to save space.
     */
    public synchronized void updateFrame(Mat newFrame) {
        if (newFrame != null && !newFrame.empty()) {
            if (this.currentFrame != null) {
                this.currentFrame.release(); 
            }
            this.currentFrame = newFrame;
        }
    }

    /**
     * Retrieves the latest frame reference.
     * The processing service is responsible for copying if needed.
     */
    public synchronized Mat getFrame() {
        return currentFrame;
    }
}

