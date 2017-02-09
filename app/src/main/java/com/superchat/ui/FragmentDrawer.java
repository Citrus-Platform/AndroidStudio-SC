package com.superchat.ui;

/**
 * Created by citrus on 9/15/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.DrawerUpdated;
import com.superchat.model.multiplesg.InviteJoinDataModel;
import com.superchat.model.multiplesg.InvitedDomainNameSet;
import com.superchat.model.multiplesg.JoinedDomainNameSet;
import com.superchat.model.multiplesg.OwnerDomainName;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.superchat.ui.OpenHubSearchScreen.KEY_IS_COMING_FROM_LOGIN_FLOW;

public class FragmentDrawer extends Fragment implements View.OnClickListener, ConnectorDrawer {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    //////////////////////////////////
    public TextView currentSGName;
    public ImageView displayPictureCurrent;
    public ImageView notifyCurrent;
    public TextView user;

    public TextView invitedNotificationCount;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public ExpandableListDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

    LinearLayout userContainer;
    LinearLayout llInvited;
    LinearLayout llAddSuperGroup, superGroupContainer;
    RelativeLayout notificationLayout;
    boolean flagNotify = false;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    private String getPluralSingularText(List list, String title){
        if(list != null && list.size() > 1){
            title = title + "s";
        }
        return title;
    }

    ArrayList<String> invitedDomainNameSet = new ArrayList<String>();

    public List<ExpandableListDrawerAdapter.Item> getSuperGroupList() {
        List<ExpandableListDrawerAdapter.Item> parentDataList = new ArrayList<>();
        ArrayList<OwnerDomainName> ownerDomainNameSet = DBWrapper.getInstance().getOwnedSGList();
        if (ownerDomainNameSet != null && ownerDomainNameSet.size() > 0) {
            List<ExpandableListDrawerAdapter.Item> childDataList = new ArrayList<>();

            for (int i = 0; i < ownerDomainNameSet.size(); i++) {
                String ownerDisplayName = "";
                if (ownerDomainNameSet.get(i).getDomainDisplayName() != null &&
                        ownerDomainNameSet.get(i).getDomainDisplayName().trim().length() > 0) {
                    ownerDisplayName = ownerDomainNameSet.get(i).getDomainDisplayName().trim();
                } else {
                    if (ownerDomainNameSet.get(i).getDomainName() != null)
                        ownerDisplayName = ownerDomainNameSet.get(i).getDomainName().trim();
                }

                childDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.CHILD,
                        ownerDisplayName,
                        ownerDomainNameSet.get(i).getDomainCount(),
                        ownerDomainNameSet.get(i).getDomainNotify(),
                        ownerDomainNameSet.get(i).getDomainName(),
                        ownerDomainNameSet.get(i).getDomainType()));
            }

            parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER,
                    getPluralSingularText(childDataList, HEADER_OWNED_HUB), childDataList));
        }

        ArrayList<JoinedDomainNameSet> ownerDomainNameSetTemp = DBWrapper.getInstance().getListOfOwnerArraySGs();
        if (ownerDomainNameSetTemp != null && ownerDomainNameSetTemp.size() > 0) {
            List<ExpandableListDrawerAdapter.Item> childDataList = new ArrayList<>();

            Collections.sort(ownerDomainNameSetTemp, new Comparator<JoinedDomainNameSet>() {
                @Override
                public int compare(final JoinedDomainNameSet object1, final JoinedDomainNameSet object2) {

                    String itemOne = "";
                    String itemTwo = "";
                    //////////////////////////
                    if (object1.getDomainDisplayName() != null && object1.getDomainDisplayName().trim().length() > 0) {
                        itemOne = object1.getDomainDisplayName().toUpperCase();
                    } else {
                        itemOne = object1.getDomainName().toUpperCase();
                    }
                    //////////////////////////
                    if (object2.getDomainDisplayName() != null && object2.getDomainDisplayName().trim().length() > 0) {
                        itemTwo = object2.getDomainDisplayName().toUpperCase();
                    } else {
                        itemTwo = object2.getDomainName().toUpperCase();
                    }
                    return itemOne.compareTo(itemTwo);
                }
            });
            for (int i = 0; i < ownerDomainNameSetTemp.size(); i++) {
                String joinedDisplayName = "";
                if (ownerDomainNameSetTemp.get(i).getDomainDisplayName() != null &&
                        ownerDomainNameSetTemp.get(i).getDomainDisplayName().trim().length() > 0) {
                    joinedDisplayName = ownerDomainNameSetTemp.get(i).getDomainDisplayName().trim();
                } else {
                    joinedDisplayName = ownerDomainNameSetTemp.get(i).getDomainName().trim();
                }

                childDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.CHILD,
                        joinedDisplayName,
                        ownerDomainNameSetTemp.get(i).getDomainCount(),
                        ownerDomainNameSetTemp.get(i).getDomainNotify(),
                        ownerDomainNameSetTemp.get(i).getDomainName(),
                        ownerDomainNameSetTemp.get(i).getDomainType()));

//                Log.e("disp" , "is : "+joinedDisplayName);
            }


            parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER,
                    getPluralSingularText(childDataList, HEADER_OWNED_HUB), childDataList));
        }

        ArrayList<JoinedDomainNameSet> joinedDomainNameSetTemp = DBWrapper.getInstance().getListOfJoinedSGs();
        if (joinedDomainNameSetTemp != null && joinedDomainNameSetTemp.size() > 0) {
            List<ExpandableListDrawerAdapter.Item> childDataList = new ArrayList<>();
            Collections.sort(joinedDomainNameSetTemp, new Comparator<JoinedDomainNameSet>() {
                @Override
                public int compare(final JoinedDomainNameSet object1, final JoinedDomainNameSet object2) {

                    String itemOne = "";
                    String itemTwo = "";
                    //////////////////////////
                    if (object1.getDomainDisplayName() != null && object1.getDomainDisplayName().trim().length() > 0) {
                        itemOne = object1.getDomainDisplayName().toUpperCase();
                    } else {
                        itemOne = object1.getDomainName().toUpperCase();
                    }
                    //////////////////////////
                    if (object2.getDomainDisplayName() != null && object2.getDomainDisplayName().trim().length() > 0) {
                        itemTwo = object2.getDomainDisplayName().toUpperCase();
                    } else {
                        itemTwo = object2.getDomainName().toUpperCase();
                    }
                    return itemOne.compareTo(itemTwo);
                }
            });
            for (int i = 0; i < joinedDomainNameSetTemp.size(); i++) {
                String joinedDisplayName = "";
                if (joinedDomainNameSetTemp.get(i).getDomainDisplayName() != null &&
                        joinedDomainNameSetTemp.get(i).getDomainDisplayName().trim().length() > 0) {
                    joinedDisplayName = joinedDomainNameSetTemp.get(i).getDomainDisplayName().trim();
                } else {
                    joinedDisplayName = joinedDomainNameSetTemp.get(i).getDomainName().trim();
                }
                childDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.CHILD,
                        joinedDisplayName,
                        joinedDomainNameSetTemp.get(i).getDomainCount(),
                        joinedDomainNameSetTemp.get(i).getDomainNotify(),
                        joinedDomainNameSetTemp.get(i).getDomainName(),
                        joinedDomainNameSetTemp.get(i).getDomainType()));

//                Log.e("disp" , "is : "+joinedDisplayName);
            }

            parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER,
                    getPluralSingularText(childDataList, HEADER_JOINED_HUB), childDataList));
        }

        parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER, HEADER_OPEN_HUB, null));
        parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER, HEADER_NEW_INVITATION, null));
        parentDataList.add(new ExpandableListDrawerAdapter.Item(ExpandableListDrawerAdapter.HEADER, HEADER_CREATE_NEW_HUB, null));

        return parentDataList;
    }

    public static final String HEADER_OPEN_HUB = "Open Hubs";
    public static final String HEADER_NEW_INVITATION = "New Invitation";
    public static final String HEADER_CREATE_NEW_HUB = "Create New Hub";
    public static final String HEADER_OWNED_HUB = "Owned Hub";
    public static final String HEADER_JOINED_HUB = "Joined Hub";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<InvitedDomainNameSet> invitedList = new ArrayList<>();
        invitedList = DBWrapper.getInstance().getListOfInvitedSGs();

        if (invitedList != null && invitedList.size() > 0) {
            for (int i = 0; i < invitedList.size(); i++) {
                invitedDomainNameSet.add(invitedList.get(i).getDomainName());
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        UtilSetFont.setFontMainScreen(layout);

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        ArrayList<InvitedDomainNameSet> invite = DBWrapper.getInstance().getListOfInvitedSGs();

        //current SG data//////////////////////////////////////////////
        currentSGName = (TextView) layout.findViewById(R.id.currentSGName);
        displayPictureCurrent = (ImageView) layout.findViewById(R.id.displayPictureCurrent);
        notifyCurrent = (ImageView) layout.findViewById(R.id.notifyCurrent);
        notifyCurrent.setOnClickListener(this);
        user = (TextView) layout.findViewById(R.id.user);

        invitedNotificationCount = (TextView) layout.findViewById(R.id.invitedNotificationCount);
        if (invite != null && invite.size() > 0)
            invitedNotificationCount.setText("" + invite.size());
        else
            invitedNotificationCount.setText("0");

        SharedPrefManager pref = SharedPrefManager.getInstance();
        String file_id = pref.getSGFileId("SG_FILE_ID");

//        String file_id = DBWrapper.getInstance().getSGLogoFileID(SharedPrefManager.getInstance().getUserDomain());
        currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        setProfilePic(displayPictureCurrent);

        String userDisplayName = SharedPrefManager.getInstance().getDisplayName();
        user.setText("" + userDisplayName);
//        user.setText("" + SharedPrefManager.getInstance().getDisplayName() + " (You)");

        ///////////////////////////////////////////////

        superGroupContainer = (LinearLayout) layout.findViewById(R.id.superGroupContainer);
        llAddSuperGroup = (LinearLayout) layout.findViewById(R.id.llAddSuperGroup);
        userContainer = (LinearLayout) layout.findViewById(R.id.userContainer);
        llInvited = (LinearLayout) layout.findViewById(R.id.llInvited);
        notificationLayout = (RelativeLayout) layout.findViewById(R.id.notificationLayout);

        superGroupContainer.setOnClickListener(this);
        llAddSuperGroup.setOnClickListener(this);
        llInvited.setOnClickListener(this);
        userContainer.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);
        displayPictureCurrent.setOnClickListener(this);
        currentSGName.setOnClickListener(this);

        refreshList();
        createAdapter();

        return layout;
    }

    private void createAdapter(){
        adapter = new ExpandableListDrawerAdapter(listItems, getActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    List<ExpandableListDrawerAdapter.Item> listItems;
    private void refreshList(){
        listItems = getSuperGroupList();
    }

    public void refreshListAndNotify(){
        refreshList();
        createAdapter();
        //adapter.notifyDataSetChanged();
    }
    public void updateView() {
        String file_id = SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID");
        currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        setProfilePic(displayPictureCurrent);

        String userDisplayName = SharedPrefManager.getInstance().getDisplayName();
        user.setText("" + userDisplayName);

        super.onResume();
    }

    public static final int CODE_INVITE = 101;
    public static final int CODE_ADD_SUPERGROUP = 102;
    public static final int CODE_OPEN_SUPERGROUP = 103;

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.displayPictureCurrent:
            case R.id.currentSGName:
                Intent intent = new Intent(getActivity(), SuperGroupProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.notificationLayout:
            case R.id.notifyCurrent: {
                showSnoozeDialog();
                break;
            }
            case R.id.llInvited: {
                eventClickedInvitaion();
                break;
            }
            case R.id.userContainer:
                intent = new Intent(getActivity(), ProfileScreen.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CHAT_USER_NAME, SharedPrefManager.getInstance().getUserName());
                bundle.putString(Constants.CHAT_NAME, SharedPrefManager.getInstance().getDisplayName());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.superGroupContainer:
            case R.id.llAddSuperGroup: {
                eventClickedCreateNewHub();
                break;
            }
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_INVITE: {
                handleHub(data);
                break;
            }
            case CODE_ADD_SUPERGROUP: {
                try {
                    SharedPrefManager pref = SharedPrefManager.getInstance();
                    String SG_NAME = pref.getUserDomain();
                    String user = pref.getUserPhone();
                    if (user != null && user.contains("-"))
                        user = user.replace("-", "");
                    pref.saveSGUserID(pref.getUserName(), pref.getUserId());
                    pref.saveSGPassword(pref.getUserName(), pref.getUserPassword());

                    OwnerDomainName owned = new OwnerDomainName();
                    owned.setDomainName(SG_NAME);
                    owned.setDisplayName(pref.getCurrentSGDisplayName());
                    owned.setUnreadCounter(0);
                    owned.setCreatedDate("");
                    owned.setDomainType("Company");
                    owned.setDomainMuteInfo(0);
                    owned.setOrgName("");
                    owned.setOrgUrl("");
                    owned.setPrivacyType("Open");
                    owned.setAdminName(pref.getUserName());

                    String domainName = SharedPrefManager.getInstance().getUserDomain();
                    String logoFileId = pref.getSGFileId(domainName);
                    owned.setLogoFileId(logoFileId);

                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                    String dateString = formatter.format(new Date(System.currentTimeMillis()));
                    owned.setCreatedDate(dateString);

                    ArrayList<OwnerDomainName> ownerDomainNameSet = new ArrayList<>();
                    ownerDomainNameSet.add(owned);

                    if ((ownerDomainNameSet != null && ownerDomainNameSet.size() > 0)) {
                        DBWrapper.getInstance().updateOwnedSGDataArray(ownerDomainNameSet);
                    }
                    DBWrapper.getInstance().updateSGCredentials(SG_NAME, pref.getUserName(), pref.getUserPassword(), pref.getUserId(), true);
                    ((HomeScreen) getActivity()).switchSG(user + "_" + SG_NAME, false, null, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

        }
    }

    private void handleHub(Intent data){
        try {
            if (data != null) {
                InviteJoinDataModel model = new InviteJoinDataModel();
                String SG_NAME = data.getStringExtra("SG_NAME");
                model.setInviteMobileNumber(data.getStringExtra("SG_MOBILE"));
                model.setInviteSGName(data.getStringExtra("SG_NAME"));
                model.setInviteSGDisplayName(data.getStringExtra("SG_DISPLAY_NAME"));
                model.setInviteSGFileID(data.getStringExtra("SG_FILE_ID"));
                model.setInviteUserName(data.getStringExtra("SG_USER_NAME"));
                model.setInviteUserID(data.getLongExtra("SG_USER_ID", -1));
                model.setInviteUserPassword(data.getStringExtra("SG_USER_PASSWORD"));
                String user = SharedPrefManager.getInstance().getUserPhone();
                if (user != null && user.contains("-"))
                    user = user.replace("-", "");

                SharedPrefManager.getInstance().saveSGUserID(model.getInviteUserName(), model.getInviteUserID());
                SharedPrefManager.getInstance().saveSGPassword(model.getInviteUserName(), model.getInviteUserPassword());

                int type = DBWrapper.getInstance().getSGUserTypeValue(SG_NAME);
                if (type == -1) {//Not Found
                    JoinedDomainNameSet joined = new JoinedDomainNameSet();
                    joined.setDomainName(SG_NAME);
                    joined.setDisplayName(model.getInviteSGDisplayName());
                    joined.setUnreadCounter(0);
                    joined.setDomainType("Company");
                    joined.setDomainMuteInfo(0);
                    joined.setOrgName("");
                    joined.setOrgUrl("");
                    joined.setPrivacyType("Open");
                    joined.setAdminName(SharedPrefManager.getInstance().getUserName());
                    joined.setLogoFileId(model.getInviteSGFileID());

                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                    String dateString = formatter.format(new Date(System.currentTimeMillis()));
                    joined.setCreatedDate(dateString);

                    if (joined != null)
                        DBWrapper.getInstance().addNewJoinedSGData(joined);

                    DBWrapper.getInstance().updateSGCredentials(SG_NAME, model.getInviteUserName(), model.getInviteUserPassword(), model.getInviteUserID(), true);
                } else {
                    DBWrapper.getInstance().updateSGCredentials(SG_NAME, model.getInviteUserName(), model.getInviteUserPassword(), model.getInviteUserID(), true);
                    DBWrapper.getInstance().updateSGTypeValue(SG_NAME, 2);
                }
                ((HomeScreen) getActivity()).switchSG(user + "_" + SG_NAME, true, model, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eventClickedCreateNewHub() {
        SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
        String mobileNumber = iPrefManager.getUserPhone();

//        Intent intent = new Intent(getActivity(), ProfileScreen.class);
//        intent.putExtra(Constants.SG_CREATE_AFTER_LOGIN, true);
//        intent.putExtra(Constants.SG_CREATE_RESET, true);
//        intent.putExtra("MANAGE_MEMBER_BY_ADMIN", true);
//        intent.putExtra("PROFILE_EDIT_REG_FLOW", true);
//        intent.putExtra(Constants.REG_TYPE, true);
//        intent.putExtra("PROFILE_FIRST", true);
//        if (mobileNumber.indexOf('-') != -1)
//            intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
//        else
//            intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
//        startActivityForResult(intent, CODE_ADD_SUPERGROUP);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        if (mobileNumber.indexOf('-') != -1)
            intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
        else
            intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
        intent.putExtra(Constants.REG_TYPE, "ADMIN");
        intent.putExtra("REGISTER_SG", true);
        intent.putExtra(Constants.SG_CREATE_AFTER_LOGIN, true);
        startActivityForResult(intent, CODE_ADD_SUPERGROUP);
    }

    @Override
    public void eventClickedInvitaion() {
        SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
        String number = iPrefManager.getUserPhone();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER_TXT, number);
        bundle.putStringArrayList(SupergroupListingScreenNew.KEY_INVITED_DOMAIN_SET, invitedDomainNameSet);
        bundle.putBoolean(SupergroupListingScreenNew.KEY_SHOW_OWNED_ALERT, false);

        Intent starter = new Intent(getActivity(), SupergroupListingScreenNew.class);
        starter.putExtras(bundle);
        startActivityForResult(starter, CODE_INVITE);
    }

    @Override
    public void eventClickedOpenHub() {
        SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
        String number = iPrefManager.getUserPhone();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER_TXT, number);
        bundle.putStringArrayList(SupergroupListingScreenNew.KEY_INVITED_DOMAIN_SET, invitedDomainNameSet);
        bundle.putBoolean(SupergroupListingScreenNew.KEY_SHOW_OWNED_ALERT, false);
        bundle.putBoolean(KEY_IS_COMING_FROM_LOGIN_FLOW, false);

        Intent starter = new Intent(getActivity(), OpenHubSearchScreen.class);
        starter.putExtras(bundle);
        startActivityForResult(starter, CODE_INVITE);
        //OpenHubSearchScreen.start(getActivity());
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

    public void initialize() {
        // Comment this
        {
            adapter.notifyDataSetChanged();
            {/*
                adapter = new ExpandableListDrawerAdapter(getSuperGroupList(), getActivity(), this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
            */}
        }
        /////////////////////////////////////
        boolean muteId = SharedPrefManager.getInstance().isSnoozeExpired(SharedPrefManager.getInstance().getUserDomain());
        if (muteId) {
            //flagNotify = false;
            notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_mute);
        } else {
            //flagNotify = true;
            notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_unmute);
        }
        /////////////////////////////////////
        ArrayList<InvitedDomainNameSet> invitedList = new ArrayList<>();
        invitedList = DBWrapper.getInstance().getListOfInvitedSGs();
        if (invitedList != null && invitedList.size() > 0)
            invitedNotificationCount.setText("" + invitedList.size());
        else
            invitedNotificationCount.setText("0");

        if (currentSGName != null)
            currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());

        setProfilePic(displayPictureCurrent);
    }

    private boolean isFirstTimeDrawerOpen = true;
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(isFirstTimeDrawerOpen) {
                    //adapter.openAllHeaders();
                    isFirstTimeDrawerOpen = false;
                }

                adapter.openHeader(ExpandableListDrawerAdapter.DRAWER_POSITION);

                initialize();
                refreshDrawerAdapter();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(containerView.getWindowToken(), 0);


                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(SharedPrefManager.getInstance().getSelectedIndexNav(), 0);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                adapter.notifyDataSetChanged();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(SharedPrefManager.getInstance().getSelectedIndexNav(), 0);
                getActivity().invalidateOptionsMenu();
                if (HomeScreen.userDeactivated) {
                    ((HomeScreen) getActivity()).switchSG(SharedPrefManager.getInstance().getUserDomain(), false, null, false);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // toolbar.setAlpha(1 - slideOffset / 2);
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

    public void fragmentClose() {
        mDrawerLayout.closeDrawer(containerView);
    }

    public void fragmentOpen() {
        /*if (HomeScreen.updateNavDrawer) {
            HomeScreen.updateNavDrawer = false;
            adapter = new ExpandableListDrawerAdapter(getSuperGroupList(), getActivity(), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
        }*/
        updateView();
        mDrawerLayout.openDrawer(containerView);
    }

    private boolean setProfilePic(ImageView picView) {

        String groupPicId = DBWrapper.getInstance().getSGLogoFileID(SharedPrefManager.getInstance().getUserDomain());
        SharedPrefManager pref = SharedPrefManager.getInstance();
        if (groupPicId == null) {
            String domainName = pref.getUserDomain();
            groupPicId = pref.getSGFileId(domainName);
        }

//		System.out.println("groupPicId : "+groupPicId);
        String img_path = getThumbPath(groupPicId);
        picView.setImageResource(R.drawable.small_hub_icon);

        if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid")) {
            return false;
        }

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

    public void showSnoozeDialog() {
        final Dialog bteldialog = new Dialog(getActivity());
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.snooze_settings);
        TextView btn = ((TextView) bteldialog.findViewById(R.id.id_set_btn));
        final Spinner spinner = (Spinner) bteldialog.findViewById(R.id.spinner);

        final SharedPrefManager sharedPrefManager;
        sharedPrefManager = SharedPrefManager.getInstance();

        sharedPrefManager.isSnoozeExpired(sharedPrefManager.getUserDomain());
        spinner.setSelection(sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain()));
        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                sharedPrefManager.setSnoozeIndex(sharedPrefManager.getUserDomain(), spinner.getSelectedItemPosition());
                sharedPrefManager.setSnoozeStartTime(sharedPrefManager.getUserDomain(), System.currentTimeMillis());
                //notifyCurrent.setImageResource(R.drawable.ic_icon_navigation_unmute);
                Toast.makeText(getActivity(), String.valueOf(spinner.getSelectedItem()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        TextView cancelBtn = ((TextView) bteldialog.findViewById(R.id.id_cancel));
        cancelBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        if (!bteldialog.isShowing())
            bteldialog.show();
    }

    private void refreshDrawerAdapter(){
        if(isDrawerToRefreshForcefully){
            isDrawerToRefreshForcefully = !isDrawerToRefreshForcefully;
            refreshListAndNotify();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDrawerAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void getIntentData(Intent data){
        System.out.print("");
        handleHub(data);
    }

    boolean isDrawerToRefreshForcefully = false;
    @Subscribe
    public void getUpdatedDrawer(DrawerUpdated drawerUpdated){
        if(drawerUpdated != null){
            isDrawerToRefreshForcefully = drawerUpdated.isDrawerUpdated();
        }
    }
}