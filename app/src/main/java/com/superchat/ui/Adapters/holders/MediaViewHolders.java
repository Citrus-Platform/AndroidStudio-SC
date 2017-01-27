package com.superchat.ui.Adapters.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaViewHolders extends RecyclerView.ViewHolder {

    Activity currActivity;

    @BindView(R.id.rlMedia)
    public RelativeLayout rlMedia;
    @BindView(R.id.ivPlayButton)
    public ImageView ivPlayButton;
    @BindView(R.id.ivMedia)
    public ImageView ivMedia;

    public MediaViewHolders(View itemView, Activity currActivity) {
        super(itemView);
        this.currActivity = currActivity;

        ButterKnife.bind(this, itemView);
        UtilSetFont.setFontMainScreen(itemView);
    }
}