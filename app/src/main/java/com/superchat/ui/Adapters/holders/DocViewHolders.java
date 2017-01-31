package com.superchat.ui.Adapters.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocViewHolders extends RecyclerView.ViewHolder {

    Activity currActivity;

    @BindView(R.id.llDoc)
    public LinearLayout llDoc;
    @BindView(R.id.ivDoc)
    public ImageView ivDoc;
    @BindView(R.id.tvDoc)
    public TextView tvDoc;

    public DocViewHolders(View itemView, Activity currActivity) {
        super(itemView);
        this.currActivity = currActivity;

        ButterKnife.bind(this, itemView);
        UtilSetFont.setFontMainScreen(itemView);
    }
}