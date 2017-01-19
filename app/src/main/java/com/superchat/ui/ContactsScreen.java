package com.superchat.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chat.sdk.ChatService;
import com.chat.sdk.ConnectionStatusListener;
import com.chat.sdk.ProfileUpdateListener;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;

//import android.app.ListFragment;

public class ContactsScreen extends CustomFragmentHomeTabs implements ConnectionStatusListener, ProfileUpdateListener{
    public static final String TAG = "ContactsFragment";
//    Cursor cursor;
    MergeCursor cursor;
    Cursor cursor1 , cursor2;
    public ContactsAdapter adapter;
    private EditText searchBoxView;
    private ImageView clearSearch;
    private boolean onForeground;
    private ChatService service;
    private XMPPConnection connection;
//    ImageView superGroupIcon;
    boolean isRWA = false;
    LinearLayout contactLoadingLayout;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            service = ((ChatService.MyBinder) binder).getService();
            Log.d("Service", "Connected");
            if (service != null) {
                connection = service.getconnection();
                service.setProfileUpdateListener(ContactsScreen.this);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            connection = null;
            service = null;
        }
    };

    public void setPorfileListener() {
        if (service != null)
            service.setProfileUpdateListener(ContactsScreen.this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(searchBoxView != null){

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBoxView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                searchBoxView.setText("");
                searchBoxView.setVisibility(EditText.GONE);
                clearSearch.setVisibility(ImageView.GONE);

            }
        }
        else {
            if(searchBoxView != null){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBoxView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                searchBoxView.setText("");
                searchBoxView.setVisibility(EditText.GONE);
                clearSearch.setVisibility(ImageView.GONE);
            }
        }
    }

    public View onCreateView(LayoutInflater layoutinflater,
                             ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.contact_home, null);

        UtilSetFont.setFontMainScreen(view);

        searchBoxView = (EditText) view.findViewById(R.id.id_search_field);

        toolbar_child_fragment_tab = (Toolbar) view.findViewById(R.id.toolbar_child_fragment_tab);
        toolbar_child_fragment_tab.setVisibility(View.GONE);

        searchBoxView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
//                if(searchBoxView.getText().toString().trim().length() == 0)
//                    return;
                String sg = SharedPrefManager.getInstance().getUserDomain();
                String s1 = (new StringBuilder()).append("%")
                        .append(searchBoxView.getText().toString().trim()).append("%")
                        .toString();
                int i = s1.length();
                String as[] = null;
                String s2 = null;
                if (i > 2) {
                    if (isRWA) {
                        s2 = DatabaseConstants.CONTACT_NAMES_FIELD + " like ? OR " + DatabaseConstants.FLAT_NUMBER + " like ? OR " + DatabaseConstants.BUILDING_NUMBER + " like ? AND "
                                + DatabaseConstants.VOPIUM_FIELD + "!=? AND "+ DatabaseConstants.USER_SG + "=?";
                        as = (new String[]{s1, s1, s1, "2", sg});
                    } else {
                        s2 = DatabaseConstants.CONTACT_NAMES_FIELD + " like ? AND " + DatabaseConstants.VOPIUM_FIELD + "!=? AND "+ DatabaseConstants.USER_SG + "=?";
                        as = (new String[]{s1, "2", sg});
                    }
                    updateCursorForSearch(s2, as);
                }else{
                    updateCursor(null, null);
                }
            }

            public void beforeTextChanged(CharSequence charsequence, int i,
                                          int j, int k) {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j,
                                      int k) {
            }

        });

        clearSearch = (ImageView) view.findViewById(R.id.id_back_arrow);
        clearSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                eventBackOnToolbar();
            }
        });

        contactLoadingLayout = (LinearLayout) view.findViewById(R.id.id_loading_layout);
//		view.findViewById(R.id.id_add_icon).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				 Intent intent = new Intent(Intent.ACTION_INSERT,  ContactsContract.Contacts.CONTENT_URI);
//				 intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//				startActivity(intent);
//			}
//		});
//		superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
//		setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
//		if(!SharedPrefManager.getInstance().isContactSynched()){
//			((ImageView)view.findViewById(R.id.sync_id)).performClick();
//			SharedPrefManager.getInstance().setContactSynched(true);
//		}
        return view;
    }

    public void eventBackOnToolbar(){
        clearHideSearch();
        try {
            if (((HomeScreen) getActivity()) != null)
                ((HomeScreen) getActivity()).clearFunction();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void clearHideSearch(){
        try{
        searchBoxView.setText("");
        searchBoxView.setVisibility(View.GONE);
        clearSearch.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        hideToolbar();
    } catch(Exception e){

    }
    }

    public void performSearch(){
        try{
            searchBoxView.setVisibility(View.VISIBLE);
            clearSearch.setVisibility(View.VISIBLE);

            isSearchOn = true;
            searchBoxView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBoxView, InputMethodManager.SHOW_IMPLICIT);
            clearSearch.setVisibility(ImageView.VISIBLE);
        } catch(Exception e){

        }
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

    private boolean setSGProfilePic(ImageView picView, String groupPicId) {
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

    private String getImagePath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";
            File file = Environment.getExternalStorageDirectory();
            return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
        }
        return null;
    }

    public void onResume() {
        super.onResume();
        isRWA = SharedPrefManager.getInstance().getDomainType().equals("rwa");
        getActivity().bindService(new Intent(getActivity(), ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
        onForeground = true;
        ChatService.setConnectionStatusListener(this);
        setPorfileListener();
        if (HomeScreen.refreshContactList) {
            updateCursor(null, null);
            HomeScreen.refreshContactList = false;
        }

        if(HomeScreen.isContactSynching && contactLoadingLayout != null){
            contactLoadingLayout.setVisibility(View.VISIBLE);
        }
//        if (superGroupIcon != null && SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID") != null)
//            setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
//		showAllContacts();
    }

    boolean isSearchOn;
    public boolean isSearchOn(){
        return isSearchOn;
    }
    public void resetSearch(){
        if(searchBoxView != null){
            searchBoxView.setText("");
            searchBoxView.setVisibility(View.GONE);
            clearSearch.setVisibility(View.GONE);
            isSearchOn = false;
        }
    }

    public void onPause() {
        super.onPause();
        onForeground = false;
        try {
            getActivity().unbindService(mConnection);
        } catch (Exception e) {
            // Just ignore that
            Log.d("MessageHistoryScreen", "Unable to un bind");
        }
    }

    public void onActivityCreated(Bundle bundle) {
        Intent intent = getActivity().getIntent();
        showAllContacts();
        super.onActivityCreated(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 105:
                    Log.d(TAG, "onActivityResult in ContactsScreen are called.");
                    break;
            }
    }

    public void showAllContacts() {
        FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity == null)
            return;
        String as[] = {DatabaseConstants.CONTACT_NAMES_FIELD};
        int ai[] = new int[1];
        ai[0] = R.id.id_contact_name;
        try {
            adapter = new ContactsAdapter(fragmentactivity, R.layout.contact_list_item, cursor, as, ai, 0);
            getListView().setAdapter(adapter);
            updateCursor(null, null);

            SharedPrefManager pref = SharedPrefManager.getInstance();
            int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
            pref.saveNewContactsCounter(pref.getUserDomain(), contactsCount);
           /* if (pref.getNewContactsCounter(pref.getUserDomain()) >= 0 && (contactsCount - pref.getNewContactsCounter(pref.getUserDomain())) > 0) {
                if (pref.getChatCounter(pref.getUserDomain()) > 0) {
                    ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.VISIBLE);
                    ((HomeScreen) fragmentactivity).unseenContactView.setText(String.valueOf(pref.getChatCounter(pref.getUserDomain())));
                } else {
                    ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.GONE);
                }
            } else
                ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.GONE);*/

            ((HomeScreen) fragmentactivity).setTabsCustom();
        } catch (Exception e) {
        }

        if(!HomeScreen.isContactSynching && contactLoadingLayout != null){
            contactLoadingLayout.setVisibility(View.GONE);
        }
    }

    public void updateCursor(String s, String as[]) {
        Log.i(TAG, "Updating cursor");
        String sg = SharedPrefManager.getInstance().getUserDomain();

//		cursor = DBWrapper.getInstance().query(DatabaseConstants.TABLE_NAME_CONTACT_NAMES, null, s, as,
//				DatabaseConstants.VOPIUM_FIELD+" DESC, "+DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE");

//        if (s == null) {
//            s = DatabaseConstants.VOPIUM_FIELD + "!=?" + " AND " + DatabaseConstants.USER_SG + "='?'";
//            as = (new String[]{"2", sg});
//        }
//        cursor = DBWrapper.getInstance().query(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, s, as,
//                DatabaseConstants.VOPIUM_FIELD + " ASC, " + DatabaseConstants.CONTACT_NAMES_FIELD + " COLLATE NOCASE");
//        cursor = DBWrapper.getInstance().getContactsCursor(sg);
//        System.out.println("Cursor size = "+cursor.getCount());

        cursor1 = DBWrapper.getInstance().getContactsCursor(sg);
        cursor2 = DBWrapper.getInstance().getContactsCursorWithoutShared(sg);

        cursor = new MergeCursor(new Cursor[]{cursor1 , cursor2});


        if(cursor != null){
            if(cursor.getCount() > 0) {
                HomeScreen.isContactSynching = false;
                if(contactLoadingLayout != null)
                    contactLoadingLayout.setVisibility(View.GONE);
            }
            if(adapter != null){
                adapter.changeCursor(cursor);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateCursorForSearch(String s, String as[]) {
        Log.i(TAG, "Updating cursor");
        String sg = SharedPrefManager.getInstance().getUserDomain();
        if(s == null){
            s = DatabaseConstants.VOPIUM_FIELD + "!=? AND " +  DatabaseConstants.USER_SG + "=?";
            as = (new String[] { "2", sg});
        }
        cursor1 = DBWrapper.getInstance().query(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, s, as,
                DatabaseConstants.VOPIUM_FIELD+" ASC, "+DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE");
        if (cursor1 != null && adapter != null)
        {
            adapter.changeCursor(cursor1);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyConnectionChange() {
    }

    @Override
    public void notifyProfileUpdate(final String userName) {
        // TODO Auto-generated method stub
//		if (onForeground && getListView() != null)
//			getActivity().runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				updateRow(userName);
//			}
//			});

    }

    private void updateRow(String userName, String status, String userDisplayName) {
        ListView listview = getListView();
        int row_count = listview.getChildCount();
        for (int i = 0; i < row_count; i++) {
            View view = listview.getChildAt(i);
            RoundedImageView imgv = (RoundedImageView) view.findViewById(R.id.contact_icon);
            ImageView def_imgv = (ImageView) view.findViewById(R.id.contact_icon_default);
            if (((String) def_imgv.getTag()).equalsIgnoreCase(userName)) {
                //Update the row.
                if (adapter != null)
                    adapter.setProfilePic(imgv, def_imgv, userName, "", null, false);
                if (userDisplayName != null)
                    ((TextView) view.findViewById(R.id.id_contact_name)).setText(userDisplayName);
                if (status != null)
                    ((TextView) view.findViewById(R.id.id_contact_status)).setText(status);
            }
        }
    }

    @Override
    public void notifyProfileUpdate(final String userName, final String status) {
        // TODO Auto-generated method stub
//		if (onForeground && getListView() != null)
//			getActivity().runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				updateRow(userName, status);
//			}
//			});
    }

    @Override
    public void notifyProfileUpdate(final String userName, final String status, final String userDisplayName) {
        // TODO Auto-generated method stub
        if (onForeground && getListView() != null)
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (SharedPrefManager.getInstance().isSharedIDContact(userName))
                        showAllContacts();
                    else
                        updateRow(userName, status, userDisplayName);
                }
            });
        try {
            ((HomeScreen) getActivity()).notificationUI();
        } catch (Exception e) {
        }
    }
}
