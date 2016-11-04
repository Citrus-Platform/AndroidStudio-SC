package com.superchat.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
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
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.superchat.widgets.ContactsAdapter;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;

//import android.app.ListFragment;

public class ContactsScreen extends ListFragment implements ConnectionStatusListener, ProfileUpdateListener, OnClickListener {
    public static final String TAG = "ContactsFragment";
    Cursor cursor;
    public ContactsAdapter adapter;
    private EditText searchBoxView;
    private ImageView clearSearch;
    private boolean onForeground;
    private ChatService service;
    private XMPPConnection connection;
//    ImageView superGroupIcon;
    TextView superGroupName;
    ImageView global_icon_white;
    ImageView searchIcon;
    boolean isRWA = false;
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
            if(global_icon_white != null && searchBoxView != null){

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBoxView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                searchBoxView.setText("");
                searchBoxView.setVisibility(EditText.GONE);
                global_icon_white.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(ImageView.GONE);
                superGroupName.setVisibility(View.VISIBLE);

                searchIcon.setVisibility(View.VISIBLE);

            }
        }
        else {
            if(global_icon_white != null && searchBoxView != null){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBoxView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                searchBoxView.setText("");
                searchBoxView.setVisibility(EditText.GONE);
                global_icon_white.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(ImageView.GONE);
                superGroupName.setVisibility(View.VISIBLE);

                searchIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    public View onCreateView(LayoutInflater layoutinflater,
                             ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.contact_home, null);
        searchBoxView = (EditText) view.findViewById(R.id.id_search_field);
//        superGroupIcon = (ImageView) view.findViewById(R.id.id_sg_icon);
        superGroupName = (TextView) view.findViewById(R.id.id_sg_name_label);
        global_icon_white = (ImageView) view.findViewById(R.id.global_icon_white);
        global_icon_white.setOnClickListener(this);
        searchIcon = (ImageView) view.findViewById(R.id.id_search_icon);
//        superGroupIcon.setOnClickListener(this);
        superGroupName.setOnClickListener(this);

        searchIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				searchViewLayout.setVisibility(View.VISIBLE);
//				searchBoxView.setVisibility(EditText.VISIBLE);

                searchBoxView.setVisibility(View.VISIBLE);
                global_icon_white.setVisibility(View.GONE);
                clearSearch.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.GONE);
//                superGroupIcon.setVisibility(View.GONE);
                superGroupName.setVisibility(View.GONE);

                searchBoxView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBoxView, InputMethodManager.SHOW_IMPLICIT);
                clearSearch.setVisibility(ImageView.VISIBLE);
            }
        });

        searchBoxView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                String s1 = (new StringBuilder()).append("%")
                        .append(searchBoxView.getText().toString().trim()).append("%")
                        .toString();
                int i = s1.length();
                String as[] = null;
                String s2 = null;
                if (i >= 1) {
                    if (isRWA) {
                        s2 = DatabaseConstants.CONTACT_NAMES_FIELD + " like ? OR " + DatabaseConstants.FLAT_NUMBER + " like ? OR " + DatabaseConstants.BUILDING_NUMBER + " like ? AND " + DatabaseConstants.VOPIUM_FIELD + "!=?";
                        as = (new String[]{s1, s1, s1, "2"});
                    } else {
                        s2 = DatabaseConstants.CONTACT_NAMES_FIELD + " like ? AND " + DatabaseConstants.VOPIUM_FIELD + "!=?";
                        as = (new String[]{s1, "2"});
                    }
                }
                updateCursor(s2, as);
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
                searchBoxView.setText("");
                searchBoxView.setVisibility(View.GONE);
                global_icon_white.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);
//                superGroupIcon.setVisibility(View.VISIBLE);
                superGroupName.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
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
        if (superGroupName != null)
            superGroupName.setText(SharedPrefManager.getInstance().getCurrentSGDisplayName());
//        if (superGroupIcon != null && SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID") != null)
//            setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
//		showAllContacts();
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

            int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
            SharedPrefManager.getInstance().saveNewContactsCounter(contactsCount);
            if (SharedPrefManager.getInstance().getNewContactsCounter() >= 0 && (contactsCount - SharedPrefManager.getInstance().getNewContactsCounter()) > 0) {
                if (SharedPrefManager.getInstance().getChatCounter() > 0) {
                    ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.VISIBLE);
                    ((HomeScreen) fragmentactivity).unseenContactView.setText(String.valueOf(SharedPrefManager.getInstance().getChatCounter()));
                } else {
                    ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.GONE);
                }
            } else
                ((HomeScreen) fragmentactivity).unseenContactView.setVisibility(View.GONE);
        } catch (Exception e) {
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
        cursor = DBWrapper.getInstance().getContactsCursor(sg);
        System.out.println("Cursor size = "+cursor.getCount());
        if (cursor != null && adapter != null) {
            adapter.changeCursor(cursor);
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.id_sg_icon:
            case R.id.id_sg_name_label:
                Intent intent = new Intent(getActivity(), SuperGroupProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.global_icon_white:
                ((HomeScreen) getActivity()).openDrawer();
                break;
        }
    }
}
