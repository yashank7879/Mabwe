package mabwe.com.mabwe.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.SliderAdapter;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;

import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private SliderAdapter sliderAdapter;
    private ImageView[] dotes;
    private EditText et_email, et_password;
    private CheckBox login_chackbox;
    private TextView tv_forgotpassword;
    private LinearLayout login_button, tv_signup, container_login;
    private String email ="";
    private String password ="";
    private Session session;
    private boolean cb_rem_isChecked;
    private boolean doubleBackToExitPressedOnce;
    private Dialog mdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = findViewById(R.id.slider_pager);
        linearLayout = findViewById(R.id.linear_layout);
        dotesIndicater(0);
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        findItemViewId();
        setRememberMeData();
    }


    /*.................................find_Item_View_Id....................................*/
    private void findItemViewId() {
        session = new Session(this);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        login_chackbox = findViewById(R.id.login_chackbox);
        tv_forgotpassword = findViewById(R.id.tv_forgotpassword);
        et_password = findViewById(R.id.et_password);
        tv_signup = findViewById(R.id.tv_signup);
        login_button = findViewById(R.id.login_button);
        container_login = findViewById(R.id.container_login);
        setOnClickListener();
    }

    /*.............................set_On_Click_Listener.................................*/
    private void setOnClickListener() {
        login_button.setOnClickListener(this);
        tv_forgotpassword.setOnClickListener(this);
        tv_signup.setOnClickListener(this);
        container_login.setOnClickListener(this);
        login_chackbox.setOnClickListener(this);
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = LoginActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*.............................set_On_Click_Listener..............................*/
    private void keyboard_submit() {
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    validate_data_for_login();
                }
                return false;
            }
        });
    }

    /*.................................View_Pagger....................................*/

    public void dotesIndicater(int position) {
        dotes = new ImageView[3];
        linearLayout.removeAllViews();
        for (int i = 0; i < dotes.length; i++) {
            dotes[i] = new ImageView(this);
            dotes[i].setImageResource(R.drawable.circuinact);
            linearLayout.addView(dotes[i]);
            linearLayout.bringToFront();
        }

        if (dotes.length > 0) {
            dotes[position].setImageResource(R.drawable.ciculeracti);
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            dotesIndicater(position);

            for (int i = 0; i < dotes.length; i++) {
                dotes[position].setImageResource(R.drawable.circuinact);
            }
            dotes[position].setImageResource(R.drawable.ciculeracti);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /*............................setRememberMeData...................................*/

    private void setRememberMeData() {

        String[] a=session.getStringPreferenceRem();
        String name=a[0];
        String pwd=a[1];

        // If Remember me option selected, Set Email and Password from session
        if (!name.equals("")){
            login_chackbox.setChecked(true);
            cb_rem_isChecked = true;
            et_email.setText(name);
        }

        if (!pwd.equals("")){
            login_chackbox.setChecked(true);
            cb_rem_isChecked = true;
            et_password.setText(pwd);
        }
    }

    /*............................validate_data_for_login............................*/
    private void validate_data_for_login() {
         email = et_email.getText().toString().trim();
         password = et_password.getText().toString().trim();

         Validation validation = new Validation(this);
        if (!validation.isEmpty(email)) {
            Utils.openAlertDialog(this, getString(R.string.email_null));
            et_email.requestFocus();
            return;
        }
        else  if (!validation.isValidEmail(email)) {
            Utils.openAlertDialog(this, getString(R.string.email_v));
            et_email.requestFocus();
            return;
        } else if (!validation.isEmpty(password)) {
            Utils.openAlertDialog(this, getString(R.string.password_null));
            et_password.requestFocus();
            return;
        }else if (!validation.isValidPassword(password)) {
            Utils.openAlertDialog(this, getString(R.string.password_v));
            et_password.requestFocus();
            return;
        } else {
            if(Utils.isNetworkAvailable(this)){
                loginTask(email,password);
            }else{
                startActivity(new Intent(this,InternateConActivity.class));
            }
        }
    }

    /*.................................onClick....................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signup:
                Intent loginIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(loginIntent);
                //finish();
                break;
            case R.id.login_button:
                validate_data_for_login();
                break;

            case R.id.tv_forgotpassword:
                Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                break;

            case R.id.container_login:
                hidekeywordOnClick();
                break;

            case R.id.login_chackbox:
                if (cb_rem_isChecked) {
                    cb_rem_isChecked = false;
                    login_chackbox.setChecked(false);
                } else {
                    cb_rem_isChecked = true;
                    login_chackbox.setChecked(true);
                }

                break;
        }
    }

    /*.............................login_Api................................................*/
    private void loginTask(final String username, final String password) {

        mdialog=Mabwe.showProgress(this);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Map<String, String> map = new HashMap<>();
        map.put("email", username);
        map.put("password", password);
        map.put("deviceToken", deviceToken);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("LOGIN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");

                            String userId = userDetail.getString("userId");
                            String fullName = userDetail.getString("fullName");
                            String email = userDetail.getString("email");
                            String authToken = userDetail.getString("authToken");
                            String deviceToken = userDetail.getString("deviceToken");
                            String deviceType = userDetail.getString("deviceType");
                            String profileImage = userDetail.getString("profileImage");
                            String profession = userDetail.getString("profession");
                            String country = userDetail.getString("country");
                            String user_status = userDetail.getString("user_status");

                            UserInfo userInfo = new UserInfo();
                            userInfo.userId = userId;
                            userInfo.fullName = fullName;
                            userInfo.email = email;
                            userInfo.authToken = authToken;
                            userInfo.deviceToken = deviceToken;
                            userInfo.deviceType = deviceType;
                            userInfo.profileImage = profileImage;
                            userInfo.profession = profession;
                            userInfo.country = country;
                            userInfo.user_status = user_status;

                            session.createSession(userInfo);
                            session.session_for_Login(username,password);

                        if (cb_rem_isChecked) {
                            session.setStringPreferenceRem(email,password);

                        } else {
                            session.setStringPreferenceRem("","");
                        }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("userInfo", userInfo);
                            startActivity(intent);
                            finish();

                    } else {
                        Utils.openAlertDialog(LoginActivity.this,message);
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
        service.callSimpleVolley("userLogin", map);
    }

    /*....................................onBackPressed....................................*/
    @Override
    public void onBackPressed() {

        Handler handler = new Handler();
        if (!doubleBackToExitPressedOnce) {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}