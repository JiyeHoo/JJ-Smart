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

package com.tuya.appsdk.sample.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.appsdk.sample.R;
import com.tuya.appsdk.sample.main.MainSampleListActivity;
import com.tuya.appsdk.sample.user.resetPassword.UserResetPasswordActivity;
import com.tuya.smart.android.common.utils.ValidatorUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

/**
 * User Login Example
 *
 * @author chuanfeng <a href="mailto:developer@tuya.com"/>
 * @since 2021/2/9 2:01 PM
 */
public class UserLoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_login);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnForget).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText etAccount = findViewById(R.id.etAccount);
        String strAccount = etAccount.getText().toString();
        EditText etPassword = findViewById(R.id.etPassword);
        String strPassword = etPassword.getText().toString();

        // 点击登录按钮
        if (v.getId() == R.id.btnLogin) {
            ILoginCallback callback = new ILoginCallback() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(UserLoginActivity.this,
                            "Login success",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(UserLoginActivity.this, MainSampleListActivity.class));
                    finish();
                }

                @Override
                public void onError(String code, String error) {
                    Toast.makeText(UserLoginActivity.this,
                            "code: " + code + "error:" + error,
                            Toast.LENGTH_SHORT).show();
                }
            };
            if (ValidatorUtil.isEmail(strAccount)) {
                TuyaHomeSdk.getUserInstance().loginWithEmail("86", strAccount, strPassword, callback);
            } else {
                TuyaHomeSdk.getUserInstance().loginWithPhonePassword("86", strAccount, strPassword, callback);
            }
        }

        // 点击忘记密码
        if (v.getId() == R.id.btnForget) {
            startActivity(new Intent(this, UserResetPasswordActivity.class));
        }
    }
}
