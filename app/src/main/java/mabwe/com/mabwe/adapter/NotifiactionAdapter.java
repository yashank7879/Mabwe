package mabwe.com.mabwe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.modals.NotificationModal;
import mabwe.com.mabwe.session.Session;


public class NotifiactionAdapter extends RecyclerView.Adapter<NotifiactionAdapter.ViewHolder> {

    private ArrayList<NotificationModal> notificationList;
    private Context context;
    private Session session;
    private SpannableString Commented, on_your, Liked;
    private NotificationModal notificationModal;
    private Listner listner;

    //listener for edit delete on menu
    public interface Listner {
        void clickNotificationItem(NotificationModal item, int position);
    }

    public NotifiactionAdapter(ArrayList<NotificationModal> notificationList, Context context, Listner listner) {
        this.notificationList = notificationList;
        this.context = context;
        this.listner = listner;
        session = new Session(context);
    }

    @NonNull
    @Override
    public NotifiactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificati_list_item, parent, false);
        return new NotifiactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotifiactionAdapter.ViewHolder holder, final int position) {
        notificationModal = notificationList.get(position);

        if (notificationModal.is_read.equals("0")) {

            holder.notification_read.setVisibility(View.VISIBLE);

        } else {
            holder.notification_read.setVisibility(View.GONE);
        }


        Spannable name = new SpannableString(notificationModal.fullName);
        name.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.notificatio_name_with_discription.setText(name);
        Spannable like = new SpannableString(" Liked ");
        like.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, like.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.notificatio_name_with_discription.append(like);

        holder.notification_time.setText(getDayDifference(notificationModal.crd, notificationModal.CurrentTime));
      /*  Spannable like = new SpannableString(" Liked ");
        like.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, like.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.notificatio_name_with_discription.append(like);*/

        holder.notificatio_name_with_discription.setText(name);
        switch (notificationModal.notification_type) {
            case "mabwe_groupComment": {

                Spannable Commented = new SpannableString(" Commented ");
                Commented.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, Commented.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.notificatio_name_with_discription.append(Commented);

                Spannable on_your = new SpannableString(" on your " + notificationModal.title + " group");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);

                break;
            }
            case "mabwe_groupLike": {

                Spannable Liked = new SpannableString(" Liked ");
                Liked.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, Liked.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.notificatio_name_with_discription.append(Liked);

                Spannable on_your = new SpannableString(" your " + notificationModal.title + " group");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);

                break;

            }
            case "mabwe_comment":

                Commented = new SpannableString(" Commented ");
                Commented.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, Commented.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.notificatio_name_with_discription.append(Commented);

                on_your = new SpannableString(" on your " + notificationModal.title + " post");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);

                break;
            case "mabwe_Like":

                Liked = new SpannableString(" Liked ");
                Liked.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, Liked.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.notificatio_name_with_discription.append(Liked);
                on_your = new SpannableString(" your  " + notificationModal.title + " post");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);
                break;
            case "connection_request":
                on_your = new SpannableString(" wants to connect with you.");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);
                break;
            case "connection_accept":
                on_your = new SpannableString(" accepted your connection request.");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);
                break;
            case "connection_declined":
                on_your = new SpannableString(" decline your connection request.");
                on_your.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.light_gray)), 0, on_your.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.notificatio_name_with_discription.append(on_your);
                break;


        }

        if (notificationModal.profileImage.equals("http://dev.mabwe.com/uploads/profile/") || notificationModal.profileImage.equals("https://www.mabwe.com/uploads/profile/")) {
            Picasso.with(context).load(notificationModal.placeholderImg).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(holder.notification_profile);
        } else {
            Picasso.with(context).load(notificationModal.profileImage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(holder.notification_profile);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notificatio_name_with_discription, notification_time;
        ImageView notification_profile, notification_read;

        ViewHolder(View itemView) {
            super(itemView);
            notificatio_name_with_discription = itemView.findViewById(R.id.notificatio_name_with_discription);
            notification_profile = itemView.findViewById(R.id.notification_profile);
            notification_time = itemView.findViewById(R.id.notification_time);
            notification_read = itemView.findViewById(R.id.notification_read);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            notificationModal = notificationList.get(getAdapterPosition());
            listner.clickNotificationItem(notificationModal, getAdapterPosition());

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
}