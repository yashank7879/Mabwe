package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.DetailsActivity;
import mabwe.com.mabwe.customListner.ViewPagerListener;
import mabwe.com.mabwe.modals.DetailsModal;

/**
 * Created by mindiii on 17/7/18.
 */

public class DetailsSliderAdapter extends PagerAdapter {

    ArrayList<DetailsModal> detailsModals;
    private Context context;
    private LayoutInflater layoutInflater;
    private ViewPagerListener viewPagerListener;


    public DetailsSliderAdapter(Context context, ArrayList<DetailsModal> detailsModals, ViewPagerListener viewPagerListener) {
        this.context = context;
        this.detailsModals = detailsModals;
        this.viewPagerListener = viewPagerListener;
    }

    @Override
    public int getCount() {
        return detailsModals.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.detail_slider, container, false);
        final DetailsModal detailsModal = detailsModals.get(position);

        ImageView imageView = view.findViewById(R.id.imagee);
        LinearLayout whole_view = view.findViewById(R.id.whole_view);


        if (detailsModal.video_thumb.length() == 0) {

            if (!detailsModal.post_attachment.equalsIgnoreCase("")) {
                Picasso.with(context).load(detailsModal.post_attachment).fit().centerCrop()
                        .placeholder(R.drawable.myplaceholder)
                        .error(R.drawable.myplaceholder)
                        .into(imageView);
            }

        } else {

            Picasso.with(context).load(detailsModal.video_thumb).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(imageView);
        }


       /* Picasso.with(context).load(detailsModal.post_attachment).fit().centerCrop()
                .placeholder(R.drawable.myplaceholder)
                .error(R.drawable.myplaceholder)
                .into(imageView);*/

        whole_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerListener.getImagePosition(position);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
