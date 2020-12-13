package com.application.mgoaplication.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.application.mgoaplication.helper.GeneralHelper;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Service {
    private Activity activity;
    private GeneralHelper functionHelper;
    private ProgressDialog pDialog;
    public String domain = "https://grosir.bedabisa.com";
    private String baseUrl = domain + "/api/";
    public String province = baseUrl + "getPropinsi";
    public String city = baseUrl + "getKota?idPropinsi=";
    public String district = baseUrl + "getKecamatan?idKota=";
    public String subDistrict = baseUrl + "getKelurahan?idKecamatan=";
    public String groupMember = baseUrl + "getKelompok";
    public String register = baseUrl + "register";
    public String resendOTP = baseUrl + "sendOTP";
    public String login = baseUrl + "login";
    public String logout = baseUrl + "logout";
    public String newProduct = baseUrl + "newestProduct?page=1";
    public String popularProduct = baseUrl + "popularProduct?page=1";
    public String recommendProduct = baseUrl + "recommendationProduct?page=1&id_member=";
    public String searchProduct = baseUrl + "search";
    public String scanBarcode = baseUrl + "scanBarcode";
    public String savePIN = baseUrl + "updatePin";
    public String categoryProduct = baseUrl + "getKategori";
    public String subCategoryProduct = baseUrl + "getSubkategori?idKategori=";
    public String listProductByCategory = baseUrl + "productByCategory?page=1&id_kategori=";
    public String listProductBySubCategory = baseUrl + "productBySubcategory?page=1&id_subkategori=";
    public String dashboard = baseUrl + "dataHome?id_member=";
    public String addToCart = baseUrl + "addtocart";
    public String listCart = baseUrl + "listKeranjang?id_member=";
    public String listVoucher = baseUrl + "getVoucher";
    public String checkout = baseUrl + "checkout";
    public String listTransaction = baseUrl + "dataPesanan?";
    public String buyAgain = baseUrl + "beliLagiPesanan";
    public String listPrize = baseUrl + "getHadiah";
    public String getPrize = baseUrl + "tukarPoin";
    public String updateCart = baseUrl + "updateKeranjang";
    public String deleteCart = baseUrl + "hapusKeranjang?";
    public String profile = baseUrl + "profile?id_member=";
    public String updateProfile = baseUrl + "updateProfile";
    public String notification = baseUrl + "pengumuman?id_member=";
    public String notificationDetail = baseUrl + "detailPengumuman?";

    public Service (Activity activity, ProgressDialog pDialog) {
        this.activity   = activity;
        this.pDialog    = pDialog;
        functionHelper  = new GeneralHelper(activity);
        functionHelper.setupProgressDialog(pDialog, "Please wait ...");

        Ion.getDefault(activity).getConscryptMiddleware().enable(false);
        Ion.getDefault(activity).configure().setLogging("LOG API", Log.DEBUG);

        configApiService();
    }

    //    Trust all certificate website
    private void configApiService() {
        Ion.getDefault(activity).getConscryptMiddleware().enable(false);
        Ion.getDefault(activity).configure().setLogging("LOG GET API", Log.DEBUG);
        Ion.getDefault(activity).getHttpClient().getSSLSocketMiddleware().setTrustManagers(new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }});
    }

    // Fungsi ini digunakan untuk mengambil data hashmap dari reponse API
    public interface hashMapListener {
        String getHashMap(Map<String, String> hashMap);
    }

    // Fungsi ini digunakan untuk mengambil data hashmap dari reponse API
    public interface bitmapListener {
        Bitmap getBitmap(Bitmap bitmap);
    }

    // Fungsi ini digunakan untuk mengambil gambar online yang diset ke imageview
    public void getImage(String url, ImageView imageView) {
        try {
            Ion.with(imageView)
//                    .placeholder(R.drawable.circle_transparent)
//                    .error(R.drawable.circle_transparent)
                    .load(url);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    // Fungsi ini digunakan untuk mengambil gambar online sebagai bitmap
    public void getImageBitmap(String url, bitmapListener listener) {
        try {
            Ion.with(activity)
                    .load(url)
                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e == null) {
                        if (result != null)
                            listener.getBitmap(result);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void apiService(String url, Map<String, String> paramList, List<Part> files, boolean showLoading,
                           String type, hashMapListener listener) {
        try {
            if (showLoading)
                functionHelper.showProgressDialog(pDialog, true);
            Builders.Any.B config = Ion.with(activity).load(url)
                    .setTimeout(AsyncHttpRequest.DEFAULT_TIMEOUT)
                    .setLogging("ApiService", Log.DEBUG)
                    .noCache();

            if (paramList != null) {
                for (Map.Entry<String, String> data : paramList.entrySet()) {
                    String key = data.getKey();
                    String value = data.getValue();

                    if (files != null) {
                        config.setMultipartParameter(key, value);
                    } else {
                        config.setBodyParameter(key, value);
                    }
                }
            }

            if (files != null) {
                try {
                    files.removeAll(Collections.singleton(null));
                    config.addMultipartParts(files);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            config.asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            if (showLoading)
                                functionHelper.showProgressDialog(pDialog, false);
                            try {
                                Map<String, String> hash = new HashMap<String, String>();
                                hash.clear();
                                if (e == null) {
                                    Log.e("result", result.getResult());
                                    if (result.getHeaders().code() == 200) {
                                        JSONObject results  = new JSONObject(result.getResult());
                                        String response     = "";
                                        if (type.equals("object"))
                                            response     = results.getJSONObject("responseData")
                                                    .getJSONObject("results").toString();
                                        else
                                            response     = results.getJSONObject("responseData")
                                                    .getJSONArray("results").toString();

                                        hash.put("status", "true");
                                        hash.put("response", response);
                                        listener.getHashMap(hash);
                                    } else {
                                        hash.put("status", "false");
                                        hash.put("message", result.getHeaders().code() + " : " + result.getHeaders().message());
                                        listener.getHashMap(hash);
                                    }
                                } else {
                                    hash.put("status", "false");
                                    hash.put("message", e.getLocalizedMessage());
                                    listener.getHashMap(hash);
                                }
                            } catch (Exception ex) {
                                functionHelper.showToast(ex.getLocalizedMessage(), 0);
                                Log.e("Exception Api Service", "exception", ex);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
