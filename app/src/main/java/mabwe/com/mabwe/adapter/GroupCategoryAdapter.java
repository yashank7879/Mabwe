package mabwe.com.mabwe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.GroupActivity;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.modals.CategoryModal;

/**
 * Created by mindiii on 23/7/18.
 */

public class GroupCategoryAdapter  extends RecyclerView.Adapter<GroupCategoryAdapter.ViewHolder> {

    private ArrayList<CategoryModal> groupModalArrayList;
    private GroupActivity groupActivity;
    private CategoryClick categoryClick;

    public GroupCategoryAdapter(ArrayList<CategoryModal> groupModalArrayList, GroupActivity groupActivity,CategoryClick categoryClick) {
        this.groupModalArrayList = groupModalArrayList;
        this.groupActivity = groupActivity;
        this.categoryClick = categoryClick;
    }


    @Override
    public GroupCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catgory_list_item, parent, false);
        return new GroupCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupCategoryAdapter.ViewHolder holder, final int position) {
        CategoryModal categoryModal = groupModalArrayList.get(position);
        holder.tv_catgory_item.setText(categoryModal.categoryName);
    }

    @Override
    public int getItemCount() {
        return groupModalArrayList.size();
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
