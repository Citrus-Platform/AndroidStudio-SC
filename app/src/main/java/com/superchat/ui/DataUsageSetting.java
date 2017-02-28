package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.helper.SuperGroupDownloadSettingsHandler;
import com.superchat.model.SuperGroupDownloadDataSettings;
import com.superchat.model.SuperGroupDownloadDataSettings_Common;
import com.superchat.model.SuperGroupDownloadDataSettings_Mobile;
import com.superchat.model.SuperGroupDownloadDataSettings_WIFI;
import com.superchat.model.SuperGroupDownloadDataSettings_connector;
import com.superchat.utils.UtilSetFont;

import static com.superchat.R.id.id_back;
import static com.superchat.ui.DataUsageSetting.PopupDialog.MOBILE;

/**
 * Created by lalitjoshi on 15/11/16.
 */

public class DataUsageSetting extends CustomAppCompatActivityViewImpl implements View.OnClickListener {

    private Context context = this;

    public enum PopupDialog {
        MOBILE("When using mobile data"),
        WIFI("When connected on Wi-Fi");

        private String title;

        PopupDialog(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    RelativeLayout mobileDataUsageButton;
    RelativeLayout wifiDataUsageButton;

    TextView mobileDataUsageLabel;
    TextView wifiDataUsageLabel;
    TextView id_back;

    SuperGroupDownloadSettingsHandler superGroupDownloadSettingsHandler;
    SuperGroupDownloadDataSettings settingDownloadData;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.data_usage_activity);

        UtilSetFont.setFontMainScreen(this);

        superGroupDownloadSettingsHandler = new SuperGroupDownloadSettingsHandler(context);
        settingDownloadData = superGroupDownloadSettingsHandler.getSGInfo();

        mobileDataUsageButton = (RelativeLayout) findViewById(R.id.mobileDataUsageButton);
        wifiDataUsageButton = (RelativeLayout) findViewById(R.id.wifiDataUsageButton);

        mobileDataUsageLabel = (TextView) findViewById(R.id.mobileDataUsageLabel);
        wifiDataUsageLabel = (TextView) findViewById(R.id.wifiDataUsageLabel);
        id_back = (TextView) findViewById(R.id.id_back);

        ////////////click listener
        mobileDataUsageButton.setOnClickListener(this);
        wifiDataUsageButton.setOnClickListener(this);
        id_back.setOnClickListener(this);

        refreshLabels();
    }

    private void refreshLabels() {
        try {
            SuperGroupDownloadDataSettings_connector objConnectorMobile = superGroupDownloadSettingsHandler.getMobileConnector();
            SuperGroupDownloadDataSettings_connector objConnectorWifi = superGroupDownloadSettingsHandler.getWIFIConnector();

            setDescText(mobileDataUsageLabel, objConnectorMobile);
            setDescText(wifiDataUsageLabel, objConnectorWifi);
        } catch (Exception e) {

        }
    }

    private void setDescText(TextView tvDesc, SuperGroupDownloadDataSettings_connector objConnector) {
        String desc = null;
        final String seperator = ", ";
        if (objConnector != null && tvDesc != null) {
            if (objConnector.isPhotoAllowed() && objConnector.isAudioAllowed() && objConnector.isVideoAllowed()) {
                desc = "All";
            } else if (objConnector.isPhotoAllowed() || objConnector.isAudioAllowed() || objConnector.isVideoAllowed()) {
                desc = "";
                if (objConnector.isPhotoAllowed()) {
                    desc = desc + "Photos";
                }
                if (objConnector.isAudioAllowed()) {
                    desc = desc + seperator + "Audio";
                }
                if (objConnector.isVideoAllowed()) {
                    desc = desc + seperator + "Videos";
                }
            } else {
                if (desc == null) {
                    desc = "None";
                }
            }
        }

        if (desc != null && desc.trim().startsWith(seperator)) {
            desc = desc.substring(desc.indexOf(seperator) + seperator.length());
        }

        tvDesc.setText((desc != null) ? desc : "");
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
            case R.id.mobileDataUsageButton:{
                showDialog(PopupDialog.MOBILE);
                break;
            }
            case R.id.wifiDataUsageButton:{
                showDialog(PopupDialog.WIFI);
                break;
            }
            case R.id.id_back:{
                finish();
                break;
            }
        }
    }

    CheckBox photoCheckBox;
    CheckBox audioCheckBox;
    CheckBox videoCheckBox;

    public void showDialog(PopupDialog popupDialog) {

        final Dialog settingNetworkDialog = new Dialog(this);
        settingNetworkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingNetworkDialog.setCanceledOnTouchOutside(false);
        settingNetworkDialog.setContentView(R.layout.data_usage_options);

        RelativeLayout rlPhotoLayout = (RelativeLayout) settingNetworkDialog.findViewById(R.id.rlPhotoLayout);
        RelativeLayout rlAudioLayout = (RelativeLayout) settingNetworkDialog.findViewById(R.id.rlAudioLayout);
        RelativeLayout rlVideoLayout = (RelativeLayout) settingNetworkDialog.findViewById(R.id.rlVideoLayout);

        photoCheckBox = (CheckBox) settingNetworkDialog.findViewById(R.id.photoCheckBox);
        audioCheckBox = (CheckBox) settingNetworkDialog.findViewById(R.id.audioCheckBox);
        videoCheckBox = (CheckBox) settingNetworkDialog.findViewById(R.id.videoCheckBox);

        TextView headerLabel = (TextView) settingNetworkDialog.findViewById(R.id.headerLabel);
        headerLabel.setText(popupDialog.getTitle());

        SuperGroupDownloadDataSettings_connector objConnector = null;
        switch (popupDialog) {
            case MOBILE: {
                objConnector = superGroupDownloadSettingsHandler.getMobileConnector();
                break;
            }
            case WIFI: {
                objConnector = superGroupDownloadSettingsHandler.getWIFIConnector();
                break;
            }
        }

        photoCheckBox.setChecked(objConnector.isPhotoAllowed());
        audioCheckBox.setChecked(objConnector.isAudioAllowed());
        videoCheckBox.setChecked(objConnector.isVideoAllowed());

        photoCheckBox.setOnCheckedChangeListener(new OnCheckChangeListener(popupDialog, objConnector));
        audioCheckBox.setOnCheckedChangeListener(new OnCheckChangeListener(popupDialog, objConnector));
        videoCheckBox.setOnCheckedChangeListener(new OnCheckChangeListener(popupDialog, objConnector));

        rlPhotoLayout.setOnClickListener(new OnClickCheckBoxLayout());
        rlAudioLayout.setOnClickListener(new OnClickCheckBoxLayout());
        rlVideoLayout.setOnClickListener(new OnClickCheckBoxLayout());

        ////////////////////////////////////////////////////////////////////
        (settingNetworkDialog.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingNetworkDialog.dismiss();
            }
        });
        (settingNetworkDialog.findViewById(R.id.okButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                superGroupDownloadSettingsHandler.saveSGInfo(settingDownloadData);
                refreshLabels();
                settingNetworkDialog.dismiss();
            }
        });

        settingNetworkDialog.show();
    }

    class OnClickCheckBoxLayout implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.rlPhotoLayout: {
                    photoCheckBox.toggle();
                    break;
                }
                case R.id.rlAudioLayout: {
                    audioCheckBox.toggle();
                    break;
                }
                case R.id.rlVideoLayout: {
                    videoCheckBox.toggle();
                    break;
                }
            }
        }
    }

    class OnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {

        PopupDialog popupDialog;
        SuperGroupDownloadDataSettings_connector objConnector;

        public OnCheckChangeListener(PopupDialog popupDialog, SuperGroupDownloadDataSettings_connector objConnector) {
            this.popupDialog = popupDialog;
            this.objConnector = objConnector;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int viewId = compoundButton.getId();
            switch (viewId) {
                case R.id.photoCheckBox: {
                    objConnector.setPhotoAllowed(isChecked);
                    break;
                }
                case R.id.audioCheckBox: {
                    objConnector.setAudioAllowed(isChecked);
                    break;
                }
                case R.id.videoCheckBox: {
                    objConnector.setVideoAllowed(isChecked);
                    break;
                }
            }

            switch (popupDialog) {
                case MOBILE: {
                    settingDownloadData.setModileSettings(getObjectFromSettingConnector(objConnector));
                    break;
                }
                case WIFI: {
                    settingDownloadData.setWifiSettings(getObjectFromSettingConnector(objConnector));
                    break;
                }
            }
        }
    }

    private SuperGroupDownloadDataSettings_Common getObjectFromSettingConnector(SuperGroupDownloadDataSettings_connector objConnector) {
        SuperGroupDownloadDataSettings_Common object = new SuperGroupDownloadDataSettings_Common();
        try {
            object.setPhotoAllowed(objConnector.isPhotoAllowed());
            object.setAudioAllowed(objConnector.isAudioAllowed());
            object.setVideoAllowed(objConnector.isVideoAllowed());
        } catch (Exception e) {

        }

        return object;
    }
}
