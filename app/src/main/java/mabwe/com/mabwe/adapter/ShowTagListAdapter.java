package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.customListner.RemoveListItemListener;
import mabwe.com.mabwe.modals.TagModal;

public class ShowTagListAdapter  extends RecyclerView.Adapter<ShowTagListAdapter.ViewHolder> {

    private ArrayList<TagModal> arrayList;
    private Context context;
    private RemoveListItemListener removeListItemListener;

    public ShowTagListAdapter(ArrayList<TagModal> arrayList, Context context, RemoveListItemListener removeListItemListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.removeListItemListener = removeListItemListener;
    }

    @NonNull
    @Override
    public ShowTagListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_view, parent, false);
        return new ShowTagListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowTagListAdapter.ViewHolder holder, final int position) {

        TagModal tagModal  = arrayList.get(position);
        if(tagModal.istagSelecte){
            holder.tv_add_tag_view.setText(tagModal.tagName);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_add_tag_view;
        CardView iv_add_tag_card_view;
        ImageView tag_iv_cancle;

        ViewHolder(View itemView) {
            super(itemView);
            tv_add_tag_view = itemView.findViewById(R.id.tv_add_tag_view);
            tag_iv_cancle = itemView.findViewById(R.id.tag_iv_cancle);
            iv_add_tag_card_view = itemView.findViewById(R.id.iv_add_tag_card_view);

            tag_iv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagModal tagModal  = arrayList.get(getAdapterPosition());
                    arrayList.remove(tagModal.tagName);
                    StringBuilder commaSepValueBuilder = new StringBuilder();

                    for ( int i = 0; i< arrayList.size(); i++){
                        commaSepValueBuilder.append(arrayList.get(i));
                        if ( i != arrayList.size()-1){
                            commaSepValueBuilder.append(",");
                        }
                    }
                    Log.i("",commaSepValueBuilder.toString());
                    String tagComaName = commaSepValueBuilder.toString();
                    removeListItemListener.removeListItem(tagModal.tagName,tagComaName);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
