package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.MemberModal;
import mabwe.com.mabwe.session.Session;

/**
 * Created by mindiii on 31/7/18.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    private ArrayList<MemberModal> memberlist;
    private Context context;
    private Session session;


    public MemberListAdapter(ArrayList<MemberModal> memberlist, Context context) {
        this.memberlist = memberlist;
        this.context = context;
        session = new Session(context);
    }


    @NonNull
    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        return new MemberListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberListAdapter.ViewHolder holder, final int position) {

        MemberModal memberModal = memberlist.get(position);

        if (memberModal.isAdmin.equals("1")){
            holder.member_admin.setVisibility(View.VISIBLE);
        }else{
            holder.member_admin.setVisibility(View.GONE);
        }

        holder.member_name.setText(memberModal.fullname);
        holder.member_country.setText(memberModal.country);
        holder.member_name.setText(memberModal.fullname);

        Picasso.with(context).load(memberModal.profile_image)
                .placeholder(R.drawable.profile_image_holder)
                .error(R.drawable.profile_image_holder)
                .into(holder.member_profile);


    }

    @Override
    public int getItemCount() {
        return memberlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView member_name, member_country, member_admin;
        ImageView member_profile;

        ViewHolder(View itemView) {
            super(itemView);
            member_name = itemView.findViewById(R.id.member_name);
            member_profile = itemView.findViewById(R.id.member_profile);
            member_country = itemView.findViewById(R.id.member_country);
            member_admin = itemView.findViewById(R.id.member_admin);

        }
    }
}