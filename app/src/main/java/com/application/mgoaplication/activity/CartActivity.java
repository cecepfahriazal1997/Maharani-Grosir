package com.application.mgoaplication.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.CartAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class CartActivity extends MasterActivity implements View.OnClickListener {
    private final int CHOOSE_COUPON = 0;
    private final int CHOOSE_PAYMENT = 1;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<ProductModel> list = new ArrayList<>();
    private FancyButton buy, update;
    private RelativeLayout chooseCoupon, chooseBank;
    private boolean isEdit = false;
    private String typePayment="0", idVoucher="", subTotalAmount="0", nameVoucher="", nameBank="Bayar ke Toko";
    private TextView subTotal, subTotalItem, currBank, currVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        update          = findViewById(R.id.btnUpdate);
        subTotal        = findViewById(R.id.price);
        subTotalItem    = findViewById(R.id.subTotalItem);
        currBank        = findViewById(R.id.currBank);
        currVoucher     = findViewById(R.id.currVoucher);
    }

    private void init() {
        title.setText("Keranjang Saya");
        back.setOnClickListener(this::onClick);
        buy.setOnClickListener(this::onClick);
        chooseBank.setOnClickListener(this::onClick);
        update.setOnClickListener(this::onClick);
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
                                    idVoucher       = detail.getString("idVoucher");
                                    nameVoucher     = detail.getString("potongan");
                                    if (!idVoucher.isEmpty()) {
                                        currVoucher.setText("Diskon " + helper.formatCurrency(nameVoucher));
                                        chooseCoupon.setVisibility(View.VISIBLE);
                                    } else {
                                        currVoucher.setText(null);
                                        chooseCoupon.setVisibility(View.GONE);
                                    }
                                    if (!detail.isNull("totalKeranjang")) {
                                        String totalAll = detail.getString("totalKeranjang");
                                        String subTotalPrice = detail.getString("subtotal");
                                        JSONArray listItem = detail.getJSONArray("dataKeranjang");

                                        currBank.setText(nameBank);
                                        subTotal.setText("Rp. " + helper.formatCurrency(subTotalPrice));
                                        helper.saveSession("cart", totalAll);
                                        helper.saveSession("cartAmount", subTotalPrice);

                                        for (int i = 0; i < listItem.length(); i++) {
                                            JSONObject detailItem = listItem.getJSONObject(i);
                                            ProductModel item = new ProductModel();

                                            item.setId(detailItem.getString("id_pesanan"));
                                            item.setProductId(detailItem.getString("id_barang"));
                                            item.setName(detailItem.getString("nama_barang"));
                                            item.setDescription(detailItem.getString("deskripsi"));
                                            item.setImage(detailItem.getString("gambar"));
                                            item.setListPrice(detailItem.getJSONArray("harga"));

                                            list.add(item);
                                        }
                                    } else {
                                        subTotal.setText("Rp. 0");
                                        helper.saveSession("cart", "0");
                                        helper.saveSession("cartAmount", "0");
                                    }
                                } else {
                                    helper.showToast(hashMap.get("message"), 1);
                                }
                                initRecyclerView(recyclerView, RecyclerView.VERTICAL);
                                if (list.size() > 0) {
                                    update.setVisibility(View.VISIBLE);
                                } else {
                                    update.setVisibility(View.GONE);
                                    update.setText("Edit Keranjang");
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
        adapter     = new CartAdapter(this, list, helper, isEdit,
                new CartAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        if (view.getId() == R.id.btnDelete) {
                            confirm(list.get(position).getId(), list.get(position).getProductId());
                        } else if (view.getId() == R.id.btnUpdate) {
                            updateCart(position);
                        }
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void confirm(String id, String productId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteCart(id, productId);
                        return;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        helper.popupConfirm("Apakah kamu sudah yakin ?", "Barang dikeranjang akan dihapus!", dialogClickListener);
    }

    private void deleteCart(String id, String productId) {
        try {
            service.apiService(service.deleteCart + "id_pesanan=" + id + "&id_barang=" + productId,
                    null, null, true, "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONObject response = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                                    helper.showToast(response.getString("message"), 1);
                                    if (response.getString("status").equalsIgnoreCase("success")) {
                                        setData();
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

    private void updateCart(int position) {
        String userId = helper.getSession("id");
        param.clear();
        param.put("id_pesanan", list.get(position).getId());
        param.put("id_member", userId);
        param.put("id_barang", list.get(position).getProductId());
        try {
            JSONArray listPrice = list.get(position).getListPrice();
            for (int x = 0; x < listPrice.length(); x++) {
                JSONObject detailPrice = listPrice.getJSONObject(x);
                String key = "satuan" + detailPrice.getString("id_satuan");
                String value = detailPrice.getString("jumlah");
                param.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        postUpdateCart(param);
    }

    private void postUpdateCart(Map<String, String> params) {
        try {
            service.apiService(service.updateCart, params, null, true, "array",
                    new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONObject response = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                                    if (response.getString("status").equalsIgnoreCase("success")) {
                                        helper.showToast("Keranjang berhasil diubah!", 1);
                                        setData();
                                        update.callOnClick();
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
            case R.id.chooseCoupon:
                param.clear();
                param.put("id", idVoucher);
                helper.startIntentForResult(CouponActivity.class, param, CHOOSE_COUPON);
                break;
            case R.id.chooseBank:
                param.clear();
                param.put("id", typePayment);
                helper.startIntentForResult(PaymentActivity.class, param, CHOOSE_PAYMENT);
                break;
            case R.id.btnSubmit:
                if (list.size() > 0) {
                    param.clear();
                    param.put("id_voucher", idVoucher);
                    param.put("type_payment", typePayment);
                    param.put("voucher", nameVoucher);
                    param.put("payment", nameBank);
                    helper.startIntent(CheckoutActivity.class, false, param);
                } else {
                    helper.showToast("Tidak ada barang yang anda pesan!", 0);
                }
                break;
            case R.id.btnUpdate:
                isEdit = !isEdit;
                if (isEdit) {
                    update.setText("Batal Edit Keranjang");
                } else {
                    update.setText("Edit Keranjang");
                }
                initRecyclerView(recyclerView, RecyclerView.VERTICAL);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && data!=null) {
            switch (requestCode) {
                case CHOOSE_COUPON:
                    idVoucher   = data.getStringExtra("value");
                    nameVoucher = data.getStringExtra("title");
                    currVoucher.setText("Diskon " + helper.formatCurrency(nameVoucher));
                    double result = Double.parseDouble(subTotalAmount) - Double.parseDouble(nameVoucher);
                    subTotalItem.setText("Rp. " + helper.formatCurrency(String.valueOf(result)));
                    subTotal.setText("Rp. " + helper.formatCurrency(String.valueOf(result)));
                    break;
                case CHOOSE_PAYMENT:
                    typePayment = data.getStringExtra("value");
                    nameBank    = data.getStringExtra("title");
                    currBank.setText(nameBank);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
