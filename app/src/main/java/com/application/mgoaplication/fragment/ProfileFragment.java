package com.application.mgoaplication.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.activity.CouponActivity;
import com.application.mgoaplication.activity.DashboardActivity;
import com.application.mgoaplication.activity.LoginActivity;
import com.application.mgoaplication.activity.PrizeActivity;
import com.application.mgoaplication.activity.ProfileActivity;
import com.application.mgoaplication.adapter.ProfileAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProfileModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private DashboardActivity parent;
    private TextView name, email, phone;
    private ImageView avatar;
    private ProfileAdapter adapter;
    private List<ProfileModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private Map<String, String> param = new HashMap<>();
    private boolean isPause = false;
    private int image[] = {
            R.drawable.profile,
//            R.drawable.coupon,
            R.drawable.coin,
//            R.drawable.love_2,
//            R.drawable.time,
            R.drawable.whatsapp,
            R.drawable.logout
    };

    private String title[] = {
            "Profile",
//            "Voucher Saya",
            "Poin Saya",
//            "Favorit Saya",
//            "Terakhir Dilihat",
            "Chat Admin via Whatsapp",
            "Logout"
    };

    private int colorImage[] = {
            R.color.black,
//            R.color.black,
            R.color.black,
//            R.color.black,
//            R.color.black,
            R.color.black,
            R.color.colorPrimary
    };

    private int colorSubtitle[] = {
            R.color.black,
//            R.color.black,
            R.color.gold,
//            R.color.black,
//            R.color.black,
            R.color.black,
            R.color.black
    };

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent      = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_profile, container, false);

        findView(rootView);
        init();
        initList();
        return rootView;
    }

    private void findView(View view) {
        recyclerView        = view.findViewById(R.id.list);
        avatar              = view.findViewById(R.id.image);
        name                = view.findViewById(R.id.name);
        email               = view.findViewById(R.id.email);
        phone               = view.findViewById(R.id.phone);
    }

    private void init() {
        parent.generalHelper.fetchImage(avatar.getRootView(), parent.generalHelper.getSession("foto"), avatar);
        name.setText(parent.generalHelper.getSession("nama_member"));
        email.setText(parent.generalHelper.getSession("email"));
        phone.setText(parent.generalHelper.getSession("hp"));
    }

    private void initList() {
        list.clear();
        for (int i = 0; i < title.length; i++) {
            ProfileModel item = new ProfileModel();
            String subtitle = "";
            if (i == 1) {
                if (parent.generalHelper.getSession("point") != null) {
                    subtitle    = parent.generalHelper.getSession("point") + " poin";
                }
            }

            item.setId(String.valueOf(i));
            item.setImage(image[i]);
            item.setTitle(title[i]);
            item.setSubtitle(subtitle);
            item.setColorImage(colorImage[i]);
            item.setColorTitle(colorImage[i]);
            item.setColorSubtitle(colorSubtitle[i]);

            list.add(item);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter     = new ProfileAdapter(getActivity(), list,
                new ProfileAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        switch (position) {
                            case 0:
                                parent.generalHelper.startIntent(ProfileActivity.class, false, null);
                                break;
                            case 1:
                                parent.generalHelper.startIntent(PrizeActivity.class, false, null);
                                break;
                            case 2:
                                parent.generalHelper.openOtherApps("com.whatsapp");
                                break;
                            case 3:
                                logout();
                                break;
                        }
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void logout() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        param.clear();
                        param.put("idMember", parent.generalHelper.getSession("id"));
                        parent.apiService.apiService(parent.apiService.logout, param, null,
                                true, "array", new Service.hashMapListener() {
                                    @Override
                                    public String getHashMap(Map<String, String> hashMap) {
                                        try {
                                            if (hashMap.get("status").equals("true")) {
                                                JSONArray response = new JSONArray(hashMap.get("response").trim());
                                                JSONObject detail = response.getJSONObject(0);
                                                if (detail.getString("status").equals("success")) {
                                                    parent.generalHelper.clearSession();
                                                    parent.generalHelper.startIntent(LoginActivity.class, true, null);
                                                } else {
                                                    parent.generalHelper.showToast("Proses logout gagal dilakukan!", 0);
                                                }
                                            } else {
                                                parent.generalHelper.showToast(hashMap.get("message"), 0);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                });
                        return;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        parent.generalHelper.popupConfirm("Apakah kamu yakin akan logout ?", "Session akan terhapus otomatis dan kamu harus melakukan login kembali.", dialogClickListener);
    }

    public void onPause() {
        super.onPause();
        isPause = true;
    }

    public void onResume() {
        super.onResume();
        if (isPause) {
            parent.generalHelper.fetchImage(avatar.getRootView(), parent.generalHelper.getSession("foto"), avatar);
        }
    }
}