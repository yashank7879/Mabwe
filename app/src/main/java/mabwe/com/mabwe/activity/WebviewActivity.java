package mabwe.com.mabwe.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.NetworkUtil;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.ToastClass;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.Utils.openAlertDialog;


public class WebviewActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webView;
    private android.app.ProgressDialog dialog;
    private String type = "";
    String urlsTerms = "";
    String urlsAbout = "";
    String urlsPrivacyPolicy = "";
    TextView tvHeader;
    private String helpSupport="";
    private ProgresDilog progresDilog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.web_view);

        ImageView backImage = findViewById(R.id.back_image);
        tvHeader = findViewById(R.id.edit_car_title);
        backImage.setOnClickListener(this);

        progresDilog = new ProgresDilog(this);
        progresDilog.show();
        /*dialog = new android.app.ProgressDialog(WebviewActivity.this);
        dialog.setMessage("Loading please wait.....");
        dialog.setCancelable(false);
        initWebView("http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf");*/
        type = getIntent().getStringExtra("SettingKey");
        if (getIntent().getStringExtra("HelpAndSupport") != null){
            helpSupport =  getIntent().getStringExtra("HelpAndSupport");
        }

        if (NetworkUtil.isNetworkConnected(WebviewActivity.this)) {
            try {
               /* dialog = new ProgressDialog(WebviewActivity.this);
                dialog.setMessage("Loading please wait.....");
                dialog.setCancelable(false);*/

                //GetUrlFromServer();
                setHeader();
                postNewComment();

            } catch (NullPointerException e) {
                ToastClass.showToast(this, getString(R.string.too_slow));
            }
        } else {
            ToastClass.showToast(this, getString(R.string.no_internet_access));
        }



    }


    /*private void GetUrlFromServer() {

        //if (NetworkUtil.isNetworkConnected(this)) {
        mdialog = Mabwe.showProgress(this);
        AndroidNetworking.post("https://www.mabwe.com/service/getContent")
                .addBodyParameter("type", type)
                .setPriority(Priority.HIGH)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(WebviewActivity.this, "content Response " + response, Toast.LENGTH_SHORT).show();

                try {
                    Mabwe.hideProgress(mdialog);
                    JSONObject jsonObject = new JSONObject();
                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        Toast.makeText(WebviewActivity.this, "sucess.....", Toast.LENGTH_SHORT).show();
                        JSONObject urldetail = jsonObject.getJSONObject("Content");

                        if (type.equalsIgnoreCase("1")) {
                            urlsTerms = urldetail.getString("term_and_condition");
                        } else if (type.equalsIgnoreCase("2")) {

                            // urlsAbout = urldetail.getString("aboutus");
                            urlsPrivacyPolicy = urldetail.getString("policy");

                        }
                        setURL();

                    } else {
                        Utils.openAlertDialog(WebviewActivity.this, message);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(ANError anError) {
                Mabwe.hideProgress(mdialog);
                Log.e("error", anError.getErrorDetail());
            }
        });
        //}
    }*/

    public void postNewComment() {

        RequestQueue queue = Volley.newRequestQueue(WebviewActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://www.mabwe.com/service/user/getContent", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(WebviewActivity.this,""+response.toString(),Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject urldetail = jsonObject.getJSONObject("Content");
                    if (type.equalsIgnoreCase("1")) {
                        urlsTerms = urldetail.getString("term_and_condition");
                        setURL();
                    } else if (type.equalsIgnoreCase("2")) {
                        // urlsAbout = urldetail.getString("aboutus");
                        urlsPrivacyPolicy = urldetail.getString("policy");
                        setURL();
                    }else if (type.equalsIgnoreCase("3")){
                        urlsAbout = urldetail.getString("aboutUs");
                        setURL();
                    }else {
                        urlsTerms = urldetail.getString("term_and_condition");
                        setURL();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(WebviewActivity.this,""+error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", type);

                return params;
            }


        };



   /*     sr.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                1));*/
        queue.add(sr);
    }


    /*private void GetUrlFromServer() {

        mdialog = Mabwe.showProgress(this);

        Map<String, String> map = new HashMap<>();
        map.put("type", type);


        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("Setting_Content RESPONS", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        JSONObject urldetail = jsonObject.getJSONObject("Content");

                        if (type.equalsIgnoreCase("1")) {
                            urlsTerms = urldetail.getString("term_and_condition");
                        } else if (type.equalsIgnoreCase("2")) {

                            // urlsAbout = urldetail.getString("aboutus");
                            urlsPrivacyPolicy = urldetail.getString("policy");

                        }
                        setURL();

                    } else {
                        Utils.openAlertDialog(WebviewActivity.this, message);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Mabwe.hideProgress(mdialog);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Mabwe.hideProgress(mdialog);
            }
        });
        service.callSimpleVolley("getContent", map);
    }*/


    private void setURL() {

        switch (type) {
            //get usin json parsing key url include term & policy url
            case "1":
                initWebView(urlsTerms);
              //  tvHeader.setText(R.string.terms_and_conditions);
                break;
            case "2":
              //  tvHeader.setText(R.string.privacy);
                initWebView(urlsPrivacyPolicy);
                break;
            case "3":
              //  tvHeader.setText(R.string.about_us);
                openWebView(urlsAbout);
                break;
                default:
                  //  tvHeader.setText(R.string.help_and_support);
                   // initWebView(urlsTerms);

        }
    }

    private void openWebView(String urlsAbout) {
        webView.setWebViewClient(new MyWebViewClient());
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(urlsAbout);
    }

    private void setHeader() {

        if (type.equals("1") && helpSupport.isEmpty()) {
            tvHeader.setText(R.string.terms_and_conditions);

        } if (type.equals("1") && !helpSupport.isEmpty()) {
            tvHeader.setText(R.string.help_and_support);

        } else if (type.equals("2")) {
            tvHeader.setText(R.string.privacy);

        } else if (type.equals("3")) {
            tvHeader.setText(R.string.about_us);

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowContentAccess(true);
        webView.loadUrl(url);
     //   webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_image) {
            onBackPressed();
        }
    }

     class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progresDilog.dismiss();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progresDilog.show();

        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }
    }
}
