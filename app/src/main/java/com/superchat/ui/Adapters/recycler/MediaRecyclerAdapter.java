package com.superchat.ui.Adapters.recycler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chat.sdk.db.ChatDBConstants;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.ui.Adapters.holders.MediaViewHolders;
import com.superchat.ui.Adapters.holders.EmptyViewHolders;
import com.superchat.ui.ChatListAdapter;
import com.superchat.utils.UtilSetFont;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int TYPE_EMPTY = 0;
    final int TYPE_ITEM = 1;

    private ArrayList<ContentValues> itemList;
    private Activity currActivity;

    public MediaRecyclerAdapter(Activity currActivity, ArrayList<ContentValues> itemList) {
        this.itemList = itemList;
        this.currActivity = currActivity;
    }

    private View getLayoutView(final ViewGroup parent, int view) {
        return LayoutInflater.from(parent.getContext()).inflate(view, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case TYPE_EMPTY: {
                view = getLayoutView(parent, R.layout.list_empty_view);
                viewHolder = new EmptyViewHolders(view, currActivity);
                break;
            }
            case TYPE_ITEM: {
                view = getLayoutView(parent, R.layout.list_media_item);
                viewHolder = new MediaViewHolders(view, currActivity);
                break;
            }
            default: {
                viewHolder = null;
            }

        }

        UtilSetFont.setFontMainScreen(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int cardType = getItemViewType(position);

        switch (cardType) {
            case TYPE_EMPTY: {
                EmptyViewHolders mVHolder = (EmptyViewHolders) holder;
                break;
            }
            case TYPE_ITEM: {
                MediaViewHolders mVHolder = (MediaViewHolders) holder;
                handleMedia(mVHolder, position);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (itemList != null && itemList.size() > 0) {
            size = itemList.size();
        }
        if (size == 0) {
            size = 1;
        }

        return size;
    }


    @Override
    public int getItemViewType(int position) {
        int itemType = 0;
        if (itemList != null && itemList.size() > 0) {
            ContentValues object = itemList.get(position);
            if(object != null){
                itemType = TYPE_ITEM;
            }
        }

        return itemType;
    }

    public ContentValues getItem(int position) {
        ContentValues category = null;
        if(itemList != null) {
            category = itemList.get(position);
        }

        return category;
    }

    private void handleMedia(final MediaViewHolders mVHolder, int position){
        final ContentValues category = getItem(position);
        if(category != null){
            String path = category.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD);
            String thumb = category.getAsString(ChatDBConstants.MESSAGE_THUMB_FIELD);

            if (category.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == Message.XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
                mVHolder.ivPlayButton.setVisibility(View.VISIBLE);

                try{
                    android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(path);
                    if (bitmap != null) {
                        mVHolder.ivMedia.setImageBitmap(bitmap);
                    } else {
                        Bitmap tmpBitMap = createVideoThumbFromByteArray(thumb);
                        mVHolder.ivMedia.setImageBitmap(tmpBitMap);
                        SuperChatApplication.addBitmapToMemoryCache(path, tmpBitMap);
                    }
                } catch(Exception e){

                }
                mVHolder.rlMedia.setOnClickListener(new onVideoClickListener(path));
            }
            else if (category.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == Message.XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                mVHolder.ivMedia.setImageResource(R.drawable.addplay);
                mVHolder.ivMedia.setBackgroundResource(R.color.orange);
                mVHolder.rlMedia.setOnClickListener(new onAudioClickListener(path));
            }
            else {
                setPicForCache(mVHolder.ivMedia, path);
                mVHolder.rlMedia.setOnClickListener(new onImageClickListener(path));
            }

        }
    }

    private void setPicForCache(ImageView view, String cacheIdPath) {
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(cacheIdPath);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else {
            File file1 = new File(cacheIdPath);
            //			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (file1 != null && file1.exists()) {
//				view.setImageURI(Uri.parse(filename));
                setThumbForCache(view, cacheIdPath);
            } else {

            }
        }
    }

    private void setThumbForCache(ImageView imageViewl, String path) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        Bitmap bm = null;
        Bitmap bm1 = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
            if (bm.getWidth() > 300)
                bm = ThumbnailUtils.extractThumbnail(bm, 200, 210);
            bm = rotateImageForCache(path, bm);
            bm1 = Bitmap.createScaledBitmap(bm, 100, 75, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm1);
            SuperChatApplication.addBitmapToMemoryCache(path, bm);
            ChatListAdapter.cacheKeys.add(path);
        } else {

        }
    }


    public static Bitmap rotateImageForCache(String path, Bitmap bm) {
        int orientation = 1;
        try {
            ExifInterface exifJpeg = new ExifInterface(path);
            orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }

        return bm;
    }
    private Bitmap createVideoThumbFromByteArray(String baseData) {
        if (baseData == null)
            return null;
        Bitmap bmp = null;
        byte[] data = Base64.decode(baseData, Base64.DEFAULT);
        if (data != null)
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }

    /**
     * Click Events
     */


    class onImageClickListener implements View.OnClickListener{

        private String path;
        public onImageClickListener(String path){
            this.path = path;
        }

        @Override
        public void onClick(View arg0) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + path), "image/*");
            currActivity.startActivity(intent);
        }
    }

    class onVideoClickListener implements View.OnClickListener{

        private String path;
        public onVideoClickListener(String path){
            this.path = path;
        }
        @Override
        public void onClick(View arg0) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(path), "video/*");
            currActivity.startActivity(intent);
        }

    }

    class onAudioClickListener implements View.OnClickListener{

        private String path;
        public onAudioClickListener(String path){
            this.path = path;
        }

        @Override
        public void onClick(View arg0) {

        }

    }
}
