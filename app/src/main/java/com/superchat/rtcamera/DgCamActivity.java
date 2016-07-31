package com.superchat.rtcamera;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.superchat.R;
import com.superchat.ui.HomeScreen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import com.kainat.app.android.core.BusinessProxy;


public class DgCamActivity extends Activity implements SensorEventListener {
	private Camera mCamera;
	private CameraPreview mPreview;
	private SensorManager sensorManager = null;
	private int orientation;
	private ExifInterface exif;
	private int deviceHeight;
	private ImageView ibRetake;
	private ImageView ibUse;
	private Button ibCapture;
	private ImageView ibSetting;
	private FrameLayout flBtnContainer;
	private File sdRoot;
	private String dir;
	private String fileName;
	private ImageView rotatingImage;
	private int degrees = -1;
	private File video;
	public MediaRecorder mrec ;
	
	private static final byte SMALL_PHONE = 0;
	private static final byte MEDIUM_PHONE = 1;
	private static final byte LARGE_PHONE = 2;
	private static final byte XLARGE_PHONE = 3;
	byte phoneType = MEDIUM_PHONE;
	int width,height;
	AlertDialog.Builder builder ;
	int displaywidth = 0;
	int displayheight = 0;
	String cameraPathUrl = null;
	String url;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customcameralayout);
		mrec= new MediaRecorder();
		// Setting all the path for the image
		sdRoot = Environment.getExternalStorageDirectory();
		 builder = new AlertDialog.Builder(this);
		dir = "/DCIM/Camera/";
		
		displaywidth=width=getWindowManager().getDefaultDisplay().getWidth();
		displayheight=height=getWindowManager().getDefaultDisplay().getHeight();
		//height=getWindowManager().getDefaultDisplay().getHeight();
		Intent intent = getIntent();
		 url=intent.getStringExtra("urlpath");
		 cameraPathUrl = url;
		// Getting all the needed elements from the layout
		rotatingImage = (ImageView) findViewById(R.id.imageView1);
		ibRetake = (ImageView) findViewById(R.id.ibRetake);
		ibUse = (ImageView) findViewById(R.id.ibUse);
		ibCapture = (Button) findViewById(R.id.ibCapture);
		ibSetting =(ImageView)findViewById(R.id.ibSetting);
		flBtnContainer = (FrameLayout) findViewById(R.id.flBtnContainer);

		//System.out.println("getDeviceName===="+getDeviceName());
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		deviceHeight = display.getHeight();
		ibSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			       Intent intent =new Intent(DgCamActivity.this, CameraSetting.class);
			       startActivity(intent);
			//	addListenerOnChkIos();
				//addListenerOnButton();
			}
		});
		// Add a listener to the Capture button
		ibCapture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mCamera.
				try {
					mCamera.takePicture(null, null, mPicture);
					//startRecording();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mCamera.takePicture(null, null, mPicture);
			}
		});

		
		rotatingImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mCamera.
				try {
					mCamera.takePicture(null, null, mPicture);
					//startRecording();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mCamera.takePicture(null, null, mPicture);
			}
		});
		
		ibRetake.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Deleting the image from the SD card/
				File discardedPhoto = new File( fileName);
				discardedPhoto.delete();

				
				mCamera.startPreview();

				
				flBtnContainer.setVisibility(LinearLayout.VISIBLE);
				ibRetake.setVisibility(LinearLayout.GONE);
				ibUse.setVisibility(LinearLayout.GONE);
			}
		});

		
		ibUse.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			DgCamActivity.this.setResult(RESULT_OK);
			if(HomeScreen.cameraSettingMap != null && HomeScreen.cameraSettingMap.size() > 0)
				HomeScreen.cameraSettingMap.clear();
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(HomeScreen.cameraSettingMap != null && HomeScreen.cameraSettingMap.size() > 0)
			HomeScreen.cameraSettingMap.clear();
	}
	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {
		    return capitalize(manufacturer) + " " + model;
		  }
		}


		private String capitalize(String s) {
		  if (s == null || s.length() == 0) {
		    return "";
		  }
		  char first = s.charAt(0);
		  if (Character.isUpperCase(first)) {
		    return s;
		  } else {
		    return Character.toUpperCase(first) + s.substring(1);
		  }
		} 
	private void createCamera() {
		
		try{
		mCamera = getCameraInstance();
		//#SceneMode=SCENE_MODE_PORTRAIT#FlashMode=FLASH_MODE_OFF#FocusMode=FOCUS_MODE_INFINITY#ColorMode=EFFECT_SEPIA
	
		Camera.Parameters params = mCamera.getParameters();
		//System.out.print("BusinessProxy.sSelf.CameraSetting=="+BusinessProxy.sSelf.CameraSetting);
		if(HomeScreen.cameraSettingMap != null && HomeScreen.cameraSettingMap.size() > 0){
			//BusinessProxy.sSelf.cameraSettingMap
			params.setJpegQuality(1);
			params.setJpegThumbnailQuality(1);
			params.setJpegThumbnailSize(0,0);
			
			if((width>240 && height>320) || (width>320 && height>240)){
				if(getDeviceName().indexOf("Cellon")!=-1 || getDeviceName().indexOf("cellon")!=-1 || getDeviceName().indexOf("LGE Nexus 4")!=-1 || getDeviceName().indexOf("I9250")!=-1 || Build.VERSION.SDK_INT==16 || Build.VERSION.SDK_INT==17){
					params.setJpegQuality(96);
					/*List<Size> sizes = params.getSupportedPictureSizes();
					// See which sizes the camera supports and choose one of those
					Size mSize = sizes.get(0);
					 params.set("picture-size", "1024x768");
				   // params.setPictureSize(mSize.width,mSize.height);
*/				   
				}else{
		          params.setPictureSize(width,height);
		
				}
		
			}
			Set keys = HomeScreen.cameraSettingMap.keySet();

			Iterator i = keys.iterator(); 
			while(i.hasNext()) { 
			       String key = (String) i.next();
			       String value = (String) HomeScreen.cameraSettingMap.get(key);
			       if(key.equalsIgnoreCase("#SceneMode")){
			    	    // "SCENE_MODE_FIREWORKS","SCENE_MODE_CANDLELIGHT","SCENE_MODE_BEACH","SCENE_MODE_AUTO","SCENE_MODE_ACTION","SCENE_MODE_THEATRE"
			    	   if(value.equalsIgnoreCase( "SCENE_MODE_SUNSET"))
			    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_SUNSET);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_STEADYPHOTO"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_SPORTS"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_SNOW"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_SNOW);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_PORTRAIT"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_PARTY"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_NIGHT"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_LANDSCAPE"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
			    	    
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_FIREWORKS"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_FIREWORKS);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_CANDLELIGHT"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_CANDLELIGHT);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_BEACH"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_BEACH);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_AUTO"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			    	    if(value.equalsIgnoreCase( "SCENE_MODE_ACTION"))
					    	   params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
			    	    //if(value.equalsIgnoreCase( "SCENE_MODE_THEATRE"))
					    	//   params.setSceneMode(Camera.Parameters.SCENE_MODE_THEATRE);
			       }
			       //"EFFECT_WHITEBOARD", "EFFECT_SOLARIZE", "EFFECT_SEPIA", "EFFECT_POSTERIZE", "EFFECT_NONE", "EFFECT_NEGATIVE", "EFFECT_MONO", "EFFECT_AQUA", "EFFECT_BLACKBOARD"
			       if(key.equalsIgnoreCase("#ColorMode")){
			    	   if(value.equalsIgnoreCase( "EFFECT_WHITEBOARD"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
			    	   if(value.equalsIgnoreCase( "EFFECT_SOLARIZE"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
			    	   if(value.equalsIgnoreCase( "EFFECT_SEPIA"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
			    	   if(value.equalsIgnoreCase( "EFFECT_POSTERIZE"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
			    	   if(value.equalsIgnoreCase( "EFFECT_NONE"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_NONE);
			    	   if(value.equalsIgnoreCase( "EFFECT_NEGATIVE"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
			    	   if(value.equalsIgnoreCase( "EFFECT_MONO"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_MONO);
			    	   if(value.equalsIgnoreCase( "EFFECT_AQUA"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_AQUA);
			    	   if(value.equalsIgnoreCase( "EFFECT_BLACKBOARD"))
				    	   params.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
			    	  
			       }
			    //   "FOCUS_MODE_MACRO", "FOCUS_MODE_INFINITY", "FOCUS_MODE_FIXED", "FOCUS_MODE_AUTO"
			       if(key.equalsIgnoreCase("#FocusMode")){
			    	   if(value.equalsIgnoreCase( "FOCUS_MODE_MACRO"))
				    	   params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
			    	   if(value.equalsIgnoreCase( "FOCUS_MODE_INFINITY"))
				    	   params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
			    	   if(value.equalsIgnoreCase( "FOCUS_MODE_FIXED"))
				    	   params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
			    	   if(value.equalsIgnoreCase( "FOCUS_MODE_AUTO"))
				    	   params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			       }
			      // "FLASH_MODE_TORCH", "FOCUS_MODE_INFINITY", "FLASH_MODE_ON", "FLASH_MODE_OFF" ,"FLASH_MODE_AUTO"
			       if(key.equalsIgnoreCase("#FlashMode")){
			    	   if(value.equalsIgnoreCase( "FLASH_MODE_TORCH"))
				    	   params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			    	   if(value.equalsIgnoreCase( "FOCUS_MODE_INFINITY"))
				    	   params.setFlashMode(Camera.Parameters.FOCUS_MODE_INFINITY);
			    	   if(value.equalsIgnoreCase( "FLASH_MODE_ON"))
				    	   params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			    	   if(value.equalsIgnoreCase( "FLASH_MODE_OFF"))
				    	   params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			    	   if(value.equalsIgnoreCase( "FLASH_MODE_AUTO"))
				    	   params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			       }
			       
			     //  "#SceneMode", btnSceneMode.getText().toString());
					//BusinessProxy.sSelf.cameraSettingMap.put("#FlashMode", btnFlashMode.getText().toString());
					//BusinessProxy.sSelf.cameraSettingMap.put("#FocusMode", btnfocusMode.getText().toString());
					//BusinessProxy.sSelf.cameraSettingMap.put("#ColorMode"
							//if()
			   }
		
		
		//params.set
		//params.setSceneMode("night");
		//params.setFocusMode(Parameters.FLASH_MODE_TORCH);
		//Camera.Parameters.
		//params.setFocusMode("fixed");
			//params.set("camera-id",2);
		//params.setPictureSize(1280, 815);
		params.setPictureFormat(PixelFormat.JPEG);
		if((width>240 && height>320) || (width>320 && height>240))
		params.setJpegQuality(96);
		else
	    params.setJpegQuality(85);	
		}else{
			// Toast.makeText(DgCamActivity.this,
                 //    "width="+width+" height="+height, Toast.LENGTH_SHORT).show();
			params.setJpegQuality(1);
			params.setJpegThumbnailQuality(1);
			params.setJpegThumbnailSize(0,0);
			//params.set("camera-id",2);
			//params.set
			//params.setSceneMode("night");
			//params.setFocusMode(Parameters.FLASH_MODE_TORCH);
			//Camera.Parameters.
			//params.setFocusMode("fixed");
			//GT-I9250
			if((width>240 && height>320) || (width>320 && height>240)){
				//Toast.makeText(DgCamActivity.this,
						 //getDeviceName(), Toast.LENGTH_SHORT).show();
				//System.out.println("getDeviceName()==="+getDeviceName());
				//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES) {
				     // only for gingerbread and newer versions
				//}
				String deviceName = Build.MODEL;
				String deviceMan = Build.MANUFACTURER;
				System.out.println("deviceName===="+deviceName + "deviceMan=="+deviceMan);
				if(getDeviceName().indexOf("Cellon")!=-1 || getDeviceName().indexOf("cellon")!=-1 || getDeviceName().indexOf("LGE Nexus 4")!=-1 || getDeviceName().indexOf("I9250")!=-1 ||  getDeviceName().indexOf("A111")!=-1 || (Build.VERSION.SDK_INT==16 && !Build.MANUFACTURER.equalsIgnoreCase("samsung")) ||(Build.VERSION.SDK_INT==17 && !Build.MANUFACTURER.equalsIgnoreCase("samsung")) ){
					params.setJpegQuality(96);
					   List<Size> sizes = params.getSupportedPictureSizes();
					// See which sizes the camera supports and choose one of those
					   Size mSize = sizes.get(0);
					  // params.set("picture-size", "1024x768");
				       params.setPictureSize(mSize.width,mSize.height);
				    Toast.makeText(DgCamActivity.this,
		                     "mSize==="+mSize.toString(), Toast.LENGTH_SHORT).show();
				}else{
					params.setJpegQuality(100);
			         List<Size> sizes = params.getSupportedPictureSizes();
//			         for(int i=0; i<sizes.size();i++)
//			         {
//			          Camera.Size mSize = sizes.get(i);
//			          System.out.println("value at +"+i+ " -> w -"+mSize.width+", and h - "+mSize.height);
//			         // See which sizes the camera supports and choose one of those
//			         }
			         Size mSize = sizes.get(sizes.size() - 1);
//			         params.set("picture-size", "1024x768");
			         params.setPictureSize(mSize.width,mSize.height);
		       //   params.setPictureSize(width,height);
			     // params.setJpegQuality(96);
				}
			}else{
				params.setJpegQuality(85);
			}
			//params.setPictureSize(1280, 815);
			params.setPictureFormat(PixelFormat.JPEG);
			
		}
		mCamera.setParameters(params);
		// mCamera.setDisplayOrientation(90);
	
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		float widthFloat;
		int widthCam ;
		LinearLayout.LayoutParams layoutParams;
		      phoneType = (width < 320) ? SMALL_PHONE : ((width >= 320 && width <480) ? MEDIUM_PHONE : LARGE_PHONE);
			switch(phoneType)
			{
			case MEDIUM_PHONE:
				 widthFloat = (float) (deviceHeight) * 4/ 3;
				 widthCam = Math.round(widthFloat);
				
				   layoutParams = new LinearLayout.LayoutParams(widthCam-55, deviceHeight); 
				// System.out.println("MEDIUM_PHONE");
				break;
			case LARGE_PHONE:
				 widthFloat = (float) (deviceHeight) * 4/ 3;
				 widthCam = Math.round(widthFloat);
				 if(height==320 && width==480)
					 layoutParams = new LinearLayout.LayoutParams(widthCam-10, deviceHeight);
				 else
				 layoutParams = new LinearLayout.LayoutParams(widthCam+60, deviceHeight);
				 //System.out.println("LARGE_PHONE");
				break;
			case XLARGE_PHONE:
				 widthFloat = (float) (deviceHeight) * 4/ 3;
				widthCam = Math.round(widthFloat);
				 layoutParams = new LinearLayout.LayoutParams(widthCam+60, deviceHeight);	
				//	preview.setLayoutParams(layoutParams);
					// System.out.println("XLARGE_PHONE");
				break;
			default:
				 widthFloat = (float) (deviceHeight) * 4/ 3;
				 widthCam = Math.round(widthFloat);
				
				 layoutParams = new LinearLayout.LayoutParams(widthCam-55, deviceHeight);	
					preview.setLayoutParams(layoutParams);
					// System.out.println("default");
					break;
			}
		
        
		
	
		//if((width>240 && height>320) || (width>320 && height>240))
		// layoutParams = new LinearLayout.LayoutParams(widthCam, height);
		//else
		  //layoutParams = new LinearLayout.LayoutParams(widthCam-55, deviceHeight);	
		//preview.setLayoutParams(layoutParams);

		
		preview.addView(mPreview, 0);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		
		if (!checkCameraHardware(this)) {
			Intent i = new Intent(this, NoCamera.class);
			startActivity(i);
			finish();
		} else if (!checkSDCard()) {
			Intent i = new Intent(this, NoSDCard.class);
			startActivity(i);
			finish();
		}

		
		try {
			createCamera();
			//startRecording();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		try{
		releaseCamera();

		
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		if(preview!=null)
		preview.removeViewAt(0);
		
		}catch (Exception e) {
			// TODO: handle exception
			 Toast.makeText(DgCamActivity.this,
                     "Camera is not support this setting, please select other setting", Toast.LENGTH_SHORT).show();
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			
			return true;
		} else {
			
			return false;
		}
	}

	private boolean checkSDCard() {
		boolean state = false;

		String sd = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sd)) {
			state = true;
		}

		return state;
	}

	
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			
			c = Camera.open();
		} catch (Exception e) {
			
		}

		
		return c;
	}
	/*private Camera openFrontFacingCameraGingerbread() {
	    int cameraCount = 0;
	    Camera cam = null;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
	            try {
	                cam = Camera.open(camIdx );
	            } catch (RuntimeException e) {
	               // Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
	            }
	        }
	    }

	    return cam;
	}*/
	private PictureCallback mPicture = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			
			flBtnContainer.setVisibility(View.GONE);
			ibRetake.setVisibility(View.VISIBLE);
			ibUse.setVisibility(View.VISIBLE);
			
			
			if(url!=null && url.length()>0)
			fileName =url; //"IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
			else
				fileName = cameraPathUrl;
			
			//File mkDir = new File(sdRoot, dir);
			//mkDir.mkdirs();

			
			File pictureFile = new File(fileName);

			try {
				FileOutputStream purge = new FileOutputStream(pictureFile);
				purge.write(data);
				purge.close();
			} catch (FileNotFoundException e) {
				Log.d("DG_DEBUG", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
			}

			
			try {
				exif = new ExifInterface(fileName);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + orientation);
				exif.saveAttributes();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				RotateAnimation animation = null;
				if (event.values[0] < 4 && event.values[0] > -4) {
					if (event.values[1] > 0 && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
						// UP
						orientation = ExifInterface.ORIENTATION_ROTATE_90;
						animation = getRotateAnimation(270);
						degrees = 270;
					} else if (event.values[1] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
						// UP SIDE DOWN
						orientation = ExifInterface.ORIENTATION_ROTATE_270;
						animation = getRotateAnimation(90);
						degrees = 90;
					}
				} else if (event.values[1] < 4 && event.values[1] > -4) {
					if (event.values[0] > 0 && orientation != ExifInterface.ORIENTATION_NORMAL) {
						// LEFT
						orientation = ExifInterface.ORIENTATION_NORMAL;
						animation = getRotateAnimation(0);
						degrees = 0;
					} else if (event.values[0] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
						// RIGHT
						orientation = ExifInterface.ORIENTATION_ROTATE_180;
						animation = getRotateAnimation(180);
						degrees = 180;
					}
				}
				if (animation != null) {
					rotatingImage.startAnimation(animation);
				}
			}

		}
	}


	private RotateAnimation getRotateAnimation(float toDegrees) {
		float compensation = 0;

		if (Math.abs(degrees - toDegrees) > 180) {
			compensation = 360;
		}

		
		if (toDegrees == 0) {
			compensation = -compensation;
		}

		
		RotateAnimation animation = new RotateAnimation(degrees, toDegrees - compensation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		
		animation.setDuration(250);

		
		animation.setFillAfter(true);

		return animation;
	}

	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	
	
	/*public void flashLightOn(View view) {

	    try {
	        if (getPackageManager().hasSystemFeature(
	                PackageManager.FEATURE_CAMERA_FLASH)) {
	            cam = Camera.open();
	            Parameters p = cam.getParameters();
	            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
	            cam.setParameters(p);
	            cam.startPreview();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Toast.makeText(getBaseContext(), "Exception flashLightOn()",
	                Toast.LENGTH_SHORT).show();
	    }
	}

	public void flashLightOff(View view) {
	    try {
	        if (getPackageManager().hasSystemFeature(
	                PackageManager.FEATURE_CAMERA_FLASH)) {
	            cam.stopPreview();
	            cam.release();
	            cam = null;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Toast.makeText(getBaseContext(), "Exception flashLightOff",
	                Toast.LENGTH_SHORT).show();
	    }
	}
	}*/
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }
 
    public boolean onOptionsItemSelected(MenuItem item) {
 
        // when you click setting menu
        switch (item.getItemId()) {
        case R.id.setting:
        	Intent intent =new Intent(DgCamActivity.this, CameraSetting.class);
		       startActivity(intent);
            return true;
        }
        return false;
    }

}