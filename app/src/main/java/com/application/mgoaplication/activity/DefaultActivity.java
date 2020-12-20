package com.application.mgoaplication.activity;

import android.os.Bundle;

import com.application.mgoaplication.R;

public class DefaultActivity extends MasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        findView();
    }

    private void findView() {
        title   = findViewById(R.id.title);
        back    = findViewById(R.id.back);
    }

    private void init() {}

    private void setData() {}
}
