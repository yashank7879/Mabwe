package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.DetailsModal;
import mabwe.com.mabwe.modals.HomePostModal;
import mabwe.com.mabwe.modals.TagModal;

/**
 * Created by mindiii on 16/7/18.
 */

public class DetailTagAdapter  extends RecyclerView.Adapter<DetailTagAdapter.ViewHolder> {

    private ArrayList<DetailsModal> detailstagList;
    private Context context;

    public DetailTagAdapter(ArrayList<DetailsModal> detailstagList, Context context) {
        this.detailstagList = detailstagList;
        this.context = context;
    }

    @Override
    public DetailTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_tag_list, parent, false);
        return new DetailTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailTagAdapter.ViewHolder holder, final int position) {
        DetailsModal detailsModal = detailstagList.get(position);
        holder.detail_tag_view.setText(MessageFormat.format("#{0}", detailsModal.tagName));
    }

    @Override
    public int getItemCount() {
        return detailstagList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView detail_tag_view;

        ViewHolder(View itemView) {
            super(itemView);
            detail_tag_view = itemView.findViewById(R.id.detail_tag_view);

        }
    }
}