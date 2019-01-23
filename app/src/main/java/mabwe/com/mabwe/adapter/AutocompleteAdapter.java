package mabwe.com.mabwe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.AddPosttActivity;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.FilterTagModal;
import mabwe.com.mabwe.modals.HomePostModal;
import mabwe.com.mabwe.server_task.API;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.Utils;

/**
 * Created by mindiii on 18/7/18.
 */

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

    private ArrayList<FilterTagModal> filterTaglist;
    private Context context;

    public AutocompleteAdapter(Context context,ArrayList<FilterTagModal> filterTaglist) {
        this.filterTaglist = filterTaglist;
        this.context = context;
    }


    @NonNull
    @Override
    public AutocompleteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_tag_list, parent, false);
        return new AutocompleteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AutocompleteAdapter.ViewHolder holder, final int position) {
        FilterTagModal filterTagModal = filterTaglist.get(position);
        holder.tv_filtertag_item.setText(filterTagModal.tagName);
    }


    /*........................TextWatcher filter..................................*/
    public void setFilter(ArrayList<FilterTagModal> filterdNames) {
        this.filterTaglist = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filterTaglist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_filtertag_item;

        ViewHolder(View itemView) {
            super(itemView);
            tv_filtertag_item = itemView.findViewById(R.id.tv_filtertag_item);
        }
    }
}