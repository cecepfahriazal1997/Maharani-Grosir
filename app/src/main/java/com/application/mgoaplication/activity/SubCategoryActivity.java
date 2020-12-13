package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.ListCategoryAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.CategoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubCategoryActivity extends MasterActivity implements View.OnClickListener {
    private ListCategoryAdapter adapter;
    private RecyclerView recyclerView;
    private List<CategoryModel> list = new ArrayList<>();
    private EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

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
        title.setText("Sub Kategori " + getIntent().getStringExtra("title"));
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
        try {
            list.clear();
            service.apiService(service.subCategoryProduct + getIntent().getStringExtra("id"), null, null,
                    false, "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data      = new JSONArray(hashMap.get("response").trim());
                                    JSONArray detail    = data.getJSONArray(0);

                                    for (int i = 0; i < detail.length(); i++) {
                                        JSONObject object   = detail.getJSONObject(i);
                                        CategoryModel param = new CategoryModel();
                                        param.setId(object.getString("id"));
                                        param.setTitle(object.getString("nama"));
                                        param.setTotal(String.valueOf(i));
                                        list.add(param);
                                    }
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    adapter     = new ListCategoryAdapter(getApplicationContext(), list,
                                            new ListCategoryAdapter.OnClickListener() {
                                                @Override
                                                public void onClickListener(int position) {
                                                    param.clear();
                                                    param.put("type", "sub_category");
                                                    param.put("groupId", list.get(position).getId());
                                                    param.put("title", list.get(position).getTitle());
                                                    helper.startIntent(ListProductActivity.class, false, param);
                                                }
                                            });
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}