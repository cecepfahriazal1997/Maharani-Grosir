package com.application.mgoaplication.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.application.mgoaplication.R;
import com.application.mgoaplication.activity.CartActivity;
import com.application.mgoaplication.activity.DashboardActivity;
import com.application.mgoaplication.adapter.ListTransactionAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.ProductModel;
import com.application.mgoaplication.model.TransactionModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionChildFragment extends Fragment {
    private DashboardActivity parent;
    private Map<String, String> param = new HashMap<>();
    private ListTransactionAdapter adapter;
    private List<TransactionModel> list = new ArrayList<>();
    private EditText keyword;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    String type = "";

    public TransactionChildFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent      = (DashboardActivity) getActivity();
        if (getArguments() != null) {
            type    = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_transaction_child, container, false);

        findView(rootView);
        return rootView;
    }

    private void findView(View view) {
        keyword         = view.findViewById(R.id.keyword);
        recyclerView    = view.findViewById(R.id.list);
        refreshLayout   = view.findViewById(R.id.swipe_refresh_layout);

        keyword.setBackgroundResource(R.drawable.border_round_white);
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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        });
    }

    private List<ProductModel> initProduct(JSONArray listItem) {
        List<ProductModel> listProduct = new ArrayList<>();
        listProduct.clear();
        try {
            for (int i = 0; i < listItem.length(); i++) {
                JSONObject detail = listItem.getJSONObject(i);
                ProductModel param = new ProductModel();
                param.setId(detail.getString("id_barang"));
                param.setName(detail.getString("nama_barang"));
                param.setImage(detail.getString("gambar"));
                param.setDescription(detail.getString("deskripsi"));
                param.setListPrice(detail.getJSONArray("harga"));
                listProduct.add(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listProduct;
    }

    private void fetchData() {
        try {
            list.clear();
            String userId = parent.generalHelper.getSession("id");
            parent.apiService.apiService(parent.apiService.listTransaction + "id_member=" + userId + "&status=" + type
                    , null, null, false, "array", new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            refreshLayout.setRefreshing(false);
                            try {
                                if (hashMap.get("status").equals("true")) {
                                    JSONObject data = new JSONArray(hashMap.get("response")).getJSONObject(0);
                                    String total = data.getString("totalPesanan");
                                    if (!total.equalsIgnoreCase("0")) {
                                        JSONArray listItem = data.getJSONArray("dataPesanan");

                                        for (int i = 0; i < listItem.length(); i++) {
                                            JSONObject detail = listItem.getJSONObject(i);
                                            JSONArray listItemDetail = detail.getJSONArray("dataKeranjang");
                                            TransactionModel item = new TransactionModel();
                                            item.setOrderNumber(parent.generalHelper.md5(detail.getString("id_pesanan")));
                                            item.setId(detail.getString("id_pesanan"));
                                            item.setAmount(parent.generalHelper.formatCurrency(detail.getString("total")));
                                            item.setTotal(String.valueOf(listItemDetail.length()));
                                            item.setItem(initProduct(listItemDetail));
                                            item.setStatus(type);
                                            item.setDate(parent.generalHelper.convertDate(detail
                                                    .getString("tanggal"), "yyyy-MM-dd", "dd MMM yyyy"));
                                            list.add(item);
                                        }
                                    }
                                } else {
                                    parent.generalHelper.showToast(hashMap.get("message"), 1);
                                }
                                initRecyclerView();
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

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter     = new ListTransactionAdapter(getActivity(), list, parent.generalHelper,
                new ListTransactionAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(View view, int position) {
                        if (view.getId() == R.id.btnSubmit) {
                            TransactionModel item = list.get(position);
                            if (item.getStatus().equals("0") || item.getStatus().equals("1")) {
                                parent.generalHelper.openOtherApps("com.whatsapp");
                            } else {
                                buyAgain(item.getId());
                            }
                        }
                    }
                });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void buyAgain(String id) {
        try {
            param.clear();
            String userId = parent.generalHelper.getSession("id");
            param.put("id_member", userId);
            param.put("id_pesanan", id);
            parent.apiService.apiService(parent.apiService.buyAgain, param, null, true, "array",
                    new Service.hashMapListener() {
                        @Override
                        public String getHashMap(Map<String, String> hashMap) {
                            try {
                                JSONArray data = new JSONArray(hashMap.get("response"));
                                JSONObject detail = data.getJSONObject(0);
                                if (hashMap.get("status").equals("true")) {
                                    parent.generalHelper.showToast(detail.getString("message"), 1);
                                    if (detail.getString("status").equalsIgnoreCase("success")) {
                                        parent.generalHelper.startIntent(CartActivity.class, false, null);
                                    }
                                } else {
                                    parent.generalHelper.showToast(hashMap.get("message"), 1);
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

    public static TransactionChildFragment newInstance(String type) {
        TransactionChildFragment fragment = new TransactionChildFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }
}