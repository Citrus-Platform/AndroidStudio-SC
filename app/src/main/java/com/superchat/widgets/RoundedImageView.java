



package com.superchat.widgets;

import com.superchat.utils.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView
{

    private boolean status;

    public RoundedImageView(Context context)
    {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RoundedImageView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int i)
    {
        Bitmap bitmap1;
        Bitmap bitmap2;
        Canvas canvas;
        Paint paint;
        Rect rect;
        if (bitmap.getWidth() != i || bitmap.getHeight() != i)
        {
            bitmap1 = Bitmap.createScaledBitmap(bitmap, i, i, true);

        } else
        {
            bitmap1 = bitmap;
        }
        bitmap2 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap2);
        paint = new Paint();
        rect = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(0.7F + (float)(bitmap1.getWidth() / 2), 0.7F + (float)(bitmap1.getHeight() / 2), 0.1F + (float)(bitmap1.getWidth() / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap1, rect, rect, paint);
        return bitmap2;
    }

    protected void onDraw(Canvas canvas)
    {
    	Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return; 
        }
        try{
        Bitmap b = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP 
            && drawable instanceof VectorDrawable) {
            ((VectorDrawable) drawable).draw(canvas);
            b = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas();
            c.setBitmap(b);
            drawable.draw(c);
        } 
        else {
            b = ((BitmapDrawable) drawable).getBitmap();
        }
if(b==null)
	return;
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0,0, null);
        
}catch(OutOfMemoryError error){
//	error.printStackTrace();
	if(error!=null && error.getMessage()!=null)
		Log.d("RoundedImageView", error.getMessage());
}
    }

    public void setRounded(boolean flag)
    {
        status = flag;
    }
}
