package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.AdminRegistrationForm;
import com.superchat.model.ErrorModel;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.multiplesg.Registration;
import com.superchat.utils.AppUtil;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Countries;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.MyriadRegularTextView;
import com.superchat.widgets.MyriadSemiboldTextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import static com.chatsdk.org.jivesoftware.smackx.pubsub.FormType.result;

public class MainActivity extends FragmentActivity implements
        OnItemSelectedListener, OnClickListener {
    private String TAG = "MainActivity";
    public static final int CODE_COUNTRY_CHOOSER = 333;
    AutoCompleteTextView mobileNumberView = null;
    //	AutoCompleteTextView domainDisplayNameView = null;
    EditText domainNameView = null;
    EditText hubAdminName = null;
    ImageView checkAvailability = null;
    long typedtime = 0;
    Button nextButton = null;
    Button topNextButton = null;
    long delayForCheckAvailability = 1000;
    EditText adminEmail = null;
    EditText adminPass = null;
    EditText adminConfPass = null;
    EditText adminOrgName = null;
    AutoCompleteTextView adminOrgURL = null;
    EditText sgDescription = null;
    MyriadRegularTextView countryCodeView = null;
    MyriadRegularTextView countryNameView = null;
    boolean canCallPaswordMatching;
    boolean restartPaswordMatching;
    ImageView countryFlagView;
    String formatedNumber = "";
    String domainName = "";
    private RadioGroup radioGroup;
    private RadioButton radioGroupType;
    private android.content.DialogInterface.OnClickListener helpDialogListner;
    int indexCountryCode;
    Spinner spinner;
    String countryName;
    SharedPrefManager sharedPrefManager;
    ArrayAdapter<String> historyNumberAdapter;
    ArrayAdapter<String> historyDomainAdapter;
    boolean regAsAdmin;
    boolean registerSG;
    boolean tempVerify;
    EditText displayNameView = null;
    int currPage = 1;
    ScrollView viewOne, viewTwo;
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    public static final Pattern MOBILE_NUMBER_PATTERN_WITH_CC = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
    public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{7,15}$");
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");
    public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z]{3,})(\\s*([A-Za-z ]+?))?\\s*$");
    public static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-z0-9_.]{1,50}$");
    public static final Pattern ORG_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-z0-9 _.@&-]{1,100}$");
    private CheckBox privacyCheckbox;

    View view_domain_displayname_name, view_mobile_num, view_email, view_org_name, view_org_url;
    TextView text_view;
    RelativeLayout groupNameFieldLayout;
    RelativeLayout profileOptionalLayout;
    TextView domainTypeChooserView;
    TextView orgNameHelpTextView;
    TextView passwordTipTextView;
    TextView emailHelpTextView;
    TextView passwordReasonTextView;
    Dialog picChooserDialog;
    ProfilePicUploader picUploader;
    ImageView groupIconView;
    boolean isForground;
    String mobileNumber;
    String countryCode;
    String disp_name;
    String domainNameBefore = null;
    private final static byte REG_NONE_SCREEN = 0;
    private final static byte REG_FIRST_SCREEN = 1;
    private final static byte REG_SECOND_SCREEN = 2;
    byte regScreenNo = REG_NONE_SCREEN;

    private void setRegSreen(byte tmpScreen) {
        regScreenNo = tmpScreen;

        if (groupNameFieldLayout == null)
            groupNameFieldLayout = (RelativeLayout) findViewById(R.id.id_search_layout);
        if (domainTypeChooserView == null)
            domainTypeChooserView = (TextView) findViewById(R.id.id_domain_type);
        if (orgNameHelpTextView == null)
            orgNameHelpTextView = (MyriadRegularTextView) findViewById(R.id.id_orgname_help_txt);
        if (passwordTipTextView == null)
            passwordTipTextView = (MyriadRegularTextView) findViewById(R.id.id_password_tip);
        if (emailHelpTextView == null)
            emailHelpTextView = (MyriadRegularTextView) findViewById(R.id.id_creation_email_help);
        if (passwordReasonTextView == null)
            passwordReasonTextView = (MyriadRegularTextView) findViewById(R.id.id_password_reason);

        profileOptionalLayout = (RelativeLayout) findViewById(R.id.id_profile_layout);

        switch (regScreenNo) {
            case REG_FIRST_SCREEN:
                groupNameFieldLayout.setVisibility(View.VISIBLE);
                domainTypeChooserView.setVisibility(View.VISIBLE);
                domainNameView.setVisibility(View.VISIBLE);
                text_view.setVisibility(View.VISIBLE);

                view_domain_displayname_name.setVisibility(View.GONE);

                adminOrgName.setVisibility(View.GONE);
                passwordReasonTextView.setVisibility(View.GONE);
                passwordTipTextView.setVisibility(View.GONE);
                emailHelpTextView.setVisibility(View.GONE);
                view_email.setVisibility(View.GONE);
                adminEmail.setVisibility(View.GONE);
                adminPass.setVisibility(View.GONE);
                adminConfPass.setVisibility(View.GONE);

                orgNameHelpTextView.setVisibility(View.VISIBLE);
                adminOrgURL.setVisibility(View.VISIBLE);
                view_org_url.setVisibility(View.VISIBLE);
                view_org_name.setVisibility(View.VISIBLE);
                break;
            case REG_SECOND_SCREEN:
                viewOne.setVisibility(View.GONE);
                viewTwo.setVisibility(View.VISIBLE);
                currPage = 2;

                groupNameFieldLayout.setVisibility(View.GONE);
                domainTypeChooserView.setVisibility(View.GONE);
                domainNameView.setVisibility(View.GONE);
                text_view.setVisibility(View.GONE);

                view_domain_displayname_name.setVisibility(View.GONE);

                passwordReasonTextView.setVisibility(View.VISIBLE);
                passwordTipTextView.setVisibility(View.VISIBLE);
                emailHelpTextView.setVisibility(View.VISIBLE);
                view_email.setVisibility(View.VISIBLE);
                adminEmail.setVisibility(View.VISIBLE);
                adminPass.setVisibility(View.VISIBLE);
                adminConfPass.setVisibility(View.VISIBLE);

                orgNameHelpTextView.setVisibility(View.GONE);
                adminOrgName.setVisibility(View.VISIBLE);
                adminOrgURL.setVisibility(View.GONE);
                view_org_url.setVisibility(View.GONE);
                view_org_name.setVisibility(View.GONE);
                profileOptionalLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(MainActivity.this, ProfileScreen.class);
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
                        startActivity(intent);
                        finish();
                    }
                });
                break;
        }
    }

    String userComingFromScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Bundle bundle = getIntent().getExtras();
        String reg_type = null;
        String circle_name = null;
        String domain_name = null;

        if (bundle != null) {
            userComingFromScreen = bundle.getString(Constants.KEY_SCREEN_USER_COMING_FROM);
            if (bundle.get(Constants.REG_TYPE) != null)
                reg_type = bundle.get(Constants.REG_TYPE).toString();
            domain_name = bundle.getString(Constants.DOMAIN_NAME);
            tempVerify = bundle.getBoolean("TEMP_VERIFY");
        }
        if (tempVerify) {
            if (reg_type != null && reg_type.equals("ADMIN")) {
                registerSG = true;
            }
        } else {
            regAsAdmin = bundle.getBoolean("REGISTER_SG");
            mobileNumber = bundle.getString(Constants.MOBILE_NUMBER_TXT);
            disp_name = bundle.getString(Constants.NAME);
            tempVerify = false;
            registerSG = false;
        }
        if (regAsAdmin) {
            setContentView(R.layout.create_hub);
//            setContentView(R.layout.supergroup_creation);
        } else {
            if (bundle != null && bundle.get("DOMAIN_NAME") != null)
                circle_name = bundle.get("DOMAIN_NAME").toString();
            setContentView(R.layout.mobile_verifiy_request);
            countryNameView = (MyriadRegularTextView) findViewById(R.id.id_country_name);
            countryFlagView = (ImageView) findViewById(R.id.id_country_flag);
            displayNameView = (EditText) findViewById(R.id.id_display_name_field);
            countryFlagView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CountryChooserScreen.class);
                    startActivityForResult(intent, CODE_COUNTRY_CHOOSER);

                }
            });

            TextView id_title = (TextView) findViewById(R.id.id_title);
            if(id_title != null) {
                if (registerSG) {
                    id_title.setText(getResources().getString(R.string.create_a_hub));
                } else {
                    id_title.setText(getResources().getString(R.string.join_a_hub));
                }
            }
        }
        viewOne = (ScrollView) findViewById(R.id.scroll_view1);
        viewTwo = (ScrollView) findViewById(R.id.scroll_view2);
        currPage = 1;
        mobileNumberView = (AutoCompleteTextView) findViewById(R.id.mobile_number);
//		displayNameView.requestFocus();
        mobileNumberView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mobileNumberView, InputMethodManager.SHOW_IMPLICIT);
        countryCodeView = (MyriadRegularTextView) findViewById(R.id.country_code);
        domainNameView = (EditText) findViewById(R.id.id_domain_name);
        hubAdminName = (EditText) findViewById(R.id.id_admin_name);
//		domainDisplayNameView = (AutoCompleteTextView) findViewById(R.id.id_sg_display_name);
        checkAvailability = (ImageView) findViewById(R.id.id_availability_tick);
        groupIconView = (ImageView) findViewById(R.id.id_group_icon);
        if (groupIconView != null) {
            groupIconView.setOnClickListener(this);
            ImageView groupCameraIcon = (ImageView) findViewById(R.id.id_group_camera_icon);
            if (groupCameraIcon != null)
                groupCameraIcon.setOnClickListener(this);
        }
        picChooserDialog = new Dialog(this);
        picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
        picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
        picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
        picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);

        if (domain_name != null && domainNameView != null)
            domainNameView.setText(domain_name);
        if (!regAsAdmin) {
            if (circle_name != null)
                domainNameView.setText(circle_name);
            ((ImageView) findViewById(R.id.id_back_arrow)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefManager.getInstance().setMobileRegistered(SharedPrefManager.getInstance().getUserPhone(), false);
                    Intent intent = new Intent(MainActivity.this, RegistrationOptions.class);
                    startActivity(intent);
                    finish();
                }
            });
            ((MyriadSemiboldTextView) findViewById(R.id.id_back_title)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RegistrationOptions.class);
                    startActivity(intent);
                    finish();
                }
            });
            topNextButton = (Button) findViewById(R.id.id_next_btn);
            nextButton = (Button) findViewById(R.id.next_btn);
        } else {
//			topNextButton = (Button) findViewById(R.id.id_next_btn);
            nextButton = (Button) findViewById(R.id.next_btn);
        }
        mobileNumberView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (privacyCheckbox != null && privacyCheckbox.isChecked()) {
                    if (mobileNumberView.getText().toString().trim().length() > 6) {
                        nextButton.setVisibility(View.VISIBLE);
                        topNextButton.setVisibility(View.VISIBLE);
                        topNextButton.setTextColor(getResources().getColor(R.color.white));
                        nextButton.setBackgroundResource(R.drawable.round_rect_blue);
                    } else {
//						topNextButton.setVisibility(View.GONE);
//						nextButton.setVisibility(View.GONE);
                        topNextButton.setTextColor(getResources().getColor(R.color.gray_dark));
                        nextButton.setBackgroundResource(R.drawable.round_rect_gray);
                    }
                }
            }
        });

        mobileNumberView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Do whatever you want here
//		        	 if(privacyCheckbox.isChecked()){
////		        		 onNextButtonClick(v);
//		        		 InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		        		 inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//		        	 }
//		        	 else
//		        		 Toast.makeText(MainActivity.this, getString(R.string.validation_privacy), Toast.LENGTH_LONG).show();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        if (regAsAdmin) {
            view_domain_displayname_name = (View) findViewById(R.id.view_sg_display_name);
            view_mobile_num = (View) findViewById(R.id.view_mobile_num);
            view_email = (View) findViewById(R.id.view_email);
            view_org_name = (View) findViewById(R.id.view_org_name);
            view_org_url = (View) findViewById(R.id.view_org_url);
            text_view = (TextView) findViewById(R.id.check_avail_alert);
            adminEmail = (EditText) findViewById(R.id.id_admin_email);
            domainNameView = (EditText) findViewById(R.id.id_domain_name);
            adminPass = (EditText) findViewById(R.id.id_admin_password);
            adminConfPass = (EditText) findViewById(R.id.id_admin_conf_password);
            adminOrgName = (EditText) findViewById(R.id.id_org_name);
            adminOrgURL = (AutoCompleteTextView) findViewById(R.id.id_org_url);
            setRegSreen(REG_FIRST_SCREEN);
            domainTypeChooserView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder bteldialog = new AlertDialog.Builder(MainActivity.this);
                    ArrayList<DomainTypesAdapter.DomainType> list = new ArrayList<DomainTypesAdapter.DomainType>();
                    for (String label : getResources().getStringArray(R.array.domain_type_array)) {
                        String[] domainTypeObj = label.split("\\|");
                        DomainTypesAdapter.DomainType info = new DomainTypesAdapter.DomainType();
                        for (int index = 0; index < domainTypeObj.length; index++) {
                            switch (index) {
                                case 0: // domain_type
                                    info.setType(domainTypeObj[index]);
                                    break;
                                case 1: // domain_type_label
                                    info.setLabel(domainTypeObj[index]);
                                    break;
                                case 2: // domain_type_info
                                    info.setInfo(domainTypeObj[index]);
                                    break;
                            }
                        }

                        list.add(info);
                    }

                    bteldialog.setCancelable(true);
                    bteldialog.setTitle(getString(R.string.please_select_who_this_for));
                    TextView titleView = new TextView(MainActivity.this);
                    titleView.setText(getString(R.string.please_select_who_this_for));
                    titleView.setTextColor(Color.GRAY);
                    titleView.setPadding(10, 10, 0, 10);
                    bteldialog.setCustomTitle(titleView);
                    DomainTypesAdapter groupRoleCreationAdapter = new DomainTypesAdapter(MainActivity.this, R.layout.domain_type_items, list, (TextView) v);
                    bteldialog.setAdapter(groupRoleCreationAdapter, null);
                    AlertDialog alertDialog = bteldialog.show();
                    groupRoleCreationAdapter.setDialog(alertDialog);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            });

            domainNameView.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {

                        view_domain_displayname_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                        view_email.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                        view_mobile_num.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                        view_org_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                        view_org_url.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                    } else if (checkAvailability != null)
                        checkAvailability.setVisibility(View.GONE);

                }
            });

            domainNameView.requestFocus();

            view_email.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
            view_mobile_num.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
            view_org_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));

            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(hubAdminName, InputMethodManager.SHOW_IMPLICIT);
            adminOrgName.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Do whatever you want here
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        onNextButtonClickedForAdmin(v);
                        return true;
                    }
                    return false;
                }
            });

            if (domainName != null) {
                domainNameView.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub
                        domainNameBefore = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        if (domainNameView.getText().toString().trim().length() >= 3) {
                            new CheckAvailability(checkAvailability, domainNameView.getText().toString().trim()).execute();
                        } else {
                            if (checkAvailability != null)
                                checkAvailability.setVisibility(View.GONE);
                            if (text_view != null)
                                text_view.setVisibility(View.GONE);
                        }
                    }
                });
            }

            canCallPaswordMatching = true;
            adminPass.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                    passwordComparator();
                }

                public void beforeTextChanged(CharSequence charsequence, int i,
                                              int j, int k) {
                }

                public void onTextChanged(CharSequence charsequence, int i, int j,
                                          int k) {
                }

            });
            adminConfPass.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                    passwordComparator();
                }

                public void beforeTextChanged(CharSequence charsequence, int i,
                                              int j, int k) {
                }

                public void onTextChanged(CharSequence charsequence, int i, int j,
                                          int k) {
                }

            });

        }
        //Privacy Check
        privacyCheckbox = (CheckBox) findViewById(R.id.privacy_check);
        if (privacyCheckbox != null)
            privacyCheckbox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (privacyCheckbox.isChecked()) {
                        if (currPage == 1) {
                            if (mobileNumberView.getText().toString().trim().length() > 6) {
//		            		((Button) findViewById(R.id.id_next_btn)).setVisibility(View.VISIBLE);
//		            		((Button) findViewById(R.id.next_btn)).setVisibility(View.VISIBLE);
                                topNextButton.setTextColor(getResources().getColor(R.color.white));
                                nextButton.setBackgroundResource(R.drawable.round_rect_blue);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        } else
                            ((Button) findViewById(R.id.next_btn2)).setVisibility(View.VISIBLE);
                    } else {
                        if (currPage == 1) {
//	            		((Button) findViewById(R.id.id_next_btn)).setVisibility(View.GONE);
//	            		((Button) findViewById(R.id.next_btn)).setVisibility(View.INVISIBLE);
                            topNextButton.setTextColor(getResources().getColor(R.color.gray_dark));
                            nextButton.setBackgroundResource(R.drawable.round_rect_gray);
                        } else
                            ((Button) findViewById(R.id.next_btn2)).setVisibility(View.INVISIBLE);
                    }
                }
            });
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        spinner = (Spinner) findViewById(R.id.id_city_drop_down);
        spinner.setAdapter(new ArrayAdapter<Countries.Country>(this, R.layout.country_testview_screen, Countries.Country.values()));
        sharedPrefManager = SharedPrefManager.getInstance();
        if (regAsAdmin)
            sharedPrefManager.setFirstTime(true);
        String[] str = sharedPrefManager.getRecentUsers().split(",");
        historyNumberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, str);
        mobileNumberView.setAdapter(historyNumberAdapter);

        sharedPrefManager.saveRecentDomains("p5domain");
        str = sharedPrefManager.getRecentDomains().split(",");
        historyDomainAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, str);
//        if (domainNameView != null)
//            domainNameView.setAdapter(historyDomainAdapter);
//		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parentView, View arg1,
//									   int arg2, long arg3) {
//				indexCountryCode = arg2;
////				try {
////					((TextView) parentView.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.list_background));
////				} catch (Exception e) {
////
////				}
//
//				String selected = ""
//						+ ((Countries.Country) spinner.getSelectedItem()).getCode();
//
//				countryName = ((Countries.Country) spinner.getSelectedItem()).getStationName();
//				if(countryCodeView != null) {
//					countryCodeView.setText("+" + selected);
//				}
//				if(countryNameView != null)
//					countryNameView.setText(countryName);
//				//				AppUtil.showTost(VerificationActivity.this, countryName) ;
//				// Countries countries = new Countries() ;
//				// countries
//			}
//
//			@Override"
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});

        ((TextView) findViewById(R.id.privacy_check_txt_link)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TermAndConditionScreen.class));
//				finish();
            }
        });


        countryCodeView.setText(Constants.countryCode);
        if (countryNameView != null)
            countryNameView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//					spinner.performClick();
                    Intent intent = new Intent(MainActivity.this, CountryChooserScreen.class);
                    startActivityForResult(intent, CODE_COUNTRY_CHOOSER);
                }
            });
        //Write code to detect Country Code, and show the selection be default :)
        try {
            String cc = "";
//					
            if (cc == null || (cc != null && cc.trim().length() == 0)) {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
                cc = tm.getNetworkCountryIso();
                if (cc == null || (cc != null && cc.trim().length() == 0))
                    cc = "in";
                if (cc != null && cc.trim().length() != 0) {
                    Countries.Country countryObj = Countries.getCodeValue(cc);
                    if (countryObj != null) {
                        if (countryCodeView != null) {
                            countryCodeView.setText("+" + countryObj.getCode());
                        }
                        if (countryNameView != null)
                            countryNameView.setText(countryObj.getStationName());
                        if (countryFlagView != null) {
                            if (cc == null)
                                countryFlagView.setVisibility(View.GONE);
                            else {

                                // Commented By - Munish
                                //countryFlagView.setVisibility(View.VISIBLE);
                                //CountryChooserScreen.chooserImage = cc.toUpperCase();
                                if (Build.VERSION.SDK_INT >= 16)
                                    countryFlagView.setBackground(getDrawableFromAsset(cc.toUpperCase() + ".png"));
                                else
                                    countryFlagView.setImageBitmap(getBitmapFromAsset(cc.toUpperCase() + ".png"));
                            }
                        }
                    }
                }
            }

            //If still country code is null then get country code from standard API
            if (cc == null || (cc != null && cc.trim().length() == 0))
                new GetCountryCode().execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // AppUtil.showTost(VerificationActivity.this, ""+indexCountryCode) ;

        try {
            if (bundle != null) {
                String mobileNumber = null;
                Object obj = bundle.get(Constants.MOBILE_NUMBER_TXT);
                if (obj != null)
                    mobileNumber = obj.toString();
                String countryCode = bundle.getString(Constants.COUNTRY_CODE_TXT);
                if (mobileNumber != null)
                    mobileNumberView.setText(mobileNumber);
                if (countryCode != null)
                    countryCodeView.setText(countryCode);
                if (countryNameView != null && CountryChooserScreen.chooserCountry != null)
                    countryNameView.setText(""+CountryChooserScreen.chooserCountry);
                if (countryFlagView != null) {
                    if (CountryChooserScreen.chooserImage == null && countryCode == null) {
                        //countryFlagView.setVisibility(View.GONE);
                    }
                    else {
                        // Commented By - Munish
                        //countryFlagView.setVisibility(View.VISIBLE);
                        if (CountryChooserScreen.chooserImage != null) {
                            if (Build.VERSION.SDK_INT >= 16)
                                countryFlagView.setBackground(getDrawableFromAsset(CountryChooserScreen.chooserImage + ".png"));
                            else
                                countryFlagView.setImageBitmap(getBitmapFromAsset(CountryChooserScreen.chooserImage + ".png"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        helpDialogListner = new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int i) {
                dialoginterface.dismiss();
            }

        };
        countryCodeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				spinner.performClick();
                Intent intent = new Intent(MainActivity.this, CountryChooserScreen.class);
                startActivityForResult(intent, CODE_COUNTRY_CHOOSER);

            }
        });
//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Login Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
        
        UtilSetFont.setFontMainScreen(this);
    }

    final Handler passwordHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.password_mismatch_on_toast), Toast.LENGTH_SHORT).show();
        }

        ;
    };

    private void passwordComparator() {
        restartPaswordMatching = true;
        if (canCallPaswordMatching) {
            try {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        canCallPaswordMatching = false;
                        restartPaswordMatching = false;
                        while (!canCallPaswordMatching) {
                            try {
                                synchronized (this) {
                                    wait(1000);
                                }

                            } catch (Exception e) {
                            }
                            if (restartPaswordMatching) {
                                restartPaswordMatching = false;
                            } else {
                                String password1 = adminPass.getText().toString();
                                String password2 = adminConfPass.getText().toString();
                                if (password1 != null && password2 != null && !password1.equals("") && !password2.equals("") && !password1.equals(password2))
                                    passwordHandler.sendEmptyMessage(0);
                                canCallPaswordMatching = true;
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {
            }
        }
    }

    private void setEditTextFocus(EditText box) {
        if (box.isFocused())
            box.setBackgroundColor(getResources().getColor(R.color.color_lite_blue));
        else
            box.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_COUNTRY_CHOOSER:
                    Bundle dBundle = data.getExtras();
                    String code = "+91";
                    String country = "India";
                    String countryCode = null;
                    if (dBundle != null) {
                        country = dBundle.getString("COUNTRY_NAME", "India");
                        code = dBundle.getString("STD_CODE", "+91");
                        countryCode = dBundle.getString("COUNTRY_CODE", null);
                    }

//				String selected = ""+ ((Countries.Country) spinner.getSelectedItem()).getCode();
//
//				countryName = ((Countries.Country) spinner.getSelectedItem()).getStationName();
                    if (countryCodeView != null) {
                        countryCodeView.setText(code);
                    }
                    if (countryNameView != null)
                        countryNameView.setText(country);
                    if (countryFlagView != null) {
                        if (countryCode == null)
                            countryFlagView.setVisibility(View.GONE);
                        else {
                            // Commented By - Munish
                            //countryFlagView.setVisibility(View.VISIBLE);
                            CountryChooserScreen.chooserImage = countryCode.toUpperCase();
                            CountryChooserScreen.chooserCountry = country;
                            if (Build.VERSION.SDK_INT >= 16)
                                countryFlagView.setBackground(getDrawableFromAsset(countryCode + ".png"));
                            else
                                countryFlagView.setImageBitmap(getBitmapFromAsset(countryCode + ".png"));
                        }
                    }
                    break;
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
                    String filePath = Environment.getExternalStorageDirectory()
                            + "/" + Constants.contentTemp + "/" + AppUtil.TEMP_PHOTO_FILE;

                    AppUtil.capturedPath1 = filePath;
                    Bitmap selectedImage = BitmapFactory.decodeFile(AppUtil.capturedPath1);
                    groupIconView.setImageBitmap(selectedImage);
                    groupIconView.setBackgroundDrawable(null);
                    groupIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    uploadProfilePicture(AppUtil.capturedPath1);
                    break;
            }
        }
    }

    public void uploadProfilePicture(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                new ProfilePicUploader(this, null, true, notifyPhotoUploadHandler).execute(imgPath, packetID, "",
                        "SG_FILE_ID", sharedPrefManager.getUserName());
            } catch (Exception ex) {
                showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
            }
        }
    }

    private final Handler notifyPhotoUploadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
//	    	if(isForground)
//	    		showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
            if (registrationForm != null) {
                String file_id = sharedPrefManager.getSGFileId(sharedPrefManager.getUserDomain());
                if (file_id != null && file_id.trim().length() > 0)
                    registrationForm.setSGFileID(file_id);
            }
        }
    };

    public void onResume() {
        super.onResume();
        isForground = true;
    }

    public void onPause() {
        super.onPause();
        isForground = false;
    }

    private void performCrop(byte resultCode) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File file = new File(AppUtil.capturedPath1);
            Uri outputFileUri = Uri.fromFile(file);
//			System.out.println("----outputFileUri:" + outputFileUri);
            cropIntent.setDataAndType(outputFileUri, "image/*");
            cropIntent.putExtra("outputX", 600);
            cropIntent.putExtra("outputY", 600);
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
        } catch (Exception e) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
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

    public void onBackPressed() {
        if (regAsAdmin) {


            if (currPage == 2) {
                viewTwo.setVisibility(View.GONE);
                viewOne.setVisibility(View.VISIBLE);
                currPage = 1;
                if (regScreenNo != REG_SECOND_SCREEN) {
                    setRegSreen(REG_SECOND_SCREEN);
                }
            } else if (currPage == 1) {
//				Intent intent = new Intent(this, RegistrationOptions.class);
//				startActivity(intent);
//				finish();
                if (regScreenNo == REG_SECOND_SCREEN) {
                    setRegSreen(REG_FIRST_SCREEN);
                    return;
                }
            }
        } else {
            Intent intent = new Intent(this, RegistrationOptions.class);
            startActivity(intent);
            finish();
        }
    }

    AdminRegistrationForm registrationForm = null;

    public void onNextButtonClickedForAdmin(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        if (validateInputForRegistration(true)) {
            sharedPrefManager.saveUserOrgName("");
            sharedPrefManager.saveDisplayName("");
            String input = mobileNumberView.getText().toString();
            if (input != null && !sharedPrefManager.getRecentUsers().contains(input))
                historyNumberAdapter.add(input);
            sharedPrefManager.saveRecentUsers(input);

            if (domainNameView != null) {
                input = domainNameView.getText().toString();
//                if (input != null && !sharedPrefManager.getRecentDomains().contains(input))
//                    historyDomainAdapter.add(input);
//                sharedPrefManager.saveRecentDomains(input);
            }

            formatedNumber = countryCodeView.getText().toString().replace("+", "") + "-" + mobileNumberView.getText();
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
            String clientVersion = "Android_" + version;

            if (currPage == 1) {
                sgDescription = (EditText) findViewById(R.id.id_status_message);
                registrationForm = new AdminRegistrationForm(formatedNumber, "normal", imei, imsi, clientVersion);
                registrationForm.setToken(imei);
                registrationForm.countryCode = countryCodeView.getText().toString().replace("+", "");
                countryCode = registrationForm.countryCode;
                sharedPrefManager.saveUserPhone(formatedNumber);
//                if (disp_name != null)
//                    registrationForm.setAdminName(disp_name);
//                else
//                    registrationForm.setAdminName("User");
//                registrationForm.setAdminName(disp_name);
                registrationForm.setDomainName(domainNameView.getText().toString());

                String privacy_type = "closed";
                int selectedId = 0;
                radioGroup = (RadioGroup) findViewById(R.id.radio_group);
                selectedId = radioGroup.getCheckedRadioButtonId();
                radioGroupType = (RadioButton) findViewById(selectedId);
                if (R.id.radio_open == radioGroupType.getId())
                    privacy_type = "open";
                else if (R.id.radio_closed == radioGroupType.getId())
                    privacy_type = "closed";
                if (sharedPrefManager.getDomainType().equals("rwa"))
                    privacy_type = "closed";
                registrationForm.setPrivacyType(privacy_type);

                viewOne.setVisibility(View.GONE);
                viewTwo.setVisibility(View.VISIBLE);
                currPage = 2;

                if (sharedPrefManager.getDomainType().equals("rwa")) {
                    RadioButton b = (RadioButton) radioGroup.findViewById(R.id.radio_closed);
                    b.setChecked(true);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {

                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                } else {
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                }
                registrationForm.setDomainType(sharedPrefManager.getDomainType());
                registrationForm.setDescription(sgDescription.getText().toString().trim());
//				domainNameView = (AutoCompleteTextView) findViewById(R.id.id_domain_name);
//				domainNameView.requestFocus();
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.showSoftInput(domainNameView, InputMethodManager.SHOW_IMPLICIT);
//				domainNameView.setOnTouchListener(new View.OnTouchListener() {
//			        @Override
//			        public boolean onTouch(View v, MotionEvent event) {
//			        	 final ScrollView scrollView = (ScrollView)findViewById(R.id.scroll_view2);
//			            scrollView.post(new Runnable() {
//			                    @Override
//			                    public void run() {
//			                    	scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//			                    }
//			                }
//			            );
//			            return false;
//			        }
//			    });
                if (regScreenNo == REG_FIRST_SCREEN) {
                    setRegSreen(REG_SECOND_SCREEN);
                    return;
                }
            } else {
                //Privacy link
                if (privacyCheckbox == null)
                    privacyCheckbox = (CheckBox) findViewById(R.id.privacy_check);
                privacyCheckbox.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (privacyCheckbox.isChecked()) {
                            ((Button) findViewById(R.id.id_next_btn)).setVisibility(View.VISIBLE);
                            //((Button) findViewById(R.id.next_btn)).setVisibility(View.VISIBLE);
                        } else
                            //((Button) findViewById(R.id.next_btn)).setVisibility(View.INVISIBLE);
                        ((Button) findViewById(R.id.id_next_btn)).setVisibility(View.GONE);
                    }
                });

                registrationForm.setAdminEmail(adminEmail.getText().toString());
                registrationForm.setAdminName(hubAdminName.getText().toString());
                registrationForm.setAdminPassword(adminPass.getText().toString().trim());
                registrationForm.setAdminOrgName(adminOrgName.getText().toString());
                registrationForm.setAdminOrgURL(adminOrgURL.getText().toString());
                registrationForm.setDomainName(domainNameView.getText().toString());

                if(registrationForm != null) {
                    if (Build.VERSION.SDK_INT >= 11)
                        new SignupTaskForAdmin(registrationForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    else
                        new SignupTaskForAdmin(registrationForm, view).execute();
                }
            }
        }
    }

    class CheckAvailability extends AsyncTask<String, String, String> {

        ImageView img_view;
        String domain_name;

        public CheckAvailability(ImageView img_view, final String domain_name) {
            this.img_view = img_view;
            this.domain_name = domain_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/check?domainName=" + URLEncoder.encode(domain_name, "UTF-8");
                Log.i(TAG, "CheckAvailability :: doInBackground : URL - " + url);
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
            if (data != null) {
                Log.i(TAG, "CheckAvailability :: onPostExecute : response data - " + data);
                if (data.contains("success")) {
                    JSONObject json;
                    try {
                        if (img_view != null) {
                            text_view.setTextColor(Color.BLUE);
                        }
                        json = new JSONObject(data);
                        if (json.has("message") && img_view != null) {
                            text_view.setVisibility(View.VISIBLE);
//                            img_view.setVisibility(View.VISIBLE);
                            String msg = json.getString("message");
                            if (msg != null && msg.contains("is available")) {
//                                img_view.setImageResource(R.drawable.check);
                                text_view.setTextColor(getResources().getColor(R.color.green_dark_text_color));
                                text_view.setText(json.getString("message"));
                            } else {
//                                img_view.setImageResource(R.drawable.exit);
                                text_view.setTextColor(getResources().getColor(R.color.red));
                                text_view.setText(json.getString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (data.contains("error")) {
                    Gson gson = new GsonBuilder().create();
                    ErrorModel errorModel = gson.fromJson(data, ErrorModel.class);
                    if (errorModel != null) {
                        if (errorModel.citrusErrors != null && !errorModel.citrusErrors.isEmpty()) {
                            ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                            if (img_view != null) {
//                                img_view.setImageResource(R.drawable.exit);
                                text_view.setTextColor(getResources().getColor(R.color.red));
                                text_view.setText(citrusError.message);
//								text_view.setTextColor(Color.RED);
//								text_view.setText(citrusError.message);
                            }
                        } else if (errorModel.message != null) {
                            if (img_view != null) {
//                                img_view.setImageResource(R.drawable.exit);
                                text_view.setTextColor(getResources().getColor(R.color.red));
                                text_view.setText(errorModel.message);
//								text_view.setTextColor(Color.RED);
//								text_view.setText(errorModel.message);
                            }
                        }
                    } else {
                        if (img_view != null) {
//                            img_view.setImageResource(R.drawable.exit);
//							text_view.setTextColor(Color.RED);
//							text_view.setText("Please try again later.");
                        }
                    }
                }
            }
        }
    }

    public void onNextButtonClick(View view) {
        if (!SuperChatApplication.isNetworkConnected()) {
            showDialog(getString(R.string.check_net_connection));
            return;
        }
//		if(!checkName(displayNameView.getText().toString().trim())){	
//			showDialog(getString(R.string.display_name_hint));
//			return;
//		}
        if (mobileNumberView != null && mobileNumberView.getText().toString().trim().length() <= 6)
            return;
        if (privacyCheckbox != null && !privacyCheckbox.isChecked()) {
            Toast.makeText(MainActivity.this, getString(R.string.validation_privacy), Toast.LENGTH_LONG).show();
            return;
        }
        if (validateInputForRegistration(true)) {
            sharedPrefManager.saveUserOrgName("");
            sharedPrefManager.saveDisplayName("");
            Registration registrationForm = null;
            String input = mobileNumberView.getText().toString();
            if (input != null && !sharedPrefManager.getRecentUsers().contains(input))
                historyNumberAdapter.add(input);
            sharedPrefManager.saveRecentUsers(input);
            input = domainNameView.getText().toString();
            if (input != null && !sharedPrefManager.getRecentDomains().contains(input))
                historyDomainAdapter.add(input);
            sharedPrefManager.saveRecentDomains(input);

            formatedNumber = countryCodeView.getText().toString().replace("+", "") + "-" + mobileNumberView.getText();
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
            String clientVersion = "Android_" + version;
            registrationForm = new Registration(formatedNumber, null, imei, null, clientVersion, null, "false");
            registrationForm.setToken(imei);
            registrationForm.countryCode = countryCodeView.getText().toString().replace("+", "");
            countryCode = registrationForm.countryCode;
            domainName = domainNameView.getText().toString();
            if (domainName != null && domainName.trim().length() > 0)
                registrationForm.setDomainName(domainNameView.getText().toString());
            sharedPrefManager.saveUserPhone(formatedNumber);
            if (Build.VERSION.SDK_INT >= 11)
                new SignupTaskOnServer(registrationForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                new SignupTaskOnServer(registrationForm, view).execute();
            //submitSignUp(registrationForm, view);
        }
    }

    private boolean validateForNumberVerify(boolean show_alert) {
        if (currPage == 1) {
            if (!checkMobileNumber(mobileNumberView.getText().toString())) {
                if (show_alert)
                    showDialog(getString(R.string.mobile_validation_length));
                return false;
            }
            if (privacyCheckbox != null && !privacyCheckbox.isChecked()) {
                Toast.makeText(MainActivity.this, getString(R.string.validation_privacy), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean validateInputForRegistration(boolean show_alert) {
        switch (regScreenNo) {
            case REG_FIRST_SCREEN:
                if (currPage == 1 && regAsAdmin && domainNameView != null) {
//                    domainNameView.setText(domainNameView.getText().toString().trim());
                    if (!checkUserDomainName(domainNameView.getText().toString())) {
                        if (show_alert)
                            showDialog(getString(R.string.domain_validation_alert));
                        return false;
                    }
                    if (!checkName(hubAdminName.getText().toString())) {
                        if (show_alert)
                            showDialog(getString(R.string.please_enter_name));
                        return false;
                    }
                    if (radioGroup != null) {
                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            if (show_alert)
                                showDialog(getString(R.string.supergroup_validation));
                            return false;
                        }
                    }
                }
                break;
            case REG_SECOND_SCREEN:
                if (regAsAdmin) {
                    if (!checkEmail(adminEmail.getText().toString())) {
                        if (show_alert)
                            showDialog(getString(R.string.please_enter_email));
                        return false;
                    }
                }
                if (!checkPassword(adminPass.getText().toString())) {
                    if (show_alert)
                        showDialog(getString(R.string.please_enter_pass));
                    return false;
                }
                if (!checkPasswordLength(adminPass.getText().toString())) {
                    if (show_alert)
                        showDialog(getString(R.string.please_enter_pass_length));
                    return false;
                }
                if (!checkConfPassword(adminConfPass.getText().toString())) {
                    if (show_alert)
                        showDialog(getString(R.string.please_enter_conf_pass));
                    return false;
                }
                if (!checkConfPasswordLength(adminConfPass.getText().toString())) {
                    if (show_alert)
                        showDialog(getString(R.string.please_enter_conf_pass_length));
                    return false;
                }
                if (!checkPasswordMismatch(adminPass.getText().toString(), adminConfPass.getText().toString())) {
                    if (show_alert)
                        showDialog(getString(R.string.please_enter_pass_mismatch));
                    return false;
                }
                break;
        }

//        if (!regAsAdmin && countryCodeView.getText().toString().equals("")) {
//            if (show_alert)
//                showDialog(getString(R.string.country_validation));
//            return false;
//        }
//        if (!regAsAdmin && !checkMobileNumber(mobileNumberView.getText().toString())) {
//            if (show_alert)
//                showDialog(getString(R.string.mobile_validation_length));
//            return false;
//        }

//			if(adminName.getText().toString().trim().length() == 0){
//				if(show_alert)
//					showDialog(getString(R.string.please_enter_name));
//				return false;
//			}
//			if(!checkName(adminName.getText().toString())){
//				if(show_alert)
//					showDialog(getString(R.string.please_enter_valid_name));
//				return false;
//			}


//			if(!checkUserOrgName(adminOrgName.getText().toString())){
//				if(show_alert)
//					showDialog(getString(R.string.please_enter_org_name));
//				return false;
//			}
//		}
        return true;
    }

    //	Handler dialogHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch(msg.what){
//			case 0:
//				if (progressDialog != null) {
//					progressDialog.dismiss();
//					progressDialog = null;
//				}
//				break;
//			case 1:
//				progressDialog = ProgressDialog.show(MainActivity.this, "",
//						"Loading. Please wait...", true);
//				break;
//			}
//		};
//	};
    public class SignupTaskOnServer extends AsyncTask<String, String, String> {
        Registration registrationForm;
        ProgressDialog progressDialog = null;
        View view1;

        public SignupTaskOnServer(Registration registrationForm, final View view1) {
            this.registrationForm = registrationForm;
            this.view1 = view1;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String JSONstring = new Gson().toJson(registrationForm);
            DefaultHttpClient client1 = new DefaultHttpClient();

            Log.i(TAG, "SignupTaskOnServer :: doInBackground :  request:" + JSONstring);

            HttpPost httpPost = new HttpPost(Constants.SERVER_URL + "/tiger/rest/user/register");
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
                            Log.i(TAG, "SignupTaskOnServer :: doInBackground:  response:" + str);
                            str = str.trim();
                            //			            	Log.alltime(TAG, "serverUpdateContact sync response: "+str);
                            Gson gson = new GsonBuilder().create();
                            if (str == null || str.contains("error")) {
//											ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
//											if (errorModel != null) {
//												if (errorModel.citrusErrors != null
//														&& !errorModel.citrusErrors.isEmpty()) {
//													ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
//													showDialog(citrusError.message);
//												} else if (errorModel.message != null)
//													showDialog(errorModel.message);
//											} else
//												showDialog("Please try again later.");
                                return str;
                            }
                            Registration regObj = gson.fromJson(str, Registration.class);
                            if (regObj != null) {
                                SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                                if (iPrefManager != null && iPrefManager.getUserId() != 0) {
                                    if (iPrefManager.getUserId() != regObj.iUserId) {
                                        try {
                                            //Clean here - check at the login time,
                                            //if number is same then don't delete - else delete.
                                            DBWrapper.getInstance().clearMessageDB();
                                            DBWrapper.getInstance().clearAllDB();
                                            iPrefManager.clearSharedPref();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                Log.i(TAG, "SignupTaskOnServer :: doInBackground : Password, mobileNumber: " + regObj.getPassword() + " , " + regObj.iMobileNumber);
                                iPrefManager.saveUserId(regObj.iUserId);
                                if (!tempVerify) {
                                    iPrefManager.saveUserDomain(domainNameView.getText().toString());
                                    iPrefManager.saveCurrentSGDisplayName(domainNameView.getText().toString());
                                    //											iPrefManager.saveUserName("m"+regObj.iMobileNumber.replace("-", ""));
                                    //											iPrefManager.saveSipServerAddress(iPrefManager.getDisplayName()+"@"+Constants.CHAT_SERVER_URL+":5060");
                                    iPrefManager.saveAuthStatus(regObj.iStatus);
                                    iPrefManager.saveDeviceToken(regObj.token);
                                    iPrefManager.setAppMode("VirginMode");
                                    iPrefManager.saveUserPhone(regObj.iMobileNumber);
                                    //											iPrefManager.saveUserPassword(regObj.getPassword());
                                    iPrefManager.saveUserLogedOut(false);
                                    iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                                }
                            }
                            if (countryCode != null)
                                sharedPrefManager.setUserCountryCode(countryCode);
                            Intent intent = new Intent(MainActivity.this, MobileVerificationScreen.class);
                            intent.putExtra(Constants.MOBILE_NUMBER_TXT, formatedNumber);
                            intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
                            if (tempVerify)
                                intent.putExtra("TEMP_VERIFY", true);
                            if (domainName != null && domainName.trim().length() > 0)
                                intent.putExtra(Constants.DOMAIN_NAME, domainName);
                            if (registerSG)
                                intent.putExtra(Constants.REG_TYPE, "ADMIN");
                            else
                                intent.putExtra(Constants.REG_TYPE, "USER");
                            intent.putExtra(Constants.NAME, displayNameView.getText().toString().trim());
                            startActivity(intent);
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            finish();
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
                            iPrefManager.saveUserId(errorModel.userId);
                            if (!tempVerify) {
                                iPrefManager.saveUserDomain(domainNameView.getText().toString());
                                iPrefManager.saveCurrentSGDisplayName(domainNameView.getText().toString());
                                //below code should be only, in case of brand new user - "First time SC user"
                                iPrefManager.setAppMode("SecondMode");
                                //							iPrefManager.saveUserPhone(regObj.iMobileNumber);
                                //						iPrefManager.saveUserPassword(regObj.getPassword());
                                iPrefManager.saveUserLogedOut(false);
                                iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                            }
//							showDialogWithPositive(citrusError.message);
                            switchScreenForVerificaton(true);
                        } else
                            showDialog(citrusError.message);
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            } else {
//				showDialog("Please try again later.");
//				Toast.makeText(MainActivity.this, "Error connecting to server, please try again later.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(str);
        }
    }

    public void switchScreenForVerificaton(boolean is_old_user) {
        if (countryCode != null)
            sharedPrefManager.setUserCountryCode(countryCode);
        Intent intent = new Intent(MainActivity.this, MobileVerificationScreen.class);
        intent.putExtra(Constants.MOBILE_NUMBER_TXT, formatedNumber);
        intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
        if (domainName != null)
            intent.putExtra(Constants.DOMAIN_NAME, domainName);
        if (regAsAdmin)
            intent.putExtra(Constants.REG_TYPE, "ADMIN");
        else {
            intent.putExtra(Constants.REG_TYPE, "USER");
        }
        if (tempVerify)
            intent.putExtra("TEMP_VERIFY", true);
        intent.putExtra(Constants.NAME, displayNameView.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    public class SignupTaskForAdmin extends AsyncTask<String, String, String> {
        AdminRegistrationForm registrationForm;
        ProgressDialog progressDialog = null;
        View view1;

        public SignupTaskForAdmin(AdminRegistrationForm registrationForm, final View view1) {
            this.registrationForm = registrationForm;
            this.view1 = view1;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String JSONstring = new Gson().toJson(registrationForm);
            DefaultHttpClient client1 = new DefaultHttpClient();
            String url = Constants.SERVER_URL + "/tiger/rest/admin/register";
            HttpPost httpPost = new HttpPost(url);
            Log.i(TAG, "SignupTaskForAdmin :: doInBackground:  url:" + url);
            Log.i(TAG, "SignupTaskForAdmin :: doInBackground:  JSONstring Request :" + JSONstring);
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, false);
            HttpResponse response = null;
            try {
                httpPost.setEntity(new StringEntity(JSONstring));
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
                        if (str != null && !str.equals("")) {
                            str = str.trim();
                            Gson gson = new GsonBuilder().create();
                            if (str == null || str.contains("error")) {
                                return str;
                            }
                            Log.i(TAG, "SignupTaskForAdmin :: doInBackground : response:" + str);
                            Registration regObj = gson.fromJson(str, Registration.class);
                            if (regObj != null) {
                                SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                                //This Code not needed now, as we haev multiple SG support so no need to clean data.
//								if (iPrefManager != null && iPrefManager.getUserId() != 0) {
//									if (iPrefManager.getUserId() != regObj.iUserId) {
//										try {
//											DBWrapper.getInstance().clearMessageDB();
//											DBWrapper.getInstance().clearAllDB();
//											iPrefManager.clearSharedPref();
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//								}
                                Log.d(TAG, "SignupTaskForAdmin :: doInBackground : password, mobileNumber: " + regObj.getPassword() + " , " + regObj.iMobileNumber);
                                iPrefManager.saveUserId(regObj.iUserId);

                                if (!tempVerify) {
                                    iPrefManager.saveUserDomain(domainNameView.getText().toString());
                                    iPrefManager.saveCurrentSGDisplayName(domainNameView.getText().toString());
                                    iPrefManager.saveUserEmail(adminEmail.getText().toString());
                                    iPrefManager.saveDisplayName(hubAdminName.getText().toString());

                                    iPrefManager.saveUserOrgName(adminOrgName.getText().toString());
                                    iPrefManager.saveAuthStatus(regObj.iStatus);
                                    iPrefManager.saveDeviceToken(regObj.token);
                                    iPrefManager.setAppMode("VirginMode");
                                    iPrefManager.saveUserPhone(regObj.iMobileNumber);
                                    iPrefManager.saveUserLogedOut(false);
                                    iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                                    iPrefManager.saveSGFileId(domainNameView.getText().toString(), registrationForm.getSGFileID());
                                }
                            }
                            if (regAsAdmin) {
                                verifyUserSG(regObj.iUserId);
                            } else {
                                if (countryCode != null)
                                    sharedPrefManager.setUserCountryCode(countryCode);
                                Intent intent = new Intent(MainActivity.this, MobileVerificationScreen.class);
                                intent.putExtra(Constants.MOBILE_NUMBER_TXT, formatedNumber);
                                intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
                                if (tempVerify)
                                    intent.putExtra("TEMP_VERIFY", true);
                                if (domainName != null)
                                    intent.putExtra(Constants.DOMAIN_NAME, domainName);
                                if (regAsAdmin)
                                    intent.putExtra(Constants.REG_TYPE, "ADMIN");
                                else
                                    intent.putExtra(Constants.REG_TYPE, "USER");
                                intent.putExtra(Constants.NAME, displayNameView.getText().toString().trim());
                                startActivity(intent);
                                finish();
                            }
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
                            iPrefManager.saveUserId(errorModel.userId);
                            if (!tempVerify) {
                                iPrefManager.saveUserDomain(domainNameView.getText().toString());
                                iPrefManager.setAppMode("VirginMode");
                                //							iPrefManager.saveUserPhone(regObj.iMobileNumber);
                                //						iPrefManager.saveUserPassword(regObj.getPassword());
                                iPrefManager.saveUserLogedOut(false);
                                iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
                            }
                            showDialogWithPositive(citrusError.message);
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

    public void showDialog1(String s) {
        Dialog bteldialog = new Dialog(this);
        MyriadRegularTextView myriadregulartextview = new MyriadRegularTextView(
                this);
        myriadregulartextview.setText(s);
        myriadregulartextview.setGravity(17);
        myriadregulartextview.setPadding(15, 15, 15, 15);
        bteldialog.setContentView(myriadregulartextview);
        bteldialog.setCancelable(true);
        myriadregulartextview.setTextColor(Color.parseColor("#ff4f4f4f"));
//		bteldialog.setSingleButton(getString(R.string.close), helpDialogListner);
        bteldialog.show();
    }

    public void showDialogWithPositive(String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();

//				iPrefManager.saveUserName("m"+regObj.iMobileNumber.replace("-", ""));
//				iPrefManager.saveSipServerAddress(iPrefManager.getDisplayName()+"@"+Constants.CHAT_SERVER_URL+":5060");
                if (countryCode != null)
                    sharedPrefManager.setUserCountryCode(countryCode);
                Intent intent = new Intent(MainActivity.this, MobileVerificationScreen.class);
                intent.putExtra(Constants.MOBILE_NUMBER_TXT, formatedNumber);
                intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
                if (domainName != null)
                    intent.putExtra(Constants.DOMAIN_NAME, domainName);
                if (regAsAdmin)
                    intent.putExtra(Constants.REG_TYPE, "ADMIN");
                else
                    intent.putExtra(Constants.REG_TYPE, "USER");
                if (tempVerify)
                    intent.putExtra("TEMP_VERIFY", true);
                if (displayNameView != null && displayNameView.getText() != null)
                    intent.putExtra(Constants.NAME, displayNameView.getText().toString().trim());
                startActivity(intent);
                finish();
                return false;
            }
        });
        bteldialog.show();
    }

    public void showDialog(String s) {
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        String code = parent.getItemAtPosition(position).toString();
        countryCodeView.setText(code.substring(0, code.indexOf(' ')));
        // Toast.makeText(parent.getContext(),
        // "On Item Select : \n" +
        // parent.getItemAtPosition(position).toString(),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    //---------------- Validation methods ---------------
    public static boolean checkEmail(String emailstring) {
        if (emailstring == null)
            return false;
        Matcher m = EMAIL_PATTERN.matcher(emailstring);
        return m.matches();
    }

    public static boolean checkName(String name) {
        if (name == null)
            return false;
        Matcher m = NAME_PATTERN.matcher(name);
        return m.matches();
    }

    public static boolean checkPassword(String password) {
        if (password == null || (password != null && password.trim().length() == 0))
            return false;
        return true;
    }

    public static boolean checkPasswordLength(String password) {
        if (password != null && password.trim().length() < 6)
            return false;
        return true;
    }

    public static boolean checkConfPassword(String password) {
        if (password == null || (password != null && password.trim().length() == 0))
            return false;
        return true;
    }

    public static boolean checkConfPasswordLength(String password) {
        if (password != null && password.trim().length() < 6)
            return false;
        return true;
    }

    public static boolean checkPasswordMismatch(String password, String conf_password) {
        if (password != null && conf_password != null && !password.equals(conf_password))
            return false;
        return true;
    }

    public static boolean checkUserName(String name) {
        if (name == null)
            return false;
        Matcher m = USERNAME_PATTERN.matcher(name);
        return m.matches();
    }

    public static boolean checkUserDomainName(String name) {
        if (name == null)
            return false;
        Matcher m = DOMAIN_NAME_PATTERN.matcher(name);
        return m.matches();
    }

    public static boolean checkUserOrgName(String name) {
        if (name == null)
            return false;
        Matcher m = ORG_NAME_PATTERN.matcher(name);
        return m.matches();
    }

    public static boolean checkMobileNumber(String number) {
        if (number == null)
            return false;
        Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
        return m.matches();
    }

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

    private Drawable getDrawableFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        return drawable;
    }

    //================

    class GetCountryCode extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            String cc = null;
            try {
                HttpGet httppost = new HttpGet("http://ip-api.com/json");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);
                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(data);
                    if (json != null && json.has("countryCode")) {
                        cc = json.getString("countryCode");
                    }
                    return cc;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return cc;
        }

        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() != 0) {
//				int ind = Countries.getCountryName(result).getIndex();
                Countries.Country countryObj = Countries.getCodeValue(result);
                if (countryObj != null) {
                    if (countryCodeView != null) {
                        countryCodeView.setText("+" + countryObj.getCode());
                    }
                    if (countryNameView != null)
                        countryNameView.setText(countryObj.getStationName());
                    if (countryFlagView != null) {
                        if (result == null)
                            countryFlagView.setVisibility(View.GONE);
                        else {
                            // Commented By - Munish
                            //countryFlagView.setVisibility(View.VISIBLE);
                            CountryChooserScreen.chooserImage = result.toUpperCase();
                            CountryChooserScreen.chooserCountry = countryObj.getStationName();
                            if (Build.VERSION.SDK_INT >= 16)
                                countryFlagView.setBackground(getDrawableFromAsset(result.toUpperCase() + ".png"));
                            else
                                countryFlagView.setImageBitmap(getBitmapFromAsset(result.toUpperCase() + ".png"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    //------------------------------------------------------------------------------------
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
        codeVerifyUrl = Constants.SERVER_URL + "/tiger/rest/user/mobileverification/verify?userId=" + id + "&clientVersion=" + clientVersion + "&imei=" + imei + "&token=" + Constants.regid;

        Log.i(TAG, "verifyUserSG : URL - " + codeVerifyUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        client = SuperChatApplication.addHeaderInfo(client, false);
        client.get(codeVerifyUrl, null, new AsyncHttpResponseHandler() {
            ProgressDialog dialog = null;

            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
                    }
                });
                Log.d(TAG, "verifyUserSG :: onStart called.");
            }

            @Override
            public void onSuccess(int arg0, String arg1) {
                Log.i(TAG, "verifyUserSG :: onSuccess data : " + arg1);

                Gson gson = new GsonBuilder().create();
                final RegMatchCodeModel objUserModel = gson.fromJson(arg1, RegMatchCodeModel.class);

                if (objUserModel.iStatus != null
                        && objUserModel.iStatus.equalsIgnoreCase("success")) {
                    sharedPrefManager.saveUserVarified(true);
                    sharedPrefManager.setMobileVerified(sharedPrefManager.getUserPhone(), true);
                    sharedPrefManager.saveUserName(objUserModel.username);
                    sharedPrefManager.saveUserPassword(objUserModel.password);
                    Bundle bundle = new Bundle();
                    if (regAsAdmin) {
                        sharedPrefManager.setFirstTime(true);
                        Intent intent = new Intent(MainActivity.this, BulkInvitationScreen.class);
                        intent.putExtra(Constants.REG_TYPE, true);
//                        if(sgCreationAfterLogin) {
//                            intent.putExtra(Constants.SG_CREATE_AFTER_LOGIN, sgCreationAfterLogin);
//                            //Update Values in DB
//                            DBWrapper.getInstance().updateSGCredentials(iSharedPrefManager.getUserDomain(), iSharedPrefManager.getUserName(), iSharedPrefManager.getUserPassword(), iSharedPrefManager.getUserId(), true);
//                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, SupergroupListingScreen.class);
                        bundle.putString(Constants.MOBILE_NUMBER_TXT, mobileNumber);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showDialog(objUserModel.iMessage);
                        }
                    });

                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                });
                super.onSuccess(arg0, arg1);
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                Log.i(TAG, "onFailure Data : " + arg1);
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
}
