package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.customListner.SelectedListener;
import mabwe.com.mabwe.modals.GroupDetailModal;
import mabwe.com.mabwe.modals.TagModal;

public class SelectedTagAdapter extends RecyclerView.Adapter<SelectedTagAdapter.ViewHolder> {

    private ArrayList<TagModal> updateTagItemList;
    private Context context;
    private SelectedListener selectedListener;

    public SelectedTagAdapter(ArrayList<TagModal> updateTagItemList, Context context, SelectedListener selectedListener) {
        this.updateTagItemList = updateTagItemList;
        this.context = context;
        this.selectedListener = selectedListener;
    }

    @Override
    public SelectedTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_items_view, parent, false);
        return new SelectedTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectedTagAdapter.ViewHolder holder, final int position) {
        TagModal tagModal = updateTagItemList.get(position);

        holder.tag_item_text.setText(tagModal.tagName);

        if (tagModal.istagSelecte) {
            holder.selected_tag_iv.setVisibility(View.VISIBLE);
        } else {
            holder.selected_tag_iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return updateTagItemList.size();
    }

    /*........................TextWatcher filter..................................*/
    public void setFilter(ArrayList<TagModal> filterdNames) {
        this.updateTagItemList = filterdNames;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tag_item_text;
        LinearLayout main_view;
        ImageView selected_tag_iv;

        ViewHolder(View itemView) {
            super(itemView);
            tag_item_text = itemView.findViewById(R.id.tag_item_text);
            main_view = itemView.findViewById(R.id.main_view);
            selected_tag_iv = itemView.findViewById(R.id.selected_tag_iv);
            main_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_view:
                    TagModal tagModal = updateTagItemList.get(getAdapterPosition());

                    if (!tagModal.istagSelecte) {
                        tagModal.istagSelecte = true;
                        selectedListener.getTagItem(tagModal, updateTagItemList.get(getAdapterPosition()).tagName, true);
                    } else {
                        tagModal.istagSelecte = false;
                        selectedListener.getTagItem(tagModal, updateTagItemList.get(getAdapterPosition()).tagName, false);
                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
