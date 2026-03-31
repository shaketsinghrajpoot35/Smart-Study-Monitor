package com.studyfocus.service;

import org.springframework.stereotype.Service;

@Service
public class NoiseMonitorService {

    private volatile boolean noisy = false;

    public NoiseMonitorService() {
        System.out.println("🎤 NoiseMonitorService initialized (Remote mode)");
    }

    /**
     * Updates the noise status based on the value received from the frontend.
     */
    public void updateNoise(boolean noisy) {
        this.noisy = noisy;
    }

    public boolean isNoisy() {
        return noisy;
    }
}
