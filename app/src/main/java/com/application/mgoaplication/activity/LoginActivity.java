package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.application.mgoaplication.R;
import com.application.mgoaplication.api.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends MasterActivity implements View.OnClickListener {
    private EditText username, password;
    private FancyButton btnSubmit, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findView();
        init();
    }

    private void findView() {
        username        = findViewById(R.id.username);
        password        = findViewById(R.id.password);
        btnRegister     = findViewById(R.id.btnRegister);
        btnSubmit       = findViewById(R.id.btnSubmit);
    }

    private void init() {
        btnRegister.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void setData() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                login();
                break;
            case R.id.btnRegister:
                helper.startIntent(RegisterActivity.class, false, null);
        }
    }

    private void login() {
        param.clear();
        param.put("username", username.getText().toString());
        param.put("password", password.getText().toString());
        param.put("token", "");

        service.apiService(service.login, param, null, true, "array", new Service.hashMapListener() {
            @Override
            public String getHashMap(Map<String, String> hashMap) {
                try {
                    if (hashMap.get("status").equals("true")) {
                        JSONArray response  = new JSONArray(hashMap.get("response").trim());
                        JSONObject detail   = response.getJSONObject(0);
                        if (!detail.getString("status").equals("failed")) {
                            helper.saveSessionBatch(detail);
                            if (helper.getSession("cart") == null)
                                helper.saveSession("cart", "0");
                            helper.startIntent(DashboardActivity.class, false, null);
                            finish();
                        } else {
                            helper.showToast(detail.getString("notification"), 0);
                        }
                    } else {
                        helper.showToast(hashMap.get("message"), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
