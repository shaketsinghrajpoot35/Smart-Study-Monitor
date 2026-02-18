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
        loadSound("alarm", "/alarm.wav");
        loadSound("drowsy", "/drowsy.wav");
        loadSound("noface", "/no_face.wav");
        loadSound("breakend", "/break_end.wav");
    }

    // ================= LOAD =================
    private void loadSound(String key, String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.out.println("❌ Sound not found: " + path);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clips.put(key, clip);
            System.out.println("🔔 Loaded sound: " + path);

        } catch (Exception e) {
            System.out.println("❌ Error loading " + path + " : " + e.getMessage());
        }
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
