package mabwe.com.mabwe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.AddPosttActivity;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.modals.CategoryModal;

/**
 * Created by mindiii on 4/7/18.
 */

public class DynamicCatgoryAdapter extends RecyclerView.Adapter<DynamicCatgoryAdapter.ViewHolder> {

    private ArrayList<CategoryModal> categoryModalArrayList;
    private Context context;
    private AddPosttActivity addPosttActivity;
    private CategoryClick categoryClick;


    public DynamicCatgoryAdapter(ArrayList<CategoryModal> categoryModalArrayList, Context context, CategoryClick categoryClick) {
        this.categoryModalArrayList = categoryModalArrayList;
        this.context = context;
        this.categoryClick = categoryClick;
    }


    @NonNull
    @Override
    public DynamicCatgoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_catgory_list_item, parent, false);

        return new DynamicCatgoryAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final DynamicCatgoryAdapter.ViewHolder holder, final int position) {
        CategoryModal categoryModal = categoryModalArrayList.get(position);

        if (categoryModal.isSelected){
             holder.category_iv_right.setVisibility(View.VISIBLE);
        } else {
            holder.category_iv_right.setVisibility(View.GONE);
        }
        if(categoryModal.categoryName.equals("Marketplace"))
            holder.tv_catgory_item.setText(" ");
        else holder.tv_catgory_item.setText(categoryModal.categoryName);
        holder.tv_catgory_item.setTextColor(Color.parseColor("#ff7701"));

        Picasso.with(context).load(categoryModal.categoryImage).into(holder.category_iv);
    }

    @Override
    public int getItemCount() {
        return categoryModalArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_catgory_item;
        ImageView category_iv,category_iv_right;
        LinearLayout linear;

        ViewHolder(View itemView) {
            super(itemView);
            tv_catgory_item = itemView.findViewById(R.id.tv_catgory_item);
            category_iv = itemView.findViewById(R.id.category_iv);
            category_iv_right= itemView.findViewById(R.id.category_iv_right);
            linear = itemView.findViewById(R.id.linear);

             linear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CategoryModal categoryModal = categoryModalArrayList.get(getAdapterPosition());
            if(categoryModal.isSelected) categoryModal.isSelected=false;
            else categoryModal.isSelected=true;

             categoryClick.getCategoryClick(getAdapterPosition());
             notifyDataSetChanged();



        }
    }
}


