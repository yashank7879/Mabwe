package mabwe.com.mabwe.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import mabwe.com.mabwe.adapter.MemberListAdapter;
import mabwe.com.mabwe.modals.MemberModal;
import mabwe.com.mabwe.server_task.WebService;

public class MembersActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_member_back;
    private CardView member_card_view;
    private TextView no_data_found_member;
    private ArrayList<MemberModal> memberList;
    private MemberListAdapter memberListAdapter;
    private Dialog mdialog;
    private SwipeRefreshLayout swipeToRefresh_for_member;
    private String group_id;
    private String setKey = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        findWidgetsById();
    }

    @Override
    protected void onResume() {
        super.onResume();
        member_list_Api(group_id, "");
    }

    /*...........................findWidgetsById..........................*/
    private void findWidgetsById() {
        memberList = new ArrayList<>();
        iv_member_back = findViewById(R.id.iv_member_back);
        member_card_view = findViewById(R.id.member_card_view);
        no_data_found_member = findViewById(R.id.no_data_found_member);
        ProgressBar progressBar_member = findViewById(R.id.progressBar_member);
        EditText et_serch_member = findViewById(R.id.et_serch_member);
        RecyclerView recycler_view_member = findViewById(R.id.recycler_view_member);
        swipeToRefresh_for_member = findViewById(R.id.swipeToRefresh_for_member);

        if (getIntent().getStringExtra("group_post_Id") != null) {
            group_id = getIntent().getStringExtra("group_post_Id");
        }


        memberListAdapter = new MemberListAdapter(memberList, this);
        recycler_view_member.setAdapter(memberListAdapter);
        memberListAdapter.notifyDataSetChanged();
        setOnclickLIstener();

        swipeToRefresh_for_member.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefresh_for_member.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeToRefresh_for_member.setRefreshing(false);
                memberList.clear();
                member_list_Api(group_id, "");
            }
        });

        if (memberList.size() != 0) {
            no_data_found_member.setVisibility(View.GONE);
        } else {
            no_data_found_member.setVisibility(View.VISIBLE);
        }


        textwatcherMethod(et_serch_member);
    }

    /*..............................setOnclickLIstener....................*/
    private void setOnclickLIstener() {
        iv_member_back.setOnClickListener(this);
        member_card_view.setOnClickListener(this);
    }

    /*................................onClick..............................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_member_back:
                onBackPressed();
                break;
        }
    }

    /*..........................textwatcherMethod().........................*/
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
                memberList.clear();
                memberListAdapter.notifyDataSetChanged();
                Mabwe.getInstance().cancelPendingRequests(Mabwe.TAG);
                member_list_Api(group_id, editable.toString());
            }
        });
    }

    /*........................category_Api.............................*/
    private void member_list_Api(String group_id, String searchtext) {

        if (searchtext.equals("")) {
            setKey = "1";
        } else {
            setKey = "0";
        }

        if (setKey.equals("1")) {
//            progressBar.setVisibility(View.VISIBLE);
            mdialog = Mabwe.showProgress(this);
        }

        Map<String, String> map = new HashMap<>();
        map.put("group_id", group_id);
        map.put("search", searchtext);
//
        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                no_data_found_member.setVisibility(View.GONE);
                Log.e("MEMBERLIST", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        memberList.clear();
                        JSONArray data = jsonObject.getJSONArray("data");
//
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) data.get(i);

                            MemberModal memberModal = new MemberModal();
                            memberModal.user_id = jsonObject1.getString("user_id");
                            memberModal.group_id = jsonObject1.getString("group_id");
                            memberModal.fullname = jsonObject1.getString("fullname");
                            memberModal.country = jsonObject1.getString("country");
                            memberModal.profile_image = jsonObject1.getString("profile_image");
                            memberModal.groupName = jsonObject1.getString("groupName");
                            memberModal.isAdmin = jsonObject1.getString("isAdmin");

                            memberList.add(memberModal);
                        }

//                        select_post_category_rv.setAdapter(catgoryAdapter);
                        memberListAdapter.notifyDataSetChanged();

                    } else {

                        if (setKey.equals("1")) {
                            no_data_found_member.setVisibility(View.VISIBLE);
                        } else {
                            Mabwe.hideProgress(mdialog);
                            no_data_found_member.setVisibility(View.VISIBLE);
                        }
//                        no_data_found_member.setVisibility(View.VISIBLE);
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
        service.callSimpleVolley("/user/getGroupMembers", map);
    }

    /*..........................onBackPressed.......................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
