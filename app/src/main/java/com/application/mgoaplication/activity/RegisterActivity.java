package com.application.mgoaplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

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

public class RegisterActivity extends MasterActivity implements View.OnClickListener {
    private final int GET_PROVINCE = 0;
    private final int GET_CITY = 1;
    private final int GET_DISTRICT = 2;
    private final int GET_SUBDISTRICT = 3;
    private final int GET_GROUP_MEMBER = 4;
    private final int GET_IMAGE = 5;
    private FancyButton login, submit;
    private AppCompatEditText name, email, phone, wa, identity, address;
    private TextView labelMember;
    private LinearLayout listSpinner;
    private RelativeLayout groupMember;
    private RelativeLayout imagePicker, imagePicker2;
    private ImageView iconImage, iconImage2;
    private TextView titleImage, titleImage2;
    private RoundedImageView selectImage, selectImage2;
    private AppCompatCheckBox phoneSameWA;
    private int typeImage = 0;
    private int spinnerChild[] = new int[4];
    private int textSpinner[] = new int[4];
    private String labelSpinner[] = {
        "Provinsi Papua",
        "Kab. Mimika",
        "Pilih Kecamatan",
        "Pilih Kelurahan",
    };
    private List<Part> listImage = new ArrayList<Part>();
    private String typeId="0", provinceId="91", cityId="91.09", districtId="", subDistrictId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findView();
        init();
    }

    private void findView() {
        title       = findViewById(R.id.title);
        back        = findViewById(R.id.back);
        login       = findViewById(R.id.btnLogin);
        submit      = findViewById(R.id.btnSubmit);
        listSpinner = findViewById(R.id.list_spinner);
        groupMember = findViewById(R.id.groupMember);
        name        = findViewById(R.id.name);
        email       = findViewById(R.id.email);
        phone       = findViewById(R.id.phone);
        wa          = findViewById(R.id.wa);
        identity    = findViewById(R.id.identity);
        address     = findViewById(R.id.address);
        labelMember = findViewById(R.id.type);
        imagePicker = findViewById(R.id.imagePicker);
        imagePicker2 = findViewById(R.id.imagePicker2);
        titleImage = findViewById(R.id.titleImage);
        titleImage2 = findViewById(R.id.titleImage2);
        iconImage = findViewById(R.id.iconImage);
        iconImage2 = findViewById(R.id.iconImage2);
        selectImage = findViewById(R.id.selectImage);
        selectImage2 = findViewById(R.id.selectImage2);
        phoneSameWA = findViewById(R.id.phoneSameWA);
    }

    private void init() {
        title.setText("Pendaftaran");
        title.setTextSize((int) helper.convertDpToPixel(16));

        login.setOnClickListener(this);
        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        groupMember.setOnClickListener(this);
        imagePicker.setOnClickListener(this);
        imagePicker2.setOnClickListener(this);
        imagePicker.setOnClickListener(this);
        imagePicker2.setOnClickListener(this);
        listImage.add(null);
        listImage.add(null);
        phoneSameWA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wa.setText(phone.getText().toString());
                    wa.setEnabled(false);
                } else {
                    wa.setText(null);
                }
            }
        });

        setupSpinner();
    }

    private void setupSpinner() {
        for (int i = 0; i < labelSpinner.length; i++) {
            initListSpinner(i);
            setTextSpinner(i, labelSpinner[i], (i < 2 ? true : false));
        }
    }

    private void initListSpinner(int pos) {
        View view = helper.inflateView(R.layout.custom_spinner);
        RelativeLayout parent = view.findViewById(R.id.parent);
        RelativeLayout content = view.findViewById(R.id.content);
        TextView title  = view.findViewById(R.id.title);

        spinnerChild[pos]   = View.generateViewId();
        textSpinner[pos]    = View.generateViewId();

        title.setId(textSpinner[pos]);
        content.setId(spinnerChild[pos]);
        content.setOnClickListener(this::onClick);
        parent.setPadding(0,(int) helper.convertDpToPixel(8), 0, 0);
        listSpinner.addView(view);
    }

    private void setTextSpinner(int pos, String title, boolean isSelected) {
        int black = ContextCompat.getColor(this, R.color.black);
        int mediumBlack = ContextCompat.getColor(this, R.color.mediumBlack);
        View nextChild = ((ViewGroup) listSpinner).getChildAt(pos);
        ((TextView)nextChild.findViewById(textSpinner[pos])).setText(title);
        if (isSelected)
            ((TextView)nextChild.findViewById(textSpinner[pos])).setTextColor(black);
        else
            ((TextView)nextChild.findViewById(textSpinner[pos])).setTextColor(mediumBlack);
    }

    private void saveData() {
        param.clear();
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
            param.put("idKelompok", typeId);
            param.put("nama", name.getText().toString());
            param.put("email", email.getText().toString());
            param.put("hp", phone.getText().toString());
            param.put("wa", wa.getText().toString());
            param.put("ktp", identity.getText().toString());
            param.put("alamat", address.getText().toString());
            param.put("idPropinsi", provinceId);
            param.put("idKota", provinceId);
            param.put("idKota", cityId);
            param.put("idKecamatan", districtId);
            param.put("idKelurahan", subDistrictId);
            service.apiService(service.register, param, listImage, true, "array", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        if (hashMap.get("status").equals("true")) {
                            JSONArray response = new JSONArray(hashMap.get("response"));
                            JSONObject detail = response.getJSONObject(0);
                            if (detail.getString("status").equals("failed")) {
                                helper.showToast(detail.getString("notification"), 0);
                            } else {
                                JSONObject profile = response.getJSONObject(0).getJSONObject("data");
                                param.clear();
                                param.put("type", "add");
                                param.put("user", profile.toString());
                                helper.startIntent(PINActivity.class, true, param);
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
        }
    }

    @Override
    public void onClick(View v) {
        param.clear();
        param.put("multiple", "0");
        if (v.getId() == spinnerChild[0]) {
            param.put("url", service.province);
            param.put("title", "Pilih Provinsi");
            param.put("id", provinceId);
            helper.startIntentForResult(PickerMasterActivity.class, param, GET_PROVINCE);
        } else if (v.getId() == spinnerChild[1]) {
            param.put("url", service.city + provinceId);
            param.put("title", "Pilih Kota");
            param.put("id", cityId);
            helper.startIntentForResult(PickerMasterActivity.class, param, GET_CITY);
        } else if (v.getId() == spinnerChild[2]) {
            param.put("url", service.district + cityId);
            param.put("title", "Pilih Kecamatan");
            param.put("id", districtId);
            helper.startIntentForResult(PickerMasterActivity.class, param, GET_DISTRICT);
        } else if (v.getId() == spinnerChild[3]) {
            param.put("url", service.subDistrict + districtId);
            param.put("title", "Pilih Kelurahan");
            param.put("id", subDistrictId);
            helper.startIntentForResult(PickerMasterActivity.class, param, GET_SUBDISTRICT);
        } else if (v.getId() == R.id.groupMember) {
            param.put("url", service.groupMember);
            param.put("title", "Pilih Kelompok Member");
            helper.startIntentForResult(PickerMasterActivity.class, param, GET_GROUP_MEMBER);
        } else if (v.getId() == R.id.btnLogin) {
            finish();
        } else if (v.getId() == R.id.btnSubmit) {
            saveData();
        } else if (v.getId() == R.id.imagePicker) {
            typeImage = 0;
            helper.openChooser(this, GET_IMAGE);
        } else if (v.getId() == R.id.imagePicker2) {
            typeImage = 1;
            helper.openChooser(this, GET_IMAGE);
        } else if (v.getId() == R.id.back) {
            finish();
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
        if(resultCode== Activity.RESULT_OK && data!=null) {
            switch (requestCode) {
                case GET_PROVINCE:
                    provinceId    = data.getStringExtra("id");
                    setTextSpinner(GET_PROVINCE, "Provinsi " + data.getStringExtra("name"), true);
                    break;
                case GET_CITY:
                    cityId    = data.getStringExtra("id");
                    setTextSpinner(GET_CITY, "Kota " + data.getStringExtra("name"), true);
                    break;
                case GET_DISTRICT:
                    districtId    = data.getStringExtra("id");
                    setTextSpinner(GET_DISTRICT, "Kecamatan " + data.getStringExtra("name"), true);
                    break;
                case GET_SUBDISTRICT:
                    subDistrictId    = data.getStringExtra("id");
                    setTextSpinner(GET_SUBDISTRICT, "Kelurahan " + data.getStringExtra("name"), true);
                    break;
                case GET_GROUP_MEMBER:
                    typeId    = data.getStringExtra("id");
                    labelMember.setText(data.getStringExtra("name"));
                    break;
            }
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
