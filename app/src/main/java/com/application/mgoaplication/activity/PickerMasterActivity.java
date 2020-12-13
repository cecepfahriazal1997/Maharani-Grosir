package com.application.mgoaplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.AdapterPickerMaster;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ModelPickerMaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class PickerMasterActivity extends MasterActivity implements View.OnClickListener {
    private String id="", name="", textTitle="";
    private boolean isMultiple = false;
    private FancyButton btnSubmit;
    private RecyclerView recyclerView;
    private EditText keyword;
    private SwipeRefreshLayout refreshLayout;

    private AdapterPickerMaster adapter;
    private String dataId="";
    private List<ModelPickerMaster> list = new ArrayList<>();
    private List<ModelPickerMaster> listFiltered = new ArrayList<>();
    private Context context;
    private List<String> listId = new ArrayList<>();
    private List<String> listName = new ArrayList<>();
    private String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_master);

        bindView();
        initial();
    }

    private void bindView() {
        back            = findViewById(R.id.back);
        title           = findViewById(R.id.title);
        recyclerView    = findViewById(R.id.recyclerView);
        btnSubmit       = findViewById(R.id.btnSubmit);
        keyword         = findViewById(R.id.keyword);
        refreshLayout   = findViewById(R.id.swipe_refresh_layout);
    }

    private void initial() {
        url         = getIntent().getStringExtra("url");
        textTitle   = getIntent().getStringExtra("title");
        isMultiple  = getIntent().getStringExtra("multiple").equals("1");
        context     = this.getApplicationContext();
        if (getIntent().hasExtra("id") && !getIntent().getStringExtra("id").isEmpty()) {
            dataId  = getIntent().getStringExtra("id");
            id      = dataId;
            if (isMultiple) {
                listId  = new ArrayList<String>(Arrays.asList(TextUtils.split(dataId, ",")));
            }
        }

        title.setText(textTitle);
        back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMasterData();
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getMasterData();
            }
        });

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

    private void getMasterData() {
        keyword.setText(null);
        refreshLayout.setRefreshing(true);
        try {
            param.clear();
            list.clear();

            service.apiService(url, null, null, false, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> response) {
                    refreshLayout.setRefreshing(false);
                    if (response.get("status").equals("true")) {
                        JSONArray data = null;
                        try {
                            JSONArray header = new JSONArray(response.get("response").trim());
                            data = header.getJSONArray(0);
                            for (int i = 0; i < data.length(); ++i) {
                                JSONObject detail   = data.getJSONObject(i);

                                ModelPickerMaster item = new ModelPickerMaster();
                                item.setId((detail.has("kode") ? detail.getString("kode") : detail.getString("id")));
                                item.setTitle(detail.getString("nama"));
                                item.setMultiple(isMultiple);

                                list.add(item);
                            }
                            listFiltered = list;
                            initRecyclerView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        helper.showToast(response.get("message"), 0);
                    }

                    return null;
                }
            });
        } catch (Exception e) {
            refreshLayout.setRefreshing(false);
            helper.showToast("Proses register gagal dilakukan, terjadi kesalahan pada sistem !", 0);
        }
    }


    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter     = new AdapterPickerMaster(PickerMasterActivity.this, list,
                new AdapterPickerMaster.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        List<ModelPickerMaster> item = adapter.getListData();
                        listFiltered    = item;
                        if (!isMultiple) {
                            for (int i = 0; i < item.size(); i++)
                                item.get(i).setChecked(false);
                            item.get(position).setChecked(true);
                        } else {
                            item.get(position).setChecked(!item.get(position).isChecked());
                        }
                        if (!item.get(position).isMultiple()) {
                            id = item.get(position).getId();
                            name = item.get(position).getTitle();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void close() {
        if (!id.isEmpty()) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("id", id);
            returnIntent.putExtra("name", name);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            helper.showToast("Please " + textTitle, 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnSubmit:
                if (isMultiple) {
                    listId.clear();
                    listName.clear();
                    for (int i = 0; i < listFiltered.size(); i++) {
                        if (listFiltered.get(i).isChecked()) {
                            listId.add(listFiltered.get(i).getId());
                            listName.add(listFiltered.get(i).getTitle());
                        }
                    }

                    id = TextUtils.join(",", listId);
                    name = TextUtils.join(", ", listName);
                }
                close();
                break;
        }
    }
}