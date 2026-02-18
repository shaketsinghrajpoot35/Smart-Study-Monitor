package com.studyfocus.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;

@Service
public class NoiseMonitorService {

    private volatile boolean noisy = false;
    private int noisyFrames = 0;

    private static final int NOISE_THRESHOLD_DB = 65;   // adjust later
    private static final int NOISE_FRAMES_LIMIT = 6;    // ~2 seconds

    @PostConstruct
    public void startMonitoring() {
        Thread t = new Thread(this::monitorMic);
        t.setDaemon(true);
        t.start();
    }

    private void monitorMic() {

        try {
            AudioFormat format = new AudioFormat(
                    44100,
                    16,
                    1,
                    true,
                    true
            );

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);

            mic.open(format);
            mic.start();

            byte[] buffer = new byte[2048];

            System.out.println("🎤 Noise monitoring started");

            while (true) {
                int bytesRead = mic.read(buffer, 0, buffer.length);
                double rms = calculateRMS(buffer, bytesRead);
                double db = 20 * Math.log10(rms);

                if (db > NOISE_THRESHOLD_DB) {
                    noisyFrames++;
                    if (noisyFrames >= NOISE_FRAMES_LIMIT) {
                        noisy = true;
                    }
                } else {
                    noisyFrames = 0;
                    noisy = false;
                }

                Thread.sleep(300);
            }

        } catch (Exception e) {
            System.out.println("❌ Mic error: " + e.getMessage());
        }
    }

    private double calculateRMS(byte[] buffer, int bytesRead) {
        long sum = 0;
        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short) ((buffer[i] << 8) | buffer[i + 1]);
            sum += sample * sample;
        }
        double mean = sum / (bytesRead / 2.0);
        return Math.sqrt(mean);
    }

    public boolean isNoisy() {
        return noisy;
    }
}
