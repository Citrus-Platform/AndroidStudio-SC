
package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.RegistrationForm;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OpenHubSearchScreen extends AppCompatActivity implements OnClickListener {

    public static void start(Context context) {
        Bundle bundle = new Bundle();

        Intent starter = new Intent(context, OpenHubSearchScreen.class);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    Activity activity = this;
    Context context = this;

    private Toolbar toolbar;
    private static final String TAG = "OpenHubSearchScreen";
    String countryCode = "91";
    String mobileNumber = null;
    String superGroupName = "";
    String displayName = null;
    String selectedSGDisplayName = null;
    Dialog picChooserDialog;
    ProfilePicUploader picUploader;
    ImageView superGroupIconView;
    boolean pendingProfile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.open_hub_search_screen);

        init();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Open Hubs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new GetSuperGroupProfile(superGroupName).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    public void onResume() {
        super.onResume();
    }

    public void showDialog(String s, boolean custom) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog_gray);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    Dialog welcomeDialog = null;

    public void showWelcomeScreen(final String supergroup_name, final String sg_display_name, final String inviter_name, final String org_name, final String file_id, final int type, final String domainType) {
        SharedPrefManager.getInstance().setDomainType(domainType);
        welcomeDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
        welcomeDialog.setCanceledOnTouchOutside(false);
        welcomeDialog.setContentView(R.layout.welcome_screen);
        superGroupIconView = (ImageView) welcomeDialog.findViewById(R.id.id_profile_pic);

        if (superGroupIconView != null)
            superGroupIconView.setOnClickListener(this);
        picChooserDialog = new Dialog(this);
        picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
        picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
        picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
        picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
//		((TextView)welcomeDialog.findViewById(R.id.id_domain_name)).setText(supergroup_name);
        ((TextView) welcomeDialog.findViewById(R.id.id_domain_name)).setText(sg_display_name);
        if (type == 1)
            ((TextView) welcomeDialog.findViewById(R.id.id_inviters_name)).setText("" + inviter_name);
        else if (type == 2) {
            ((TextView) welcomeDialog.findViewById(R.id.id_inviter_label)).setText("Owned by");
            ((TextView) welcomeDialog.findViewById(R.id.id_inviters_name)).setText("You");
        } else {
            ((TextView) welcomeDialog.findViewById(R.id.id_inviter_label)).setText("Owned by");
            ((TextView) welcomeDialog.findViewById(R.id.id_inviters_name)).setText("" + inviter_name);
        }
        if (org_name != null && !org_name.equals("")) {
            ((TextView) welcomeDialog.findViewById(R.id.user_org_name)).setText("" + org_name);
        } else {
            ((TextView) welcomeDialog.findViewById(R.id.user_org_name)).setVisibility(View.GONE);
            ((TextView) welcomeDialog.findViewById(R.id.id_org_lable)).setVisibility(View.GONE);
        }
        if (file_id != null) {
            setProfilePic(superGroupIconView, file_id);
            setSGFullPic(superGroupIconView, file_id);
            SharedPrefManager.getInstance().saveSGFileId(supergroup_name, file_id);
        }

        ((Button) welcomeDialog.findViewById(R.id.done_button)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                inviteSGFileID = file_id;
                if (type != 2 && type != 3)
                    showNameDialog(sg_display_name);
                else
                    registerUserOnServer(supergroup_name, sg_display_name, v);
            }
        });
        ((TextView) welcomeDialog.findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (welcomeDialog != null) {
                    welcomeDialog.dismiss();
                    welcomeDialog = null;
                    return;
                }
            }
        });
        ((ImageView) welcomeDialog.findViewById(R.id.id_profile_pic)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String file_path = getImagePath(file_id);
                if (file_path == null || (file_path != null && file_path.trim().length() == 0))
                    file_path = getImagePath(null);
//				if(file_path == null)
//					file_path = getThumbPath(fileName);
                if (file_path != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    if (file_path.startsWith("http://"))
                        intent.setDataAndType(Uri.parse(file_path), "image/*");
                    else
                        intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
                    startActivity(intent);
                }
            }
        });
        welcomeDialog.show();
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.row_layout:
                LinearLayout ll = (LinearLayout) view.findViewById(R.id.row_layout);
                RadioButton radio = (RadioButton) view.findViewById(R.id.id_sg_radio_button);
                if (radio != null && radio.isChecked()) {
                    superGroupName = "";
                    radio.setChecked(false);
                } else {
                    radio.setChecked(true);
                    if (ll.getTag() != null)
                        superGroupName = ll.getTag().toString();
                    //			registerUserOnServer(superGroupName, view);
                }
                break;
            case R.id.id_group_icon:
                String tmpImgId1 = null;
                if (picUploader != null && picUploader.getServerFileId() != null)
                    tmpImgId1 = picUploader.getServerFileId();
                if (picChooserDialog != null && !picChooserDialog.isShowing() && tmpImgId1 == null)
                    picChooserDialog.show();
                else if (AppUtil.capturedPath1 != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File file = new File(AppUtil.capturedPath1);
                    Uri outputFileUri = Uri.fromFile(file);
                    intent.setDataAndType(outputFileUri, "image/*");
                    startActivity(intent);
                }
                break;
            case R.id.id_back:
            case R.id.id_cancel:
                finish();
                break;
            case R.id.id_camera:
                AppUtil.clearAppData();
                AppUtil.openCamera(this, AppUtil.capturedPath1,
                        AppUtil.POSITION_CAMERA_PICTURE);
                picChooserDialog.cancel();
                break;
            case R.id.id_gallery:
                AppUtil.clearAppData();
                AppUtil.openImageGallery(this,
                        AppUtil.POSITION_GALLRY_PICTURE);
                picChooserDialog.cancel();
                break;
            case R.id.id_group_camera_icon:
                if (picChooserDialog != null && !picChooserDialog.isShowing())
                    picChooserDialog.show();
                break;
        }
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

    private boolean setSGFullPic(ImageView picView, String groupPicId) {
        String img_path = getImagePath(groupPicId);
//		picView.setImageResource(R.drawable.about_icon);
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
                    new BitmapDownloader(this, (RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
                else
                    new BitmapDownloader(this, (RoundedImageView) picView).execute(groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 11)
                new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
            else
                new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);

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

    public void showDialog(String s, String btnTxt) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        TextView btn = ((TextView) bteldialog.findViewById(R.id.id_ok));
        btn.setText(btnTxt);
        btn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    private void registerUserOnServer(String super_group, String sg_display_name, View view) {
        String imei = SuperChatApplication.getDeviceId();
        String imsi = SuperChatApplication.getNetworkOperator();
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (version != null && version.contains("."))
                version = version.replace(".", "_");
            if (version == null)
                version = "";
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        selectedSGDisplayName = sg_display_name;
        String clientVersion = "Android_" + version;
        RegistrationForm registrationForm = new RegistrationForm(mobileNumber, null, imei, null, clientVersion, null, "false");
        registrationForm.setToken(imei);
        registrationForm.countryCode = countryCode;
        if (super_group != null && super_group.trim().length() > 0)
            registrationForm.setDomainName(super_group);
//        SharedPrefManager.getInstance().saveUserPhone(mobileNumber);
        inviteMobileNumber = mobileNumber;
        if (Build.VERSION.SDK_INT >= 11)
            new SignupTaskOnServer(registrationForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new SignupTaskOnServer(registrationForm, view).execute();
    }

    public void onBackClick(View view) {
        if (welcomeDialog != null) {
            welcomeDialog.dismiss();
            welcomeDialog = null;
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (welcomeDialog != null) {
            welcomeDialog.dismiss();
            welcomeDialog = null;
            return;
        }
        finish();
    }

    //---------------------------------------
    String inviteMobileNumber;
    String inviteSGName;
    String inviteSGDisplayName;
    String inviteSGFileID;
    String inviteUserName;
    String inviteUserPassword;
    long inviteUserID;

    public class SignupTaskOnServer extends AsyncTask<String, String, String> {
        RegistrationForm registrationForm;
        ProgressDialog progressDialog = null;
        View view1;

        public SignupTaskOnServer(RegistrationForm registrationForm, final View view1) {
            this.registrationForm = registrationForm;
            this.view1 = view1;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(OpenHubSearchScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String JSONstring = new Gson().toJson(registrationForm);
            DefaultHttpClient client1 = new DefaultHttpClient();

            Log.i(TAG, "SignupTaskOnServer :: request:" + JSONstring);
            String url = Constants.SERVER_URL + "/tiger/rest/user/register";
            Log.i(TAG, "SignupTaskOnServer :: url:" + url);
            HttpPost httpPost = new HttpPost(url);
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, false);
            HttpResponse response = null;

            try {
                httpPost.setEntity(new StringEntity(JSONstring));
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) { //new1
                        HttpEntity entity = response.getEntity();
//						    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String line = "";
                        String str = "";
                        while ((line = rd.readLine()) != null) {
                            str += line;
                        }
                        if (str != null && !str.equals("")) {
                            Log.d(TAG, "SignupTaskOnServer ::  response:" + str);
                            str = str.trim();
                            Gson gson = new GsonBuilder().create();
                            if (str == null || str.contains("error")) {
                                return str;
                            }
                            RegistrationForm regObj = gson.fromJson(str, RegistrationForm.class);
                            if (regObj != null) {
                                SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//                                if (iPrefManager != null && iPrefManager.getUserId() != 0) {
//                                    if (iPrefManager.getUserId() != regObj.iUserId) {
//                                        try {
//                                            DBWrapper.getInstance().clearMessageDB();
////														iPrefManager.clearSharedPref();
//                                        } catch (Exception e) {
//                                        }
//                                    }
//                                }
                                Log.i(TAG, "SignupTaskOnServer :: password, mobileNumber: " + regObj.getPassword() + " , " + regObj.iMobileNumber);

//                                iPrefManager.saveUserDomain(superGroupName);
                                inviteSGName = superGroupName;
                                if (selectedSGDisplayName != null) {
//                                    iPrefManager.saveCurrentSGDisplayName(selectedSGDisplayName);
                                    inviteSGDisplayName = selectedSGDisplayName;
                                } else
                                    inviteSGDisplayName = inviteSGName;

//                                iPrefManager.saveAuthStatus(regObj.iStatus);
//                                if (regObj.token != null)
//                                    iPrefManager.saveDeviceToken(regObj.token);

//                                iPrefManager.saveUserId(regObj.iUserId);
                                inviteUserID = regObj.iUserId;

//                                iPrefManager.setAppMode("VirginMode");
//                                iPrefManager.saveUserPhone(regObj.iMobileNumber);
//											iPrefManager.saveUserPassword(regObj.getPassword());
//                                iPrefManager.saveUserLogedOut(false);
                                pendingProfile = regObj.pendingProfile;
//											pendingProfile = true;
//                                iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                            }
                            verifyUserSG(regObj.iUserId);
                        }

                    }
                } catch (ClientProtocolException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                } catch (IOException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                }

            } catch (UnsupportedEncodingException e1) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:" + e1.toString());
            } catch (Exception e) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:" + e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (str != null && str.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = gson.fromJson(str, ErrorModel.class);
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null && citrusError.code.equals("20019")) {
                            SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                            iPrefManager.saveUserDomain(superGroupName);
                            iPrefManager.saveCurrentSGDisplayName(selectedSGDisplayName);
                            iPrefManager.saveUserId(errorModel.userId);
                            //below code should be only, in case of brand new user - "First time SC user"
                            iPrefManager.setAppMode("SecondMode");
                            iPrefManager.saveUserLogedOut(false);
                            iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                            //Do not show this dialog, Simply verify and get in
//							verifyUserSG(errorModel.userId);
                            //Update SG type in db
                            DBWrapper.getInstance().updateSGTypeValue(superGroupName, 2);
                            showAlertDialog(citrusError.message, errorModel.userId);
                        } else
                            showDialog(citrusError.message);
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            }
            super.onPostExecute(str);
        }
    }

    //--------------------------------------------------------------------------------------------------------
    public void showDialog(String s) {
        try {
            final Dialog bteldialog = new Dialog(this);
            bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bteldialog.setCanceledOnTouchOutside(false);
            bteldialog.setContentView(R.layout.custom_dialog);
            ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
            ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    bteldialog.cancel();
                    return false;
                }
            });
            bteldialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showAlertDialog(String s, final long user_id) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                verifyUserSG(user_id);
                return false;
            }
        });
        bteldialog.show();
    }

    public void showNameDialog(final String sg_display_name) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.name_add_popup);
        ((Button) bteldialog.findViewById(R.id.id_join_btn)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                displayName = ((EditText) bteldialog.findViewById(R.id.id_display_name_field)).getText().toString().trim();
                if (!Utilities.checkName(displayName)) {
//					showDialog(getString(R.string.display_name_hint));
                    Toast.makeText(OpenHubSearchScreen.this, getString(R.string.display_name_hint), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!SuperChatApplication.isNetworkConnected()) {
                    Toast.makeText(OpenHubSearchScreen.this, getString(R.string.check_net_connection), Toast.LENGTH_LONG).show();
                    return false;
                }
                bteldialog.cancel();
                registerUserOnServer(superGroupName, sg_display_name, v);
                return false;
            }
        });
        ((Button) bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    //-------------------------------------------------------------------------------
    private void verifyUserSG(long id) {
        String codeVerifyUrl = null;
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (version != null && version.contains("."))
                version = version.replace(".", "_");
            if (version == null)
                version = "";
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String imei = SuperChatApplication.getDeviceId();
        String clientVersion = "Android_" + version;
//		String display_name = null;
//		if(SharedPrefManager.getInstance().getDisplayName() != null)
//			display_name = SharedPrefManager.getInstance().getDisplayName();
        if (displayName != null && Utilities.checkName(displayName)) {
            try {
                codeVerifyUrl = Constants.SERVER_URL + "/tiger/rest/user/mobileverification/verify?userId=" + id + "&clientVersion=" + clientVersion + "&imei=" + imei + "&token=" + Constants.regid + "&name=" + URLEncoder.encode(displayName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            codeVerifyUrl = Constants.SERVER_URL + "/tiger/rest/user/mobileverification/verify?userId=" + id + "&clientVersion=" + clientVersion + "&imei=" + imei + "&token=" + Constants.regid;
        }

        Log.i(TAG, "verifyUserSG :: url : " + codeVerifyUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        client = SuperChatApplication.addHeaderInfo(client, false);
        client.get(codeVerifyUrl, null, new AsyncHttpResponseHandler() {
            ProgressDialog dialog = null;

            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = ProgressDialog.show(OpenHubSearchScreen.this, "", "Loading. Please wait...", true);
                    }
                });
                Log.d(TAG, "verifyUserSGonStart: ");
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
//				if(welcomeDialog != null)
//					welcomeDialog.cancel();
                Log.i(TAG, "verifyUserSG :: response : " + arg1);
                Gson gson = new GsonBuilder().create();
                final RegMatchCodeModel objUserModel = gson.fromJson(arg1, RegMatchCodeModel.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                });
                if (objUserModel.iStatus != null
                        && objUserModel.iStatus.equalsIgnoreCase("success")) {
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();

//                    sharedPrefManager.saveUserVarified(true);
//                    sharedPrefManager.setMobileVerified(sharedPrefManager.getUserPhone(), true);
//                    sharedPrefManager.saveUserName(objUserModel.username);
//                    sharedPrefManager.saveUserPassword(objUserModel.password);
//                    sharedPrefManager.setOTPVerified(true);

                    inviteUserName = objUserModel.username;
                    inviteUserPassword = objUserModel.password;

//                    saveDataAndMove();


                    Intent data = new Intent();
                    data.putExtra("SG_MOBILE", "" + inviteMobileNumber);
                    data.putExtra("SG_NAME", "" + inviteSGName);
                    data.putExtra("SG_DISPLAY_NAME", "" + inviteSGDisplayName);
                    data.putExtra("SG_FILE_ID", "" + inviteSGFileID);
                    data.putExtra("SG_USER_NAME", "" + inviteUserName);
                    data.putExtra("SG_USER_ID", inviteUserID);
                    data.putExtra("SG_USER_PASSWORD", "" + inviteUserPassword);
                    setResult(RESULT_OK, data);
                    finish();

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showDialog(objUserModel.iMessage);
                        }
                    });

                }
//			runOnUiThread(new Runnable() {
//				Splas
//				@Override
//				public void run() {
//					if (dialog != null) {
//						dialog.dismiss();
//						dialog = null;
//					}
//				}
//			});
                super.onSuccess(arg0, arg1);
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                Log.i(TAG, "verifyCode method onFailure: " + arg1);
                if (welcomeDialog != null)
                    welcomeDialog.cancel();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                });
                showDialog(getString(R.string.network_not_responding));
                super.onFailure(arg0, arg1);
            }
        });
    }

    //============================================================
    class GetSuperGroupProfile extends AsyncTask<String, String, String> {

        String domain_name;
        ProgressDialog progressDialog = null;

        public GetSuperGroupProfile(final String domain_name) {
            this.domain_name = domain_name;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(OpenHubSearchScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/profile?domainName=" + URLEncoder.encode(domain_name, "UTF-8");
                Log.i(TAG, "GetSuperGroupProfile :: doInBackground : URL - " + url);
                HttpGet httppost = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);
                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    return data;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String data) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (data != null) {
                Log.i(TAG, "GetSuperGroupProfile :: onPostExecute : response data - " + data);
                if (data.contains("success")) {
                    String sg_display_name = null;
                    String sg_name = null;
                    String inviter = null;
                    String file_id = null;
                    String org_name = null;
                    String privacy_type = null;
                    String domainType = null;
                    try {
                        JSONObject json = new JSONObject(data);
                        if (json != null && json.has("domainName")) {
                            sg_name = json.getString("domainName");
                            superGroupName = sg_name;
                        } else
                            sg_name = null;
                        if (json != null && json.has("displayName"))
                            sg_display_name = json.getString("displayName");
                        else
                            sg_display_name = sg_name;
                        if (json != null && json.has("adminName"))
                            inviter = json.getString("adminName");
                        else
                            inviter = null;
                        if (json != null && json.has("logoFileId")) {
                            file_id = json.getString("logoFileId");
                            SharedPrefManager.getInstance().saveSGFileId(sg_name, file_id);
                        } else
                            file_id = null;
                        if (json != null && json.has("orgName"))
                            org_name = json.getString("orgName");
                        else
                            org_name = null;
                        if (json != null && json.has("privacyType"))
                            privacy_type = json.getString("privacyType");
                        else
                            privacy_type = "open";
                        if (json != null && json.has("domainType"))
                            domainType = json.getString("domainType");
                        else
                            domainType = null;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (privacy_type != null && privacy_type.equalsIgnoreCase("closed"))
                        showDialog("This SuperGroup only accepts members by invitation");
                    else
                        showWelcomeScreen(superGroupName, sg_display_name, inviter, org_name, file_id, 0, domainType);
                } else if (data.contains("error")) {
                    Gson gson = new GsonBuilder().create();
                    ErrorModel errorModel = gson.fromJson(data, ErrorModel.class);
                    if (errorModel != null) {
                        if (errorModel.citrusErrors != null && !errorModel.citrusErrors.isEmpty()) {
                            ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                            if (citrusError != null && citrusError.code.equals("20021")) {
                                showDialog(citrusError.message);
                            } else
                                showDialog(citrusError.message);
                        } else if (errorModel.message != null)
                            showDialog(errorModel.message);
                    } else
                        showDialog("Please try again later.");
                }
            } else
                showDialog("Please try again later.");
        }
    }
}
