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
        // No-op: handled by browser
    }

    // ================= STOP =================
    public synchronized void stop() {
        // No-op: handled by browser
    }
}
