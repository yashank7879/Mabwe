package mabwe.com.mabwe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.customListner.CuntryNameListener;
import mabwe.com.mabwe.modals.Country;

/**
 * Created by mindiii on 6/8/18.
 */

public class MyCountryAdapter extends BaseAdapter {
    private List<Country> countryArrayList;
    private Context context;
    private LayoutInflater inflter;
    private CuntryNameListener cuntryNameListener;


    public MyCountryAdapter(Context context, List<Country> countryArrayList, CuntryNameListener cuntryNameListener) {
        this.context = context;
        this.countryArrayList = countryArrayList;
        this.cuntryNameListener = cuntryNameListener;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return countryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = inflter.inflate(R.layout.country_list_item, null);
        TextView txt_new = view.findViewById(R.id.tv_country_item);
        Country country = countryArrayList.get(position);
        txt_new.setText(country.country_name);

        if (country.country_name.equals("Select Country")) {
            txt_new.setTextColor(context.getResources().getColor(R.color.light_gray));
        } else {
            txt_new.setTextColor(context.getResources().getColor(R.color.light_black));
        }

        cuntryNameListener.getCountryName(country);

        return view;
    }
}
