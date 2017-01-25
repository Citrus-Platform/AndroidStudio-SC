package com.superchat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

//import android.app.ListFragment;

public class GroupDocFragment extends Fragment {
    public static final String TAG = "GroupMediaFragment";

    public View onCreateView(LayoutInflater layoutinflater,
                             ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.fragment_group_media, null);

        UtilSetFont.setFontMainScreen(view);

        return view;
    }
}