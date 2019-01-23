package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.CommentUserModal;
import mabwe.com.mabwe.modals.GroupDetailModal;
import mabwe.com.mabwe.session.Session;

/**
 * Created by mindiii on 25/7/18.
 */

public class CommenteUserAdapter extends RecyclerView.Adapter<CommenteUserAdapter.ViewHolder> {

    private ArrayList<CommentUserModal> commenteduserlist;
    private Context context;
    private Session session;


    public CommenteUserAdapter(ArrayList<CommentUserModal> commenteduserlist, Context context) {
        this.commenteduserlist = commenteduserlist;
        this.context = context;
        session = new Session(context);
    }


    @Override
    public CommenteUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commented_user_list, parent, false);
        return new CommenteUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommenteUserAdapter.ViewHolder holder, final int position) {
        final CommentUserModal commentUserModal = commenteduserlist.get(position);
        holder.comment_user_name.setText(commentUserModal.fullname);
//        holder.comment_user_address_tv.setText(commentUserModal.country);
        if(commentUserModal.country.equals("null")){
            holder.comment_user_address_tv.setText("NA");
        }else{
            holder.comment_user_address_tv.setText(commentUserModal.country);
        }

//        holder.comment_user_date.setText(commentUserModal.Date);
        holder.description.setText(commentUserModal.comment);
        holder.comment_user_date.setText(getDayDifference(commentUserModal.Date,commentUserModal.currentDate));

        if(commentUserModal.profile_image.equals("")){

            Picasso.with(context).load(R.drawable.placeholder_img)
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.commentd_profile);

        }else{

            Picasso.with(context).load(commentUserModal.profile_image)
                    .placeholder(R.drawable.profile_image_holder)
                    .error(R.drawable.profile_image_holder)
                    .into(holder.commentd_profile);
        }

        //holder.comment_user_date.setText(parseDateToddMMyyyy(commentUserModal.Date));

    }

    @Override
    public int getItemCount() {
        return commenteduserlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment_user_name,comment_user_address_tv,comment_user_date,description;
        ImageView commentd_profile;
        View line_view;

        ViewHolder(View itemView) {
            super(itemView);
            comment_user_address_tv = itemView.findViewById(R.id.comment_user_address_tv);
            comment_user_name = itemView.findViewById(R.id.comment_user_name);
            commentd_profile = itemView.findViewById(R.id.commentd_profile);
            comment_user_date = itemView.findViewById(R.id.comment_user_date);
            description = itemView.findViewById(R.id.description);
            line_view = itemView.findViewById(R.id.line_view);

        }
    }


    //********** day diffrence  ****************//
    public static String getDayDifference(String departDateTime, String returnDateTime) {
        String returnDay = "";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(returnDateTime);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();


            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        returnDay = /*elapsedSeconds +*/ " Just now";
                    } else {
                        returnDay = elapsedMinutes + " minutes ago";
                    }
                } else if (elapsedHours == 1) {
                    returnDay = elapsedHours + " hour ago";
                } else {
                    returnDay = elapsedHours + " hours ago";
                }
            } else if (elapsedDays == 1) {
                returnDay =  /*elapsedDays + */"yesterday";
            } else {
                returnDay = elapsedDays + " days ago";
            }
        } catch (ParseException e) {
            Log.d("day diffrence", e.getMessage());
        }
        return returnDay;
    }

    public String parseDateToddMMyyyy(String time) {
//        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String inputPattern = "dd/MM/yyyy hh:mm:ss";
        String outputPattern = "dd MMMM yyyy, hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}