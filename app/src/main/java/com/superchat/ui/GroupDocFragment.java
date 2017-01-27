package com.superchat.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.sdk.db.ChatDBWrapper;
import com.superchat.R;
import com.superchat.ui.Adapters.decorators.DividerItemDecoration;
import com.superchat.ui.Adapters.recycler.DocsRecyclerAdapter;
import com.superchat.utils.Log;
import com.superchat.utils.UtilSetFont;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//import android.app.ListFragment;

public class GroupDocFragment extends Fragment {
    public static final String TAG = "GroupDocFragment";

    private static final String KEY_groupUUID = "groupUUID";

    public static Fragment getInstance(final Context context, String groupUUID){

        Bundle bundle = new Bundle();
        bundle.putString(KEY_groupUUID, groupUUID);

        GroupDocFragment instance = new GroupDocFragment();
        instance.setArguments(bundle);
        return instance;
    }

    @BindView(R.id.tvEmptyMessage)
    TextView tvEmptyMessage;
    @BindView(R.id.rvGroupMedia)
    RecyclerView rvGroupMedia;

    public View onCreateView(LayoutInflater layoutinflater,
                             ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.fragment_group_media, null);

        ButterKnife.bind(this, view);

        UtilSetFont.setFontMainScreen(view);

        init();
        return view;
    }

    Bundle bundle;
    String groupUUID;
    ArrayList<ContentValues> allItems;
    private void init(){

        bundle = getArguments();
        if(bundle != null){
            groupUUID = bundle.getString(KEY_groupUUID);
        }

        if(groupUUID != null && groupUUID.length() > 0) {
            allItems = ChatDBWrapper.getInstance().getAllPersonDocs(groupUUID);
        }

        if(allItems != null && allItems.size() > 0){
            tvEmptyMessage.setVisibility(View.GONE);
            setUpMediaAdapter();
        } else {
            tvEmptyMessage.setVisibility(View.VISIBLE);
        }
    }

    DocsRecyclerAdapter adapter;
    GridLayoutManager layoutManager;
    private void setUpMediaAdapter() {
        adapter = new DocsRecyclerAdapter(getActivity(), allItems);
        layoutManager = new GridLayoutManager(getActivity(), 4);
        rvGroupMedia.setLayoutManager(layoutManager);
        rvGroupMedia.setAdapter(adapter);
        rvGroupMedia.setNestedScrollingEnabled(true);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvGroupMedia.getContext(),
                layoutManager.getOrientation());
        rvGroupMedia.addItemDecoration(mDividerItemDecoration);

    }
}