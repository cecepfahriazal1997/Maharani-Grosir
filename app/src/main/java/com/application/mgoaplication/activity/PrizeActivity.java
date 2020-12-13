package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.PrizeAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.PrizeModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrizeActivity extends MasterActivity implements View.OnClickListener {
    private PrizeAdapter adapter;
    private List<PrizeModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize);

        findView();
        init();
        setData();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        recyclerView    = findViewById(R.id.list);
        keyword         = findViewById(R.id.keyword);
    }

    private void init() {
        title.setText("Tukar Hadiah");
        keyword.setHint("Masukan nama hadiah ...");
        back.setOnClickListener(this::onClick);
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
            service.apiService(service.listPrize, null, null, true,
                    "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data = new JSONArray(hashMap.get("response")).getJSONArray(0);
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject detail = data.getJSONObject(i);
                                        PrizeModel item = new PrizeModel();
                                        item.setId(detail.getString("id"));
                                        item.setImage(detail.getString("gambarFile"));
                                        item.setTitle(detail.getString("nama_hadiah"));
                                        item.setDate(helper.convertDate(detail
                                                .getString("tanggal_berlaku"), "yyyy-MM-dd", "dd MMM yyyy"));
                                        item.setCoin(detail.getString("jumlah_poin"));
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
        adapter     = new PrizeAdapter(this, list, helper,
                new PrizeAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        if (view.getId() == R.id.btnSubmit) {
                            getPrize(list.get(position).getId(), list.get(position).getCoin());
                        }
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getPrize(String prizeId, String poin) {
        try {
            param.clear();
            param.put("id_member", helper.getSession("id"));
            param.put("id_hadiah", prizeId);
            param.put("jumlah_poin", poin);
            service.apiService(service.getPrize, param, null, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONObject data = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                            if (data.getString("status").equals("success")) {
                                helper.showToast("Hadiah berhasil dibeli menggunakan poin !", 1);
                                finish();
                            } else {
                                helper.showToast("Hadiah gagal dibeli menggunakan poin !", 1);
                            }
                        } else {
                            helper.showToast(hashMap.get("message"), 1);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
