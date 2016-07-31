package com.superchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Prakash on 11-12-2015.
 */
public class ShareScreen extends Activity {
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        String type = getIntent().getType();
        Uri imageUri = (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        Intent intent = new Intent(ShareScreen.this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,imageUri);
        intent.setType(type);
        startActivity(intent);
        finish();
    }
}
