package mabwe.com.mabwe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

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
import mabwe.com.mabwe.modals.CommentUserModal;
import mabwe.com.mabwe.pagination.EndlessRecyclerViewScrollListener;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.Utils;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_comment_back;
    private ImageView send_comment_iv;
    private RecyclerView comment_recycler_view;
    private ArrayList<CommentUserModal> commentedList;
    private CommenteUserAdapter commenteUserAdapter;
    private EditText send_comment_et;
    private String comment;
    private Session session;
    private ProgressBar progressBar_comt;
    private String group_post_Id;
    private TextView no_data_found_comment;
    private CommentUserModal commentUserModal;
    private SwipeRefreshLayout swipeToRefress_comment;
    private RelativeLayout mainView;
    private RelativeLayout tv_no_post_exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        findWidgetsById();
    }

    /*............................findWidgetsById...........................*/
    private void findWidgetsById() {
        session = new Session(this);

        if (getIntent().getStringExtra("post_Id") != null) {
            group_post_Id = getIntent().getStringExtra("post_Id");
        } else if (getIntent().getStringExtra("notify_id") != null) {
            group_post_Id = getIntent().getStringExtra("notify_id");
        }

        commentedList = new ArrayList<>();
        iv_comment_back = findViewById(R.id.iv_comment_back);
        send_comment_iv = findViewById(R.id.send_comment_iv);
        send_comment_et = findViewById(R.id.send_comment_et);
        progressBar_comt = findViewById(R.id.progressBar_comt);
        no_data_found_comment = findViewById(R.id.no_data_found_comment);
        comment_recycler_view = findViewById(R.id.comment_recycler_view);
        swipeToRefress_comment = findViewById(R.id.swipeToRefress_comment);
        mainView = findViewById(R.id.main_view);
        tv_no_post_exist = findViewById(R.id.tv_no_post_exist);

        if (commentedList.size() == 0) {
            no_data_found_comment.setVisibility(View.VISIBLE);
        } else {
            no_data_found_comment.setVisibility(View.GONE);
        }
        commenteUserAdapter = new CommenteUserAdapter(commentedList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        comment_recycler_view.setAdapter(commenteUserAdapter);
        commenteUserAdapter.notifyDataSetChanged();


        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                showAllComments(group_post_Id);
            }
        };
        comment_recycler_view.addOnScrollListener(scrollListener);

        swipeToRefress_comment.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefress_comment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefress_comment.setRefreshing(false);
                commentedList.clear();
                showAllComments(group_post_Id);
            }
        });

        showAllComments(group_post_Id);
        /*.........................*/
        setOnClickListener();
    }

    /*....................................setOnClickListener..................................*/
    private void setOnClickListener() {
        send_comment_iv.setOnClickListener(this);
        iv_comment_back.setOnClickListener(this);
    }

    public static String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = CommentActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*............................onClick...........................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_comment_iv:
                comment = send_comment_et.getText().toString().trim();
                if (comment.equals("") || comment.isEmpty()) {
                    Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {

                    if (Utils.isNetworkAvailable(this)) {

                        CommentUserModal commentUserModal = new CommentUserModal();
                        commentUserModal.comment = send_comment_et.getText().toString().trim();
                        // commentUserModal.Date = getDateTime();
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
                            no_data_found_comment.setVisibility(View.VISIBLE);
                        } else {
                            no_data_found_comment.setVisibility(View.GONE);
                        }
                        commenteUserAdapter.notifyDataSetChanged();
                        add_comment_api(comment, group_post_Id);


                        commentedList.clear();
                        showAllComments(group_post_Id);

                        send_comment_et.setText("");
                        comment_recycler_view.scrollToPosition(commentedList.size() - 1);


                    } else {
                        Toast.makeText(this, "Check Internete Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.iv_comment_back:
                onBackPressed();
                hidekeywordOnClick();
                break;
        }
    }

    /*.....................................getDetails_api.........................................*/
    private void showAllComments(String group_post_Id) {

        progressBar_comt.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("offset", "2");
        map.put("post_id", group_post_Id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progressBar_comt.setVisibility(View.GONE);
                no_data_found_comment.setVisibility(View.GONE);
                Log.e("GroupDetails", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        commentedList.clear();

                        mainView.setVisibility(View.VISIBLE);
                        tv_no_post_exist.setVisibility(View.GONE);
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) data.get(i);

                            commentUserModal = new CommentUserModal();
                            commentUserModal.comment = jsonObject2.getString("comment");
                            commentUserModal.Date = jsonObject2.getString("Date");
                            commentUserModal.currentDate = jsonObject2.getString("currentDate");
                            commentUserModal.fullname = jsonObject2.getString("fullname");
                            commentUserModal.country = jsonObject2.getString("country");
                            commentUserModal.profile_image = jsonObject2.getString("profile_image");
                            commentUserModal.userId = jsonObject2.getString("userId");

                            commentedList.add(0, commentUserModal);
                            commenteUserAdapter.notifyDataSetChanged();
                            comment_recycler_view.scrollToPosition(commentedList.size() - 1);
                        }

                    } else {

                        if (message.equals("This post does not exist")) {
                            mainView.setVisibility(View.GONE);
                            tv_no_post_exist.setVisibility(View.VISIBLE);

                        }
                        progressBar_comt.setVisibility(View.GONE);
                        no_data_found_comment.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progressBar_comt.setVisibility(View.GONE);
            }
        });
        //service.callSimpleVolley("/user/get_post_details", map);
        service.callSimpleVolley("/user/getPostDetails", map);
    }


    //********** day diffrence  ****************//
    public static String getDayDifference(String departDateTime, String returnDateTime) {
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

    /*.....................................getDetails_api.........................................*/
    private void add_comment_api(String comment, String group_post_Id) {
        progressBar_comt.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("post_id", group_post_Id);
        map.put("comment", comment);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                progressBar_comt.setVisibility(View.GONE);
                no_data_found_comment.setVisibility(View.GONE);
                Log.e("Post_comment", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                    } else {
                        progressBar_comt.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar_comt.setVisibility(View.GONE);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progressBar_comt.setVisibility(View.GONE);
            }
        });
        service.callSimpleVolley("/user/comment", map);
    }

    /*.........................................................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
