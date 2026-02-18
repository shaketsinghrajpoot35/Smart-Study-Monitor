package com.studyfocus.model;

public class StatusInfo {

	// ===== EXISTING IMPORTANT FIELDS (UNCHANGED) =====
	private boolean faceDetected;
	private boolean drowsy;
	private String currentStatus;

	private long faceAbsentSeconds;
	private long drowsySeconds;
	private long studiedSecondsToday;

	// ===== NEW TIMER FIELDS (ADDED) =====
	private int remainingSeconds; // study / break remaining time
	private String timerMode; // STUDY / BREAK

	public int getFocusScore() {
		return focusScore;
	}

	public void setFocusScore(int focusScore) {
		this.focusScore = focusScore;
	}

	public String getFocusLabel() {
		return focusLabel;
	}

	public void setFocusLabel(String focusLabel) {
		this.focusLabel = focusLabel;
	}

	// 🔥 NEW FIELDS (FOCUS SCORE)
	private int focusScore;
	private String focusLabel;
	// ================= GETTERS & SETTERS =================

	private boolean noisy;
	private String noiseMessage;

	public boolean isNoisy() {
		return noisy;
	}

	public void setNoisy(boolean noisy) {
		this.noisy = noisy;
	}

	public String getNoiseMessage() {
		return noiseMessage;
	}

	public void setNoiseMessage(String noiseMessage) {
		this.noiseMessage = noiseMessage;
	}

	public boolean isFaceDetected() {
		return faceDetected;
	}

	public void setFaceDetected(boolean faceDetected) {
		this.faceDetected = faceDetected;
	}

	public boolean isDrowsy() {
		return drowsy;
	}

	public void setDrowsy(boolean drowsy) {
		this.drowsy = drowsy;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public long getFaceAbsentSeconds() {
		return faceAbsentSeconds;
	}

	public void setFaceAbsentSeconds(long faceAbsentSeconds) {
		this.faceAbsentSeconds = faceAbsentSeconds;
	}

	public long getDrowsySeconds() {
		return drowsySeconds;
	}

	public void setDrowsySeconds(long drowsySeconds) {
		this.drowsySeconds = drowsySeconds;
	}

	public long getStudiedSecondsToday() {
		return studiedSecondsToday;
	}

	public void setStudiedSecondsToday(long studiedSecondsToday) {
		this.studiedSecondsToday = studiedSecondsToday;
	}

	// ===== NEW TIMER GETTERS / SETTERS =====

	public int getRemainingSeconds() {
		return remainingSeconds;
	}

	public void setRemainingSeconds(int remainingSeconds) {
		this.remainingSeconds = remainingSeconds;
	}

	public String getTimerMode() {
		return timerMode;
	}

	public void setTimerMode(String timerMode) {
		this.timerMode = timerMode;
	}
}
