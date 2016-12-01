package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

/**
 * Created by lalitjoshi on 15/11/16.
 */

public class DataUsageSetting extends Activity implements View.OnClickListener {

    RelativeLayout mobileDataUsageButton;
    RelativeLayout wifiDataUsageButton;

    TextView headerLabel;

    boolean photoCheckedFlag = false;
    boolean audioCheckedFlag = false;
    boolean videoCheckedFlag = false;

    String mobileDataUsageText = "";
    String wifiDataUsageText = "";

    TextView mobileDataUsageLabel;
    TextView wifiDataUsageLabel;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.data_usage_activity);

        mobileDataUsageButton = (RelativeLayout) findViewById(R.id.mobileDataUsageButton);
        wifiDataUsageButton = (RelativeLayout) findViewById(R.id.wifiDataUsageButton);

        mobileDataUsageLabel = (TextView) findViewById(R.id.mobileDataUsageLabel);
        wifiDataUsageLabel = (TextView) findViewById(R.id.wifiDataUsageLabel);

        ////////////click listener
        mobileDataUsageButton.setOnClickListener(this);
        wifiDataUsageButton.setOnClickListener(this);

        if (SharedPrefManager.getInstance().isDataUsageMobile() != null)
            mobileDataUsageLabel.setText("" + SharedPrefManager.getInstance().isDataUsageMobile());
        else
            mobileDataUsageLabel.setText("No media");

        if (SharedPrefManager.getInstance().isDataUsageWifi() != null)
            wifiDataUsageLabel.setText("" + SharedPrefManager.getInstance().isDataUsageWifi());
        else
            wifiDataUsageLabel.setText("No media");

    }

    @Override
    public void onClick(View view) {
        if (view == mobileDataUsageButton) {

            showDialog("When using mobile data", "mobile");
        }
        if (view == wifiDataUsageButton) {

            showDialog("When connected on Wi-Fi", "wifi");
        }

    }

    public void showDialog(String title, final String from) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.data_usage_options);


        ((TextView) bteldialog.findViewById(R.id.headerLabel)).setText(title);
        final CheckBox photoCheckBox = (CheckBox) bteldialog.findViewById(R.id.photoCheckBox);
        final CheckBox audioCheckBox = (CheckBox) bteldialog.findViewById(R.id.audioCheckBox);
        final CheckBox videoCheckBox = (CheckBox) bteldialog.findViewById(R.id.videoCheckBox);

        if (from.equalsIgnoreCase("mobile")) {

            if (SharedPrefManager.getInstance().isDataUsageMobile() != null &&
                    SharedPrefManager.getInstance().isDataUsageMobile().length() > 2) {

                if (SharedPrefManager.getInstance().isDataUsageMobile().contains("photo")) {
                    photoCheckBox.setChecked(true);
                    photoCheckedFlag = true;
                } else {
                    photoCheckBox.setChecked(false);
                    photoCheckedFlag = false;
                }

                if (SharedPrefManager.getInstance().isDataUsageMobile().contains("audio")) {
                    audioCheckBox.setChecked(true);
                    audioCheckedFlag = true;
                } else {
                    audioCheckBox.setChecked(false);
                    audioCheckedFlag = false;
                }
                if (SharedPrefManager.getInstance().isDataUsageMobile().contains("video")) {
                    videoCheckBox.setChecked(true);
                    videoCheckedFlag = true;
                } else {
                    videoCheckBox.setChecked(false);
                    videoCheckedFlag = false;
                }
            }
        } else {

            if (SharedPrefManager.getInstance().isDataUsageWifi() != null &&
                    SharedPrefManager.getInstance().isDataUsageWifi().length() > 2) {

                if (SharedPrefManager.getInstance().isDataUsageWifi().contains("photo,")) {
                    photoCheckBox.setChecked(true);
                    photoCheckedFlag = true;
                } else {
                    photoCheckBox.setChecked(false);
                    photoCheckedFlag = false;
                }
                if (SharedPrefManager.getInstance().isDataUsageWifi().contains("audio,")) {
                    audioCheckBox.setChecked(true);
                    audioCheckedFlag = true;
                } else {
                    audioCheckBox.setChecked(false);
                    audioCheckedFlag = false;
                }
                if (SharedPrefManager.getInstance().isDataUsageWifi().contains("video,")) {
                    videoCheckBox.setChecked(true);
                    videoCheckedFlag = true;
                } else {
                    videoCheckBox.setChecked(false);
                    videoCheckedFlag = false;
                }
            }
        }
        ////////////////////////////////////////////////////////////////////
        ((RelativeLayout) bteldialog.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bteldialog.dismiss();
            }
        });
        ((RelativeLayout) bteldialog.findViewById(R.id.okButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tempString = "";
                String replaceTempString = "";
                String newPre = "";
                if (from.equalsIgnoreCase("mobile")) {
                    tempString = SharedPrefManager.getInstance().isDataUsageMobile();
                    replaceTempString = tempString;

                } else {
                    tempString = SharedPrefManager.getInstance().isDataUsageWifi();
                    replaceTempString = tempString;
                }


                if (tempString != null || (photoCheckedFlag || audioCheckedFlag || videoCheckedFlag)) {
                    if (!photoCheckedFlag)
                        replaceTempString = replaceTempString.replace("photo,", "");
                    else {
                        if (replaceTempString !=null  && !replaceTempString.contains("photo,"))
                            replaceTempString = replaceTempString + "photo,";
                        else
                            replaceTempString = replaceTempString + "photo,";
                    }

                    if (!audioCheckedFlag)
                        replaceTempString = replaceTempString.replace("audio,", " ");
                    else {
                        if (replaceTempString !=null  && !replaceTempString.contains("audio,"))
                            replaceTempString = replaceTempString + "audio,";
                        else
                            replaceTempString = replaceTempString + "audio,";
                    }

                    if (!videoCheckedFlag)
                        replaceTempString = replaceTempString.replace("video,", " ");
                    else {
                        if (replaceTempString !=null  && !replaceTempString.contains("video,"))
                            replaceTempString = replaceTempString + "video,";
                        else
                            replaceTempString = replaceTempString + "video,";
                    }

                    newPre = replaceTempString;
                } else {
                    newPre = tempString;
                }

                Log.e("temp", "temp : " + replaceTempString);
                if (from.equalsIgnoreCase("mobile")) {
                    SharedPrefManager.getInstance().setDataUsageMobile(newPre);
                } else {
                    SharedPrefManager.getInstance().setDataUsageWifi(newPre);
                }


                if (SharedPrefManager.getInstance().isDataUsageMobile() != null)
                    mobileDataUsageLabel.setText("" + SharedPrefManager.getInstance().isDataUsageMobile());
                else
                    mobileDataUsageLabel.setText("No media");

                if (SharedPrefManager.getInstance().isDataUsageWifi() != null)
                    wifiDataUsageLabel.setText("" + SharedPrefManager.getInstance().isDataUsageWifi());
                else
                    wifiDataUsageLabel.setText("No media");

                bteldialog.dismiss();
            }
        });

        ////////////////////////////////////////////////////////////////////
        ((RelativeLayout) bteldialog.findViewById(R.id.photoLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!photoCheckedFlag) {
                    photoCheckBox.setChecked(true);
                    photoCheckedFlag = true;

                } else {
                    photoCheckBox.setChecked(false);
                    photoCheckedFlag = false;

                }
            }
        });
        ((RelativeLayout) bteldialog.findViewById(R.id.audioLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!audioCheckedFlag) {
                    audioCheckBox.setChecked(true);
                    audioCheckedFlag = true;

                } else {
                    audioCheckBox.setChecked(false);
                    audioCheckedFlag = false;

                }
            }
        });
        ((RelativeLayout) bteldialog.findViewById(R.id.videoLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!videoCheckedFlag) {
                    videoCheckBox.setChecked(true);
                    videoCheckedFlag = true;


                } else {
                    videoCheckBox.setChecked(false);
                    videoCheckedFlag = false;
                }
            }
        });
        /////////////////////////////////////////////////////////////////////
        photoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!photoCheckedFlag) {
                    photoCheckBox.setChecked(true);
                    photoCheckedFlag = true;

                } else {
                    photoCheckBox.setChecked(false);
                    photoCheckedFlag = false;

                }
            }
        });

        audioCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!audioCheckedFlag) {
                    audioCheckBox.setChecked(true);
                    audioCheckedFlag = true;

                } else {
                    audioCheckBox.setChecked(false);
                    audioCheckedFlag = false;

                }
            }
        });
        videoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!videoCheckedFlag) {
                    videoCheckBox.setChecked(true);
                    videoCheckedFlag = true;

                } else {
                    videoCheckBox.setChecked(false);
                    videoCheckedFlag = false;

                }
            }
        });

        ////////////////////////////////////////////////////////////////////

        bteldialog.show();
    }
}
