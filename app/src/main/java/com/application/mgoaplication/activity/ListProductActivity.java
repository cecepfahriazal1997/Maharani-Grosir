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
import com.application.mgoaplication.adapter.ProductAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.CategoryModel;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListProductActivity extends MasterActivity implements View.OnClickListener {
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    private List<ProductModel> list = new ArrayList<>();
    private EditText keyword;
    private String type, groupId;

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
        type            = getIntent().getStringExtra("type");
        groupId         = getIntent().getStringExtra("groupId");
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
            String url = "";
            if (type.equals("category"))
                url = service.listProductByCategory + groupId;
            else
                url = service.listProductBySubCategory + groupId;

            service.apiService(url, null, null,
                    false,"array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data      = new JSONArray(hashMap.get("response").trim());
                                    JSONArray lists     = data.getJSONArray(0);

                                    for (int i = 0; i < lists.length(); i++) {
                                        ProductModel item = new ProductModel();
                                        JSONObject detail = lists.getJSONObject(i);

                                        item.setId(detail.getString("id"));
                                        item.setName(detail.getString("nama_barang"));
                                        item.setDescription(detail.getString("deskripsi"));
                                        item.setImage(detail.getString("gambar1"));
                                        item.setListPrice(detail.getJSONArray("harga"));

                                        list.add(item);
                                    }
                                    initRecyclerView(RecyclerView.VERTICAL, list);
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

    private void initRecyclerView(int orientation, List<ProductModel> listProduct) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), orientation, false);
        adapter     = new ProductAdapter(getApplicationContext(), listProduct, helper, service,
                new ProductAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position, Map<String, Object> paramAmount) {
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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