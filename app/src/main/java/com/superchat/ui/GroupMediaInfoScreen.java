
package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.RegistrationForm;
import com.superchat.model.SGroupListObject;
import com.superchat.retrofit.api.RetrofitRetrofitCallback;
import com.superchat.retrofit.response.model.ResponseOpenDomains;
import com.superchat.ui.Adapters.connectors.OpenGroupAdapterConnector;
import com.superchat.ui.Adapters.decorators.DividerItemDecoration;
import com.superchat.ui.Adapters.recycler.OpenGroupRecyclerAdapter;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.utils.Utilities;
import com.superchat.widgets.MyriadRegularTextView;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static android.provider.Contacts.SettingsColumns.KEY;
import static com.superchat.R.id.id_sg_name_label;
import static com.superchat.R.id.swipeRefreshLayout;
import static com.superchat.interfaces.interfaceInstances.objApi;
import static com.superchat.interfaces.interfaceInstances.objExceptione;
import static com.superchat.interfaces.interfaceInstances.objGlobal;
import static com.superchat.interfaces.interfaceInstances.objToast;
import static com.superchat.ui.HomeScreen.firstTimeAdmin;
import static com.superchat.ui.HomeScreen.isContactRefreshed;

public class GroupMediaInfoScreen extends AppCompatActivity implements OnClickListener, SmartTabLayout.TabProvider {

    private static final String KEY_groupUUID = "groupUUID";

    public static void start(Activity context, final String groupUUID) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_groupUUID, groupUUID);

        Intent starter = new Intent(context, GroupMediaInfoScreen.class);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    Activity activity = this;
    Context context = this;
    private MediaPagerAdapter mAdapter;

    @BindView(R.id.view_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.viewPagerTab)
    SmartTabLayout viewPagerTab;;

    private Toolbar toolbar;
    private static final String TAG = "OpenHubSearchScreen";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.group_media_indo_screen);

        init();
    }

    Bundle bundle;
    String groupUUID;
    private void init() {
        ButterKnife.bind(this);
        bundle = getIntent().getExtras();
        if(bundle != null){
            groupUUID = bundle.getString(KEY_groupUUID);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Media");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        populateData();
    }

    private void populateData(){
        viewPagerTab.setCustomTabView(this);

        mAdapter = new MediaPagerAdapter(getSupportFragmentManager(), getApplicationContext(), groupUUID);
        mViewPager.setPagingEnabled(true);
        mViewPager.setAdapter(mAdapter);

        viewPagerTab.setViewPager(mViewPager);

        setTabsCustom();
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position,
                                   float possitionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setTabsCustom();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // actions
        }
    };


    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

    }

    class MediaPagerAdapter extends FragmentPagerAdapter {
        private Context mContext;
        FragmentManager fm;
        private String groupUUID;

        public MediaPagerAdapter(FragmentManager fm, Context context, String groupUUID) {
            super(fm);
            this.fm = fm;
            this.mContext = context;
            this.groupUUID = groupUUID;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GroupMediaFragment.getInstance(mContext, groupUUID);
                case 1:
                    return GroupDocFragment.getInstance(mContext, groupUUID);
                default:
                    return new TempFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTabText(position);
        }

    }

    /**
     * Tabs
     */


    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        Resources res = container.getContext().getResources();
        View tab = inflater.inflate(R.layout.custom_tab_icon_and_notification_mark, container, false);

        tab = handleTabView(tab, position);

        return tab;
    }

    final private String[] titles = {"Media", "Docs"};
    private String getTabText(final int position) {
        return titles[position].toUpperCase();
    }

    private View handleTabView(View view, int position) {
        View tab = view;

        try {
            if (tab != null) {

                UtilSetFont.setFontMainScreen(view);

                LinearLayout llTabIndicator = (LinearLayout) tab.findViewById(R.id.llTabIndicator);
                TextView tvTabText = (TextView) tab.findViewById(R.id.tvTabText);

                String tabText = getTabText(position);
                tvTabText.setText(tabText);

                int flagFrag = mViewPager.getCurrentItem();
                if (flagFrag == (position)) {
                    setSelectTabStyle(tvTabText);
                    llTabIndicator.setVisibility(View.VISIBLE);
                } else {
                    setUnSelectTabStyle(tvTabText);
                    llTabIndicator.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tab;
    }

    private void setSelectTabStyle(TextView tvTab) {
        if (tvTab != null) {
            tvTab.setTypeface(tvTab.getTypeface(), Typeface.BOLD);
            tvTab.setTextColor(ContextCompat.getColor(this, R.color.textColorPagerSelected));
        }
    }

    private void setUnSelectTabStyle(TextView tvTab) {
        tvTab.setTypeface(tvTab.getTypeface(), Typeface.NORMAL);
        tvTab.setTextColor(ContextCompat.getColor(this, R.color.textColorPagerUnselected));
    }

    public void setTabsCustom() {
        int count = 0;
        try {
            for (int index = 0; index < 10; index++) {
                viewPagerTab.getTabAt(index);
                count++;
            }
        } catch (Exception e) {

        }
        for (int index = 0; index < count; index++) {
            View tab = viewPagerTab.getTabAt(index);
            if (tab != null) {
                handleTabView(tab, index);
            }
        }
    }

}

