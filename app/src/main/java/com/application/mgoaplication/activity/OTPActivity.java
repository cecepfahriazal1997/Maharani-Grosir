package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.application.mgoaplication.R;
import com.application.mgoaplication.api.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class OTPActivity extends MasterActivity implements View.OnClickListener {
    private FancyButton btnSubmit;
    private TextView email, resend;
    private int time = 59;
    private boolean canResend = false;
    private PinEntryEditText codeOTP;
    private JSONObject profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        findView();
        init();
        setData();
        setTimer();
    }

    private void findView() {
        btnSubmit   = findViewById(R.id.btnSubmit);
        email       = findViewById(R.id.email);
        resend      = findViewById(R.id.resend);
        codeOTP     = findViewById(R.id.codeOTP);
    }

    private void init() {
        btnSubmit.setOnClickListener(this);
        resend.setOnClickListener(this);
        try {
            profile = new JSONObject(getIntent().getStringExtra("user"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setTimer() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                resend.setText("00:" + helper.checkDigit(time));
                time--;
            }

            public void onFinish() {
                resend.setText("Kirim Ulang");
                canResend = true;
            }
        }.start();
    }

    private void setData() {
        try {
            if (profile != null)
                email.setText(profile.getString("email").replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                try {
                    if (codeOTP.getText().toString().equals(profile.getString("otp"))) {
                        try {
                            helper.saveSessionBatch(profile);
                            helper.startIntent(DashboardActivity.class, false, null);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        helper.showToast("OTP yang anda masukan tidak sesuai !", 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.resend:
                if (canResend) {
                    time    = 59;
                    setTimer();
                    resendCode();
                }
                break;
        }
    }

    private void resendCode() {
        try {
            param.clear();
            param.put("id_member", profile.getString("id"));
            service.apiService(service.resendOTP, param, null, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray response  = new JSONArray(hashMap.get("response"));
                            if (response.length() > 0) {
                                JSONObject detail   = response.getJSONArray(0).getJSONObject(0);
                                if (detail.getString("status").equals("success"))
                                    helper.showToast("Kode otp telah berhasil dikirim ulang ke email!", 0);
                                else
                                    helper.showToast("Kode otp gagal dikirim ulang ke email!", 0);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
