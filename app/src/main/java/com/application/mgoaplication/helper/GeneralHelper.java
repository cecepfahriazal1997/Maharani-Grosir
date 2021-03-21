package com.application.mgoaplication.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.application.mgoaplication.R;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GeneralHelper {
    private Activity activity;
    private Context context;

    public GeneralHelper(Activity activity) {
        this.activity   = activity;
        this.context    = activity.getApplicationContext();
    }

    public GeneralHelper(Context context) {
        this.context = context;
    }

    // Fungsi ini digunakan untuk berpindah ke activity lain / page lain
    public void startIntent(Class destination, boolean clearIntent,
                            Map<String, String> paramList)
    {
        Intent intent   = new Intent(activity, destination);
        if (clearIntent)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
        else
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (paramList != null) {
            for (Map.Entry<String, String> data : paramList.entrySet()) {
                String key      = data.getKey();
                String value    = data.getValue();

                intent.putExtra(key, value);
            }
        }

        activity.startActivity(intent);
    }

    public Intent buldIntent(Class destination, boolean clearIntent,
                            Map<String, String> paramList)
    {
        Intent intent   = new Intent(activity, destination);
        if (clearIntent)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
        else
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (paramList != null) {
            for (Map.Entry<String, String> data : paramList.entrySet()) {
                String key      = data.getKey();
                String value    = data.getValue();

                intent.putExtra(key, value);
            }
        }

        return intent;
    }

    // Fungsi ini digunakan untuk berpindah ke activity lain namun saat kembali ke activity sebelumnya
    // memberikan suatu nilai
    public void startIntentForResult(Class destination, Map<String, String> paramList, int code)
    {
        Intent intent = new Intent(activity, destination);

        if (paramList != null) {
            for (Map.Entry<String, String> data : paramList.entrySet()) {
                String key      = data.getKey();
                String value    = data.getValue();

                intent.putExtra(key, value);
            }
        }
        activity.startActivityForResult(intent, code);
    }

    // Fungsi ini digunakan untuk menyimpan session sesuai dengan key dan value yang diinginkan
    public void saveSession(String name, String value) {
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(name, value).apply();
    }

    public void saveSessionBatch(JSONObject data) {
        try {
            JSONArray keys = data.names();
            for (int i = 0; i < keys.length(); ++i) {
                String key = keys.getString(i); // Here's your key
                String value = data.getString(key); // Here's your value

                saveSession(key, value);
            }
        } catch (Exception e) {}
    }

    // Fungsi ini digunakan untuk mengambil data session sesuai dengan key yang diinginkan
    public String getSession(String key) {
        return PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(key, null);
    }

    // Fungsi ini digunakan untuk membersihkan session / proses logout aplikasi
    public void clearSession() {
        SharedPreferences sharedPreferences         = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor             = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    // Fungsi ini digunakan untuk membersihkan session / proses logout aplikasi
    public void clearSessionByKey(String key) {
        SharedPreferences sharedPreferences         = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor             = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void changeColorImage(ImageView imageView, int color) {
        imageView.setColorFilter(activity.getBaseContext().getResources().getColor(color));
    }

    public void changeColorButtinImage(ImageButton button, int color) {
        button.setColorFilter(activity.getBaseContext().getResources().getColor(color));
    }

    public void setTextHtml(TextView textView, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }

    public void openUrlToBrowser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    public void showToast(String message, int duration) {
        Toast.makeText(activity.getApplicationContext(), message, duration).show();
    }

    public void hideStatusBar() {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    public View inflateView(int layout) {
        View view;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);
        return view;
    }

    // Fungsi ini digunakan untuk setup progress dialog / loading sesuai dengan context
    public void setupProgressDialog(ProgressDialog pDialog, String title) {
        pDialog.setMessage(title);
        pDialog.setCancelable(false);
    }

    // Fungsi ini digunakan untuk menampilkan progress dialog / loading
    public void showProgressDialog(ProgressDialog pDialog, boolean show) {
        if (show) {
            if (!pDialog.isShowing()) {
                pDialog.show();
            }
        } else {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    // Fungsi ini digunakan untuk mengenkripsi base 64 suatu String
    public String encryptString(String text){
        byte[] data;
        String base64   = "";
        try {
            data            = text.getBytes(StandardCharsets.UTF_8);
            base64          = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
        }
        return base64;
    }

//    Fungsi ini digunakan untuk memberikan gari bawah pada text
    public void setUnderline(TextView view, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        view.setText(content);
    }

    public void showEmptyState(ViewGroup parent, boolean show, int image,
                               String txtTitle, String txtMessage) {
        View child          = inflateView(R.layout.empty_state);
        ImageView icon      = (ImageView) child.findViewById(R.id.icon);
        TextView title      = (TextView) child.findViewById(R.id.title);
        TextView message    = (TextView) child.findViewById(R.id.message);

        if (image != 0) {
            icon.setImageResource(image);
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }

        title.setText(txtTitle);
        message.setText(txtMessage);

        parent.removeAllViews();
        if (show)
            parent.addView(child);
    }

    // Fungsi ini digunakan untuk setup webview yang memiliki konten text
    public void formatIsText(final ProgressDialog pDialog, final WebView webView, final String urlContent, final String type)
    {
        showProgressDialog(pDialog, true);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(WebView.LAYER_TYPE_NONE, null);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                showProgressDialog(pDialog, false);
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                showProgressDialog(pDialog, false);
            }
        });

        if (type.equals("url")) {
            webView.loadUrl(urlContent);
        } else {
            webView.loadData(urlContent, "text/html", "UTF-8");
        }
    }

    // Fungsi ini digunakan untuk menampilkan popup biasa yang berisi judul dan deskripsi
    public void popupDialog(String title, String message, final boolean isFinishActivity) {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            if (isFinishActivity)
                                activity.finish();
                            return;
                        default:
                            return;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle((CharSequence) title);
            builder.setCancelable(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
            } else {
                builder.setMessage(Html.fromHtml(message));
            }

            builder.setNegativeButton((CharSequence) "Tutup", dialogClickListener);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Fungsi ini digunakan untuk menampilkan popup konfirmasi
    public void popupConfirm(String title, String message, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle((CharSequence) title);
        builder.setCancelable(false);
        builder.setMessage((CharSequence) message)
                .setPositiveButton((CharSequence) "Ya", dialogClickListener)
                .setNegativeButton((CharSequence) "Tidak", dialogClickListener).show();
    }

    public float convertDpToPixel(float dp){
        return dp * ((float) activity.getResources().getDisplayMetrics().densityDpi
                / DisplayMetrics.DENSITY_DEFAULT);
    }

    public float convertPixelsToDp(float px){
        return px / ((float) activity.getResources().getDisplayMetrics().densityDpi
                / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openChooser(Activity activity, int code) {
        if (arePermissionsGranted(activity)) {
            ImagePicker.create(activity)
                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Pilih gambar") // image selection title
                    .toolbarArrowColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.darkGray)) // Toolbar 'up' arrow color
                    .includeVideo(false) // Show video on image picker
                    .single() // single mode
//                .multi() // multi mode (default mode)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                    .enableLog(true) // disabling log
                    .start(); // start image picker activity with request code
        } else {
            requestPermissionsCompat(activity, code);
        }
    }

    private boolean arePermissionsGranted(Context context) {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        }
        return true;
    }

    private void requestPermissionsCompat(Activity activity, int code) {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permissions, code);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void fetchImage(View view, String url, ImageView imageView) {
        Glide.with(view)
                .load(url)
                .error(R.drawable.logo)
                .into(imageView);
    }

    public void openOtherApps(String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String formatCurrency(String price) {
        NumberFormat formatter = new DecimalFormat("#,###");
        double myNumber = Double.parseDouble(price);
        String formattedNumber = formatter.format(myNumber);
        return formattedNumber.replace(",", ".");
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String convertDate(String date, String fromPattern, String toPattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(fromPattern);
            Date newDate = format.parse(date);

            format = new SimpleDateFormat(toPattern);
            String result = format.format(newDate);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showPopUpCart(ViewGroup parent, View content, TextView amount, TextView price) {
        if (getSession("cart") != null && !getSession("cart").equals("0")) {
            content.setVisibility(View.VISIBLE);
            amount.setText(getSession("cart") + " Item");
            price.setText("Rp. " + formatCurrency(getSession("cartAmount")));
            animateSlide(true, parent, content);
        } else {
            content.setVisibility(View.GONE);
            amount.setText("0 Item");
            price.setText("Rp. 0");
            animateSlide(false, parent, content);
        }
    }

    public void animateSlide(boolean show, ViewGroup parent, View content) {
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(content);

        TransitionManager.beginDelayedTransition(parent, transition);
        content.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
