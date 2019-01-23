package mabwe.com.mabwe.volley;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import mabwe.com.mabwe.Application.Mabwe;

public class VolleySingleton extends Application{
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context context;
    private ImageLoader mImageLoader;

    private VolleySingleton(final Context context) {
        VolleySingleton.context = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruBitmapCache();

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }
    public static VolleySingleton getInstance() {
        return mInstance;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String cancel_req_tag) {
        getRequestQueue().add(req);
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
}
