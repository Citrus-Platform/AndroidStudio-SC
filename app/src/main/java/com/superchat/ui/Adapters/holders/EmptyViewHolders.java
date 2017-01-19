package com.superchat.ui.Adapters.holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyViewHolders extends RecyclerView.ViewHolder {

    Activity currActivity;

    @BindView(R.id.tvErrorText)
    public TextView tvErrorText;

    public EmptyViewHolders(View itemView, Activity currActivity) {
        super(itemView);
        this.currActivity = currActivity;
        ButterKnife.bind(this, itemView);

        UtilSetFont.setFontMainScreen(itemView);
    }
}