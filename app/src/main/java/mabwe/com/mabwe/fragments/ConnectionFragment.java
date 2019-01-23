package mabwe.com.mabwe.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import mabwe.com.mabwe.activity.Activity_PendingConnections;
import mabwe.com.mabwe.activity.InternateConActivity;
import mabwe.com.mabwe.adapter.Connections_adapter;
import mabwe.com.mabwe.dialog.PopDialog;
import mabwe.com.mabwe.modals.ConnectionModel;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;


public class ConnectionFragment extends Fragment implements View.OnClickListener {

    private RecyclerView group_recycler_view;
    private Context context;
    private ArrayList<ConnectionModel> connectionModels;
    private Connections_adapter connections_adapter;
    private String setKey = "0";
    private TextView no_data_found_tv;

    private ProgressBar progressBar;

    public static boolean popAdShow = false;


    TextView tv_title_pending;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        findWidgetsViewById(view);
        return view;
    }

    /*.............................findWidgetsViewById.................................*/
    private void findWidgetsViewById(View view) {

        connectionModels = new ArrayList<>();
        tv_title_pending = view.findViewById(R.id.tv_title_pending);
        no_data_found_tv = view.findViewById(R.id.no_data_found_tv);
        progressBar = view.findViewById(R.id.progressBar_cyclic);


        group_recycler_view = view.findViewById(R.id.group_recycler_view);


      /*  //show pop dialog
        if (!popAdShow) {
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
            popAdShow = true;
        }
*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        group_recycler_view.setLayoutManager(linearLayoutManager);

        if (Utils.isNetworkAvailable(context)) {
            getAllConnections();
        } else {
            startActivity(new Intent(context, InternateConActivity.class));
        }


        connections_adapter = new Connections_adapter(connectionModels, context);
        group_recycler_view.setAdapter(connections_adapter);
        connections_adapter.notifyDataSetChanged();


        tv_title_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile_intent = new Intent(context, Activity_PendingConnections.class);
                context.startActivity(profile_intent);
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    /*.....................................onResume.........................................*/
    @Override
    public void onResume() {
        super.onResume();
        getAllConnections();
    }


    public void getAllConnections() {
        if (Utils.isNetworkAvailable(context)) {
            try {

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
                                    connectionModel.setFullname(jsonObject2.getString("fullname"));
                                    connectionModel.setUserId(jsonObject2.getString("userId"));
                                    connectionModel.setMain_image_url(jsonObject2.getString("main_image_url"));
                                    connectionModel.setThumb_image_url(jsonObject2.getString("thumb_image_url"));
                                    connectionModel.setProfileImage(jsonObject2.getString("profileImage"));
                                    connectionModel.setCountry(jsonObject2.getString("country"));
                                    connectionModel.setDefaultImage(jsonObject2.getString("defaultImage"));

                                    connectionModels.add(connectionModel);
                                }

                               /* "connectionId":"51",
                                        "requestBy":"138",
                                        "requestFor":"139",
                                        "requestStatus":"1",
                                        "status":"1",
                                        "crd":"2018-12-24 10:26:40",
                                        "upd":"2018-12-24 10:26:43",
                                        "userId":"139",
                                        "fullname":"yash2",
                                        "country":null,
                                        "main_image_url":"http:\/\/dev.mabwe.com\/uploads\/profile\/",
                                        "thumb_image_url":"http:\/\/dev.mabwe.com\/uploads\/profile\/thumb\/"
*/
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

                service.callSimpleVolley("/connections/getListofConnections", map);


            } catch (NullPointerException e) {
                showToast(context, getString(R.string.too_slow));
            }
        } else showToast(context, getString(R.string.no_internet_access));
    }


    @Override
    public void onClick(View view) {

    }
}
