package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.GroupDetailModal;

public class UpdateTagAdapter  extends RecyclerView.Adapter<UpdateTagAdapter.ViewHolder> {

    private ArrayList<GroupDetailModal> groupDetailModals;
    private Context context;

    public UpdateTagAdapter(ArrayList<GroupDetailModal> groupDetailModals, Context context) {
        this.groupDetailModals = groupDetailModals;
        this.context = context;
    }

    @NonNull
    @Override
    public UpdateTagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_tag_list, parent, false);
        return new UpdateTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateTagAdapter.ViewHolder holder, final int position) {
        GroupDetailModal groupDetailModal = groupDetailModals.get(position);
        holder.detail_tag_view.setText(MessageFormat.format("#{0}", groupDetailModal.tagName));
    }

    @Override
    public int getItemCount() {
        return groupDetailModals.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView detail_tag_view;

        ViewHolder(View itemView) {
            super(itemView);
            detail_tag_view = itemView.findViewById(R.id.detail_tag_view);

        }
    }
}
