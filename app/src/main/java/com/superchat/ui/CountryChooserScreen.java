package com.superchat.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.ui.BulkInvitationAdapter.AppContact;
import com.superchat.ui.BulkInvitationScreen.ContactLoadingTask;
import com.superchat.ui.CountryChooserAdapter.CountryItem;
import com.superchat.utils.Countries;
import com.superchat.utils.Countries.Country;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CountryChooserScreen  extends Activity implements OnClickListener{
	ListView listView;
	CountryChooserAdapter adapter;
	EditText searchBoxView;
	
	HashMap<String ,CountryItem> allCountries = new HashMap<String,CountryItem>();
	ArrayList<CountryItem>  dataList = new ArrayList<CountryItem>();
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.country_chooser_layout);
//		Countries.Country.values()
		listView = (ListView) findViewById(R.id.id_country_list);
		 searchBoxView = (EditText)findViewById(R.id.id_search_box);
		 ((ImageView)findViewById(R.id.id_back_img)).setOnClickListener(this);
		 
		new CountryLoadingTask().execute();
		
		searchBoxView.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable editable) {
				try{
					String s1 = (new StringBuilder()).append(searchBoxView.getText().toString()).toString();
					int itemIndex = 0;
					if(s1!=null && !s1.trim().equals("")){
					 itemIndex = getCountryItemIndex(s1);
					}
					listView.setSelection(itemIndex);
				}catch(Exception e){}
//				if(dataList!=null)
//					dataList.clear();
//				dataList = new ArrayList<CountryItem>();
//				for(String key: allCountries.keySet()){
//					CountryItem appContact = allCountries.get(key);
//					dataList.add(appContact);
////					Log.d("CONTACT_FATCH", "After: displayedNumber : "+appContact.getName()+" , "+appContact.getDisplayNumber()+" , "+appContact.getNumber());
//					}
//				Collections.sort(dataList);
//				if(dataList==null || dataList.isEmpty())
//					return;
//				String s1 = (new StringBuilder()).append(searchBoxView.getText().toString()).toString();
//					boolean isAllShown = false;
//					ArrayList <CountryItem> listClone = new ArrayList<CountryItem>(); 
//					if(s1!=null && !s1.trim().equals("")){
//			           for (CountryItem tmpContact : dataList) {
//			        	  String tmpSearch = tmpContact.getName();
//			               if(tmpSearch.toLowerCase().contains(s1.toLowerCase())){
//			                   listClone.add(tmpContact);
//			               }
//			           }
//			           }else{
//			        	   listClone.addAll(dataList);
//			        	   isAllShown = true;
//			           }
//					adapter.clear();
////					ArrayList<AppContact> list = new ArrayList<AppContact>();
//					for(CountryItem member:listClone){
////						if(SharedPrefManager.getInstance().getUserName().equals(member.userName))
////							continue;
//						CountryItem info = new CountryItem();
//						info.setDisplayNumber(member.getDisplayNumber());
//						info.setNumber(member.getNumber());
//						info.setName(member.getName());
//////						list.add(info);
//						adapter.add(info);
//					}
////					adapter.addAll(listClone);
//					adapter.notifyDataSetChanged();
			}

			public void beforeTextChanged(CharSequence charsequence, int i,
					int j, int k) {
			}

			public void onTextChanged(CharSequence charsequence, int i, int j,
					int k) {
			}

		});
	}
	public void alphbetOnClick(View v){
//		Toast.makeText(this, "You clicked"+((TextView)v).getText(), Toast.LENGTH_SHORT).show();
		try{
			int itemIndex = getCountryItemIndex(((TextView)v).getText().toString());
			listView.setSelection(itemIndex);
		}catch(Exception e){}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_back_img:
			finish();
			break;
		}
	}
	private int getCountryItemIndex(String firstLetter){
		for(CountryItem item :  dataList){
			if(item.getName().toLowerCase().startsWith(firstLetter.toLowerCase())){
				return dataList.indexOf(item);
			}
		}
		return 0;
	}
	class CountryLoadingTask  extends AsyncTask<String,String,String>{
		ProgressDialog progressDialog;
		List<String> numbers = new ArrayList<String>();
		@Override
		protected void onPreExecute() {
				progressDialog = ProgressDialog.show(CountryChooserScreen.this, "", "Contact loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
//			dataList = new ArrayList<CountryItem>();
			for(Country country: Countries.Country.values()){
				CountryItem countryItem = new CountryItem();
				countryItem.setName(country.getStationName());
				countryItem.setDisplayNumber(country.getStationName());
				countryItem.setNumber(country.getCountryCode());
				countryItem.setId("+"+country.getCode());
				allCountries.put(country.getStationName(),countryItem);
//				dataList.add(countryItem);
			}
			return null;
		}
		@Override
		protected void onPostExecute(String str) {
			 dataList = new ArrayList<CountryItem>();
//			
			for(String key: allCountries.keySet()){
				CountryItem countryItem = allCountries.get(key);
				dataList.add(countryItem);
				}
			Collections.sort(dataList);
			
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			// 
			adapter = new CountryChooserAdapter(CountryChooserScreen.this,R.layout.country_list_item,dataList);
			listView.setAdapter(null);
			listView.setAdapter(adapter);
		}
	}
}
