
package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.sdk.db.ChatDBWrapper;
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
import com.superchat.ui.BulkInvitationAdapter.AppContact;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.widgets.RoundedImageView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SupergroupListingScreen extends Activity implements OnClickListener, OnItemClickListener{
	private static final String TAG = "SupergroupListingScreen";
	 HashMap<String ,AppContact> allContacts = new HashMap<String,AppContact>();
	 ArrayList<AppContact> dataList;
	 ListView listView;
	 PackageManager packageManager;
	 LinearLayout bottomLayout;
	 EditText numberEditText;
	 String countryCode = "91";
	 String mobileNumber = null;
	 Button joinAsMember, createSG;
	 String superGroupName = "";
	 String displayName = null;
	 ExpandableListView expandableListView;
	    ExpandableListAdapter expandableListAdapter;
	    List<String> expandableListTitle;
	    HashMap<String, List<String>> expandableListDetail;
	    ArrayList<String> ownerDomainNameSet = null;
		ArrayList<String> invitedDomainNameSet = null;
		ArrayList<String> joinedDomainNameSet = null;
		boolean newUser;
		boolean singleEntry;
		boolean showAlertForAlreadyOwnedSG;
		Dialog picChooserDialog;
		ProfilePicUploader picUploader;
		ImageView superGroupIconView;
		boolean pendingProfile;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 Bundle bundle = getIntent().getExtras();
		 int size = 0;
		 if(bundle != null){
//			countryCode = bundle.getString(Constants.COUNTRY_CODE_TXT);
			 mobileNumber = bundle.getString(Constants.MOBILE_NUMBER_TXT);
			 ownerDomainNameSet = bundle.getStringArrayList("OWNERDOMAINNAMESET");
			 if(ownerDomainNameSet!=null && !ownerDomainNameSet.isEmpty())
				 Collections.sort(ownerDomainNameSet);
			 invitedDomainNameSet = bundle.getStringArrayList("INVITEDDOMAINSET");
			 if(invitedDomainNameSet!=null && !invitedDomainNameSet.isEmpty())
				 Collections.sort(invitedDomainNameSet);
			 joinedDomainNameSet = bundle.getStringArrayList("JOINEDDOMAINSET");
			 if(joinedDomainNameSet!=null && !joinedDomainNameSet.isEmpty())
				 Collections.sort(joinedDomainNameSet);
			 showAlertForAlreadyOwnedSG = bundle.getBoolean("SHOW_OWNED_ALERT");
			 
			 size = (ownerDomainNameSet != null ? ownerDomainNameSet.size() : 0) 
					 + (invitedDomainNameSet != null? invitedDomainNameSet.size() : 0)
					 + (joinedDomainNameSet != null? joinedDomainNameSet.size() : 0);
			 if(size == 1 && ownerDomainNameSet == null && joinedDomainNameSet ==  null){
				 newUser = true;
				 superGroupName = invitedDomainNameSet.get(0);
				 String sg_name = superGroupName;
				 JSONObject json;
				try {
					json = new JSONObject(superGroupName);
					if(json != null && json.has("domainName")){
						sg_name = json.getString("domainName");
						superGroupName = sg_name;
					}
					else
						sg_name = superGroupName;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
//			 if(!newUser && size == 1){
//				 //Directly go for registration to that supergroup
//				 setContentView(R.layout.sg_new_user);
//				 if(ownerDomainNameSet != null && ownerDomainNameSet.size() == 1)
//					 superGroupName = ownerDomainNameSet.get(0);
//				 else if(joinedDomainNameSet != null && joinedDomainNameSet.size() == 1)
//					 superGroupName = joinedDomainNameSet.get(0);
//				 else if(invitedDomainNameSet != null && invitedDomainNameSet.size() == 1)
//					 superGroupName = invitedDomainNameSet.get(0);
//				 singleEntry = true;
////				 ((EditText)findViewById(R.id.id_sg_name_field)).setText(superGroupName);
////				 registerUserOnServer(superGroupName, listView);
//				 
//				 	String sg_name = superGroupName;
//	                String inviter = null;
//	                String file_id = null;
//	                try {
//	    				JSONObject json = new JSONObject(superGroupName);
//	    				if(json != null && json.has("domainName")){
//	    					sg_name = json.getString("domainName");
//	    					superGroupName = sg_name;
//	    				}
//	    				else
//	    					sg_name = superGroupName;
//	    				if(json != null && json.has("adminName"))
//	    					inviter = json.getString("adminName");
//	    				else
//	    					inviter = null;
//	    				if(json != null && json.has("logoFileId"))
//	    					file_id = json.getString("logoFileId");
//	    				else
//	    					file_id = null;
//	    			} catch (JSONException e) {
//	    				// TODO Auto-generated catch block
//	    				e.printStackTrace();
//	    			}
//	             ((EditText)findViewById(R.id.id_sg_name_field)).setText(sg_name);
//	             if(ownerDomainNameSet != null && ownerDomainNameSet.size() == 1)
//	            	 showWelcomeScreen(sg_name, inviter, file_id, 2);
//	             else
//	            	 showWelcomeScreen(sg_name, inviter, file_id, 3);
//				 
//			 }else 
			 if(size > 0){
					 if(ownerDomainNameSet != null && ownerDomainNameSet.size() >= 1){
						 if(joinedDomainNameSet != null && joinedDomainNameSet.contains(ownerDomainNameSet.get(0).toString()))
							 joinedDomainNameSet.remove(ownerDomainNameSet.get(0).toString());
				}
				 //This is new member, show him different view
				 listView = (ListView) findViewById(R.id.id_contacts_list);
				 setContentView(R.layout.sg_listing_for_reg);
			 }else{
				 newUser = true;
				 setContentView(R.layout.sg_new_user);
				 if(superGroupName != null)
					 ((EditText)findViewById(R.id.id_sg_name_field)).setText(superGroupName);
			 }
		 }
		 if(!newUser){
//			 listView = (ListView) findViewById(R.id.id_contacts_list);
			 SharedPrefManager.getInstance().setFirstTime(false);
		 }else
			 SharedPrefManager.getInstance().setFirstTime(true);
		bottomLayout = (LinearLayout)findViewById(R.id.id_bottom_layout);
		joinAsMember = (Button)findViewById(R.id.as_user);
		createSG = (Button)findViewById(R.id.create_sg);
		 final View view = getCurrentFocus();
         if (view != null) {
             InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
             
         }
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         
//         if(Build.VERSION.SDK_INT >= 11)
//			new GetRegisteredSGList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		 else
//			new GetRegisteredSGList().execute();
         
         
        final EditText domaine_name = ((EditText)findViewById(R.id.id_sg_name_field));
 		domaine_name.setOnEditorActionListener(new OnEditorActionListener() {
 		    @Override
 		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
 		        if (actionId == EditorInfo.IME_ACTION_DONE) {
 		        	if(domaine_name.getText().toString().trim().length() < 3){
 		        		Toast.makeText(SupergroupListingScreen.this, getString(R.string.enter_sg_name_to_continue), Toast.LENGTH_SHORT).show();
 		        		return false;
 		        	}
 		        	String text = domaine_name.getText().toString();
 		        	registerUserOnServer(text, view);
 		            return true;
 		        }
 		        return false;
 		    }
 		});
         
         joinAsMember.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				superGroupName = domaine_name.getText().toString();
					boolean contains = false;
					if(ownerDomainNameSet != null && ownerDomainNameSet.contains(superGroupName))
						contains = true;
					else if(invitedDomainNameSet != null && invitedDomainNameSet.contains(superGroupName))
						contains = true;
					else if(joinedDomainNameSet != null && joinedDomainNameSet.contains(superGroupName))
						contains = true;
					if(!contains)
						newUser = true;
					
					JSONObject json;
					String sg_name;
					String inviter = null;
		            String file_id = null;
		            String org_name = null;
		            String domainType = null;
		            boolean sg_found = false;
				if(superGroupName != null && superGroupName.trim().length() > 0){
					if(invitedDomainNameSet != null){
						for(String data : invitedDomainNameSet){
							try {
								json = new JSONObject(data);
								if(json != null && json.has("domainName")){
									sg_name = json.getString("domainName");
									if(sg_name.equalsIgnoreCase(superGroupName)){
										if(json != null && json.has("adminName"))
					    					inviter = json.getString("adminName");
					    				else
					    					inviter = null;
					    				if(json != null && json.has("logoFileId"))
					    					file_id = json.getString("logoFileId");
					    				else
					    					file_id = null;
					    				if(json != null && json.has("orgName"))
					    					org_name = json.getString("orgName");
					    				else
					    					org_name = null;
					    				if(json != null && json.has("domainType"))
					    					domainType = json.getString("domainType");
					    				else
					    					domainType = null;
					    				sg_found = true;
										showWelcomeScreen(superGroupName, inviter, org_name, file_id, 1,domainType);
										break;
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if(ownerDomainNameSet != null){
						sg_name = null;
						inviter = null;
			            file_id = null;
			            org_name = null;
			            domainType = null;
						for(String data : ownerDomainNameSet){
							try {
								json = new JSONObject(data);
								if(json != null && json.has("domainName")){
									sg_name = json.getString("domainName");
									if(sg_name.equalsIgnoreCase(superGroupName)){
										if(json != null && json.has("adminName"))
					    					inviter = json.getString("adminName");
					    				else
					    					inviter = null;
					    				if(json != null && json.has("logoFileId"))
					    					file_id = json.getString("logoFileId");
					    				else
					    					file_id = null;
					    				if(json != null && json.has("orgName"))
					    					org_name = json.getString("orgName");
					    				else
					    					org_name = null;
					    				if(json != null && json.has("domainType"))
					    					domainType = json.getString("domainType");
					    				else
					    					domainType = null;
					    				sg_found = true;
										showWelcomeScreen(superGroupName, inviter, org_name, file_id, 2,domainType);
										break;
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} 
					if(joinedDomainNameSet != null){
						sg_name = null;
						inviter = null;
			            file_id = null;
			            org_name = null;
			            domainType = null;
						for(String data : joinedDomainNameSet){
							try {
								json = new JSONObject(data);
								if(json != null && json.has("domainName")){
									sg_name = json.getString("domainName");
									if(sg_name.equalsIgnoreCase(superGroupName)){
										if(json != null && json.has("adminName"))
					    					inviter = json.getString("adminName");
					    				else
					    					inviter = null;
					    				if(json != null && json.has("logoFileId"))
					    					file_id = json.getString("logoFileId");
					    				else
					    					file_id = null;
					    				if(json != null && json.has("orgName"))
					    					org_name = json.getString("orgName");
					    				else
					    					org_name = null;
					    				if(json != null && json.has("domainType"))
					    					domainType = json.getString("domainType");
					    				else
					    					domainType = null;
					    				sg_found = true;
										showWelcomeScreen(superGroupName, inviter, org_name, file_id, 3,domainType);
										break;
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if(!sg_found){
//						showWelcomeScreen(superGroupName, inviter, org_name, file_id, 0);
						new GetSuperGroupProfile(superGroupName).execute();
					}
				}
				else{
					if(domaine_name.getText().toString().trim().length() < 3){
			        		Toast.makeText(SupergroupListingScreen.this, getString(R.string.enter_sg_name_to_continue), Toast.LENGTH_SHORT).show();
			        		return;
			        	}
			        	String text = domaine_name.getText().toString();
			        	registerUserOnServer(text, view);
					}
			}
		});
         createSG.setOnClickListener(new OnClickListener() {
        	 
        	 @Override
        	 public void onClick(View v) {
        		 // TODO Auto-generated method stub
        		 Intent intent = new Intent(SupergroupListingScreen.this, MainActivity.class);
				 if(mobileNumber.indexOf('-') != -1)
					 intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
				 else
					 intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
				 intent.putExtra(Constants.REG_TYPE, "ADMIN");
				 intent.putExtra("REGISTER_SG", true);
				 startActivity(intent);
				 finish();
        	 }
         });
         if(size > 0){
		        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		        expandableListDetail = new HashMap<String, List<String>>();
		        int list_size = ownerDomainNameSet != null ? ownerDomainNameSet.size() : 0;
		        if(list_size > 0)
		        	expandableListDetail.put("Owned SuperGroups" + "("+ownerDomainNameSet.size()+")", ownerDomainNameSet);
		        list_size = invitedDomainNameSet != null ? invitedDomainNameSet.size() : 0;
		        if(list_size > 0)
		        	expandableListDetail.put("Invited SuperGroups" + "("+invitedDomainNameSet.size()+")", invitedDomainNameSet);
		        list_size = joinedDomainNameSet != null ? joinedDomainNameSet.size() : 0;
		        if(list_size > 0)
		        	expandableListDetail.put("Joined SuperGroups"+ "("+list_size+")", joinedDomainNameSet);
		        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
		        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableListDetail);
		        expandableListView.setAdapter(expandableListAdapter);
		        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
	
		            @Override
		            public void onGroupExpand(int groupPosition) {
//		                Toast.makeText(getApplicationContext(),
//		                        expandableListTitle.get(groupPosition) + " List Expanded.",
//		                        Toast.LENGTH_SHORT).show();
		            	String title_name = expandableListTitle.get(groupPosition);
		            	if(title_name.startsWith("Invited SuperGroups")){
		            		expandableListAdapter.setExpandedType(ExpandableListAdapter.INVITED_SUPERGROUPS);
		            	}else if(title_name.startsWith("Owned SuperGroups")){
		            		expandableListAdapter.setExpandedType(ExpandableListAdapter.OWNED_SUPERGROUPS);
		            	}else if(title_name.startsWith("Joined SuperGroups")){
		            		expandableListAdapter.setExpandedType(ExpandableListAdapter.JOINED_SUPERGROUPS);
		            	}
		            }
		        });
	
		        expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
	
		            @Override
		            public void onGroupCollapse(int groupPosition) {
//		                Toast.makeText(getApplicationContext(),
//		                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//		                        Toast.LENGTH_SHORT).show();
	
		            }
		        });
	
		        expandableListView.setOnChildClickListener(new OnChildClickListener() {
		            @Override
		            public boolean onChildClick(ExpandableListView parent, View v,
		                                        int groupPosition, int childPosition, long id) {
//		                Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition)
//		                                + " -> "+ expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition), 
//		                                Toast.LENGTH_SHORT
//		                ).show();
		                superGroupName = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
		                String sg_name = superGroupName;
		                String title_name = expandableListTitle.get(groupPosition);
		                String inviter = null;
		                String file_id = null;
		                String org_name = null;
		                String domainType = null;
		                try {
		    				JSONObject json = new JSONObject(superGroupName);
		    				if(json != null && json.has("domainName")){
		    					sg_name = json.getString("domainName");
		    					superGroupName = sg_name;
		    				}
		    				else
		    					sg_name = superGroupName;
		    				if(json != null && json.has("adminName"))
		    					inviter = json.getString("adminName");
		    				else
		    					inviter = null;
		    				if(json != null && json.has("logoFileId"))
		    					file_id = json.getString("logoFileId");
		    				else
		    					file_id = null;
		    				if(json != null && json.has("orgName"))
		    					org_name = json.getString("orgName");
		    				else
		    					org_name = null;
		    				if(json != null && json.has("domainType"))
		    					domainType = json.getString("domainType");
		    				else
		    					domainType = null;
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
//		                registerUserOnServer(superGroupName, v);
		                if(title_name.startsWith("Invited SuperGroups")){
		                	newUser = true;
		                	showWelcomeScreen(sg_name, inviter, org_name, file_id, 1,domainType);
		                }
		                else if(title_name.startsWith("Owned SuperGroups"))
		                	showWelcomeScreen(sg_name, inviter, org_name, file_id, 2,domainType);
		                else
		                	showWelcomeScreen(sg_name, inviter, org_name, file_id, 3,domainType);
		                return false;
		            }
		        });   
         }
	}
	public void onResume(){
		super.onResume();
		if(showAlertForAlreadyOwnedSG){
			showDialog(getResources().getString(R.string.sg_already_owned_alert));
			showAlertForAlreadyOwnedSG = false;
		}
	}
	public void showDialog(String s, boolean custom) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog_gray);
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
	Dialog welcomeDialog = null;
    public void showWelcomeScreen(final String supergroup_name, final String inviter_name, final String org_name, final String file_id, final int type, final String domainType) {
    	SharedPrefManager.getInstance().setDomainType(domainType);
    	welcomeDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar);
    	welcomeDialog.setCanceledOnTouchOutside(false);
    	welcomeDialog.setContentView(R.layout.welcome_screen);
		superGroupIconView = (ImageView) welcomeDialog.findViewById(R.id.id_profile_pic);
		
		if(superGroupIconView != null)
			superGroupIconView.setOnClickListener(this);
		picChooserDialog = new Dialog(this);
		picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
		picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
		picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
		((TextView)welcomeDialog.findViewById(R.id.id_domain_name)).setText(supergroup_name);
		if(type == 1)
			((TextView)welcomeDialog.findViewById(R.id.id_inviters_name)).setText(""+inviter_name);
		else if(type == 2){
			((TextView)welcomeDialog.findViewById(R.id.id_inviter_label)).setText("Owned by");
			((TextView)welcomeDialog.findViewById(R.id.id_inviters_name)).setText("You");
		}
		else{
			((TextView)welcomeDialog.findViewById(R.id.id_inviter_label)).setText("Owned by");
			((TextView)welcomeDialog.findViewById(R.id.id_inviters_name)).setText(""+inviter_name);
		}
		if(org_name != null && !org_name.equals("")){
			((TextView)welcomeDialog.findViewById(R.id.user_org_name)).setText(""+org_name);
		}else{
			((TextView)welcomeDialog.findViewById(R.id.user_org_name)).setVisibility(View.GONE);
			((TextView)welcomeDialog.findViewById(R.id.id_org_lable)).setVisibility(View.GONE);
		}
		if(file_id != null){
			setProfilePic(superGroupIconView, file_id);
			setSGFullPic(superGroupIconView, file_id);
			SharedPrefManager.getInstance().saveSGFileId("SG_FILE_ID", file_id);
		}
		
		((Button)welcomeDialog.findViewById(R.id.done_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(type != 2 && type != 3)
					showNameDialog();
				else
				registerUserOnServer(supergroup_name, v);
			}
		});
		((TextView)welcomeDialog.findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				welcomeDialog.cancel();
			}
		});
		((ImageView)welcomeDialog.findViewById(R.id.id_profile_pic)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String file_path = getImagePath(file_id);
				if(file_path == null || (file_path != null && file_path.trim().length() == 0))
					file_path = getImagePath(null);
//				if(file_path == null)
//					file_path = getThumbPath(fileName);
				if(file_path != null)
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if(file_path.startsWith("http://"))
						intent.setDataAndType(Uri.parse(file_path), "image/*");
					else
						intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
					startActivity(intent);
				}
			}
		});
		welcomeDialog.show();
    }
    public void onClick(View view){
		switch(view.getId()){
		case R.id.row_layout:
			LinearLayout ll = (LinearLayout) view.findViewById(R.id.row_layout);
			RadioButton radio = (RadioButton) view.findViewById(R.id.id_sg_radio_button);
			if(radio != null && radio.isChecked()){
				superGroupName = "";
				radio.setChecked(false);
			}else{
				radio.setChecked(true);
				if(ll.getTag() != null)
					superGroupName = ll.getTag().toString();
	//			registerUserOnServer(superGroupName, view);
			}
			break;
			case R.id.id_group_icon:
				String tmpImgId1 = null;
				 if(picUploader!=null && picUploader.getServerFileId()!=null)
					 tmpImgId1 = picUploader.getServerFileId();
				if(picChooserDialog!=null && !picChooserDialog.isShowing() && tmpImgId1==null)
					picChooserDialog.show();
				else if(AppUtil.capturedPath1!=null){
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
				if(picChooserDialog!=null && !picChooserDialog.isShowing())
					picChooserDialog.show();
				break;
		}
    }
    public void uploadProfilePicture(final String imgPath) {
		String packetID = null;
		if(imgPath != null && imgPath.length() > 0)
		{
			try
			{
				new ProfilePicUploader(this, null, true, notifyPhotoUploadHandler).execute(imgPath, packetID,"",
						"SG_FILE_ID", SharedPrefManager.getInstance().getUserName());
			}catch(Exception ex)
			{
				showDialog(getString(R.string.failed),getString(R.string.photo_upload_failed));
			}
		}
	}
	private final Handler notifyPhotoUploadHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    		String file_id = SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID");
	    }
	};
	private String getImagePath(String groupPicId)
	{
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";
			File file = Environment.getExternalStorageDirectory();
			return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
		}
		return null;
	}
	private String getThumbPath(String groupPicId)
	{
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
			File contentFile = new File(filename);
			if(contentFile!=null && contentFile.exists()){
				return filename;
			}
			
		}
		return null;
	}
	private boolean setProfilePic(ImageView picView, String groupPicId){
		String img_path = getThumbPath(groupPicId);
		picView.setImageResource(R.drawable.about_icon);
			if(groupPicId == null || groupPicId.equals("clear") ||  groupPicId.contains("logofileid"))
				return false;
			if(img_path != null){
			File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
			}else{
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else{
			if (Build.VERSION.SDK_INT >= 11)
				new BitmapDownloader((RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
             else
            	 new BitmapDownloader((RoundedImageView)picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			
		}
		return false;	
	}
	private boolean setSGFullPic(ImageView picView, String groupPicId){
		String img_path = getImagePath(groupPicId);
//		picView.setImageResource(R.drawable.about_icon);
			if(groupPicId == null || groupPicId.equals("clear") ||  groupPicId.contains("logofileid"))
				return false;
			if(img_path != null){
			File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
			}else{
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader(this,(RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
	             else
	            	 new BitmapDownloader(this,(RoundedImageView)picView).execute(groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
			}
		}else{
			if (Build.VERSION.SDK_INT >= 11)
				new BitmapDownloader((RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
             else
            	 new BitmapDownloader((RoundedImageView)picView).execute(groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
			
		}
		return false;	
	}
	private void setThumb(ImageView imageViewl,String path, String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
		    bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		
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
	if (orientation != ExifInterface.ORIENTATION_NORMAL)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		Matrix matrix = new Matrix();
		if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 
		{
			matrix.postRotate(90);
		} 
		else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			matrix.postRotate(180);
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			matrix.postRotate(270);
		}
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		}
			
		return bm;
	}
	
	public void showDialog(String s,String btnTxt) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		TextView btn = ((TextView)bteldialog.findViewById(R.id.id_ok));
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

private void registerUserOnServer(String super_group, View view){
	String imei = SuperChatApplication.getDeviceId();
	String imsi = SuperChatApplication.getNetworkOperator();
	String version = "";
	try {
		version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		if(version!=null && version.contains("."))
			version = version.replace(".", "_");
		if(version==null)
			version = "";
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String clientVersion = "Android_"+version;
	RegistrationForm registrationForm = new RegistrationForm(mobileNumber, "normal",imei, imsi, clientVersion);
	registrationForm.setToken(imei);
	registrationForm.countryCode = countryCode;
	if(super_group != null && super_group.trim().length() > 0)
		registrationForm.setDomainName(super_group);
	SharedPrefManager.getInstance().saveUserPhone(mobileNumber);
	if(Build.VERSION.SDK_INT >= 11)
		new SignupTaskOnServer(registrationForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	else
		new SignupTaskOnServer(registrationForm, view).execute();
}

	public void restartActivity(Activity activity) {
		if (Build.VERSION.SDK_INT >= 11) {
			activity.recreate();
		} else {
			activity.finish();
			activity.startActivity(getIntent());
		}
	}

	public void onBackClick(View view){
		if(welcomeDialog != null){
			welcomeDialog.cancel();
			welcomeDialog = null;
			return;
		}
		SharedPrefManager.getInstance().clearSharedPref();
		Intent intent = new Intent(this, RegistrationOptions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
@Override
public void onBackPressed() {
	return;
}
//=======================================================================================
class GetRegisteredSGList extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();

	}

	@Override
	protected String doInBackground(String... urls) {
		String data = null;
	    try {
	    	String url = Constants.SERVER_URL + "/tiger/rest/user/findDomain?mobileNumber="+mobileNumber;
	        HttpGet httppost = new HttpGet(url);
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response = httpclient.execute(httppost);
	        // StatusLine stat = response.getStatusLine();
	        int status = response.getStatusLine().getStatusCode();
	        if (status == 200) {
	            HttpEntity entity = response.getEntity();
	            data = EntityUtils.toString(entity);
	            return data;
	        }


	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
	    return data;
	}

	protected void onPostExecute(String result) {
		if(result != null && result.trim().length() != 0)
		{
			try{
				 JSONObject json = new JSONObject(result);
				 if(json != null){
					 if(json.has("status") && json.get("status").toString().equalsIgnoreCase("success")){
						 if(json.has("domainNameSet")){
							 JSONArray array = json.getJSONArray("domainNameSet");
							 List<String> list = new ArrayList<String>();
							 for(int i = 0; i < array.length(); i++){
							     list.add(array.getString(i));
							 }
							 if(list != null && !list.isEmpty()){
									listView.setAdapter(new SuperGroupListingAdapter(SupergroupListingScreen.this, list));
									listView.setOnItemClickListener(SupergroupListingScreen.this);
							 }
						 }
					 }
				 }
			}catch(JSONException ex){
				
			}
		}
	}
}
//====================================================
class SuperGroupListingAdapter extends BaseAdapter {
	 
    List<String> superGroupList;
    Activity context;
 
    public SuperGroupListingAdapter(Activity context, List<String> superGroupList) {
        super();
        this.context = context;
        this.superGroupList = superGroupList;
    }
 
    private class ViewHolder {
        TextView nameView;
        LinearLayout layout;
        ImageView imageView;
        RadioButton radioButton;
    }
 
    public int getCount() {
        return superGroupList.size();
    }
 
    public Object getItem(int position) {
        return superGroupList.get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sg_listing_item, null);
            holder = new ViewHolder();
            holder.layout = (LinearLayout) convertView.findViewById(R.id.row_layout);
            holder.imageView = (ImageView) convertView.findViewById(R.id.contact_icon);
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.id_sg_radio_button);
            holder.nameView = (TextView) convertView.findViewById(R.id.id_contact_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try{
	        String name = (String) getItem(position);
	        holder.nameView.setText(name);
	        if(name.equalsIgnoreCase(superGroupName))
	        	holder.radioButton.setChecked(true);
	        else
	        	holder.radioButton.setChecked(false);
	        holder.layout.setTag(name);
	        holder.radioButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout ll = (LinearLayout) v.findViewById(R.id.row_layout);
					RadioButton radio = (RadioButton) v.findViewById(R.id.id_sg_radio_button);
					if(radio != null && radio.isChecked()){
						superGroupName = "";
						radio.setChecked(false);
					}else{
						radio.setChecked(true);
						if(ll.getTag() != null)
							superGroupName = ll.getTag().toString();
//						registerUserOnServer(superGroupName, view);
						notifyDataSetChanged();
					}
				}
			});
	        holder.layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout ll = (LinearLayout) v.findViewById(R.id.row_layout);
					RadioButton radio = (RadioButton) v.findViewById(R.id.id_sg_radio_button);
					if(radio != null && radio.isChecked()){
						superGroupName = "";
						radio.setChecked(false);
					}else{
						radio.setChecked(true);
						if(ll.getTag() != null)
							superGroupName = ll.getTag().toString();
//						registerUserOnServer(superGroupName, view);
						notifyDataSetChanged();
					}
				}
			});
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        return convertView;
    }
}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}
	//---------------------------------------
	public class SignupTaskOnServer extends AsyncTask<String, String, String> {
		RegistrationForm registrationForm;
		ProgressDialog progressDialog = null;
		View view1;
		public SignupTaskOnServer(RegistrationForm registrationForm,final View view1){
			this.registrationForm = registrationForm;
			this.view1 = view1;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(SupergroupListingScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String JSONstring = new Gson().toJson(registrationForm);		    
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
			Log.i(TAG, "SignupTaskOnServer :: request:"+JSONstring);
			String url = Constants.SERVER_URL+ "/tiger/rest/user/register";
			Log.i(TAG, "SignupTaskOnServer :: url:"+url);
			 HttpPost httpPost = new HttpPost(url);
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,false);
			 HttpResponse response = null;
			 
	         try {
				httpPost.setEntity(new StringEntity(JSONstring));
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){ //new1
						 HttpEntity entity = response.getEntity();
//						    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						    String line = "";
				            String str = "";
				            while ((line = rd.readLine()) != null) {
				            	str+=line;
				            }
				            if(str!=null &&!str.equals("")){
				            	Log.d(TAG, "SignupTaskOnServer ::  response:"+str);
						            	str = str.trim();
						            	Gson gson = new GsonBuilder().create();
										if (str==null || str.contains("error")){
											return str;
										}
										RegistrationForm regObj = gson.fromJson(str, RegistrationForm.class);
										if (regObj != null) {
											SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
											if (iPrefManager != null && iPrefManager.getUserId() != 0) {
												if (iPrefManager.getUserId() != regObj.iUserId) {
													try {
														DBWrapper.getInstance().clearMessageDB();
//														iPrefManager.clearSharedPref();
													} catch (Exception e) {
													}
												}
											}
											Log.i(TAG, "SignupTaskOnServer :: password, mobileNumber: " + regObj.getPassword()+" , "+regObj.iMobileNumber);
											iPrefManager.saveUserDomain(superGroupName);
											iPrefManager.saveAuthStatus(regObj.iStatus);
											if(regObj.token != null)
												iPrefManager.saveDeviceToken(regObj.token);
											iPrefManager.saveUserId(regObj.iUserId);
											iPrefManager.setAppMode("VirginMode");
											iPrefManager.saveUserPhone(regObj.iMobileNumber);
//											iPrefManager.saveUserPassword(regObj.getPassword());
											iPrefManager.saveUserLogedOut(false);
											pendingProfile = regObj.pendingProfile;
//											pendingProfile = true;
											iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
										}
										verifyUserSG(regObj.iUserId);
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
				ErrorModel errorModel = gson.fromJson(str, ErrorModel.class);
				if (errorModel != null) {
					if (errorModel.citrusErrors != null
							&& !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						if(citrusError!=null && citrusError.code.equals("20019") ){
							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
							iPrefManager.saveUserDomain(superGroupName);
							iPrefManager.saveUserId(errorModel.userId);
							//below code should be only, in case of brand new user - "First time SC user"
							iPrefManager.setAppMode("SecondMode");
							iPrefManager.saveUserLogedOut(false);
							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
							//Do not show this dialog, Simply verify and get in
//							verifyUserSG(errorModel.userId);
							showAlertDialog(citrusError.message, errorModel.userId);
						}else
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
	public void showDialogWithPositive(String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
			Intent intent = new Intent(SupergroupListingScreen.this, MobileVerificationScreen.class);
			intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
			intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCode);
			if(superGroupName != null)
				intent.putExtra(Constants.DOMAIN_NAME, superGroupName);
			
			intent.putExtra(Constants.REG_TYPE, "USER");
			startActivity(intent);
			finish();
				return false;
			}
		});
		bteldialog.show();
	}
	public void showDialog(String s) {
		try{
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
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void showAlertDialog(String s, final long user_id) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				verifyUserSG(user_id);
				return false;
			}
		});
		bteldialog.show();
	}
	public void showNameDialog() {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.name_add_popup);
		((Button)bteldialog.findViewById(R.id.id_join_btn)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				displayName = ((EditText)bteldialog.findViewById(R.id.id_display_name_field)).getText().toString().trim();
				if(!Utilities.checkName(displayName)){
//					showDialog(getString(R.string.display_name_hint));
					Toast.makeText(SupergroupListingScreen.this, getString(R.string.display_name_hint), Toast.LENGTH_SHORT).show();
					return false;
				}
				bteldialog.cancel();
				registerUserOnServer(superGroupName, v);
				return false;
			}
		});
		((Button)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
			
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
			if(version!=null && version.contains("."))
				version = version.replace(".", "_");
			if(version==null)
				version = "";
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String imei = SuperChatApplication.getDeviceId();
		String clientVersion = "Android_"+version;
//		String display_name = null;
//		if(SharedPrefManager.getInstance().getDisplayName() != null)
//			display_name = SharedPrefManager.getInstance().getDisplayName();
		if(displayName != null && Utilities.checkName(displayName)){
			try {
				codeVerifyUrl = Constants.SERVER_URL+ "/tiger/rest/user/mobileverification/verify?userId="+ id+"&clientVersion="+clientVersion+"&imei="+imei+"&token="+Constants.regid +"&name="+URLEncoder.encode(displayName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			codeVerifyUrl = Constants.SERVER_URL+ "/tiger/rest/user/mobileverification/verify?userId="+ id+"&clientVersion="+clientVersion+"&imei="+imei+"&token="+Constants.regid;
		}
			
		Log.i(TAG, "verifyUserSG :: url : "+codeVerifyUrl);
		AsyncHttpClient client = new AsyncHttpClient();
		 client = SuperChatApplication.addHeaderInfo(client,false);
		client.get(codeVerifyUrl, null, new AsyncHttpResponseHandler() {
			ProgressDialog dialog = null;

			@Override
			public void onStart() {
					runOnUiThread(new Runnable() {
						public void run() {
							dialog = ProgressDialog.show(SupergroupListingScreen.this, "", "Loading. Please wait...", true);
						}
					});
				Log.d(TAG, "verifyUserSGonStart: ");
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
//				if(welcomeDialog != null)
//					welcomeDialog.cancel();
				Log.i(TAG, "verifyUserSG :: reponse : " + arg1);
				Gson gson = new GsonBuilder().create();
				final RegMatchCodeModel objUserModel = gson.fromJson(arg1, RegMatchCodeModel.class);
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						if (dialog != null) {
//							dialog.dismiss();
//							dialog = null;
//						}
//					}
//				});
				if (objUserModel.iStatus != null
						&& objUserModel.iStatus.equalsIgnoreCase("success")) {
					SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
					sharedPrefManager.saveUserVarified(true);
					sharedPrefManager.setMobileVerified(sharedPrefManager.getUserPhone(), true);
					sharedPrefManager.saveUserName(objUserModel.username);
					sharedPrefManager.saveUserPassword(objUserModel.password);
					sharedPrefManager.setOTPVerified(false);
					
					if(newUser || pendingProfile){
						Intent intent = new Intent(SupergroupListingScreen.this, ProfileScreen.class);
						Bundle bundle = new Bundle();
						sharedPrefManager.setFirstTime(true);
						sharedPrefManager.setAppMode("VirginMode");
						bundle.putString(Constants.CHAT_USER_NAME, objUserModel.username);
						bundle.putString(Constants.CHAT_NAME, "");
						bundle.putBoolean(Constants.REG_TYPE, false);
						bundle.putBoolean("PROFILE_EDIT_REG_FLOW", true);
						intent.putExtras(bundle);
						startActivity(intent);
					}else{
						Intent intent = new Intent(SupergroupListingScreen.this, HomeScreen.class);
						sharedPrefManager.setProfileAdded(sharedPrefManager.getUserName(),true);
						startActivity(intent);
					}
					finish();
					if(welcomeDialog != null)
						welcomeDialog.cancel();
				} 
				else{
					runOnUiThread(new Runnable() {
						public void run() {
							showDialog(objUserModel.iMessage);
						}
					});

				}
//			runOnUiThread(new Runnable() {
//				
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
				if(welcomeDialog != null)
					welcomeDialog.cancel();
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						if (dialog != null) {
//							dialog.dismiss();
//							dialog = null;
//						}
//					}
//				});
				showDialog(getString(R.string.network_not_responding));
				super.onFailure(arg0, arg1);
			}
		});
	}
//------------------------------------------------------
//	public void onProfileImagePicClick(String file_id){
//		String file_path = getImagePath(file_id);
//		if(file_path == null || (file_path != null && file_path.trim().length() == 0))
//			file_path = getImagePath(null);
////		if(file_path == null)
////			file_path = getThumbPath(fileName);
//		if(file_path != null)
//		{
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			if(file_path.startsWith("http://"))
//				intent.setDataAndType(Uri.parse(file_path), "image/*");
//			else
//				intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
//			startActivity(intent);
//		}
//		
//	}
//------------------------------------------------------
	class ExpandableListAdapter extends BaseExpandableListAdapter {

	    private Context context;
	    private List<String> expandableListTitle;
	    private HashMap<String, List<String>> expandableListDetail;
	    public static final int INVITED_SUPERGROUPS = 1;
	    public static final int OWNED_SUPERGROUPS = 2;
	    public static final int JOINED_SUPERGROUPS = 3;
	    int type = INVITED_SUPERGROUPS;

	    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
	                                 HashMap<String, List<String>> expandableListDetail) {
	        this.context = context;
	        this.expandableListTitle = expandableListTitle;
	        this.expandableListDetail = expandableListDetail;
	    }
	    
	    public void setExpandedType(int type){
	    	this.type = type;
	    }

	    @Override
	    public Object getChild(int listPosition, int expandedListPosition) {
	        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
	                .get(expandedListPosition);
	    }

	    @Override
	    public long getChildId(int listPosition, int expandedListPosition) {
	        return expandedListPosition;
	    }

	    @Override
	    public View getChildView(int listPosition, final int expandedListPosition,
	                             boolean isLastChild, View convertView, ViewGroup parent) {
	        String expandedListText = (String) getChild(listPosition, expandedListPosition);
	        String title_name = (String) getGroup(listPosition);
	        String sg_name = expandedListText;
	        String inviter = null;;
	        String logoFileId = null;;
	        try {
				JSONObject json = new JSONObject(sg_name);
				if(json != null && json.has("domainName"))
					sg_name = json.getString("domainName");
				else
					sg_name = expandedListText;
				if(json != null && json.has("adminName"))
					inviter = json.getString("adminName");
				else
					inviter = null;
				if(json != null && json.has("logoFileId"))
					logoFileId = json.getString("logoFileId");
				else
					logoFileId = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if (convertView == null) {
	            LayoutInflater layoutInflater = (LayoutInflater) this.context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = layoutInflater.inflate(R.layout.list_item, null);
	        }
	        TextView expandedListTextView = (TextView) convertView .findViewById(R.id.expandedListItem);
	        TextView invited_by = (TextView) convertView .findViewById(R.id.invited_by);
	        ImageView groupView = (ImageView) convertView .findViewById(R.id.contact_icon);
	        expandedListTextView.setText(sg_name);
	        if(inviter != null && title_name != null){
		        if(title_name.startsWith("Invited SuperGroups")){
		        	invited_by.setText("Invited by "+inviter);
	        	}else if(title_name.startsWith("Owned SuperGroups")){
	        		invited_by.setText("Owned by you");
	        	}else if(title_name.startsWith("Joined SuperGroups")){
	        		invited_by.setText("Created by "+inviter);
	        	}
	        }
	        if(logoFileId != null && !logoFileId.contains("logofileid")){
	        	setProfilePic(groupView, logoFileId);
	        }else 
	        	groupView.setImageResource(R.drawable.about_icon);
	        return convertView;
	    }

	    @Override
	    public int getChildrenCount(int listPosition) {
	        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
	                .size();
	    }

	    @Override
	    public Object getGroup(int listPosition) {
	        return this.expandableListTitle.get(listPosition);
	    }

	    @Override
	    public int getGroupCount() {
	        return this.expandableListTitle.size();
	    }

	    @Override
	    public long getGroupId(int listPosition) {
	        return listPosition;
	    }

	    @Override
	    public View getGroupView(int listPosition, boolean isExpanded,
	                             View convertView, ViewGroup parent) {
	        String listTitle = (String) getGroup(listPosition);
	        if (convertView == null) {
	            LayoutInflater layoutInflater = (LayoutInflater) this.context.
	                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = layoutInflater.inflate(R.layout.list_group, null);
	        }
	        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
	        listTitleTextView.setTypeface(null, Typeface.BOLD);
	        listTitleTextView.setText(listTitle);
	        return convertView;
	    }

	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }

	    @Override
	    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
	        return true;
	    }
	}
	class ExpandableListData {
		HashMap<String, List<String>> expandableListDetail = null;
		public ExpandableListData(HashMap<String, List<String>> map){
			expandableListDetail = map;
		}
	    public HashMap<String, List<String>> getData() {
	        return expandableListDetail;
	    }
	    
	}
	//============================================================
	class GetSuperGroupProfile extends AsyncTask<String, String, String> {
		
		String domain_name;
		ProgressDialog progressDialog = null;
		public GetSuperGroupProfile(final String domain_name){
			this.domain_name = domain_name;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SupergroupListingScreen.this, "", "Loading. Please wait...", true);
		    super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
		    try {
		    	String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/profile?domainName="+URLEncoder.encode(domain_name, "UTF-8");
		    	Log.i(TAG, "GetSuperGroupProfile :: doInBackground : URL - "+url);
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
			if(data != null){
				Log.i(TAG, "GetSuperGroupProfile :: onPostExecute : response data - "+data);
				 if(data.contains("success")){
					 	String sg_name = null;
		                String inviter = null;
		                String file_id = null;
		                String org_name = null;
		                String privacy_type = null;
		                String domainType = null;
		                try {
		    				JSONObject json = new JSONObject(data);
		    				if(json != null && json.has("domainName")){
		    					sg_name = json.getString("domainName");
		    					superGroupName = sg_name;
		    				}
		    				else
		    					sg_name = null;
		    				if(json != null && json.has("adminName"))
		    					inviter = json.getString("adminName");
		    				else
		    					inviter = null;
		    				if(json != null && json.has("logoFileId")){
		    					file_id = json.getString("logoFileId");
		    					SharedPrefManager.getInstance().saveSGFileId("SG_FILE_ID", file_id);
		    				}
		    				else
		    					file_id = null;
		    				if(json != null && json.has("orgName"))
		    					org_name = json.getString("orgName");
		    				else
		    					org_name = null;
		    				if(json != null && json.has("privacyType"))
		    					privacy_type = json.getString("privacyType");
		    				else
		    					privacy_type = "open";
		    				if(json != null && json.has("domainType"))
		    					domainType = json.getString("domainType");
		    				else
		    					domainType = null;
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		                if(privacy_type != null && privacy_type.equalsIgnoreCase("closed"))
		                	showDialog("For this SuperGroup to join, you need an invitation. Else you can't join this SuperGroup.");
		                else
		                	showWelcomeScreen(superGroupName, inviter, org_name, file_id, 0,domainType);
	            }else if(data.contains("error")){
	            	Gson gson = new GsonBuilder().create();
					ErrorModel errorModel = gson.fromJson(data, ErrorModel.class);
					if (errorModel != null) {
						if (errorModel.citrusErrors != null && !errorModel.citrusErrors.isEmpty()) {
							ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
							if(citrusError!=null && citrusError.code.equals("20021") ){
								showDialog(citrusError.message);
							}else
								showDialog(citrusError.message);
						} else if (errorModel.message != null)
							showDialog(errorModel.message);
					} else
						showDialog("Please try again later.");
	            }
			}else
				showDialog("Please try again later.");
		}
	}
}

