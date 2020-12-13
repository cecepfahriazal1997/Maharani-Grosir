package com.application.mgoaplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.ProductAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.helper.GeneralHelper;
import com.application.mgoaplication.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterActivity extends AppCompatActivity {
    protected GeneralHelper helper;
    protected Service service;
    protected ProgressDialog pDialog;
    protected Map<String, String> param = new HashMap<>();
    protected ImageButton back;
    protected TextView title;
    public TextView badgeCart, badgeNotification;
    public View popupCart;
    public ViewGroup parent;
    public TextView amount, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(this);
        helper  = new GeneralHelper(this);
        service = new Service(this, pDialog);
        helper.setupProgressDialog(pDialog, "Loading data ...");
    }

    // Function global for product
    public void initViewPopUpCart() {
        popupCart               = findViewById(R.id.popupCart);
        amount                  = findViewById(R.id.amount);
        price                   = findViewById(R.id.price);
        parent                  = findViewById(R.id.parent);
    }

    public void initRecyclerView(RecyclerView recyclerView, int orientation, List<ProductModel> listProduct,
                                  Context context, ProductAdapter productAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, orientation, false);
        productAdapter     = new ProductAdapter(context, listProduct, helper,
                service, new ProductAdapter.OnClickListener() {
            @Override
            public void onClickListener(View view, int position, Map<String, Object> paramAmount) {
                switch (view.getId()) {
                    case R.id.btnSubmit:
                        if (listProduct.get(position).getListPrice() != null
                                && listProduct.get(position).getListPrice().length() > 0) {
                            Map<String, Object> detailAmmount = (Map<String, Object>)
                                    paramAmount.get("amount_" + listProduct.get(position).getId());
                            if (detailAmmount != null) {
                                buyItem(listProduct.get(position).getId(), detailAmmount);
                            } else {
                                helper.showToast("Silahkan pilih total barang yang akan dibeli!", 0);
                            }
                        } else {
                            helper.showToast("Silahkan pilih total barang yang akan dibeli!", 0);
                        }
                        break;
                }
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    public void initBadge(int TYPE) {
        if (TYPE == 0) {
            if (helper.getSession("cart") != null
                    && !helper.getSession("cart").equals("0")) {
                badgeCart.setVisibility(View.VISIBLE);
                badgeCart.setText(helper.getSession("cart"));
            } else {
                badgeCart.setVisibility(View.GONE);
            }
        } else {
            if (helper.getSession("notification") != null
                    && !helper.getSession("notification").equals("0")) {
                badgeNotification.setVisibility(View.VISIBLE);
                badgeNotification.setText(helper.getSession("notification"));
            } else {
                badgeNotification.setVisibility(View.GONE);
            }
        }
    }

    public void buyItem(String id, Map<String, Object> detailAmmount) {
        try {
            int total = 0;
            int totalAmount = 0;
            param.clear();
            for (Map.Entry<String, Object> data : detailAmmount.entrySet()) {
                String key = data.getKey();
                String value = data.getValue().toString();
                if (Integer.parseInt(value) > 0) {
                    if (key.contains("satuan")) {
                        total += Integer.parseInt(value);
                        param.put(key, value);
                    } if (key.contains("price")) {
                        totalAmount += Integer.parseInt(value);
                    }
                }
            }
            param.put("id_member", helper.getSession("id"));
            param.put("id_barang", id);
            int curTotalAmount = 0;
            if (helper.getSession("cartAmount") != null)
                curTotalAmount    = Integer.parseInt(helper.getSession("cartAmount"));
            int finalTotal = total;
            int finalTotalAmount = totalAmount + curTotalAmount;
            service.apiService(service.addToCart, param, null, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray data = new JSONArray(hashMap.get("response").trim());
                            JSONObject detail = data.getJSONObject(0);
                            if (detail.getString("status").equals("success")) {
                                helper.showToast("Produk berhasil ditambahkan ke keranjang!", 0);
                                int hasOrder = helper.getSession("cart") != null ? Integer.parseInt(helper.getSession("cart")) : 0;
                                helper.saveSession("cart", String.valueOf(hasOrder + finalTotal));
                                helper.saveSession("cartAmount", String.valueOf(finalTotalAmount));
                                helper.showPopUpCart(parent, popupCart, amount, price);
                                if (helper.getSession("showBadge").equals("true"))
                                    initBadge(0);
                            } else {
                                helper.showToast("Produk gagal ditambahkan ke keranjang!", 0);
                            }
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
}