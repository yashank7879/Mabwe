package mabwe.com.mabwe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.server_task.API;
import mabwe.com.mabwe.utils.NetworkUtil;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText forgot_tv_password;
    private LinearLayout layout_for_submit;
    private ImageView iv_back;
    private Dialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findItemViewId();
    }

    /*.................................find_Item_View_Id....................................*/
    private void findItemViewId() {
        forgot_tv_password = findViewById(R.id.forgot_tv_password);
        layout_for_submit = findViewById(R.id.layout_for_submit);
        iv_back = findViewById(R.id.iv_back);
        setOnClickListener();
        hidekeywordOnClick();
    }

    /*.............................set_On_Click_Listener.................................*/
    private void setOnClickListener() {
        forgot_tv_password.setOnClickListener(this);
        layout_for_submit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
//        keyboard_submit();
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = ForgotPasswordActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*.............................set_On_Click_Listener..............................*/
    private void keyboard_submit() {
        forgot_tv_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    validate_data_for_login();
                }
                return false;
            }
        });
    }

    private void validate_data_for_login() {
        String mail_for_forgpassword = forgot_tv_password.getText().toString().trim();

        Validation validation = new Validation(this);
        if (!validation.isEmpty(mail_for_forgpassword)) {
            Utils.openAlertDialog(this, getString(R.string.email_null));
            forgot_tv_password.requestFocus();
            return;
        } else if (!validation.isValidEmail(mail_for_forgpassword)) {
            Utils.openAlertDialog(this, getString(R.string.email_v));
            forgot_tv_password.requestFocus();
            return;
        }


        if (Utils.isNetworkAvailable(this)) {
            forgot_password_Api();
        } else {
            startActivity(new Intent(this, InternateConActivity.class));
        }
    }


    /*.................................onClick....................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_for_submit:
                validate_data_for_login();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }


    /*......................forgot_password_Api.......................................*/

    private void forgot_password_Api() {

        if (NetworkUtil.isNetworkConnected(this)) {
            mdialog = Mabwe.showProgress(this);
            AndroidNetworking.post("https://www.mabwe.com/service/forgetPassword")
                    //AndroidNetworking.post(API.BASE_URL+forgetPassword)
                    .addBodyParameter("email", forgot_tv_password.getText().toString().trim())
                    .setPriority(Priority.HIGH)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Mabwe.hideProgress(mdialog);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            //  Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            openAlertDialog(ForgotPasswordActivity.this, message, true);
                        } else
                            openAlertDialog(ForgotPasswordActivity.this, message, false);
                        //Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

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
        }
    }


    public void openAlertDialog(Context context, String message, final boolean value) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mabwe");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (value) {
                    Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    AlertDialog alert = builder.create();
                    alert.dismiss();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
