package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.application.mgoaplication.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class CheckoutResultActivity extends MasterActivity implements View.OnClickListener {
    private FancyButton chat, dashboard;
    private TextView subTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_result);

        findView();
        init();
    }

    private void findView() {
        chat        = findViewById(R.id.btnChat);
        dashboard   = findViewById(R.id.btnDashboard);
        subTotal    = findViewById(R.id.price);
    }

    private void init() {
        subTotal.setText("Rp. " + helper.formatCurrency(getIntent().getStringExtra("subTotalAmount")));
        chat.setOnClickListener(this::onClick);
        dashboard.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChat:
                helper.openOtherApps("com.whatsapp");
                break;
            case R.id.btnDashboard:
                helper.startIntent(DashboardActivity.class, false, null);
                finish();
                break;
        }
    }
}
