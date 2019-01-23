package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.customListner.CustomTagLIstener;
import mabwe.com.mabwe.modals.TagModal;

/**
 * Created by mindiii on 5/7/18.
 */

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    private ArrayList<TagModal> tagItemList;
    private Context context;
    private CustomTagLIstener customTagLIstener;

    public TagListAdapter(ArrayList<TagModal> tagItemList, Context context, CustomTagLIstener customTagLIstener) {
        this.tagItemList = tagItemList;
        this.context = context;
        this.customTagLIstener = customTagLIstener;
    }

    @Override
    public TagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_items_view, parent, false);
        return new TagListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TagListAdapter.ViewHolder holder, final int position) {
        TagModal tagModal = tagItemList.get(position);

            holder.tag_item_text.setText(tagModal.tagName);

        if (tagModal.istagSelecte) {
            holder.selected_tag_iv.setVisibility(View.VISIBLE);
        } else {
            holder.selected_tag_iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tagItemList.size();
    }

    /*........................TextWatcher filter..................................*/
    public void setFilter(ArrayList<TagModal> filterdNames) {
        this.tagItemList = filterdNames;
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
                    TagModal tagModal = tagItemList.get(getAdapterPosition());

                    if (!tagModal.istagSelecte) {
                        tagModal.istagSelecte = true;
                        customTagLIstener.getTagItem(tagModal, tagItemList.get(getAdapterPosition()).tagName, true);
                    } else {
                        tagModal.istagSelecte = false;
                        customTagLIstener.getTagItem(tagModal, tagItemList.get(getAdapterPosition()).tagName, false);
                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
