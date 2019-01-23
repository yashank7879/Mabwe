package mabwe.com.mabwe.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import java.io.IOException;
import java.security.AllPermission;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.cropper.CropImage;
import mabwe.com.cropper.CropImageView;
import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.ImagePickerPackge.ImagePicker;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.PermissionAll;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class RegisterationActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView profile_ImageView;
    private ImageView select_iv_camera;
    private EditText register_tv_full_name, register_tv_email_id, register_tv_password;
    private LinearLayout register_button;
    private LinearLayout register_tv_login, container_register;
    private String TAG = "tag";
    private Bitmap bitmap;
    private Dialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        PermissionAll allPermission= new PermissionAll();
        allPermission.RequestMultiplePermission(this);
        findItemViewId();
    }

    /*............................find_Item_View_Id............................*/
    private void findItemViewId() {
        profile_ImageView = findViewById(R.id.profile_ImageView);
        select_iv_camera = findViewById(R.id.select_iv_camera);
        register_tv_full_name = findViewById(R.id.register_tv_full_name);
        register_tv_email_id = findViewById(R.id.register_tv_email_id);
        register_tv_password = findViewById(R.id.register_tv_password);
        register_button = findViewById(R.id.register_button);
        register_tv_login = findViewById(R.id.register_tv_login);
        container_register = findViewById(R.id.container_register);
        setOnclickListener();
    }

    /*............................set_On_click_Listener............................*/
    private void setOnclickListener() {
        register_button.setOnClickListener(this);
        register_tv_login.setOnClickListener(this);
        container_register.setOnClickListener(this);
        profile_ImageView.setOnClickListener(this);
    }

    /*............................keyboard_done_btn_submit............................*/
    private void keyboard_done_btn_submit() {
        register_tv_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    validate_data_for_registration();
                }
                return false;
            }
        });
    }


    /*............................validate_data_for_registration............................*/
    private void validate_data_for_registration() {
        String fullname = register_tv_full_name.getText().toString().trim();
        String emailId = register_tv_email_id.getText().toString().trim();
        String password = register_tv_password.getText().toString().trim();

        Validation validation = new Validation(this);

        if (!validation.isEmpty(fullname)) {
            Utils.openAlertDialog(this, getString(R.string.fullname_v));
            register_tv_full_name.requestFocus();
            return;

        } else if (!validation.isValidName(fullname)) {
            Utils.openAlertDialog(this, getString(R.string.fullname_null));
            register_tv_full_name.requestFocus();
            return;

        } else if (!validation.isEmpty(emailId)) {
            Utils.openAlertDialog(this, getString(R.string.email_null));
            register_tv_email_id.requestFocus();
            return;
        } else if (!validation.isValidEmail(emailId)) {
            Utils.openAlertDialog(this, getString(R.string.email_v));
            register_tv_email_id.requestFocus();
            return;
        } else if (!validation.isEmpty(password)) {
            Utils.openAlertDialog(this, getString(R.string.password_null));
            register_tv_password.requestFocus();
            return;
        } else if (!validation.isValidPassword(password)) {
            //Utils.openAlertDialog(this, getString(R.string.password_v));
            Utils.openAlertDialog(this, getString(R.string.new_passw_valid));
            register_tv_password.requestFocus();
            return;
        } else {

            if (Utils.isNetworkAvailable(this)) {
                registration_Api(fullname, emailId, password);
            } else {
                startActivity(new Intent(this, InternateConActivity.class));
            }

        }
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = RegisterationActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*........................onClick............................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_ImageView:

                getPermissionAndPicImage();
                break;

            case R.id.register_button:
                validate_data_for_registration();
                break;

            case R.id.register_tv_login:
                Intent loginIntent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

                break;

            case R.id.container_register:
                hidekeywordOnClick();
                break;
        }
    }


    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(RegisterationActivity.this);
            }
        } else {
            ImagePicker.pickImage(RegisterationActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(RegisterationActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(RegisterationActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(RegisterationActivity.this);
                } else {
                    Toast.makeText(RegisterationActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }


    /*....................................registration_Api.........................................*/
    private void registration_Api(final String fullname, final String emailId, final String password) {
        mdialog = Mabwe.showProgress(RegisterationActivity.this);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> param = new HashMap<>();
        param.put("email", emailId);
        param.put("fullName", fullname);
        param.put("password", password);
        param.put("deviceToken", deviceToken);

        Map<String, Bitmap> map = new HashMap<>();

        if (bitmap != null) {
            map.put("photo", bitmap);
        }

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("SIGN IN RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        JSONObject userDetails = jsonObject.getJSONObject("userDetail");
                        String userId = userDetails.getString("userId");
                        String fullName = userDetails.getString("fullName");
                        String email = userDetails.getString("email");
                        String authToken = userDetails.getString("authToken");
                        String deviceToken = userDetails.getString("deviceToken");
                        String deviceType = userDetails.getString("deviceType");
                        String profileImage = userDetails.getString("profileImage");
                        String profession = userDetails.getString("profession");
                        String country = userDetails.getString("country");
                        String user_status = userDetails.getString("user_status");

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

                        Session session = new Session(RegisterationActivity.this);
                        session.createSession(userInfo);

//                        Toast.makeText(RegisterationActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterationActivity.this, MainActivity.class);
//                        intent.putExtra("userInfo", userInfo);
                        startActivity(intent);
                        finish();


                    } else {
                        Utils.openAlertDialog(RegisterationActivity.this, message);
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

        service.callMultiPartApi("userRegistration", param, map);
    }

    /*................................onActivityResult...................................*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(RegisterationActivity.this, requestCode, resultCode, data);

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL)
                            .setRequestedSize(600, 600, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                            .setAspectRatio(3, 2)
                            .start(this);
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                try {
                    if (result != null)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (bitmap != null) {

                        bitmap = ImagePicker.getImageResized(this, result.getUri());
                        profile_ImageView.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
