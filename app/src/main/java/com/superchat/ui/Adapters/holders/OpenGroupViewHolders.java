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

public class OpenGroupViewHolders extends RecyclerView.ViewHolder {

    @BindView(R.id.row_layout)
    public LinearLayout row_layout;
    @BindView(R.id.contact_icon)
    public ImageView contact_icon;
    @BindView(R.id.expandedListItem)
    public TextView expandedListItem;
    @BindView(R.id.invited_by)
    public TextView invited_by;

    Activity currActivity;

    public OpenGroupViewHolders(View itemView, Activity currActivity) {
        super(itemView);
        this.currActivity = currActivity;

        ButterKnife.bind(this, itemView);
        UtilSetFont.setFontMainScreen(itemView);
    }
}