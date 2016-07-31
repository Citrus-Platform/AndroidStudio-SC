package com.superchat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import com.chatsdk.org.jivesoftware.smack.util.StringUtils;
import com.superchat.R;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class BulkInvitationAdapter extends ArrayAdapter<BulkInvitationAdapter.AppContact>{
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	Context context;
	int layout;
	int totalChecked;
	ArrayList<AppContact> data;
	boolean isEditableContact;
	public BulkInvitationAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public BulkInvitationAdapter(Context context1, int layout, ArrayList<AppContact> data)
	{
		super(context1,layout,data);
		context = context1;
		this.data = data;
		this.layout = layout;
		totalChecked = 0;
		checkedTagMap.clear();
		mDrawableBuilder = TextDrawable.builder().beginConfig().toUpperCase().endConfig().round();
	}
	public static class AppContact implements Comparable{
		String id;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		String name;
		String number;
		String displayNumber;
		public String getDisplayNumber() {
			return displayNumber;
		}
		public void setDisplayNumber(String displayNumber) {
			this.displayNumber = displayNumber;
		}
		@Override
		public int compareTo(Object another) {
				String tmpName = ((AppContact)another).getName();
			return this.name.compareToIgnoreCase(tmpName);
		}
		String email;
		AppContact(){
			
		}
	}
	public class ViewHolder
	{
		String id;
		TextView displayNameView;
		TextView contactNumberView;
		String userDisplayNumber;
		String userNumber;
		String displayName;
		ImageView imageDefault;
		ImageView image;
		CheckBox iCheckBox;
		ViewHolder(){

		}
		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
			}};
			private OnClickListener onCheckeClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
						if(iCheckBox!=null){
//							if(v.getId() == R.id.id_owner_choose){
//								for(String key:ownerCheckedMap.keySet()){
//									ownerCheckedMap.put(key, false);
//								}
//								iOwnerChooseBox.setChecked(true);
//								ownerCheckedMap.put(userNames, true);
//								notifyDataSetChanged();
//								return;
//							}
							
							if(v.getId() != R.id.contact_sel_box){
								iCheckBox.setChecked(!checkedTagMap.get(userDisplayNumber));
							}
							checkedTagMap.put(userDisplayNumber, !checkedTagMap.get(userDisplayNumber));
							if(checkedTagMap.get(userDisplayNumber))
								totalChecked++;
							else
								totalChecked--;
							if(totalChecked == 0)
								((BulkInvitationScreen)context).countTextView.setVisibility(View.GONE);
							else{
								((BulkInvitationScreen)context).countTextView.setVisibility(View.VISIBLE);
								((BulkInvitationScreen)context).countTextView.setText("("+String.valueOf(totalChecked)+")");
							}
						}
//					}else{
//						Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
//						intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, displayName);
//						intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userNames);
//						intent.putExtra("is_vopium_user", true);
//						((EsiaChatContactsScreen)context).startActivity(intent);
//
//					}
				}};
	}
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder viewholder = null;
		if(row == null)
		{
			row = LayoutInflater.from(context).inflate(layout, parent, false);
			viewholder = new ViewHolder();
			viewholder.image = (ImageView)row.findViewById(R.id.contact_icon);
			viewholder.imageDefault = (ImageView)row.findViewById(R.id.contact_icon_default);
			viewholder.contactNumberView = (TextView)row.findViewById(R.id.id_contact_number);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.iCheckBox = (CheckBox) row.findViewById(R.id.contact_sel_box);
			viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
			row.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.iOwnerChooseBox.setOnClickListener(viewholder.onCheckeClickListener);
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
		
		
		AppContact map = (AppContact)getItem(position);
		viewholder.userNumber = map.getNumber();
		viewholder.displayName = map.getName();
		viewholder.userDisplayNumber =map.getDisplayNumber();
		
		boolean isChecked = false;
		Object obj = checkedTagMap.get(map.getDisplayNumber());
		if (obj != null) {
			isChecked = checkedTagMap.get(map.getDisplayNumber());
		}else{
			checkedTagMap.put(map.getDisplayNumber(),isChecked);
		}
		viewholder.iCheckBox.setChecked(isChecked);
		viewholder.iCheckBox.setTag(map.getDisplayNumber());
		
		
//		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames);
		viewholder.displayNameView.setText(viewholder.displayName);
		viewholder.contactNumberView.setText(viewholder.userDisplayNumber);
		if(map.getId()!=null){
			Uri u = Uri.parse(map.getId());
			if (u != null) {
//			        viewholder.imageDefault.setImageURI(u);
				viewholder.image.setVisibility(View.VISIBLE);
					viewholder.imageDefault.setVisibility(View.GONE);
			        viewholder.image.setImageURI(u);
			} else {
				
				try{
					String name_alpha = String.valueOf(viewholder.displayName.charAt(0));
					if(viewholder.displayName.contains(" ") && viewholder.displayName.indexOf(' ') < (viewholder.displayName.length() - 1))
						name_alpha +=  viewholder.displayName.substring(viewholder.displayName.indexOf(' ') + 1).charAt(0);
					TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(viewholder.displayName));
					viewholder.image.setVisibility(View.INVISIBLE);
					viewholder.imageDefault.setVisibility(View.VISIBLE);
					viewholder.imageDefault.setImageDrawable(drawable);
					viewholder.imageDefault.setBackgroundColor(Color.TRANSPARENT);
				}catch(Exception ex){
					viewholder.image.setImageResource(R.drawable.avatar);
				}
			}
		}else {
			
			try{
				String name_alpha = String.valueOf(viewholder.displayName.charAt(0));
				if(viewholder.displayName.contains(" ") && viewholder.displayName.indexOf(' ') < (viewholder.displayName.length() - 1))
					name_alpha +=  viewholder.displayName.substring(viewholder.displayName.indexOf(' ') + 1).charAt(0);
				TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(viewholder.displayName));
				viewholder.image.setVisibility(View.INVISIBLE);
				viewholder.imageDefault.setVisibility(View.VISIBLE);
				viewholder.imageDefault.setImageDrawable(drawable);
				viewholder.imageDefault.setBackgroundColor(Color.TRANSPARENT);
			}catch(Exception ex){
				viewholder.image.setVisibility(View.VISIBLE);
				viewholder.image.setImageResource(R.drawable.avatar);
			}
		}
		return row;
	}
	public void setItems(String user,boolean flg){
		checkedTagMap.put(user, flg);
}
public void removeSelectedItems(){
	totalChecked = 0;
	checkedTagMap.clear();
}
public void setEditableContact(boolean isEdit) {
	this.isEditableContact = isEdit;
}
public boolean isEditable(){
	return this.isEditableContact;
}
public int getCheckedCounts(){
	return totalChecked;
}
public ArrayList<String> getSelectedMembers(){
	
	ArrayList<String> list = new ArrayList<String>();
	if(checkedTagMap!=null && !checkedTagMap.isEmpty())
		for(String item: checkedTagMap.keySet()){
			if(checkedTagMap.get(item)){
				list.add(item);
			}
		}
	return list;
}
}
