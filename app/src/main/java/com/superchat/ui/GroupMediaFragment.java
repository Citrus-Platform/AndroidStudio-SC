package com.superchat.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chat.sdk.ChatService;
import com.chat.sdk.ConnectionStatusListener;
import com.chat.sdk.ProfileUpdateListener;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;

import static android.R.attr.data;
import static com.superchat.R.id.toolbar_child_fragment_tab;
import static com.superchat.ui.ChatListScreen.onForeground;

//import android.app.ListFragment;

public class GroupMediaFragment extends Fragment {
    public static final String TAG = "GroupMediaFragment";

    public static Fragment getInstance(final Context context){
        GroupMediaFragment instance = new GroupMediaFragment();
        return instance;
    }

    public View onCreateView(LayoutInflater layoutinflater,
                             ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.fragment_group_media, null);

        UtilSetFont.setFontMainScreen(view);

        return view;
    }
}