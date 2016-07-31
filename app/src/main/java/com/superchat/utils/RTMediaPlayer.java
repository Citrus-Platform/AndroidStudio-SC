package com.superchat.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;



public class RTMediaPlayer {
	private boolean _dirty;
	private boolean _loop;
	private MediaPlayer _mediaPlayer = new MediaPlayer();
	private boolean _paused;
	private boolean _prepared;
	private VoiceMediaHandler mediaHandler;
	private int bufferRet = 0 ;
	long maxValue;
	 private WeakReference<SeekBar> progressBarReference = null;
	private View view;
	{
		_dirty = true;
		_loop = false;
		_paused = false;
		_prepared = false;
	}

	public void setMediaHandler(VoiceMediaHandler mediaHandlerl) {
		mediaHandler = mediaHandlerl;
	}
	public View getCurrentView() {
		return view;
	}
	public String getUrl() {
		return urln;
	}
	public void setUrl(String url) {
		urln = url;
	}
	// public boolean isPlaying(){
	// return _mediaPlayer.isPlaying();
	// }
	private String urln;

	public boolean playFile(String path, final View view, final Handler handle) {
		
		try {
			_mediaPlayer = new MediaPlayer();
			
			FileInputStream fin = new FileInputStream(path);
			_mediaPlayer.setDataSource(fin.getFD());
			
			_mediaPlayer.prepare();
			_mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					// System.out.println("-----------indide prepare method----");
					if (_prepared) {
						if(!mp.isPlaying()){
							setStreem();
							mp.start();
						}
						if(mediaHandler!=null)
							mediaHandler.voicePlayStarted();
						startProgressBar(view, handle);
					}
					// System.out.println("-----------getDuration()--------"
					// + getDuration());
				}
			});
			_mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(!isProxyNotSupport() && mediaHandler!=null)
					mediaHandler.onError(1);
					return false;
				}
			});
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if(!isProxyNotSupport())
					playerStart = false;
				}
			});
			_mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {			
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					bufferRet = percent ;
				}
			});
			bufferRet = 100 ;
			_mediaPlayer.start();
			startProgressBar(view,handle);
		} catch (IOException e) {
			
			return false;
		} catch (Exception ex) {
			
			return false;
		}
		return true;
	}
	public boolean _startPlay(String url, final View pauseView, final Handler handle) {
//		url = "http://74.208.228.56/akm/yashpreet/Jagjit.mp3" ;
//		 System.out.println("-----------streem url---"+url);
		view= pauseView;
		if(progressBar!=null)
			progressBar.setProgress(0);
		_prepared = true;
		_paused = false;
		callRinging = false ;
		urln = url;
		bufferRet = 0 ;
		try {
			if (_mediaPlayer.isPlaying())
				_mediaPlayer.pause();
			_mediaPlayer.reset();
			
			if (url.indexOf("http:") != -1){
				_mediaPlayer.setDataSource(url);
				_mediaPlayer.prepareAsync();
			}
			else {
				return playFile(url,pauseView, handle);
			}

			_mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					// System.out.println("-----------indide prepare method----");
					if (_prepared && !callRinging) {
						if(!mp.isPlaying()){
//							RocketalkApplication.am.setMode(AudioManager.STREAM_MUSIC);
//							_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
							setStreem();
							mp.start();
						}
						mediaHandler.voicePlayStarted();
						startProgressBar(pauseView,handle);
					}
					if(callRinging){
						Log.d("RTMediaPlayer", "RTMediaPlayer: onPrepared");
						mediaHandler.voicePlayCompleted(pauseView);
					}
					// System.out.println("-----------getDuration()--------"
					// + getDuration());
				}
			});
			_mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(!isProxyNotSupport())
					mediaHandler.onError(1);
					return false;
				}
			});
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if(!isProxyNotSupport())
					playerStart = false;
				}
			});
			_mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					bufferRet = percent ;
				}
			});
			int i = 1;
			return true;
		} catch (Exception j) {
			return false;
		}
	}

	public void clear() {
		reset();
		_paused = false;
		_dirty = true;
	}
	public int getTotbuffer() {
//		int i = _mediaPlayer.getCurrentPosition();// etCurrentMediaTime();
		return bufferRet;
	}
	public int getCurrentMediaTime() {
		int i = _mediaPlayer.getCurrentPosition();// etCurrentMediaTime();
		return i;
	}

	public int getDuration() {
		int i = _mediaPlayer.getDuration();
		return i;
	}

	public MediaPlayer getPlayer() {
		return _mediaPlayer;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public boolean isLooping() {
		return _loop;
	}

	public boolean isPaused() {
		return _paused;
	}

	public boolean isPlaying() {
		return _mediaPlayer.isPlaying();
	}

	public void pause() {
		// if(_paused){
		// play();
		// }
		// else{
		if(!isProxyNotSupport())
		_mediaPlayer.pause();
		_paused = true;
		// }
	}

	public void resume() {

		_mediaPlayer.start();
		_paused = false;

	}

	public void play() {
		if (_mediaPlayer.isPlaying())
			_mediaPlayer.pause();
		_mediaPlayer.start();
		_paused = false;
	}

	public void start(View view, Handler handle) {
		// if (_mediaPlayer.isPlaying())
		// _mediaPlayer.pause();
		callRinging = false ;
		_mediaPlayer.start();
		if (!playerStart) {
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					playerStart = false;
				}
			});
			startProgressBar(view,handle);
			playerStart = true;
		}
		_paused = false;
	}

	public void reset() {
//		System.out.println("------------------------reset--------------------");
		setStreemSystem();
		playerStart = false;
		_mediaPlayer.reset();
		
		_prepared = false;
		playerStart=false;
	}

	public void seek(int seekTime) {
		// playerStart = false;
		_mediaPlayer.seekTo(seekTime);
		// _prepared = false;
	}

	public void resetDirty() {
		_dirty = false;
	}

	public void setDirty() {
		_dirty = true;
	}

	public void setLooping(boolean paramBoolean) {
		_loop = paramBoolean;
	}

	private SeekBar progressBar;
	private TextView total_autio_time;
	private TextView played_autio_time;
	public int progress;
	public void setTimeView(TextView total_autio_timeL,TextView played_autio_timeL){
		total_autio_time = total_autio_timeL;
		played_autio_time = played_autio_timeL;
	}
	public void setProgressBar(SeekBar progressBarl) {
//		progressBar = progressBarl;
		maxValue = 0;
		progressBarReference = new WeakReference<SeekBar>(progressBarl);
		progressBar = progressBarReference.get();
	}
	public SeekBar getProgressBar() {
		return progressBar;
	}
	public void sendStopNotification(View view) {
		playerStart = false ;
		Log.d("RTMediaPlayer", "RTMediaPlayer: sendStopNotification"+progressBar.getMax());
		view.setTag(String.valueOf(progressBar.getMax()));
		mediaHandler.voicePlayCompleted(view);
	}

	private boolean playerStart = false;
	private boolean callRinging = false;
	private void startProgressBar(final View view,final Handler handle) {
		if(progressBar == null)
			return;
		if(playerStart)
			return;
		progressBar.setProgress(0);
		progressBar.setMax(getDuration());
		maxValue = progressBar.getMax();
		progressBar.setEnabled(true);
		
		class ProgressRunner implements Runnable {

			public ProgressRunner() {
			}

			@Override
			public void run() {
				 progress = getCurrentMediaTime();
				playerStart = true;
				if(!isProxyNotSupport())
				// System.out.println("----------progressBar.getMax()-"+progressBar.getMax());
				while (playerStart && progress < maxValue) {
					try {
						changeSpeekerToHeadPhone();
						progress = getCurrentMediaTime();
//						if(progressBar.isEnabled())
						mediaHandler.onDureationchanged(getDuration(), progress,progressBar);
						if(handle!=null){
							
						handle.sendEmptyMessage(0);
						}
//						played_autio_time.setText(""+progress);
//						Message msg = new Message();
//						Bundle data = new Bundle();
//						data.putString("Timing", ""+progress+" , "+progressBar.getMax());
//						((ChatListScreen)AtMeApplication.context).runOnUiThread(new Runnable() {
//							
//							@Override
//							public void run() {
////								data.putString("Timing", ""+progress+" , "+progressBar.getMax());
//								total_autio_time.setText(""+progressBar.getMax());
//								played_autio_time.setText(""+progress);
//							}
//						});
//						data.putString("Timing", ""+progress+" , "+progressBar.getMax());
//						msg.setData(data);
//						handle.sendMessage(msg);
//						progressBar.setProgress(progress);
						Thread.sleep(600);
						// if(!RTMediaPlayer.isPlaying())
						// playerStart = false ;
						// System.out.println("----------progress-"+progress);
						// System.out.println("----------RTMediaPlayer.isPlaying()-"+RTMediaPlayer.isPlaying());
					} catch (Exception e) {
						Log.d("RTMediaPlayer", "TESTER_ Exception RTMediaPlayer: "+e.toString());
						e.printStackTrace();
						sendStopNotification(view);
						return;
					}
				}
//				android.util.Log.d("ChatListAdapter", "ProgressRunner current. " + playerStart +" , "+progress+" , "+maxValue);
				sendStopNotification(view);
			}
		}

		new Thread(new ProgressRunner()).start();
	}
	public void changeSpeekerToHeadPhone(){
		
	}
	
	public void setStreem(){
		if(!isProxyNotSupport()){
		if(_mediaPlayer!=null  && _mediaPlayer.isPlaying())
		_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) ;
		}
	}
	public void setStreemSystem(){
		if(!isProxyNotSupport()){
		if(playerStart){
		playerStart = false;
		if(_mediaPlayer!=null && _mediaPlayer.isPlaying())
		_mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM) ;
		}
		}
	}
	public void setCallStreemSystem(){
		if(!isProxyNotSupport()){
		if(playerStart){
		if(_mediaPlayer!=null && _mediaPlayer.isPlaying())
		_mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM) ;
		playerStart = false;
		}
		}
	}
	public void callRinging(){
		callRinging = true;
	}
	public void callEnd(){
		callRinging = false;
	}
	
	
	public boolean isProxyNotSupport(){
			return false ;
	}
}