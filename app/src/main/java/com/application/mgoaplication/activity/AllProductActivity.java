package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.ProductAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllProductActivity extends MasterActivity implements View.OnClickListener {
    private EditText keyword;
    private List<ProductModel> list = new ArrayList<>();
    private ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    private String url, type;
    private boolean onPause = false;
    private String tmpParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        findView();
        initViewPopUpCart();
        init();
        fetchData();
        helper.showPopUpCart(parent, popupCart, amount, price);
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        keyword         = findViewById(R.id.keyword);
        recyclerView    = findViewById(R.id.list);
    }

    public void init() {
        helper.saveSession("showBadge", "false");
        url         = getIntent().getStringExtra("url");
        type        = getIntent().getStringExtra("type");
        if (type.equals("post"))
            tmpParam    = getIntent().getStringExtra("param");
        title.setText(getIntent().getStringExtra("title"));
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (productAdapter != null && list != null && list.size() > 0) {
                    productAdapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        back.setOnClickListener(this::onClick);
        popupCart.setOnClickListener(this::onClick);
    }

    private void fetchData() {
        try {
            list.clear();
            if (type.equals("post")) {
                param.clear();
                JSONObject data = new JSONObject(tmpParam.trim());
                JSONArray keys = data.names();
                for (int i = 0; i < keys.length(); ++i) {
                    String key = keys.getString(i); // Here's your key
                    String value = data.getString(key); // Here's your value

                    param.put(key, value);
                }
            }
            service.apiService(url, param, null, false, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray data = new JSONArray(hashMap.get("response")).getJSONArray(0);
                            for (int i = 0; i < data.length(); i++) {
                                ProductModel item = new ProductModel();
                                JSONObject detail = data.getJSONObject(i);

                                item.setId(detail.getString("id"));
                                item.setName(detail.getString("nama_barang"));
                                item.setDescription(detail.getString("deskripsi"));
                                item.setImage(detail.getString("gambar1"));
                                item.setListPrice(detail.getJSONArray("harga"));
                                list.add(item);
                            }
                        } else {
                            helper.showToast(hashMap.get("message"), 0);
                        }
                        initRecyclerView(recyclerView, RecyclerView.VERTICAL, list, getApplicationContext(), productAdapter);
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
            case R.id.popupCart:
                helper.startIntent(CartActivity.class, false, null);
                break;
        }
    }

    public void onPause() {
        super.onPause();
        onPause = true;
    }

    public void onResume() {
        super.onResume();
        if (onPause) {
            helper.saveSession("showBadge", "false");
        }
    }
}
