package com.studyfocus.service;

import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlarmService {

    private final Map<String, Clip> clips = new HashMap<>();
    private Clip currentClip = null;

    public AlarmService() {
        // Sounds are now handled by the frontend (dashboard.html)
        // to support cloud deployment without server audio hardware.
    }

    // ================= LOAD =================
    private void loadSound(String key, String path) {
        // No-op: server-side audio is disabled
    }

    // ================= PLAY =================
    public synchronized void play(String key) {
        Clip clip = clips.get(key);
        if (clip == null) return;

        stop(); // stop previous sound

        clip.setFramePosition(0);
        clip.start();
        currentClip = clip;

        System.out.println("🔊 Playing sound: " + key);
    }

    // ================= STOP =================
    public synchronized void stop() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            System.out.println("🔇 Sound stopped");
        }
        currentClip = null;
    }
}
