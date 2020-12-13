package com.application.mgoaplication.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.application.mgoaplication.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import mehdi.sakout.fancybuttons.FancyButton;

public class ScanActivity extends MasterActivity implements ZXingScannerView.ResultHandler,
        View.OnClickListener {
    private ZXingScannerView mScannerView;
    private ViewGroup contentFrame;
    private List<BarcodeFormat> listFormat;
    private boolean flashIsOn = false;
    private FancyButton btnFlash;
    private CameraManager mCameraManager;
    private String mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        findView();
        init();
        setData();
        setupFlashlight();
    }

    private void findView() {
        title           = findViewById(R.id.title);
        back            = findViewById(R.id.back);
        contentFrame    = findViewById(R.id.content_frame);
        btnFlash        = findViewById(R.id.btnFlash);
    }

    private void init() {
        title.setText("Scan QR Code Barang");
        title.setTextColor(ContextCompat.getColor(this, R.color.white));
        back.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        mScannerView    = new ZXingScannerView(this);
        listFormat      = new ArrayList<BarcodeFormat>();
        back.setOnClickListener(this::onClick);
        btnFlash.setOnClickListener(this::onClick);
    }

    private void setData() {
        listFormat.clear();
        listFormat.add(BarcodeFormat.UPC_A);
        listFormat.add(BarcodeFormat.UPC_E);
        listFormat.add(BarcodeFormat.EAN_13);
        listFormat.add(BarcodeFormat.EAN_8);
        listFormat.add(BarcodeFormat.RSS_14);
        listFormat.add(BarcodeFormat.CODE_39);
        listFormat.add(BarcodeFormat.CODE_39);
        listFormat.add(BarcodeFormat.CODE_93);
        listFormat.add(BarcodeFormat.CODE_128);
        listFormat.add(BarcodeFormat.ITF);
        listFormat.add(BarcodeFormat.CODABAR);
        listFormat.add(BarcodeFormat.QR_CODE);
        listFormat.add(BarcodeFormat.DATA_MATRIX);
        listFormat.add(BarcodeFormat.PDF_417);

        mScannerView.setFormats(listFormat);
        contentFrame.addView(mScannerView);
    }

    private void setupFlashlight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnFlash.setVisibility(View.VISIBLE);
            boolean isFlashAvailable = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if (!isFlashAvailable) {
                showNoFlashError();

                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    mCameraId = mCameraManager.getCameraIdList()[0];
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                btnFlash.setOnClickListener(this::onClick);
            }
        } else {
            btnFlash.setVisibility(View.GONE);
        }
    }

    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchFlashLight(boolean status) {
        try {
            mCameraManager.setTorchMode(mCameraId, status);
            flashIsOn   = !status;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnFlash:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    switchFlashLight(flashIsOn);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra("result", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }
}
