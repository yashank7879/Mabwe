package mabwe.com.mabwe.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.Activity_profile;
import mabwe.com.mabwe.modals.ConnectionModel;

public class Connections_adapter extends RecyclerView.Adapter<Connections_adapter.ViewHolder> {

    private ArrayList<ConnectionModel> connectionModels;
    private Context context;
    private Dialog mdialog;
    private int likeCount;

    public Connections_adapter(ArrayList<ConnectionModel> connectionModels, Context context) {
        this.connectionModels = connectionModels;
        this.context = context;
    }


    @Override
    public Connections_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_item_list, parent, false);
        return new Connections_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Connections_adapter.ViewHolder holder, final int position) {
        final ConnectionModel connectionModel = connectionModels.get(position);

        holder.group_category_name_tv.setText(connectionModel.getFullname());

        if (!connectionModel.getCountry().equals("null")) {
            holder.we_are_hiring_for_design_group.setText(connectionModel.getCountry());
        }

        if (connectionModel.getProfileImage().isEmpty()){
            Picasso.with(context).load(connectionModel.getDefaultImage())
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.group_view_iv);
        }else {
            Picasso.with(context).load(connectionModel.getMain_image_url() + connectionModel.getProfileImage())
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.group_view_iv);
        }

    }

    @Override
    public int getItemCount() {
        return connectionModels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView group_category_name_tv, we_are_hiring_for_design_group, group_tv_like_count, group_tv_comment_count;
        ImageView group_view_iv, group_like_iv;
        RelativeLayout cotainer_group;
        LinearLayout like_view_group, group_comment_view;
        TextView txt_like, txt_comment;

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

            ConnectionModel groupModal = connectionModels.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.cotainer_group:
                    Intent profile_intent = new Intent(context, Activity_profile.class);
                    profile_intent.putExtra("userid", groupModal.getUserId());
                    profile_intent.putExtra("screenFlag", "fromConnection");
                    context.startActivity(profile_intent);
                    break;
            }
        }
    }


}

