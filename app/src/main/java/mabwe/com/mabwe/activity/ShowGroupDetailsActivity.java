package mabwe.com.mabwe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.CommenteUserAdapter;
import mabwe.com.mabwe.adapter.GroupDetailTagAdapter;
import mabwe.com.mabwe.modals.CommentUserModal;
import mabwe.com.mabwe.modals.GroupDetailModal;
import mabwe.com.mabwe.modals.GroupDetailTagData;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;

public class ShowGroupDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView iv_groupDetail_back, group_profil_iv, comment_iv, iv_groupDetail_edit, iv_groupDetail_delete;
    private GroupDetailModal groupDetailModal;
    private String group_post_Id;
    private RecyclerView comment_user_recycler_view;
    private CommenteUserAdapter commenteUserAdapter;
    private TextView description_group_tv, tv_member_count_group, tv_like_count_group, category_name_groupdetails_tv, count_of_commented_user_tv;
    private EditText comment_et;
    private ArrayList<CommentUserModal> commentedList;
    private String comment;
    private Session session;
    private Dialog mdialog;
    private ProgresDilog progresDilog;
    private TextView no_found_group_comt;
    private SwipeRefreshLayout swipeToRefress_coment_details;
    private LinearLayout member_details;
    private RecyclerView details_tag_recycler_view;
    private ArrayList<GroupDetailTagData> groupDetailsTagList;
    private GroupDetailTagAdapter groupDetailTagAdapter;

    RelativeLayout view_present, view_notexit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_details);
        hidekeywordOnClick();
        findWidgetsViewById();
        setOnClick();
    }

    /*..............................findWidgetsViewById......................................*/
    private void findWidgetsViewById() {

        view_present = findViewById(R.id.view_present);
        view_notexit = findViewById(R.id.view_notexit);

        commentedList = new ArrayList<>();
        session = new Session(this);

        iv_groupDetail_back = findViewById(R.id.iv_groupDetail_back);
        member_details = findViewById(R.id.member_details);
        iv_groupDetail_edit = findViewById(R.id.iv_groupDetail_edit);
        iv_groupDetail_delete = findViewById(R.id.iv_groupDetail_delete);
        comment_user_recycler_view = findViewById(R.id.comment_user_recycler_view);
        group_profil_iv = findViewById(R.id.group_profil_iv);
        description_group_tv = findViewById(R.id.description_group_tv);
        tv_member_count_group = findViewById(R.id.tv_member_count_group);
        tv_like_count_group = findViewById(R.id.tv_like_count_group);
        category_name_groupdetails_tv = findViewById(R.id.category_name_groupdetails_tv);
        count_of_commented_user_tv = findViewById(R.id.count_of_commented_user_tv);
        comment_iv = findViewById(R.id.comment_iv);
        comment_et = findViewById(R.id.comment_et);
        // progressBar_cyclic = findViewById(R.id.progressBar_cyclic);
        no_found_group_comt = findViewById(R.id.no_found_group_comt);
        swipeToRefress_coment_details = findViewById(R.id.swipeToRefress_coment_details);
        no_found_group_comt.setVisibility(View.VISIBLE);

        progresDilog = new ProgresDilog(this);
        commenteUserAdapter = new CommenteUserAdapter(commentedList, this);
        comment_user_recycler_view.setAdapter(commenteUserAdapter);
        commenteUserAdapter.notifyDataSetChanged();


        //set tag on recyclerview
        groupDetailsTagList = new ArrayList<>();
        details_tag_recycler_view = findViewById(R.id.details_tag_recycler_view);
        groupDetailTagAdapter = new GroupDetailTagAdapter(groupDetailsTagList, this);
        details_tag_recycler_view.setAdapter(groupDetailTagAdapter);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().getStringExtra("group_post_Id") != null) {
            group_post_Id = getIntent().getStringExtra("group_post_Id");
        } else {
            assert bundle != null;
            if (bundle.getString("notify_id") != null) {
                group_post_Id = bundle.getString("notify_id");
            }
        }

        swipeToRefress_coment_details.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefress_coment_details.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefress_coment_details.setRefreshing(false);
                commentedList.clear();
                showAllComments(group_post_Id);
            }
        });
    }


    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = ShowGroupDetailsActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*..............................getDateTime......................................*/
    @Override
    protected void onResume() {
        super.onResume();
        getDetails_api(group_post_Id);
        showAllComments(group_post_Id);
    }

    /*..............................setOnClick......................................*/
    private void setOnClick() {
        iv_groupDetail_back.setOnClickListener(this);
        comment_iv.setOnClickListener(this);
        member_details.setOnClickListener(this);
        iv_groupDetail_delete.setOnClickListener(this);
    }

    /*..............................onClick......................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member_details:
                Intent intent = new Intent(this, MembersActivity.class);
                intent.putExtra("group_post_Id", group_post_Id);
                startActivity(intent);
                break;

            case R.id.iv_groupDetail_back:
                onBackPressed();
                break;

            case R.id.comment_iv:
                comment = comment_et.getText().toString().trim();
                if (comment.equals("") || comment.isEmpty()) {
                    Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isNetworkAvailable(this)) {
                        CommentUserModal commentUserModal = new CommentUserModal();
                        commentUserModal.comment = comment_et.getText().toString().trim();
                        //commentUserModal.Date = getDateTime();
                        commentUserModal.currentDate = getDayDifference(commentUserModal.Date, commentUserModal.currentDate);
                        commentUserModal.fullname = session.getName();

                        if (session.getCountry().equals("")) {
                            commentUserModal.country = "NA";
                        } else {
                            commentUserModal.country = session.getCountry();
                        }
                        if (session.getProfileImage() != null && !session.getProfileImage().isEmpty()) {
                            commentUserModal.profile_image = session.getProfileImage();
                        } else {
                            commentUserModal.profile_image = "";
                        }

                        commentedList.add(commentUserModal);
                        if (commentedList.size() == 0) {
                            no_found_group_comt.setVisibility(View.VISIBLE);
                        } else {
                            no_found_group_comt.setVisibility(View.GONE);
                        }
                        commenteUserAdapter.notifyDataSetChanged();
                        add_comment_api(comment, group_post_Id);
                        showAllComments(group_post_Id);
                        getDetails_api(group_post_Id);
                        comment_et.setText("");
                        comment_user_recycler_view.scrollToPosition(commentedList.size() - 1);

                    } else {
                        Toast.makeText(this, "Check Internete Connection", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    /*.....................................getDetails_api.........................................*/
    private void getDetails_api(String group_post_Id) {
        progresDilog.show();
        // progressBar_cyclic.setVisibility(View.VISIBLE);
        no_found_group_comt.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("offset", "1");
        map.put("group_id", group_post_Id);
        Log.e("groupDetail param", "" + map);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progresDilog.dismiss();
                //   progressBar_cyclic.setVisibility(View.GONE);
                no_found_group_comt.setVisibility(View.GONE);
                Log.e("GroupDetails", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        groupDetailsTagList.clear();
                        view_notexit.setVisibility(View.GONE);
                        view_present.setVisibility(View.VISIBLE);

                        JSONObject data = null;
                        try {
                            data = (JSONObject) jsonObject.get("data");
                            groupDetailModal = new GroupDetailModal();
                            groupDetailModal.groupId = data.getString("groupId");
                            groupDetailModal.groupName = data.getString("groupName");
                            groupDetailModal.category_id = data.getString("category_id");
                            groupDetailModal.Date = data.getString("Date");
                            groupDetailModal.like_count = data.getString("like_count");
                            groupDetailModal.comment_count = data.getString("comment_count");
                            groupDetailModal.post_image = data.getString("post_image");
                            groupDetailModal.members_count = data.getString("members_count");
                            groupDetailModal.categoryName = data.getString("categoryName");
                            groupDetailModal.user_like_status = data.getString("user_like_status");
                            groupDetailModal.isAdmin = data.getString("isAdmin");

                            JSONArray tags = data.getJSONArray("tags");
                            for (int i = 0; i < tags.length(); i++) {

                                JSONObject jsonObject1 = tags.getJSONObject(i);
                                GroupDetailTagData groupDetailTagData = new GroupDetailTagData();
                                groupDetailTagData.tag_id = jsonObject1.getString("tag_id");
                                groupDetailTagData.tagName = jsonObject1.getString("tagName");

                                //when tag is blank
                                if (!groupDetailTagData.tagName.isEmpty()) {
                                    groupDetailsTagList.add(groupDetailTagData);
                                }
                            }

                            groupDetailTagAdapter.notifyDataSetChanged();
                            setData(groupDetailModal);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    } else {
                        view_present.setVisibility(View.GONE);
                        view_notexit.setVisibility(View.VISIBLE);

                        Toast.makeText(ShowGroupDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        progresDilog.dismiss();
                        //   progressBar_cyclic.setVisibility(View.GONE);
                        no_found_group_comt.setVisibility(View.VISIBLE);
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
        //service.callSimpleVolley("/user/get_group_details", map);
        service.callSimpleVolley("/user/getGroupDetails", map);
    }

    /*.....................................getDetails_api.........................................*/
    private void showAllComments(String group_post_Id) {
        progresDilog.show();
        //  progressBar_cyclic.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("offset", "2");
        map.put("group_id", group_post_Id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                // progressBar_cyclic.setVisibility(View.GONE);
                progresDilog.dismiss();
                no_found_group_comt.setVisibility(View.GONE);

                Log.e("comment GroupDetails", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        commentedList.clear();

                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) data.get(i);

                            CommentUserModal commentUserModal = new CommentUserModal();
                            commentUserModal.comment = jsonObject2.getString("comment");
                            commentUserModal.Date = jsonObject2.getString("Date");
                            commentUserModal.currentDate = jsonObject2.getString("currentDate");
                            commentUserModal.fullname = jsonObject2.getString("fullname");
                            commentUserModal.country = jsonObject2.getString("country");
                            commentUserModal.profile_image = jsonObject2.getString("profile_image");

                            commentedList.add(0, commentUserModal);
                            commenteUserAdapter.notifyDataSetChanged();
                            comment_user_recycler_view.scrollToPosition(commentedList.size() - 1);
                        }

                    } else {
                        // progressBar_cyclic.setVisibility(View.GONE);
                        progresDilog.dismiss();
                        no_found_group_comt.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progresDilog.dismiss();
                // progressBar_cyclic.setVisibility(View.GONE);
            }
        });
        //service.callSimpleVolley("/user/get_group_details", map);
        service.callSimpleVolley("/user/getGroupDetails", map);
    }


    /*.....................................getDetails_api.........................................*/
    private void add_comment_api(String comment, String group_post_Id) {
        progresDilog.show();
        // progressBar_cyclic.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("comment", comment);
        map.put("group_id", group_post_Id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progresDilog.dismiss();
                // progressBar_cyclic.setVisibility(View.GONE);
                Log.e("Groupcomment", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                    } else {
                        progresDilog.dismiss();
                        //  progressBar_cyclic.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progresDilog.dismiss();
                    //  progressBar_cyclic.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progresDilog.dismiss();
                //  progressBar_cyclic.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/user/Groupcomment", map);
    }


    /*.............................setData.......................................*/
    private void setData(final GroupDetailModal groupDetailModal) {

        if (groupDetailModal.isAdmin.equals("1")) {
            iv_groupDetail_edit.setVisibility(View.VISIBLE);
            iv_groupDetail_delete.setVisibility(View.VISIBLE);

            iv_groupDetail_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent updateIntent = new Intent(ShowGroupDetailsActivity.this, UpdateActivity.class);
                    updateIntent.putExtra("group_id", group_post_Id);
                    startActivity(updateIntent);
                }
            });

            iv_groupDetail_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowGroupDetailsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.mabwe);
                    builder.setMessage("Are you sure you want to delete this group?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    delete_Group_Api(group_post_Id);
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
                }
            });

        }
        description_group_tv.setText(groupDetailModal.groupName);
        tv_member_count_group.setText(groupDetailModal.members_count);
        tv_like_count_group.setText(groupDetailModal.like_count);
        category_name_groupdetails_tv.setText(groupDetailModal.categoryName);
        count_of_commented_user_tv.setText(groupDetailModal.comment_count);

//        count_of_commented_user_tv.setText(groupDetailModal.members_count);

        Picasso.with(this).load(groupDetailModal.post_image)
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(group_profil_iv);
    }

    /*.............................delete_Group_Api....................................*/
    private void delete_Group_Api(String group_post_Id) {
        progresDilog.show();
        // progressBar_cyclic.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("groupId", group_post_Id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progresDilog.dismiss();
                //  progressBar_cyclic.setVisibility(View.GONE);
                Log.e("deleteGroup", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        delete_group_dilog(ShowGroupDetailsActivity.this, message);
                    } else {
                        progresDilog.dismiss();
                        // progressBar_cyclic.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progresDilog.dismiss();
                    // progressBar_cyclic.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progresDilog.dismiss();
                //progressBar_cyclic.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/user/deleteGroup", map);
    }


    public void delete_group_dilog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mabwe");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //********** day diffrence  ****************//
    private String getDayDifference(String departDateTime, String returnDateTime) {
        String returnDay = "";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(returnDateTime);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();


            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        returnDay = /*elapsedSeconds +*/ " Just now";
                    } else {
                        returnDay = elapsedMinutes + " minutes ago";
                    }
                } else if (elapsedHours == 1) {
                    returnDay = elapsedHours + " hour ago";
                } else {
                    returnDay = elapsedHours + " hours ago";
                }
            } else if (elapsedDays == 1) {
                returnDay =  /*elapsedDays + */"yesterday";
            } else {
                returnDay = elapsedDays + " days ago";
            }
        } catch (ParseException e) {
            Log.d("day diffrence", e.getMessage());
        }
        return returnDay;
    }


    /*..............................onBackPressed..................................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

