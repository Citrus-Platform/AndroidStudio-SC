



package com.superchat.utils;

import android.content.Context;
import android.graphics.Typeface;

public class Methods
{

    private static float f_maxImageDimension = 150F;
    private static Typeface tf_Arial = null;
    private static Typeface tf_VagRoundBold = null;
    private static Typeface tf_VagRoundLight = null;

    public Methods()
    {
    }

    public static Typeface Get_tf_MyriadProRegular(Context context)
    {
        if (tf_VagRoundLight == null)
        {
            tf_VagRoundLight = Typeface.createFromAsset(context.getAssets(), "MyriadProRegular.ttf");
        }
        return tf_VagRoundLight;
    }

    public static Typeface Get_tf_Myriad_semi_bold(Context context)
    {
        if (tf_Arial == null)
        {
            tf_Arial = Typeface.createFromAsset(context.getAssets(), "MyriadProSemibold.ttf");
        }
        return tf_Arial;
    }

    public static Typeface Get_tf_VagRoundedBold(Context context)
    {
        if (tf_VagRoundBold == null)
        {
            tf_VagRoundBold = Typeface.createFromAsset(context.getAssets(), "VAGRoundedStd-Bold.otf");
        }
        return tf_VagRoundBold;
    }

    public static boolean VarifyAddress(String s)
    {
        return s.length() > 0;
    }

    public static boolean VarifyEmail(String s)
    {
        return s.length() > 0 && s.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    public static boolean VarifyName(String s)
    {
        return s.length() > 0;
    }

    public static boolean VarifyZip(String s)
    {
        return s.length() == 4;
    }

//    private static int getCameraPhotoOrientation(Context context, Uri uri, String s)
//    {
//        int i;
//        try
//        {
//            context.getContentResolver().notifyChange(uri, null);
//            i = (new ExifInterface((new File(s)).getAbsolutePath())).getAttributeInt("Orientation", 1);
//        }
//        catch (Exception exception)
//        {
//            exception.printStackTrace();
//            return 0;
//        }
//        switch (i)
//        {
//        case 4: // '\004'
//        case 5: // '\005'
//        case 7: // '\007'
//        default:
//            return 0;
//
//        case 8: // '\b'
//            return 270;
//
//        case 3: // '\003'
//            return 180;
//
//        case 6: // '\006'
//            return 90;
//        }
//    }

//    public static Drawable getScaledDrawableHandlingOOME(Context context, Uri uri)
//        throws MalformedURLException, IOException
//    {
//        int i;
//        boolean flag;
//        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        java.io.InputStream inputstream = context.getContentResolver().openInputStream(uri);
//        if (inputstream == null)
//        {
//            inputstream = (new URL(uri.toString())).openStream();
//        }
//        BitmapFactory.decodeStream(inputstream, null, options);
//        float f = options.outWidth;
//        float f1 = options.outHeight;
//        options.inJustDecodeBounds = false;
//        float f2;
//        java.io.InputStream inputstream1;
//        Bitmap bitmap;
//        BitmapDrawable bitmapdrawable;
//        if (f > f1)
//        {
//            f2 = f / f_maxImageDimension;
//        } else
//        {
//            f2 = f1 / f_maxImageDimension;
//        }
//        i = (int)(f2 + 1.0F);
//        flag = true;
//        while(true){
//        if (!flag)
//        {
//            break MISSING_BLOCK_LABEL_235;
//        }
//        options.inSampleSize = i;
//        inputstream1 = context.getContentResolver().openInputStream(uri);
//        if (inputstream1 != null)
//        {
//        	inputstream1 = (new URL(uri.toString())).openStream();
//            bitmap = BitmapFactory.decodeStream(inputstream1, null, options);
//            bitmapdrawable = new BitmapDrawable(context.getResources(), bitmap);
//            return bitmapdrawable;
//        }
//        Logger.e((new StringBuilder()).append("OOME-sample::").append(i).toString());
//        i *= 2;
//        System.gc();
//        if (i <= 16);
//        break MISSING_BLOCK_LABEL_237;
//        Exception exception1;
//        exception1;
//        exception1.printStackTrace();
//        flag = false;
//        continue; /* Loop/switch isn't completed */
//        Exception exception;
//        exception;
//        throw exception;
//        return null;
//        flag = false;
//        }
//    }
//
//    public static Drawable getScaledDrawableHandlingOOME(Context context, Uri uri, String s, int i)
//    {
//        int j;
//        boolean flag;
//        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(s, options);
//        float f = options.outWidth;
//        float f1 = options.outHeight;
//        Logger.d((new StringBuilder()).append("w,h::").append(f).append(",").append(f1).toString());
//        options.inJustDecodeBounds = false;
//        float f2;
//        int k;
//        Bitmap bitmap;
//        Matrix matrix;
//        BitmapDrawable bitmapdrawable;
//        if (f > f1)
//        {
//            f2 = f / f_maxImageDimension;
//        } else
//        {
//            f2 = f1 / f_maxImageDimension;
//        }
//        j = (int)(f2 + 1.0F);
//        k = getCameraPhotoOrientation(context, uri, s);
//        flag = true;
//_L2:
//        if (!flag)
//        {
//            break MISSING_BLOCK_LABEL_290;
//        }
//        options.inSampleSize = j;
//        bitmap = BitmapFactory.decodeFile(s, options);
//        Logger.d((new StringBuilder()).append("bitmap::").append(bitmap).toString());
//        if (k == 0)
//        {
//            break MISSING_BLOCK_LABEL_199;
//        }
//        matrix = new Matrix();
//        matrix.postRotate(k);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        bitmapdrawable = new BitmapDrawable(context.getResources(), bitmap);
//        return bitmapdrawable;
//        OutOfMemoryError outofmemoryerror;
//        outofmemoryerror;
//        Logger.e((new StringBuilder()).append("OOME-sample::").append(j).toString());
//        j *= 2;
//        System.gc();
//        if (j <= 16);
//        break MISSING_BLOCK_LABEL_292;
//        Exception exception1;
//        exception1;
//        exception1.printStackTrace();
//        flag = false;
//        continue; /* Loop/switch isn't completed */
//        Exception exception;
//        exception;
//        throw exception;
//        return null;
//        flag = false;
//        if (true) goto _L2; else goto _L1
//_L1:
//    }

}
