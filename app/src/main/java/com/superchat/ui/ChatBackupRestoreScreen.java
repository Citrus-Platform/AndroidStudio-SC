package com.superchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.db.ChatDBWrapper;
import com.superchat.R;
import com.superchat.utils.Constants;
import com.superchat.utils.FileDownloadResponseHandler;
import com.superchat.utils.FileUploaderDownloader;
import com.superchat.utils.ZipManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

public class ChatBackupRestoreScreen extends Activity implements OnClickListener, FileDownloadResponseHandler{

    Button restore;
    Button noThanks;
    TextView lastDate;
	private static final String TAG = "ChatBackupRestoreScreen";
	String backedUpFileID = null;
	String lastBackupDate = null;
	
	String backup_dir = "SCBackup";
	String zip_file = "SCBackup.scb";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.restore_backup);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			backedUpFileID = bundle.getString(Constants.BACKUP_FILEID);
			lastBackupDate = bundle.getString(Constants.LAST_BACKUP_DATE);
		}
		
		noThanks = (Button)findViewById(R.id.id_no_thanks);
		restore = (Button)findViewById(R.id.id_restore);
		lastDate = (TextView)findViewById(R.id.id_last_backup_date);
		if(lastBackupDate != null)
			lastDate.setText(lastBackupDate);
		restore.setOnClickListener(this);
		noThanks.setOnClickListener(this);
	}
//=========================================================
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_no_thanks:
			finish();
			break;
		case R.id.id_restore:
			//Download backed up file
			if(backedUpFileID != null){
				createDirIfNotExists(Environment.getExternalStorageDirectory().getAbsolutePath(), backup_dir);
				String full_path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir + "/" + zip_file;
				new FileUploaderDownloader(ChatBackupRestoreScreen.this, ChatBackupRestoreScreen.this, true, false, null).execute(full_path, backedUpFileID);
			}
			break;
		}
	}
	
	public boolean createDirIfNotExists(String path, String folder_name) {
	    boolean ret = true;
	    File file = new File(path, folder_name);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating Image folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT >= 11)
			new RestoreMessagesInDB().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new RestoreMessagesInDB().execute();
	}
	@Override
	public void onFileDownloadResposne(View view, int[] type, String[] file_urls, String[] file_paths) {
		// TODO Auto-generated method stub
	}
	public static void deleteDirectoryWithContets(File dir)
	{
	    if ( dir.isDirectory() )
	    {
	        String [] children = dir.list();
	        for ( int i = 0 ; i < children.length ; i ++ )
	        {
	         File child =    new File( dir , children[i] );
	         if(child.isDirectory()){
	        	 deleteDirectoryWithContets( child );
	             child.delete();
	         }else{
	             child.delete();

	         }
	        }
	        dir.delete();
	    }
	}
	
	public static String getStringFromFile (String filename) {
		BufferedReader br = null;
		StringBuffer file_date = new StringBuffer();
		try {
		        try {
		            br = new BufferedReader(new FileReader(filename));
		        } catch (FileNotFoundException e1) {
		            e1.printStackTrace();
		        }
		        String line = "";
		        while ((line = br.readLine()) != null) {
		         //Do something here 
		        	file_date.append(line);
		        }
			}catch(Exception ex){
		}
		return file_date.toString();
	}
	
//==================================================
	ProgressDialog dialog = null;
	class RestoreMessagesInDB extends AsyncTask<String, String, String> {
		
		public RestoreMessagesInDB(){
		}
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ChatBackupRestoreScreen.this, "","Restoring backup. Please wait...", true);
		    super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			 String data = null;
		    try {
				createDirIfNotExists(Environment.getExternalStorageDirectory().getAbsolutePath(), backup_dir);
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir +"/";
				String full_path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir + "/" + zip_file;
				ZipManager zipManager = new ZipManager(null, null);
				Vector<String> files = zipManager.unzip(full_path, path);
				String os_type = null;
				JSONArray messages = null;
				if(files != null){
					for(int i = 0; i < files.size(); i++){
						String filedata = getStringFromFile(path + files.elementAt(i));
						JSONObject jsonobj;
						try {
							jsonobj = new JSONObject(filedata);
							if(jsonobj.has("osType"))
								os_type = (String) jsonobj.get("osType");
							if(i == 0) {
								messages = (JSONArray) jsonobj.get("messages");
								int success = ChatDBWrapper.getInstance().insertBackUpInDB(messages, os_type);
								if(success == 1){
									data = ""+success;
								}
							}
							else {
								messages = (JSONArray) jsonobj.get("messageStatus");
								int success = ChatDBWrapper.getInstance().insertMessageStatusInDB(messages, os_type);
								if (success == 1) {
									data = "" + success;
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//Delete folder
				deleteDirectoryWithContets(new File(path));
		    } catch (Exception e) {
		        e.printStackTrace();
		    } 
		    return data;
		}
		@Override
		protected void onPostExecute(String data) {
			if(dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			if(data != null){
				if(data.equals("1")){
					Toast.makeText(ChatBackupRestoreScreen.this, "Data restored successfully!!", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ChatBackupRestoreScreen.this, "Unable to restore data!", Toast.LENGTH_SHORT).show();
				}
				finish();
			}else{
				Toast.makeText(ChatBackupRestoreScreen.this, "Unable to restore data!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

