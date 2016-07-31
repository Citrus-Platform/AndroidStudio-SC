package com.superchat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaRecorder;

import com.superchat.utils.MediaEventsHandler;
public final class MediaEngine implements OnCompletionListener,
		OnErrorListener, OnInfoListener,
		android.media.MediaRecorder.OnErrorListener,
		android.media.MediaRecorder.OnInfoListener {

	private static final String TAG = MediaEngine.class.getSimpleName();

	public static MediaEngine sSelf;
	private MediaPlayer mMediaPlayer;
	private MediaEventsHandler mParent;
	private MediaRecorder recorder;// = new MediaRecorder();
	private String path;
	private Context context;
	private boolean isRecordingStart = false;
	public static final String MEDIA_TYPE = ".amr";// ".m4a";

	private MediaEngine() {
	}

	public static void initMediaEngineInstance(Context context) {
		sSelf = new MediaEngine();
		sSelf.context = context;
	}

	public static MediaEngine getMediaEngineInstance() {
		if (sSelf == null) {
			sSelf = new MediaEngine();
		}
		return sSelf;
	}

	public void playResource(int resourceId) {
		play(loadResToByteArray(resourceId, context), null);
	}

	public void play(byte[] soundByteArray, MediaEventsHandler parent) {
		try {
			if ((null != mMediaPlayer && mMediaPlayer.isPlaying())) {
				return;
			}
			File tempMp3 = new File(context.getCacheDir(), "kurchina.mp3");
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			playFile(tempMp3.getAbsolutePath(), parent, false);
		} catch (IOException ex) {
		}
	}

	public boolean playFileForMute(String path, MediaEventsHandler parent) {
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			return false;
		}
		this.mParent = parent;
		try {
			mMediaPlayer = new MediaPlayer();
			FileInputStream fin = new FileInputStream(path);
			mMediaPlayer.setDataSource(fin.getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnInfoListener(this);
			mMediaPlayer.start();

		} catch (IOException e) {

			return false;
		} catch (Exception e) {
			System.out
					.println("########################EXCEPTION HANDLED#############################");
			e.printStackTrace();

			return false;
		}
		return true;
	}

	public boolean playFile(String path, MediaEventsHandler parent,
			boolean isUrl) {
		this.mParent = parent;
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			parent.mediaEvent((byte) 123);
		}
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			return false;
		}
		try {
			mMediaPlayer = new MediaPlayer();
			if (!isUrl) {
				FileInputStream fin = new FileInputStream(path);
				mMediaPlayer.setDataSource(fin.getFD());
			} else {
				mMediaPlayer.setDataSource(path);
			}
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnInfoListener(this);
			// System.out.println("isplaying====================false"
			// +"mMediaPlayer.isPlaying==="+mMediaPlayer.isPlaying() +
			// "currentstate==="+BusinessProxy.sSelf.mUIActivityManager.getCurrentState());
			// RocketalkApplication.am.setMode(AudioManager.STREAM_MUSIC);
			// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		} catch (Exception ex) {
			ex.printStackTrace();

			return false;
		}
		return true;
	}

	public int getMediaDuration() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getDuration();
		else
			return 0;
	}

	public int getCurrentMediaTime() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getCurrentPosition();
		else
			return 0;
	}

	/**
	 * Stops a recording that has been previously started.
	 */
	public void stopRecorder() throws IOException {
		isRecordingStart = false;
		if (recorder != null) {
			recorder.stop();
			// recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}
		// recorder.release();
	}

	public void stopPlayer() {
		try {
			if (null != mMediaPlayer) {
				mMediaPlayer.stop();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_STOPPED);
				}
			}
		} catch (IllegalStateException ies) {

		}
	}

	public void resumePlay() {
		try {
			if (null != mMediaPlayer) {
				mMediaPlayer.start();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_RESUMED);
				}
			}
		} catch (IllegalStateException ies) {

		}
	}

	public void pausePlay() {
		try {
			if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				if (null != mParent) {
					mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_PAUSED);
				}
			}
		} catch (IllegalStateException ies) {

		}
	}

	public static byte[] loadResToByteArray(int resId, Context ctx) {

		byte[] s = null;
		try {
			InputStream is = ctx.getResources().openRawResource(resId);
			s = new byte[is.available()];
			is.read(s);
			is.close();
		} catch (Exception e) {

		}
		return s;// return
					// "<html><head><title>test</title></head><body><a href='l'>click here to see date (this doesnt work in m5 dk)</a>.</body></html>";
	}

	public void recorderWithNewPath(MediaEventsHandler parent, String path) {
		this.mParent = parent;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += MEDIA_TYPE;// ".amr";
		}
		this.path = this.context.getCacheDir() + path;
	}

	public String getFilePath() {
		return this.path;
	}

	/**
	 * Starts a new recording.
	 */
	public boolean startRecording(int seconds) {
		isRecordingStart = true;
		// String state = android.os.Environment.getExternalStorageState();
		// if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
		// throw new IOException("SD Card is not mounted.  It is " + state +
		// ".");
		// }
		try {
			// make sure the directory we plan to store the recording in exists
			File directory = new File(path).getParentFile();
			if (!directory.exists() && !directory.mkdirs()) {
				isRecordingStart = false;
				throw new IOException("Path to file could not be created.");
			}

			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			if (MEDIA_TYPE.equals(".amr")) {
				recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			} else {
				// recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				recorder.setAudioChannels(1);
				// recorder.setAudioSamplingRate(SAMPLE_RATE);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				// recorder.setAudioEncodingBitRate(AUDIO_BITRATE);

			}

			recorder.setOutputFile(path);

			// recorder.setMaxDuration(seconds * 1000);
			recorder.prepare();
			recorder.start();
			recorder.setOnErrorListener(this);
			recorder.setOnInfoListener(this);
		} catch (Exception ex) {
			ex.printStackTrace();
			isRecordingStart = false;
			return false;
		}
		return true;
	}

	public void onCompletion(MediaPlayer mp) {
		mp.reset();
		if (null != mParent) {
			mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_COMPLETED);
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.reset();
		if (null != mParent) {
			mParent.mediaEvent(MediaEventsHandler.MEDIA_PLAYING_FAILED);
		}
		return false;
	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	public void onError(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
			try {
				stopRecorder();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_ERROR_UNKNOWN);
			}
			break;
		}
	}

	public void onInfo(MediaRecorder mr, int what, int extra) {
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED);
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED);
			}
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
			if (null != mParent) {
				mParent.mediaEvent(MediaEventsHandler.MEDIA_RECORDER_INFO_UNKNOWN);
			}
			break;
		}
	}

}
