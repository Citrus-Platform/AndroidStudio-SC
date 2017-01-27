package com.superchat.ui.Adapters.recycler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import com.chat.sdk.db.ChatDBConstants;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.superchat.R;
import com.superchat.model.SGroupListObject;
import com.superchat.ui.Adapters.connectors.OpenGroupAdapterConnector;
import com.superchat.ui.Adapters.holders.EmptyViewHolders;
import com.superchat.ui.Adapters.holders.DocViewHolders;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.superchat.R.id.imageView;


public class DocsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int TYPE_EMPTY = 0;
    final int TYPE_ITEM = 1;

    private ArrayList<ContentValues> itemList;
    private Activity currActivity;
    private Context currContext;

    public DocsRecyclerAdapter(Activity currActivity, ArrayList<ContentValues> itemList) {
        this.itemList = itemList;
        this.currActivity = currActivity;
        this.currContext = currActivity;
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
                view = getLayoutView(parent, R.layout.list_doc_item);
                viewHolder = new DocViewHolders(view, currActivity);
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
                DocViewHolders mVHolder = (DocViewHolders) holder;
                handleDoc(mVHolder, position);
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

    private void handleDoc(final DocViewHolders mVHolder, int position){
        try {
            final ContentValues category = getItem(position);
            if (category != null) {
                String path = category.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD);
                String thumb = category.getAsString(ChatDBConstants.MESSAGE_THUMB_FIELD);
                int fileType = category.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD);

                mVHolder.ivDoc.setOnClickListener(new onDocClickListener(path));

                if (fileType == Message.XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
                    mVHolder.ivDoc.setImageResource(R.drawable.docs);
                else if (fileType == Message.XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
                    mVHolder.ivDoc.setImageResource(R.drawable.pdf);
                else if (fileType == Message.XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
                    mVHolder.ivDoc.setImageResource(R.drawable.ppt);
                else if (fileType == Message.XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
                    mVHolder.ivDoc.setImageResource(R.drawable.xls);

                int index = path.lastIndexOf("/");
                mVHolder.tvDoc.setText(path.substring(index + 1, index + 9));
            }
        } catch(Exception e){

        }
    }


    class onDocClickListener implements View.OnClickListener{

        private String path;
        public onDocClickListener(String path){
            this.path = path;
        }

        @Override
        public void onClick(View arg0) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String mediaLocalPath = path.toString();
                if (mediaLocalPath.contains(".pdf")) {
                    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/pdf");
                } else if (mediaLocalPath.contains(".doc")) {
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/msword");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else if (mediaLocalPath.contains(".xls")) {
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else if (mediaLocalPath.contains(".ppt")) {
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-powerpoint");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                currActivity.startActivity(intent);
            } catch(Exception e){

            }
        }

    };
}
