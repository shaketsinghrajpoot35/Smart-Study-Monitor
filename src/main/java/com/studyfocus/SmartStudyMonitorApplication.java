package com.studyfocus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // ✅ THIS IS STEP 3 (VERY IMPORTANT)
public class SmartStudyMonitorApplication {

    static {
        try {
            // Load OpenCV native libraries automatically (Cross-Platform)
            nu.pattern.OpenCV.loadShared();
            System.out.println("✅ OpenCV native libraries loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to load OpenCV native libs");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartStudyMonitorApplication.class, args);
    }
}
