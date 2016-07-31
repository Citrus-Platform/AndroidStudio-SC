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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class DomainTypesAdapter extends ArrayAdapter<DomainTypesAdapter.DomainType>{
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	Context context;
	int layout;
	int totalChecked;
	ArrayList<DomainType> data;
	TextView selectedDomainView;
	boolean isEditable;
	AlertDialog dialog;
	public DomainTypesAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public DomainTypesAdapter(Context context1, int layout, ArrayList<DomainType> data, TextView selectedDomainView )
	{
		super(context1,layout,data);
		context = context1;
		this.data = data;
		this.layout = layout;
		this.selectedDomainView = selectedDomainView;
		totalChecked = 0;
		checkedTagMap.clear();
		checkedTagMap.put(SharedPrefManager.getInstance().getDomainType(), true);
	}
	public void setDialog(AlertDialog dialog){
		this.dialog = dialog;
	}
	public static class DomainType implements Comparable{
		
		String type;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		String label;
		String info;
		
		@Override
		public int compareTo(Object another) {
				String tmpName = ((DomainType)another).getLabel();
			return this.label.compareToIgnoreCase(tmpName);
		}
		DomainType(){
			
		}
		DomainType(String type, String label, String info){
			this.type = type;
			this.label = label;
			this.info = info;
		}
	}
	public class ViewHolder
	{
		String domainType;
		TextView domainTypeLabelView;
		TextView domainTypeInfoView;
		String domainTypeInfo;
		String domainTypeLabel;
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
//							if(v.getType() == R.id.id_owner_choose){
//								for(String key:ownerCheckedMap.keySet()){
//									ownerCheckedMap.put(key, false);
//								}
//								iOwnerChooseBox.setChecked(true);
//								ownerCheckedMap.put(userNames, true);
//								notifyDataSetChanged();
//								return;
//							}
							
							for(String key:checkedTagMap.keySet()){
								checkedTagMap.put(key, false);
							}
//							iCheckBox.setChecked(true);
//							iCheckBox.setTag(domainType);
							checkedTagMap.put(domainType, true);
							SharedPrefManager.getInstance().setDomainType(domainType);
							if(selectedDomainView!=null)
								selectedDomainView.setText(domainTypeLabel);
							if(checkedTagMap.get(domainType))
								totalChecked++;
							else
								totalChecked--;
							notifyDataSetChanged();
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									try{
									wait(700);
									}catch(Exception e){}
									handler.sendEmptyMessage(0);
								}
							});
							
							
						}
				}};
	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(dialog!=null)
				dialog.cancel();
		};
	};
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder viewholder = null;
		if(row == null)
		{
			row = LayoutInflater.from(context).inflate(layout, parent, false);
			viewholder = new ViewHolder();
			viewholder.domainTypeInfoView = (TextView)row.findViewById(R.id.id_domain_type_info);
			viewholder.domainTypeLabelView = (TextView)row.findViewById(R.id.id_domain_type_label);
			viewholder.iCheckBox = (CheckBox) row.findViewById(R.id.domain_check_box);
			viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
			row.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.iOwnerChooseBox.setOnClickListener(viewholder.onCheckeClickListener);
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
		
		
		DomainType map = (DomainType)getItem(position);
		viewholder.domainTypeLabel = map.getLabel();
		viewholder.domainTypeInfo =map.getInfo();
		viewholder.domainType = map.getType();
		boolean isChecked = false;
		Object obj = checkedTagMap.get(map.getType());
		if (obj != null) {
			isChecked = checkedTagMap.get(map.getType());
		}else{
			checkedTagMap.put(map.getType(),isChecked);
		}
		viewholder.iCheckBox.setChecked(isChecked);
		viewholder.iCheckBox.setTag(map.getType());
		
		
//		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.domainTypeLabel, viewholder.userNames);
		viewholder.domainTypeLabelView.setText(viewholder.domainTypeLabel);
		viewholder.domainTypeInfoView.setText(viewholder.domainTypeInfo);
		
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
	this.isEditable = isEdit;
}
public boolean isEditable(){
	return this.isEditable;
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
