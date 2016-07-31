



package com.superchat.task;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.superchat.SuperChatApplication;
import com.superchat.data.beans.PhotoToLoad;

public class ImageLoaderWorker
    implements Runnable
{

    private Bitmap bitmap;
    private PhotoToLoad photo;

    public ImageLoaderWorker(PhotoToLoad phototoload)
    {
        photo = null;
        bitmap = null;
        photo = phototoload;
    }
private Bitmap getByteContactPhoto(){
	Uri uri3 = ContentUris.withAppendedId(
			android.provider.ContactsContract.Contacts.CONTENT_URI,
			Long.parseLong(photo.id));
	Bitmap contactPicture = null;
	if (uri3 != null) {
		java.io.InputStream inputstream = android.provider.ContactsContract.Contacts
				.openContactPhotoInputStream(SuperChatApplication.context.getContentResolver(), uri3);
		if (inputstream != null) {
			try {
				contactPicture = BitmapFactory.decodeStream(inputstream);
			} catch (OutOfMemoryError outofmemoryerror) {
				outofmemoryerror.printStackTrace();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	return contactPicture;
}
    public void run()
    {
        try
        {
            bitmap = getByteContactPhoto();//Imageutils.getByteContactPhoto(photo.id);
            if (bitmap != null)
            {
                SuperChatApplication.addBitmapToMemoryCache(photo.id, bitmap);
            }
        }
        catch (Exception exception) { }
        catch (OutOfMemoryError outofmemoryerror)
        {
            System.gc();
        }
        photo.iv.post(new Runnable() {


            public void run()
            {
                if (bitmap != null)
                {
                    photo.iv.setImageBitmap(bitmap);
                }
            }

            
        });
    }


}
