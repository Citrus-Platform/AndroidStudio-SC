package com.superchat.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nuhbye on 05/08/15.
 */
public class Util {


    public static final int SELECT_PICTURE_SIMPLE = 99;

    public static void fireIntentSelectPicture(Activity currActivity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        currActivity.startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), requestCode);
    }

    public static boolean isPanValid(String panNumber) {
        boolean isValid = false;
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        panNumber = panNumber.toUpperCase();
        Matcher matcher = pattern.matcher(panNumber);
        // Check if pattern matches
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }

    public static String getEditTextValue(final EditText ettext) {
        String value = "";
        if (ettext != null) {
            value = ettext.getText().toString().trim();
        }
        return value;
    }

    public static String getTruncatedTransactionId(String transactionId) {
        return transactionId.split("-")[0];
    }

    /**
     * Util Methods
     */

    public static long getFileSizeInMB(File file) {
        if (file != null) {
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            return fileSizeInMB;
        } else {
            return 0;
        }
    }

    public static Bitmap getBitmapFromFile(File f) {
        Bitmap bitmap = null;
       /* BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        bitmap = get_Picture_bitmap(f);
        return bitmap;
    }

    public static File getCachedImageFile(Context currContext, Bitmap bitmap) {
        File f = null;
        try {
            //create a file to write bitmap data
            f = new File(currContext.getCacheDir(), "signature.png");
            f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {

        }
        return f;
    }

    public static File getImageFile(Context currContext, Bitmap bitmap) {
        try {
            Uri uri = getImageUri(currContext, bitmap);
            String imagepath = getPath(currContext, uri);
            File imageFile = new File(imagepath);
            return imageFile;
        } catch (Exception e) {
            return null;
        }
    }

    public static Uri getImageUri(Context currContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(currContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getPath(Context currContext, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = currContext.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static void open_url(String link, Activity activity) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        activity.startActivity(browserIntent);
    }

    public static String formatGraphDate(String original) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
        try {
            Date date = format.parse(original);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * BITMAP Methods
     */
    public static Bitmap get_Picture_bitmap(File f) {

        long size_file = getFileSize(f);

        size_file = (size_file) / 1000;// in Kb now
        int ample_size = 1;

        if (size_file <= 250) {

            System.out.println("SSSSS1111= " + size_file);
            ample_size = 2;

        } else if (size_file > 251 && size_file < 1500) {

            System.out.println("SSSSS2222= " + size_file);
            ample_size = 4;

        } else if (size_file >= 1500 && size_file < 3000) {

            System.out.println("SSSSS3333= " + size_file);
            ample_size = 8;

        } else if (size_file >= 3000 && size_file <= 4500) {

            System.out.println("SSSSS4444= " + size_file);
            ample_size = 12;

        } else if (size_file >= 4500) {

            System.out.println("SSSSS4444= " + size_file);
            ample_size = 16;
        }

        // TEMP CODE
        ample_size = (ample_size / 2);

        Bitmap bitmap = null;

        BitmapFactory.Options bitoption = new BitmapFactory.Options();
        bitoption.inSampleSize = ample_size;

        Bitmap bitmapPhoto = BitmapFactory.decodeFile(f.getAbsolutePath(), bitoption);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f.getAbsolutePath());
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Matrix matrix = new Matrix();

        if ((orientation == 3)) {
            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 6) {
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 8) {
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else {
            matrix.postRotate(0);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        }

        return bitmap;

    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }

        return result;
    }
}
