package com.application.mgoaplication.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.application.mgoaplication.R;

public class SplashScreenActivity extends MasterActivity {
    private static int SPLASH_TIME_OUT  = 3000;
    private boolean isFirstInstall      = true;
    private Handler handler;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initial();
    }

    private void initial() {
        activity            = this;
        handler             = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                helper.saveSession("cart", "0");
                helper.saveSession("cartAmount", "0");
                helper.saveSession("notification", "0");
                helper.saveSession("point", "0");
                if (helper.getSession("id") != null) {
                    helper.startIntent(DashboardActivity.class, false, null);
                    finish();
                } else {
                    helper.startIntent(LoginActivity.class, false, null);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void onStop()
    {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    public void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
