package mabwe.com.mabwe.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import mabwe.com.mabwe.activity.GroupActivity;
import mabwe.com.mabwe.activity.InternateConActivity;
import mabwe.com.mabwe.adapter.GroupListAdapter;
import mabwe.com.mabwe.dialog.PopDialog;
import mabwe.com.mabwe.modals.GroupModal;
import mabwe.com.mabwe.pagination.EndlessRecyclerViewScrollListener;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.NetworkUtil;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class GroupFragment extends Fragment implements View.OnClickListener {

    private LinearLayout linear_layout_group;
    private AutoCompleteTextView et_serch_post_group;
    private RecyclerView group_recycler_view;
    private Context context;
    private ArrayList<GroupModal> groupArrayList;
    private GroupListAdapter groupListAdapter;
    private String setKey = "0";
    private TextView no_data_found_tv;
    private Dialog mdialog;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeToRefresh_group;
    public static boolean popAdShow = false;

    private int offset_def = 0;
    private String searchText = "";
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        findWidgetsViewById(view);
        return view;
    }

    /*.............................findWidgetsViewById.................................*/
    private void findWidgetsViewById(View view) {

        groupArrayList = new ArrayList<>();
        linear_layout_group = view.findViewById(R.id.linear_layout_group);
        et_serch_post_group = view.findViewById(R.id.et_serch_post_group);
        no_data_found_tv = view.findViewById(R.id.no_data_found_tv);
        progressBar = view.findViewById(R.id.progressBar_cyclic);
        swipeToRefresh_group = view.findViewById(R.id.swipeToRefresh_group);

        group_recycler_view = view.findViewById(R.id.group_recycler_view);


     /*   //show pop dialog
        if (!popAdShow) {
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
            popAdShow = true;
        }*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        group_recycler_view.setLayoutManager(linearLayoutManager);

        if (Utils.isNetworkAvailable(context)) {
            getAllGroupPost_api(searchText);
        } else {
            startActivity(new Intent(context, InternateConActivity.class));
        }

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAllGroupPost_api(searchText);
            }
        };
        group_recycler_view.addOnScrollListener(scrollListener);

        groupListAdapter = new GroupListAdapter(groupArrayList, context);
        group_recycler_view.setAdapter(groupListAdapter);
        groupListAdapter.notifyDataSetChanged();


        /*if (groupArrayList.size() == 0) {
            no_data_found_tv.setVisibility(View.VISIBLE);
        } else no_data_found_tv.setVisibility(View.GONE);
*/
        swipeToRefresh_group.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefresh_group.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeToRefresh_group.setRefreshing(false);
                groupArrayList.clear();
                offset_def = 0;
                getAllGroupPost_api(searchText);

            }
        });

        textwatcherMethod(et_serch_post_group);
        setOnClickListener();
    }

    /*................................textwatcherMethod().......................................*/
    private void textwatcherMethod(final EditText searchtext) {
        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                groupArrayList.clear();
                offset_def = 0;
                groupListAdapter.notifyDataSetChanged();
                Mabwe.getInstance().cancelPendingRequests(Mabwe.TAG);
                searchText = editable.toString().trim();
                getAllGroupPost_api(searchText);
            }
        });
    }

    /*.............................set_On_Click_Listener.................................*/
    private void setOnClickListener() {
        linear_layout_group.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.linear_layout_group:
                et_serch_post_group.getText().toString().trim();
                break;
        }
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
        getAllGroupPost_api(searchText);

//        if (Constant.checkIntenateKey == 1) {
//            getAllHomePost_api("");        }
//        Constant.checkIntenateKey = 0;

    }

    /*.....................................getAllHomePost_api.........................................*/
    private void getAllGroupPost_api(String searchtext) {

        if (Utils.isNetworkAvailable(context)) {
            try {

                if (searchtext.equals("")) {
                    setKey = "1";
                } else {
                    setKey = "0";
                }

                if (setKey.equals("1")) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Map<String, String> map = new HashMap<>();
                map.put("limit", "");
                map.put("start", String.valueOf(offset_def));
                map.put("search", searchtext);

                WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        if (setKey.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e("GETALLGROUPPOST", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                groupArrayList.clear();

                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject2 = (JSONObject) data.get(i);

                                    GroupModal groupModal = new GroupModal();
                                    groupModal.groupId = jsonObject2.getString("groupId");
                                    groupModal.groupName = jsonObject2.getString("groupName");
                                    groupModal.category_id = jsonObject2.getString("category_id");
                                    groupModal.Date = jsonObject2.getString("Date");
                                    groupModal.like_count = jsonObject2.getString("like_count");
                                    groupModal.comment_count = jsonObject2.getString("comment_count");
                                    groupModal.post_image = jsonObject2.getString("post_image");
                                    groupModal.categoryName = jsonObject2.getString("categoryName");
                                    groupModal.tag_id = jsonObject2.getString("tag_id");
                                    groupModal.name = jsonObject2.getString("name");
                                    groupModal.user_like_status = jsonObject2.getString("user_like_status");
                                    groupModal.tagName = jsonObject2.getString("tagName");

                                    groupArrayList.add(groupModal);
                                }

                                offset_def += 5;
                            } else {
                                groupArrayList.clear();
                            }
                            if (groupArrayList.size() == 0) {
                                group_recycler_view.setVisibility(View.GONE);
                                no_data_found_tv.setVisibility(View.VISIBLE);
                            } else {
                                // offset_def += 5;
                                group_recycler_view.setVisibility(View.VISIBLE);
                                groupListAdapter.notifyDataSetChanged();
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
                //service.callSimpleVolley("/user/get_group", map);
                service.callSimpleVolley("/user/getGroup", map);


            } catch (NullPointerException e) {
                showToast(context,getString(R.string.too_slow));
            }
        } else showToast(context,getString(R.string.no_internet_access));
    }


}
