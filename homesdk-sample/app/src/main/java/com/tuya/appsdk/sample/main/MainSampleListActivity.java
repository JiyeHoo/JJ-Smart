/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tuya.appsdk.sample.main;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.appsdk.sample.R;
import com.tuya.appsdk.sample.device.config.main.DeviceConfigFuncWidget;
import com.tuya.appsdk.sample.device.mgt.main.DeviceMgtFuncWidget;
import com.tuya.appsdk.sample.resource.HomeModel;
import com.tuya.appsdk.sample.user.main.UserFuncActivity;
import com.tuya.smart.android.user.api.ILogoutCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Sample Main List Page
 */
public final class MainSampleListActivity extends AppCompatActivity {

    private final String TAG = "###Main";


//    public HomeFuncWidget homeFuncWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_sample_list);

//        findViewById(R.id.tvUserInfo).setOnClickListener(v -> {
//            // User Info
//            startActivity(new Intent(MainSampleListActivity.this, UserInfoActivity.class));
//
//        });


        setHomeId();


        findViewById(R.id.tvLogout).setOnClickListener(v ->
                TuyaHomeSdk.getUserInstance().logout(new ILogoutCallback() {
                    @Override
                    public void onSuccess() {
                        // Clear cache
                        HomeModel.INSTANCE.clear(MainSampleListActivity.this);

                        // Navigate to User Func Navigation Page
                        Intent intent = new Intent(MainSampleListActivity.this, UserFuncActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                    }
        }));

        LinearLayout llFunc = findViewById(R.id.llFunc);

        // Home Management
//        homeFuncWidget = new HomeFuncWidget();
//        llFunc.addView(homeFuncWidget.render(this));

        // Device Configuration Management
        DeviceConfigFuncWidget deviceConfigFucWidget = new DeviceConfigFuncWidget();
        llFunc.addView(deviceConfigFucWidget.render(this));

        // Device Management
        DeviceMgtFuncWidget deviceMgtFuncWidget = new DeviceMgtFuncWidget();
        llFunc.addView(deviceMgtFuncWidget.render(this));


    }

    @Override
    protected void onResume() {
        super.onResume();
//        homeFuncWidget.refresh();
    }

    /**
     * ??????homelist
     */
    private void setHomeId() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeanList) {
                Toast.makeText(MainSampleListActivity.this, "???????????????", Toast.LENGTH_LONG).show();

                if (!homeBeanList.isEmpty()) {
                    Log.d(TAG, "home list ?????????");
                    // homeList ?????????????????????0???????????? Sp
                    long homeId = homeBeanList.get(0).getHomeId();
                    Log.d(TAG, "homeId:" + homeId);
                    HomeModel.INSTANCE.setCurrentHome(MainSampleListActivity.this, homeId);
                } else {
                    // ????????????????????? home
                    Log.d(TAG, "home list ??????,????????????");
                    createHome();
                }
            }

            @Override
            public void onError(String errorCode, String error) {
                Log.d(TAG, "?????? homeList ??????");
                Toast.makeText(MainSampleListActivity.this, "???????????????", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * ????????????
     */
    private void createHome() {
        TuyaHomeSdk.getHomeManagerInstance().createHome(
                "orgName",
                0,
                0,
                "",
                new ArrayList<>(),
                new ITuyaHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        // homeId ?????? sp
                        Log.d(TAG, "?????? home ??????:" + bean.getHomeId());
//                        HomeModel.INSTANCE.setHomeId(mContext, bean.getHomeId());

                        // ????????????????????????
                        setHomeId();
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Log.d(TAG, "?????? home ??????");
                        Toast.makeText(MainSampleListActivity.this, "????????????????????????", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}