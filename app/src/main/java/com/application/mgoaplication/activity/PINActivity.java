package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.application.mgoaplication.R;
import com.application.mgoaplication.api.Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class PINActivity extends MasterActivity implements View.OnClickListener {
    private FancyButton btnSubmit;
    private TextView title, description;
    private String type;
    private PinEntryEditText pin;
    private JSONObject profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        findView();
        init();
    }

    private void findView() {
        btnSubmit   = findViewById(R.id.btnSubmit);
        title       = findViewById(R.id.title);
        description = findViewById(R.id.description);
        pin         = findViewById(R.id.pin);
    }

    private void init() {
        try {
            type    = getIntent().getStringExtra("type");
            if (type.equals("confirm")) {
                title.setText("Konfirmasi PIN");
                description.setText("Silahkan konfirmasi nomor PIN baru untuk menyelesaikan proses pendaftaran.");
                btnSubmit.setText("Konfirmasi");
            } else if (type.equals("login")) {
                title.setText("Masukan PIN");
                description.setText("Silahkan masukan nomor PIN untuk masuk ke Aplikasi.");
                btnSubmit.setText("Login");
            } else {
                title.setText("Membuat PIN Baru");
                description.setText("Silahkan membuat Nomor PIN baru untuk keamanan akun kamu.");
                btnSubmit.setText("Simpan");
            }
            btnSubmit.setOnClickListener(this);
            try {
                profile = new JSONObject(getIntent().getStringExtra("user"));
            } catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (type.equals("login")) {
                    helper.startIntent(DashboardActivity.class, false, null);
                    finish();
                } else if (type.equals("add")) {
                    helper.saveSession("newPassword", pin.getText().toString());
                    param.clear();
                    param.put("type", "confirm");
                    param.put("user", getIntent().getStringExtra("user"));
                    helper.startIntent(PINActivity.class, false, param);
                } else if (type.equals("confirm")) {
                    if (helper.getSession("newPassword").equals(pin.getText().toString()))
                        if (profile != null) {
                            try {
                                savePin(profile.getString("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    else
                        helper.showToast("PIN yang anda masukan tidak sama !", 0);
                }
                break;
        }
    }

    private void savePin(String id) {
        param.clear();
        param.put("id_member", id);
        param.put("pin", pin.getText().toString());
        service.apiService(service.savePIN, param, null, true, "array", new Service.hashMapListener() {
            @Override
            public String getHashMap(Map<String, String> hashMap) {
                try {
                    if (hashMap.get("status").equals("true")) {
                        JSONArray response = new JSONArray(hashMap.get("response"));
                        JSONObject detail = response.getJSONObject(0);
                        if (detail.getString("status").equals("success")) {
                            helper.saveSession("setPin", "1");
                            param.clear();
                            param.put("user", getIntent().getStringExtra("user"));
                            helper.startIntent(OTPActivity.class, false, param);
                        } else {
                            helper.showToast("Oops PIN baru gagal disimpan!", 0);
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
