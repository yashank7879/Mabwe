package mabwe.com.mabwe.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.MyCountryAdapter;
import mabwe.com.mabwe.customListner.CuntryNameListener;
import mabwe.com.mabwe.modals.Country;
import mabwe.com.mabwe.databinding.ActivityProfileDetailsBinding;
import mabwe.com.mabwe.modals.DetailsModal;
import mabwe.com.mabwe.modals.UserInfo;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.ToastClass;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class Activity_profile extends AppCompatActivity {

    Context context;
    ActivityProfileDetailsBinding binding;
    private MyCountryAdapter countryAdapter;
    private List<Country> countryList;
    UserInfo userInfo;
    private String cuntry = null;
    private String countryShortName = null;
    String userid, screenFlag;
    TextView btn_sendreq, btn_acceptreq, btn_rejectreq;
    RelativeLayout send_lay, rej_lay;
    private Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_details);
        context = this;

        Bundle bundle = getIntent().getExtras();

        if (getIntent().getStringExtra("userid") != null) {
            userid = getIntent().getStringExtra("userid");
            screenFlag = getIntent().getStringExtra("screenFlag");
            Log.i("screenFlag23542", "screenFlag: " + screenFlag);
        }



        initViews();
    }

    public void initViews() {
        btn_sendreq = findViewById(R.id.btn_sendreq);
        btn_acceptreq = findViewById(R.id.btn_acceptreq);
        btn_rejectreq = findViewById(R.id.btn_rejectreq);
        rej_lay = findViewById(R.id.rej_lay);
        send_lay = findViewById(R.id.send_lay);

        if (screenFlag.equals("fromConnection")) {
            send_lay.setVisibility(View.GONE);
            rej_lay.setVisibility(View.GONE);

        } else if (screenFlag.equals("fromDetails")) {
            send_lay.setVisibility(View.VISIBLE);
            rej_lay.setVisibility(View.GONE);

        }
         session = new Session(Activity_profile.this);
        if (session.getUserId().equals(userid)) {
            send_lay.setVisibility(View.GONE);
        }

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

    private void setOnClickListener() {

        binding.profileCountryTv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConnectionrequest();
            }
        });
        btn_acceptreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptConnectionrequest();
            }
        });

        btn_rejectreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectConnectionrequest();
            }
        });
    }

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

    /*.....................................getAllHomePost_api.........................................*/
    private void getProfile_Api() {
        binding.progressProfile.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userid);
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("ProfileData134", response);

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
                            userInfo.user_request_sent_status = data.getString("user_request_sent_status");
                            userInfo.user_request_recived_status = data.getString("user_request_recived_status");
                            userInfo.user_request_current_status = data.getString("user_request_current_status");
                            setData(userInfo);
                            binding.setUserData(userInfo);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        /*"user_request_sent_status":"0",
                                "user_request_recived_status":"0",
                                "user_request_current_status":"0"*/

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
        service.callSimpleVolley("/user/user", map);
    }

    private void setData(UserInfo userInfo) {

        if (!userInfo.profileImage.isEmpty()) {
            Picasso.with(context).load(userInfo.profileImage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(binding.profileImageIv);
        }


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

        if (userInfo.user_request_sent_status.equals("1") && !session.getUserId().equals(userid) && userInfo.user_request_recived_status.equals("0")){
            // show send btn
            binding.sendLay.setVisibility(View.GONE);
            binding.tvConnected.setVisibility(View.VISIBLE);
            binding.tvConnected.setText("Pending");
           // binding.rejLay.setVisibility(View.GONE);

        }else if (userInfo.user_request_recived_status.equals("1") && !session.getUserId().equals(userid) && userInfo.user_request_sent_status.equals("0")){
            // show accecpt reject btn
            binding.rejLay.setVisibility(View.VISIBLE);
            binding.sendLay.setVisibility(View.GONE);
        }else if (userInfo.user_request_current_status.equals("1") && !session.getUserId().equals(userid)){
            // connection
            binding.rejLay.setVisibility(View.GONE);
            binding.sendLay.setVisibility(View.GONE);
            binding.tvConnected.setVisibility(View.VISIBLE);
        }
    }

    private void sendConnectionrequest() {
        binding.progressProfile.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("request_opposite_person_id", userid);
        map.put("requestStatus", "0");
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("connection345435", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    ToastClass.showToast(context, "" + message);

                    if (status.equals("success")) {
                        binding.sendLay.setVisibility(View.GONE);
                        binding.rejLay.setVisibility(View.GONE);
                        onBackPressed();

                    }
                /*    if (status.equals("success")) {

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
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                binding.progressProfile.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/connections/manageConection", map);
    }

    private void acceptConnectionrequest() {
        binding.progressProfile.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("request_opposite_person_id", userid);
        map.put("requestStatus", "1");
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("connection345435", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    ToastClass.showToast(context, "" + message);
                    if (status.equals("success")) {
                        binding.sendLay.setVisibility(View.GONE);
                        binding.rejLay.setVisibility(View.GONE);
                        onBackPressed();
                    }

                /*    if (status.equals("success")) {

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
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                binding.progressProfile.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/connections/manageConection", map);
    }

    private void rejectConnectionrequest() {
        binding.progressProfile.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("request_opposite_person_id", userid);
        map.put("requestStatus", "2");
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                binding.progressProfile.setVisibility(View.GONE);

                Log.e("connection345435", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    ToastClass.showToast(context, "" + message);

                    if (status.equals("success")) {
                        binding.sendLay.setVisibility(View.GONE);
                        binding.rejLay.setVisibility(View.GONE);
                        onBackPressed();
                    }
                /*    if (status.equals("success")) {

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
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                binding.progressProfile.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/connections/manageConection", map);
    }

}
