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

package com.tuya.appsdk.sample.device.mgt.control.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.appsdk.sample.device.mgt.R;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class DeviceMgtControlActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "###DeviceControl";
    private boolean isOpen = false;

    private TextView tvDeviceName;
    private ImageView mIvIsOpen;
    private TextView mTvTempUp;
    private EditText mEtTempDown;
    private Button mBtnTempDown;
    private Button mBtnRemove;

    private ITuyaDevice mDevice;
    private DeviceBean deviceBean;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.unRegisterDevListener();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_mgt_activity_control);

        initView();

        String deviceId = getIntent().getStringExtra("deviceId");

        mDevice = TuyaHomeSdk.newDeviceInstance(deviceId);
        deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(deviceId);

        tvDeviceName.setText(deviceBean.getName());

        // ??????
        String name = deviceBean.getName();
        isOpen = (boolean) deviceBean.getDps().get("1");
        int tempUp = (int) deviceBean.getDps().get("101");
        double tempUpDouble = tempUp / 10.0;
        int tempDown = (int) deviceBean.getDps().get("102");
        setIsOpen(isOpen);

        Log.d(TAG, "??????:" + name);
        Log.d(TAG, "??????:" + isOpen);
        Log.d(TAG, "??????:" + tempUpDouble + " ???");
        Log.d(TAG, "??????:" + tempDown);

        // ??????
        mDevice.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d(TAG, "???????????????" + dpStr.toString());
                for (String key : dpStr.keySet()) {
                    if ("WENDU".equals(key)) {
                        // ???????????????
                        Log.d(TAG, "???????????????:" + dpStr.get("WENDU").toString());
                        runOnUiThread(() -> setTempToView((int) dpStr.get("WENDU")));
                    } else if ("switch_1".equals(key)) {
                        // ???????????????
                        Log.d(TAG, "???????????????:" + dpStr.get(key));
//                        isOpen = (boolean) dpStr.get(key);
//                        runOnUiThread(() -> setIsOpen(isOpen));
                    }
                }
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {

            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });

        // ??????????????????
        setIsOpen(isOpen);
        mTvTempUp.setText(tempUpDouble + " ???");
//        mEtTempDown.setText(String.valueOf(tempDown));

//        for (SchemaBean bean : schemaBeans) {
//
//            Object value = deviceBean.getDps().get(bean.getId());
//
//
//            if (bean.type.equals(DataTypeEnum.OBJ.getType())) {
//                // obj
//                switch (bean.getSchemaType()) {
//                    case BoolSchemaBean.type:
//                        DpBooleanItem dpBooleanItem = new DpBooleanItem(
//                                this,
//                                bean,
//                                (Boolean) value,
//                                mDevice);
//                        llDp.addView(dpBooleanItem);
//                        break;
//
//                    case EnumSchemaBean.type:
//                        DpEnumItem dpEnumItem = new DpEnumItem(
//                                this,
//                                bean,
//                                value.toString(),
//                                mDevice);
//                        llDp.addView(dpEnumItem);
//                        break;
//
//                    case StringSchemaBean.type:
//                        DpCharTypeItem dpCharTypeItem = new DpCharTypeItem(
//                                this,
//                                bean,
//                                (String) value,
//                                mDevice);
//                        llDp.addView(dpCharTypeItem);
//                        break;
//
//                    case ValueSchemaBean.type:
//                        DpIntegerItem dpIntegerItem = new DpIntegerItem(
//                                this,
//                                bean,
//                                (int) value,
//                                mDevice);
//                        llDp.addView(dpIntegerItem);
//
//                        break;
//
//                    case BitmapSchemaBean.type:
//                        DpFaultItem dpFaultItem = new DpFaultItem(
//                                this,
//                                bean,
//                                value.toString());
//                        llDp.addView(dpFaultItem);
//                }
//
//            } else if (bean.type.equals(DataTypeEnum.RAW.getType())) {
//                // raw | file
//                if (value == null) {
//                    value = "null";
//                }
//                DpRawTypeItem dpRawTypeItem = new DpRawTypeItem(
//                        this,
//                        bean,
//                        value.toString(),
//                        mDevice);
//                llDp.addView(dpRawTypeItem);
//
//            }
//        }

    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.group_control);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvDeviceName = findViewById(R.id.tvDeviceName);
        mIvIsOpen = findViewById(R.id.iv_is_open);
        mTvTempUp = findViewById(R.id.tv_temp_up);
        mEtTempDown = findViewById(R.id.et_temp_down);
        mBtnTempDown = findViewById(R.id.btn_start_down);
        mBtnRemove = findViewById(R.id.btnRemove);
        mIvIsOpen.setOnClickListener(this);
        mBtnTempDown.setOnClickListener(this);
        mBtnRemove.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_is_open) {
            // ??????????????????
            // true:?????????????????????
            String dps = isOpen ? "{\"1\": false}" : "{\"1\": true}";

            setIsOpen(!isOpen);
            isOpen = !isOpen;

            sendOpen(dps);
        }

        if (v.getId() == R.id.btn_start_down) {

            View view = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
            NumberPicker numberPicker = view.findViewById(R.id.number_picker);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(50);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("??????????????????")
                    .setView(view)
                    .setPositiveButton("??????", (dialog, which) -> sendTempDown(numberPicker.getValue()))
                    .setNegativeButton("??????", null)
                    .show();

        }

        if (v.getId() == R.id.btnRemove) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("??????????????????")
                    .setMessage("???????????????????????????????????????????????????????????????")
                    .setPositiveButton("??????", (dialog, which) -> {
                        startRemove();
                    })
                    .setNegativeButton("??????", null)
                    .show();

        }
    }

    private void setIsOpen(boolean open) {
        // ??????????????????
        mIvIsOpen.setColorFilter(open ? Color.GREEN : Color.RED);
    }

    /**
     * ????????????
     */
    private void sendTempDown(int temp) {

        String dps = "{\"102\":" + temp + "}";
        Log.d(TAG, "???????????????" + dps);

        mDevice.publishDps(dps, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d(TAG, "????????????:" + error);
                runOnUiThread(() -> {
                    Toast.makeText(DeviceMgtControlActivity.this, "????????????:" + error, Toast.LENGTH_LONG).show();
                    setIsOpen(!isOpen);
                    isOpen = !isOpen;
                });

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "????????????");
                runOnUiThread(() -> {
                    Toast.makeText(DeviceMgtControlActivity.this, "????????????", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * ????????????
     */
    private void sendOpen(String dps) {
        mDevice.publishDps(dps, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d(TAG, "????????????:" + error);
                runOnUiThread(() -> {
                    Toast.makeText(DeviceMgtControlActivity.this, "????????????:" + error, Toast.LENGTH_LONG).show();
                    setIsOpen(!isOpen);
                    isOpen = !isOpen;
                });

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "????????????");
                runOnUiThread(() -> {
                    Toast.makeText(DeviceMgtControlActivity.this, "????????????", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * ??????
     */
    private void startRemove() {
        mDevice.removeDevice(new IResultCallback() {
            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(DeviceMgtControlActivity.this,
                        "??????????????????:" + errorMsg,
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(DeviceMgtControlActivity.this, "??????????????????", Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }

    /**
     * ????????????
     */
    private void setTempToView(int value) {
        double valueDouble = value / 10.0;
        mTvTempUp.setText(valueDouble + " ???");
    }
}

