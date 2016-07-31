package com.superchat.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.widget.Toast;

/**
 * Created by MotoBeans on 1/10/2016.
 */
public class UtilShare {

    Context currContext;
    String message;
    
    public UtilShare(Context currContext, final String message){
        this.currContext = currContext;
        this.message = message;
    }
    public void shareText(){
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    ""+message);
            sendIntent.setType("text/plain");
            currContext.startActivity(sendIntent);
        } catch(Exception e){

        }
    }


    /**
     * Share Methods
     */

    final String S_SUBJECT_TO_SHARE = "Voidz";
    final String S_TEXT_TO_SHARE = "Dummy Test to Share";

    public void share_google_plus() {
        try {
            // Launch the Google+ share dialog with attribution to your app.
           /* Intent shareIntent = new PlusShare.Builder(currContext)
                    .setType("text/plain")
                    .setText("" + S_TEXT_TO_SHARE)
                    .setContentUrl(Uri.parse("http://www.tripmd.com/"))
                    .getIntent();

            currContext.startActivity(shareIntent);
            */
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Google Plus Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_gmail() {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setData(Uri.parse("test@gmail.com"));
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"test@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "" + S_SUBJECT_TO_SHARE);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Gmail Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_hangout() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.google.android.talk");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Hang-Out Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_messaging() {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Messaging Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_whatsapp() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Whatsapp Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_Facebook() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.facebook.katana");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Facebook Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_Facebook_messenger() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.facebook.orca");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Messenger Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_twitter() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.twitter.android");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Twitter Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_bluetooth() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.android.bluetooth");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_linkdin() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.linkedin.android");
            currContext.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Linkdin Not Supported", Toast.LENGTH_LONG).show();
        }
    }

    public void share_all() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "" + S_SUBJECT_TO_SHARE);
            i.putExtra(Intent.EXTRA_TEXT, "" + S_TEXT_TO_SHARE);

            // CharSequence styledText = Html.fromHtml(text);
            currContext.startActivity(Intent.createChooser(
                    i,
                    Html.fromHtml("Testing text")));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currContext, "Not Supported", Toast.LENGTH_LONG).show();
        }
    }
}
