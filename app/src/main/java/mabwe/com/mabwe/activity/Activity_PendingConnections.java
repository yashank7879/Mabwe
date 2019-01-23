package mabwe.com.mabwe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.PendingConnectionAdapter;
import mabwe.com.mabwe.modals.ConnectionModel;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.ToastClass;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class Activity_PendingConnections extends AppCompatActivity {


    private RecyclerView group_recycler_view;
    private Context context;
    private ArrayList<ConnectionModel> connectionModels;
    private PendingConnectionAdapter connections_adapter;
    private String setKey = "0";
    private TextView no_data_found_tv;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_connection);
        context = this;
        initViews();
    }

    public void initViews() {
        connectionModels = new ArrayList<>();
        no_data_found_tv = findViewById(R.id.no_data_found_tv);
        progressBar = findViewById(R.id.progressBar_cyclic);


        group_recycler_view = findViewById(R.id.group_recycler_view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        group_recycler_view.setLayoutManager(linearLayoutManager);

        if (Utils.isNetworkAvailable(context)) {
            getPendingConnections();
        } else {
            startActivity(new Intent(context, InternateConActivity.class));
        }


        connections_adapter = new PendingConnectionAdapter(connectionModels, context);
        group_recycler_view.setAdapter(connections_adapter);
        connections_adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(context)) {
            getPendingConnections();
        } else {
            startActivity(new Intent(context, InternateConActivity.class));
        }
    }

    public void getPendingConnections() {
        if (Utils.isNetworkAvailable(context)) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                if (setKey.equals("1")) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Map<String, String> map = new HashMap<>();

                WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        if (setKey.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        Log.e("getAllConnections", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");


                            if (status.equals("success")) {
                                connectionModels.clear();

                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject2 = (JSONObject) data.get(i);

                                    ConnectionModel connectionModel = new ConnectionModel();
                                    connectionModel.setConnectionId(jsonObject2.getString("connectionId"));
                                    connectionModel.setRequestBy(jsonObject2.getString("requestBy"));
                                    connectionModel.setRequestFor(jsonObject2.getString("requestFor"));
                                    connectionModel.setFullname(jsonObject2.getString("request_by_name"));
                                   // connectionModel.setEmail(jsonObject2.getString("email"));
                                    //connectionModel.setUserId(jsonObject2.getString("userId"));
                                    connectionModel.setMain_image_url(jsonObject2.getString("main_image_url"));
                                    connectionModel.setThumb_image_url(jsonObject2.getString("thumb_image_url"));
                                    connectionModel.setProfileImage(jsonObject2.getString("requseted_by_image"));
                                    connectionModel.setDefaultImage(jsonObject2.getString("defaultImage"));
                                    connectionModel.setRequested_by_country(jsonObject2.getString("requested_by_country"));


                                    connectionModels.add(connectionModel);
                                }


                            }
                            if (connectionModels.size() == 0) {
                                group_recycler_view.setVisibility(View.GONE);
                                no_data_found_tv.setVisibility(View.VISIBLE);
                            } else {
                                group_recycler_view.setVisibility(View.VISIBLE);
                                connections_adapter.notifyDataSetChanged();
                                no_data_found_tv.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {

                    }
                });

                service.callSimpleVolley("/connections/getPendingRequest", map);


            } catch (NullPointerException e) {
                showToast(context, getString(R.string.too_slow));
            }
        } else showToast(context, getString(R.string.no_internet_access));
    }


    public void acceptRequest(String userid) {
        acceptConnectionrequest(userid);
    }

    public void rejecttRequest(String userid) {
        rejectConnectionrequest(userid);
    }

    private void acceptConnectionrequest(String userid) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("request_opposite_person_id", userid);
        map.put("requestStatus", "1");
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                Log.e("connection345435", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    ToastClass.showToast(context, "" + message);
                    if (status.equals("success")) {
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
                progressBar.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/connections/manageConection", map);
    }

    private void rejectConnectionrequest(String userid) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("request_opposite_person_id", userid);
        map.put("requestStatus", "2");
        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                Log.e("connection345435", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    ToastClass.showToast(context, "" + message);

                    if (status.equals("success")) {
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
                progressBar.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/connections/manageConection", map);
    }

}
