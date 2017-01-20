package com.superchat.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.interfaces.interfaceInstances;
import com.superchat.model.ErrorModel;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.BulletinPicUploader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by lalitjoshi on 08/11/16.
 */

    public class EditBulletinBoard extends AppCompatActivity implements View.OnClickListener, interfaceInstances {

    TextView back_button;
    TextView edit_button;
    ImageView id_edit_pic;
    ImageView bulletin_image;
    ImageView bulletin_default_image;
    TextView id_done_btn_bottom;

    Dialog picChooserDialog;
    ProgressDialog progressDialog;
    SharedPrefManager iPrefManager;

    boolean isForground;
    public static String oldImageFileId;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    private static final String TAG = "HubProfileActivity";
    private ChatService messageService;

    private ServiceConnection mMessageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            messageService = ((ChatService.MyBinder) binder).getService();
            com.superchat.utils.Log.d("Service", "Connected");
            // xmppConnection = messageService.getconnection();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // xmppConnection = null;
            messageService = null;
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_bulletin_screen);

        UtilSetFont.setFontMainScreen(this);

        oldImageFileId = null;
        iPrefManager = SharedPrefManager.getInstance().getInstance();
        mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
                .endConfig()
                .round();

        back_button = (TextView) findViewById(R.id.back_button);
        edit_button = (TextView) findViewById(R.id.edit_button);
        id_edit_pic = (ImageView) findViewById(R.id.id_edit_pic);
        bulletin_image = (ImageView) findViewById(R.id.bulletin_image);
        bulletin_default_image = (ImageView) findViewById(R.id.bulletin_default_image);
        id_done_btn_bottom = (TextView) findViewById(R.id.id_done_btn_bottom);

        back_button.setOnClickListener(this);
        edit_button.setOnClickListener(this);
        id_edit_pic.setOnClickListener(this);
        id_done_btn_bottom.setOnClickListener(this);

        /*setProfilePic(bulletin_image, bulletin_default_image,
                "", SharedPrefManager.getInstance().getUserDomain());*/

    }

    public void onResume() {
        super.onResume();
        isForground = true;
        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
    }

    public void onPause() {
        super.onPause();
        isForground = false;
        unbindService(mMessageConnection);
    }

    @Override
    public void onClick(View view) {
        if (view == back_button) {
            finish();
        }
        if (view == edit_button || view == id_edit_pic) {

            picChooserDialog = new Dialog(this);
            picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
            picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
            picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
            picChooserDialog.findViewById(R.id.id_remove).setOnClickListener(this);

            picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
            picChooserDialog.show();

            id_done_btn_bottom.setVisibility(View.VISIBLE);
            edit_button.setVisibility(View.GONE);

        }
        if (view == bulletin_image) {

        }

        if (view.getId() == R.id.id_camera) {
            AppUtil.clearAppData();
            AppUtil.openCamera(this, AppUtil.capturedPath1, AppUtil.POSITION_CAMERA_PICTURE);
            picChooserDialog.cancel();
        }
        if (view.getId() == R.id.id_gallery) {
            AppUtil.clearAppData();
            AppUtil.openImageGallery(this, AppUtil.POSITION_GALLRY_PICTURE);
            picChooserDialog.cancel();
        }

        if (view == id_done_btn_bottom) {

            String fileid = SharedPrefManager.getInstance().getSGFileId(iPrefManager.getUserDomain());
            String bulletin_field_id = SharedPrefManager.getInstance().getBulletin_File_Id(iPrefManager.getUserDomain());

            JSONObject finalJSONbject = new JSONObject();

            String sg_display_name = iPrefManager.getDisplayName();
            String sg_domain_name = iPrefManager.getUserDomain();
            String sg_description = iPrefManager.getDisplayName();
            String sg_org_name = iPrefManager.getUserOrgName();
            String sg_org_url = iPrefManager.getDisplayName();

            try {
                if (sg_display_name != null && sg_display_name.trim().length() > 0) {
                    finalJSONbject.put("displayName", sg_display_name);
                }
                if (sg_domain_name != null && sg_domain_name.trim().length() > 0)
                    finalJSONbject.put("domainName", sg_domain_name.trim());

                if (sg_description != null)// && sgDescriptionBox.getText().toString().trim().length() > 0)
                    finalJSONbject.put("description", sg_description);

                if (sg_org_name != null)// && orgNameBox.getText().toString().trim().length() > 0)
                    finalJSONbject.put("orgName", sg_org_name.trim());

                if (sg_org_url != null)// && orgURLBox.getText().toString().trim().length() > 0)
                    finalJSONbject.put("orgUrl", sg_org_url.trim());

                if (fileid != null && fileid.length() > 0)
                    finalJSONbject.put("logoFileId", fileid);

                if (bulletin_field_id != null)
                    finalJSONbject.put("bulletinFileId", bulletin_field_id);

            } catch (JSONException ex) {

            }
            if (Build.VERSION.SDK_INT >= 11)
                new UpdateSuperGroupTask(finalJSONbject, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                new UpdateSuperGroupTask(finalJSONbject, null).execute();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == HomeScreen.RESULT_OK)
                switch (requestCode) {
                    case AppUtil.POSITION_CAMERA_PICTURE:
                    case AppUtil.POSITION_GALLRY_PICTURE:
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
                        }
                        CompressImage compressImage = new CompressImage(this);
                        AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                        performCrop(AppUtil.PIC_CROP);
                        break;
                    case AppUtil.PIC_CROP:
                        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.contentTemp + "/"
                                + AppUtil.TEMP_PHOTO_FILE;

                        AppUtil.capturedPath1 = filePath;
                        Bitmap selectedImage = BitmapFactory.decodeFile(AppUtil.capturedPath1);
                        ((ImageView) findViewById(R.id.bulletin_image)).setImageBitmap(selectedImage);
                        ((ImageView) findViewById(R.id.bulletin_image)).setScaleType(ImageView.ScaleType.CENTER_CROP);
                        // ((ImageView)
                        // findViewById(R.id.id_profile_pic)).setImageURI(Uri.parse(filePath));
                        // ((ImageView)
                        // findViewById(R.id.id_profile_pic)).setScaleType(ImageView.ScaleType.CENTER_CROP);
                        sendProfilePictureMessage(AppUtil.capturedPath1);
                        break;
                }
            else
                id_done_btn_bottom.setVisibility(View.VISIBLE);
                edit_button.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private void performCrop(byte resultCode) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File file = new File(AppUtil.capturedPath1);
            Uri outputFileUri = Uri.fromFile(file);
            // System.out.println("----outputFileUri:" + outputFileUri);
            cropIntent.setDataAndType(outputFileUri, "image/*");
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            try {
                cropIntent.putExtra("return-data", false);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
                cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(cropIntent, resultCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private boolean setProfilePic(ImageView picView, String groupPicId) {
        if (groupPicId == null || groupPicId != null && (groupPicId.equals("") || groupPicId.equals("clear") || groupPicId.contains("logofileid")))
            return false;
        String img_path = getImagePath(groupPicId);
        Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        picView.setImageResource(R.drawable.about_icon);
        if (bitmap != null) {
            picView.setImageBitmap(bitmap);
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            picView.setTag(filename);
            return true;
        } else if (img_path != null) {
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
        }
        if (groupPicId != null && groupPicId.equals("clear"))
            return true;
        return false;
    }

    private String getImagePath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";
            File file = Environment.getExternalStorageDirectory();
            return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
//			return Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+groupPicId+".jpg";
        }
        return null;
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

    public void sendProfilePictureMessage(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                String thumbImg = null;
                // if (messageService != null){
                // chatAdapter.setChatService(messageService);
                // thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
                // packetID = messageService.sendMediaMessage(senderName, "",
                // imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
                oldImageFileId = iPrefManager.getBulletin_File_Id(iPrefManager.getUserName());
                new BulletinPicUploader(this, null, true, notifyPhotoUploadHandler).execute(imgPath, packetID, "",
                        Message.XMPPMessageType.atMeXmppMessageTypeImage.name(), iPrefManager.getUserName());
                // }
            } catch (Exception ex) {
                //showDialog(getString(R.string.failed)), getString(R.string.photo_upload_failed));
                // System.out.println(""+ex.toString());
            }
        }
    }

    private final Handler notifyPhotoUploadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // if (isForground)
            //showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
        }
    };

    public void progress() {
        progressDialog = ProgressDialog.show(EditBulletinBoard.this, "", "Updating. Please wait...", true);
    }

    public class UpdateSuperGroupTask extends AsyncTask<String, String, String> {
        JSONObject requestJSON;
        ProgressDialog progressDialog = null;
        View view1;

        public UpdateSuperGroupTask(JSONObject requestJSON, final View view1) {
            this.requestJSON = requestJSON;
            this.view1 = view1;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(EditBulletinBoard.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(registrationForm);
            DefaultHttpClient client1 = new DefaultHttpClient();
            String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/update";
            HttpPost httpPost = new HttpPost(url);
            Log.i(TAG, "SignupTaskForAdmin :: doInBackground:  url:" + url);
            System.out.println("Request :: " + requestJSON.toString());
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
            HttpResponse response = null;
            try {
                httpPost.setEntity(new StringEntity(requestJSON.toString()));
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String line = "";
                        String str = "";
                        while ((line = rd.readLine()) != null) {
                            str += line;
                        }
                        return str;
//						if(str!=null &&!str.equals("")){
//							str = str.trim();
//							Gson gson = new GsonBuilder().create();
//							if (str==null || str.contains("error")){
//								return str;
//							}
//						}
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
            System.out.println("Onpost str :: " + str);
            if (str != null && str.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = gson.fromJson(str, ErrorModel.class);
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null && citrusError.code.equals("20019")) {
                            //showDialog(citrusError.message);
                        } else {
                            if (citrusError.message != null && citrusError.message.contains("no data to update")) {
                                backToBulletinView();
                            } else {
                                //showDialog(citrusError.message);
                            }
                        }
                    } else if (errorModel.message != null) {
                        showDialog(errorModel.message);
                    }
                } else {
                    showDialog("Please try again later.");
                }
            } else {
                SharedPrefManager prefManager = SharedPrefManager.getInstance();
                prefManager.saveCurrentSGDisplayName(iPrefManager.getCurrentSGDisplayName());

                //backToBulletinView();
                //Braodcast message to all
                JSONObject finalJSONbject = new JSONObject();
                // if (superGroupDisplayName != null)
                try {
                    if (prefManager.getBulletin_File_Id(prefManager.getUserDomain()) != null)
                        finalJSONbject.put("bulletinFileId", prefManager.getBulletin_File_Id(prefManager.getUserDomain()));

                    String file = prefManager.getBulletin_File_Id(prefManager.getUserDomain());
                    String json_txt = finalJSONbject.toString();

                    messageService.sendSpecialMessageToAllDomainMembers(
                            prefManager.getUserDomain() + "-system", json_txt,
                            Message.XMPPMessageType.atMeXmppMessageTypeUpdateBulletin);
                    json_txt = null;

                    backToBulletinView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Show toast
                try {
                    JSONObject json = new JSONObject(str);
                    if (json != null && json.has("message")) {
                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            super.onPostExecute(str);
        }
    }

    public void backToBulletinView() {

        /*Intent intent = new Intent();
        intent.putExtra("bulletin_image_update" , "yes");
        setResult(500, intent);*/
        finish();

    }

    public void showDialog(String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    public void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName) {
        //String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02

        String groupPicId = SharedPrefManager.getInstance().getBulletin_File_Id(userName);
        Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        if (bitmap != null) {
            view_default.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(bitmap);
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            view.setTag(filename);
        } else if (groupPicId != null && !groupPicId.equals("")) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            view.setTag(filename);
            File file1 = new File(filename);
            if (file1.exists()) {
//				view.setImageURI(Uri.parse(filename));
                com.superchat.utils.Log.i("", "setProfilePic : filename : " + filename);
                view_default.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
                setThumb(view, filename, groupPicId);
//				view.setBackgroundDrawable(null);

            } else {
                //Downloading the file
                view_default.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
                if (iPrefManager.isGroupChat(userName))
                    view.setImageResource(R.drawable.group_icon);
                else if (!iPrefManager.isBroadCast(userName)) {
                    view.setImageResource(R.drawable.avatar);
                }

                (new ProfilePicDownloader()).download(Constants.media_get_url + groupPicId + ".jpg", (RoundedImageView) view, null);
            }
        } else if (iPrefManager.isBroadCast(userName)) {
            view_default.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.announce);
            view.setTag(userName);
        } else if (iPrefManager.isGroupChat(userName)) {
            view_default.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.chat_person);
            view.setTag(userName);
        } else {
//			if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//				view.setImageResource(R.drawable.female_default);
//			else
//				view.setImageResource(R.drawable.male_default);
            try {
                String name_alpha = String.valueOf(displayName.charAt(0));
                if (displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
                    name_alpha += displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
                TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
                view.setVisibility(View.INVISIBLE);
                view_default.setVisibility(View.VISIBLE);
                view_default.setImageDrawable(drawable);
                view_default.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
