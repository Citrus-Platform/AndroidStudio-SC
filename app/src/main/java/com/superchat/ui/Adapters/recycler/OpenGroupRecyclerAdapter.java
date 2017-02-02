package com.superchat.ui.Adapters.recycler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.superchat.R;
import com.superchat.model.SGroupListObject;
import com.superchat.ui.Adapters.connectors.OpenGroupAdapterConnector;
import com.superchat.ui.Adapters.holders.EmptyViewHolders;
import com.superchat.ui.Adapters.holders.OpenGroupViewHolders;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class OpenGroupRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int TYPE_EMPTY = 0;
    final int TYPE_OPEN_GROUP = 1;

    private ArrayList<SGroupListObject> itemList;
    private Activity currActivity;
    private Context currContext;
    private OpenGroupAdapterConnector openGroupAdapterConnector;

    public OpenGroupRecyclerAdapter(Activity currActivity, Context currContext, OpenGroupAdapterConnector openGroupAdapterConnector, ArrayList<SGroupListObject> itemList) {
        this.itemList = itemList;
        this.currActivity = currActivity;
        this.currContext = currContext;
        this.openGroupAdapterConnector = openGroupAdapterConnector;
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
            case TYPE_OPEN_GROUP: {
                view = getLayoutView(parent, R.layout.list_item);
                viewHolder = new OpenGroupViewHolders(view, currActivity);
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
            case TYPE_OPEN_GROUP: {
                OpenGroupViewHolders mVHolder = (OpenGroupViewHolders) holder;
                handleOpenGroup(mVHolder, position);
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
            SGroupListObject object = itemList.get(position);
            if(object != null){
                itemType = TYPE_OPEN_GROUP;
            }
        }

        return itemType;
    }

    public SGroupListObject getItem(int position) {
        SGroupListObject category = null;
        if(itemList != null) {
            category = itemList.get(position);
        }

        return category;
    }

    private void handleOpenGroup(final OpenGroupViewHolders mVHolder, int position){
        final SGroupListObject category = getItem(position);
        if(category != null){
            String domainName = category.getDomainName();
            String adminName = category.getAdminName();
            String orgName = category.getOrgName();
            String orgUrl = category.getOrgUrl();
            String privacyType = category.getPrivacyType();
            String logoFileId = category.getLogoFileId();
            String domainType = category.getDomainType();
            String createdDate = category.getCreatedDate();
            String domainCount = category.getDomainCount();
            String domainNotify = category.getDomainNotify();
            String domainDisplayName = category.getDomainDisplayName();
            String description = category.getDescription();

            String HeadingToDisplay = "";
            if(domainDisplayName != null){
                HeadingToDisplay = domainDisplayName;
            } else if(domainName != null){
                HeadingToDisplay = domainName;
            }
            mVHolder.expandedListItem.setText(HeadingToDisplay);
            mVHolder.invited_by.setText(adminName);

            setProfilePic(mVHolder.contact_icon, logoFileId);
            /*
            Glide.with(currActivity)
                    .load("https://cdn0.iconfinder.com/data/icons/social-flat-rounded-rects/512/facebook-512.png")
                    .override(100, 100)
                    .bitmapTransform(new CropCircleTransformation(currContext))
                    .fitCenter()
                    .into(mVHolder.contact_icon);*/

            mVHolder.row_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGroupAdapterConnector.getGroupClickedInfo(category);
                }
            });
        }
    }

    private boolean setProfilePic(ImageView picView, String groupPicId) {
//		System.out.println("groupPicId : "+groupPicId);
        String img_path = getThumbPath(groupPicId);
        picView.setImageResource(R.drawable.small_hub_icon);
        if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid"))
            return false;
        if (img_path != null) {
            File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (file1.exists()) {
                picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
                setThumb((ImageView) picView, img_path, groupPicId);
                return true;
            } else {
                if (Build.VERSION.SDK_INT >= 11)
                    new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
                else
                    new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 11)
                new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
            else
                new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);

        }
        return false;
    }

    private String getThumbPath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
            File contentFile = new File(filename);
            if (contentFile != null && contentFile.exists()) {
                return filename;
            }

        }
        return null;
    }

    private void setThumb(ImageView imageViewl, String path, String groupPicId) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
            bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
        } else {
            try {
                imageViewl.setImageURI(Uri.parse(path));
            } catch (Exception e) {

            }
        }
    }


    public static Bitmap rotateImage(String path, Bitmap bm) {
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

}
