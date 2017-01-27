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

import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.superchat.R;
import com.superchat.ui.Adapters.decorators.DividerItemDecoration;
import com.superchat.ui.Adapters.recycler.MediaRecyclerAdapter;
import com.superchat.utils.Log;
import com.superchat.utils.UtilSetFont;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMediaFragment extends Fragment {
    public static final String TAG = "GroupMediaFragment";

    private static final String KEY_groupUUID = "groupUUID";

    public static Fragment getInstance(final Context context, String groupUUID){

        Bundle bundle = new Bundle();
        bundle.putString(KEY_groupUUID, groupUUID);

        GroupMediaFragment instance = new GroupMediaFragment();
        instance.setArguments(bundle);
        return instance;
    }

    @BindView(R.id.tvEmptyMessage)
    TextView tvEmptyMessage;
    @BindView(R.id.rvGroupMedia)
    RecyclerView rvGroupMedia;

    View view;
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
            allItems = ChatDBWrapper.getInstance().getAllPersonMedia(groupUUID);
        }

        if(allItems != null && allItems.size() > 0){
            tvEmptyMessage.setVisibility(View.GONE);
            setUpMediaAdapter();
        } else {
            tvEmptyMessage.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<ContentValues> filterMedia(ArrayList<ContentValues> allMedia){
        allItems = new ArrayList<>();
        for (ContentValues value : allMedia) {
            if (value.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == Message.XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
                allItems.add(value);
            else if (value.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == Message.XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
                allItems.add(value);
            else
                allItems.add(value);
        }
        return allItems;
    }

    MediaRecyclerAdapter adapter;
    GridLayoutManager layoutManager;
    private void setUpMediaAdapter() {
        adapter = new MediaRecyclerAdapter(getActivity(), allItems);
        layoutManager = new GridLayoutManager(getActivity(), 4);
        rvGroupMedia.setLayoutManager(layoutManager);
        rvGroupMedia.setAdapter(adapter);
        rvGroupMedia.setNestedScrollingEnabled(true);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvGroupMedia.getContext(),
                layoutManager.getOrientation());
        rvGroupMedia.addItemDecoration(mDividerItemDecoration);

    }
}