package mabwe.com.mabwe.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.ShowGroupDetailsActivity;
import mabwe.com.mabwe.modals.GroupModal;
import mabwe.com.mabwe.server_task.WebService;

/**
 * Created by mindiii on 23/7/18.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private ArrayList<GroupModal> gropList;
    private Context context;
    private Dialog mdialog;
    private int likeCount;

    public GroupListAdapter(ArrayList<GroupModal> gropList, Context context) {
        this.gropList = gropList;
        this.context = context;
    }


    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupListAdapter.ViewHolder holder, final int position) {
       final GroupModal groupModal = gropList.get(position);
       holder.group_category_name_tv.setText(groupModal.categoryName);
       holder.group_tv_like_count.setText(groupModal.like_count);
       holder.group_tv_comment_count.setText(groupModal.comment_count);
       holder.we_are_hiring_for_design_group.setText(groupModal.groupName);

        if(groupModal.like_count.equals("0") || groupModal.like_count.equals("1"))
            holder.txt_like.setText("Like");
        else holder.txt_like.setText("Likes");

        if(groupModal.comment_count.equals("0") || groupModal.comment_count.equals("1"))
            holder.txt_comment.setText("Comment");
        else holder.txt_comment.setText("Comments");

        Picasso.with(context).load(groupModal.post_image)
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(holder.group_view_iv);


        if(groupModal.user_like_status.equals("1")){
            holder.group_like_iv.setImageResource(R.drawable.heart);
        }else {
            holder.group_like_iv.setImageResource(R.drawable.inactive_like_ico);
        }

        holder.like_view_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeCount = Integer.parseInt(groupModal.like_count);
                like_Api(groupModal.groupId,holder);
            }
        });


        holder.group_comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ShowGroupDetailsActivity.class);
                intent.putExtra("group_post_Id",groupModal.groupId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gropList.size();
    }

    /*........................TextWatcher filter..................................*/
    public void setFilter(ArrayList<GroupModal> filterdNames) {
        this.gropList = filterdNames;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView group_category_name_tv,we_are_hiring_for_design_group,group_tv_like_count,group_tv_comment_count;
        ImageView group_view_iv, group_like_iv;
        RelativeLayout cotainer_group;
        LinearLayout like_view_group,group_comment_view;
//        CardView card_view;

        // by rahul
        TextView txt_like,txt_comment;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        ViewHolder(View itemView) {
            super(itemView);
            group_view_iv = itemView.findViewById(R.id.group_view_iv);
            group_category_name_tv = itemView.findViewById(R.id.group_category_name_tv);
            we_are_hiring_for_design_group = itemView.findViewById(R.id.we_are_hiring_for_design_group);
            group_tv_like_count = itemView.findViewById(R.id.group_tv_like_count);
            group_tv_comment_count = itemView.findViewById(R.id.group_tv_comment_count);
            group_like_iv = itemView.findViewById(R.id.group_like_iv);
            cotainer_group = itemView.findViewById(R.id.cotainer_group);
            like_view_group = itemView.findViewById(R.id.like_view_group);
            group_comment_view = itemView.findViewById(R.id.group_comment_view);
            cotainer_group.setOnClickListener(this);

            // by rahul
            txt_like = itemView.findViewById(R.id.txt_like);
            txt_comment = itemView.findViewById(R.id.txt_comment);
        }

        @Override
        public void onClick(View v) {

            GroupModal groupModal = gropList.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.cotainer_group:
                    Intent intent = new Intent(context,ShowGroupDetailsActivity.class);
                    intent.putExtra("group_post_Id",groupModal.groupId);
                    context.startActivity(intent);
                    break;
            }
        }
    }

    /*.........................................................................*/

    /*.............................like_Api................................................*/
    private void like_Api(String post_id, final ViewHolder holder) {

        mdialog = Mabwe.showProgress(context);

        Map<String, String> map = new HashMap<>();
        map.put("group_id", post_id);

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

                       /* String group_id = data.getString("group_id");
                        String user_id = data.getString("user_id");*/
                        String statuss = data.getString("status");
                        int like_count = Integer.parseInt(data.getString("like_count"));

//                        holder.group_like_iv.setImageResource(statuss.equals("1")?R.drawable.heart:R.drawable.inactive_like_ico);
                        if(statuss.equals("1")){
//                            like_count = like_count +1;
                            holder.group_tv_like_count.setText(like_count+"");
                            holder.group_like_iv.setImageResource(R.drawable.heart);
                        }else {
//                            like_count = like_count -1;
                            holder.group_tv_like_count.setText(like_count >= 0?like_count+"":"0");
                            holder.group_like_iv.setImageResource(R.drawable.inactive_like_ico);
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
        service.callSimpleVolley("/user/Grouplikes", map);
    }
}
