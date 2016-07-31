package com.superchat.ui;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.superchat.utils.Utilities;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
//import com.kainat.app.android.util.Utilities;

public class CImageView extends ImageView {

	private String mImagePah;
	private String mImageUrl;
	public Bitmap bitmap;
	int gender ;
	Context context;
	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
	String imageType ;
	/**
	 * @param context
	 */
	public CImageView(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		//@Mahesh - plz check
//		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.image);
//		imageType= ta.getString(R.styleable.image_imagetype);
//		ta.recycle();
		animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
	}
	public int aniType =-1;
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/**
	 * Gets the value of mImagePah
	 * 
	 * @return The mImagePah
	 */
	public final String getImagePah() {
		return mImagePah;
	}

	/**
	 * Sets the value of imagePah
	 * 
	 * @param imagePah
	 *            The mImagePah to set
	 */
	public final void setImagePah(String imagePah) {
		mImagePah = imagePah;
		setImageURI(Uri.parse(imagePah));
	}
	public final void setImageUrl(String imagePah) {
		mImageUrl = imagePah;
	}
	public final String getImageUrl() {//np
		return mImageUrl ;
	}
//	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		try{
//			if(getVisibility()!=View.VISIBLE)
//				return;
			if(bitmap != null && bitmap.isRecycled()){

//						System.out
//						 .println("--------------------CIMAGEVIEW---onDraw return---");
						return ;
			}
			
//			Drawable drawable = getDrawable();
//			if (drawable instanceof BitmapDrawable) {
//				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//				Bitmap bitmap = bitmapDrawable.getBitmap();
//					if(bitmap != null && bitmap.isRecycled()){
////				bitmap.recycle();
//						System.out
//						 .println("--------------------CIMAGEVIEW---onDraw return---");
//						return ;
//					}
//				
//			}
//			if (ImageDownloader.thumbCircile) {
//				if (imageType != null && imageType.equalsIgnoreCase("1")) {
//					Drawable drawable = getDrawable();
//					if (drawable == null) {
//						return;
//					}
//					if (getWidth() == 0 || getHeight() == 0) {
//						return;
//					}
//					Bitmap b = ((BitmapDrawable) drawable).getBitmap();
//					if (b != null && b.isRecycled()) {
//						return;
//					}
//					int w = getWidth(), h = getHeight();
//					b = getResizedBitmap(b, getWidth(), getHeight());
//					Bitmap roundBitmap = getRoundedBitmap(b, w, Color.WHITE);
//					b.recycle();
//					int centreX = (getWidth() - roundBitmap.getWidth()) / 2;
//					int centreY = (getHeight() - roundBitmap.getHeight()) / 2;
////					setImageBitmap(roundBitmap);
////					ImageDownloader.saveCache(roundBitmap, getImageUrl()) ;
////					super.onDraw(canvas);
//					canvas.drawBitmap(roundBitmap, centreX, centreY, null);
//					roundBitmap.recycle();
//				}
//			}
//			else
		super.onDraw(canvas);
			{
			 Drawable drawable = getDrawable();

			    if (drawable == null) {
			        return;
			    }

			    if (getWidth() == 0 || getHeight() == 0) {
			        return; 
			    }
//			    Bitmap b =  drawable.getBitmap();
			    Bitmap b =  drawableToBitmap(drawable);
			    if(b != null){
//			    	if(getLayoutParams()!=null){
//			    	getLayoutParams().width=b.getWidth();
//			    	getLayoutParams().width=b.getHeight();
//			    	}
//			    	ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(
//							b.getWidth(), b.getHeight());
//					setLayoutParams(parms);
//			    System.out
//				 .println("--------------------CIMAGEVIEW---onDraw return--getw-"+w);
			    }
			    if(b != null && b.isRecycled()){
//					System.out
//					 .println("--------------------CIMAGEVIEW---onDraw return---");
					return ;
			    }
			    super.onDraw(canvas) ;
//			int centreX = (getWidth()  - b.getWidth()) /2 ;
//
//			  int centreY = (getHeight() - b.getHeight()) /2 ;
//			  Paint paint = new Paint(); 
//				paint.setColor(Color.GRAY);
//			  canvas.drawRect(new Rect(centreX,centreY,b.getWidth(),b.getHeight()), paint);
//			    canvas.drawBitmap(b, centreX,centreY, null);
			    
			}
			    
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    Bitmap bitmap = null;

	    if (drawable instanceof BitmapDrawable) {
	        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
	        if(bitmapDrawable.getBitmap() != null) {
	            return bitmapDrawable.getBitmap();
	        }
	    }

	    if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
	        bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
	    } else {
	        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    }

	    Canvas canvas = new Canvas(bitmap);
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);
	    return bitmap;
	}
	
	
	public int w = 0;
	public int getw(){
		return w;
	}
	Animation animation ;
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		 
		int width = bm.getWidth();
		 
		int height = bm.getHeight();
		 
		float scaleWidth = ((float) newWidth) / width;
		 
		float scaleHeight = ((float) newHeight) / height;
		 
		// CREATE A MATRIX FOR THE MANIPULATION
		 
		Matrix matrix = new Matrix();
		 
		// RESIZE THE BIT MAP
		 
		matrix.postScale(scaleWidth, scaleHeight);
		 
		// RECREATE THE NEW BITMAP
		 
		bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
//		bm.recycle();
		return bm;
		 
		}
	public static Bitmap getCircularBitmap(Bitmap bitmap) {
		int targetWidth =50;
		int targetHeight = 50 ;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Config.ARGB_8888);
				Canvas canvas = new Canvas(targetBitmap);
				Path path = new Path();
				path.addCircle(((float) targetWidth) / 2,
				((float) targetHeight) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CW);
				Paint paint = new Paint(); 
				paint.setColor(Color.GRAY); 
				//paint.setStyle(Paint.Style.STROKE);
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
				paint.setDither(true);
				paint.setFilterBitmap(true);
				canvas.drawOval(new RectF(0, 0, targetWidth, targetHeight), paint) ;
				//paint.setColor(Color.TRANSPARENT); 
				canvas.clipPath(path);
				Bitmap sourceBitmap = bitmap;
				canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new RectF(sourceBitmap.getWidth()/2-50/2, sourceBitmap.getHeight()/2-50/2, targetWidth,
				targetHeight), paint);
				return targetBitmap ;
	}
	public Bitmap getCroppedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(50,
	            50, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    return output;
	}
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
	    Bitmap sbmp;
	    if(bmp.getWidth() != radius || bmp.getHeight() != radius)
	        sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
	    else
	        sbmp = bmp;
	    Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
	            sbmp.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xffa19774;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

	    paint.setAntiAlias(true);
	    paint.setFilterBitmap(true);
	    paint.setDither(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(Color.parseColor("#BAB399"));
	    canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
	            sbmp.getWidth() / 2+0.1f, paint);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(sbmp, rect, rect, paint);


	            return output;
	}
	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float upperLeft,
	        float upperRight, float lowerRight, float lowerLeft, int endWidth,
	        int endHeight) {
	    float densityMultiplier = context.getResources().getDisplayMetrics().density;

	    // scale incoming bitmap to appropriate px size given arguments and display dpi
	    bitmap = Bitmap.createScaledBitmap(bitmap, 
	            Math.round(endWidth * densityMultiplier),
	            Math.round(endHeight * densityMultiplier), true);

	    // create empty bitmap for drawing
	    Bitmap output = Bitmap.createBitmap(
	            Math.round(endWidth * densityMultiplier),
	            Math.round(endHeight * densityMultiplier), Config.ARGB_8888);

	    // get canvas for empty bitmap
	    Canvas canvas = new Canvas(output);
	    int width = canvas.getWidth();
	    int height = canvas.getHeight();

	    // scale the rounded corners appropriately given dpi
	    upperLeft *= densityMultiplier;
	    upperRight *= densityMultiplier;
	    lowerRight *= densityMultiplier;
	    lowerLeft *= densityMultiplier;

	    Paint paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setColor(Color.WHITE);

	    // fill the canvas with transparency
	    canvas.drawARGB(0, 0, 0, 0);

	    // draw the rounded corners around the image rect. clockwise, starting in upper left.
	    canvas.drawCircle(upperLeft, upperLeft, upperLeft, paint);
	    canvas.drawCircle(width - upperRight, upperRight, upperRight, paint);
	    canvas.drawCircle(width - lowerRight, height - lowerRight, lowerRight, paint);
	    canvas.drawCircle(lowerLeft, height - lowerLeft, lowerLeft, paint);

	    // fill in all the gaps between circles. clockwise, starting at top.
	    RectF rectT = new RectF(upperLeft, 0, width - upperRight, height / 2);
	    RectF rectR = new RectF(width / 2, upperRight, width, height - lowerRight);
	    RectF rectB = new RectF(lowerLeft, height / 2, width - lowerRight, height);
	    RectF rectL = new RectF(0, upperLeft, width / 2, height - lowerLeft);

	    canvas.drawRect(rectT, paint);
	    canvas.drawRect(rectR, paint);
	    canvas.drawRect(rectB, paint);
	    canvas.drawRect(rectL, paint);

	    // set up the rect for the image
	    Rect imageRect = new Rect(0, 0, width, height);

	    // set up paint object such that it only paints on Color.WHITE
	    paint.setXfermode(new AvoidXfermode(Color.WHITE, 255, AvoidXfermode.Mode.TARGET));

	    // draw resized bitmap onto imageRect in canvas, using paint as configured above
	    canvas.drawBitmap(bitmap, imageRect, imageRect, paint);

	    return output;
	}
	public  Bitmap getRoundedBitmap(Bitmap inpBitmap, int pixels, int color) {
		
		pixels=5;//50
//		Matrix matrix = new Matrix();
//		matrix.postScale(0.5f, 0.5f);
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0,50, 50, matrix, true);

		int w = getWidth();int h = getHeight();
//	    Bitmap inpBitmap = bitmap;
	    int width = 0;
	    int height = 0;
	    width = getWidth();
	    height = getHeight();

	    if (width <= height) {
	        height = width;
	    } else {
	        width = height;
	    }

	    Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, width, height);
	    final RectF rectF = new RectF(0, 0, width, height);
	    final RectF rectF2 = new RectF(2, 2, width-2, height-2);
	    final float roundPx = pixels;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
//	    paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f));
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//	    paint.setMaskFilter(null);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    
	    int  centreX = (getWidth()  - inpBitmap.getWidth()) /2 ;

		  int  		centreY = (getHeight() - inpBitmap.getHeight()) /2 ;
	    
	    canvas.drawBitmap(inpBitmap, centreX, centreY, paint);
		//@Mahesh - plz check
//	    paint.setColor(getResources().getColor(R.color.white));
	    paint.setStyle(Paint.Style.STROKE);
	    
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawRoundRect(rectF2, roundPx, roundPx, paint);
	    
	    return output;
	}

	@Override
	protected void finalize() throws Throwable {
//		System.out
//		 .println("--------------------CIMAGEVIEW---finalize-xy--"+getLeft()+"x"+getTop());
//		onDetachedFromWindowss();
		super.finalize();
	}
//	@Override
	protected void onDetachedFromWindow() {
		
//		
////		System.out
////			 .println("----------------CIMAGEVIEW----recycle---xy-1--"+getLeft()+"x"+getTop());
//		destroyDrawingCache() ;
//		
//		try{
//		Drawable drawable = getDrawable();
//		if (drawable instanceof BitmapDrawable) {
//			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//			Bitmap bitmap = bitmapDrawable.getBitmap();
////			System.out
////			 .println("-----------------CIMAGEVIEW---recycle----1--"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
//			
////			bitmapDrawable = null;
////			 System.out
////			 .println("--------------------recycle------"+bitmap.getRowBytes());
//		}
////		drawable = getBackground();
////		if (drawable instanceof BitmapDrawable) {
////			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
////			Bitmap bitmap = bitmapDrawable.getBitmap();
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
////			bitmapDrawable = null;
////			System.out
////			 .println("----------------CIMAGEVIEW----recycle-----2-"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////		}
////		Bitmap bitmap = getDrawingCache();
////		if (bitmap !=null) {
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
////			bitmap = null;
////			System.out
////			 .println("--------------------recycle-----2-"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////		}
//		}catch (Exception e) {
//			// TODO: handle exception
//		}
//		
////		System.out.println("-----onDetachedFromWindow-------"+getTag());
////		String s = (String)getTag();
////		SoftReference b = ImageDownloader.imageCache.remove(s.hashCode()) ;
////		((Bitmap)b.get()).recycle();
//		if(mImageUrl!=null && mImageUrl.trim().length()>0){
////			getDrawable().
//		}
		super.onDetachedFromWindow();
	}
	@Override
	protected void onAttachedToWindow() {
		setDrawingCacheEnabled(false) ;
//		System.out.println("-----onAttachedToWindow-------");
		super.onAttachedToWindow();
		
//		if(mImageUrl!=null && mImageUrl.trim().length()>0){
//		loadProfilePic = new LoadProfilePic();
//		loadProfilePic.execute(mImageUrl);
//		}
	}
	
	
	LoadProfilePic loadProfilePic;

	private class LoadProfilePic extends AsyncTask<String, Void, String> {

		Drawable drawable;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {

			try {
//				System.out.println("----image url ----:"+urls[0]);
//				UIActivityManager.getCacheDirectoryImage();
				
				String filename = String.valueOf(urls[0].hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(HomeScreen.cacheDirImage, filename);
				drawable = Drawable.createFromPath(f.getAbsolutePath());
				if(drawable==null){
				InputStream is = (InputStream) new URL(urls[0]).getContent();
				byte data[] = Utilities.inputStreamToByteArray(is);
				if(data.length > 0)
				{
					filename = String.valueOf(urls[0].hashCode());
					extension = "jpg";
					filename = filename + "." + extension;
					f = new File(HomeScreen.cacheDirImage, filename);
					Utilities.writeFile(data, f, urls[0].hashCode());
					drawable = Drawable.createFromPath(f.getAbsolutePath());
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
//				makeToast(e.toString());
				return null;
			} catch (OutOfMemoryError e) {
//				makeToast(e.toString());
				return null;
			}
			// drawable=loadImage(User.getInstance().profilePic);
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			//
			try{
//			ImageView i = ((ImageView) findViewById(R.id.signup_thumb));
			if(drawable!=null)
			setImageDrawable(drawable);
			
//			Utilities.startAnimition(ProfileActivity.this, i, R.anim.fade);
			}catch(Exception e){
				
			}
		}
	}
}
