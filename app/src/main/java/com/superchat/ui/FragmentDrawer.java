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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.zzip;
import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.SGroupListObject;
import com.superchat.model.multiplesg.InviteJoinDataModel;
import com.superchat.model.multiplesg.InvitedDomainNameSet;
import com.superchat.model.multiplesg.JoinedDomainNameSet;
import com.superchat.model.multiplesg.OwnerDomainName;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragmentDrawer extends Fragment implements View.OnClickListener {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    //////////////////////////////////
    public TextView currentSGName;
    public ImageView displayPictureCurrent;
    public ImageView notifyCurrent;
    public TextView user;
    public TextView addSGTextView;

    public TextView invitedNotificationCount;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public ExpandableListAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

    LinearLayout userContainer;
    LinearLayout llInvited;
    LinearLayout llAddSuperGroup;
    RelativeLayout notificationLayout;
    boolean flagNotify = false;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    ArrayList<String> invitedDomainNameSet = new ArrayList<String>();

    public List<ExpandableListAdapter.Item> getSuperGroupList() {
        List<ExpandableListAdapter.Item> dataList = new ArrayList<>();
        OwnerDomainName owned = DBWrapper.getInstance().getOwnedSG();
        ArrayList<OwnerDomainName> ownerDomainNameSet = new ArrayList<>();
        if(owned != null)
            ownerDomainNameSet.add(owned);
            if (ownerDomainNameSet != null && ownerDomainNameSet.size() > 0) {
                for (int i = 0; i < ownerDomainNameSet.size(); i++) {
                    String ownerDisplayName = "";
                    if(ownerDomainNameSet.get(i).getDomainDisplayName()!=null &&
                            ownerDomainNameSet.get(i).getDomainDisplayName().trim().length() > 0){
                        ownerDisplayName = ownerDomainNameSet.get(i).getDomainDisplayName().trim();
                    }else{
                        if(ownerDomainNameSet.get(i).getDomainName() != null)
                            ownerDisplayName = ownerDomainNameSet.get(i).getDomainName().trim();
                    }

                    dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD,
                            ownerDisplayName,
                            ownerDomainNameSet.get(i).getDomainCount(),
                            ownerDomainNameSet.get(i).getDomainNotify(),
                            ownerDomainNameSet.get(i).getDomainName(),
                            ownerDomainNameSet.get(i).getDomainType()));
                }
            }
        ArrayList<JoinedDomainNameSet> joinedDomainNameSetTemp = DBWrapper.getInstance().getListOfJoinedSGs();
        if (joinedDomainNameSetTemp != null && joinedDomainNameSetTemp.size() > 0) {
                Collections.sort(joinedDomainNameSetTemp, new Comparator<JoinedDomainNameSet>() {
                    @Override
                    public int compare(final JoinedDomainNameSet object1, final JoinedDomainNameSet object2) {

                        String itemOne = "";
                        String itemTwo = "";
                        //////////////////////////
                        if(object1.getDomainDisplayName()!=null && object1.getDomainDisplayName().trim().length()>0){
                            itemOne = object1.getDomainDisplayName().toUpperCase();
                        }else{
                            itemOne = object1.getDomainName().toUpperCase();
                        }
                        //////////////////////////
                        if(object2.getDomainDisplayName()!=null && object2.getDomainDisplayName().trim().length()>0){
                            itemTwo = object2.getDomainDisplayName().toUpperCase();
                        }else{
                            itemTwo = object2.getDomainName().toUpperCase();
                        }
                        return itemOne.compareTo(itemTwo);
                    }
                });
                for (int i = 0; i < joinedDomainNameSetTemp.size(); i++) {
                    String joinedDisplayName = "";
                    if(joinedDomainNameSetTemp.get(i).getDomainDisplayName()!=null &&
                            joinedDomainNameSetTemp.get(i).getDomainDisplayName().trim().length()>0){
                        joinedDisplayName = joinedDomainNameSetTemp.get(i).getDomainDisplayName().trim();
                    }else{
                        joinedDisplayName = joinedDomainNameSetTemp.get(i).getDomainName().trim();
                    }
                    dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD,
                            joinedDisplayName,
                            joinedDomainNameSetTemp.get(i).getDomainCount(),
                            joinedDomainNameSetTemp.get(i).getDomainNotify(),
                            joinedDomainNameSetTemp.get(i).getDomainName(),
                            joinedDomainNameSetTemp.get(i).getDomainType()));
                }
            }
        return dataList;
    }


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
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        ArrayList<InvitedDomainNameSet> invite = DBWrapper.getInstance().getListOfInvitedSGs();

        //current SG data//////////////////////////////////////////////
        currentSGName = (TextView) layout.findViewById(R.id.currentSGName);
        displayPictureCurrent = (ImageView) layout.findViewById(R.id.displayPictureCurrent);
        notifyCurrent = (ImageView) layout.findViewById(R.id.notifyCurrent);
        notifyCurrent.setOnClickListener(this);
        user = (TextView) layout.findViewById(R.id.user);
        addSGTextView = (TextView) layout.findViewById(R.id.id_add_sg_txt);

        invitedNotificationCount = (TextView)layout.findViewById(R.id.invitedNotificationCount);
        if(invite != null && invite.size() > 0)
            invitedNotificationCount.setText(""+invite.size());


        /*int muteId = DBWrapper.getInstance().getSGMuteInfo(SharedPrefManager.getInstance().getUserDomain());

        if (muteId == 0) {
            notifyCurrent.setVisibility(View.GONE);
        } else {
            notifyCurrent.setVisibility(View.VISIBLE);
        }*/

        String file_id = SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID");
//        String file_id = DBWrapper.getInstance().getSGLogoFileID(SharedPrefManager.getInstance().getUserDomain());
        currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        if (file_id != null && file_id.trim().length() > 0) {
            setProfilePic(displayPictureCurrent, file_id);
        }else{
            setProfilePic(displayPictureCurrent, "");
        }
        user.setText("" + SharedPrefManager.getInstance().getDisplayName() + " (You)");

        ///////////////////////////////////////////////

        llAddSuperGroup = (LinearLayout) layout.findViewById(R.id.llAddSuperGroup);
        userContainer = (LinearLayout) layout.findViewById(R.id.userContainer);
        llInvited = (LinearLayout) layout.findViewById(R.id.llInvited);
        notificationLayout = (RelativeLayout) layout.findViewById(R.id.notificationLayout);

        if (SharedPrefManager.getInstance().getOwnedDomain() != null) {
            addSGTextView.setVisibility(View.GONE);
            llAddSuperGroup.setVisibility(View.GONE);
        }

        llAddSuperGroup.setOnClickListener(this);
        llInvited.setOnClickListener(this);
        userContainer.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);
        displayPictureCurrent.setOnClickListener(this);
        currentSGName.setOnClickListener(this);

        adapter = new ExpandableListAdapter(getSuperGroupList(), getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return layout;
    }


    public void updateView() {
        String file_id = SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID");
        currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        if (file_id != null && file_id.trim().length() > 0) {
            setProfilePic(displayPictureCurrent, file_id);
        }else{
            setProfilePic(displayPictureCurrent, "");
        }
        user.setText("" + SharedPrefManager.getInstance().getDisplayName() + "(You)");
        super.onResume();
    }


    private final int CODE_INVITE = 101;
    private final int CODE_ADD_SUPERGROUP = 102;

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.displayPictureCurrent:
            case R.id.currentSGName:
                Intent intent = new Intent(getActivity(), SuperGroupProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.notifyCurrent: {
                showSnoozeDialog();
                break;
            }
            /*case R.id.notificationLayout: {
                if (flagNotify == true) {
                    flagNotify = false;
                    notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_mute);
                } else {
                    flagNotify = true;
                    notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_unmute);
                }

                break;
            }*/
            case R.id.llInvited: {
                //if (invitedDomainNameSet != null && invitedDomainNameSet.size() > 0) {
                    SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                    String number = iPrefManager.getUserPhone();

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MOBILE_NUMBER_TXT, number);
                    bundle.putStringArrayList(SupergroupListingScreenNew.KEY_INVITED_DOMAIN_SET, invitedDomainNameSet);
                    bundle.putBoolean(SupergroupListingScreenNew.KEY_SHOW_OWNED_ALERT, false);

                    Intent starter = new Intent(getActivity(), SupergroupListingScreenNew.class);
                    starter.putExtras(bundle);
                    startActivityForResult(starter, CODE_INVITE);
                /*} else {
                    Toast.makeText(getActivity(), "Invited List is empty", Toast.LENGTH_SHORT).show();
                }*/
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
            case R.id.llAddSuperGroup: {
                SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                String mobileNumber = iPrefManager.getUserPhone();
//                Bundle bundle = new Bundle();
                intent = new Intent(getActivity(), ProfileScreen.class);
                intent.putExtra(Constants.SG_CREATE_AFTER_LOGIN, true);
                intent.putExtra("MANAGE_MEMBER_BY_ADMIN", true);
                intent.putExtra("PROFILE_EDIT_REG_FLOW", true);
                intent.putExtra(Constants.REG_TYPE, true);
                //SPECIAL DATA FOR BACK
                intent.putExtra("PROFILE_FIRST", true);
                if (mobileNumber.indexOf('-') != -1)
                    intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
                else
                    intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
//                intent.putExtra(Constants.REG_TYPE, "ADMIN");
//                intent.putExtra("REGISTER_SG", true);
                startActivityForResult(intent, CODE_ADD_SUPERGROUP);
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
                try {

//                    data.putExtra("SG_MOBILE", ""+inviteMobileNumber);
//                    data.putExtra("SG_NAME", ""+inviteSGName);
//                    data.putExtra("SG_DISPLAY_NAME", ""+inviteSGDisplayName);
//                    data.putExtra("SG_FILE_ID", ""+inviteSGFileID);
//                    data.putExtra("SG_USER_NAME", ""+inviteUserName);
//                    data.putExtra("SG_USER_ID", inviteUserID);
//                    data.putExtra("SG_USER_PASSWORD", ""+inviteUserPassword);

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
                    if(user != null && user.contains("-"))
                        user = user.replace("-", "");
                    DBWrapper.getInstance().updateSGTypeValue(SG_NAME, 2);
                    DBWrapper.getInstance().updateSGActiveStatus(SG_NAME, "true");
                    ((HomeScreen) getActivity()).switchSG(user + "_" + SG_NAME, true, model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case CODE_ADD_SUPERGROUP: {
                try {
                    String SG_NAME = data.getStringExtra("SG_NAME");
                    ((HomeScreen) getActivity()).switchSG(SG_NAME, false, null);
                } catch (Exception e) {

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

    public void initialize(){
        adapter.notifyDataSetChanged();
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
        if(invitedList != null && invitedNotificationCount != null && invitedList.size() > 0)
            invitedNotificationCount.setText(""+invitedList.size());

        String file_id = SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID");
        if (file_id != null && file_id.trim().length() > 0) {
            setProfilePic(displayPictureCurrent, file_id);
        }else{
            setProfilePic(displayPictureCurrent, "");
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                initialize();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                adapter.notifyDataSetChanged();
                getActivity().invalidateOptionsMenu();
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
        if(HomeScreen.updateNavDrawer){
            HomeScreen.updateNavDrawer = false;
            adapter = new ExpandableListAdapter(getSuperGroupList(), getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
        }
        updateView();
        mDrawerLayout.openDrawer(containerView);
    }

    private boolean setProfilePic(ImageView picView, String groupPicId) {
//		System.out.println("groupPicId : "+groupPicId);
        String img_path = getThumbPath(groupPicId);
        picView.setImageResource(R.drawable.about_icon);

        if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid")){
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
                sharedPrefManager.setSnoozeStartTime(sharedPrefManager.getUserDomain(),System.currentTimeMillis());
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
}