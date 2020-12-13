package com.application.mgoaplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.PaymentAdapter;
import com.application.mgoaplication.model.PaymentModel;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class PaymentActivity extends MasterActivity implements View.OnClickListener {
    private PaymentAdapter adapter;
    private List<PaymentModel> list = new ArrayList<>();
    private FancyButton btnSubmit;
    private RecyclerView recyclerView;
    private String name[] = {"Transfer Bank", "Bayar ke Toko", "Bayar di Rumah"};
    private int image[] = {R.drawable.mobile_banking, R.drawable.shop, R.drawable.home};
    private String description[] = {
            "Melakukan pembayaran melalui transfer ke bank, bisa menggunakan e-banking, m-banking atau ke atm terdekat.",
            "Melakukan pembayaran dengan uang tunai dan datang ke toko jika sedang tidak memiliki saldo di atm mu.",
            "Melakukan pembayaran dengan uang tunai tanpa ribet dan barang akan di antar ke rumah mu."
    };
    private String typePayment[] = {"2", "0", "1"};
    private int curPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findView();
        init();
        setData();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        recyclerView    = findViewById(R.id.list);
        btnSubmit       = findViewById(R.id.btnSubmit);
    }

    private void init() {
        title.setText("Metode Pembayaran");
        back.setOnClickListener(this::onClick);
        btnSubmit.setOnClickListener(this::onClick);
    }

    private void setData() {
        list.clear();

        for (int i = 0; i < name.length; i++) {
            PaymentModel item = new PaymentModel();
            item.setId(typePayment[i]);
            item.setImage(String.valueOf(image[i]));
            item.setName(name[i]);
            item.setDescription(description[i]);
            item.setType(typePayment[i]);
            item.setChecked((getIntent().getStringExtra("id").equals(typePayment[i])));
            list.add(item);
        }

        initRecyclerView(recyclerView, RecyclerView.VERTICAL);
    }

    private void initRecyclerView(RecyclerView recyclerView, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, orientation, false);
        adapter     = new PaymentAdapter(this, list, helper,
                new PaymentAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        for (int i = 0; i < list.size(); i++)
                            list.get(i).setChecked(false);
                        list.get(position).setChecked(true);
                        curPosition = position;
                        adapter.notifyDataSetChanged();
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
            case R.id.btnSubmit:
                helper.showToast("Pembayaran berhasil dipilih !", 0);
                Intent intent = new Intent();
                intent.putExtra("title", list.get(curPosition).getName());
                intent.putExtra("value", list.get(curPosition).getType());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
