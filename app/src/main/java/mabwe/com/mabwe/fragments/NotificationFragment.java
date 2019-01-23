package mabwe.com.mabwe.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.Activity_profile;
import mabwe.com.mabwe.activity.CommentActivity;
import mabwe.com.mabwe.activity.DetailsActivity;
import mabwe.com.mabwe.activity.MembersActivity;
import mabwe.com.mabwe.activity.ShowGroupDetailsActivity;
import mabwe.com.mabwe.adapter.MemberListAdapter;
import mabwe.com.mabwe.adapter.NotifiactionAdapter;
import mabwe.com.mabwe.dialog.PopDialog;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.NotificationModal;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.NetworkUtil;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;


public class NotificationFragment extends Fragment implements NotifiactionAdapter.Listner {


    private Context context;
    private TextView no_data_found_notification;
    private RecyclerView recycler_view_notification;
    private ArrayList<NotificationModal> notificationList;
    private NotifiactionAdapter notifiactionAdapter;
   // private Dialog mdialog;
    private String userId = "";
    private SwipeRefreshLayout swipeToRefresh;
    public static boolean popAdShow = false;
    private ProgresDilog progresDilogl;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        //show pop dialog
       /* if (!popAdShow) {
            PopDialog dialog = new PopDialog();
            dialog.setCancelable(true);
            dialog.show(getChildFragmentManager(), "");
            popAdShow = true;
        }*/

        findWidgetsById(view);
        member_list_Api(userId);

        return view;
    }

    private void findWidgetsById(View view) {
        Session session = new Session(context);

        progresDilogl = new ProgresDilog(context);
        notificationList = new ArrayList<>();
        no_data_found_notification = view.findViewById(R.id.no_data_found_notification);
        //ProgressBar progressBar_notification = view.findViewById(R.id.progressBar_notification);
        recycler_view_notification = view.findViewById(R.id.recycler_view_notification);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        notifiactionAdapter = new NotifiactionAdapter(notificationList, context, this);
        recycler_view_notification.setAdapter(notifiactionAdapter);
        notifiactionAdapter.notifyDataSetChanged();
        no_data_found_notification.setVisibility(View.GONE);
        userId = session.getUserId();

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeToRefresh.setRefreshing(false);
                notificationList.clear();
                member_list_Api(userId);
            }
        });
    }


    /*@Override
    public void onResume() {
        super.onResume();

        member_list_Api(userId);

    }*/


    /*................................onAttached.................................*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /*........................category_Api.............................*/
    private void member_list_Api(String userId) {
        if (Utils.isNetworkAvailable(context)) {
            try {
                progresDilogl.show();
               // mdialog = Mabwe.showProgress(context);
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);

                WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        progresDilogl.dismiss();
                        Log.e("Notification", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {

                                JSONArray data = jsonObject.getJSONArray("data");
//
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) data.get(i);

                                    NotificationModal notificationModal = new NotificationModal();
                                    notificationModal.fullName = jsonObject1.getString("fullName");
                                    notificationModal.profileImage = jsonObject1.getString("profileImage");
                                    notificationModal.id = jsonObject1.getString("id");
                                    notificationModal.notification_by = jsonObject1.getString("notification_by");
                                    notificationModal.notification_for = jsonObject1.getString("notification_for");
                                    notificationModal.is_read = jsonObject1.getString("is_read");
                                    notificationModal.status = jsonObject1.getString("status");
                                    notificationModal.notification_type = jsonObject1.getString("notification_type");
                                    notificationModal.created_on = jsonObject1.getString("created_on");

                                    JSONObject childObj = new JSONObject(jsonObject1.getString("notification_category"));
                                    notificationModal.title = childObj.getString("title");
                                    notificationModal.notify_id = childObj.getString("notify_id");
                                    notificationModal.placeholderImg = childObj.getString("profileImage");

                                    notificationModal.crd = jsonObject1.getString("crd");
                                    notificationModal.CurrentTime = jsonObject1.getString("CurrentTime");
                                    notificationList.add(notificationModal);
                                }

                                notifiactionAdapter.notifyDataSetChanged();

                            } else {
                                Utils.openAlertDialog(context, message);
                                if (notificationList.size() == 0) {
                                    no_data_found_notification.setVisibility(View.VISIBLE);
                                    swipeToRefresh.setVisibility(View.GONE);
                                }
                                progresDilogl.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progresDilogl.dismiss();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        progresDilogl.dismiss();
                    }
                });
                service.callSimpleVolley("/user/getNotification", map);

            } catch (NullPointerException e) {
                //Toast.makeText(context,R.string.too_slow, Toast.LENGTH_SHORT).show();
                showToast(context, getString(R.string.too_slow));
            }
        } else
            //Toast.makeText(context,R.string.no_internet_access, Toast.LENGTH_SHORT).show();
            showToast(context, getString(R.string.no_internet_access));
    }


    /*................................onClick.................................*/


    @Override
    public void clickNotificationItem(NotificationModal item, int position) {
        Intent intent = null;
        switch (item.notification_type) {
            case "mabwe_groupComment":
            case "mabwe_groupLike":
                intent = new Intent(context, ShowGroupDetailsActivity.class);
                intent.putExtra("group_post_Id", item.notify_id);
                context.startActivity(intent);
                break;

           // case "mabwe_comment":
            case "mabwe_Like":
                intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("postId", item.notify_id);
                context.startActivity(intent);
                break;

                case "mabwe_comment":
                     intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("post_Id", item.notify_id);
                    startActivity(intent);
                break;

            case "connection_declined":
            case "connection_accept":
            case "connection_request":
                intent = new Intent(context, Activity_profile.class);
                intent.putExtra("userid", item.notification_by);
                intent.putExtra("screenFlag", "fromDetails");
                context.startActivity(intent);
                break;
        }


    }
}
