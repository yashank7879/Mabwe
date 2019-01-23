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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.AddPosttActivity;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.server_task.WebService;

/**
 * Created by mindiii on 4/7/18.
 */

public class CatgoryAdapter extends RecyclerView.Adapter<CatgoryAdapter.ViewHolder> {

    private ArrayList<CategoryModal> categoryModalArrayList;
    private Context context;
    private AddPosttActivity addPosttActivity;
    private CategoryClick categoryClick;


    public CatgoryAdapter(ArrayList<CategoryModal> categoryModalArrayList, Context context,CategoryClick categoryClick) {
        this.categoryModalArrayList = categoryModalArrayList;
        this.context = context;
        this.categoryClick=categoryClick;
    }


    @NonNull
    @Override
    public CatgoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catgory_list_item, parent, false);
        return new CatgoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatgoryAdapter.ViewHolder holder, final int position) {
         CategoryModal categoryModal = categoryModalArrayList.get(position);
         holder.tv_catgory_item.setText(categoryModal.categoryName);

    }

    @Override
    public int getItemCount() {
        return categoryModalArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_catgory_item;
        LinearLayout linear;

        ViewHolder(View itemView) {
            super(itemView);
            tv_catgory_item = itemView.findViewById(R.id.tv_catgory_item);
            linear = itemView.findViewById(R.id.linear);

            linear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.linear:
                    categoryClick.getCategoryClick(getAdapterPosition());
                    break;
            }
        }
    }
}
