package com.superchat.rtcamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.superchat.R;
import com.superchat.ui.HomeScreen;
//import com.kainat.app.android.core.BusinessProxy;

public class CameraSetting extends Activity {

	//private CheckBox nightMod, flash, chkWindows;
	private Button btnDisplay,btnColorEffect,btnfocusMode,btnFlashMode,btnSceneMode;
	StringBuffer result = new StringBuffer();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camer_setting);

		//addListenerOnChkIos();
		addListenerOnButton();
	}

	/*public void addListenerOnChkIos() {

		chkIos = (CheckBox) findViewById(R.id.chkIos);

		chkIos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					Toast.makeText(CameraSetting.this,
							"Bro, try Android :)", Toast.LENGTH_LONG).show();
				}

			}
		});

	}*/

	public void addListenerOnButton() {

		//nightMod = (CheckBox) findViewById(R.id.chkIos);
		//flash = (CheckBox) findViewById(R.id.chkAndroid);
		//chkWindows = (CheckBox) findViewById(R.id.chkWindows);
		btnDisplay = (Button) findViewById(R.id.btnDisplay);
		btnColorEffect=(Button) findViewById(R.id.btnColorEffect);
		btnfocusMode =(Button)findViewById(R.id.btnfocusMode);
		
		btnFlashMode =(Button)findViewById(R.id.btnflashmode);
		btnSceneMode =(Button)findViewById(R.id.btnScenemode);
		btnDisplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			/*	result.append("#SceneMode=").append(
                		btnSceneMode.getText());
				result.append("#FlashMode=").append(
                		btnFlashMode.getText());
				result.append("#FocusMode=").append(
                		btnfocusMode.getText());
				result.append("#ColorMode=").append(
                		btnColorEffect.getText());*/
				
				HomeScreen.cameraSettingMap.put("#SceneMode", btnSceneMode.getText().toString());
				HomeScreen.cameraSettingMap.put("#FlashMode", btnFlashMode.getText().toString());
				HomeScreen.cameraSettingMap.put("#FocusMode", btnfocusMode.getText().toString());
				HomeScreen.cameraSettingMap.put("#ColorMode", btnColorEffect.getText().toString());
				//BusinessProxy.sSelf.CameraSetting=result.toString();
				//result.append("#NightMode=")
					//	.append(nightMod.isChecked());
				//result.append("#FlashMode=").append(
					//	flash.isChecked());
				//result.append("\nWindows Mobile check :").append(
						//chkWindows.isChecked());
					//Intent intent =new Intent(CameraSetting.this,DgCamActivity.class);
					//startActivity(intent);
					finish();
				Toast.makeText(CameraSetting.this, result.toString(),
						Toast.LENGTH_LONG).show();

			}
		});
		
		btnColorEffect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 final CharSequence[] items = { "EFFECT_WHITEBOARD", "EFFECT_SOLARIZE", "EFFECT_SEPIA", "EFFECT_POSTERIZE", "EFFECT_NONE", "EFFECT_NEGATIVE", "EFFECT_MONO", "EFFECT_AQUA", "EFFECT_BLACKBOARD" };
	                AlertDialog.Builder builder = new AlertDialog.Builder(CameraSetting.this);
	                builder.setTitle("Color Effect");
	                builder.setSingleChoiceItems(items, -1,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int item) {
	                                Toast.makeText(getApplicationContext(),
	                                        items[item], Toast.LENGTH_SHORT).show();
	                                
	                                btnColorEffect.setText(items[item]);
	                                dialog.cancel();
	                               
	                            }
	                        });
	               
	                AlertDialog alert = builder.create();
	                alert.show();
			}
		});
		
		btnfocusMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 final CharSequence[] items = { "FOCUS_MODE_MACRO", "FOCUS_MODE_INFINITY", "FOCUS_MODE_FIXED", "FOCUS_MODE_AUTO" };
	                AlertDialog.Builder builder = new AlertDialog.Builder(CameraSetting.this);
	                builder.setTitle("Focus Mode");
	                builder.setSingleChoiceItems(items, -1,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int item) {
	                                Toast.makeText(getApplicationContext(),
	                                        items[item], Toast.LENGTH_SHORT).show();
	                                
	                                btnfocusMode.setText(items[item]);
	                                dialog.cancel();
	                               
	                            }
	                        });
	               
	                AlertDialog alert = builder.create();
	                alert.show();
			}
		});
btnFlashMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 final CharSequence[] items = { "FLASH_MODE_TORCH", "FOCUS_MODE_INFINITY", "FLASH_MODE_ON", "FLASH_MODE_OFF" ,"FLASH_MODE_AUTO"};
	                AlertDialog.Builder builder = new AlertDialog.Builder(CameraSetting.this);
	                builder.setTitle("Flash Mode");
	                builder.setSingleChoiceItems(items, -1,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int item) {
	                                Toast.makeText(getApplicationContext(),
	                                        items[item], Toast.LENGTH_SHORT).show();
	                                
	                                btnFlashMode.setText(items[item]);
	                                dialog.cancel();
	                               
	                            }
	                        });
	               
	                AlertDialog alert = builder.create();
	                alert.show();
			}
		});
btnSceneMode.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		 final CharSequence[] items = { "SCENE_MODE_SUNSET", "SCENE_MODE_STEADYPHOTO", "SCENE_MODE_SPORTS", "SCENE_MODE_SNOW" ,"SCENE_MODE_PORTRAIT","SCENE_MODE_PARTY","SCENE_MODE_NIGHT","SCENE_MODE_LANDSCAPE","SCENE_MODE_FIREWORKS","SCENE_MODE_CANDLELIGHT","SCENE_MODE_BEACH","SCENE_MODE_AUTO","SCENE_MODE_ACTION","SCENE_MODE_THEATRE"};
            AlertDialog.Builder builder = new AlertDialog.Builder(CameraSetting.this);
            builder.setTitle("Scene Mode");
            builder.setSingleChoiceItems(items, -1,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Toast.makeText(getApplicationContext(),
                                    items[item], Toast.LENGTH_SHORT).show();
                            
                            btnSceneMode.setText(items[item]);
                            dialog.cancel();
                           
                        }
                    });
           
            AlertDialog alert = builder.create();
            alert.show();
	}
});


		
	}
}