package com.superchat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.superchat.R;
import com.superchat.colorpicker.ColorPickerView;
import com.superchat.colorpicker.ColourPickerDialog;
import com.superchat.rtcamera.DgCamActivity;
import com.superchat.utils.AppUtil;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import com.kainat.app.android.util.DMKeys;
//import com.kainat.app.android.util.DataModel;
//import com.kainat.app.android.util.ImageUtils;
//import com.kainat.app.android.util.Utilities;

//import com.kainat.app.android.YelatalkApplication.TrackerName;
//import com.kainat.app.android.core.BusinessProxy;

public class RTCanvas extends Activity implements OnColorChangedListener {
	LinearLayout parent;
	MyView view;
	boolean blankCanvas =true;
	private static final byte POSITION_MULTI_SELECT_PICTURE = 5;
	ColorPickerView colorPickerView;
	Dialog mAddDialog;
	int idsOptions[] = new int[] { 1, 2,   4, 5, 6, 7, 8, 9,  10,12};
	String idsNameOptions[] = new String[] { "Emboss",  "Blur",
			"2px",  "4px", "6px", "8px", "10px", "12px","Cancel" };
	
	
	int idsOptionsImageAttach[] = new int[] { 1, 2, 3};
	String idsNameOptionsImageAttach[] = new String[] { "Camera", "Gallery", "Cancel"};
	
	int idsOptionsErrase[] = new int[] { 1, 2, 3};
	String idsNameOptionsErrase[] = new String[] { "Rub", "Clear all", "Cancel"};
	
	
	
	int pencileWidth = 8;
	int bgColor = -1445390 ;
	public Dialog lDialog = null;
//	02-27 13:10:30.475: I/System.out(4996): -------color ; -6614777
//07-10 13:36:55.755: I/System.out(6887): -----------color change----------15728127
//07-10 13:39:24.317: I/System.out(22065): -----------color change--------- : -16707051
///07-10 13:40:00.966: I/System.out(22065): -----------color change--------- : -1445390

	int initColor = -3334637;//Color.rgb(30,30,30);//-16707051;//
	boolean bgColoeToBeSelect ;
	public QuickAction quickAction, quickActionUserStstus;
	Bitmap backgroundBitmap = null;
	private String cameraImagePath = null;
	boolean frontCam ,rearCam;
	private static final byte POSITION_CAMERA_PICTURE = 3;
	Display display ;
	int leftH = 0 ;
//	Bitmap logo;
	boolean erase;
	int previousSelectedColor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 idsNameOptions = new String[] { getString(R.string.emboss),  getString(R.string.blur),
				 getString(R.string._2px),  getString(R.string._4px), getString(R.string._6px), getString(R.string._8px), getString(R.string._10px), getString(R.string._12px),getString(R.string.cancel) };
		
		
		idsOptionsImageAttach = new int[] { 1, 2, 3};//take_photo
		idsNameOptionsImageAttach = new String[] {getString(R.string.take_photo), getString(R.string.gallery), getString(R.string.cancel)};
		
		
		idsNameOptionsErrase = new String[] { getString(R.string.rub), getString(R.string.clear_all),getString(R.string.cancel)};
		
		
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		 display = wm.getDefaultDisplay();
		LayoutInflater inflater = LayoutInflater.from(this);
		View panel = inflater.inflate(R.layout.panel, null, false);
		panel.measure(display.getWidth(), display.getHeight());
		leftH = panel.getHeight() ;
		
//		//System.out.println("---leftH2 :"+leftH);
		parent = new LinearLayout(this);
		parent.setBackgroundColor(Color.WHITE);
		parent.setOrientation(LinearLayout.VERTICAL);
		ImageView bb = (ImageView) panel.findViewById(R.id.done);
		bb.setOnClickListener(panelListener);
		Button b = (Button) panel.findViewById(R.id.effact);
		b.setOnClickListener(panelListener);
		b = (Button) panel.findViewById(R.id.erasar);
		b.setOnClickListener(panelListener);
		b = (Button) panel.findViewById(R.id.color);
		b.setOnClickListener(panelListener);

		panel.findViewById(R.id.pencil).setOnClickListener(panelListener);
		panel.findViewById(R.id.close).setOnClickListener(panelListener);
		panel.findViewById(R.id.attach_image).setOnClickListener(panelListener);
		panel.findViewById(R.id.attach_image_gall).setOnClickListener(panelListener);

		parent.addView(panel);
		parent.addView(view = new MyView(this));
		view.setPadding(3, 3, 3, 3);
		setContentView(parent);
	
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(initColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(pencileWidth);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
//		BusinessProxy.sSelf.recordScreenStatistics("rt-canvas-open", true, true);// Add Record
//		logo =  BitmapFactory.decodeResource(getResources(), R.drawable.icon1);
		blankCanvas = true;
		previousSelectedColor = mPaint.getColor();
		
		//Add Google Analytics
//		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Doodle Draw Screen");
//        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
//        t.send(new HitBuilders.AppViewBuilder().build());
		
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");
		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	protected void onResume() {
		try{
			if(cameraImagePath != null)
			{
				if(backgroundBitmap!=null)
					backgroundBitmap.recycle();
				if(backgroundBitmap!=null){
					backgroundBitmap.recycle();
					backgroundBitmap=null;
					backgroundBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
					view.invalidate();
				}
				backgroundBitmap = getBitmap(Uri.parse(cameraImagePath));
				if(backgroundBitmap==null){
				}
				if(view!=null){
					view.clear();
					view.invalidate();
				}
				blankCanvas =false;
				bgColor = -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onResume();
	}
	public void onBackPressed() {
		new AlertDialog.Builder(this).setMessage(getString(R.string.exit_canvas))
		.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog,
					int whichButton) {
				if (lDialog != null && lDialog.isShowing()) {
					lDialog.dismiss();
					lDialog = null;
				}
				overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
				finish();
		}
		}).setNegativeButton(R.string.no, null).show();
	};
	View.OnClickListener panelListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			try{
				
			
			if(quickAction!=null)
				quickAction.hide();
			}catch (Exception e) {
			}
			mPaint.setXfermode(null);
			mPaint.setAlpha(0xFF);
			
			switch (view.getId()) {
			case R.id.attach_image:
//				openCamera();
				if (quickAction != null)
					quickAction.hide();
//				action(idsOptionsImageAttach, idsNameOptionsImageAttach, (byte) 4);
//				quickAction.show(view);
				cameraImagePath = AppUtil.capturedPath1 = null;
				AppUtil.openGallery(RTCanvas.this, AppUtil.POSITION_GALLRY_PICTURE);
				break;
			case R.id.attach_image_gall:
//				Intent	intent = new Intent(RTCanvas.this, RocketalkMultipleSelectImage.class);
//				intent.putExtra("MAX", 1);
//				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
//						| Intent.FLAG_ACTIVITY_CLEAR_TOP
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
				AppUtil.openGallery(RTCanvas.this, AppUtil.POSITION_GALLRY_PICTURE);
				break;
			case R.id.close:
				onBackPressed() ;
				
				break;
			case R.id.done:
				if(sendClicked)
					return;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					save();
				}
			}).start();
				
//				BusinessProxy.sSelf.recordScreenStatistics("rt-canvas-save", true, true);
				break;
			case R.id.pencil:
				erase = false;
				mPaint.setMaskFilter(null);
				mPaint.setColor(previousSelectedColor);
				mPaint.setStrokeWidth(pencileWidth);
				break;
			case R.id.effact:
			
				if (quickAction != null)
					quickAction.hide();
				action(idsOptions, idsNameOptions, (byte) 3);
				quickAction.show(view);
				break;
			case R.id.erasar:
				//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				if (quickAction != null)
					quickAction.hide();
				action(idsOptionsErrase, idsNameOptionsErrase , (byte) 5);
				quickAction.show(view);
				
				break;
			case R.id.color:
//				mAddDialog = new Dialog(RTCanvas.this/*,
//						android.R.style.Theme_Translucent_NoTitleBar*/);
//				
////				ColorPickerView(Context c, OnColorChangedListener l, int color, int defaultColor) {
//				colorPickerView = new ColorPickerView(RTCanvas.this,
//						RTCanvas.this, mPaint.getColor(), mPaint.getColor());
//				colorPickerView.setLayoutParams(LAYOUT_PARAM);
//				mAddDialog.setContentView(colorPickerView);
//				mAddDialog.getWindow().setGravity(Gravity.CENTER);
//				 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//				    lp.copyFrom(mAddDialog.getWindow().getAttributes());
//				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
//				    lp.height = WindowManager.LayoutParams.FILL_PARENT;
//				    mAddDialog.getWindow().setAttributes(lp);
//				mAddDialog.show();
				
//				intent = new Intent(RTCanvas.this, ColourPickerActivity.class);
//				startActivity(intent);
				openDialog(mPaint.getColor(), true);
				break;
			}
		}
	};
	private final LayoutParams LAYOUT_PARAM = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	void openDialog(int color, boolean supportsAlpha) {
		ColourPickerDialog dialog = new ColourPickerDialog(RTCanvas.this, color = previousSelectedColor, supportsAlpha, new ColourPickerDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(ColourPickerDialog dialog, int color) {
//				Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
//				displayColor();
				erase = false;
				mPaint.setColor(color);
			}

			@Override
			public void onCancel(ColourPickerDialog dialog) {
//				Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, false,name.length);

		for (int i = 0; i < name.length; i++) {
			Drawable icon = null;
			 if (i > 2) {
				icon = getResources().getDrawable(R.drawable.pencil) ;
			}
			ActionItem nextItem = new ActionItem(ids[i], name[i], icon);
			quickAction.addActionItem(nextItem);
		}
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if(type==5){
							if(actionId==1){
								erase = true;
								previousSelectedColor = mPaint.getColor();
//								mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
								return;
							}
							else if(actionId==2){
								new AlertDialog.Builder(RTCanvas.this).setMessage(getString(R.string.clear_all_msg))
								.setPositiveButton(R.string.yes,
										new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {
										if (lDialog != null && lDialog.isShowing()) {
											lDialog.dismiss();
											lDialog = null;
										}
										if(view!=null){
											view.clear() ;
											view.invalidate() ;
											mPaint.setMaskFilter(null);
											mPaint.setStrokeWidth(pencileWidth);
										}
								}
								}).setNegativeButton(R.string.no, null).show();
							}
						}
						if(type==4){
							if(actionId==1){
//								cameraImagePath = AppUtil.capturedPath1 = null;
								AppUtil.openCamera(RTCanvas.this, AppUtil.capturedPath1, AppUtil.POSITION_CAMERA_PICTURE);
							}
							if(actionId==2){
//								Intent	intent = new Intent(RTCanvas.this, RocketalkMultipleSelectImage.class);
//								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
//														| Intent.FLAG_ACTIVITY_CLEAR_TOP
//														| Intent.FLAG_ACTIVITY_NEW_TASK);
//												startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
								cameraImagePath = AppUtil.capturedPath1 = null;
								AppUtil.openGallery(RTCanvas.this, AppUtil.POSITION_GALLRY_PICTURE);
							}
							return;
						}
						if (actionId == 1) {
							if (mPaint.getMaskFilter() != mEmboss) {
								mPaint.setMaskFilter(mEmboss);
							} else {
								mPaint.setMaskFilter(null);
							}
							mPaint.setStrokeWidth(pencileWidth);
						} 
//						else if (actionId == 2) {
//							mPaint.setXfermode(new PorterDuffXfermode(
//									PorterDuff.Mode.SRC_ATOP));
//							mPaint.setAlpha(0x80);
//							mPaint.setStrokeWidth(pencileWidth);
//
//						}
						else if (actionId == 2) {
							
							mBlur = new BlurMaskFilter(pencileWidth, BlurMaskFilter.Blur.NORMAL);
							
							
							if (mPaint.getMaskFilter() != mBlur) {
								mPaint.setMaskFilter(mBlur);
							} else {
								mPaint.setMaskFilter(null);
								mPaint.setStrokeWidth(pencileWidth);
							}
						} 
						//else if (actionId > 3) 
						{
							switch (actionId) {
							case 4:
								mPaint.setStrokeWidth(pencileWidth = 2);
								break;
							
							case 5:
								mPaint.setStrokeWidth(pencileWidth = 4);
								break;
							case 6:
								mPaint.setStrokeWidth(pencileWidth = 6);
								break;
							case 7:
								mPaint.setStrokeWidth(pencileWidth = 8);
								break;
							case 8:
								mPaint.setStrokeWidth(pencileWidth = 10);
								break;
							case 9:
								mPaint.setStrokeWidth(pencileWidth = 12);
								break;
							case 3:
//								bgColoeToBeSelect = true ;
//								mAddDialog = new Dialog(RTCanvas.this);
//								colorPickerView = new ColorPickerView(RTCanvas.this,
//										RTCanvas.this, bgColor, mPaint.getColor(),RTCanvas.this);
//								mAddDialog.setContentView(colorPickerView);
//								mAddDialog.show();
								break;
							case 11:
								
								final DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
//										BusinessProxy.sSelf.recordScreenStatistics("rt-canvas-clear", true, true);	
										if (lDialog != null && lDialog.isShowing()) {
											lDialog.dismiss();
											lDialog = null;
										}
										if(view!=null){
											view.clear() ;
											view.invalidate() ;
											}
										
									}
								};
//								showAlertMessage(
//										"RockeTalk",
//										"Do you want to clear canvas?",
//										new int[] { DialogInterface.BUTTON_POSITIVE,
//												DialogInterface.BUTTON_NEGATIVE }, new String[] {
//												"Yes", "No" }, new DialogInterface.OnClickListener[] {
//												exitHandler, null });
								
								
								break;
							}
		
						}

					}
				});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				
			}
		});
	}
	
	private void openCamera() {
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");

		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		/*	cameraImagePath = null;
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			cameraImagePath = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			this.startActivityForResult(i, POSITION_CAMERA_PICTURE);*/
			
		

			//It would be safer to use the constant PackageManager.FEATURE_CAMERA_FRONT
			//but since it is not defined for Android 2.2, I substituted the literal value
			
			
			if((frontCam && rearCam) || (!frontCam && rearCam)){
				cameraImagePath = null;
//				File file = new File(Environment.getExternalStorageDirectory(),
//						getRandomNumber() + ".jpg");
				 String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat"+ File.separator + getRandomNumber() + ".jpg";
				  File file = new File(filename);
		          if (!file.exists()) {
		              file.mkdirs();
		          }
				cameraImagePath = file.getPath();
				Uri outputFileUri = Uri.fromFile(file);
				//Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
				
				////System.out.println("cameraImagePath===on"+cameraImagePath);
				Intent i =new Intent(RTCanvas.this,DgCamActivity.class);
				  i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				 i.putExtra("urlpath", cameraImagePath );
				 
				this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
			}if(frontCam && !rearCam){
			cameraImagePath = null;
//				File file = new File(Environment.getExternalStorageDirectory(),
//						getRandomNumber() + ".jpg");
			String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat"+ File.separator + getRandomNumber() + ".jpg";
			  File file = new File(filename);
	          if (!file.exists()) {
	              file.mkdirs();
	          }
				cameraImagePath = file.getPath();
				Uri outputFileUri = Uri.fromFile(file);
				Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
				i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
			}
			
		}

	public static long getRandomNumber() {
		return System.currentTimeMillis();// rand.nextInt();
	}

	public static String imagePath = "rocketalk/canvas";
	private static File cacheDir;

	private static File getCacheDirectory(Context context) {

		if (cacheDir != null)
			return cacheDir;
		String sdState = Environment.getExternalStorageState();
//		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
//			File sdDir = Environment.getExternalStorageDirectory();
////			cacheDir = new File(sdDir, imagePath);
////			cacheDir = new File(Utilities.getPicPath());
//			String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat"+ File.separator + getRandomNumber() + ".jpg";
//			cacheDir = new File(filename);
//			
//		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

	boolean sendClicked = false;
	public void save() {
		getCacheDirectory(this);
		try {
			if(blankCanvas){
			final DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
//					BusinessProxy.sSelf.recordScreenStatistics("rt-canvas-close", true, true);	
					if (lDialog != null && lDialog.isShowing()) {
						lDialog.dismiss();
						lDialog = null;
					}
					
				}
			};
//			showAlertMessage(
//					"RockeTalk",
//					getString(R.string.draw_something),
//					new int[] { DialogInterface.BUTTON_POSITIVE
//							 }, new String[] {
//						getString(R.string.ok) }, new DialogInterface.OnClickListener[] {
//							exitHandler});
			return;
			}
			sendClicked = true;
			view.setDrawingCacheEnabled(true);
			Bitmap b = view.getDrawingCache();
			File f = new File(cacheDir, "RT_" + System.currentTimeMillis()
					+ "_jmd.jpeg");
			f.createNewFile();
			FileOutputStream fos = null;

			fos = new FileOutputStream(f);

			b.compress(Constants.COMPRESS_TYPE, Constants.COMPRESS, fos);
			fos.close();
			b.recycle();
			getIntent().setData(Uri.fromFile(f));
			setResult(RESULT_OK, getIntent());
			if(backgroundBitmap!=null){
				backgroundBitmap.recycle() ;
			}
			finish();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	public void colorChanged(int color) {
//		//System.out.println("-----------color change---------2"+color);
		mPaint.setColor(color);
	}

	public class MyView extends View {

		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		private boolean drawBorder = false ;
		public MyView(Context c) {
			super(c);

			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}

		public void clear(){
			if(mBitmap!=null){
			mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			blankCanvas =true;
			mPaint.setMaskFilter(null);
			mPaint.setStrokeWidth(pencileWidth);
			}
		}
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			if(mBitmap==null)
			{
				mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
//				mCanvas.drawRect(r, paint)
				
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if(bgColor!=-1)
				canvas.drawColor(bgColor);
			if(backgroundBitmap!=null){
				int centreX = (display.getWidth()  - backgroundBitmap.getWidth()) /2 ;
				int		centreY = ((display.getHeight() )- backgroundBitmap.getHeight()) /2;
				if(leftH==0)
					leftH=50 ;
				canvas.drawBitmap(backgroundBitmap, centreX, centreY-leftH, null);
			}
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
//			int colo = mPaint.getColor();
			float strok = mPaint.getStrokeWidth() ;
			//Draw Border
//			if(!drawBorder)
//			{
//				drawBorder = true ;
//				mPaint.setColor(Color.rgb(255, 0, 0)); 
//				mPaint.setStrokeWidth(5);
//				canvas.drawRect(0, 0, mBitmap.getWidth()-0, mBitmap.getHeight()-0, mPaint);
//		    }
			if(erase)
				mPaint.setColor(bgColor);
			mPaint.setStrokeWidth(strok);
//            canvas.drawBitmap(logo, mBitmap.getWidth()-logo.getWidth(), 0, null);
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			blankCanvas =false;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
		@Override
		protected void onDetachedFromWindow() {
			/// TODO Auto-generated method stub
			super.onDetachedFromWindow();
			mBitmap.recycle() ;
			if(backgroundBitmap!=null){
				backgroundBitmap.recycle() ;
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public void colorChanged(String key, int color) {
//		//System.out.println("-----------color change--------- : "+color);
		if(key==null)
			{
			if (mAddDialog != null && mAddDialog.isShowing())
				mAddDialog.dismiss();
			return ;
			}
		if(bgColoeToBeSelect){
			bgColor = color ;
			if(view!=null)
				view.invalidate();
		}else
		mPaint.setColor(color);
		
		bgColoeToBeSelect = false ;
		
		if (mAddDialog != null && mAddDialog.isShowing())
			mAddDialog.dismiss();
		
		if(bgColor>=0){
			if(backgroundBitmap!=null){
			backgroundBitmap.recycle();
			backgroundBitmap=null;
			}
		}
//		//System.out.println("-------color ; "+color);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
			
				case AppUtil.POSITION_GALLRY_PICTURE:
				case AppUtil.POSITION_CAMERA_PICTURE:
					
					if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        cameraImagePath = AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
                    }
					if(requestCode == AppUtil.POSITION_CAMERA_PICTURE)
						cameraImagePath = AppUtil.capturedPath1;
					
//					 Bitmap bm=null;
					if((frontCam && rearCam) || (!frontCam && rearCam)){
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither=false;                     //Disable Dithering mode
						bfo.inPurgeable=true; 
						if(backgroundBitmap!=null){
							
							backgroundBitmap.recycle();
							backgroundBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
							view.invalidate() ;
						}
						
						backgroundBitmap=null;
//						backgroundBitmap = BitmapFactory.decodeFile(cameraImagePath,bfo);
						backgroundBitmap = getBitmap((Uri.parse(cameraImagePath)));
						blankCanvas =false;
						if(requestCode == AppUtil.POSITION_CAMERA_PICTURE)
							backgroundBitmap = imageRotateafterClick(backgroundBitmap,cameraImagePath);
						if(view!=null){
							view.clear() ;
							view.invalidate() ;
							}
					}if(frontCam && !rearCam){
						if(backgroundBitmap!=null){
							
							backgroundBitmap.recycle();
//							backgroundBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
//							view.invalidate() ;
						}
//						backgroundBitmap = getBitmap((Uri.parse(cameraImagePath)));//ImageUtils.getImageFor(cameraImagePath, 4);
					}
					bgColor=-1;
//					//System.out.println("Picture Taken:"+cameraImagePath);
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		}
	public  void function(Uri uri) {
		InputStream in = null;
		Bitmap bitmap ;
		try {
			// int origWidth = 327;
			// int origHeight = 496;
			// int newWidth = 320;
			// int newHeight = 455;
			int origWidth = 50;
			int origHeight = 150;
			int newWidth = display.getWidth();
			int newHeight = display.getHeight();
			float scaleWidth;
			float scaleHeight;
			float scaleFactor;
			long start;
			long end;

			File file = new File(uri.toString());
			if (file.exists()) {
				// Toast.makeText(getActivity(), "File exists in /mnt",
				// Toast.LENGTH_SHORT);
			}

			in = new FileInputStream(file);// new
											// inopenFileInput(uri.toString());//getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			// BitmapFactory.Options bfo = new BitmapFactory.Options();
			// bfo.inDither=false; //Disable Dithering mode
			// bfo.inPurgeable=true;
			o.inDither = false; // Disable Dithering mode
			o.inPurgeable = true;
			o.inJustDecodeBounds = true; // request only the dimesion
			bitmap = BitmapFactory.decodeStream(in, null, o);
			
			in.close();
			
			file = new File(uri.toString());
			in = new FileInputStream(file);
			o = new BitmapFactory.Options();
			o.inSampleSize = calculateInSampleSize(o,newWidth,newHeight);
//			b = BitmapFactory.decodeStream(in, null, o);
			bitmap = BitmapFactory.decodeStream(in, null, o);

			origWidth = bitmap.getHeight();
			origHeight = bitmap.getWidth();
			
//			//System.out.println("origWidth:" + origWidth);
//			//System.out.println("origHeight:" + origHeight);

			start = System.nanoTime();

			if (origWidth >= origHeight) {
				scaleWidth = (float) newWidth / origWidth;
				scaleHeight = scaleWidth;
			} else {
				scaleHeight = (float) newHeight / origHeight;
				scaleWidth = scaleHeight;
			}
			newWidth = Math.round(origWidth * scaleWidth);
			newHeight = Math.round(origHeight * scaleWidth);
			
			file = new File(uri.toString());
			in = new FileInputStream(file);
			o = new BitmapFactory.Options();
			o.inSampleSize = calculateInSampleSize(o,newWidth,newHeight);
//			b = BitmapFactory.decodeStream(in, null, o);
			bitmap = BitmapFactory.decodeStream(in, null, o);
			
//			//System.out.println("newWidth:" + newWidth);
//			//System.out.println("newHeight:" + newHeight);
			

			
			Matrix matrix = new Matrix();
			// RESIZE THE BIT MAP
			matrix.postScale(scaleWidth, scaleHeight);
			// RECREATE THE NEW BITMAP
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,false);//,
//					matrix, false);
			
			File sdDir = Environment.getExternalStorageDirectory();
			File cacheDir = new File(sdDir, imageTempPath);
			if (!cacheDir.exists())
				cacheDir.mkdirs();
			newPath = newWidth+"x"+newHeight+"rtc.jpg" ;
			File f = new File(cacheDir, newPath);
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);
			resizedBitmap.compress(Constants.COMPRESS_TYPE, Constants.COMPRESS, out);
			resizedBitmap.recycle();
			in.close();
			out.close();
			end = System.nanoTime();
//			//System.out.println("TIME" + " " + (end - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	String newPath = "" ;
	public static final String imageTempPath = "Rocketalk/streem/tmp/Rocketalk";// ".rocketalk/streem/images"
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	private Bitmap getBitmap(Uri uri) { 
		InputStream in = null;
//		if(1==1){
//			
//			try{
//				function(uri) ;
//				File filet = new File(uri.toString());
//		        in = new FileInputStream(newPath);
//		        BitmapFactory.Options o = new BitmapFactory.Options();
//		        Bitmap   b = BitmapFactory.decodeStream(in, null, o);
//return b;
//			}catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//		//System.out.println("----"+uri.toString());
	    
	    try {
	        final int IMAGE_MAX_SIZE = display.getWidth()*display.getHeight();//200000; // 0.2MP
	        
//	        File f = new File(uri.toString());
	        
	        File file = new File(uri.toString());
	        if(file.exists()){
//	            Toast.makeText(getActivity(), "File exists in /mnt", Toast.LENGTH_SHORT);
	        }
	        
	        in = new FileInputStream(file);//new inopenFileInput(uri.toString());//getContentResolver().openInputStream(uri);

	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
//			BitmapFactory.Options bfo = new BitmapFactory.Options();
//			bfo.inDither=false;                     //Disable Dithering mode
//			bfo.inPurgeable=true;  
			o.inDither=false;                     //Disable Dithering mode
			o.inPurgeable=true;  
	        o.inJustDecodeBounds = true;                    //request only the dimesion
	        BitmapFactory.decodeStream(in, null, o);
	        in.close();

	        int scale = 1;
	        while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
	            scale++;
	        }

	        Bitmap b = null;
//	        in = getContentResolver().openInputStream(uri);
//	        in = openFileInput(uri.toString());
	        file = new File(uri.toString());
	        in = new FileInputStream(file);
	        if (scale > 1) {
	            scale--;
	            // scale to max possible inSampleSize that still yields an image
	            // larger than target
	            o = new BitmapFactory.Options();
	            o.inSampleSize = scale;
	            b = BitmapFactory.decodeStream(in, null, o);
	            // resize to desired dimensions
	            int height = b.getHeight();
	            int width = b.getWidth();

	            double y = Math.sqrt(IMAGE_MAX_SIZE
	                    / (((double) width) / height));
	            double x = (y / height) * width;

	            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
	            b.recycle();
	            b = scaledBitmap;
	            System.gc();
	        } else {
	            b = BitmapFactory.decodeStream(in);
	        }
	        in.close();

	        return b;
	    } catch (IOException e) {
	    	e.printStackTrace();
	        return null;
	    }

	}
	
	public static Bitmap imageRotateafterClick(Bitmap bm ,String path){
		 Matrix matrix = new Matrix();
		    matrix.postRotate(90);
		   if(bm!=null){
		    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		    FileOutputStream fOut;
		    try {
		        fOut = new FileOutputStream(path);
		        bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		        fOut.flush();
		        fOut.close();

		    } catch (FileNotFoundException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
	}
		    return bm;
	}
}