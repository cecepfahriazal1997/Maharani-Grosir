package com.application.mgoaplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.CheckoutAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProductModel;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class CheckoutActivity extends MasterActivity implements View.OnClickListener {
    private final int CHOOSE_COUPON = 0;
    private final int CHOOSE_PAYMENT = 1;
    private RecyclerView recyclerView;
    private CheckoutAdapter adapter;
    private List<ProductModel> list = new ArrayList<>();
    private FancyButton buy, btnAddress;
    private RelativeLayout chooseCoupon, chooseBank;
    private TextView subTotal, subTotalItem, currBank, currVoucher, address;
    private String typePayment="", idVoucher="", subTotalAmount="0";
    private boolean onPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        findView();
        init();
        setData();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        buy             = findViewById(R.id.btnSubmit);
        recyclerView    = findViewById(R.id.list);
        chooseCoupon    = findViewById(R.id.chooseCoupon);
        chooseBank      = findViewById(R.id.chooseBank);
        subTotal        = findViewById(R.id.price);
        subTotalItem    = findViewById(R.id.subTotalItem);
        currBank        = findViewById(R.id.currBank);
        currVoucher     = findViewById(R.id.currVoucher);
        btnAddress      = findViewById(R.id.btnAddress);
        address         = findViewById(R.id.address);
    }

    private void init() {
        title.setText("Checkout");
        chooseBank.setVisibility(View.VISIBLE);
        back.setOnClickListener(this::onClick);
        buy.setOnClickListener(this::onClick);
        chooseCoupon.setOnClickListener(this::onClick);
        chooseBank.setOnClickListener(this::onClick);
        btnAddress.setOnClickListener(this::onClick);
        typePayment = getIntent().getStringExtra("type_payment");
        if (!getIntent().getStringExtra("voucher").equals("0")) {
            idVoucher   = getIntent().getStringExtra("id_voucher");
            currVoucher.setText("Diskon " + helper.formatCurrency(getIntent().getStringExtra("voucher")));
        } else {
            currVoucher.setText(null);
        }
        currBank.setText(getIntent().getStringExtra("payment"));
    }

    private void setData() {
        list.clear();
        try {
            service.apiService(service.listCart + helper.getSession("id"), null,
                    null, true, "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data = new JSONArray(hashMap.get("response"));
                                    JSONObject detail = data.getJSONObject(0);
                                    String totalAll = detail.getString("totalKeranjang");
                                    String subTotalPrice = detail.getString("subtotal");
                                    JSONArray listItem = detail.getJSONArray("dataKeranjang");

                                    subTotal.setText("Rp. " + helper.formatCurrency(subTotalPrice));
                                    subTotalItem.setText("Rp. " + helper.formatCurrency(subTotalPrice));
                                    subTotalAmount  = subTotalPrice;

                                    for (int i = 0; i < listItem.length(); i++) {
                                        JSONObject detailItem = listItem.getJSONObject(i);
                                        ProductModel item = new ProductModel();

                                        item.setId(detailItem.getString("id_barang"));
                                        item.setName(detailItem.getString("nama_barang"));
                                        item.setDescription(detailItem.getString("deskripsi"));
                                        item.setImage(detailItem.getString("gambar"));
                                        item.setListPrice(detailItem.getJSONArray("harga"));

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

    private void checkout() {
        try {
            Date currDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            param.clear();
            param.put("id_member", helper.getSession("id"));
            param.put("tanggal", df.format(currDate));
            param.put("metode", typePayment);
            param.put("id_voucher", idVoucher);
            service.apiService(service.checkout, param, null, true,
                    "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                JSONArray data = new JSONArray(hashMap.get("response"));
                                JSONObject detail = data.getJSONObject(0);
                                if (hashMap.get("status").equals("true")) {
                                    helper.showToast(detail.getString("message"), 1);
                                    if (detail.getString("status").equalsIgnoreCase("success")) {
                                        param.clear();
                                        param.put("subTotalAmount", subTotalAmount);
                                        helper.startIntent(CheckoutResultActivity.class, false, param);
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

    private void initRecyclerView(RecyclerView recyclerView, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, orientation, false);
        adapter     = new CheckoutAdapter(this, list, helper,
                new CheckoutAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
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
            case R.id.chooseCoupon:
                helper.startIntentForResult(CouponActivity.class, null, CHOOSE_COUPON);
                break;
            case R.id.chooseBank:
                helper.startIntentForResult(PaymentActivity.class, null, CHOOSE_PAYMENT);
                break;
            case R.id.btnAddress:
                helper.startIntent(ProfileActivity.class, false, null);
                break;
            case R.id.btnSubmit:
                checkout();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && data!=null) {
            switch (requestCode) {
                case CHOOSE_COUPON:
                    idVoucher = data.getStringExtra("value");
                    currVoucher.setText("Diskon " + helper.formatCurrency(data.getStringExtra("title")));
                    double result = Double.parseDouble(subTotalAmount) - Double.parseDouble(data.getStringExtra("title"));
                    subTotalItem.setText("Rp. " + helper.formatCurrency(String.valueOf(result)));
                    subTotal.setText("Rp. " + helper.formatCurrency(String.valueOf(result)));
                    break;
                case CHOOSE_PAYMENT:
                    typePayment = data.getStringExtra("value");
                    currBank.setText(data.getStringExtra("title"));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        address.setText(helper.getSession("address"));
    }
}
