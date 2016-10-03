package com.superchat.ui;

/**
 * Created by citrus on 9/15/2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.superchat.R;
import com.superchat.model.SGroupListObject;
import com.superchat.model.SlidingMenuData;
import com.superchat.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentDrawer extends Fragment implements View.OnClickListener{

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

    LinearLayout llInvited;
    RelativeLayout notificationLayout;
    ImageView notification;
    ImageView notificationoff;
    boolean flagNotify = false;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    ArrayList<String> invitedDomainNameSet = new ArrayList<String>();
    public List<ExpandableListAdapter.Item> getSuperGroupList() {
        List<ExpandableListAdapter.Item> dataList = new ArrayList<>();

        //////////////////////////////////////////
        JSONObject json;
        SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
        String data = iPrefManager.getSgListData();
        ////////////////////////////////////////
        ArrayList<SGroupListObject> ownerDomainNameSet = new ArrayList<>();
        SGroupListObject sGroupListObject = new SGroupListObject();
        try {
            json = new JSONObject(data);
            //Get owner List
            if (json != null && json.has("ownerDomainName")) {
                // ownerDomainNameSet.add(json.getString("ownerDomainName"));
                sGroupListObject.setDomainName(json.getJSONObject("ownerDomainName").get("domainName").toString());
                sGroupListObject.setAdminName(json.getJSONObject("ownerDomainName").get("adminName").toString());
                sGroupListObject.setOrgName(json.getJSONObject("ownerDomainName").get("orgName").toString());
                sGroupListObject.setPrivacyType(json.getJSONObject("ownerDomainName").get("privacyType").toString());
                sGroupListObject.setDomainType(json.getJSONObject("ownerDomainName").get("domainType").toString());
                sGroupListObject.setCreatedDate(json.getJSONObject("ownerDomainName").get("createdDate").toString());

                ownerDomainNameSet.add(sGroupListObject);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //////////////////////////////////////////
        SlidingMenuData gsonObject = new Gson().fromJson(data, SlidingMenuData.class);

        ArrayList<SGroupListObject> joinedDomainNameSetTemp = new ArrayList<>();
        joinedDomainNameSetTemp = gsonObject.getJoinedDomainNameSet();

        ArrayList<SGroupListObject> invitedDomainNameSetTemp = new ArrayList<>();
        invitedDomainNameSetTemp = gsonObject.getInvitedDomainNameSet();

        dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Super Group", "0", "0"));

        /**
         * Get Invited List Data in String Array
         */
        {
            try {
                JSONObject jsonTemp = new JSONObject(data);
                //invitedDomainNameSet
                JSONArray array = jsonTemp.getJSONArray("invitedDomainNameSet");
                invitedDomainNameSet = new ArrayList<String>();
                for (int i = 0; i < array.length(); i++) {
                    invitedDomainNameSet.add(array.getString(i));
                }
                ;
            } catch(Exception e){

            }
        }
        if (ownerDomainNameSet != null && ownerDomainNameSet.size() > 0) {

            for (int i = 0; i < ownerDomainNameSet.size(); i++) {

                dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD,
                        ownerDomainNameSet.get(i).getDomainName(),
                        ownerDomainNameSet.get(i).getDomainCount(),
                        ownerDomainNameSet.get(i).getDomainNotify()));
            }
        }
        ///////////////////////////////////////////////
        if (joinedDomainNameSetTemp != null && joinedDomainNameSetTemp.size() > 0) {

            for (int i = 0; i < joinedDomainNameSetTemp.size(); i++) {

                dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD,
                        joinedDomainNameSetTemp.get(i).getDomainName(),
                        joinedDomainNameSetTemp.get(i).getDomainCount(),
                        joinedDomainNameSetTemp.get(i).getDomainNotify()));
            }
        }
        ///////////////////////////////////////////////
        dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Invited Group", "0", "0"));

        if (invitedDomainNameSetTemp != null && invitedDomainNameSetTemp.size() > 0) {
            for (int i = 0; i < invitedDomainNameSetTemp.size(); i++) {
                dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD,
                        invitedDomainNameSetTemp.get(i).getDomainName(),
                        invitedDomainNameSetTemp.get(i).getDomainCount(),
                        invitedDomainNameSetTemp.get(i).getDomainNotify()));
            }
        }

        return dataList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        llInvited = (LinearLayout) layout.findViewById(R.id.llInvited);
        notification = (ImageView) layout.findViewById(R.id.notification);
        notificationoff = (ImageView) layout.findViewById(R.id.notificationoff);
        notificationLayout = (RelativeLayout) layout.findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagNotify == true) {
                    flagNotify = false;
                    notification.setVisibility(View.VISIBLE);
                    notificationoff.setVisibility(View.GONE);
                } else {
                    flagNotify = true;
                    notification.setVisibility(View.GONE);
                    notificationoff.setVisibility(View.VISIBLE);
                }
            }
        });

        llInvited.setOnClickListener(this);
        ////////////////////////////////////////////////////////////
        adapter = new ExpandableListAdapter(getSuperGroupList(), getActivity());
        ////////////////////////////////////////////////////////////

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch(viewID){
            case R.id.llInvited:{
                if(invitedDomainNameSet != null && invitedDomainNameSet.size() > 0) {
                    SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                    String number = iPrefManager.getUserPhone();
                    SupergroupListingScreenNew.start(getActivity(), number, invitedDomainNameSet, false);
                } else {
                    Toast.makeText(getActivity(), "Invited List is empty", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}