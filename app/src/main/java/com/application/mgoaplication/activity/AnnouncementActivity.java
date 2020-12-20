package com.application.mgoaplication.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.AnnouncementAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.AnnouncementModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class AnnouncementActivity extends MasterActivity implements View.OnClickListener {
    private AnnouncementAdapter adapter;
    private List<AnnouncementModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private Dialog builder;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anncouncement);

        findView();
        init();
        setData();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        recyclerView    = findViewById(R.id.list);
    }

    private void init() {
        title.setText("Pengumuman");
        back.setOnClickListener(this::onClick);
        userId  = helper.getSession("id");
    }

    private void setData() {
        try {
            list.clear();
            service.apiService(service.notification + userId, null,
                    null, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray data = new JSONArray(hashMap.get("response").trim()).getJSONArray(0);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject detail = data.getJSONObject(i);
                                AnnouncementModel item = new AnnouncementModel();
                                item.setId(detail.getString("id"));
                                item.setImage(detail.getString("gambarFile"));
                                item.setTitle(detail.getString("judul"));
                                item.setDescription(detail.getString("isi"));
                                item.setStatus(detail.getString("baca"));
                                item.setDate(detail.getString("created_at"));
                                list.add(item);
                            }
                        } else {
                            helper.showToast(hashMap.get("message"), 0);
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

    private void detailAnnouncement(String id) {
        try {
            String url = service.notificationDetail + "id_member=" + userId + "&id_pengumuman=" + id;
            service.apiService(url, null, null, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONObject detail = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                            detailPopup(detail);
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

    private void detailPopup(JSONObject data) {
        try {
            builder = new Dialog(this);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //nothing;
                }
            });

            final View customView = getLayoutInflater().inflate(R.layout.popup_detail_announcement, null);
            ImageView imageView     = customView.findViewById(R.id.image);
            TextView title          = customView.findViewById(R.id.title);
            TextView description    = customView.findViewById(R.id.description);
            TextView date           = customView.findViewById(R.id.date);
            FancyButton close       = customView.findViewById(R.id.close);

            helper.fetchImage(imageView.getRootView(), data.getString("gambarFile"), imageView);
            title.setText(data.getString("judul"));
            date.setText(data.getString("created_at"));
            helper.setTextHtml(description, data.getString("isi"));
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });

            builder.setContentView(customView);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(RecyclerView recyclerView, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, orientation, false);
        adapter     = new AnnouncementAdapter(this, list, helper,
                new AnnouncementAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        detailAnnouncement(list.get(position).getId());
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
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
