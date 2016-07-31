package com.superchat.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.chatsdk.org.jivesoftware.smack.util.StringUtils;
import com.superchat.R;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CountryChooserAdapter extends ArrayAdapter<CountryChooserAdapter.CountryItem>{
	Context context;
	int layout;
	ArrayList<CountryItem> data;
	boolean isEditableContact;
	public CountryChooserAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public CountryChooserAdapter(Context context1, int layout, ArrayList<CountryItem> data)
	{
		super(context1,layout,data);
		context = context1;
		this.data = data;
		this.layout = layout;
		mDrawableBuilder = TextDrawable.builder().beginConfig().toUpperCase().endConfig().round();
	}
	public static class CountryItem implements Comparable{
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
				String tmpName = ((CountryItem)another).getName();
			return this.name.compareToIgnoreCase(tmpName);
		}
		String email;
		CountryItem(){
			
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
		TextView firstLetterView;
		
		ViewHolder(){

		}
		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
			}};
			private OnClickListener onCheckeClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent= new Intent(((CountryChooserScreen)context),MainActivity.class);
					intent.putExtra("COUNTRY_NAME", displayName);
					intent.putExtra("STD_CODE", id);
					intent.putExtra("COUNTRY_CODE", userNumber);
					((CountryChooserScreen)context).setResult(CountryChooserScreen.RESULT_OK, intent);
					((CountryChooserScreen)context).finish();
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
			viewholder.firstLetterView = (TextView)row.findViewById(R.id.id_first_letter);
			
			viewholder.imageDefault = (ImageView)row.findViewById(R.id.contact_icon_default);
			viewholder.contactNumberView = (TextView)row.findViewById(R.id.id_contact_number);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			row.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.iOwnerChooseBox.setOnClickListener(viewholder.onCheckeClickListener);
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
		
		
		CountryItem map = (CountryItem)getItem(position);
		viewholder.userNumber = map.getNumber();
		viewholder.displayName = map.getName();
		viewholder.userDisplayNumber =map.getDisplayNumber();
		viewholder.id = map.getId();
		
		viewholder.firstLetterView.setVisibility(View.GONE);
		if(position==0 ) {
		viewholder.firstLetterView.setVisibility(View.VISIBLE);
		viewholder.firstLetterView.setText("A");
		} else {
			 CountryItem map1 = (CountryItem)getItem(position-1);
			  if(map1.getName().charAt(0)==map.getName().charAt(0)){
				  viewholder.firstLetterView.setVisibility(View.GONE); 
				  }else{
					  viewholder.firstLetterView.setVisibility(View.VISIBLE);
						viewholder.firstLetterView.setText(""+map.getName().charAt(0));
				  }
			
			
			}
		
//		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames);
		viewholder.displayNameView.setText(viewholder.displayName);
		viewholder.contactNumberView.setText(viewholder.userDisplayNumber);
		if(Build.VERSION.SDK_INT >= 16){
			Drawable flag = getDrawableFromAsset(viewholder.userNumber+".png");//flagName+".png");
			if(flag!=null){
				viewholder.imageDefault.setVisibility(View.VISIBLE);
//				viewholder.imageDefault.setImageBitmap(flag);
				viewholder.imageDefault.setBackground(flag);
			}else{
				viewholder.imageDefault.setVisibility(View.INVISIBLE);
			}
		}else{
			Bitmap flag = getBitmapFromAsset(viewholder.userNumber+".png");//flagName+".png");
			if(flag!=null){
				viewholder.imageDefault.setVisibility(View.VISIBLE);
				viewholder.imageDefault.setImageBitmap(flag);
			}else{
				viewholder.imageDefault.setVisibility(View.INVISIBLE);
			}
		}
		
//		if(map.getId()!=null){
//			Uri u = Uri.parse(map.getId());
//			if (u != null) {
////			        viewholder.imageDefault.setImageURI(u);
//				viewholder.image.setVisibility(View.VISIBLE);
//					viewholder.imageDefault.setVisibility(View.GONE);
//			        viewholder.image.setImageURI(u);
//			} else {
//				
//				try{
//					String name_alpha = String.valueOf(viewholder.displayName.charAt(0));
//					if(viewholder.displayName.contains(" ") && viewholder.displayName.indexOf(' ') < (viewholder.displayName.length() - 1))
//						name_alpha +=  viewholder.displayName.substring(viewholder.displayName.indexOf(' ') + 1).charAt(0);
//					TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(viewholder.displayName));
//					viewholder.image.setVisibility(View.INVISIBLE);
//					viewholder.imageDefault.setVisibility(View.VISIBLE);
//					viewholder.imageDefault.setImageDrawable(drawable);
//					viewholder.imageDefault.setBackgroundColor(Color.TRANSPARENT);
//				}catch(Exception ex){
//					viewholder.image.setImageResource(R.drawable.avatar);
//				}
//			}
//		}else {
//			
//			try{
//				String name_alpha = String.valueOf(viewholder.displayName.charAt(0));
//				if(viewholder.displayName.contains(" ") && viewholder.displayName.indexOf(' ') < (viewholder.displayName.length() - 1))
//					name_alpha +=  viewholder.displayName.substring(viewholder.displayName.indexOf(' ') + 1).charAt(0);
//				TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(viewholder.displayName));
//				viewholder.image.setVisibility(View.INVISIBLE);
//				viewholder.imageDefault.setVisibility(View.VISIBLE);
//				viewholder.imageDefault.setImageDrawable(drawable);
//				viewholder.imageDefault.setBackgroundColor(Color.TRANSPARENT);
//			}catch(Exception ex){
//				viewholder.image.setVisibility(View.VISIBLE);
//				viewholder.image.setImageResource(R.drawable.avatar);
//			}
//		}
		return row;
	}
	private Drawable getDrawableFromAsset(String strName)
    {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;
    }
	private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }
}
