package com.superchat.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

import com.superchat.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;



public final class VoiceMedia implements OnSchedularListener, MediaEventsHandler, OnClickListener, OnKeyListener {
	private static final String TAG = VoiceMedia.class.getSimpleName();
//	private TextView textView;
	private int time = Constants.MAX_AUDIO_RECORD_TIME - 1;
	private Timer timer;
	private Activity mActivity;
//	private Dialog mDialog;
	private MediaEngine mMediaEngine = MediaEngine.getMediaEngineInstance();
	private String mVoicePath;
	private VoiceMediaHandler mParent;
	private Object mTag;
	private int mMediaState = Constants.UI_STATE_IDLE;
	public boolean startingRec = false;

	public VoiceMedia(Activity context, VoiceMediaHandler parent) {
		mActivity = context;
		mParent = parent;
	}

	public void startRecording(String positive, String negative, String aTitle, int aTime) {
		if (startingRec) {
			return;
		}
				startingRec = true;
		positive = " " + positive + "  ";
		negative = "  " + negative + "  ";
		time = aTime;
//		mDialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
//		mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		mDialog.setContentView(R.layout.voice_touch_record);
//		mDialog.setCancelable(false);
//		mDialog.setOnKeyListener(this);
//		
//		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
//		lp.dimAmount = Constants.dimamount;
//		mDialog.getWindow().setAttributes(lp);
//		mDialog.getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		
//		textView = (TextView) mDialog.findViewById(R.id.voiceRecordingDialog_secondsText);
//		textView.setText(String.format("%02d:%02d", (time / 60), (time % 60)));
//		//		textView.setText("" + aTime);
//
//		if (aTitle != null)
//			((TextView) mDialog.findViewById(R.id.recording_title)).setText(aTitle);
//
//		TextView bTextView = (TextView) mDialog.findViewById(R.id.voiceRecordingDialog_done);
//		bTextView.setOnClickListener(this);
//		bTextView.setText(positive);
//
//		bTextView = (TextView) mDialog.findViewById(R.id.voiceRecordingDialog_cancel);
//		bTextView.setOnClickListener(this);
//		bTextView.setText(negative);

		mMediaEngine.recorderWithNewPath(this, "VM_" + System.currentTimeMillis());
		if (!mMediaEngine.startRecording(aTime)) {
			showAlert("Error", "Error start recording !");
			return;
		}
		mMediaState = Constants.UI_STATE_RECORDING;
//		mDialog.show();
		if (null != mParent) {
			mParent.voiceRecordingStarted(mMediaEngine.getFilePath());
		}
		timer = new Timer();
		timer.schedule(new OtsSchedularTask(this, null, (byte)0), 1000, 1000);
	}

	public void startRecording(int aTime){
		mMediaEngine.recorderWithNewPath(this, "VM_" + System.currentTimeMillis());
		if (!mMediaEngine.startRecording(aTime)) {
			showAlert("Error", "Error start recording !");
			return;
		}
		if (null != mParent) {
			mParent.voiceRecordingStarted(mMediaEngine.getFilePath());
		}
//		timer = new Timer();
//		timer.schedule(new OtsSchedularTask(this, null, (byte)1), 1000, 1000);

	}
	public void stopRec(){
		stopMediaRecording();
		if (null != mParent) {
			mParent.voiceRecordingCompleted(mMediaEngine.getFilePath());
		}
		setRecordingStatus(false);
		mMediaState = Constants.UI_STATE_IDLE;
	}
	private void showAlert(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	public void startPlaying(byte[] soundByteArray) {
		try {
			File tempMp3 = File.createTempFile("kurchina", "mp3", mActivity.getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			startPlaying(tempMp3.getAbsolutePath(), null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void startPlayingWithName(byte[] soundByteArray, String fname) {
		try {
			File tempMp3 = File.createTempFile(fname, "mp3", mActivity.getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			startPlaying(tempMp3.getAbsolutePath(), null);
		} catch (IOException ex) {
		} catch (Exception ex) {
			
		}
	}

	public void startPlaying(byte[] soundByteArray, String extention) {
		try {
			File tempMp3 = File.createTempFile("kurchina", "." + extention, mActivity.getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			startPlaying(tempMp3.getAbsolutePath(), null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void startPlayingMedia(byte[] soundByteArray, ProgressBar bar) {
		try {
			byte s[] = new byte[12];
			System.arraycopy(soundByteArray, 0, s, 0, 12);
			String str = new String(s);
			String extention = "amr";
			System.out.println("Voice type:"+new String(s));
			if(str.indexOf("amr") != -1)
				extention = "amr";
			else if(str.indexOf("mp3") != -1)
				extention = "mp3";
			else if(str.indexOf("mp4a") != -1)
				extention = "mp4a";
			File tempMp3 = File.createTempFile("rttemp", "."+extention, mActivity.getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			startPlayingMedia(tempMp3.getAbsolutePath(), bar);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void startPlaying() {
		if (null != mVoicePath) {
			startNewPlaying(mVoicePath, null,false);//startPlaying(mVoicePath, null);
		}
	}

	public void startPlayingMedia(ProgressBar bar) {
		if (null != mVoicePath) {
			startPlayingMedia(mVoicePath, bar);
		}
	}

	public void startPlayingMedia(String path, ProgressBar bar) {
		startPlayingMedia(path, bar, false);
	}

	public void startPlayingMedia(String path, ProgressBar bar, boolean isUrl) {
		if (mMediaEngine.playFile(path, this, isUrl)) {
//			if (mDialog != null)
//				mDialog.show();
			if (null != mParent) {
				mParent.voicePlayStarted();
			}
			startProgressBar(bar);
		}
	}

	public void startPlaying(String path, String aTitle) {
		startPlaying(path, aTitle, false);
	}

	public void startPlaying(String path, String aTitle, boolean isUrl) {
//		mDialog = new Dialog(mActivity,
//				android.R.style.Theme_Translucent_NoTitleBar);
//		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mDialog.setContentView(R.layout.voiceplay_dialog);
//		mDialog.findViewById(R.id.voicePlayingDialog_pause).setOnClickListener(this);
//		mDialog.findViewById(R.id.voicePlayingDialog_Stop).setOnClickListener(this);
//		
//		
//		TextView view = (TextView) mDialog.findViewById(R.id.voicePlayingDialog_pause);
//		view.setTag("Pause");
//		
//		mDialog.setCancelable(false);
//		
//		
//		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
//		lp.dimAmount = Constants.dimamount;
//		mDialog.getWindow().setAttributes(lp);
//		mDialog.getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		
//		if (aTitle != null)
//			((TextView) mDialog.findViewById(R.id.playing_voice)).setText(aTitle);
//		if (mMediaEngine.playFile(path, this, isUrl)) {
//			ProgressBar bar = (ProgressBar) mDialog.findViewById(R.id.voicePlayingDialog_progressbar);
//			mDialog.show();
//			if (null != mParent) {
//				mParent.voicePlayStarted();
//			}
//			startProgressBar(bar);
//		}
//		mDialog.getWindow().getAttributes().windowAnimations = R.style.actionmenu_animation_popdown_center;

//		 WMLP.x = 10;   //x position
//		 WMLP.y = 500;   //y position
//		 mDialog.getWindow().g
//		 mDialog.getWindow().setAttributes(WMLP);
//		mDialog.setGravity(Gravity.BOTTOM);	 
	}
	public void startNewPlaying(String path, String aTitle, boolean isUrl) {
		
		if (mMediaEngine.playFile(path, this, isUrl)) {
			
			
			if (null != mParent) {
				mParent.voicePlayStarted();
			}
			
		}
			
	}
	public void startPlayingWithByte(byte[] soundByteArray) {
		System.out.println("soundByteArray====="+soundByteArray.length);
		try {
			File tempMp3 = File.createTempFile("kurchina", "mp3", mActivity.getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(soundByteArray);
			fos.close();
			startNewPlayingAnimation(tempMp3.getAbsolutePath(), null,false);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
public void startNewPlayingAnimation(String path, String aTitle, boolean isUrl) {
		
		if (mMediaEngine.playFile(path, this, isUrl)) {
			
			
			if (null != mParent) {
				mParent.voicePlayStarted();
			}
			
		}
			
	}

	public int getMediaState() {
		return mMediaState;
	}

	boolean playerStart = false ; 
	private void startProgressBar(final ProgressBar progressBar) {
	        progressBar.setProgress(0);
	        progressBar.setMax(mMediaEngine.getMediaDuration());
	        
	        class ProgressRunner implements Runnable {
	            
	            public ProgressRunner() {
	            }
	            
	            @Override
	            public void run() {
	                int progress = mMediaEngine.getCurrentMediaTime();
	                playerStart = true ; 
	                while (playerStart  && progress < progressBar.getMax()) {
	                	changeSpeekerToHeadPhone();
//	                	System.out.println("---------progress-------"+progress);
//	                	System.out.println("---------progressBar.getMax()-------"+progressBar.getMax());
	                    try {
	                        progress = mMediaEngine.getCurrentMediaTime();
	                        progressBar.setProgress(progress);
	                        Thread.sleep(100);
	                    } catch (Exception e) {
	                    	return ;
	                    }
	                    try{
	                    PowerManager pm = (PowerManager) mActivity.getSystemService(mActivity.POWER_SERVICE);
	                    boolean isScreenOn = pm.isScreenOn();
	                    if(!isScreenOn){
	                    	mActivity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									try{
//									View v = mDialog.findViewById(R.id.voicePlayingDialog_Stop) ;
//									if( v!=null)
//									onClick(v);
									}catch (Exception e) {
										e.printStackTrace();
										
										return ;
									}
								}
							});
	                    	
//	                    	 break;
	                    }
	                    }catch (Exception e) {
							e.printStackTrace();
							return ;
						}
	                }
	            }
	        }
	        
	        new Thread(new ProgressRunner()).start();
	    }

	public void stop() {
		time = Constants.MAX_AUDIO_RECORD_TIME - 1;
		if (null != this.timer) {
			this.timer.cancel();
			this.timer = null;
		}
	}
public void setRecordingStatus(boolean status){
	startingRec = status;
}
	public void onTaskCallback(Object parameter, byte req) {
		--time;
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				//BusinessProxy.sSelf.startRecording=false;
						startingRec = false;
				//StringBuilder totalTime = new StringBuilder();
				//if (time < 10) {
				//totalTime.append('0');
				//}
				//totalTime.append(time);
				//textView.setText(totalTime.toString());
				//totalTime = null;
//				textView.setText(String.format("%02d:%02d", (time / 60), (time % 60)));
//				if (time < 10)
//					textView.setTextColor(0x80ff0000);
			}
		});
		if (time == 0) {
			stopMediaRecording();
			if (null != mParent) {
				mParent.voiceRecordingCompleted(mMediaEngine.getFilePath());
			}
		}
	}

	private void stopMediaRecording() {
//		if (mDialog != null)
//			mDialog.dismiss();
		try {
			if(mMediaEngine!=null)
				mMediaEngine.stopRecorder();
		} catch (Exception ex) {
			
		}
		if(mMediaEngine!=null)
			mVoicePath = mMediaEngine.getFilePath();
		stop();
	}

	public void stopVoicePlaying() {
		TextView view = null;
//		if (null != mDialog) {
//			view = (TextView) mDialog.findViewById(R.id.voicePlayingDialog_pause);
//			if (null != view && ((String)view.getTag()).toString().equals("Play")) {
//				mMediaEngine.resumePlay();
//			} else {
//				mMediaEngine.pausePlay();
//			}
//		} else
			if (mMediaEngine != null) {
			mMediaEngine.stopPlayer();
		}
	}

	public void pause() {
		if (mMediaEngine != null) {
			mMediaEngine.pausePlay();
		}
	}

	public void resume() {
		if (mMediaEngine != null) {
			mMediaEngine.resumePlay();
		}
	}

	public void mediaEvent(byte event) {
		switch (event) {
		case 123:
			mParent.onError(123);
			break;
		case MEDIA_PLAYING_STOPPED:
			playerStart = false ; 
			break ;
		case MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
		case MediaEventsHandler.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
		case MediaEventsHandler.MEDIA_RECORDER_INFO_UNKNOWN:
			stopMediaRecording();
			break;
		case MediaEventsHandler.MEDIA_RECORDER_ERROR_UNKNOWN:
			stopMediaRecording();
			break;
		case MediaEventsHandler.MEDIA_PLAYING_COMPLETED:
		case MediaEventsHandler.MEDIA_PLAYING_FAILED:
//			if (mDialog != null)
//				mDialog.dismiss();
			if (null != mParent) {
				mParent.voicePlayCompleted(null);
			}
			break;
		case MediaEventsHandler.MEDIA_PLAYING_PAUSED:
//			if (null != mDialog) {
//				TextView view = (TextView) mDialog.findViewById(R.id.voicePlayingDialog_pause);
//				view.setTag("Play");
//				view.setBackgroundResource(R.drawable.play);
//			}
			break;
		case MediaEventsHandler.MEDIA_PLAYING_RESUMED:
//			if (null != mDialog) {
//				TextView view = (TextView) mDialog.findViewById(R.id.voicePlayingDialog_pause);
//				view.setTag("Pause");
//				view.setBackgroundResource(R.drawable.pause);
//			}
			break;
		}
	}
public void autoStop(){
	if (startingRec) {
		return;
	}
	stopMediaRecording();
	if(mMediaEngine!=null && mMediaEngine.equals("")){
		File file = new File(mMediaEngine.getFilePath());
		if (file.exists()) {
			file.delete();
		}
	}
}
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.voiceRecordingDialog_done:
//			stopMediaRecording();
//			if (null != mParent) {
//				mParent.voiceRecordingCompleted(mMediaEngine.getFilePath());
//			}
//			break;
//		case R.id.voiceRecordingDialog_cancel:
//			if (startingRec) {
//				return;
//			}
//			stopMediaRecording();
//			File file = new File(mMediaEngine.getFilePath());
//			if (file.exists()) {
//				file.delete();
//			}
//			break;
//		case R.id.voicePlayingDialog_Stop:
//			if (mDialog != null)
//				mDialog.dismiss();
//			if (mMediaEngine != null){
//				mMediaEngine.stopPlayer();
//				
//			}
//			if (null != mParent) {
//				mParent.voicePlayCompleted(null);
//			}
//			break;
//		case R.id.voicePlayingDialog_pause:
//			TextView view = null;
//			if (null != mDialog) {
//				view = (TextView) mDialog.findViewById(R.id.voicePlayingDialog_pause);
//				if (null != view && ((String)view.getTag()).toString().equals("Play")) {
//					mMediaEngine.resumePlay();
//				} else {
//					mMediaEngine.pausePlay();
//				}
//			}
//			break;
//		}
	}

	
	public void pausePlayMedia(Boolean flag){
		if(mMediaEngine!=null){
			if(flag)
		mMediaEngine.resumePlay();
			else
		mMediaEngine.pausePlay();
		}
	}
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return true;
	}

	/**
	 * Gets the value of Voice Path
	 * 
	 * @return The Voice Path
	 */
	public final String getVoicePath() {
		return mVoicePath;
	}

	/**
	 * Sets the value of mTag
	 * 
	 * @param mTag
	 *            The mTag to set
	 */
	public void setTag(Object mTag) {
		this.mTag = mTag;
	}

	/**
	 * Gets the value of mTag
	 * 
	 * @return The mTag
	 */
	public Object getTag() {
		return mTag;
	}
	public static void changeSpeekerToHeadPhone(){

	}
}