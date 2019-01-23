package mabwe.com.mabwe.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

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
import mabwe.com.mabwe.activity.CommentActivity;
import mabwe.com.mabwe.activity.DetailsActivity;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.fragments.HomeFragment;
import mabwe.com.mabwe.modals.HomePostModal;
import mabwe.com.mabwe.server_task.WebService;


public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    private ArrayList<HomePostModal> homeList;
    private Context context;
    private int likeCount = 0;
    private Dialog mdialog;
    private ShareHomeListListener listenr;


    public HomeListAdapter(ArrayList<HomePostModal> homeList, Context context,ShareHomeListListener listenr) {
        this.homeList = homeList;
        this.context = context;
        this.listenr = listenr;
    }

    @NonNull
    @Override
    public HomeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeListAdapter.ViewHolder holder, final int position) {
        final HomePostModal homePostModal = homeList.get(position);
        if(homePostModal.address.isEmpty()) holder.home_address_tv.setText("NA");
        else  holder.home_address_tv.setText(homePostModal.address);
        if (homePostModal.category_id.equalsIgnoreCase("2") || homePostModal.category_id.equalsIgnoreCase("4")) {

            holder.home_category_name_tv.setText(homePostModal.categoryName);
            holder.home_category_name_tv.setBackgroundResource(R.drawable.catagarybgblue);
        } else {
            holder.home_category_name_tv.setText(homePostModal.categoryName);
            holder.home_category_name_tv.setBackgroundResource(R.drawable.catagarybgorange);
        }


        holder.we_are_hiring_for_design.setText(homePostModal.title);
        holder.tv_comment_count.setText(homePostModal.comment_count);
        holder.tv_like_count.setText(homePostModal.like_count);

        if(homePostModal.like_count.equals("0") || homePostModal.like_count.equals("1"))
        holder.txt_like.setText("Like");
        else holder.txt_like.setText("Likes");

        if(homePostModal.comment_count.equals("0") || homePostModal.comment_count.equals("1"))
            holder.txt_comment.setText("Comment");
        else holder.txt_comment.setText("Comments");

        if (homePostModal.user_like_status.equals("1")) {
            holder.like_iv.setImageResource(R.drawable.heart);
        } else {
            holder.like_iv.setImageResource(R.drawable.inactive_like_ico);
        }

        holder.like_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeCount = Integer.parseInt(homePostModal.like_count);
                like_Api(homePostModal.postId, holder);

            }
        });

        holder.comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("post_Id", homePostModal.postId);
                context.startActivity(intent);

            }
        });



        holder.home_day_tv.setText(getDayDifference(homePostModal.Crd, homePostModal.CurrentTime));


        if (homePostModal.video_thumb.length() == 0) {

            if (!homePostModal.post_attachment.equals("")) {
                Picasso.with(context).load(homePostModal.post_attachment).fit().centerCrop()
                        .placeholder(R.drawable.myplaceholder)
                        .error(R.drawable.myplaceholder)
                        .into(holder.home_image_iv);
                holder.share_layout.setVisibility(View.VISIBLE);
            }


        } else if (!homePostModal.video_thumb.isEmpty()){
            holder.share_layout.setVisibility(View.GONE);

            Picasso.with(context).load(homePostModal.video_thumb).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(holder.home_image_iv);
        }


    }


    @Override
    public int getItemCount() {
        return homeList.size();
    }

    /*........................TextWatcher filter..................................*/
    public void setFilter(ArrayList<HomePostModal> filterdNames) {
        this.homeList = filterdNames;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView home_category_name_tv, home_day_tv, we_are_hiring_for_design, home_address_tv, tv_comment_count, tv_like_count;
        ImageView home_image_iv, like_iv;
        RelativeLayout list_item_view;
        LinearLayout like_view, comment_view;
        LinearLayout share_layout;
        // by rahul
        TextView txt_like,txt_comment;

        ImageView location_ic;

        ViewHolder(View itemView) {
            super(itemView);
            location_ic=itemView.findViewById(R.id.location_ic);
            home_category_name_tv = itemView.findViewById(R.id.home_category_name_tv);
            home_day_tv = itemView.findViewById(R.id.home_day_tv);
            we_are_hiring_for_design = itemView.findViewById(R.id.we_are_hiring_for_design);
            home_address_tv = itemView.findViewById(R.id.home_address_tv);
            home_image_iv = itemView.findViewById(R.id.home_image_iv);
            tv_comment_count = itemView.findViewById(R.id.tv_comment_count);
            tv_like_count = itemView.findViewById(R.id.tv_like_count);
            like_view = itemView.findViewById(R.id.like_view);
            share_layout = itemView.findViewById(R.id.share_layout);
            like_iv = itemView.findViewById(R.id.like_iv);
            comment_view = itemView.findViewById(R.id.comment_view);
            list_item_view = itemView.findViewById(R.id.list_item_view);
            list_item_view.setOnClickListener(this);
            like_view.setOnClickListener(this);
            share_layout.setOnClickListener(this);

            // by rahul
            txt_like = itemView.findViewById(R.id.txt_like);
            txt_comment = itemView.findViewById(R.id.txt_comment);

        }

        @Override
        public void onClick(View v) {

            HomePostModal homePostModal = homeList.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.list_item_view:
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("postId", homePostModal.postId);
                    context.startActivity(intent);
                    break;
                case R.id.share_layout:
                    listenr.shareClickOn(homeList.get(getAdapterPosition()));
                    break;

            }
        }
    }

    /*.............................like_Api................................................*/
    private void like_Api(String post_id, final ViewHolder holder) {

        mdialog = Mabwe.showProgress(context);

        Map<String, String> map = new HashMap<>();
        map.put("post_id", post_id);

        WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                Log.e("LIKE", response);
                Mabwe.hideProgress(mdialog);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONObject data = (JSONObject) jsonObject.get("data");

                       /* String post_id = data.getString("post_id");
                        String user_id = data.getString("user_id");*/
                        String statuss = data.getString("status");
                        int like_count = Integer.parseInt(data.getString("like_count"));

//                        holder.like_iv.setImageResource(statuss.equals("1")?R.drawable.heart:R.drawable.inactive_like_ico);
                        if (statuss.equals("1")) {
//                            like_count = like_count +1;
                            holder.tv_like_count.setText(like_count + "");
                            holder.like_iv.setImageResource(R.drawable.heart);
                        } else {
//                            like_count = like_count -1;
                            holder.tv_like_count.setText(like_count >= 0 ? like_count + "" : "0");
                            holder.like_iv.setImageResource(R.drawable.inactive_like_ico);
                        }
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
        service.callSimpleVolley("/user/likes", map);
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

  public interface  ShareHomeListListener{
        void shareClickOn(HomePostModal homePostModal);
    }

}
