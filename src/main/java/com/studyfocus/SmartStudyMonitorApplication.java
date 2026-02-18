package com.studyfocus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // ✅ THIS IS STEP 3 (VERY IMPORTANT)
public class SmartStudyMonitorApplication {

    static {
        try {
            // Load OpenCV Java DLL
            System.load("C:/opencv/build/java/x64/opencv_java490.dll");

            // Load OpenCV video backend DLLs
            System.load("C:/opencv/build/x64/vc16/bin/opencv_videoio_ffmpeg490_64.dll");

            System.out.println("✅ OpenCV native libraries loaded manually");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Failed to load OpenCV native libs");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartStudyMonitorApplication.class, args);
    }
}
