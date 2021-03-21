package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.ProductAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class AllProductActivity extends MasterActivity implements View.OnClickListener {
    private EditText keyword;
    private List<ProductModel> list = new ArrayList<>();
    private ProductAdapter productAdapter;
    private FancyButton loadMore;
    private RecyclerView recyclerView;
    private String url, type;
    private boolean onPause = false;
    private String tmpParam;
    private int page = 1;
    private SwipeRefreshLayout refreshLayout;
    private FrameLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        findView();
        initViewPopUpCart();
        init();
        initSearchProduct();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData(true, null);
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchData(true, null);
            }
        });
        helper.showPopUpCart(parent, popupCart, amount, price);
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        keyword         = findViewById(R.id.keyword);
        recyclerView    = findViewById(R.id.list);
        loadMore        = findViewById(R.id.loadMore);
        refreshLayout   = findViewById(R.id.swipe_refresh_layout);
        emptyState      = findViewById(R.id.emptyState);
    }

    public void init() {
        helper.saveSession("showBadge", "false");
        url         = getIntent().getStringExtra("url");
        type        = getIntent().getStringExtra("type");
        if (type.equals("post"))
            tmpParam    = getIntent().getStringExtra("param");
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(this::onClick);
        popupCart.setOnClickListener(this::onClick);
        loadMore.setOnClickListener(this::onClick);
    }

    private void initSearchProduct() {
        keyword.setImeActionLabel("CARI", EditorInfo.IME_ACTION_SEARCH);
        keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        try {
                            String tmpKey = keyword.getText().toString();
                            if (type.equals("post")) {
                                JSONObject data = new JSONObject();
                                data.put("keywords", tmpKey);
                                tmpParam = data.toString();
                            }
                            fetchData(true, tmpKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void fetchData(boolean clearData, String keyword) {
        try {
            // if clear data or no load more
            if (clearData) {
                page    = 1;
                list.clear();
            } else {
                page++;
            }
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
            String tmpUrl = "";
            tmpUrl = url + "page=" + page;
            if (keyword != null && !keyword.isEmpty())
                tmpUrl += "&keywords=" + keyword;
            service.apiService(tmpUrl, param, null, false, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    refreshLayout.setRefreshing(false);
                    helper.showEmptyState(emptyState, false, R.drawable.empty, null, null);
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray response  = new JSONArray(hashMap.get("response"));
                            JSONArray data      = null;
//                            init data
                            if (!response.isNull(0)) {
                                data = response.getJSONArray(0);
                                if (data.length() > 0) {
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
                                    helper.showEmptyState(emptyState, true, R.drawable.empty, "Oops!", "Barang yang kamu cari tidak ditemukan.");
                                }
                            } else {
                                helper.showEmptyState(emptyState, true, R.drawable.empty, "Oops!", "Barang yang kamu cari tidak ditemukan.");
                            }
//                            init pagination
                            JSONObject pagination = new JSONArray(hashMap.get("response")).getJSONObject(1);
                            if (pagination.getBoolean("next_page")) {
                                loadMore.setVisibility(View.VISIBLE);
                            } else {
                                loadMore.setVisibility(View.GONE);
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
            case R.id.loadMore:
                fetchData(false, null);
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
