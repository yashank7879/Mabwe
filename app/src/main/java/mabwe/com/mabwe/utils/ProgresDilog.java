package mabwe.com.mabwe.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import mabwe.com.mabwe.R;

/**
 * Created by mindiii on 29/6/18.
 */

public class ProgresDilog extends Dialog{
    public static ProgressDialog progress;
   private Context context;

    public ProgresDilog(Context context) {
        super(context);
        this.context = context;
        // This is the fragment_search_details XML file that describes your Dialog fragment_search_details
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
        this.setContentView(R.layout.activity_progress_dialog);
    }

    public static void displayDilog(Context context) {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
        progress = new ProgressDialog(context);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.show();
        progress.setCancelable(false);
    }

    public static void showDilog(Context context){
        progress = new ProgressDialog(context);
        progress.setMessage("loading....");
        progress.setCancelable(false);
    }

    public static void hideDilog(Context context) {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.cancel();
            }
        }
    }
}
