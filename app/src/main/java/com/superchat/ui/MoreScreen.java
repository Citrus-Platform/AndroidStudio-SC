package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.helper.SuperGroupDownloadSettingsHandler;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.http.HEAD;

import static android.R.attr.mode;
import static com.superchat.R.id.switchBroadcast;

public class MoreScreen extends Activity implements OnClickListener {

    RelativeLayout profileIconLayout;
    //	RelativeLayout sharedID;
    RelativeLayout advancedSettings;
    //	LinearLayout accountIconLayout;
//	LinearLayout helpIconLayout;
    RelativeLayout aboutIconLayout;
    RelativeLayout blockListLayout;
    RelativeLayout feedbackLayout;
    RelativeLayout consolePassLayout;
    RelativeLayout sgProfileLayout;
    RelativeLayout checkUpdateLayout;
    RelativeLayout soonzeLayout;
    RelativeLayout id_data_usage_layout;
    RelativeLayout officialIDLayout;
    RelativeLayout memberStatsLayout;
    RelativeLayout memberManageLayout;
    //	RelativeLayout memberStatsLayout;
    RelativeLayout privacyLayout;
    TextView myNameView;
    TextView muteSettingText;
    TextView aboutSuperGroupView;
    Switch switchSaveGalleryMedia;
    //	RoundedImageView profileIconView;
    SharedPrefManager sharedPrefManager;
    SuperGroupDownloadSettingsHandler superGroupDownloadSettingsHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.more_screen);
        profileIconLayout = (RelativeLayout) findViewById(R.id.id_profile_layout);
//		advancedSettings = (RelativeLayout)findViewById(R.id.id_advanced_settings);
//		sharedID = (RelativeLayout)findViewById(R.id.id_shared);
//		accountIconLayout = (LinearLayout)view.findViewById(R.id.id_account_layout);
//		settingIconLayout = (LinearLayout)view.findViewById(R.id.id_setting_layout);
        myNameView = (TextView) findViewById(R.id.id_my_name);
        aboutSuperGroupView = (TextView) findViewById(R.id.id_about_supergroup);
        aboutIconLayout = (RelativeLayout) findViewById(R.id.id_about_us);
        blockListLayout = (RelativeLayout) findViewById(R.id.id_block_list_layout);
        feedbackLayout = (RelativeLayout) findViewById(R.id.id_feedback_layout);
        consolePassLayout = (RelativeLayout) findViewById(R.id.id_configure_console_pass);
        sgProfileLayout = (RelativeLayout) findViewById(R.id.id_supergroup_profile);
        privacyLayout = (RelativeLayout) findViewById(R.id.id_privacy_layout);

        checkUpdateLayout = (RelativeLayout) findViewById(R.id.id_checkupdate_layout);
        soonzeLayout = (RelativeLayout) findViewById(R.id.id_snooze_layout);
        muteSettingText = (TextView) findViewById(R.id.mute_setting_txt);
        id_data_usage_layout = (RelativeLayout) findViewById(R.id.id_data_usage_layout);
        switchSaveGalleryMedia = (Switch) findViewById(R.id.switchSaveGalleryMedia);

        superGroupDownloadSettingsHandler = new SuperGroupDownloadSettingsHandler(this);

        boolean isSaveInGallery = superGroupDownloadSettingsHandler.isSavedInGallery();
        switchSaveGalleryMedia.setChecked(isSaveInGallery);

        switchSaveGalleryMedia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                superGroupDownloadSettingsHandler.changeMediaGallerySetting(isChecked);
            }
        });

        officialIDLayout = (RelativeLayout) findViewById(R.id.id_offocial_id_layout);
        memberStatsLayout = (RelativeLayout) findViewById(R.id.id_memberstats_layout);
        privacyLayout.setOnClickListener(this);
        id_data_usage_layout.setOnClickListener(this);
        soonzeLayout.setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.id_chat_layout)).setOnClickListener(this);
        sharedPrefManager = SharedPrefManager.getInstance();
        myNameView.setText(sharedPrefManager.getDisplayName());
        aboutSuperGroupView.setText("About " + sharedPrefManager.getCurrentSGDisplayName());
        if (sharedPrefManager.isDomainAdmin(sharedPrefManager.getUserDomain()) || sharedPrefManager.isDomainSubAdmin(sharedPrefManager.getUserDomain())) {
//			memberStatsLayout = (RelativeLayout)findViewById(R.id.id_member_stats_layout);
            advancedSettings = (RelativeLayout) findViewById(R.id.id_advanced_settings);
            memberManageLayout = (RelativeLayout) findViewById(R.id.id_manage_members_layout);
//			memberStatsLayout.setVisibility(View.VISIBLE);
            memberManageLayout.setVisibility(View.VISIBLE);
//            advancedSettings.setVisibility(View.VISIBLE);
            officialIDLayout.setVisibility(View.VISIBLE);
            memberStatsLayout.setVisibility(View.VISIBLE);
//			consolePassLayout.setVisibility(View.VISIBLE);
            advancedSettings.setOnClickListener(this);
            officialIDLayout.setOnClickListener(this);
            memberStatsLayout.setOnClickListener(this);
            memberManageLayout.setOnClickListener(this);
        }
        if(sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain()) > 0){
            String time = (getResources().getStringArray(R.array.snooze_list_array))[sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain())];
            muteSettingText.setVisibility(View.VISIBLE);
            muteSettingText.setText(time);
        }
        ((ImageView) findViewById(R.id.id_profile_pic)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String file_path = getImagePath(SharedPrefManager.getInstance().getUserFileId(sharedPrefManager.getUserName()));
                if (file_path == null || (file_path != null && file_path.trim().length() == 0))
                    file_path = getImagePath(null);

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
//		profileIconView = (RoundedImageView)view.findViewById(R.id.id_profile_icon);
        ((TextView) findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        ImageView xmppStatusView = (ImageView) findViewById(R.id.id_xmpp_status);

        if (ChatService.xmppConectionStatus) {
            xmppStatusView.setImageResource(R.drawable.blue_dot);
        } else {
            xmppStatusView.setImageResource(R.drawable.red_dot);
        }

//		settingIconLayout.setOnClickListener(this);
        blockListLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        consolePassLayout.setOnClickListener(this);
        profileIconLayout.setOnClickListener(this);
//		sharedID.setOnClickListener(this);
//		accountIconLayout.setOnClickListener(this);
        sgProfileLayout.setVisibility(View.VISIBLE);
        sgProfileLayout.setOnClickListener(this);
        checkUpdateLayout.setOnClickListener(this);
        aboutIconLayout.setOnClickListener(this);

//		Tracker t = ((SuperChatApplication) SuperChatApplication.context).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("More Options Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
//		setProfilePic(sharedPrefManager.getUserName());
    }

    private boolean setProfilePic(String userName) {
        String picId = SharedPrefManager.getInstance().getUserFileId(userName);

        String img_path = getImagePath(picId);
//		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        ImageView picView = (ImageView) findViewById(R.id.id_profile_pic);
//		if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//			picView.setImageResource(R.drawable.female_default);
//		else
        picView.setImageResource(R.drawable.profile_pic);
        if (picId == null)
            return false;
//		if (bitmap != null) {
//			picView.setImageBitmap(bitmap);
//			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
//			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
//			picView.setTag(filename);
//			return true;
//		}else
        if (img_path != null) {
            File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (file1.exists()) {
                picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
                setThumb((ImageView) picView, img_path, picId);
                return true;
            }
        } else {
            img_path = getThumbPath(picId);
            if (img_path != null) {
                File file1 = new File(img_path);
                //			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
                if (file1.exists()) {
                    picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    //				picView.setImageURI(Uri.parse(img_path));
                    setThumb((ImageView) picView, img_path, picId);
                }
            }
        }
        if (picId != null && picId.equals("clear"))
            return true;
        return false;
    }

    private String getImagePath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            File contentFile = new File(filename);
            if (contentFile != null && contentFile.exists()) {
                return filename;
            }
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

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		setProfilePic();
        if (myNameView != null)
            myNameView.setText(sharedPrefManager.getDisplayName());
        setProfilePic(sharedPrefManager.getUserName());
        if (aboutSuperGroupView != null)
            aboutSuperGroupView.setText("About " + sharedPrefManager.getCurrentSGDisplayName());
    }

    //	public void onActivityCreated(Bundle bundle) {
//		super.onActivityCreated(bundle);
//	}
    Dialog privacyOptionDialog;

    private void showPrivacyOptionsDialog(boolean isShow) {
        if (privacyOptionDialog == null) {
            privacyOptionDialog = new Dialog(this);
            privacyOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            privacyOptionDialog.setContentView(R.layout.privacy_options_dialog);
            privacyOptionDialog.findViewById(R.id.id_chat_settings).setOnClickListener(this);
            privacyOptionDialog.findViewById(R.id.id_call_settings).setOnClickListener(this);
            privacyOptionDialog.findViewById(R.id.id_block_list).setOnClickListener(this);
        }
        if (privacyOptionDialog != null && !privacyOptionDialog.isShowing() && isShow)
            privacyOptionDialog.show();
        if (privacyOptionDialog != null && privacyOptionDialog.isShowing() && !isShow)
            privacyOptionDialog.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_offocial_id_layout:
                Intent intent = new Intent(MoreScreen.this, SharedIDScreen.class);
                startActivity(intent);
                break;
            case R.id.id_memberstats_layout:
                intent = new Intent(MoreScreen.this, MemberStatsScreen.class);
                startActivity(intent);
                break;
            case R.id.id_data_usage_layout: {
                intent = new Intent(this, DataUsageSetting.class);
                startActivity(intent);
                break;
            }
            case R.id.id_chat_layout:
                intent = new Intent(this, ChatBackupScreen.class);
                startActivity(intent);
                break;
            case R.id.id_configure_console_pass:
                intent = new Intent(this, GenerateConsolePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.id_privacy_layout:
                showPrivacyOptionsDialog(true);
                break;
            case R.id.id_snooze_layout:
//                boolean expired = sharedPrefManager.isSnoozeExpired(sharedPrefManager.getUserDomain());
//                int index = sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain());
//                if(expired && sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain()) == 0) {
//                    showSnoozeDialog();
//                }else{
//                    showUpdateDialog("", "Un-mute?");
//                }
                showSnoozeDialog();
                break;
//		case R.id.id_member_stats_layout:
//			Intent intent = new Intent(this, MemberStatsScreen.class);
//			startActivity(intent);
//			break;
            case R.id.id_supergroup_profile:
                intent = new Intent(this, SuperGroupProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.id_manage_members_layout:
                intent = new Intent(this, EsiaChatContactsScreen.class);
                intent.putExtra(Constants.CHAT_TYPE, Constants.MEMBER_DELETE);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 105);
                break;
            case R.id.id_feedback_layout:
                Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
                mailIntent.setType("text/plain");
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@citrusplatform.com"});
                mailIntent.putExtra(Intent.EXTRA_TEXT, Utilities.getSupportParameters(this));
                final PackageManager pm = getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") ||
                            info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                if (best != null)
                    mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                startActivity(mailIntent);
                break;
            case R.id.id_profile_layout:
                intent = new Intent(this, ProfileScreen.class);
                intent.putExtra(Constants.PROFILE_UPDATE, true);
                startActivity(intent);
//			finish();
                break;
            case R.id.id_advanced_settings:
                showAdvancedSettingsDialog();
//			intent = new Intent(this, CreateSharedIDActivity.class);
//			intent = new Intent(this, SharedIDScreen.class);
//			startActivity(intent);
                break;
//		case R.id.id_setting_layout:
//			Toast.makeText(getActivity(), getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.id_account_layout:
//			intent = new Intent(getActivity(), AccountScreen.class);
//			startActivity(intent);
//			break;
//		case R.id.id_invite_layout:
//			//do invite here
//			try {
//				String text = SuperChatApplication.context.getResources().getString(R.string.invite_text);
//				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//				shareIntent.setType("text/plain");
//				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
//				startActivity(Intent.createChooser(shareIntent, SuperChatApplication.context.getString(R.string.invite)));
//			} catch (Exception e) {
//				//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
//			}  
//			intent = new Intent(this, BulkInvitationScreen.class);
//			startActivity(intent);
//			break;
//		case R.id.id_help_item:
//			intent = new Intent(getActivity(), HelpScreen.class);
//			startActivity(intent);
//			break;
            case R.id.id_chat_settings:
                showPrivacyOptionsDialog(false);
                intent = new Intent(this, PrivacyChatSettings.class);
                startActivity(intent);
                break;
            case R.id.id_call_settings:
                showPrivacyOptionsDialog(false);
                intent = new Intent(this, PrivacyCallSettings.class);
                startActivity(intent);
                break;
            case R.id.id_block_list://id_block_list_layout:
                showPrivacyOptionsDialog(false);
                intent = new Intent(this, BlockListScreen.class);
                startActivity(intent);
                break;
            case R.id.id_about_us:
                intent = new Intent(this, AboutScreen.class);
                startActivity(intent);
                break;
            case R.id.id_checkupdate_layout:
                if (Build.VERSION.SDK_INT >= 11)
                    new GetVersionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new GetVersionTask().execute();
                break;
        }
    }

    Dialog advancedSettingsDialog = null;

    private void showAdvancedSettingsDialog() {
        try {
            // TODO Auto-generated method stub
            advancedSettingsDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
            advancedSettingsDialog.setCanceledOnTouchOutside(false);
            advancedSettingsDialog.setContentView(R.layout.settings_advanced);

            RelativeLayout sharedID = (RelativeLayout) advancedSettingsDialog.findViewById(R.id.id_shared);
            RelativeLayout memberStatsLayout = (RelativeLayout) advancedSettingsDialog.findViewById(R.id.id_member_stats_layout);

            sharedID.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(MoreScreen.this, SharedIDScreen.class);
                    startActivity(intent);
//					advancedSettingsDialog.dismiss();
                }
            });
            memberStatsLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(MoreScreen.this, MemberStatsScreen.class);
                    startActivity(intent);
//					advancedSettingsDialog.dismiss();
                }
            });
            (advancedSettingsDialog.findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    advancedSettingsDialog.dismiss();
                }
            });
            advancedSettingsDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setProfilePic() {
        if (ProfileScreen.oldImageFileId != null) {
            String currentFileId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
            if (currentFileId != null && !ProfileScreen.oldImageFileId.equals(currentFileId)) {
                SharedPrefManager.getInstance().saveUserFileId(SharedPrefManager.getInstance().getUserName(), ProfileScreen.oldImageFileId);
            }
        }
        String groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        if (groupPicId != null && !groupPicId.equals("clear")) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
//		if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
//			profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            File file1 = new File(filename);
//		Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (bitmap != null) {
//			profileIconView.setImageBitmap(bitmap);
            } else if (file1.exists()) {
//			profileIconView.setImageURI(Uri.parse(filename));
//			profileIconView.setBackgroundDrawable(null);

//			 Bitmap myBitmap = BitmapFactory.decodeFile(filename);
//			    Drawable drawable = new BitmapDrawable(getResources(), myBitmap);
//			    profileIconView.setBackgroundDrawable(drawable);
            }
        } else {
//		profileIconView.setImageDrawable(null);
//		profileIconView.setBackgroundResource(R.drawable.about_icon);
        }
    }

    public void showSnoozeDialog() {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.snooze_settings);
        TextView btn = ((TextView) bteldialog.findViewById(R.id.id_set_btn));
        final Spinner spinner = (Spinner) bteldialog.findViewById(R.id.spinner);
        sharedPrefManager.isSnoozeExpired(sharedPrefManager.getUserDomain());
        spinner.setSelection(sharedPrefManager.getSnoozeIndex(sharedPrefManager.getUserDomain()));
        btn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                sharedPrefManager.setSnoozeIndex(sharedPrefManager.getUserDomain(), spinner.getSelectedItemPosition());
                sharedPrefManager.setSnoozeStartTime(sharedPrefManager.getUserDomain(), System.currentTimeMillis());
                muteSettingText.setVisibility(View.VISIBLE);
                muteSettingText.setText(String.valueOf(spinner.getSelectedItem()));
//                Toast.makeText(MoreScreen.this, String.valueOf(spinner.getSelectedItem()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        TextView cancelBtn = ((TextView) bteldialog.findViewById(R.id.id_cancel));
        cancelBtn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        if (!bteldialog.isShowing())
            bteldialog.show();
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

    private class GetVersionTask extends AsyncTask<String, String, String> {
        public final Pattern CURRENT_VERSION_REGEX = Pattern.compile(".*?softwareVersion.*?[>](.*?)[<][/]div.*?");
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MoreScreen.this, "", "Checking. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... voids) {
            String version = null;
            try {
                version = getData("https://play.google.com/store/apps/details?id=com.superchat");
                if (version != null && !version.equals("")) {
                    version = getCurrentVersion(version);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return version;
        }

        public String getCurrentVersion(String data) {

            data = data.replaceAll("\\s+", " ");

            Matcher m = CURRENT_VERSION_REGEX.matcher(data);

            if (m.matches()) {

                String matcher = m.group(1);
                System.out.println(matcher);
                return matcher.trim();
            }

            return null;

        }


        public String getData(String urlPath) {
            try {
                URL url = new URL(urlPath);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//    	   urlConnection.setSSLSocketFactory(getSocketFactory());
                urlConnection.connect();
                java.io.InputStream input = urlConnection.getInputStream();

                int count;

//            java.io.InputStream input = conection.getInputStream();
                // getting file length
                int lenghtOfFile = urlConnection.getContentLength();
                // input stream to read file - with 8k buffer
//			  java.io.InputStream input = new BufferedInputStream(urlConnection.openStream());
                // Output stream to write file
                byte data[] = new byte[4096];
                long total = 0;
                StringBuilder sb = new StringBuilder();
                while ((count = input.read(data)) != -1) {


//            while (input.read(b) > 0) {

                    sb.append(new String(data, Charset.forName("utf-8")));

                }

                input.close();

                return sb.toString();
            } catch (Throwable e) {
                e.printStackTrace();
                Log.d("MoreScreen", "Error throwing in version fetching from play store." + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            String currentVersion = getCurrentAppVersion();
            SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (!currentVersion.equals(onlineVersion)) {
//	            	Toast.makeText(MoreScreen.this, "Updates is available.", Toast.LENGTH_LONG).show();
                    showUpdateDialog("Update Available!", "Do you want to update \"SuperChat\"?");
                } else {
                    iPrefManager.setUpdateCheck(true);
                    Toast.makeText(MoreScreen.this, "No updates available.", Toast.LENGTH_SHORT).show();
                }
            }
//	        Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }

    public void showUpdateDialog(final String title, final String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Update Now");
        ((TextView) bteldialog.findViewById(R.id.id_cancel)).setText("Later");
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.superchat")));
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=com.superchat"));
                    startActivity(intent);

                } catch (Exception anfe) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.superchat"));
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                }
                return false;
            }
        });
        ((TextView) bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    private String getCurrentAppVersion() {
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (version != null)
                version = version.trim();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    //-------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String message) {
        showDialogWithPositive(message);
//        showAlertDialog(message);
    }

    Dialog systemMessageDialog;

    public void showDialogWithPositive(String s) {
        if (systemMessageDialog != null)
            systemMessageDialog.dismiss();
        systemMessageDialog = new Dialog(MoreScreen.this);
        try {
            systemMessageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            systemMessageDialog.setCanceledOnTouchOutside(false);
//            dailog.setContentView(R.layout.custom_dialog);
            systemMessageDialog.setContentView(R.layout.system_message_dialog);
            ((TextView) systemMessageDialog.findViewById(R.id.id_dialog_message)).setText(s);
            ((TextView) systemMessageDialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    systemMessageDialog.cancel();
                    return false;
                }
            });
            systemMessageDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
