package com.superchat.utils;

import android.view.View;
import android.widget.SeekBar;

public interface VoiceMediaHandler {
	public void voiceRecordingStarted(final String recordingPath);

	public void voiceRecordingCompleted(final String recordedVoicePath);

	public void voicePlayStarted();

	public void voicePlayCompleted(View view);
	public void onError(int i);
	public void onDureationchanged(long total,long current,SeekBar currentSeekBar);
}
