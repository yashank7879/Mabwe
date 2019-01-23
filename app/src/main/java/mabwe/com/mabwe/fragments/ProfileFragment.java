package mabwe.com.mabwe.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mabwe.com.cropper.CropImage;
import mabwe.com.cropper.CropImageView;
import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.ImagePickerPackge.ImagePicker;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.InternateConActivity;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.adapter.MyCountryAdapter;
import mabwe.com.mabwe.customListner.CuntryNameListener;
import mabwe.com.mabwe.databinding.FragmentProfileBinding;
import mabwe.com.mabwe.dialog.PopDialog;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.modals.Country;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

import static mabwe.com.mabwe.utils.ToastClass.showToast;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    FragmentProfileBinding binding;

    UserInfo userInfo;

    private Bitmap bitmap;
    private boolean chack = true;
    public static boolean popAdShow = false;
    private MyCountryAdapter countryAdapter;
    private String cuntry = null;
    private String countryShortName = null;
    private List<Country> countryList;
    private MainActivity context;
    private AppCompatImageView img_setting;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        // View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //show pop dialog
      /*  if (!popAdShow) {
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
            popAdShow = true;
        }*/
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);

    }

    /*..............................findViewById()...................................*/
    private void findViewById(View view) {
        /*proprprofile_Image_Iv = view.findViewById(R.id.profile_Image_Iv);
        profile_Email_Tv = view.findViewById(R.id.profile_Email_Tv);
        profile_Country_Tv = view.findViewById(R.id.profile_Country_Tv);
        profile_Post_Tv = view.findViewById(R.id.profile_Post_Tv);
        profile_Name_Tv = view.findViewById(R.id.profile_Name_Tv);
        progress_Profile = view.findViewById(R.id.progress_Profile);
        profile_edit_btn = view.findViewById(R.id.profile_edit_btn);*/
        img_setting = view.findViewById(R.id.img_setting);

        setOnClickListener();


        binding.profileImageIv.setEnabled(false);
        binding.profileEmailTv.setEnabled(false);
        binding.profileCountryTv.setEnabled(false);
        binding.profilePostTv.setEnabled(false);
        binding.profileNameTv.setEnabled(false);

        countryList = loadCountries(context);
        assert countryList != null;
        countryAdapter = new MyCountryAdapter(context, countryList, new CuntryNameListener() {
            @Override
            public void getCountryName(Country country) {
                cuntry = country.country_name;
                countryShortName = country.code;
            }
        });
        binding.profileCountryTv.setAdapter(countryAdapter);

        if (Utils.isNetworkAvailable(context)) {
            try {
                getProfile_Api();
            } catch (NullPointerException e) {
                showToast(context, getString(R.string.too_slow));
            }
        } else showToast(context, getString(R.string.no_internet_access));

    }
//city boy

    public static List<Country> loadCountries(Context context) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray array = new JSONArray(loadJSONFromAsset(context, "country_code.json"));
            List<Country> countries = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Country country = gson.fromJson(array.getString(i), Country.class);
                countries.add(country);

                Log.i("list", "" + country.country_name);
            }
            return countries;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is = null;
        try {
            AssetManager manager = context.getAssets();
            Log.d("JSON Path ", jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /*..............................setOnClickListener()...................................*/
    private void setOnClickListener() {
        img_setting.setOnClickListener(this);
        binding.profileEditBtn.setOnClickListener(this);
        binding.profileImageIv.setOnClickListener(this);

        binding.profileCountryTv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*..............................onAttach()...................................*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (MainActivity) context;
    }

    /*..............................onResume()...................................*/
   /* @Override
    public void onResume() {
        super.onResume();
        getProfile_Api();
    }*/

    //............................validate_data_for_registration.....................

    private void validate_for_Update() {

        String Email = binding.profileEmailTv.getText().toString().trim();
        String Profession = binding.profilePostTv.getText().toString().trim();
        String Name = binding.profileNameTv.getText().toString().trim();
        Validation validation = new Validation(context);

        if (!validation.isEmpty(Name)) {
            Utils.openAlertDialog(context, getString(R.string.fullname_v));
            binding.profileNameTv.requestFocus();
            return;

        } else if (!validation.isValidName(Name)) {
            Utils.openAlertDialog(context, getString(R.string.fullname_null));
            binding.profileNameTv.requestFocus();
            return;

        } else if (!validation.isEmpty(Email)) {
            Utils.openAlertDialog(context, getString(R.string.email_null));
//            profile_Email_Tv.requestFocus();
            return;

        } else if (!validation.isValidEmail(Email)) {
            Utils.openAlertDialog(context, getString(R.string.email));
            binding.profileEmailTv.requestFocus();
            return;

        } else if (cuntry.equals("Select Country")) {
            Utils.openAlertDialog(context, getString(R.string.select_country));
            binding.profileCountryTv.requestFocus();
            return;

        } else if (!validation.isEmpty(Profession)) {
            Utils.openAlertDialog(context, getString(R.string.profession));
            binding.profilePostTv.requestFocus();
            return;
        } else {

            if (Utils.isNetworkAvailable(context)) {
                update_profile_Api(Name, cuntry, countryShortName, Profession);
            } else {
                startActivity(new Intent(context, InternateConActivity.class));
            }
        }
    }

    /*..............................onClick()...................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_edit_btn:
                if (chack) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    binding.profileEditBtn.setImageResource(R.drawable.right_ico);
                    binding.profileNameTv.setSelection(binding.profileNameTv.getText().length());
                    binding.profilePostTv.setSelection(binding.profilePostTv.getText().length());
                    binding.profileNameTv.requestFocus();
                    binding.profileNameTv.setEnabled(true);
                    binding.profileImageIv.setEnabled(true);
                    binding.profileEmailTv.setEnabled(false);
                    binding.profileCountryTv.setEnabled(true);
                    binding.profilePostTv.setEnabled(true);

                    chack = false;
                } else {
                    validate_for_Update();
                }
                break;

            case R.id.profile_Image_Iv:
                context.hidekeywordOnClick();
                getPermissionAndPicImage();
                break;

            case R.id.img_setting:
                context.initSettingFrag();
                break;
        }
    }


    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(ProfileFragment.this);
            }
        } else {
            ImagePicker.pickImage(ProfileFragment.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(ProfileFragment.this);
                } else {
                    //permission denied
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(ProfileFragment.this);
                } else {
                    //permission denied
                }
            }
            break;
        }
    }


    /*.....................................getAllHomePost_api.........................................*/
    private void update_profile_Api(String name, final String country, String countryShortName, String profession) {
        binding.progressProfile.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("fullName", name);
        map.put("country", country);
        map.put("countryShortName", countryShortName);
        map.put("profession", profession);

        Map<String, Bitmap> param = new HashMap<>();

        if (bitmap != null) {
            param.put("profileImage", bitmap);
        }

        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("Profile Data", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("success")) {

                        JSONObject jsonObject2 = null;
                        try {
                            jsonObject2 = (JSONObject) jsonObject.get("data");
                            UserInfo userInfo = new UserInfo();
                            userInfo.userId = jsonObject2.getString("userId");
                            userInfo.fullName = jsonObject2.getString("fullName");
                            userInfo.email = jsonObject2.getString("email");
                            userInfo.authToken = jsonObject2.getString("authToken");
                            userInfo.deviceToken = jsonObject2.getString("deviceToken");
                            userInfo.deviceType = jsonObject2.getString("deviceType");
                            userInfo.profileImage = jsonObject2.getString("profileImage");
                            userInfo.profession = jsonObject2.getString("profession");
                            userInfo.country = jsonObject2.getString("country");
                            userInfo.user_status = jsonObject2.getString("user_status");

                            Session session = new Session(context);
                            session.createSession(userInfo);
                            Utils.openAlertDialog(context, message);

                            setData(userInfo);
                            binding.setUserData(userInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chack = true;

                        binding.profileEditBtn.setImageResource(R.drawable.edit_icon);
                        binding.profileEmailTv.setEnabled(false);
                        binding.profileCountryTv.setEnabled(false);
                        binding.profilePostTv.setEnabled(false);
                        binding.profileNameTv.setEnabled(false);
                        binding.profileImageIv.setEnabled(false);

                    } else {
                        Utils.openAlertDialog(context, message);
                        binding.progressProfile.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                binding.progressProfile.setVisibility(View.GONE);
            }
        });
        service.callMultiPartApi("/user/updateUserProfile", map, param);
    }

    /*.....................................getAllHomePost_api.........................................*/
    private void getProfile_Api() {
        Map<String, String> map = new HashMap<>();
        Session session = new Session(context);
        map.put("user_id",session.getUserId());
        binding.progressProfile.setVisibility(View.VISIBLE);
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("Profile Data25", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        JSONObject data = null;
                        try {
                            data = (JSONObject) jsonObject.get("data");
                            userInfo = new UserInfo();
                            userInfo.userId = data.getString("userId");
                            userInfo.fullName = data.getString("fullName");
                            userInfo.email = data.getString("email");
                            userInfo.authToken = data.getString("authToken");
                            userInfo.deviceToken = data.getString("deviceToken");
                            userInfo.deviceType = data.getString("deviceType");
                            userInfo.profileImage = data.getString("profileImage");
                            userInfo.profession = data.getString("profession");
                            userInfo.country = data.getString("country");
                            userInfo.user_status = data.getString("user_status");
                            setData(userInfo);
                            binding.setUserData(userInfo);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    } else {
                        binding.progressProfile.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                binding.progressProfile.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/user/user",map);
    }


    /*............................setData.............................*/
    private void setData(UserInfo userInfo) {

    /*    binding.profileEmailTv.setText(userInfo.email);
        binding.profileNameTv.setText(userInfo.fullName);*/
        if (!userInfo.profileImage.isEmpty()) {
            Picasso.with(context).load(userInfo.profileImage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(binding.profileImageIv);
        }

        /*if (userInfo.profileImage.isEmpty()) {
            binding.profileImageIv.setImageResource(R.drawable.myplaceholder);
        } else {
            Picasso.with(context).load(userInfo.profileImage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(binding.profileImageIv);
        }*/
/*        if (userInfo.profession.equals("null")) {

        } else {
            binding.profilePostTv.setText(userInfo.profession);
        }*/

//        cuntry = userInfo.country;
        if (!userInfo.country.equals("Select Country")) {

            for (int i = 0; countryList.size() > i; i++) {
                if (countryList.get(i).country_name.equals(userInfo.country)) {
                    binding.profileCountryTv.setSelection(i);
                    cuntry = userInfo.country;
                    countryShortName = userInfo.countryShortName;
                    break;
                }
            }
        }
    }

    /*............................onActivityResult.............................*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 234) {    // Image Picker
                Uri imageUri = ImagePicker.getImageURIFromResult(context, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setRequestedSize(600, 600, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                            .setAspectRatio(3, 2)
                            //.setAspectRatio(4, 3)
                            .start(context, this);
                } else {
                    //alert
//                    DataManager.getInstance().showError(context, getString(R.string.something_went_wromg));
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {   // Image Cropper
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null)

                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());
                    binding.profileImageIv.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
