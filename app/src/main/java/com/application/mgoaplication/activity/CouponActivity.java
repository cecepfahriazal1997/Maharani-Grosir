package com.application.mgoaplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.CouponAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.CouponModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class CouponActivity extends MasterActivity implements View.OnClickListener {
    private CouponAdapter adapter;
    private List<CouponModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private FancyButton submit;
    private EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        findView();
        init();
        setData();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        recyclerView    = findViewById(R.id.list);
        submit          = findViewById(R.id.submit);
        keyword         = findViewById(R.id.keyword);
    }

    private void init() {
        title.setText("Pilih Vouchermu");
        keyword.setHint("Masukan voucher disini ...");
        back.setOnClickListener(this::onClick);
        submit.setOnClickListener(this::onClick);
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null && list != null && list.size() > 0) {
                    adapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setData() {
        list.clear();
        try {
            param.clear();
            param.put("id_member", helper.getSession("id"));
            service.apiService(service.listVoucher, param, null, true,
                    "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data = new JSONArray(hashMap.get("response")).getJSONArray(0);
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject detail = data.getJSONObject(i);
                                        CouponModel item = new CouponModel();
                                        item.setId(detail.getString("id"));
                                        item.setTitle("Voucher Diskon");
                                        item.setDescription("Berlaku hingga " + detail.getString("tgl_berlaku") + "\nMin. Belanja Rp. "
                                                + helper.formatCurrency(detail.getString("minimal")));
                                        item.setAmount(detail.getString("nominal"));
                                        list.add(item);
                                    }
                                } else {
                                    helper.showToast(hashMap.get("message"), 1);
                                }
                                initRecyclerView(recyclerView, RecyclerView.VERTICAL);
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

    private void initRecyclerView(RecyclerView recyclerView, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, orientation, false);
        adapter     = new CouponAdapter(this, list, helper,
                new CouponAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        if (view.getId() == R.id.btnSubmit) {
                            chooseCoupon(position);
                        }
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void chooseCoupon(int position) {
        helper.showToast("Voucher berhasil digunakan !", 0);
        Intent intent = new Intent();
        intent.putExtra("value", list.get(position).getId());
        intent.putExtra("title", list.get(position).getAmount());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
