package mabwe.com.mabwe.modals;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by mindiii on 9/7/18.
 */

public class ImageBean {
    public Uri uri;
    public boolean isUrl;
    public Bitmap bitmap;
    public String url;
    public String imageId;
    public boolean isVideoUrl;

    public ImageBean(String url, Bitmap bitmap,String imageId){
        this.imageId = imageId;
        this.url = url;
        this.bitmap = bitmap;
    }
    public ImageBean(String url, Bitmap bitmap,String imageId, boolean isVideoUrl){
        this.imageId = imageId;
        this.url = url;
        this.bitmap = bitmap;
        this.isVideoUrl = isVideoUrl;
    }
}
