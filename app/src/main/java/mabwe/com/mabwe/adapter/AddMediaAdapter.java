package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.List;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.AddPosttActivity;
import mabwe.com.mabwe.activity.EditPostActivity;
import mabwe.com.mabwe.customListner.GetImageFromGallaryLIstener;
import mabwe.com.mabwe.modals.ImageBean;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

/**
 * Created by mindiii on 2/7/18.
 */

public class AddMediaAdapter extends RecyclerView.Adapter<AddMediaAdapter.ViewHolder> {
   private List<ImageBean> list;
    private Context context;
    private GetImageFromGallaryLIstener listener;

    public AddMediaAdapter(List<ImageBean> list, Context context, GetImageFromGallaryLIstener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item_view, parent, false);
        return new AddMediaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddMediaAdapter.ViewHolder holder, final int position) {


        if (position == 0) {
            holder.media_remove_iv.setVisibility(View.GONE);
            holder.media_view_iv.setImageResource(R.drawable.img_ico);
        } else if (position > 0) {

            ImageBean imageBean = list.get(position);
            holder.media_remove_iv.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(imageBean.url)) {

                Glide.with(holder.media_view_iv.getContext()).load(imageBean.url).into(holder.media_view_iv);
                holder.media_view_iv.setBorderColor(context.getResources().getColor(R.color.light_gray));
                holder.media_view_iv.setBorderWidth(5);

            } else if (imageBean.bitmap != null) {

                holder.media_view_iv.setBorderColor(context.getResources().getColor(R.color.colorPrimary));
                holder.media_view_iv.setBorderWidth(3);
                holder.media_view_iv.setImageBitmap(imageBean.bitmap);
            }

            holder.media_view_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = holder.getAdapterPosition();

                    if (!list.get(pos).imageId.equals("")) {
//                        deleteImages(list.get(pos).imageId);
                    }

                    if (list.size() > pos) {

                        list.remove(pos);

                        notifyItemRemoved(pos);
                    }
                }
            });
        }

        holder.media_view_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.size() >= 6) {
                    if (holder.getAdapterPosition() == 0)
                        Utils.openAlertDialog(context, context.getString(R.string.image_selection_limite_msg));
                } else listener.getImagePosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView media_remove_iv;
        RoundedImageView media_view_iv;

        ViewHolder(View itemView) {
            super(itemView);
            media_remove_iv = itemView.findViewById(R.id.media_remove_iv);
            media_view_iv = itemView.findViewById(R.id.media_view_iv);
            media_remove_iv.setOnClickListener(this);
            //itemView.setOnClickListener(this);
            //media_remove_iv.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.media_remove_iv:

                    if (Utils.isNetworkAvailable(context)) {
                        try {
                            //Api calling
                            if (list.get(getAdapterPosition()).isVideoUrl || !list.get(getAdapterPosition()).imageId.isEmpty()) {
                                listener.removePostImage(list.get(getAdapterPosition()), getAdapterPosition(), list.get(getAdapterPosition()).isVideoUrl);
                                int pos = getAdapterPosition();
                                list.remove(pos);
                                if (list.size() == 1) AddPosttActivity.imagevideoSel = 0;
                                notifyItemRemoved(pos);
                            }else {
                              //  listener.removePostImage(list.get(getAdapterPosition()), getAdapterPosition(), list.get(getAdapterPosition()).isVideoUrl);
                                int pos = getAdapterPosition();
                                list.remove(pos);
                                if (list.size() == 1) AddPosttActivity.imagevideoSel = 0;
                                notifyItemRemoved(pos);
                            }
                        } catch (NullPointerException e) {
                            showToast(context, context.getString(R.string.too_slow));
                        }
                    } else showToast(context, context.getString(R.string.no_internet_access));

                    break;

            }
        }
    }
}
