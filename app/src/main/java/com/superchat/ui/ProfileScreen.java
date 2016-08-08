package com.superchat.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sinch.android.rtc.calling.Call;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.model.ProfileUpdateModel;
import com.superchat.model.ProfileUpdateModelForAdmin;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.UserProfileModel;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileScreen extends FragmentActivity implements OnClickListener, OnItemSelectedListener {
	public static final String TAG = "ProfileScreen";
	Dialog picChooserDialog;
	LinearLayout currentLocationLayout;
	EditText displayNameView;
	EditText currentStatusView;
	EditText aboutView;
	EditText designationView;
	EditText flatNoView;
	EditText buildingNoView;
	// EditText residenceTypeView;
	String residenceType = null;
	Spinner residenceTypeView;
	EditText addressView;
	EditText rwaAddressView;
	EditText currentLocationView;
	EditText departmentView;
	TextView mobileView;
	EditText emailView;
	TextView mobileLabelView;
	TextView emailLabelView;
	EditText empIdView;
	Button nextButtonView;
	Button saveButtonView;
	TextView genderViewText;
	TextView statusViewLabel;
	TextView aboutViewLabel;
	TextView empidViewLabel;
	TextView departmentViewLabel;
	TextView designationViewLabel;
	TextView flatNoViewLabel;
	TextView displayNameLabel;
	TextView buildingNoViewLabel;
	TextView residenceTypeViewLabel;
	TextView addressViewLabel;
	TextView rwaAddressViewLabel;
	TextView currentLocationViewLabel;

	View view_display_name;
	// ToggleButton genderButton;
	String gender = null;
	Spinner genderSpinner = null;
	SharedPrefManager iSharedPrefManager;
	boolean isForground;
	String initalStatus;
	String initalAbout;
	String initalAddress;
	String initalCurrentLocation;
	String userName;
	public static String oldImageFileId;
	public String userSelectedFileID;
	ImageView callOption;
	private static final byte VIEWWING_AS_SELF_IN_REG = 0;
	private static final byte EDIT_BY_SELF = 1;
	private static final byte VIEW_SG_MEMBER = 2;
	private static final byte EDIT_MEMBER_BY_SG_OWNER = 3;
	private static final byte EDIT_SG_OWNER = 4;
	byte purposeType = EDIT_BY_SELF;
	boolean selfEdit;
	private SinchService.SinchServiceInterface mSinchServiceInterface;
	private ServiceConnection mCallConnection = new ServiceConnection() {
		// ------------ Changes for call ---------------
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			// if
			// (SinchService.class.getName().equals(componentName.getClassName()))
			{
				mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
				onServiceConnected();

			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			// if
			// (SinchService.class.getName().equals(componentName.getClassName()))
			{
				mSinchServiceInterface = null;
				onServiceDisconnected();
			}
		}

		protected void onServiceConnected() {
			// Register the user for call
			if (mSinchServiceInterface != null && !mSinchServiceInterface.isStarted()) {
				mSinchServiceInterface.startClient(SharedPrefManager.getInstance().getUserName());
			}
		}

		protected void onServiceDisconnected() {
			// for subclasses
		}

		protected SinchService.SinchServiceInterface getSinchServiceInterface() {
			return mSinchServiceInterface;
		}
	};
	private ChatService messageService;
	// private XMPPConnection xmppConnection;
	private ServiceConnection mMessageConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messageService = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
			// xmppConnection = messageService.getconnection();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// xmppConnection = null;
			messageService = null;
		}
	};

	// private static final String TIME_PATTERN = "HH:mm";
	DateFormat dateFormat = null;
	// SimpleDateFormat timeFormat = null;
	// TextView dateOfBirth = null;
	// Calendar dobCalender = Calendar.getInstance();

	private DatePicker datePicker;
	private Calendar calendar;
	private TextView dateView;
	private TextView dateViewLabel;
	private Button birthDayButtonView;
	private LinearLayout birthDayLayout;
	private int year, month, day;
	boolean domainReg = false;
	String mobForReg;
	boolean isProfileDataValidated;
	String displayName;
	boolean isViewOnlyDisplayName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		iSharedPrefManager = SharedPrefManager.getInstance();
		Bundle bundle = getIntent().getExtras();
		userName = iSharedPrefManager.getUserName();
		displayName = iSharedPrefManager.getDisplayName();
		// String tmpUserName = null;
		boolean manage_by_admin = false;
		boolean reg_flow = false;

		if (bundle != null) {
			userName = bundle.getString(Constants.CHAT_USER_NAME, userName);
			displayName = bundle.getString(Constants.CHAT_NAME, displayName);
			manage_by_admin = bundle.getBoolean("MANAGE_MEMBER_BY_ADMIN");
			reg_flow = bundle.getBoolean("PROFILE_EDIT_REG_FLOW");
			domainReg = bundle.getBoolean(Constants.REG_TYPE);
			isViewOnlyDisplayName = bundle.getBoolean("VIEW_ONLY");

			if (domainReg && bundle.getBoolean("PROFILE_FIRST")) {
				mobForReg = bundle.getString(Constants.MOBILE_NUMBER_TXT);
			}
		}

		if (reg_flow) {
			purposeType = VIEWWING_AS_SELF_IN_REG;
		} else if (manage_by_admin) {
			purposeType = EDIT_MEMBER_BY_SG_OWNER;
		} else if (userName != null && userName.equals(iSharedPrefManager.getUserName())) {
			if (iSharedPrefManager.isDomainAdminORSubAdmin())
				purposeType = EDIT_SG_OWNER;
			else
				purposeType = EDIT_BY_SELF;
		} else {
			purposeType = VIEW_SG_MEMBER;
		}

		// Set Dates
		calendar = Calendar.getInstance();
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		switch (purposeType) {
		case EDIT_MEMBER_BY_SG_OWNER:
			setContentView(R.layout.manage_member);
			residenceTypeViewLabel = (TextView) findViewById(R.id.id_residence_type_label);
			buildingNoViewLabel = (TextView) findViewById(R.id.id_building_number_label);
			flatNoViewLabel = (TextView) findViewById(R.id.id_flat_no_label);
			displayNameLabel = (TextView) findViewById(R.id.id_display_name_label);
			// residenceTypeView =
			// (EditText)findViewById(R.id.id_residence_type_field);
			buildingNoView = (EditText) findViewById(R.id.id_building_number_field);
			flatNoView = (EditText) findViewById(R.id.id_flat_no_field);

			// Spinner element
			residenceTypeView = (Spinner) findViewById(R.id.id_residence_type_spinner);
			if (residenceTypeView != null)
				residenceTypeView.setTag("1");
			genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
			if (genderSpinner != null)
				genderSpinner.setTag("2");
			// Spinner click listener
			break;
		case EDIT_SG_OWNER:
			setContentView(R.layout.profile_screen_self_admin);
			residenceTypeViewLabel = (TextView) findViewById(R.id.id_residence_type_label);
			buildingNoViewLabel = (TextView) findViewById(R.id.id_building_number_label);
			flatNoViewLabel = (TextView) findViewById(R.id.id_flat_no_label);
			currentLocationViewLabel = (TextView) findViewById(R.id.id_current_location_label);
			// residenceTypeView =
			// (EditText)findViewById(R.id.id_residence_type_field);
			buildingNoView = (EditText) findViewById(R.id.id_building_number_field);
			flatNoView = (EditText) findViewById(R.id.id_flat_no_field);
			// Spinner element
			residenceTypeView = (Spinner) findViewById(R.id.id_residence_type_spinner);
			if (residenceTypeView != null)
				residenceTypeView.setTag("1");
			genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
			dateView = (TextView) findViewById(R.id.lbl_dob);
			birthDayButtonView = (Button) findViewById(R.id.btn_set_dob);
			currentLocationView = (EditText) findViewById(R.id.id_location_field);
			currentStatusView = (EditText) findViewById(R.id.id_profile_status_field);
			aboutView = (EditText) findViewById(R.id.id_profile_about_field);
			// genderButton =
			// (ToggleButton)findViewById(R.id.toggel_btn_gender);
			break;
		case EDIT_BY_SELF:
			setContentView(R.layout.profile_screen);
			residenceTypeViewLabel = (TextView) findViewById(R.id.id_residence_type_label);
			buildingNoViewLabel = (TextView) findViewById(R.id.id_building_number_label);
			flatNoViewLabel = (TextView) findViewById(R.id.id_flat_no_label);

			// residenceTypeView =
			// (EditText)findViewById(R.id.id_residence_type_field);
			buildingNoView = (EditText) findViewById(R.id.id_building_number_field);
			flatNoView = (EditText) findViewById(R.id.id_flat_no_field);
			// Spinner element
			residenceTypeView = (Spinner) findViewById(R.id.id_residence_type_spinner);
			if (residenceTypeView != null) {
				// ((Spinner)
				// residenceTypeView).getSelectedView().setEnabled(false);
				residenceTypeView.setEnabled(false);
				residenceTypeView.setFocusable(false);
				residenceTypeView.setTag("1");
			}
			genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
			dateView = (TextView) findViewById(R.id.lbl_dob);
			birthDayButtonView = (Button) findViewById(R.id.btn_set_dob);
			currentLocationView = (EditText) findViewById(R.id.id_location_field);
			currentStatusView = (EditText) findViewById(R.id.id_profile_status_field);
			aboutView = (EditText) findViewById(R.id.id_profile_about_field);
			currentLocationViewLabel = (TextView) findViewById(R.id.id_location_label);
			// genderButton =
			// (ToggleButton)findViewById(R.id.toggel_btn_gender);
			break;
		case VIEWWING_AS_SELF_IN_REG:
			setContentView(R.layout.profile_in_reg);
			genderViewText = (TextView) findViewById(R.id.lbl_gender);
			dateView = (TextView) findViewById(R.id.lbl_dob);
			currentLocationView = (EditText) findViewById(R.id.id_location_field);
			saveButtonView = (Button) findViewById(R.id.profile_save);
			break;
		case VIEW_SG_MEMBER:
			setContentView(R.layout.profile_screen_view);
			dateView = (TextView) findViewById(R.id.lbl_dob);
			dateViewLabel = (TextView) findViewById(R.id.id_dob_label);
			statusViewLabel = (TextView) findViewById(R.id.id_profile_status_label);
			aboutViewLabel = (TextView) findViewById(R.id.id_profile_about_label);

			residenceTypeViewLabel = (TextView) findViewById(R.id.id_residence_type_label);
			buildingNoViewLabel = (TextView) findViewById(R.id.id_building_number_label);
			flatNoViewLabel = (TextView) findViewById(R.id.id_flat_no_label);

			// residenceTypeView =
			// (EditText)findViewById(R.id.id_residence_type_field);
			residenceTypeView = (Spinner) findViewById(R.id.id_residence_type_spinner);

			if (residenceTypeView != null) {
				// ((Spinner)
				// residenceTypeView).getSelectedView().setEnabled(false);
				residenceTypeView.setEnabled(false);
				residenceTypeView.setFocusable(false);
				residenceTypeView.setTag("1");
			}
			buildingNoView = (EditText) findViewById(R.id.id_building_number_field);
			flatNoView = (EditText) findViewById(R.id.id_flat_no_field);
			currentLocationViewLabel = (TextView) findViewById(R.id.id_location_label);

			aboutView = (EditText) findViewById(R.id.id_profile_about_field);
			birthDayLayout = (LinearLayout) findViewById(R.id.id_dob_layout);

			currentLocationView = (EditText) findViewById(R.id.id_location_field);
			currentStatusView = (EditText) findViewById(R.id.id_profile_status_field);
			genderViewText = (TextView) findViewById(R.id.gender_view);
			break;
		}

		oldImageFileId = null;
		displayNameView = (EditText) findViewById(R.id.id_display_name_field);
		view_display_name = (View) findViewById(R.id.view_display_name);
		currentLocationLayout = (LinearLayout) findViewById(R.id.id_current_location_layout);
		displayNameView.requestFocus();
		designationView = (EditText) findViewById(R.id.id_designation_field);
		rwaAddressViewLabel = (TextView) findViewById(R.id.id_rwa_address_label);
		rwaAddressView = (EditText) findViewById(R.id.id_rwa_address_field);
		addressViewLabel = (TextView) findViewById(R.id.id_address_label);
		addressView = (EditText) findViewById(R.id.id_address_field);
		departmentView = (EditText) findViewById(R.id.id_department_field);
		emailView = (EditText) findViewById(R.id.id_email_field);
		mobileView = (TextView) findViewById(R.id.id_mobile_field);
		empIdView = (EditText) findViewById(R.id.id_empid_field);
		nextButtonView = (Button) findViewById(R.id.id_next_btn);
		empidViewLabel = (TextView) findViewById(R.id.id_empid_label);
		departmentViewLabel = (TextView) findViewById(R.id.id_department_label);
		designationViewLabel = (TextView) findViewById(R.id.id_designation_label);
		nextButtonView.setOnClickListener(this);

		picChooserDialog = new Dialog(this);
		picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
		picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_remove).setOnClickListener(this);
		callOption = (ImageView) findViewById(R.id.call_option);

		initProfile();
		setViewListener();

		if (!isRWA()) {
			if (flatNoView != null)
				flatNoView.setVisibility(View.GONE);
			if (flatNoViewLabel != null)
				flatNoViewLabel.setVisibility(View.GONE);
			if (buildingNoView != null)
				buildingNoView.setVisibility(View.GONE);
			if (buildingNoViewLabel != null)
				buildingNoViewLabel.setVisibility(View.GONE);
			if (residenceTypeView != null)
				residenceTypeView.setVisibility(View.GONE);
			if (residenceTypeViewLabel != null)
				residenceTypeViewLabel.setVisibility(View.GONE);
			if (rwaAddressViewLabel != null)
				rwaAddressViewLabel.setVisibility(View.GONE);
			if (rwaAddressView != null)
				rwaAddressView.setVisibility(View.GONE);
			if (addressViewLabel != null)
				addressViewLabel.setVisibility(View.VISIBLE);
			if (addressView != null)
				addressView.setVisibility(View.VISIBLE);
		} else {
			if (empidViewLabel != null)
				empidViewLabel.setVisibility(View.GONE);
			if (departmentViewLabel != null)
				departmentViewLabel.setVisibility(View.GONE);
			if (designationViewLabel != null)
				designationViewLabel.setVisibility(View.GONE);
			if (designationView != null)
				designationView.setVisibility(View.GONE);
			if (departmentView != null)
				departmentView.setVisibility(View.GONE);
			if (empIdView != null)
				empIdView.setVisibility(View.GONE);
			if (addressViewLabel != null)
				addressViewLabel.setVisibility(View.GONE);
			if (addressView != null)
				addressView.setVisibility(View.GONE);
			if (rwaAddressViewLabel != null)
				rwaAddressViewLabel.setVisibility(View.VISIBLE);
			if (rwaAddressView != null)
				rwaAddressView.setVisibility(View.VISIBLE);

		}

		if (userName != null)
			getServerUserProfile(userName);
	}

	private boolean isRWA() {
		if (SharedPrefManager.getInstance().getDomainType().equals("rwa"))
			return true;
		return false;
	}

	private void setViewListener() {
		if (displayNameView != null) {
			displayNameView.setFilters(new InputFilter[] { filter });
			displayNameView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_blue));

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});
		}

		if (designationView != null)
			designationView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						designationView.setBackgroundResource(R.drawable.textbox_selection_bg);

						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});

		if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
			currentLocationView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						currentLocationView.setBackgroundResource(R.drawable.textbox_selection_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});

		if (departmentView != null && departmentView.getText() != null)
			departmentView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						departmentView.setBackgroundResource(R.drawable.textbox_selection_bg);

						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});

		if (currentStatusView != null)
			currentStatusView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						currentStatusView.setBackgroundResource(R.drawable.textbox_selection_bg);

						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});

		if (empIdView != null)
			empIdView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						empIdView.setBackgroundResource(R.drawable.textbox_selection_bg);

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});
		if (isRWA()) {
			if (rwaAddressView != null) {
				initalAddress = rwaAddressView.getText().toString();
				rwaAddressView.setOnFocusChangeListener(new OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							rwaAddressView.setBackgroundResource(R.drawable.textbox_selection_bg);

							if (empIdView != null)
								empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);

							if (currentStatusView != null)
								currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
							if (view_display_name != null)
								view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
							if (aboutView != null)
								aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
							if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
								currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
							if (departmentView != null)
								departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
							if (emailView != null)
								emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
							if (designationView != null)
								designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						}
					}
				});
			}
		} else if (addressView != null) {
			initalAddress = addressView.getText().toString();
			addressView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						addressView.setBackgroundResource(R.drawable.textbox_selection_bg);

						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});
		}
		if (aboutView != null) {
			aboutView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						aboutView.setBackgroundResource(R.drawable.textbox_selection_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (emailView != null)
							emailView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});
		}
		if (emailView != null) {
			emailView.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						emailView.setBackgroundResource(R.drawable.textbox_selection_bg);
						if (aboutView != null)
							aboutView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (addressView != null)
							addressView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (empIdView != null)
							empIdView.setBackgroundResource(R.drawable.round_rect_profile_bg);

						if (currentStatusView != null)
							currentStatusView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (view_display_name != null)
							view_display_name.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
						if (currentLocationView != null && purposeType != VIEWWING_AS_SELF_IN_REG)
							currentLocationView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (departmentView != null)
							departmentView.setBackgroundResource(R.drawable.round_rect_profile_bg);
						if (designationView != null)
							designationView.setBackgroundResource(R.drawable.round_rect_profile_bg);

					}
				}
			});
		}
	}

	private void initProfile() {

		if (isViewOnlyDisplayName)
			displayNameView.setEnabled(false);
		else if (iSharedPrefManager.isDomainAdminORSubAdmin())
			displayNameView.setEnabled(true);

		if (displayName != null) {
			String tmpName = displayName;
			if (tmpName.contains("#786#"))
				tmpName = displayName.substring(0, tmpName.indexOf("#786#"));
			displayNameView.setText(tmpName);
		}

		String tmp = iSharedPrefManager.getUserDesignation(userName);
		if (tmp != null && designationView != null)
			designationView.setText(tmp);

		tmp = iSharedPrefManager.getUserDepartment(userName);
		if (tmp != null && departmentView != null) {
			departmentView.setText(tmp);

		}
		if (isRWA()) {
			if (rwaAddressView != null)
				rwaAddressView.setHint(getString(R.string.address));
			if (rwaAddressViewLabel != null)
				rwaAddressViewLabel.setText(getString(R.string.address));
		}
		switch (purposeType) {
		case VIEWWING_AS_SELF_IN_REG:
			if (isRWA()) {
				((LinearLayout) findViewById(R.id.id_location_layout)).setVisibility(View.GONE);
				findViewById(R.id.id_line_bar).setVisibility(View.GONE);
			}

			dateView.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH);
					day = calendar.get(Calendar.DAY_OF_MONTH);
					showDialog(999);
				}
			});
			((LinearLayout) findViewById(R.id.id_gender_layout)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showGenderDialog(genderViewText);
				}
			});

			currentLocationView.addTextChangedListener(new TextWatcher() {

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
					if (currentLocationView.getText().toString().trim().length() > 3) {
						if (isProfileValidForReg(false)) {
							// nextButtonView.setVisibility(Button.VISIBLE);
							if (saveButtonView != null) {
								saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
								saveButtonView.setVisibility(View.VISIBLE);
							}
						} else
							saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
					} else
						saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
				}
			});

			saveButtonView.setOnClickListener(this);
			if (displayNameView != null)
				displayNameView.addTextChangedListener(new TextWatcher() {

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
						if (displayNameView.getText().toString().trim().length() >= 3) {
							if (isProfileValidForReg(false)) {
								// nextButtonView.setVisibility(Button.VISIBLE);
								if (saveButtonView != null) {
									saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
									saveButtonView.setVisibility(View.VISIBLE);
								}
							} else
								saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
						} else
							saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
					}
				});
			break;
		case EDIT_BY_SELF:
		case EDIT_SG_OWNER:

			// Spinner click listener
			if (residenceTypeView != null) {
				residenceTypeView.setTag("1");
				residenceTypeView.setOnItemSelectedListener(this);
			}
			if (genderSpinner != null) {
				genderSpinner.setTag("2");
				genderSpinner.setOnItemSelectedListener(this);
			}
			dateView.setVisibility(View.GONE);
			dateView.setText("dd/mm/yyyy");

			// showDate(year, month+1, day);
			birthDayButtonView.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH);
					day = calendar.get(Calendar.DAY_OF_MONTH);
					showDialog(999);
				}
			});

			boolean isPic = setProfilePic(userName);
			if (!isPic) {
				String picId = SharedPrefManager.getInstance().getUserFileId(userName);
				if (picId != null && !picId.equals("")) {
					if (Build.VERSION.SDK_INT >= 11)
						new BitmapDownloader(this, ((RoundedImageView) findViewById(R.id.id_profile_pic)))
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, picId,
										BitmapDownloader.PROFILE_PIC_REQUEST);
					else
						new BitmapDownloader(this, ((ImageView) findViewById(R.id.id_profile_pic))).execute(picId,
								BitmapDownloader.PROFILE_PIC_REQUEST);
					// (new
					// ProfilePicDownloader()).download(Constants.media_get_url+picId+".jpg",((RoundedImageView)findViewById(R.id.id_profile_pic)),null);
				}
			}
			if (callOption != null)
				callOption.setVisibility(View.GONE);
			if (emailView != null)
				emailView.setText(iSharedPrefManager.getUserEmail());
			String formatedNumber = iSharedPrefManager.getUserPhone();
			if (formatedNumber.contains("-"))
				formatedNumber = "+" + iSharedPrefManager.getUserPhone().replace("-", "");
			if (mobileView != null)
				mobileView.setText(formatedNumber);
			if (currentStatusView.getText().length() > 0)
				currentStatusView.setSelection(currentStatusView.getText().length());
			if (currentStatusView != null)
				initalStatus = currentStatusView.getText().toString();
			if (aboutView != null)
				initalAbout = aboutView.getText().toString();
			if (currentLocationView != null)
				initalCurrentLocation = currentLocationView.getText().toString();
			if (currentStatusView != null)
				currentStatusView.setText(iSharedPrefManager.getUserStatusMessage(userName));
			break;
		case EDIT_MEMBER_BY_SG_OWNER:
			if (residenceTypeView != null) {
				residenceTypeView.setTag("1");
				residenceTypeView.setOnItemSelectedListener(this);
			}
			if (genderSpinner != null) {
				genderSpinner.setTag("2");
				genderSpinner.setOnItemSelectedListener(this);
			}
			if (displayNameLabel != null && isRWA())
				displayNameLabel.setText(getString(R.string.name_label));
			break;
		case VIEW_SG_MEMBER:

			dateView.setText("dd/mm");
			// showDate(year, month+1, day);
			if (emailLabelView != null)
				emailLabelView.setVisibility(Button.GONE);
			if (mobileLabelView != null)
				mobileLabelView.setVisibility(Button.GONE);
			if (emailView != null)
				emailView.setVisibility(Button.GONE);
			if (mobileView != null)
				mobileView.setVisibility(Button.GONE);
			if (callOption != null)
				callOption.setVisibility(View.VISIBLE);
			if (nextButtonView != null)
				nextButtonView.setVisibility(Button.GONE);
			if (((ImageView) findViewById(R.id.id_edit_pic)) != null)
				((ImageView) findViewById(R.id.id_edit_pic)).setVisibility(ImageView.GONE);
			if (currentStatusView != null) {
				currentStatusView.setFocusable(false);
				currentStatusView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						int inType = currentStatusView.getInputType(); // backup
																		// the
																		// input
																		// type
						currentStatusView.setInputType(InputType.TYPE_NULL); // disable
																				// soft
																				// input
						currentStatusView.onTouchEvent(event); // call native
																// handler
						currentStatusView.setInputType(inType); // restore input
																// type
						currentStatusView.setFocusable(false);
						return true; // consume touch even
					}
				});
			}
			if (currentStatusView != null && currentStatusView.getText().length() > 0)
				currentStatusView.setSelection(currentStatusView.getText().length());
			if (currentStatusView != null)
				initalStatus = currentStatusView.getText().toString();
			if (aboutView != null)
				initalAbout = aboutView.getText().toString();
			if (currentLocationView != null)
				initalCurrentLocation = currentLocationView.getText().toString();
			if (currentStatusView != null)
				currentStatusView.setText(iSharedPrefManager.getUserStatusMessage(userName));
			break;
		}

	}

	public void onGenderToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			// Enable vibrate
			gender = getString(R.string.female);
		} else {
			// Disable vibrate
			gender = getString(R.string.male);
		}
		Log.i(TAG, "onGenderToggleClicked : " + gender);
	}

	@SuppressWarnings("deprecation")
	public void setDate(View view) {
		showDialog(999);
		Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if (id == 999) {
			return new android.app.DatePickerDialog(this, myDateListener, year, month, day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			// arg1 = year
			// arg2 = month
			// arg3 = day
			Calendar current_calendar = Calendar.getInstance();
			int curr_year = current_calendar.get(Calendar.YEAR);
			if ((curr_year - arg1) < 5) {
				Toast.makeText(ProfileScreen.this, "Your age must be at least 5 years, please enter valid date!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			showDate(arg1, arg2 + 1, arg3);
		}
	};

	private void showDate(int year, int month, int day) {
		String month_value = null;
		switch (month) {
		case 1:
			month_value = "JAN";
			break;
		case 2:
			month_value = "FEB";
			break;
		case 3:
			month_value = "MAR";
			break;
		case 4:
			month_value = "APR";
			break;
		case 5:
			month_value = "MAY";
			break;
		case 6:
			month_value = "JUN";
			break;
		case 7:
			month_value = "JUL";
			break;
		case 8:
			month_value = "AUG";
			break;
		case 9:
			month_value = "SEP";
			break;
		case 10:
			month_value = "OCT";
			break;
		case 11:
			month_value = "NOV";
			break;
		case 12:
			month_value = "DEC";
			break;
		}
		if (purposeType == VIEW_SG_MEMBER)
			dateView.setText(convertDOBInFormat(
					new StringBuilder().append(day).append("-").append(month_value).toString(), true));
		else {
			dateView.setText(convertDOBInFormat(
					new StringBuilder().append(day).append("-").append(month_value).append("-").append(year).toString(),
					true));
			if (birthDayButtonView != null)
				birthDayButtonView.setText(
						new StringBuilder().append(day).append("-").append(month_value).append("-").append(year));
		}
		if (purposeType == VIEWWING_AS_SELF_IN_REG) {
			if (isProfileValidForReg(false)) {
				// nextButtonView.setVisibility(Button.VISIBLE);
				if (saveButtonView != null) {
					saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
					saveButtonView.setVisibility(View.VISIBLE);
				}
			} else
				saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
		}
	}

	public void onBackClick(View view) {
		if (purposeType != VIEWWING_AS_SELF_IN_REG)
			finish();
	}

	@Override
	public void onBackPressed() {
		if (purposeType == VIEWWING_AS_SELF_IN_REG)
			return;
		else
			finish();
	}

	public void onCallClicked(View view) {
		if (mSinchServiceInterface != null) {
			try {
				Call call = mSinchServiceInterface.callUser(userName);
				String callId = call.getCallId();

				Intent callScreen = new Intent(this, CallScreenActivity.class);
				callScreen.putExtra(SinchService.CALL_ID, callId);
				startActivity(callScreen);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				int type = Character.getType(source.charAt(i));
				// System.out.println("Type : " + type);
				if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
					return "";
				}
			}
			return null;
		}
	};

	private String getImagePath(String groupPicId) {
		if (groupPicId == null)
			groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
		if (groupPicId != null) {
			String profilePicUrl = groupPicId + ".jpg";// AppConstants.media_get_url+
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
			String profilePicUrl = groupPicId + ".jpg";// AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
			File contentFile = new File(filename);
			if (contentFile != null && contentFile.exists()) {
				return filename;
			}

		}
		return null;
	}

	private boolean setProfilePic(String userName) {
		String picId = SharedPrefManager.getInstance().getUserFileId(userName);

		String img_path = getImagePath(picId);
		// android.graphics.Bitmap bitmap =
		// SuperChatApplication.getBitmapFromMemCache(groupPicId);
		ImageView picView = (ImageView) findViewById(R.id.id_profile_pic);
		// if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
		// picView.setImageResource(R.drawable.female_default);
		// else
		picView.setImageResource(R.drawable.profile_pic);
		if (picId == null)
			return false;
		// if (bitmap != null) {
		// picView.setImageBitmap(bitmap);
		// String profilePicUrl =
		// groupPicId+".jpg";//AppConstants.media_get_url+
		// File file = Environment.getExternalStorageDirectory();
		// String filename = file.getPath()+ File.separator +
		// "SuperChat/"+profilePicUrl;
		// picView.setTag(filename);
		// return true;
		// }else
		if (img_path != null) {
			File file1 = new File(img_path);
			// Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" ,
			// "+filename+" , "+file1.exists());
			if (file1.exists()) {
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView, img_path, picId);
				return true;
			}
		} else {
			img_path = getThumbPath(picId);
			if (img_path != null) {
				File file1 = new File(img_path);
				// Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" ,
				// "+filename+" , "+file1.exists());
				if (file1.exists()) {
					picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					// picView.setImageURI(Uri.parse(img_path));
					setThumb((ImageView) picView, img_path, picId);
				}
			}
		}
		if (picId != null && picId.equals("clear"))
			return true;
		return false;
	}

	private void setThumb(ImageView imageViewl, String path, String groupPicId) {
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(path, bfo);
			// bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
			bm = rotateImage(path, bm);
			// bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
		} catch (Exception ex) {

		}
		if (bm != null) {
			imageViewl.setImageBitmap(bm);
			// SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
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

			//// orientation =
			//// Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
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

	public void onProfileImagePicClick(View view) {
		String fileName = iSharedPrefManager.getUserFileId(userName);
		if (fileName == null || fileName.equals("") || fileName.equals("clear")) {
			if (purposeType == VIEWWING_AS_SELF_IN_REG || (picChooserDialog != null && !picChooserDialog.isShowing()
					&& userName != null && userName.equals(iSharedPrefManager.getUserName()))) {
				picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
				picChooserDialog.show();
			}
			return;
		}
		String file_path = getImagePath(fileName);// AppUtil.capturedPath1;
		if (file_path == null || (file_path != null && file_path.trim().length() == 0))
			file_path = getImagePath(null);
		if (file_path == null)
			file_path = getThumbPath(fileName);
		if (file_path != null) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			if (file_path.startsWith("http://"))
				intent.setDataAndType(Uri.parse(file_path), "image/*");
			else
				intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
			startActivity(intent);
		} else if (picChooserDialog != null && !picChooserDialog.isShowing()) {
			picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
			picChooserDialog.show();
		}
	}

	public void onProfilePicClick(View view) {

		if (picChooserDialog != null && !picChooserDialog.isShowing()) {
			String fileName = iSharedPrefManager.getUserFileId(userName);
			if (fileName == null || fileName.equals("") || fileName.equals("clear"))
				picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
			else
				picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.VISIBLE);
			picChooserDialog.show();
		}
	}

	public void onCurrentLocationClicked(View view) {
		// Get Current location and pre-fill.
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		boolean network_enabled = false;

		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gps_enabled && !network_enabled) {
			// notify user
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Not enabled");
			dialog.setPositiveButton("Open Locations Settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					// TODO Auto-generated method stub
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(myIntent);
					// get gps
				}
			});
			dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					// TODO Auto-generated method stub

				}
			});
			dialog.show();
			return;
		}

		Location location = null;
		double latitude = 0;
		double longitude = 0;
		if (lm != null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location == null) {
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			String address = getCompleteAddressString(latitude, longitude);
			if (address != null && address.trim().length() > 0) {
				currentLocationView.setText(address);
				if (address != null && address.trim().length() > 0 && isProfileValidForReg(false)) {
					// nextButtonView.setVisibility(Button.VISIBLE);
					if (saveButtonView != null) {
						saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
						saveButtonView.setVisibility(View.VISIBLE);
					}
				} else
					saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
			}
		}
	}

	public void onClearCurrentLocationClicked(View view) {
		// Get Current location and pre-fill.
		if (currentLocationView != null && currentLocationView.getText().toString().trim().length() > 0) {
			currentLocationView.setText("");
		}
	}

	private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
		String strAdd = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");

				// for (int i = 0; i < returnedAddress.getMaxAddressLineIndex();
				// i++) {
				// Log.i("My Current loction address", "" +
				// returnedAddress.getAddressLine(i));
				// strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				// }
				strReturnedAddress.append(addresses.get(0).getLocality());
				strReturnedAddress.append(", ");
				strReturnedAddress.append(addresses.get(0).getCountryName());
				strAdd = strReturnedAddress.toString();
				Log.i("My Current loction address", "" + strReturnedAddress.toString());
			} else {
				Log.w("My Current loction address", "No Address returned!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("My Current loction address", "Canont get Address!");
		}
		return strAdd;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.id_next_btn:
		case R.id.profile_save:
			if (purposeType == VIEWWING_AS_SELF_IN_REG) {
				if (isProfileValidForReg(true)) {
					if (domainReg && mobForReg != null) {
						// goto SG registration without finishing UI
						Intent intent = new Intent(ProfileScreen.this, MainActivity.class);
						if (mobForReg.indexOf('-') != -1)
							intent.putExtra(Constants.MOBILE_NUMBER_TXT,
									mobForReg.substring(mobForReg.indexOf('-') + 1));
						else
							intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobForReg);
						intent.putExtra(Constants.REG_TYPE, "ADMIN");
						intent.putExtra("REGISTER_SG", true);
						if (displayNameView != null)
							intent.putExtra(Constants.NAME, displayNameView.getText().toString().trim());
						mobForReg = null;
						isProfileDataValidated = true;
						userSelectedFileID = SharedPrefManager.getInstance()
								.getUserFileId(SharedPrefManager.getInstance().getUserName());
						startActivity(intent);
					} else
						new UpdateProfileTaskOnServer().execute();
				}
				return;
			}
			if (isProfileValid()) {
				switch (purposeType) {
				case EDIT_SG_OWNER:
					new UpdateProfileTaskOnServer().execute();
					break;

				default:
					new UpdateProfileTaskOnServer().execute();
					break;
				}
			}

			break;
		case R.id.id_camera:
			AppUtil.clearAppData();
			AppUtil.openCamera(this, AppUtil.capturedPath1, AppUtil.POSITION_CAMERA_PICTURE);
			picChooserDialog.cancel();
			break;
		case R.id.id_gallery:
			AppUtil.clearAppData();
			AppUtil.openImageGallery(this, AppUtil.POSITION_GALLRY_PICTURE);
			picChooserDialog.cancel();
			break;
		case R.id.id_remove:
			picChooserDialog.cancel();
			String currentFileId = iSharedPrefManager.getUserFileId(iSharedPrefManager.getUserName());
			if (currentFileId != null && !currentFileId.equals("clear") && !currentFileId.equals("")) {
				iSharedPrefManager.saveUserFileId(iSharedPrefManager.getUserName(), "clear");
				((ImageView) findViewById(R.id.id_profile_pic)).setImageResource(R.drawable.profile_pic);
				// new UpdateProfileTaskOnServer().execute();
			} else {
				showDialog(getString(R.string.app_name), getString(R.string.no_image_delete));
			}
			break;
		}
	}

	private boolean isProfileValid() {
		if (displayNameView != null && displayNameView.getText().toString() != null
				&& !Utilities.checkName(displayNameView.getText().toString().trim())) {
			Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (displayNameView != null && displayNameView.getText().toString().trim().equals("")) {

			showDialog(getString(R.string.app_name), getString(R.string.please_enter_name));
			return false;
		}
		if (currentStatusView != null) {
			String tmpStatus = currentStatusView.getText().toString();
			if (currentStatusView.getText().toString().trim().equals("") && !tmpStatus.equals("")) {
				showDialog(getString(R.string.app_name), getString(R.string.please_write_status));
				return false;
			}
		}

		return true;
	}

	private boolean isProfileValidForReg(boolean show_alert) {
		if (displayNameView != null && displayNameView.getText().toString() != null
				&& !Utilities.checkName(displayNameView.getText().toString().trim())) {
			Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (displayNameView != null && displayNameView.getText().toString().trim().equals("")) {
			if (show_alert) {

				showDialog(getString(R.string.app_name), getString(R.string.please_enter_name));
			}
			return false;
		}
		if (dateView != null && dateView.getText().toString().trim().equals(getString(R.string.dob))) {
			if (show_alert)
				showDialog(getString(R.string.app_name), getString(R.string.validation_dob));
			return false;
		}
		if (genderViewText != null
				&& genderViewText.getText().toString().trim().equals(getString(R.string.select_gender))) {
			if (show_alert)
				showDialog(getString(R.string.app_name), getString(R.string.validation_gender));
			return false;
		}

		if (currentLocationView != null && currentLocationView.getText().toString().trim().equals("")) {
			if (isRWA() && purposeType == VIEWWING_AS_SELF_IN_REG)
				return true;
			if (show_alert)
				showDialog(getString(R.string.app_name), getString(R.string.validation_curr_location));
			return false;
		}

		return true;
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
					((ImageView) findViewById(R.id.id_profile_pic)).setImageBitmap(selectedImage);
					((ImageView) findViewById(R.id.id_profile_pic)).setScaleType(ImageView.ScaleType.CENTER_CROP);
					// ((ImageView)
					// findViewById(R.id.id_profile_pic)).setImageURI(Uri.parse(filePath));
					// ((ImageView)
					// findViewById(R.id.id_profile_pic)).setScaleType(ImageView.ScaleType.CENTER_CROP);
					sendProfilePictureMessage(AppUtil.capturedPath1);
					break;
				}
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
				oldImageFileId = iSharedPrefManager.getUserFileId(userName);
				new ProfilePicUploader(this, null, true, notifyPhotoUploadHandler).execute(imgPath, packetID, "",
						XMPPMessageType.atMeXmppMessageTypeImage.name(), iSharedPrefManager.getUserName());
				// }
			} catch (Exception ex) {
				showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
				// System.out.println(""+ex.toString());
			}
		}
	}

	private final Handler notifyPhotoUploadHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isForground)
				showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
		}
	};

	public void onResume() {
		super.onResume();
		isForground = true;
		bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
		if (userName != null && !userName.equals(SharedPrefManager.getInstance().getUserName())) {
			bindService(new Intent(this, SinchService.class), mCallConnection, Context.BIND_AUTO_CREATE);
		}
		if (domainReg && isProfileDataValidated) {
			userName = SharedPrefManager.getInstance().getUserName();
			purposeType = VIEWWING_AS_SELF_IN_REG;
			new UpdateProfileTaskOnServer().execute();
		}
	}

	public void onPause() {
		super.onPause();
		isForground = false;
		unbindService(mMessageConnection);
		if (userName != null && !userName.equals(SharedPrefManager.getInstance().getUserName()))
			try {
				unbindService(mCallConnection);
			} catch (Exception e) {
				// Just ignore that
			}
	}

	public void onDestroy() {
		super.onDestroy();
		oldImageFileId = null;

	}

	private class UpdateProfileTaskOnServer extends AsyncTask<String, String, String> {
		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			if (domainReg && isProfileDataValidated)
				dialog = ProgressDialog.show(ProfileScreen.this, "", "Please wait while we create your Supergroup..",
						true);
			else
				dialog = ProgressDialog.show(ProfileScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {

			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			ProfileUpdateModel model = new ProfileUpdateModel();
			ProfileUpdateModelForAdmin admin_model = new ProfileUpdateModelForAdmin();

			model.setUserId(String.valueOf(iPrefManager.getUserId()));
			admin_model.setUserId(String.valueOf(iPrefManager.getUserId()));
			// model.setName(displayNameView.getText().toString());
			String statusText = null;
			String dob = null;
			String aboutText = null;
			String addressText = null;
			String currentLocation = null;
			String display_name = null;
			String designation = null;
			String department = null;
			String empID = null;
			String fileId = null;
			String emailID = null;

			DefaultHttpClient client1 = new DefaultHttpClient();
			String JSONstring = null;
			HttpPost httpPost = null;
			switch (purposeType) {
			case VIEWWING_AS_SELF_IN_REG:

				if (userName != null && !userName.equals("")) {
					model.setUserName(userName);
				}
				display_name = displayNameView.getText().toString().trim();
				if (display_name != null && !display_name.equals("")) {
					model.setName(display_name);
				}
				if (dateView != null) {
					dob = convertDOBInFormat(dateView.getText().toString().trim(), false);
					if (dob == null || dob.equals("dd/mm/yyyy")) {
						dob = "";// getString(R.string.status_hint);
					}
					if (!dob.equals("") && !dob.equals("Please enter your birthday"))
						model.setDOB(dob);
				}
				fileId = iPrefManager.getUserFileId(iPrefManager.getUserName());
				if (fileId == null && userSelectedFileID != null)
					fileId = userSelectedFileID;
				if (fileId != null && !fileId.equals("")) {
					if (fileId.contains("."))
						model.setImageFileId(fileId.substring(0, fileId.indexOf(".")));
					else
						model.setImageFileId(fileId);
				}
				if (gender != null && !gender.equals("") && !gender.equals("Select Gender")) {
					if (gender.equalsIgnoreCase("Do not want to disclose"))
						gender = "donotdisclose";
					model.setGender(gender);
				}
				if (currentLocationView != null) {
					currentLocation = currentLocationView.getText().toString().trim();
					if (currentLocation == null) {
						currentLocation = "";
					}
					if (!currentLocation.equals(""))
						model.setAddress(currentLocation);
				}

				JSONstring = new Gson().toJson(model);
				Log.d(TAG, "updateProfileTaskOnServer request:" + JSONstring);
				httpPost = new HttpPost(
						Constants.SERVER_URL + "/tiger/rest/user/profile/update?userId=" + model.getUserId());
				httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
				try {
					httpPost.setEntity(new StringEntity(JSONstring));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case EDIT_MEMBER_BY_SG_OWNER:

				if (userName != null && !userName.equals("")) {
					admin_model.setUserName(userName);
				}
				display_name = displayNameView.getText().toString();
				if (display_name != null) {// && !display_name.equals("")){
					display_name = display_name.trim();
					admin_model.setName(display_name);
				}
				designation = designationView.getText().toString();
				if (designation != null) {// && !designation.equals("")){
					designation = designation.trim();
					admin_model.setDesignation(designation);
				}
				if (isRWA()) {
					if (flatNoView != null) {
						String tmpTxt = flatNoView.getText().toString();
						if (tmpTxt != null) {
							tmpTxt = tmpTxt.trim();
							admin_model.setFlatNumber(tmpTxt);
						}
					}
					if (buildingNoView != null) {
						String tmpTxt = buildingNoView.getText().toString();
						if (tmpTxt != null) {
							tmpTxt = tmpTxt.trim();
							admin_model.setBuildingNumber(tmpTxt);
						}
					}
					if (residenceTypeView != null && residenceType != null) {
						// String tmpTxt =
						// residenceTypeView.getText().toString();
						String tmpTxt = null;
						if (residenceType.equalsIgnoreCase("Owner"))
							tmpTxt = "owner";
						else if (residenceType.equalsIgnoreCase("Tenant"))
							tmpTxt = "tenant";
						else if (residenceType.equalsIgnoreCase("Others"))
							tmpTxt = "other";
						if (tmpTxt != null) {
							admin_model.setResidenceType(tmpTxt);
						}
					}
				}
				if (isRWA()) {
					if (rwaAddressView != null) {
						addressText = rwaAddressView.getText().toString();
						if (addressText != null) {
							addressText = addressText.trim();
							admin_model.setAddress(addressText);

						}
					}
				} else {
					if (addressView != null) {
						addressText = addressView.getText().toString();
						if (addressText != null) {
							addressText = addressText.trim();
							admin_model.setLocation(addressText);
						}
					}
				}
				department = departmentView.getText().toString();
				if (department != null) {
					department = department.trim();
					admin_model.setDepartment(department);
				}
				empID = empIdView.getText().toString();
				if (empID != null) {// && !empID.equals("")){
					empID = empID.trim();
					admin_model.setEmpId(empID);
				}
				emailID = emailView.getText().toString();
				if (emailID != null) {// && !emailID.equals("")){
					emailID = emailID.trim();
					admin_model.setEmail(emailID);
				}
				if (gender != null && !gender.equals("") && !gender.equals("Select Gender")) {
					if (gender.equalsIgnoreCase("Do not want to disclose"))
						gender = "donotdisclose";
					admin_model.setGender(gender);
				}

				JSONstring = new Gson().toJson(admin_model);
				Log.d(TAG, "updateProfileTaskOnServer request:" + JSONstring);
				httpPost = new HttpPost(Constants.SERVER_URL + "/tiger/rest/user/profile/adminupdate?userId="
						+ admin_model.getUserId());
				httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
				try {
					httpPost.setEntity(new StringEntity(JSONstring));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:

				if (currentStatusView != null) {
					statusText = currentStatusView.getText().toString();
					if (statusText != null && statusText.trim().length() > 0) {
						statusText = statusText.trim();
						model.setCurrentStatus(statusText);
					}
				}

				if (userName != null && !userName.equals("")) {
					model.setUserName(userName);
				}

				display_name = displayNameView.getText().toString();
				if (display_name != null) {// && !display_name.equals("")){
					display_name = display_name.trim();
					model.setName(display_name);
				}

				if (dateView != null) {
					dob = convertDOBInFormat(dateView.getText().toString().trim(), false);
					if (dob == null || dob.equals("dd/mm/yyyy")) {
						dob = "";// getString(R.string.status_hint);
					}
					if (!dob.equals(""))
						model.setDOB(dob);
				}
				if (aboutView != null) {
					aboutText = aboutView.getText().toString();
					if (aboutText != null && aboutText.trim().length() > 0) {
						aboutText = aboutText.trim();
						model.setAbout(aboutText);
					}
				}

				fileId = iPrefManager.getUserFileId(iPrefManager.getUserName());
				if (fileId != null && !fileId.equals("")) {
					if (fileId.contains("."))
						model.setImageFileId(fileId.substring(0, fileId.indexOf(".")));
					else
						model.setImageFileId(fileId);
				}
				if (gender != null && !gender.equals("") && !gender.equals("Select Gender")) {
					if (gender.equalsIgnoreCase("Do not want to disclose"))
						gender = "donotdisclose";
					if (!gender.equals(""))
						model.setGender(gender);
				}
				if (isRWA() && purposeType == EDIT_SG_OWNER) {
					if (flatNoView != null) {
						String tmpTxt = flatNoView.getText().toString();
						if (tmpTxt != null) {
							tmpTxt = tmpTxt.trim();
							model.setFlatNumber(tmpTxt);
						}
					}
					if (buildingNoView != null) {
						String tmpTxt = buildingNoView.getText().toString();
						if (tmpTxt != null) {
							tmpTxt = tmpTxt.trim();
							model.setBuildingNumber(tmpTxt);
						}
					}
					if (residenceTypeView != null && residenceType != null) {
						// String tmpTxt =
						// residenceTypeView.getText().toString();
						String tmpTxt = null;
						if (residenceType.equalsIgnoreCase("Owner"))
							tmpTxt = "owner";
						else if (residenceType.equalsIgnoreCase("Tenant"))
							tmpTxt = "tenant";
						else if (residenceType.equalsIgnoreCase("Others"))
							tmpTxt = "other";
						if (tmpTxt != null) {
							model.setResidenceType(tmpTxt);
						}
					}
				}
				if (isRWA()) {
					if (rwaAddressView != null) {
						addressText = rwaAddressView.getText().toString();
						if (addressText != null) {
							addressText = addressText.trim();
							model.setAddress(addressText);

						}
					}
				} else {
					if (addressView != null) {
						addressText = addressView.getText().toString();
						if (addressText != null && addressText.trim().length() > 0) {
							addressText = addressText.trim();
							model.setLocation(addressText);
						}
					}

					// Check if he is owner
					// if(userName.equals(iPrefManager.getUserName())){
					// designation = designationView.getText().toString();
					// if(designation != null && designation.trim().length() >
					// 0){// && !designation.equals("")){
					// designation = designation.trim();
					// admin_model.setDesignation(designation);
					// }
					// department = departmentView.getText().toString();
					// if(department != null && department.trim().length() > 0){
					// department = department.trim();
					// admin_model.setDepartment(department);
					// }
					// empID = empIdView.getText().toString();
					// if(empID != null && empID.trim().length() > 0){// &&
					// !empID.equals("")){
					// empID = empID.trim();
					// admin_model.setEmpId(empID);
					// }
					// emailID = emailView.getText().toString();
					// if(emailID != null && emailID.trim().length() > 0){// &&
					// !emailID.equals("")){
					// emailID = emailID.trim();
					// admin_model.setEmail(emailID);
					// }
					// }
				}

				if (currentLocationView != null && !isRWA()) {
					currentLocation = currentLocationView.getText().toString();
					if (currentLocation != null && currentLocation.trim().length() > 0) {
						currentLocation = currentLocation.trim();
						model.setAddress(currentLocation);
					}
				}

				JSONstring = new Gson().toJson(model);
				Log.d(TAG, "updateProfileTaskOnServer request:" + JSONstring);
				// if(purposeType == EDIT_SG_OWNER)
				// httpPost = new HttpPost(Constants.SERVER_URL+
				// "/tiger/rest/user/profile/adminupdate?userId="+model.getUserId());
				// else
				httpPost = new HttpPost(
						Constants.SERVER_URL + "/tiger/rest/user/profile/update?userId=" + model.getUserId());
				httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
				try {
					httpPost.setEntity(new StringEntity(JSONstring));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}

			// httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			HttpResponse response = null;
			try {
				// httpPost.setEntity(new StringEntity(JSONstring));
				try {
					response = client1.execute(httpPost);
					final int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						String result = EntityUtils.toString(entity);
						// System.out.println("SERVER RESPONSE STRING: " +
						// entity.getContent());
						// BufferedReader rd = new BufferedReader(new
						// InputStreamReader(entity.getContent()));
						// String line = "";
						// while ((line = rd.readLine()) != null) {
						Log.d(TAG, "updateProfileTaskOnServer response: " + result);
						// }
						Gson gson = new GsonBuilder().create();
						final RegMatchCodeModel objResponseModel = gson.fromJson(result, RegMatchCodeModel.class);

						if (objResponseModel != null && objResponseModel.iStatus != null) {
							if (objResponseModel.iStatus.equalsIgnoreCase("success")) {
								// iSharedPrefManager.saveDisplayName(model.getName());
								oldImageFileId = null;
								iSharedPrefManager.saveUserStatusMessage(iSharedPrefManager.getUserName(),
										model.getCurrentStatus());
								iSharedPrefManager.saveUserFileId(iSharedPrefManager.getUserName(), model.imageFileId);
								iSharedPrefManager.setProfileAdded(iSharedPrefManager.getUserName(), true);

								// Update information to Other domain members
								// for the update.
								// Create Json here for group update info.
								JSONObject finalJSONbject = new JSONObject();
								String tmpUserName = null;
								switch (purposeType) {
								case EDIT_MEMBER_BY_SG_OWNER:
									if (admin_model != null && admin_model.getName() != null)
										iSharedPrefManager.saveUserServerName(iSharedPrefManager.getUserName(),
												admin_model.getName());
									if (admin_model != null && admin_model.getUserName() != null)
										tmpUserName = admin_model.getUserName();
									break;
								case VIEW_SG_MEMBER:
									iSharedPrefManager.saveDisplayName(model.getName());
									iSharedPrefManager.saveUserServerName(iSharedPrefManager.getUserName(),
											model.getName());
									tmpUserName = iSharedPrefManager.getUserName();
									break;
								case EDIT_BY_SELF:
								case EDIT_SG_OWNER:
									iSharedPrefManager.saveDisplayName(model.getName());
									tmpUserName = iSharedPrefManager.getUserName();
									break;
								default:
									tmpUserName = iSharedPrefManager.getUserName();
								}

								/////////////// Send Broadcast for Respective
								/////////////// Profile Update
								/////////////// //////////////////////
								try {
									if (tmpUserName != null && !tmpUserName.equals(""))
										finalJSONbject.put("userName", tmpUserName);

									if (admin_model != null && admin_model.getName() != null)
										finalJSONbject.put("displayname", admin_model.getName());

									// if(purposeType == VIEW_SG_MEMBER){
									// finalJSONbject.put("displayname",
									// model.getName());
									// }
									if (model.getName() != null && !model.getName().equals("")) {
										finalJSONbject.put("displayname", model.getName());
									}
									if (tmpUserName.equals(SharedPrefManager.getInstance().getUserName())) {
										if (model.getFlatNumber() != null && !model.getFlatNumber().equals("")) {
											finalJSONbject.put("flatNumber", model.getFlatNumber());
										}
										if (model.getBuildingNumber() != null
												&& !model.getBuildingNumber().equals("")) {
											finalJSONbject.put("buildingNumber", model.getBuildingNumber());
										}
										if (model.getResidenceType() != null && !model.getResidenceType().equals("")) {
											finalJSONbject.put("residenceType", model.getResidenceType());
										}
										if (model.getAddress() != null && !model.getAddress().equals("")) {
											finalJSONbject.put("address", model.getAddress());
										}
									} else {
										if (admin_model.getFlatNumber() != null
												&& !admin_model.getFlatNumber().equals("")) {
											finalJSONbject.put("flatNumber", admin_model.getFlatNumber());
										}
										if (admin_model.getBuildingNumber() != null
												&& !admin_model.getBuildingNumber().equals("")) {
											finalJSONbject.put("buildingNumber", admin_model.getBuildingNumber());
										}
										if (admin_model.getResidenceType() != null
												&& !admin_model.getResidenceType().equals("")) {
											finalJSONbject.put("residenceType", admin_model.getResidenceType());
										}
										if (admin_model.getAddress() != null && !admin_model.getAddress().equals("")) {
											finalJSONbject.put("address", admin_model.getAddress());
										}
									}
									if (model.getCurrentStatus() != null)
										finalJSONbject.put("statusMessage", model.getCurrentStatus());

									if (model.imageFileId != null && !model.imageFileId.equals(""))
										finalJSONbject.put("fileId", model.imageFileId);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// !SharedPrefManager.getInstance().isUserInvited(addedUser)
								if (messageService != null
										&& (tmpUserName.equals(SharedPrefManager.getInstance().getUserName())
												|| !SharedPrefManager.getInstance().isUserInvited(tmpUserName))) {
									String json = finalJSONbject.toString();
									Log.i(TAG, "Final JSON :  " + json);
									// json = json.replace("\"", "&quot;");
									messageService.sendSpecialMessageToAllDomainMembers(
											iSharedPrefManager.getUserDomain() + "-system", json,
											XMPPMessageType.atMeXmppMessageTypeUserProfileUpdate);
									json = null;
								}
								//////////////////////////

								// Intent intent = new
								// Intent(ProfileScreen.this, HomeScreen.class);
								// startActivity(intent);
							} else {
								return result;
								// if(objResponseModel.iMessage!=null)
								// showDialog(null,objResponseModel.iMessage);
								// else
								// showDialog(null,getString(R.string.lbl_server_not_responding));
								// finish();
								// if(objResponseModel.iMessage!=null)
								// Toast.makeText(ProfileScreen.this,
								// objResponseModel.iMessage,
								// Toast.LENGTH_SHORT).show();
								// else
								// Toast.makeText(ProfileScreen.this, "Server
								// Response code : "+statusCode,
								// Toast.LENGTH_SHORT).show();
							}
						}

						return result;
					} else {
						// if(purposeType != EDIT_SG_OWNER)
						// finish();
						// Toast.makeText(ProfileScreen.this, "Server Response
						// code : "+statusCode, Toast.LENGTH_SHORT).show();
					}
				} catch (ClientProtocolException e) {
					Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution ClientProtocolException:"
							+ e.toString());
				} catch (IOException e) {
					Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution ClientProtocolException:"
							+ e.toString());
				}

			} catch (Exception e) {
				Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution Exception:" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {

			super.onPostExecute(response);
			if (response != null && response.contains("error")) {
				Gson gson = new GsonBuilder().create();
				ErrorModel errorModel = gson.fromJson(response, ErrorModel.class);
				if (errorModel != null) {
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					if (errorModel.citrusErrors != null && !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						if (citrusError != null && citrusError.code.equals("20019")) {
							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
							// iPrefManager.saveUserDomain(domainNameView.getText().toString());
							iPrefManager.saveUserId(errorModel.userId);
							// below code should be only, in case of brand new
							// user - "First time SC user"
							iPrefManager.setAppMode("SecondMode");
							// iPrefManager.saveUserPhone(regObj.iMobileNumber);
							// iPrefManager.saveUserPassword(regObj.getPassword());
							iPrefManager.saveUserLogedOut(false);
							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
							showDialog("Error", citrusError.message);
						} else if (citrusError != null && citrusError.code.equals("20026")) {
							finish();
						} else if (citrusError != null && citrusError.code.equals("20020")) {
							showDialog("Error", citrusError.message);
						} else
							showDialog("Error", citrusError.message);
					} else if (errorModel.message != null)
						showDialog("Error", errorModel.message);
				} else
					showDialog("Error", "Please try again later.");
				// }else if(purposeType == EDIT_SG_OWNER){
				// purposeType = EDIT_MEMBER_BY_SG_OWNER;
				// new UpdateProfileTaskOnServer().execute();
			} else {
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (purposeType == EDIT_SG_OWNER) {
					purposeType = EDIT_MEMBER_BY_SG_OWNER;
					selfEdit = true;
					new UpdateProfileTaskOnServer().execute();
					return;
				}
				if (purposeType == VIEWWING_AS_SELF_IN_REG) {
					Intent intent = null;
					iSharedPrefManager.setProfileAdded(iSharedPrefManager.getUserName(), true);
					if (domainReg) {
						// intent = new Intent(ProfileScreen.this,
						// InviteMemberScreen.class);
						intent = new Intent(ProfileScreen.this, BulkInvitationScreen.class);
						intent.putExtra(Constants.REG_TYPE, true);
					} else {
						intent = new Intent(ProfileScreen.this, HomeScreen.class);
					}
					startActivity(intent);
					// finish();
				} else if (purposeType == EDIT_MEMBER_BY_SG_OWNER) {
					if (selfEdit)
						finish();
					else {
						Intent intent = new Intent(ProfileScreen.this, EsiaChatContactsScreen.class);
						intent.putExtra(Constants.CHAT_TYPE, Constants.MEMBER_DELETE);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				} else if (purposeType == EDIT_BY_SELF || purposeType == EDIT_SG_OWNER) {
					Intent intent = new Intent(ProfileScreen.this, MoreScreen.class);
					startActivity(intent);
				}
				finish();
			}
			if (response == null) {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
				if (!isConnected)
					Toast.makeText(ProfileScreen.this, getString(R.string.error_network_connection), Toast.LENGTH_SHORT)
							.show();
				else
					Toast.makeText(ProfileScreen.this, getString(R.string.network_not_responding), Toast.LENGTH_SHORT)
							.show();
			}
		}
	}

	private String convertDOBInFormat(String date, boolean for_view) {
		if (date == null)
			return null;
		String dob = date;
		String[] fields = null;
		if (for_view)
			fields = date.split("-");
		else
			fields = date.split(" ");
		if (fields != null && fields.length != 3)
			return dob;
		String month_value = fields[1];
		if (for_view) {
			if (month_value.equalsIgnoreCase("JAN"))
				month_value = "January";
			else if (month_value.equalsIgnoreCase("FEB"))
				month_value = "February";
			else if (month_value.equalsIgnoreCase("MAR"))
				month_value = "March";
			else if (month_value.equalsIgnoreCase("APR"))
				month_value = "April";
			else if (month_value.equalsIgnoreCase("MAY"))
				month_value = "May";
			else if (month_value.equalsIgnoreCase("JUN"))
				month_value = "June";
			else if (month_value.equalsIgnoreCase("JUL"))
				month_value = "July";
			else if (month_value.equalsIgnoreCase("AUG"))
				month_value = "August";
			else if (month_value.equalsIgnoreCase("SEP"))
				month_value = "September";
			else if (month_value.equalsIgnoreCase("OCT"))
				month_value = "October";
			else if (month_value.equalsIgnoreCase("NOV"))
				month_value = "November";
			else if (month_value.equalsIgnoreCase("DEC"))
				month_value = "December";

			dob = new StringBuffer(fields[0]).append(' ').append(month_value).append(' ').append(fields[2]).toString();
		} else {
			if (month_value.equalsIgnoreCase("January"))
				month_value = "JAV";
			else if (month_value.equalsIgnoreCase("February"))
				month_value = "FEB";
			else if (month_value.equalsIgnoreCase("March"))
				month_value = "MAR";
			else if (month_value.equalsIgnoreCase("April"))
				month_value = "APR";
			else if (month_value.equalsIgnoreCase("May"))
				month_value = "MAY";
			else if (month_value.equalsIgnoreCase("June"))
				month_value = "JUN";
			else if (month_value.equalsIgnoreCase("July"))
				month_value = "JUL";
			else if (month_value.equalsIgnoreCase("August"))
				month_value = "AUG";
			else if (month_value.equalsIgnoreCase("September"))
				month_value = "SEP";
			else if (month_value.equalsIgnoreCase("October"))
				month_value = "OCT";
			else if (month_value.equalsIgnoreCase("November"))
				month_value = "NOV";
			else if (month_value.equalsIgnoreCase("December"))
				month_value = "DEC";

			dob = new StringBuffer(fields[0]).append('-').append(month_value).append('-').append(fields[2]).toString();
		}
		return dob;
	}

	private void getServerUserProfile(final String userName) {
		final Context context = this;

		AsyncHttpClient client = new AsyncHttpClient();
		client = SuperChatApplication.addHeaderInfo(client, true);
		client.get(Constants.SERVER_URL + "/tiger/rest/user/profile/get?userName=" + userName, null,
				new AsyncHttpResponseHandler() {
					ProgressDialog dialog = null;

					@Override
					public void onStart() {
						runOnUiThread(new Runnable() {
							public void run() {
								dialog = ProgressDialog.show(ProfileScreen.this, "", "Loading. Please wait...", true);
							}
						});

						Log.d(TAG, "AsyncHttpClient onStart: ");
					}

					@Override
					public void onSuccess(int arg0, String arg1) {
						Log.d(TAG, "AsyncHttpClient onSuccess: " + arg1);

						Gson gson = new GsonBuilder().create();
						UserProfileModel objUserModel = gson.fromJson(arg1, UserProfileModel.class);
						if (arg1 == null || arg1.contains("error") || objUserModel == null) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (dialog != null) {
										dialog.dismiss();
										dialog = null;
									}
								}
							});

							return;
						}
						// SharedPrefManager.getInstance().saveUserName(objUserModel.iUserBaseApi.iName);
						// SharedPrefManager.getInstance().saveUserPhone(objUserModel.iUserBaseApi.iMobileNumber);
						// SharedPrefManager.getInstance().saveUserEmail(objUserModel.iUserBaseApi.iEmail);
						// String mobileNumber =
						// iSharedPrefManager.getUserPhone();

						String myUserName = iSharedPrefManager.getUserName();

						iSharedPrefManager.saveUserServerName(userName, objUserModel.iName);
						iSharedPrefManager.saveUserStatusMessage(userName, objUserModel.currentStatus);
						UserProfileModel.PrivacyStatusMap privacyStatusMap = objUserModel.getPrivacyStatusMap();
						if (privacyStatusMap != null) {
							if (privacyStatusMap.dnc == 1)
								iSharedPrefManager.saveStatusDNC(userName, true);
							else
								iSharedPrefManager.saveStatusDNC(userName, false);
							if (privacyStatusMap.dnm == 1)
								iSharedPrefManager.saveStatusDNM(userName, true);
							else
								iSharedPrefManager.saveStatusDNM(userName, false);
						}
						if (displayNameView != null)
							displayNameView.setText(iSharedPrefManager.getUserServerDisplayName(userName));
						if (currentStatusView != null) {
							if (statusViewLabel != null)
								statusViewLabel.setVisibility(View.VISIBLE);
							currentStatusView.setVisibility(View.VISIBLE);
							currentStatusView.setText(iSharedPrefManager.getUserStatusMessage(userName));
						}
						if (isRWA()) {
							if (flatNoView != null && flatNoViewLabel != null) {
								if (objUserModel.flatNumber != null && !objUserModel.flatNumber.equals("")) {
									flatNoView.setVisibility(View.VISIBLE);
									flatNoViewLabel.setVisibility(View.VISIBLE);
									flatNoView.setText(objUserModel.flatNumber);
								} else if (purposeType == VIEW_SG_MEMBER) {
									flatNoViewLabel.setVisibility(View.GONE);
									flatNoView.setVisibility(View.GONE);
								}
							}
							if (buildingNoView != null && buildingNoViewLabel != null) {
								if (objUserModel.buildingNumber != null && !objUserModel.buildingNumber.equals("")) {
									buildingNoView.setVisibility(View.VISIBLE);
									buildingNoViewLabel.setVisibility(View.VISIBLE);
									buildingNoView.setText(objUserModel.buildingNumber);
								} else if (purposeType == VIEW_SG_MEMBER) {
									buildingNoViewLabel.setVisibility(View.GONE);
									buildingNoView.setVisibility(View.GONE);
								}
							}
							if (residenceTypeView != null && residenceTypeViewLabel != null) {
								if (objUserModel.residenceType != null && !objUserModel.residenceType.equals("")) {
									residenceTypeView.setVisibility(View.VISIBLE);
									residenceTypeViewLabel.setVisibility(View.VISIBLE);
									// residenceTypeView.setText(objUserModel.residenceType);
									if (objUserModel.residenceType.equalsIgnoreCase("Owner"))
										residenceTypeView.setSelection(1);
									else if (objUserModel.residenceType.equalsIgnoreCase("Tenant"))
										residenceTypeView.setSelection(2);
									else if (objUserModel.residenceType.equalsIgnoreCase("Other"))
										residenceTypeView.setSelection(3);
									else
										residenceTypeView.setSelection(0);

								} else if (purposeType == VIEW_SG_MEMBER) {
									residenceTypeViewLabel.setVisibility(View.GONE);
									residenceTypeView.setVisibility(View.GONE);
								}
							}
						}
						switch (purposeType) {
						case VIEWWING_AS_SELF_IN_REG:
							if (dateView != null && objUserModel.dob != null) {
								// dateView.setText(objUserModel.dob);
								dateView.setText(convertDOBInFormat(objUserModel.dob, true));
								if (birthDayButtonView != null)
									birthDayButtonView.setText(convertDOBInFormat(objUserModel.dob, true));
							}
							if (currentLocationView != null && objUserModel.address != null) {
								currentLocationView.setText(objUserModel.address);
							}
							break;
						case EDIT_MEMBER_BY_SG_OWNER:

							if (displayNameView != null && objUserModel.iName != null) {
								displayNameView.setText("" + objUserModel.iName);
							}

							if (isRWA()) {
								if (rwaAddressView != null && objUserModel.address != null) {
									rwaAddressView.setText(objUserModel.address);
								}
							} else {
								if (empIdView != null && objUserModel.empId != null) {
									empIdView.setText("" + objUserModel.empId);
								}
								if (departmentView != null && objUserModel.department != null) {
									departmentView.setText("" + objUserModel.department);
								}
								if (designationView != null && objUserModel.designation != null) {
									designationView.setText("" + objUserModel.designation);
								}
								if (addressView != null && objUserModel.location != null) {
									addressView.setText(objUserModel.location);
								}
							}
							if (mobileView != null && objUserModel.iMobileNumber != null) {
								mobileView.setText(objUserModel.iMobileNumber);
							}
							if (emailView != null && objUserModel.iEmail != null) {
								emailView.setText(objUserModel.iEmail);
							}

							break;
						default: /// for if(userName.equals(myUserName) ||
									/// purposeType == VIEW_SG_MEMBER)

							if (objUserModel.iEmail != null)
								iSharedPrefManager.saveUserEmail(objUserModel.iEmail);
							// if(objUserModel.iMobileNumber!=null)
							// iSharedPrefManager.saveUserPhone(objUserModel.iMobileNumber);
							if (emailView != null)
								emailView.setText(iSharedPrefManager.getUserEmail());
							String formatedNumber = iSharedPrefManager.getUserPhone();
							if (formatedNumber != null && formatedNumber.contains("-"))
								formatedNumber = "+" + iSharedPrefManager.getUserPhone().replace("-", "");
							if (mobileView != null)
								mobileView.setText(formatedNumber);
							if (currentStatusView != null)
								currentStatusView.setSelection(currentStatusView.getText().length());
							if (aboutView != null) {
								if (objUserModel.aboutMe != null && !objUserModel.aboutMe.equals("")) {
									aboutView.setVisibility(View.VISIBLE);
									if (purposeType == VIEW_SG_MEMBER)
										aboutViewLabel.setVisibility(View.VISIBLE);
									aboutView.setText(objUserModel.aboutMe);
								} else {
									if (purposeType == VIEW_SG_MEMBER) {
										aboutViewLabel.setVisibility(View.GONE);
										aboutView.setVisibility(View.GONE);
									}
								}
							}

							if (dateView != null && objUserModel.dob != null) {
								String db = convertDOBInFormat(objUserModel.dob, true);
								if (purposeType == VIEW_SG_MEMBER) {
									if (db != null && db.contains(" "))
										db = db.substring(0, db.lastIndexOf(" "));
								}
								dateView.setText(db);
								if (birthDayButtonView != null)
									birthDayButtonView.setText(db);
							} else {
								if (birthDayLayout != null) {
									if (purposeType == VIEW_SG_MEMBER) {
										dateViewLabel.setVisibility(View.GONE);
										birthDayLayout.setVisibility(View.GONE);
									}
								}
							}

							if (currentLocationView != null) {
								if (isRWA()) {
									if (currentLocationLayout != null)
										currentLocationLayout.setVisibility(View.GONE);
									if (currentLocationViewLabel != null)
										currentLocationViewLabel.setVisibility(View.GONE);
									currentLocationView.setVisibility(View.GONE);
								} else if (objUserModel.address != null && !objUserModel.address.equals("")) {
									currentLocationView.setVisibility(View.VISIBLE);
									if (purposeType == VIEW_SG_MEMBER)
										currentLocationViewLabel.setVisibility(View.VISIBLE);
									currentLocationView.setText(objUserModel.address);
								} else if (purposeType == VIEW_SG_MEMBER) {
									currentLocationView.setVisibility(View.GONE);
									currentLocationViewLabel.setVisibility(View.GONE);
								}
							}
							if (!isRWA())
								if (empIdView != null) {
									if (objUserModel.empId != null && !objUserModel.empId.equals("")) {
										if (purposeType == VIEW_SG_MEMBER)
											empidViewLabel.setVisibility(View.VISIBLE);
										empIdView.setVisibility(View.VISIBLE);
										empIdView.setText("" + objUserModel.empId);
									} else if (purposeType == VIEW_SG_MEMBER) {

										empidViewLabel.setVisibility(View.GONE);
										empIdView.setVisibility(View.GONE);
									}
								}
							if (!isRWA())
								if (departmentView != null) {
									if (objUserModel.department != null && !objUserModel.department.equals("")) {
										departmentView.setVisibility(View.VISIBLE);
										if (purposeType == VIEW_SG_MEMBER)
											departmentViewLabel.setVisibility(View.VISIBLE);
										departmentView.setText("" + objUserModel.department);
									} else if (purposeType == VIEW_SG_MEMBER) {
										departmentView.setVisibility(View.GONE);

										departmentViewLabel.setVisibility(View.GONE);
									}
								}
							if (!isRWA())
								if (designationView != null) {
									if (objUserModel.designation != null && !objUserModel.designation.equals("")) {
										designationView.setVisibility(View.VISIBLE);
										if (purposeType == VIEW_SG_MEMBER)
											designationViewLabel.setVisibility(View.VISIBLE);
										designationView.setText("" + objUserModel.designation);
									} else {

										if (purposeType == VIEW_SG_MEMBER) {
											designationView.setVisibility(View.GONE);
											designationViewLabel.setVisibility(View.GONE);
										}
									}
								} {
							if (isRWA()) {
								if (rwaAddressView != null) {
									if (objUserModel.address != null && !objUserModel.address.equals("")) {
										rwaAddressView.setVisibility(View.VISIBLE);
										if (purposeType == VIEW_SG_MEMBER)
											rwaAddressViewLabel.setVisibility(View.VISIBLE);
										else if (purposeType == EDIT_SG_OWNER) {
											rwaAddressView.setEnabled(true);
										}
										rwaAddressView.setText(objUserModel.address);
									} else {
										if (purposeType == VIEW_SG_MEMBER) {
											rwaAddressViewLabel.setVisibility(View.GONE);
											rwaAddressView.setVisibility(View.GONE);
										}
									}
								}
							} else {
								if (addressView != null) {
									if (objUserModel.location != null && !objUserModel.location.equals("")) {
										addressView.setVisibility(View.VISIBLE);
										if (purposeType == VIEW_SG_MEMBER)
											addressViewLabel.setVisibility(View.VISIBLE);
										addressView.setText(objUserModel.location);
									} else {
										if (purposeType == VIEW_SG_MEMBER) {
											addressViewLabel.setVisibility(View.GONE);
											addressView.setVisibility(View.GONE);
										}
									}
								}
							}

						}

						}

						if (objUserModel.gender != null) {
							gender = objUserModel.gender;
							switch (purposeType) {
							case EDIT_BY_SELF:
							case EDIT_MEMBER_BY_SG_OWNER:
							case EDIT_SG_OWNER:
								if (genderSpinner != null) {
									if (gender != null) {
										if (gender.equalsIgnoreCase("donotdisclose"))
											genderSpinner.setSelection(3);
										else {
											int position = getArrayIndex(
													getResources().getStringArray(R.array.gender_options), gender);
											genderSpinner.setSelection(position);
										}
									} else
										genderSpinner.setSelection(0);
								}
								break;
							case VIEWWING_AS_SELF_IN_REG:
								if (genderViewText != null) {
									if ((gender != null && gender.equals(""))
											|| gender.equalsIgnoreCase("donotdisclose")) {
										genderViewText
												.setText(getResources().getStringArray(R.array.gender_options)[3]);
									} else {
										// genderViewText.setText(gender);
										genderViewText
												.setText(gender.substring(0, 1).toUpperCase() + gender.substring(1));
									}
								}
								if (isProfileValidForReg(false)) {
									// nextButtonView.setVisibility(Button.VISIBLE);
									if (saveButtonView != null) {
										saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
										saveButtonView.setVisibility(View.VISIBLE);
									}
								} else
									saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
								break;
							case VIEW_SG_MEMBER:
								if (genderViewText != null) {
									if ((gender != null && gender.equals(""))
											|| gender.equalsIgnoreCase("donotdisclose")) {
										genderViewText
												.setText(getResources().getStringArray(R.array.gender_options)[3]);
									} else
										gender = gender.substring(0, 1).toUpperCase()
												+ gender.substring(1).toLowerCase();
									genderViewText.setText(gender);
								}
								break;
							}
							// if(gender.equalsIgnoreCase(getString(R.string.male)))
							// genderButton.setChecked(false);
							// else
							// genderButton.setChecked(true);
						}
						String tmpPicId = SharedPrefManager.getInstance().getUserFileId(myUserName);
						if (objUserModel.imageFileId != null && !objUserModel.imageFileId.equals("")
								&& (tmpPicId == null || !objUserModel.imageFileId.equals(tmpPicId))) {
							SharedPrefManager.getInstance().saveUserFileId(userName, objUserModel.imageFileId);
							boolean isPic = setProfilePic(userName);
							if (tmpPicId == null || !objUserModel.imageFileId.equals(tmpPicId))
								isPic = false;
							if (!isPic) {
								if (Build.VERSION.SDK_INT >= 11)
									new BitmapDownloader(context,
											((RoundedImageView) findViewById(R.id.id_profile_pic))).executeOnExecutor(
													AsyncTask.THREAD_POOL_EXECUTOR, objUserModel.imageFileId,
													BitmapDownloader.PROFILE_PIC_REQUEST);
								else
									new BitmapDownloader(context, ((ImageView) findViewById(R.id.id_profile_pic)))
											.execute(objUserModel.imageFileId, BitmapDownloader.PROFILE_PIC_REQUEST);
								// (new
								// ProfilePicDownloader()).download(Constants.media_get_url+objUserModel.imageFileId+".jpg",((RoundedImageView)findViewById(R.id.id_profile_pic)),null);
							}
						} else
							setProfilePic(userName);

						if (dialog != null) {
							dialog.dismiss();
							dialog = null;
						}
						super.onSuccess(arg0, arg1);
					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						Log.d(TAG, "AsyncHttpClient onFailure: " + arg1);
						if (dialog != null) {
							dialog.dismiss();
							dialog = null;
						}
						// showDialog("Please try again later.");
						super.onFailure(arg0, arg1);
					}
				});
	}

	public int getArrayIndex(String[] arr, String value) {
		int k = 0;
		for (int i = 0; i < arr.length; i++) {

			if (arr[i].equalsIgnoreCase(value)) {
				k = i;
				break;
			}
		}
		return k;
	}

	public void showDialog(String title, String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		if (title != null)
			((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
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
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		// On selecting a spinner item
		String item = parent.getItemAtPosition(position).toString();
		String tag = (String) parent.getTag();
		if (tag != null) {
			if (tag.equals("1"))
				residenceType = item;
			else if (tag.equals("2"))
				gender = item;
		}
		Log.i(TAG, "onItemSelected : Gender option : " + item);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	public void showGenderDialog(final TextView gender_view) {
		// final CharSequence[] items = { getString(R.string.male),
		// getString(R.string.female) };
		final String[] items = getResources().getStringArray(R.array.gender_options);
		AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
		builder.setTitle(getString(R.string.select_gender));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				// will toast your selection
				gender = items[item].toString();
				if (gender != null && gender_view != null)
					gender_view.setText(gender);
				dialog.dismiss();
				if (purposeType == VIEWWING_AS_SELF_IN_REG) {
					if (isProfileValidForReg(false)) {
						// nextButtonView.setVisibility(Button.VISIBLE);
						if (saveButtonView != null) {
							saveButtonView.setBackgroundResource(R.drawable.round_rect_blue);
							saveButtonView.setVisibility(View.VISIBLE);
						}
					} else
						saveButtonView.setBackgroundResource(R.drawable.round_rect_gray);
				}

			}
		}).show();
	}

}
