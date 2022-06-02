package com.example.instabug;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instabug.utils.Helpers;
import com.example.instabug.utils.Response;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.instabug.utils.StringPair;
import com.example.instabug.view.CustomToolbar;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class ResponseActivity extends AppCompatActivity {
    static final int SUCCESS = 0;
    static final int FAIL = 1;
    final int DIALOG_ERROR_CONNECT = 0;
    String responseBody;
    ArrayList<StringPair> headers;
    ArrayList<StringPair> params;
    ArrayList<StringPair> body;
    String method;
    String url;
    String charset;
    String mimeType;
    Response response;
    MyHandler myHandler = new MyHandler(this);
    boolean refreshing;
    int statusCode;
    CharSequence responseHeaders;
    CustomToolbar toolbar;
    ViewPager viewPager;
    TextView textViewStatusCode;
    TextView textViewHeader;
    View refreshView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView textViewURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        toolbar = findViewById(R.id.appBar);
        textViewURL = findViewById(R.id.textViewURL);
        textViewStatusCode = findViewById(R.id.textViewStatusCode);
        textViewHeader = findViewById(R.id.textViewHeaders);
        viewPager = findViewById(R.id.content);
        viewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        refreshView = findViewById(R.id.refreshingView);
        Intent intent = getIntent();
        url = intent.getStringExtra("http") + intent.getStringExtra("url");
        params = (ArrayList<StringPair>) intent.getSerializableExtra("parameter");
        headers = (ArrayList<StringPair>) intent.getSerializableExtra("header");
        body = (ArrayList<StringPair>) intent.getSerializableExtra("body");
        url = Helpers.combineUrl(url, params);
        method = intent.getStringExtra("method");
        if (savedInstanceState == null || (refreshing = savedInstanceState.getBoolean("refreshing"))) {
            refresh();
        }
        else{
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            url = savedInstanceState.getString("url");
            statusCode = savedInstanceState.getInt("statusCode");
            responseHeaders = savedInstanceState.getCharSequence("responseHeaders");
            textViewURL.setText(url);
            textViewStatusCode.setText(String.valueOf(statusCode));
            textViewHeader.setText(responseHeaders);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        ViewCompat.setElevation(bottomSheet, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        if (savedInstanceState != null)
            bottomSheetBehavior.setState(savedInstanceState.getInt("bottomSheetState"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_result_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });
        final View dimBackground = findViewById(R.id.dimBackground);
        dimBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    dimBackground.setVisibility(View.GONE);
                    textViewHeader.clearFocus();
                    textViewURL.clearFocus();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                dimBackground.setVisibility(View.VISIBLE);
                dimBackground.setAlpha(slideOffset);
            }
        });
    }


    private void refresh() {
        refreshing = true;
        refreshView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        toolbar.setAlpha(0.4f);
        toolbar.touchable(false);
        new APIClient(ResponseActivity.this, url, method, body,headers,response,
                new APIClient.OnCompleteListener() {
                    @Override
                    public void onComplete(Response result) {
                        if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            response=result;
                            new MyCallback(response);
                        }
                    }
                }).execute();
    }

    void refreshFinish() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        textViewURL.setText(url);
        textViewStatusCode.setText(String.valueOf(statusCode));
        textViewHeader.setText(responseHeaders);
        refreshView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        toolbar.setAlpha(1f);
        toolbar.touchable(true);
    }

    void refreshFail(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("errorMessage", message);
        showDialog(DIALOG_ERROR_CONNECT, bundle);
        refreshView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        toolbar.setAlpha(1f);
        toolbar.touchable(true);
    }

    @Override
    protected void onDestroy() {
        responseBody = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshing", refreshing);
        outState.putInt("bottomSheetState", bottomSheetBehavior.getState());
        outState.putInt("statusCode", statusCode);
        outState.putCharSequence("responseHeaders", responseHeaders);
        outState.putString("url", url);
        outState.putString("charset", charset);
        outState.putString("mimeType", mimeType);
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        return new AlertDialog.Builder(this).setTitle(R.string.dialog_error).setMessage(getString
                (R.string.error, args.getString("errorMessage"))).setPositiveButton(getString(R
                .string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).create();
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ResponseActivity> mFragment;

        MyHandler(ResponseActivity fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                ResponseActivity fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshFinish();
                }
            } else if (msg.what == FAIL) {
                ResponseActivity fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshFail(msg.getData().getString("error"));
                }
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ResponseFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

private class MyCallback {
    public MyCallback(Response response) {
        responseBody = response.getResponseBody();
        url = response.getRequestUrl();
        statusCode = response.getResponseCode();
        responseHeaders = Helpers.buildHeaders(response.getResponseHeaders());
        refreshing = false;
        if (statusCode != 200) {
            myHandler.sendEmptyMessage(FAIL);
            refreshFail("Error Handling Request");
        }
        else {
            myHandler.sendEmptyMessage(SUCCESS);
            refreshFinish();
        }

    }
}
}
