package com.application.mgoaplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.application.mgoaplication.R;
import com.application.mgoaplication.api.Service;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileActivity extends MasterActivity implements View.OnClickListener {
    private AppCompatEditText name, email, phone, wa, identity, address;
    private FancyButton submit;
    private final int GET_IMAGE = 0;
    private int typeImage;
    private RelativeLayout imagePicker, imagePicker2;
    private ImageView iconImage, iconImage2;
    private TextView titleImage, titleImage2;
    private RoundedImageView selectImage, selectImage2;
    private List<Part> listImage = new ArrayList<Part>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findView();
        init();
        fetchData();
    }

    private void findView() {
        title       = findViewById(R.id.title);
        back        = findViewById(R.id.back);
        name        = findViewById(R.id.name);
        email       = findViewById(R.id.email);
        phone       = findViewById(R.id.phone);
        wa          = findViewById(R.id.wa);
        identity    = findViewById(R.id.identity);
        address     = findViewById(R.id.address);
        submit      = findViewById(R.id.btnSubmit);
        imagePicker = findViewById(R.id.imagePicker);
        imagePicker2 = findViewById(R.id.imagePicker2);
        titleImage = findViewById(R.id.titleImage);
        titleImage2 = findViewById(R.id.titleImage2);
        iconImage = findViewById(R.id.iconImage);
        iconImage2 = findViewById(R.id.iconImage2);
        selectImage = findViewById(R.id.selectImage);
        selectImage2 = findViewById(R.id.selectImage2);
    }

    private void init() {
        title.setText("Profile");
        back.setOnClickListener(this::onClick);
        submit.setOnClickListener(this::onClick);
        imagePicker.setOnClickListener(this::onClick);
        imagePicker2.setOnClickListener(this::onClick);
        listImage.add(null);
        listImage.add(null);
    }

    private void fetchData() {
        try {
            service.apiService(service.profile + helper.getSession("id"), null, null,
                    true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONObject detail = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                            helper.saveSessionBatch(detail);
                            setData();
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

    private void setData() {
        name.setText(helper.getSession("nama_member"));
        email.setText(helper.getSession("email"));
        phone.setText(helper.getSession("hp"));
        wa.setText(helper.getSession("wa"));
        identity.setText(helper.getSession("ktp"));
        address.setText(helper.getSession("alamat"));
        if (helper.getSession("foto") != null) {
            helper.fetchImage(selectImage.getRootView(), helper.getSession("foto"), selectImage);
            iconImage.setVisibility(View.GONE);
            titleImage.setVisibility(View.GONE);
        } if (helper.getSession("foto_kios") != null) {
            helper.fetchImage(selectImage2.getRootView(), helper.getSession("foto_kios"), selectImage2);
            iconImage2.setVisibility(View.GONE);
            titleImage2.setVisibility(View.GONE);
        }
    }

    private void updateProfile() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Masukan nama lengkap");
        } if (email.getText().toString().isEmpty()) {
            email.setError("Masukan alamat email");
        } if (phone.getText().toString().isEmpty()) {
            phone.setError("Masukan nomor telepon");
        } if (identity.getText().toString().isEmpty()) {
            identity.setError("Masukan nomor KTP");
        } if (address.getText().toString().isEmpty()) {
            address.setError("Masukan alamat rumah");
        } else {
            param.clear();
            param.put("id_member", helper.getSession("id"));
            param.put("nama", name.getText().toString());
            param.put("email", email.getText().toString());
            param.put("hp", phone.getText().toString());
            param.put("wa", wa.getText().toString());
            param.put("alamat", address.getText().toString());
            param.put("ktp", identity.getText().toString());
            service.apiService(service.updateProfile, param, listImage, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONObject response = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                            if (response.getString("status").equals("success")) {
//                                JSONObject data = response.getJSONObject("data");
//                                helper.saveSessionBatch(data);
                                fetchData();
                            }
                            helper.showToast(response.getString("notification"), 0);
                        } else {
                            helper.showToast(hashMap.get("message"), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.imagePicker:
                typeImage = 0;
                helper.openChooser(this, GET_IMAGE);
                break;
            case R.id.imagePicker2:
                typeImage = 1;
                helper.openChooser(this, GET_IMAGE);
                break;
            case R.id.btnSubmit:
                updateProfile();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
//            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            setImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(Image image) {
        FilePart partFile = null;
        if (typeImage == 0) {
            partFile = new FilePart("foto", new File(image.getPath()));
            selectImage.setImageURI(image.getUri());
            iconImage.setVisibility(View.GONE);
            titleImage.setVisibility(View.GONE);
        } else {
            partFile = new FilePart("fotoKios", new File(image.getPath()));
            selectImage2.setImageURI(image.getUri());
            iconImage2.setVisibility(View.GONE);
            titleImage2.setVisibility(View.GONE);
        }

        listImage.set(typeImage, partFile);
    }
}
