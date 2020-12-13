package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.model.ProductModel;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;

public class SingleProductActivity extends MasterActivity implements View.OnClickListener {
    private FlexboxLayout listPicker;
    private ImageView image;
    private TextView name, description;
    private FancyButton btnSubmit;
    private ProductModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_scan);

        findView();
        init();
    }

    private void findView() {
        title       = findViewById(R.id.title);
        back        = findViewById(R.id.back);
        listPicker  = findViewById(R.id.listPicker);
        btnSubmit   = findViewById(R.id.btnSubmit);
        image       = findViewById(R.id.image);
        name        = findViewById(R.id.name);
        description = findViewById(R.id.description);
    }

    private void init() {
        data        = new Gson().fromJson(getIntent().getStringExtra("data"), ProductModel.class);
        title.setText(getIntent().getStringExtra("title"));
        name.setText(data.getName());
        description.setText(data.getDescription());
        helper.fetchImage(image.getRootView(), data.getImage(), image);
        back.setOnClickListener(this::onClick);
        btnSubmit.setOnClickListener(this::onClick);

        setListPicker();
    }

    private void setListPicker() {
        try {
            JSONArray listPrice = new JSONArray(getIntent().getStringExtra("listPrice").trim());
            for (int i = 0; i < listPrice.length(); i++) {
                JSONObject detail = listPrice.getJSONObject(i);
                View itemPick = helper.inflateView(R.layout.item_picker);
                LinearLayout parent = itemPick.findViewById(R.id.content);
                TextView title = itemPick.findViewById(R.id.title);
                title.setText(detail.getString("satuan"));
                parent.setPadding((int) helper.convertDpToPixel(4), (int) helper.convertDpToPixel(8), (int) helper.convertDpToPixel(8), 0);
                parent.setLayoutParams(new LinearLayout.LayoutParams((int) helper.convertDpToPixel(100), ViewGroup.LayoutParams.WRAP_CONTENT));
                listPicker.addView(itemPick);
            }
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
            case R.id.btnSubmit:
                helper.showToast("Barang berhasil dibeli", 0);
                finish();
                break;
        }
    }
}
