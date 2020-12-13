package com.application.mgoaplication.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.activity.DashboardActivity;
import com.application.mgoaplication.activity.ListProductActivity;
import com.application.mgoaplication.activity.SubCategoryActivity;
import com.application.mgoaplication.adapter.GridCategoryAdapter;
import com.application.mgoaplication.adapter.ListCategoryAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.CategoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {
    private ListCategoryAdapter adapter;
    private RecyclerView recyclerView;
    private List<CategoryModel> list = new ArrayList<>();
    private EditText keyword;
    private DashboardActivity parent;
    private Map<String, String> param = new HashMap<>();

    public CategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent          = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_category, container, false);

        findView(rootView);
        return rootView;
    }

    private void findView(View view) {
        recyclerView    = view.findViewById(R.id.list);
        keyword         = view.findViewById(R.id.keyword);

        init();
    }
    
    private void init() {
        keyword.setHint("Cari kategori disini ...");
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null && list != null && list.size() > 0) {
                    adapter.getFilter().filter(s);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setData();
    }

    private void setData() {
        try {
            list.clear();
            parent.apiService.apiService(parent.apiService.categoryProduct, null, null,
                    false, "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONArray data      = new JSONArray(hashMap.get("response").trim());
                                    JSONArray detail    = data.getJSONArray(0);

                                    for (int i = 0; i < detail.length(); i++) {
                                        JSONObject object   = detail.getJSONObject(i);
                                        CategoryModel param = new CategoryModel();
                                        param.setId(object.getString("id"));
                                        param.setTitle(object.getString("nama"));
                                        param.setImage(object.getString("iconFile"));
                                        param.setHaveCategory(!object.getString("subkategori").equals("0"));
                                        param.setTotal(String.valueOf(i));
                                        list.add(param);
                                    }
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                                    adapter     = new ListCategoryAdapter(getActivity(), list,
                                            new ListCategoryAdapter.OnClickListener() {
                                                @Override
                                                public void onClickListener(int position) {
                                                    param.clear();
                                                    if (list.get(position).isHaveCategory()) {
                                                        param.put("id", list.get(position).getId());
                                                        param.put("title", list.get(position).getTitle());
                                                        parent.generalHelper.startIntent(SubCategoryActivity.class, false, param);
                                                    } else {
                                                        param.put("type", "category");
                                                        param.put("groupId", list.get(position).getId());
                                                        param.put("title", list.get(position).getTitle());
                                                        parent.generalHelper.startIntent(ListProductActivity.class, false, param);
                                                    }
                                                }
                                            });
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    parent.generalHelper.showToast(hashMap.get("message"), 0);
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