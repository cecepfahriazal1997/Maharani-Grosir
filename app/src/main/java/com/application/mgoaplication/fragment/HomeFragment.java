package com.application.mgoaplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mgoaplication.R;
import com.application.mgoaplication.activity.AllProductActivity;
import com.application.mgoaplication.activity.AnnouncementActivity;
import com.application.mgoaplication.activity.CartActivity;
import com.application.mgoaplication.activity.DashboardActivity;
import com.application.mgoaplication.activity.ListProductActivity;
import com.application.mgoaplication.activity.ScanActivity;
import com.application.mgoaplication.activity.SingleProductActivity;
import com.application.mgoaplication.activity.SubCategoryActivity;
import com.application.mgoaplication.adapter.BannerAdapter;
import com.application.mgoaplication.adapter.GridCategoryAdapter;
import com.application.mgoaplication.adapter.ProductAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.model.BannerModel;
import com.application.mgoaplication.model.CategoryModel;
import com.application.mgoaplication.model.ProductModel;
import com.google.gson.GsonBuilder;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    private final int SCAN = 0;
    private List<CategoryModel> listCategory = new ArrayList<>();
    private GridCategoryAdapter gridCategoryAdapter;
    private List<ProductModel> listBestProduct = new ArrayList<>();
    private List<ProductModel> listNewProduct = new ArrayList<>();
    private List<ProductModel> listRecommendProduct = new ArrayList<>();
    private List<BannerModel> dataBanner = new ArrayList<>();
    private ProductAdapter productAdapter;
    private RecyclerView category, bestProduct, newProduct, recommendProduct;
    private ImageView scan;
    private NestedScrollView content;
    private RelativeLayout toolbar;
    private DashboardActivity parent;
    private RelativeLayout contentCart, contentNotif;
    private EditText search;
    private TextView name, email, phone;
    private ImageView avatar;
    private Map<String, String> param = new HashMap<>();
    private SliderView listBanner;
    private BannerAdapter bannerAdapter;
    private boolean isPause = false;
    private RelativeLayout seeAllBest, seeAllNew, seeAllRecommend;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent      = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_home, container, false);

        findView(rootView);
        init();
        initSearchProduct();
        fetchDataHome();
        initCategory();
        return rootView;
    }

    private void findView(View view) {
        category            = view.findViewById(R.id.list_category);
        content             = view.findViewById(R.id.content);
        toolbar             = view.findViewById(R.id.toolbar);
        bestProduct         = view.findViewById(R.id.list_best_seller);
        newProduct          = view.findViewById(R.id.list_new);
        recommendProduct    = view.findViewById(R.id.list_recommended);
        scan                = view.findViewById(R.id.scan);
        contentCart         = view.findViewById(R.id.contentCart);
        search              = view.findViewById(R.id.keyword);
        avatar              = view.findViewById(R.id.image);
        name                = view.findViewById(R.id.name);
        email               = view.findViewById(R.id.email);
        phone               = view.findViewById(R.id.phone);
        listBanner          = view.findViewById(R.id.imageSlider);
        seeAllBest          = view.findViewById(R.id.see_all_best);
        seeAllNew           = view.findViewById(R.id.see_all_new);
        seeAllRecommend     = view.findViewById(R.id.see_all_recommend);
        parent.badgeCart           = view.findViewById(R.id.badge_cart);
        parent.badgeNotification   = view.findViewById(R.id.badge_notification);
        contentNotif        = view.findViewById(R.id.contentNotif);
    }

    private void init() {
        int transparent = getResources().getColor(R.color.transparent);
        int transparentWhite = getResources().getColor(R.color.transparentWhite);
        int white = getResources().getColor(R.color.white);
        parent.generalHelper.fetchImage(avatar.getRootView(), parent.generalHelper.getSession("foto"), avatar);
        name.setText(parent.generalHelper.getSession("nama_member"));
        email.setText(parent.generalHelper.getSession("email"));
        phone.setText(parent.generalHelper.getSession("hp"));
        parent.generalHelper.saveSession("showBadge", "true");

        content.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = content.getScrollY();
                int oldScrollY = 290;
                if (scrollY == 0 && scrollY < oldScrollY) {
                    toolbar.setBackgroundColor(transparent);
                } if (scrollY > (oldScrollY - (oldScrollY / 2))) {
                    float alpha = (scrollY / oldScrollY);
                    int resultColor = ColorUtils.blendARGB(transparentWhite, transparent, alpha);
                    toolbar.setBackgroundColor(resultColor);
                } if (scrollY > oldScrollY) {
                    toolbar.setBackgroundColor(white);
                }
            }
        });
        scan.setOnClickListener(clickListener);
        contentCart.setOnClickListener(clickListener);
        contentNotif.setOnClickListener(clickListener);
        seeAllBest.setOnClickListener(clickListener);
        seeAllNew.setOnClickListener(clickListener);
        seeAllRecommend.setOnClickListener(clickListener);
    }

    private void initSearchProduct() {
        search.setImeActionLabel("CARI", EditorInfo.IME_ACTION_SEARCH);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                    searchProduct(parent.apiService.searchProduct, search.getText().toString());
                    break;
                }
                return false;
            }
        });
//        search.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    searchProduct(parent.apiService.searchProduct, search.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private void searchProduct(String url, String keyword) {
        param.clear();
        if (url == parent.apiService.scanBarcode)
            param.put("barcode", keyword);
        else
            param.put("keywords", keyword);

        parent.apiService.apiService(url, param, null,
                true, "array", new Service.hashMapListener() {
                    @Override
                    public String getHashMap(Map<String, String> hashMap) {
                        try {
                            if (hashMap.get("status").equals("true")) {
                                JSONObject detail;
                                if (url == parent.apiService.scanBarcode) {
                                    detail              = new JSONArray(hashMap.get("response").trim()).getJSONObject(0);
                                } else {
                                    JSONArray data      = new JSONArray(hashMap.get("response").trim()).getJSONArray(0);
                                    detail              = data.getJSONObject(0);
                                }
                                ProductModel item = new ProductModel();
                                item.setId(detail.getString("id"));
                                item.setName(detail.getString("nama_barang"));
                                item.setDescription(detail.getString("deskripsi"));
                                item.setImage(detail.getString("gambar1"));
                                item.setListPrice(detail.getJSONArray("harga"));

                                param.clear();
                                param.put("title", "Hasil Pencarian Product");
                                param.put("data", new GsonBuilder().create().toJson(item));
                                param.put("listPrice", detail.getJSONArray("harga").toString());
                                parent.generalHelper.startIntent(SingleProductActivity.class, false, param);
                            } else {
                                parent.generalHelper.showToast(hashMap.get("message"), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
    }

    private void setProduct(JSONArray data, int type) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            ProductModel item = new ProductModel();
            JSONObject detail = data.getJSONObject(i);

            item.setId(detail.getString("id"));
            item.setName(detail.getString("nama_barang"));
            item.setDescription(detail.getString("deskripsi"));
            item.setImage(detail.getString("gambar1"));
            item.setListPrice(detail.getJSONArray("harga"));

            if (type == 0)
                listBestProduct.add(item);
            else if (type == 1)
                listNewProduct.add(item);
            else if (type == 2)
                listRecommendProduct.add(item);
        }
        if (type == 0)
            parent.initRecyclerView(bestProduct, RecyclerView.HORIZONTAL, listBestProduct, getActivity(), productAdapter);
        else if (type == 1)
            parent.initRecyclerView(newProduct, RecyclerView.HORIZONTAL, listNewProduct, getActivity(), productAdapter);
        else if (type == 2)
            parent.initRecyclerView(recommendProduct, RecyclerView.VERTICAL, listRecommendProduct, getActivity(), productAdapter);
    }

    private void initCategory() {
        try {
            listCategory.clear();
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
                                        listCategory.add(param);
                                    }

                                    int numberOfColumns = 4;
                                    category.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
                                    gridCategoryAdapter = new GridCategoryAdapter(getActivity(), listCategory,
                                            new GridCategoryAdapter.OnClickListener() {
                                                @Override
                                                public void onClickListener(int position) {
                                                    param.clear();
                                                    if (listCategory.get(position).isHaveCategory()) {
                                                        param.put("id", listCategory.get(position).getId());
                                                        param.put("title", listCategory.get(position).getTitle());
                                                        parent.generalHelper.startIntent(SubCategoryActivity.class, false, param);
                                                    } else {
                                                        param.put("type", "category");
                                                        param.put("groupId", listCategory.get(position).getId());
                                                        param.put("title", listCategory.get(position).getTitle());
                                                        parent.generalHelper.startIntent(ListProductActivity.class, false, param);
                                                    }
                                                }
                                            });
                                    category.setAdapter(gridCategoryAdapter);
                                    gridCategoryAdapter.notifyDataSetChanged();
                                    category.setNestedScrollingEnabled(false);
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

    private void fetchDataHome() {
        try {
            parent.apiService.apiService(parent.apiService.dashboard + parent.generalHelper.getSession("id"),
                    null, null, false, "object", new Service.hashMapListener() {
                @Override
                public String getHashMap(Map<String, String> hashMap) {
                    try {
                        JSONObject data     = new JSONObject(hashMap.get("response").trim());
                        JSONObject cart     = data.getJSONObject("keranjang");

                        // set sessions cart & notification
                        String totalCart = "0", amountCart = "0";
                        if (!cart.isNull("totalKeranjang")) {
                            totalCart   = cart.getString("totalKeranjang");
                            amountCart  = cart.getString("subtotal");
                        }
                        parent.generalHelper.saveSession("cart", totalCart);
                        parent.generalHelper.saveSession("cartAmount", amountCart);
                        parent.generalHelper.saveSession("notification", data.getString("jumlahPengumuman"));
                        parent.generalHelper.saveSession("point", data.getString("jumlahPoin"));
                        parent.initBadge(0);
                        parent.initBadge(1);

                        // set product
                        listBestProduct.clear();
                        listNewProduct.clear();
                        listRecommendProduct.clear();
                        setProduct(data.getJSONArray("terlaris"), 0);
                        setProduct(data.getJSONArray("terbaru"), 1);
                        setProduct(data.getJSONArray("rekomendasi"), 2);

                        // show popup cart
                        parent.generalHelper.showPopUpCart(parent.parent, parent.popupCart, parent.amount, parent.price);

                        // set badge
                        JSONArray lists     = data.getJSONArray("banner");
                        dataBanner.clear();
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject detail = lists.getJSONObject(i);
                            BannerModel item = new BannerModel();
                            item.setId(detail.getString("id"));
                            item.setTitle(detail.getString("nama_banner"));
                            item.setImage(detail.getString("gambarFile"));
                            dataBanner.add(item);
                        }
                        bannerAdapter   = new BannerAdapter(getActivity(), dataBanner);
                        listBanner.setSliderAdapter(bannerAdapter);
                        listBanner.setIndicatorAnimation(IndicatorAnimationType.WORM);
                        listBanner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        listBanner.setScrollTimeInSec(4);
                        listBanner.startAutoCycle();
                        bannerAdapter.notifyDataSetChanged();
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

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scan:
                    parent.generalHelper.startIntentForResult(ScanActivity.class, null, SCAN);
                    break;
                case R.id.contentCart:
                    parent.generalHelper.startIntent(CartActivity.class, false, null);
                    break;
                case R.id.contentNotif:
                    parent.generalHelper.startIntent(AnnouncementActivity.class, false, null);
                    break;
                case R.id.see_all_best:
                    param.clear();
                    param.put("title", "Daftar Produk Terlaris");
                    param.put("url", parent.apiService.popularProduct);
                    parent.generalHelper.startIntent(AllProductActivity.class, false, param);
                    break;
                case R.id.see_all_new:
                    param.clear();
                    param.put("title", "Daftar Produk Terbaru");
                    param.put("url", parent.apiService.newProduct);
                    parent.generalHelper.startIntent(AllProductActivity.class, false, param);
                    break;
                case R.id.see_all_recommend:
                    param.clear();
                    param.put("title", "Daftar Produk Rekomendasi");
                    param.put("url", parent.apiService.recommendProduct);
                    parent.generalHelper.startIntent(AllProductActivity.class, false, param);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN) {
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("result");
                searchProduct(parent.apiService.scanBarcode, result);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
//            re-initialize badge
            parent.initBadge(0);
            parent.initBadge(1);
            // show popup cart
            parent.generalHelper.showPopUpCart(parent.parent, parent.popupCart, parent.amount, parent.price);
            parent.generalHelper.fetchImage(avatar.getRootView(), parent.generalHelper.getSession("foto"), avatar);
            parent.generalHelper.saveSession("showBadge", "true");
        }
    }
}