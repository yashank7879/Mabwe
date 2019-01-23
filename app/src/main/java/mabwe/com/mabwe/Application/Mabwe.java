package mabwe.com.mabwe.Application;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import mabwe.com.mabwe.R;

/**
 * Created by mindiii on 28/6/18.
 */

public class Mabwe extends Application {

    public static final String TAG = Mabwe.class.getSimpleName();
    private  Dialog mDialog;

    private RequestQueue mRequestQueue;
    private static Mabwe mInstance;

    public static Mabwe getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AndroidNetworking.initialize(getApplicationContext());
        mInstance = this;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    public void cancelTask(){
        mRequestQueue.cancelAll(TAG);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }

    }

    public static Dialog showProgress(Context mContext) {
        Dialog mDialog = null;
        if (mContext != null) {
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.custom_progress_layout);
            mDialog.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
       return  mDialog;
    }

    public static void hideProgress(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
