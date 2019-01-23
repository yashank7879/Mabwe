package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mabwe.com.mabwe.R;

/**
 * Created by mindiii on 27/6/18.
 */

public class SliderAdapter  extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;


    public SliderAdapter(Context context) {
        this.context = context;
    }

    public  int[] sliderImage = {
            R.drawable.sliderone,
            R.drawable.slidertwo,
            R.drawable.sliderthree
    };

    @Override
    public int getCount() {
        return sliderImage.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);

        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageResource(sliderImage[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
