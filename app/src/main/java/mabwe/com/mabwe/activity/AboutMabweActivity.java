package mabwe.com.mabwe.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
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

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.utils.NetworkUtil;
import mabwe.com.mabwe.utils.ProgresDilog;

import static android.webkit.WebView.RENDERER_PRIORITY_BOUND;

public class AboutMabweActivity extends AppCompatActivity implements View.OnClickListener {
    private String type = "";
    private WebView webView;
    String urlsTerms = "";
    String urlsAbout = "";
    String urlsPrivacyPolicy = "";
    private ProgresDilog progresDilog;
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mabwe);
        webView = findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        progresDilog = new ProgresDilog(this);
        ImageView backImage = findViewById(R.id.back_image);
        tvHeader = findViewById(R.id.edit_car_title);
        backImage.setOnClickListener(this);

        type = getIntent().getStringExtra("SettingKey");
        if (NetworkUtil.isNetworkConnected(this)) {
            postNewComment();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_image) {
            onBackPressed();
        }
    }

    public void postNewComment() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://www.mabwe.com/service/user/getContent", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(WebviewActivity.this,""+response.toString(),Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject urldetail = jsonObject.getJSONObject("Content");
                    if (type.equalsIgnoreCase("3")){
                        urlsAbout = urldetail.getString("aboutUs");
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


    private void setURL() {
        switch (type) {
            case "3":
                tvHeader.setText(R.string.about_us);
                openWebView(urlsAbout);
                break;
        default:
        }
    }

    private void openWebView(String urlsAbout) {
        webView.setWebViewClient(new MyWebViewClient());
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(urlsAbout);
    }


    private class MyWebViewClient extends WebViewClient {
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
