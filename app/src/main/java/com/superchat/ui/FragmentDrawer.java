package com.superchat.ui;

/**
 * Created by citrus on 9/15/2016.
 */

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.superchat.R;
import com.superchat.model.SGroupListObject;
import com.superchat.model.SlidingMenuData;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentDrawer extends Fragment implements View.OnClickListener {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    //////////////////////////////////
    public TextView currentSGName;
    public ImageView displayPictureCurrent;
    public ImageView notifyCurrent;
    public TextView user;

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

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

        //////////////////////////////////////////
        JSONObject json;
        SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
        String data = iPrefManager.getSgListData();
        ////////////////////////////////////////
        if(data!=null){

        ArrayList<SGroupListObject> ownerDomainNameSet = new ArrayList<>();
        SGroupListObject sGroupListObject = new SGroupListObject();
        try {
                json = new JSONObject(data);
            Log.e("loginresponse" , "is : "+json.toString());

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
            } catch (Exception e) {

            }
        }
        }else{
            dataList.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Super Group", "0", "0"));
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

        //current SG data//////////////////////////////////////////////
        currentSGName = (TextView) layout.findViewById(R.id.currentSGName);
        displayPictureCurrent = (ImageView) layout.findViewById(R.id.displayPictureCurrent);
        notifyCurrent = (ImageView) layout.findViewById(R.id.notifyCurrent);
        user = (TextView) layout.findViewById(R.id.user);


        String file_id = SharedPrefManager.getInstance().getSGFileId("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        currentSGName.setText("" + SharedPrefManager.getInstance().getCurrentSGDisplayName());
        if (file_id != null && file_id.length() > 0) {
            setProfilePic(displayPictureCurrent, file_id);
        } else {

        }
        user.setText("" + SharedPrefManager.getInstance().getDisplayName() + "(You)");


        ///////////////////////////////////////////////

        llAddSuperGroup = (LinearLayout) layout.findViewById(R.id.llAddSuperGroup);
        llInvited = (LinearLayout) layout.findViewById(R.id.llInvited);
        notificationLayout = (RelativeLayout) layout.findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(this);

        if (SharedPrefManager.getInstance().getOwnedDomain() == null) {
            llAddSuperGroup.setVisibility(View.GONE);
        }

        llAddSuperGroup.setOnClickListener(this);
        llInvited.setOnClickListener(this);
        ////////////////////////////////////////////////////////////
        adapter = new ExpandableListAdapter(getSuperGroupList(), getActivity());
        ////////////////////////////////////////////////////////////
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return layout;
    }


    private final int CODE_INVITE = 101;
    private final int CODE_ADD_SUPERGROUP = 102;

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.notificationLayout: {
                if (flagNotify == true) {
                    flagNotify = false;
                    notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_mute);
                } else {
                    flagNotify = true;
                    notifyCurrent.setBackgroundResource(R.drawable.ic_icon_navigation_unmute);
                }

                break;
            }
            case R.id.llInvited: {
                if (invitedDomainNameSet != null && invitedDomainNameSet.size() > 0) {
                    SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                    String number = iPrefManager.getUserPhone();

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MOBILE_NUMBER_TXT, number);
                    bundle.putStringArrayList(SupergroupListingScreenNew.KEY_INVITED_DOMAIN_SET, invitedDomainNameSet);
                    bundle.putBoolean(SupergroupListingScreenNew.KEY_SHOW_OWNED_ALERT, false);

                    Intent starter = new Intent(getActivity(), SupergroupListingScreenNew.class);
                    starter.putExtras(bundle);
                    startActivityForResult(starter, CODE_INVITE);
                } else {
                    Toast.makeText(getActivity(), "Invited List is empty", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.llAddSuperGroup: {
                SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                String mobileNumber = iPrefManager.getUserPhone();

                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), ProfileScreen.class);
                bundle.putString(Constants.KEY_SCREEN_USER_COMING_FROM, Constants.VAL_SCREEN_USER_COMING_FROM_NEW_USER_AFTER_LOGIN);
                bundle.putBoolean(Constants.REG_TYPE, true);
                bundle.putBoolean("PROFILE_EDIT_REG_FLOW", true);
                //SPECIAL DATA FOR BACK
                bundle.putBoolean("PROFILE_FIRST", true);
                if (mobileNumber.indexOf('-') != -1)
                    intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
                else
                    intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
                intent.putExtra(Constants.REG_TYPE, "ADMIN");
                intent.putExtra("REGISTER_SG", true);
                intent.putExtras(bundle);
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
                    String SG_NAME = data.getStringExtra("SG_NAME");
                    ((HomeScreen) getActivity()).switchSG(SG_NAME);
                } catch (Exception e) {

                }
                break;
            }
            case CODE_ADD_SUPERGROUP: {
                try {
                    String SG_NAME = data.getStringExtra("SG_NAME");
                    ((HomeScreen) getActivity()).switchSG(SG_NAME);
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

    private boolean setProfilePic(ImageView picView, String groupPicId) {
//		System.out.println("groupPicId : "+groupPicId);
        String img_path = getThumbPath(groupPicId);
        picView.setImageResource(R.drawable.about_icon);
        if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid"))
            return false;
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
}