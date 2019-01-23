package mabwe.com.mabwe.customListner;


import mabwe.com.mabwe.modals.ImageBean;

/**
 * Created by mindiii on 9/7/18.
 */

public interface GetImageFromGallaryLIstener {

    void getImagePosition(int pos);
    void removePostImage(ImageBean bean, int pos,boolean value);
}
