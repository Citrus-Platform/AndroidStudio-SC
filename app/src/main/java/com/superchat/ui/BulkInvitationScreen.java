package com.superchat.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberModel.MemberDetail;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.ui.BulkInvitationAdapter.AppContact;
import com.superchat.utils.Constants;
import com.superchat.utils.Countries;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BulkInvitationScreen extends Activity implements OnClickListener, OnItemClickListener{
	private static final String TAG = "BulkInvitationScreen";
	public static final int CODE_COUNTRY_CHOOSER = 333;
	 HashMap<String ,AppContact> allContacts = new HashMap<String,AppContact>();
	 ArrayList<AppContact> dataList;
	 ListView listView;
	 BulkInvitationAdapter adapter;
	 TextView searchBoxView;
	 public TextView countTextView;
	 RelativeLayout searchLayout;
	 boolean inviteFromReg;
	 boolean isContactTabSelected = false;
	 LinearLayout contactTabLayout;
	 LinearLayout otherApssTabLayout;
	 PackageManager packageManager;
	 View viewContacts, viewOtherApps;
	 TextView contacts, otherApps;
	 TextView idBulkInfoLabel, copyLink;
	 ImageView idBulkInfo;
	 LinearLayout bottomLayout;
	 ImageView contactsIcon, otherAppsIcon;
	  Dialog inviteMenualDialog;	
	  EditText nameEditText;
	  EditText numberEditText;
	  TextView countryCodeEditText;
	  ImageView countryFlagView;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bulk_invitation_screen);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.getBoolean(Constants.REG_TYPE))
			inviteFromReg = true;
		if(inviteFromReg){
			((TextView)findViewById(R.id.id_cancel)).setText(getString(R.string.done_caps));
			((TextView)findViewById(R.id.id_back)).setVisibility(View.INVISIBLE);
			}
		((TextView)findViewById(R.id.id_cancel)).setOnClickListener(this);
		((TextView)findViewById(R.id.id_invite)).setOnClickListener(this);
		((ImageView)findViewById(R.id.id_bulk_info)).setOnClickListener(this);
		((RelativeLayout)findViewById(R.id.id_add_layout)).setOnClickListener(this);
		
		countTextView = (TextView)findViewById(R.id.id_select_count);
		 searchBoxView = (TextView)findViewById(R.id.id_search_box);
		listView = (ListView) findViewById(R.id.id_contacts_list);
		searchLayout = (RelativeLayout)findViewById(R.id.header_layout);
		contactTabLayout = (LinearLayout)findViewById(R.id.id_contacts);
		bottomLayout = (LinearLayout)findViewById(R.id.id_bottom_layout);
		otherApssTabLayout = (LinearLayout)findViewById(R.id.id_other_apps);
		viewContacts = (View)findViewById(R.id.view_contacts);
		viewOtherApps = (View)findViewById(R.id.view_otherapps);
		contacts = (TextView)findViewById(R.id.text_contacts);
		otherApps = (TextView)findViewById(R.id.text_otherapps);
		idBulkInfoLabel = (TextView)findViewById(R.id.id_select_contact);
		idBulkInfo = (ImageView)findViewById(R.id.id_bulk_info);
		contactsIcon = (ImageView)findViewById(R.id.contacts_icon);
		otherAppsIcon = (ImageView)findViewById(R.id.other_apps_icon);
		copyLink = (TextView)findViewById(R.id.id_copy_link);
		copyLink.setOnClickListener(this);
		contactTabLayout.setOnClickListener(this);
		otherApssTabLayout.setOnClickListener(this);
		
		new ContactLoadingTask().execute();
		
		searchBoxView.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable editable) {
				if(dataList!=null)
					dataList.clear();
				dataList = new ArrayList<AppContact>();
				for(String key: allContacts.keySet()){
					AppContact appContact = allContacts.get(key);
					dataList.add(appContact);
//					Log.d("CONTACT_FATCH", "After: displayedNumber : "+appContact.getName()+" , "+appContact.getDisplayNumber()+" , "+appContact.getNumber());
					}
				Collections.sort(dataList);
				if(dataList==null || dataList.isEmpty())
					return;
				String s1 = (new StringBuilder()).append(searchBoxView.getText().toString()).toString();
					boolean isAllShown = false;
					ArrayList <AppContact> listClone = new ArrayList<AppContact>(); 
					if(s1!=null && !s1.trim().equals("")){
			           for (AppContact tmpContact : dataList) {
			        	  String tmpSearch = tmpContact.getName();
			               if(tmpSearch.toLowerCase().contains(s1.toLowerCase())){
			                   listClone.add(tmpContact);
			               }
			           }
			           }else{
			        	   listClone.addAll(dataList);
			        	   isAllShown = true;
			           }
					adapter.clear();
//					ArrayList<AppContact> list = new ArrayList<AppContact>();
					for(AppContact member:listClone){
//						if(SharedPrefManager.getInstance().getUserName().equals(member.userName))
//							continue;
						AppContact info = new AppContact();
						info.setDisplayNumber(member.getDisplayNumber());
						info.setNumber(member.getNumber());
						info.setName(member.getName());
////						list.add(info);
						adapter.add(info);
					}
//					adapter.addAll(listClone);
					adapter.notifyDataSetChanged();
			}

			public void beforeTextChanged(CharSequence charsequence, int i,
					int j, int k) {
			}

			public void onTextChanged(CharSequence charsequence, int i, int j,
					int k) {
			}

		});

		 View view = getCurrentFocus();
		 searchBoxView.setFocusable(false);
		 searchBoxView.setOnTouchListener(new View.OnTouchListener() {
		             @Override
		             public boolean onTouch(View v, MotionEvent event) {
		            	 searchBoxView.setFocusableInTouchMode(true);
		                 return false;
		             }
		 });
         if (view != null) {
             InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
             
         }
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         
         inviteMenualDialog = new Dialog(this);
         inviteMenualDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         inviteMenualDialog.setContentView(R.layout.invite_contact_dialog);
         inviteMenualDialog.findViewById(R.id.id_invite_btn).setOnClickListener(this);
         inviteMenualDialog.findViewById(R.id.id_cancel_btn).setOnClickListener(this);
         nameEditText = (EditText)inviteMenualDialog.findViewById(R.id.id_display_name_field);
         numberEditText = (EditText)inviteMenualDialog.findViewById(R.id.id_invite_number);
         countryCodeEditText =(TextView)inviteMenualDialog.findViewById(R.id.id_country_code1);
         
         countryFlagView = (ImageView)inviteMenualDialog.findViewById(R.id.id_country_flag1);
			countryFlagView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BulkInvitationScreen.this,CountryChooserScreen.class);
					startActivityForResult(intent,CODE_COUNTRY_CHOOSER);
					
				}
			});
			countryCodeEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BulkInvitationScreen.this,CountryChooserScreen.class);
					startActivityForResult(intent,CODE_COUNTRY_CHOOSER);
					
				}
			});
			String cc = Constants.countryCode.replace("+", "");
			countryCodeEditText.setText("+"+cc);
			cc = Countries.getCountryNameViaCode(cc).getCountryCode();
			
			if(Build.VERSION.SDK_INT >= 16)
				countryFlagView.setBackground(getDrawableFromAsset(cc.toUpperCase()+".png"));
			else
				countryFlagView.setImageBitmap(getBitmapFromAsset(cc.toUpperCase()+".png"));
			
//			numberEditText.addTextChangedListener(new TextWatcher() {
//
//				public void afterTextChanged(Editable editable) {
//					String s1 = (new StringBuilder()).append(numberEditText.getText().toString()).toString();
//					if(s1.length()>0 &&s1.length()<5 && !s1.contains("-")){
//						String cc = s1.replace("+", "");
//						Countries.Country myCountry = Countries.getCountryNameViaCode(cc);
//						if(myCountry!=null){
//							cc = myCountry.getCountryCode();
//							if(Build.VERSION.SDK_INT >= 16)
//								countryFlagView.setBackground(getDrawableFromAsset(cc.toUpperCase()+".png"));
//							else
//								countryFlagView.setImageBitmap(getBitmapFromAsset(cc.toUpperCase()+".png"));
//						}
//						}
//				}
//
//				public void beforeTextChanged(CharSequence charsequence, int i,
//						int j, int k) {
//				}
//
//				public void onTextChanged(CharSequence charsequence, int i, int j,
//						int k) {
//				}
//
//			});
	}

public void onClick(View view){
	switch(view.getId()){
	case R.id.row_layout:
		try{
			LinearLayout ll = (LinearLayout) view.findViewById(R.id.row_layout);
			Log.i(TAG, "Clicked : "+ll.getTag());
			
			String text = getString(R.string.invite_language);
			if(text.contains("$username"))
				text = text.replace("$username", SharedPrefManager.getInstance().getDisplayName());
			if(text.contains("$groupname"))
				text = text.replace("$groupname", SharedPrefManager.getInstance().getUserDomain());
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, text);
			sendIntent.setType("text/plain");
			sendIntent.setPackage(ll.getTag().toString());
			startActivity(sendIntent);
		}catch(ActivityNotFoundException ex){
			ex.printStackTrace();
		}
		break;
	case R.id.id_copy_link:
		if(!isContactTabSelected){
			 String text = getString(R.string.invite_language);
			if(text.contains("$username"))
				text = text.replace("$username", SharedPrefManager.getInstance().getDisplayName());
			if(text.contains("$groupname"))
				text = text.replace("$groupname", SharedPrefManager.getInstance().getUserDomain());
			ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
			Toast.makeText(BulkInvitationScreen.this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
		}
		break;
	case R.id.id_contacts:
		isContactTabSelected = true;
		searchLayout.setVisibility(View.VISIBLE);
		viewOtherApps.setVisibility(View.GONE);
		bottomLayout.setVisibility(View.VISIBLE);
		viewContacts.setVisibility(View.VISIBLE);
		otherApps.setTextColor(R.color.black);
		contacts.setTextColor(getResources().getColor(R.color.color_lite_blue));
		copyLink.setVisibility(View.GONE);
		idBulkInfo.setVisibility(View.VISIBLE);
		idBulkInfoLabel.setVisibility(View.VISIBLE);
		contactsIcon.setImageResource(R.drawable.invite_contacts_sel);
		otherAppsIcon.setImageResource(R.drawable.invite_otherapps);
		
//		copyTextLable.setTextColor(getResources().getColor(R.color.black));
//		copyTextLable.setText(getString(R.string.select_contact_send_invite));
//		copyTextLable.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		new ContactLoadingTask().execute();
		break;
	case R.id.id_other_apps:
		isContactTabSelected = false;
		viewContacts.setVisibility(View.GONE);
		bottomLayout.setVisibility(View.GONE);
		viewOtherApps.setVisibility(View.VISIBLE);
		searchLayout.setVisibility(View.GONE);
		idBulkInfo.setVisibility(View.GONE);
		otherApps.setTextColor(getResources().getColor(R.color.color_lite_blue));
		contacts.setTextColor(R.color.black);
		idBulkInfoLabel.setVisibility(View.GONE);
		copyLink.setVisibility(View.VISIBLE);
		contactsIcon.setImageResource(R.drawable.invite_contacts);
		otherAppsIcon.setImageResource(R.drawable.invite_otherapps_sel);
		
//		copyTextLable.setTextColor(getResources().getColor(R.color.header_footer_color));
//		copyTextLable.setText(getString(R.string.copy_invite_link));
//		copyTextLable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.copy_link, 0, 0, 0);
//		copyTextLable.setPadding(30, 0, 0, 0);
		packageManager = getPackageManager();
		List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
	    List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();
	    packageList1 = isAppAllowed(packageList);
	    adapter = new BulkInvitationAdapter(BulkInvitationScreen.this,R.layout.apklist_item, dataList);
		listView.setAdapter(new ApkAdapter(this, packageList1, packageManager));
		listView.setOnItemClickListener(this);
		break;
	case R.id.id_cancel:
		if(!inviteFromReg){
			finish();
		}else{
			Intent intent = new Intent(BulkInvitationScreen.this, HomeScreen.class);
			intent.putExtra("ADMIN_FIRST_TIME", true);
			startActivity(intent);
			finish();
		}
		break;
	case R.id.id_add_layout:
		if(inviteMenualDialog!=null && !inviteMenualDialog.isShowing()){
			if(numberEditText!=null)
				numberEditText.setText("");
			String cc = Constants.countryCode.replace("+", "");
			countryCodeEditText.setText("+"+cc);
			cc = Countries.getCountryNameViaCode(cc).getCountryCode();
			if(Build.VERSION.SDK_INT >= 16)
				countryFlagView.setBackground(getDrawableFromAsset(cc.toUpperCase()+".png"));
			else
				countryFlagView.setImageBitmap(getBitmapFromAsset(cc.toUpperCase()+".png"));
			
			
			inviteMenualDialog.show();
		}
		break;
	case R.id.id_cancel_btn:
		if(inviteMenualDialog!=null && inviteMenualDialog.isShowing())
			inviteMenualDialog.cancel();
		break;
	case R.id.id_invite_btn:
		String enteredName = nameEditText.getText().toString();
		if(!checkName(enteredName)){
			Toast.makeText(this, "Please type full name.", Toast.LENGTH_SHORT).show();
			break;
		}
		String enteredNumber = numberEditText.getText().toString();
		if(enteredNumber==null || enteredNumber.equals("") || enteredNumber.length() < 8){
			Toast.makeText(this, "Enter valid number.", Toast.LENGTH_SHORT).show();
			break;
		}
		ArrayList<MemberDetail> members1 = new ArrayList<MemberDetail>();
		MemberDetail memberDetail1 = new MemberDetail();
		memberDetail1.name   = enteredName;
		memberDetail1.mobileNumber = countryCodeEditText.getText().toString().replace("+", "")+"-"+enteredNumber;
		members1.add(memberDetail1);
		AddMemberModel requestForm1 = new AddMemberModel(members1);
		requestForm1.domainName = SharedPrefManager.getInstance().getUserDomain();
		if(inviteMenualDialog!=null && inviteMenualDialog.isShowing())
			inviteMenualDialog.cancel();
		if(Build.VERSION.SDK_INT >= 11)
			new BulkInviteServerTask(requestForm1, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new BulkInviteServerTask(requestForm1, view).execute();
		break;
	case R.id.id_invite:
		
		ArrayList<String> selectedMembersList = adapter.getSelectedMembers();
		if(selectedMembersList == null || selectedMembersList.isEmpty()){
			showDialog(getString(R.string.none_selected));
			return;
		}
		ArrayList<MemberDetail> members = new ArrayList<MemberDetail>();
		for(String selectedMembers:selectedMembersList){
			AppContact contact = allContacts.get(selectedMembers);
			if(contact!=null){
				MemberDetail memberDetail = new MemberDetail();
//				memberDetail.name   = "User";//contact.getName();
				if(contact.getName() != null)//if(contact.getName() != null && checkName(contact.getName()))
					memberDetail.name   = contact.getName();
				else
					memberDetail.name   = "Superchatter";
				memberDetail.mobileNumber = contact.getNumber();
				if(memberDetail.mobileNumber != null && memberDetail.mobileNumber.length() == 10){
					String cc = SharedPrefManager.getInstance().getUserCountryCode();
					if(cc == null || (cc != null && cc.trim().length() == 0))
						cc = getCountryCode();
					if(cc != null && cc.startsWith("+")){
						cc = cc.substring(1);
					}
					memberDetail.mobileNumber = cc + "-" + memberDetail.mobileNumber;
				}
//				memberDetail.gender = "donotdisclose";
//				memberDetail.gender = null;
//				Log.d("selectedMembersList", "selectedMembers : "+contact.getName()+" , "+contact.getNumber());
				Log.d("selectedMembersList", "selectedMembers : "+contact.getId()+" , "+contact.getNumber());
				members.add(memberDetail);
				}
		}
		
		AddMemberModel requestForm = new AddMemberModel(members);//formatedNumber, "normal",imei, imsi, clientVersion);
		requestForm.domainName = SharedPrefManager.getInstance().getUserDomain();
		if(Build.VERSION.SDK_INT >= 11)
			new BulkInviteServerTask(requestForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new BulkInviteServerTask(requestForm, view).execute();
		break;
	case R.id.id_bulk_info:
		startActivity(new Intent(BulkInvitationScreen.this,InviteViaEmailScreen.class));
		break;
	}
}
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == RESULT_OK){
		switch(requestCode){
		case CODE_COUNTRY_CHOOSER:
			Bundle dBundle = data.getExtras();
			String code = "+91";
			String country = "India";
			String countryCode = null;
			if(dBundle!=null){
				country = dBundle.getString("COUNTRY_NAME", "India");
				code = dBundle.getString("STD_CODE", "+91");
				countryCode  = dBundle.getString("COUNTRY_CODE", null);
			}
			
//			String selected = ""+ ((Countries.Country) spinner.getSelectedItem()).getCode();
//
//			countryName = ((Countries.Country) spinner.getSelectedItem()).getStationName();
//			if(numberEditText != null) {
//				numberEditText.setText(code+"-");
//			}
			countryCodeEditText.setText(code);
//			if(countryNameView != null)
//				countryNameView.setText(country);
			
			if(countryFlagView != null){
				if(countryCode == null)
					countryFlagView.setVisibility(View.INVISIBLE);
				else{
					countryFlagView.setVisibility(View.VISIBLE);
					if(Build.VERSION.SDK_INT >= 16)
						countryFlagView.setBackground(getDrawableFromAsset(countryCode+".png"));
					else
						countryFlagView.setImageBitmap(getBitmapFromAsset(countryCode+".png"));
				}
			}
			break;
		}
	}
	}
private List<PackageInfo> isAppAllowed(List<PackageInfo> packageList){
	String[] apps_filter = {"whatsapp", "facebook", "bbm", "hike", "line", "com.google.android.gm", "com.android.email", "com.google.android.email", "com.android.mms", "com.google.android.talk", "twitter", "skype", "wechat", "telegram", "tango", "viber",
			"linkedin", "imo"};
	List<PackageInfo> filteredList = new ArrayList<PackageInfo>();
	String app_name = null;
	for(String name : apps_filter){
		for(PackageInfo pi : packageList) {
//	        boolean b = isSystemPackage(pi);
//	        System.out.println("Package Name => "+pi.packageName.toString());
//	        if(!b)
	        {
	        	app_name = pi.packageName.toString();
	        	if(app_name.contains(name) && !app_name.equals("com.google.android.gm.exchange")
	        			&& !app_name.equals("com.google.android.gms")
	        			&& !app_name.equals("com.sec.android.app.minimode.res")
	        			&& !app_name.equals("com.scimob.ninetyfour.percent")
	        			&& !app_name.equals("com.android.mms.service")){
	        		Log.i(TAG, "Package Name => "+pi.packageName.toString());
	        		if(!filteredList.contains(pi)){
	        			filteredList.add(pi);
	        		}
	        	}
	        }
	    }
	}
	return filteredList;
}
private boolean isSystemPackage(PackageInfo pkgInfo) {
    return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
}

class ContactLoadingTask  extends AsyncTask<String,String,String>{
	ProgressDialog progressDialog;
	List<String> numbers = new ArrayList<String>();
	@Override
	protected void onPreExecute() {
			progressDialog = ProgressDialog.show(BulkInvitationScreen.this, "", "Contact loading. Please wait...", true);
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... params) {
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String THUMBNAIL_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
		Uri PHONECONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Cursor cursor = BulkInvitationScreen.this.getContentResolver().query(PHONECONTENT_URI, null,
				null, null, null);
		if (cursor == null) {
			return null;
		}
		numbers = DBWrapper.getInstance().getAllNumbers();
		while (cursor.moveToNext()) {
			String displayNumber = cursor.getString(cursor.getColumnIndex(NUMBER));
			String displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
			String thumbUri = cursor.getString(cursor.getColumnIndex(THUMBNAIL_URI));
			if(displayNumber!=null && displayNumber.contains(" "))
				displayNumber = displayNumber.replace(" ", "");
			String number = formatNumber(displayNumber);
			if(numbers.contains(number))
				continue;
			AppContact appContact = new AppContact();
			appContact.setId(thumbUri);
			appContact.setName(displayName);
			appContact.setNumber(number);
			appContact.setDisplayNumber(displayNumber);
			allContacts.put(displayNumber,appContact);
		}
		return null;
	}
	@Override
	protected void onPostExecute(String str) {
//		AppContact[] appContacts = allContacts.values().toArray(new AppContact[allContacts.values().size()]);
//		Arrays.sort(appContacts);
		 dataList = new ArrayList<AppContact>();
		
		for(String key: allContacts.keySet()){
			AppContact appContact = allContacts.get(key);
			dataList.add(appContact);
//			Log.d("CONTACT_FATCH", "After: displayedNumber : "+appContact.getName()+" , "+appContact.getDisplayNumber()+" , "+appContact.getNumber());
			}
		Collections.sort(dataList);
		
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		// 
		adapter = new BulkInvitationAdapter(BulkInvitationScreen.this,R.layout.bulk_invite_item,dataList);
		listView.setAdapter(null);
		listView.setAdapter(adapter);
	}
}
//class AppContact implements Comparable{
//	String id;
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getNumber() {
//		return number;
//	}
//	public void setNumber(String number) {
//		this.number = number;
//	}
//	public String getEmail() {
//		return email;
//	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
//	String name;
//	String number;
//	String displayNumber;
//	public String getDisplayNumber() {
//		return displayNumber;
//	}
//	public void setDisplayNumber(String displayNumber) {
//		this.displayNumber = displayNumber;
//	}
//	@Override
//	public int compareTo(Object another) {
//			String tmpName = ((AppContact)another).getName();
//		return this.name.compareToIgnoreCase(tmpName);
//	}
//	String email;
//	AppContact(){
//		
//	}
//	
//}
public static String formatNumber(String str){
	try{
		if(str==null)
			return null;
	
		boolean isCountryCheckingNeeded = false;
		if(str.startsWith("00"))
		isCountryCheckingNeeded = true;
		if(str.length()>1)
			while(str.startsWith("0")){					
				if(str.length()>1)
					str = str.substring(1);
				else break;
			}
		
		
	boolean isPlus = str.contains("+")?true:false;
//	if(isPlus)
		isCountryCheckingNeeded = true;
	
	str = str.replace(" ","");
	str = str.replace("+","");
	str = str.replace("-","");
	str = str.replace("(","");
	str = str.replace(")","");
	
	if(str.length() <= 10)
		return str;
	
	String replacingCode = null;
	boolean isNumberModified = false;
	if(isCountryCheckingNeeded){
		for(int i = 5;i>=1;i--){
			replacingCode = str.substring(0, i);
		if(SuperChatApplication.countrySet.contains(replacingCode)){
			str = replacingCode+"-"+str.replaceFirst(replacingCode, "");
			isNumberModified = true;
			break;
		}}
	}
	if(!isNumberModified)
	{
		String code = Constants.countryCode.replace("+", "");
		if(str.startsWith(code))
			str = code+"-"+str.replaceFirst(code, "");
		else
			str = code+"-"+str;
	}
	
	}catch(Exception e){
		e.printStackTrace();
	}
	return str;
}
private class BulkInviteServerTask extends AsyncTask<String, String, String> {
	
	AddMemberModel requestForm;
	ProgressDialog progressDialog = null;
	public BulkInviteServerTask(AddMemberModel requestForm,final View view1){
		this.requestForm = requestForm;
	}
	@Override
	protected void onPreExecute() {		
		progressDialog = ProgressDialog.show(BulkInvitationScreen.this, "", "Loading. Please wait...", true);
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String JSONstring = new Gson().toJson(requestForm);		    
		DefaultHttpClient client1 = new DefaultHttpClient();
		Log.d(TAG, "InviteMemberServerTask request:"+JSONstring);
		HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/admin/inviteuser");
		 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
		 HttpResponse response = null;
		try {
			httpPost.setEntity(new StringEntity(JSONstring));
			try {
				response = client1.execute(httpPost);
				final int statusCode=response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK){
					HttpEntity entity = response.getEntity();
					BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = "";
					String str = "";
					while ((line = rd.readLine()) != null) {
						
						str+=line;
					}
					Log.d(TAG, "invite Result : "+str);
					if(str!=null &&!str.equals("")){
						str = str.trim();
						Gson gson = new GsonBuilder().create();
						if (str==null || str.contains("error")){
							return str;
						}
						AddMemberResponseModel regObj = gson.fromJson(str, AddMemberResponseModel.class);
						if (regObj != null) {
							if(regObj.accountCreated!=null && !regObj.accountCreated.isEmpty()){
								dialogHandler.sendEmptyMessage(1);//showDialog("User invitation sent",true);
							}else if(regObj.accountAlreadyExists!=null && !regObj.accountAlreadyExists.isEmpty()){
								dialogHandler.sendEmptyMessage(2);//showDialog("User already added.",false);
							}else if(regObj.accountFailed!=null && !regObj.accountFailed.isEmpty()){
								dialogHandler.sendEmptyMessage(3);//showDialog("User already added in this domain.",false);
							}								
						}
						return str;
//							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//							if (iPrefManager != null
//									&& iPrefManager.getUserId() != 0) {
//								if (iPrefManager.getUserId() != regObj.iUserId) {
//									try {
//										DBWrapper.getInstance().clearMessageDB();
//										iPrefManager.clearSharedPref();
//									} catch (Exception e) {
//									}
//								}
//							}
//							Log.d(TAG, "Esia chat registration password mobileNumber: " + regObj.getPassword()+" , "+regObj.iMobileNumber);
//							iPrefManager.saveUserDomain(domainNameView.getText().toString());
//							iPrefManager.saveUserEmail(memberEmail.getText().toString());
//							iPrefManager.saveDisplayName(memberName.getText().toString());
//							iPrefManager.saveUserOrgName(memberDeparment.getText().toString());
//							iPrefManager.saveAuthStatus(regObj.iStatus);
//							iPrefManager.saveDeviceToken(regObj.token);
//							iPrefManager.saveUserId(regObj.iUserId);
//							iPrefManager.setAppMode("VirginMode");
//							iPrefManager.saveUserPhone(regObj.iMobileNumber);
//							iPrefManager.saveUserLogedOut(false);
//							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//						}
//						Intent intent = new Intent(InviteMemberScreen.this, MobileVerificationScreen.class);
//						intent.putExtra(Constants.MOBILE_NUMBER_TXT,formatedNumber);
//						intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
//						if(isAdmin)
//							intent.putExtra(Constants.REG_TYPE, "ADMIN");
//						else
//							intent.putExtra(Constants.REG_TYPE, "USER");
//						startActivity(intent);
//						finish();
					}
				}
			} catch (ClientProtocolException e) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
			} catch (IOException e) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
			}
			
		} catch (UnsupportedEncodingException e1) {
			Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
		}catch(Exception e){
			Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
		}
		
		
		return null;
	}
	@Override
	protected void onPostExecute(String str) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (str!=null && str.contains("error")){
			Gson gson = new GsonBuilder().create();
			ErrorModel errorModel = null;
			try{
				errorModel = gson.fromJson(str,ErrorModel.class);
			}catch(Exception e){}
			if (errorModel != null) {
				if (errorModel.citrusErrors != null
						&& !errorModel.citrusErrors.isEmpty()) {
					ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
//					if(citrusError!=null && citrusError.code.equals("20019") ){
//						SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//						iPrefManager.saveUserDomain(domainNameView.getText().toString());
//						iPrefManager.saveUserId(errorModel.userId);
//						iPrefManager.setAppMode("VirginMode");
////						iPrefManager.saveUserPhone(regObj.iMobileNumber);
//						//						iPrefManager.saveUserPassword(regObj.getPassword());
//						iPrefManager.saveUserLogedOut(false);
//						iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//						showDialogWithPositive(citrusError.message);
//					}else 
					if(citrusError!=null)
						showDialog(citrusError.message);
					else
						showDialog("Please try again later.");
				} else if (errorModel.message != null)
					showDialog(errorModel.message);
			} else
				showDialog("Please try again later.");
		}else if (str!=null && str.contains("success")){
			if(nameEditText != null)
				nameEditText.setText("");
		}
		super.onPostExecute(str);
	}
	
	
}
Handler dialogHandler = new Handler(){
	@Override
	public void handleMessage(android.os.Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		int type = msg.what;
		if(countTextView!=null){
			countTextView.setText("");
			countTextView.setVisibility(View.GONE);
		}
		switch(type){
		case 1:
//			showDialog("User invitation sent",true);
			Toast.makeText(BulkInvitationScreen.this, "Invite sent", Toast.LENGTH_SHORT).show();
//			if(inviteFromReg){
//				Intent intent = new Intent(BulkInvitationScreen.this, HomeScreen.class);
//				intent.putExtra("ADMIN_FIRST_TIME", true);
//				startActivity(intent);
//				finish();
//			}
//			else
//				restartActivity(BulkInvitationScreen.this);
			break;
		case 2:
//			showDialog("User already added.",false);
			Toast.makeText(BulkInvitationScreen.this, "Invite already sent!", Toast.LENGTH_SHORT).show();
			break;
		case 3:
//			showDialog("User already added in this domain.",false);
			Toast.makeText(BulkInvitationScreen.this, "Invite already sent!", Toast.LENGTH_SHORT).show();
			break;
		}
		if(adapter!=null){
			adapter.removeSelectedItems();
			adapter.notifyDataSetChanged();
		}
	}
};
public void restartActivity(Activity activity) {
	if (Build.VERSION.SDK_INT >= 11) {
		activity.recreate();
	} else {
		activity.finish();
		activity.startActivity(getIntent());
	}
}

public void onBackClick(View view){
	if(inviteFromReg)
		return;
	finish();
}
@Override
public void onBackPressed() {
	if(inviteFromReg)
		return;
	finish();
}
public void showDialog(String s,final boolean needExit) {
	final Dialog bteldialog = new Dialog(this);
	bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	bteldialog.setCanceledOnTouchOutside(false);
	bteldialog.setContentView(R.layout.custom_dialog);
	((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
	((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			bteldialog.cancel();
			if(inviteFromReg){
				Intent intent = new Intent(BulkInvitationScreen.this, HomeScreen.class);
				intent.putExtra("ADMIN_FIRST_TIME", true);
				startActivity(intent);
				finish();
			}else
			if(needExit){
//				Intent intent = new Intent(InviteMemberScreen.this, HomeScreen.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//						| Intent.FLAG_ACTIVITY_CLEAR_TASK
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				finish();
				}
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
	((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
	((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			bteldialog.cancel();
			return false;
		}
	});
	bteldialog.show();
}
//-------------get Installed application list with filters--------
class ApkAdapter extends BaseAdapter {
	 
    List<PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;
 
    public ApkAdapter(Activity context, List<PackageInfo> packageList,
            PackageManager packageManager) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.packageManager = packageManager;
    }
 
    private class ViewHolder {
        TextView apkName;
        LinearLayout layout;
        ImageView imageView;
    }
 
    public int getCount() {
        return packageList.size();
    }
 
    public Object getItem(int position) {
        return packageList.get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
 
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.apklist_item, null);
            holder = new ViewHolder();
 
            holder.layout = (LinearLayout) convertView.findViewById(R.id.row_layout);
            holder.imageView = (ImageView) convertView.findViewById(R.id.contact_icon);
            holder.apkName = (TextView) convertView.findViewById(R.id.appname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try{
        PackageInfo packageInfo = (PackageInfo) getItem(position);
        Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
//        String appName = packageInfo.applicationInfo.packageName.toString();
//        appIcon.setBounds(0, 0, 100, 100);
        holder.imageView.setImageDrawable(appIcon);
//        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
//        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(appName);
        Log.i(TAG, "===>"+packageInfo.applicationInfo.packageName.toString());
        holder.layout.setTag(packageInfo.applicationInfo.packageName.toString());
        holder.layout.setOnClickListener(BulkInvitationScreen.this);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        return convertView;
    }
}
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	switch(view.getId()){
	case R.id.id_contact_title:
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.id_contact_title);
		Log.i(TAG, "Clicked : "+ll.getTag());
		break;
	}
}
private Drawable getDrawableFromAsset(String strName)
{
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
private Bitmap getBitmapFromAsset(String strName)
{
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
//==================
	public boolean checkName(String name) {
		if (name == null)
			return false;
		Matcher m = Constants.NAME_PATTERN.matcher(name);
		return m.matches();
	}
	public String getCountryCode(){
		String code = null;
		TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
		String  cc = tm.getNetworkCountryIso();
		if(cc != null && cc.trim().length() != 0)
		{
			Countries.Country countryObj = Countries.getCodeValue(cc);
			if(countryObj!=null){
				code = "+"+countryObj.getCode();
			}
		}
		return code;
	}
}
