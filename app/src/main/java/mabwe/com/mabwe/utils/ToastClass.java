package mabwe.com.mabwe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastClass {

    //Toast message
    public static void showToast(Context mcontext,String message) {
        Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
    }
}
