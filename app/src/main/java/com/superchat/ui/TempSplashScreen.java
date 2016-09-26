package com.superchat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.superchat.R;
import com.superchat.helper.SuperGroupNotificationsInfoHandler;

import java.util.Random;

public class TempSplashScreen extends Activity implements View.OnClickListener{

    Button btnPrintAll;
    Button btnNewGroup;
    Button btnIncrement;
    Button btnDecrement;

    SuperGroupNotificationsInfoHandler objNotiHandler = new SuperGroupNotificationsInfoHandler(this, "NewGroup1");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activity_splash);

        init();
    }

    private void init(){

        SuperGroupNotificationsInfoHandler spObj = new SuperGroupNotificationsInfoHandler(this, "GroupName");
        spObj.updateCounter(25);


        btnPrintAll = (Button) findViewById(R.id.btnPrintAll);
        btnNewGroup = (Button) findViewById(R.id.btnNewGroup);
        btnIncrement = (Button) findViewById(R.id.btnIncrement);
        btnDecrement = (Button) findViewById(R.id.btnDecrement);

        btnPrintAll.setOnClickListener(this);
        btnNewGroup.setOnClickListener(this);
        btnIncrement.setOnClickListener(this);
        btnDecrement.setOnClickListener(this);
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private int getSPNumber(){
        int groupNumber = getRandomNumberInRange(0, 100);
        return groupNumber;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch(viewID){
            case R.id.btnNewGroup:{
                objNotiHandler = new SuperGroupNotificationsInfoHandler(this, "NewGroup"+getSPNumber());
                break;
            }
            case R.id.btnIncrement:{
                objNotiHandler.incrementCounter();
                break;
            }
            case R.id.btnDecrement:{
                objNotiHandler.decrementCounter();
                break;
            }
            case R.id.btnPrintAll:{
                break;
            }
        }
    }
}
