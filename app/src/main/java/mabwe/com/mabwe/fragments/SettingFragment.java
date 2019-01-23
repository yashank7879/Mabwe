package mabwe.com.mabwe.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.AboutMabweActivity;
import mabwe.com.mabwe.activity.ForgotPasswordActivity;
import mabwe.com.mabwe.activity.InternateConActivity;
import mabwe.com.mabwe.activity.LoginActivity;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.activity.WebviewActivity;
import mabwe.com.mabwe.dialog.PopDialog;
import mabwe.com.mabwe.fcmServices.MyFirebaseInstanceIDService;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView logout_button_setting;
    private Context context;
    private RelativeLayout setting_about_us,setting_privacy, setting_term_and_condsn, setting_change_password, setting_notification;
    private Dialog mdialog;
    private Session session;
    public static boolean popAdShow = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        setUpUI(view);
        //show pop dialog
       /* if (!popAdShow) {
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
            popAdShow = true;
        }*/
        return view;
    }

    private void setUpUI(View view) {
        session = new Session(context);
        logout_button_setting = view.findViewById(R.id.logout_button_setting);
        setting_about_us = view.findViewById(R.id.setting_about_us);
        setting_term_and_condsn = view.findViewById(R.id.setting_term_and_condsn);
        setting_privacy = view.findViewById(R.id.setting_privacy);
       // setting_help_and_support = view.findViewById(R.id.setting_help_and_support);
        setting_change_password = view.findViewById(R.id.setting_change_password);
        setting_notification = view.findViewById(R.id.setting_notification);
        logout_button_setting.setOnClickListener(this);
        setting_about_us.setOnClickListener(this);
        setting_term_and_condsn.setOnClickListener(this);
        setting_privacy.setOnClickListener(this);
      //  setting_help_and_support.setOnClickListener(this);
        setting_change_password.setOnClickListener(this);
        setting_notification.setOnClickListener(this);
        MyFirebaseInstanceIDService MyFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
//        MyFirebaseInstanceIDService.onTokenRefresh();

    }


    @Override
    public void onClick(View v) {
        String type;
        Intent intent;
        switch (v.getId()) {
            case R.id.logout_button_setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.mabwe);
                builder.setMessage("Do you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logout_Api();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.setting_about_us:
                type = "3";
                 intent = new Intent(context, AboutMabweActivity.class);
                intent.putExtra("SettingKey",type);
                context.startActivity(intent);
              //  context.startActivity(new Intent(context, WebviewActivity.class));
                break;

            case R.id.setting_term_and_condsn:
                type = "1";
                goWebView(type);
              //  context.startActivity(new Intent(context, WebviewActivity.class));
                break;

                case R.id.setting_privacy:
                type = "2";
                goWebView(type);
               // context.startActivity(new Intent(context, WebviewActivity.class));
                break;

/*            case R.id.setting_help_and_support:
                type = "1";
                 intent = new Intent(context, WebviewActivity.class);
                 intent.putExtra("HelpAndSupport","helpSupport");
                intent.putExtra("SettingKey",type);
                context.startActivity(intent);
                break;*/

            case R.id.setting_change_password:
                changePassword(context);
                break;

            case R.id.setting_notification:
                break;
        }
    }

    private void goWebView(String type) {
        Intent intent = new Intent(context, WebviewActivity.class);

        intent.putExtra("SettingKey",type);
        context.startActivity(intent);
    }


    /*............................validate_data_for_login............................*/
    private void validate_data_for_login(EditText et_old_password, EditText et_new_password, EditText et_conform_password) {
        String opassword = et_old_password.getText().toString().trim();
        String npassword = et_new_password.getText().toString().trim();
        String cpassword = et_conform_password.getText().toString().trim();

        Validation validation = new Validation(context);
        if (!validation.isEmpty(opassword)) {
            Utils.openAlertDialog(context, getString(R.string.old_pass));
            et_old_password.requestFocus();
            return;
        } else if (!session.getPassword().equals(opassword)) {
            Utils.openAlertDialog(context, getString(R.string.valid_password));
            et_old_password.requestFocus();
            return;
        } else if (!validation.isEmpty(npassword)) {
            Utils.openAlertDialog(context, getString(R.string.new_password));
            et_new_password.requestFocus();
            return;
        } else if (!validation.isValidPassword(npassword)) {
            Utils.openAlertDialog(context, getString(R.string.new_passw_valid));
            et_new_password.requestFocus();
            return;
        } else if (!validation.isEmpty(cpassword)) {
            Utils.openAlertDialog(context, getString(R.string.c_pass));
            et_conform_password.requestFocus();
            return;
        } else if (!validation.isValidPassword(cpassword)) {
            Utils.openAlertDialog(context, getString(R.string.new_passw_valid));
            et_conform_password.requestFocus();
            return;
        } else if (!npassword.equals(cpassword)) {
            Utils.openAlertDialog(context, getString(R.string.cant_same_pass));
            et_conform_password.requestFocus();
            return;
        } else {
            if (Utils.isNetworkAvailable(context)) {
                change_Password_Api(opassword, npassword, cpassword);
            } else {
                startActivity(new Intent(context, InternateConActivity.class));
            }
        }
    }


    /*........................................addtag_dilog......................................*/
    public void changePassword(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.changepassword_layout);
        ImageView setting_iv_cancle = dialog.findViewById(R.id.setting_iv_cancle);
        final EditText et_old_password = dialog.findViewById(R.id.et_old_password);
        final EditText et_new_password = dialog.findViewById(R.id.et_new_password);
        final EditText et_conform_password = dialog.findViewById(R.id.et_conform_password);
        LinearLayout submit_button = dialog.findViewById(R.id.submit_button);
        setting_iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_data_for_login(et_old_password, et_new_password, et_conform_password);
            }
        });

        dialog.show();
    }

    /*......................onAttach()........................*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /*.............................forgot_password_Api................................................*/
    private void change_Password_Api(String opassword, final String npassword, String cpassword) {
        mdialog = Mabwe.showProgress(context);
        Map<String, String> map = new HashMap<>();
        map.put("password", opassword);
        map.put("npassword", npassword);
        map.put("cpassword", cpassword);

        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("Change Password", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        session.session_for_Login(session.getEmail(),npassword);
                        openAlertDialog(context, message);

                    } else {
                        Utils.openAlertDialog(context, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("changePassword", map);
    }


    public void openAlertDialog(final Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mabwe");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent loginIntent = new Intent(context, LoginActivity.class);
                context.startActivity(loginIntent);
                session.logout();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*.............................login_Api................................................*/
    private void logout_Api() {

        mdialog = Mabwe.showProgress(context);
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("LOGOUT RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.cancelAll();
                        session.logout();

                    } else {
                        Utils.openAlertDialog(context, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Mabwe.hideProgress(mdialog);
            }
        });
        service.callGetSimpleVolley("logout");
    }
}
