/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NO
 */

package com.tuya.appsdk.sample.device.config.ap;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.tuya.appsdk.sample.device.config.R;
import com.tuya.appsdk.sample.resource.HomeModel;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

/**
 * Device Configuration AP Mode Sample
 *
 * @author chuanfeng <a href="mailto:developer@tuya.com"/>
 * @since 2021/2/18 9:50 AM
 */
public class DeviceConfigAPActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "DeviceConfigAPActivity";

    public CircularProgressIndicator cpiLoading;
    public Button btnSearch;
    private TextView mContentTv;
    private String strSsid;
    private EditText etPassword;
    private String strPassword;
    private String mToken;
    private ITuyaActivator mTuyaActivator;
    private Button mBtnGetToken;
    private EditText mEtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_config_activity);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(getString(R.string.device_config_ap_title));

        cpiLoading = findViewById(R.id.cpiLoading);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        mContentTv = findViewById(R.id.content_tv);
        mContentTv.setText(getString(R.string.device_config_ap_description));

        mEtToken = findViewById(R.id.et_token);
        mBtnGetToken = findViewById(R.id.btn_get_token);
        mBtnGetToken.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        EditText etSsid = findViewById(R.id.etSsid);
        strSsid = etSsid.getText().toString();
        etPassword = findViewById(R.id.etPassword);
        strPassword = etPassword.getText().toString();

        if (v.getId() == R.id.btnSearch) {
            long homeId = HomeModel.getCurrentHome(this);

            // Get Network Configuration Token
            TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                    new ITuyaActivatorGetToken() {
                        @Override
                        public void onSuccess(String token) {
                            mToken = token;
                            onClickSetting();
                        }


                        @Override
                        public void onFailure(String errorCode, String errorMsg) {

                        }
                    });
        }

        if (v.getId() == R.id.btn_get_token) {
            long homeId = HomeModel.getCurrentHome(this);
            TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, new ITuyaActivatorGetToken() {
                @Override
                public void onSuccess(String token) {
                    Log.d(TAG, "??????token??????:" + token);
                    runOnUiThread(() ->
                            mEtToken.setText(token));
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    Log.d(TAG, "??????token??????:" + errorMsg);
                    runOnUiThread(() ->
                            Toast.makeText(DeviceConfigAPActivity.this, "????????????", Toast.LENGTH_LONG).show());
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Show loading progress, disable btnSearch clickable
        btnSearch.setClickable(false);

        cpiLoading.setVisibility(View.VISIBLE);
        cpiLoading.setIndeterminate(true);

        ActivatorBuilder builder = new ActivatorBuilder()
                .setSsid(strSsid)
                .setContext(DeviceConfigAPActivity.this)
                .setPassword(strPassword)
                .setActivatorModel(ActivatorModelEnum.TY_AP)
                .setTimeOut(100)
                .setToken(mToken)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        cpiLoading.setVisibility(View.GONE);
                        btnSearch.setClickable(true);
                        Toast.makeText(DeviceConfigAPActivity.this,
                                "Activate error-->" + errorMsg,
                                Toast.LENGTH_LONG
                        ).show();

                    }

                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        cpiLoading.setVisibility(View.GONE);

                        Log.i(TAG, "Activate success");
                        Toast.makeText(DeviceConfigAPActivity.this,
                                "Activate success",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    }

                    @Override
                    public void onStep(String step, Object data) {
                        Log.i(TAG, step + " --> " + data);
                    }
                });

        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newActivator(builder);

        //Start configuration
        mTuyaActivator.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Exit the page to destroy some cache data and monitoring data.
        if (null != mTuyaActivator) {
            mTuyaActivator.onDestroy();
        }
    }

    /**
     *
     * wifi setting
     */
    private void onClickSetting() {
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
            startActivity(wifiSettingsIntent);
        } else {
            wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
                startActivity(wifiSettingsIntent);
            }
        }
    }
}
