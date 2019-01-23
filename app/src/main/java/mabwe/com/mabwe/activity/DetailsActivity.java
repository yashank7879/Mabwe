package mabwe.com.mabwe.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.DetailTagAdapter;
import mabwe.com.mabwe.adapter.DetailsSliderAdapter;
import mabwe.com.mabwe.customListner.ViewPagerListener;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.DetailsModal;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.PermissionAll;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.ZoomOutPageTransformer;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    int dotsCount;
    ArrayList<DetailsModal> arrayList = new ArrayList<>();
    DetailsModal detailsModal;
    private TextView details_category_name_tv, details_day_tv, details_description;
    private TextView details_name, details_title_tv, detail_address_tv, tv__details_like_count, tv_details_comment_count, details_contact_info, details_contact_info_text, details_general_info;
    private ImageView details_profile_ImageView, iv_details_back, coment_iv_details, iv_share, iv_edit_post;
    private DetailTagAdapter detailTagAdapter;
    private ArrayList<DetailsModal> detailstaglist;
    private ViewPager viewPager;
    private DetailsSliderAdapter detailsSliderAdapter;
    private ImageView[] dotes;
    private LinearLayout details_linear_layout;
    private String id;
    private Dialog mdialog;
    private ImageView detail_like_iv, iv_delete_post;
    private LinearLayout details_coment_view, details_like_view;
    private ProgressBar progressBar_detail;
    private ProgresDilog progresDilog;
    StringBuilder stBuilder;
    ImageView img_playicon;
    LinearLayout userinfo_lay;
    private TextView details_hired_sold;
    private NestedScrollView bottom_sheet;
    private TextView tv_no_more_exist;
    private LinearLayout llEditDelete;
    private CardView upperViewIv;
    private LinearLayout details_report;
    private String name ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        PermissionAll allPermission = new PermissionAll();
        allPermission.RequestMultiplePermission(this);
        findItemViewId();
    }

    /*............................find_Item_View_Id............................*/
    private void findItemViewId() {

        progresDilog = new ProgresDilog(this);
        detailsModal = new DetailsModal();
        Bundle bundle = getIntent().getExtras();

        if (getIntent().getStringExtra("postId") != null) {
            id = getIntent().getStringExtra("postId");
        } else {
            assert bundle != null;
            if (bundle.getString("notify_id") != null) {
                id = bundle.getString("notify_id");
            }
        }

        detailstaglist = new ArrayList<>();
        RecyclerView details_tag_recycler_view = findViewById(R.id.details_tag_recycler_view);
        detailTagAdapter = new DetailTagAdapter(detailstaglist, this);
        details_tag_recycler_view.setAdapter(detailTagAdapter);

        details_report = findViewById(R.id.details_report);
        userinfo_lay = findViewById(R.id.userinfo_lay);
        img_playicon = findViewById(R.id.img_playicon);
        details_category_name_tv = findViewById(R.id.details_category_name_tv);
        details_day_tv = findViewById(R.id.details_day_tv);
        details_description = findViewById(R.id.details_description);
        details_title_tv = findViewById(R.id.details_title_tv);
        detail_address_tv = findViewById(R.id.detail_address_tv);
        details_profile_ImageView = findViewById(R.id.details_profile_ImageView);
        details_name = findViewById(R.id.details_posted_name);
        iv_details_back = findViewById(R.id.iv_details_back);
        tv__details_like_count = findViewById(R.id.tv__details_like_count);
        tv_no_more_exist = findViewById(R.id.tv_no_more_exist);
        tv_details_comment_count = findViewById(R.id.tv_details_comment_count);
        details_like_view = findViewById(R.id.details_like_view);
        coment_iv_details = findViewById(R.id.coment_iv_details);
        details_coment_view = findViewById(R.id.details_coment_view);
        detail_like_iv = findViewById(R.id.detail_like_iv);
        details_contact_info = findViewById(R.id.details_contact_info);
        details_hired_sold = findViewById(R.id.details_hired_sold);
        details_contact_info_text = findViewById(R.id.details_contact_info_text);
        details_general_info = findViewById(R.id.details_general_info);
        progressBar_detail = findViewById(R.id.progressBar_detail);
        iv_share = findViewById(R.id.iv_share);
        iv_edit_post = findViewById(R.id.iv_edit_post);
        iv_delete_post = findViewById(R.id.iv_delete_post);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        llEditDelete = findViewById(R.id.ll_edit_delete);
        upperViewIv = findViewById(R.id.upper_view_iv);

        viewPager = findViewById(R.id.detail_slider_pager);
        details_linear_layout = findViewById(R.id.details_linear_layout);
        setOnclickListener();
        getDetails_api();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails_api();
    }

    private void setdata() {


        name  = detailsModal.fullname;
        if (detailsModal.post_video.length() != 0) {
            iv_share.setVisibility(View.GONE);

            img_playicon.setVisibility(View.VISIBLE);
        } else {
            iv_share.setVisibility(View.VISIBLE);

            img_playicon.setVisibility(View.GONE);
        }

        if (detailsModal.is_create.equals("1")) {
            iv_edit_post.setVisibility(View.VISIBLE);
            iv_delete_post.setVisibility(View.VISIBLE);

        } else {
            iv_edit_post.setVisibility(View.GONE);
            iv_delete_post.setVisibility(View.GONE);

        }

        // Toast.makeText(this, ""+detailsModal.tagName, Toast.LENGTH_SHORT).show();
        details_category_name_tv.setText(detailsModal.categoryName);

        if (detailsModal.address.isEmpty()) detail_address_tv.setText("NA");
        else detail_address_tv.setText(detailsModal.address);
        tv__details_like_count.setText(detailsModal.like_count);
        tv_details_comment_count.setText(detailsModal.Comment_count);

        details_description.setText(detailsModal.description);

        //for post detail
        stBuilder = new StringBuilder();
        if (!detailsModal.authorisedToWork.equals("0") || !detailsModal.whilingToship.equals("0") || !detailsModal.willingToRelocate.equals("0")) {
            details_general_info.setVisibility(View.VISIBLE);
            //StringBuilder stBuilder = new StringBuilder();
            if (!detailsModal.whilingToship.equals("0")) {
                stBuilder.append("Willing to ship - Yes");
            } else if (!detailsModal.authorisedToWork.equals("0") && !detailsModal.willingToRelocate.equals("0")) {
                stBuilder.append("Legally authorized to work in this country - Yes\nWilling to relocate - Yes");
            } else if (!detailsModal.authorisedToWork.equals("1") && !detailsModal.willingToRelocate.equals("0")) {
                stBuilder.append("Legally authorized to work in this country - No\nWilling to relocate - Yes");
            } else if (!detailsModal.authorisedToWork.equals("0") && !detailsModal.willingToRelocate.equals("1")) {
                stBuilder.append("Legally authorized to work in this country - Yes\nWilling to relocate - No");
            }
            details_general_info.setText(stBuilder.toString());
        } else {

            //when no show in case of no select
            if (detailsModal.categoryName.equals("Marketplace")) {
                details_general_info.setVisibility(View.VISIBLE);
                stBuilder.append("Willing to ship - No");
                details_general_info.setText(stBuilder.toString());
            } else if (detailsModal.categoryName.equals("Career")) {
                details_general_info.setVisibility(View.VISIBLE);
                stBuilder.append("Legally authorized to work in this country - No\nWilling to relocate - No");
                details_general_info.setText(stBuilder.toString());
            } else details_general_info.setVisibility(View.GONE);

        }

        /*show available sold hired and still searching*/
        switch (detailsModal.category_id) {
            case "2": // career :
                details_hired_sold.setVisibility(View.VISIBLE);
                if (detailsModal.isHired.equals("0")) {
                    details_hired_sold.setText(R.string.still_seraching_yes);
                } else {
                    details_hired_sold.setText(R.string.hired_yes);
                }
                break;
            case "3": // Market place
                details_hired_sold.setVisibility(View.VISIBLE);
                if (detailsModal.availability.equals("0")) {
                    details_hired_sold.setText(R.string.available_yes);
                } else {
                    details_hired_sold.setText(R.string.sold_yes);
                }
                break;
            default:
                details_hired_sold.setVisibility(View.GONE);
                break;
        }

        if (!detailsModal.email.equals("") || !detailsModal.contact.equals("")) {
            details_contact_info_text.setVisibility(View.VISIBLE);
            details_contact_info.setVisibility(View.VISIBLE);
            details_contact_info_text.setText("" + detailsModal.email + "\n" + detailsModal.contact);
        } else {
            details_contact_info.setVisibility(View.GONE);
            details_contact_info_text.setVisibility(View.GONE);
        }


        details_name.setText(detailsModal.fullname);
        details_title_tv.setText(detailsModal.title);

        if (detailsModal.profileImage != null) {
            Picasso.with(this).load(detailsModal.profileImage)
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(details_profile_ImageView);
        } else {
            Picasso.with(this).load(R.drawable.profile_image_holder)
                    .placeholder(R.drawable.profile_image_holder)
                    .error(R.drawable.profile_image_holder)
                    .into(details_profile_ImageView);
        }

        if (detailsModal.user_like_status.equals("1")) {
            detail_like_iv.setImageResource(R.drawable.heart);
        } else {
            detail_like_iv.setImageResource(R.drawable.inactive_like_ico);
        }

        details_day_tv.setText(getDayDifference(detailsModal.crd, detailsModal.CurrentTime));
    }

    private void setOnclickListener() {
        details_report.setOnClickListener(this);
        iv_details_back.setOnClickListener(this);
        coment_iv_details.setOnClickListener(this);
        details_like_view.setOnClickListener(this);
        details_coment_view.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_edit_post.setOnClickListener(this);
        iv_delete_post.setOnClickListener(this);
        userinfo_lay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_details_back:
                onBackPressed();
                break;

            case R.id.details_report:
                Intent reportIntent = new Intent(this, ReportActivity.class);
                reportIntent.putExtra("reportUser", name);
                startActivityForResult(reportIntent, 2);
                break;

            case R.id.details_like_view:
                like_Api(id);
                break;


            case R.id.details_coment_view:
                intent = new Intent(this, CommentActivity.class);
                intent.putExtra("post_Id", detailsModal.postId);
                startActivity(intent);
                break;
            case R.id.iv_edit_post:
                intent = new Intent(this, EditPostActivity.class);
                intent.putExtra("Postdata", detailsModal);
                intent.putExtra("PostTagdata", detailstaglist);
                intent.putExtra("PostAttachdata", arrayList);
                startActivity(intent);
                break;

            case R.id.iv_share:

                if (detailsModal != null) {
                    DetailsModal detailsModalViewPager = arrayList.get(viewPager.getCurrentItem());
                    shareDetailsDialog(detailsModal, detailsModalViewPager.post_attachment);
                }
                break;
            case R.id.iv_delete_post:
                deletePostDialog();
                break;

            case R.id.userinfo_lay:
                Intent profile_intent = new Intent(this, Activity_profile.class);
                profile_intent.putExtra("userid", detailsModal.user_id);
                profile_intent.putExtra("screenFlag", "fromDetails");
                startActivity(profile_intent);
                break;
        }
    }

    private void deletePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.mabwe);
        builder.setMessage("Do you want to delete this post?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePostApi();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //"""""""""""" if user created this post so user can delete post """""""""""""//
    private void deletePostApi() {
        if (Utils.isNetworkAvailable(getApplication())) {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("postId", detailsModal.postId);

                mdialog = Mabwe.showProgress(this);

                WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        Mabwe.hideProgress(mdialog);
                        Log.e("delete Post", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                onBackPressed();
                            } else {
                                Utils.openAlertDialog(DetailsActivity.this, message);
                                Mabwe.hideProgress(mdialog);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Mabwe.hideProgress(mdialog);
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        Mabwe.hideProgress(mdialog);
                    }
                });
                service.callSimpleVolley("user/deletePost", map);
            } catch (NullPointerException e) {
                showToast(DetailsActivity.this, getString(R.string.too_slow));
            }
        } else showToast(DetailsActivity.this, getString(R.string.no_internet_access));

    }

    @SuppressLint("SetTextI18n")
    private void shareDetailsDialog(DetailsModal detailsModal, String postimage) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(350, WindowManager.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_share);


        TextView catogary_share = dialog.findViewById(R.id.catogary_share);
        TextView details_title_tv_share = dialog.findViewById(R.id.details_title_tv_share);
        TextView details_day_tv_share = dialog.findViewById(R.id.details_day_tv_share);
        TextView details_description_share = dialog.findViewById(R.id.details_description_share);
        TextView details_general_info_share = dialog.findViewById(R.id.details_general_info_share);
        TextView details_contact_info_share = dialog.findViewById(R.id.details_contact_info_share);
        TextView tv_share_hired_sold = dialog.findViewById(R.id.tv_share_hired_sold);
        TextView tv_Tag_Detail_share = dialog.findViewById(R.id.tv_Tag_Detail_share);
        ImageView img_place_holder_share = dialog.findViewById(R.id.img_place_holder_share);
        TextView details_contact_info_text_share = dialog.findViewById(R.id.details_contact_info_text_share);
        CardView card_for_share = dialog.findViewById(R.id.card_for_share);
        CardView card_for_close = dialog.findViewById(R.id.card_for_close);
        final RelativeLayout share_view = dialog.findViewById(R.id.share_view);
        //RecyclerView details_tag_recycler_view_share = dialog.findViewById(R.id.details_tag_recycler_view_share);


        catogary_share.setText(detailsModal.categoryName);
        //details_day_tv_share.setText(getDayDifference(detailsModal.crd, detailsModal.CurrentTime));
        details_title_tv_share.setText(detailsModal.title);
        details_description_share.setText(detailsModal.description);
        details_contact_info_text_share.setText(detailsModal.contact);

        //cama seprator tag name set on textview on share dialog
        StringBuilder tagBuilder = new StringBuilder();
        for (int i = 0; i < detailstaglist.size(); i++) {
            if (i == 0) {
                tagBuilder.append(detailstaglist.get(i).tagName);
            } else {
                tagBuilder.append(", ").append(detailstaglist.get(i).tagName);
            }
            tv_Tag_Detail_share.setText(tagBuilder);
            tv_Tag_Detail_share.setTextColor(Color.parseColor("#ff7701"));
        }


        if (!detailsModal.email.equals("") || !detailsModal.contact.equals("")) {
            details_contact_info_share.setVisibility(View.VISIBLE);
            details_contact_info_text_share.setVisibility(View.VISIBLE);
            details_contact_info_text_share.setText("" + detailsModal.email + "\n" + detailsModal.contact);
        } else {
            details_contact_info_share.setVisibility(View.GONE);
            details_contact_info_text_share.setVisibility(View.GONE);
        }

         /*show available sold hired and still searching*/
        switch (detailsModal.category_id) {
            case "2": // career :
                tv_share_hired_sold.setVisibility(View.VISIBLE);
                if (detailsModal.isHired.equals("0")) {
                    tv_share_hired_sold.setText(R.string.still_seraching_yes);
                } else {
                    tv_share_hired_sold.setText(R.string.hired_yes);
                }
                break;
            case "3": // Market place
                tv_share_hired_sold.setVisibility(View.VISIBLE);
                if (detailsModal.availability.equals("0")) {
                    tv_share_hired_sold.setText(R.string.available_yes);
                } else {
                    tv_share_hired_sold.setText(R.string.sold_yes);
                }
                break;
            default:
                tv_share_hired_sold.setVisibility(View.GONE);
                break;
        }

        //for dialog
        stBuilder = new StringBuilder();
        if (!detailsModal.authorisedToWork.equals("0") || !detailsModal.whilingToship.equals("0") || !detailsModal.willingToRelocate.equals("0")) {
            details_general_info_share.setVisibility(View.VISIBLE);
            if (!detailsModal.whilingToship.equals("0")) {
                stBuilder.append("Willing to ship - Yes");
            } else if (!detailsModal.authorisedToWork.equals("0") && !detailsModal.willingToRelocate.equals("0")) {
                stBuilder.append("Legally authorized to work in this country - Yes\nWilling to relocate - Yes");
            } else if (!detailsModal.authorisedToWork.equals("1") && !detailsModal.willingToRelocate.equals("0")) {
                stBuilder.append("Legally authorized to work in this country - No\nWilling to relocate - Yes");
            } else if (!detailsModal.authorisedToWork.equals("0") && !detailsModal.willingToRelocate.equals("1")) {
                stBuilder.append("Legally authorized to work in this country - Yes\nWilling to relocate - No");
            }
            details_general_info_share.setText(stBuilder.toString());
        } else {

            //when no show in case of no select
            if (detailsModal.categoryName.equals("Marketplace")) {
                details_general_info_share.setVisibility(View.VISIBLE);
                stBuilder.append("Willing to ship - No");
                details_general_info_share.setText(stBuilder.toString());
            } else if (detailsModal.categoryName.equals("Career")) {
                details_general_info_share.setVisibility(View.VISIBLE);
                stBuilder.append("Legally authorized to work in this country - No\nWilling to relocate - No");
                details_general_info_share.setText(stBuilder.toString());
            } else details_general_info_share.setVisibility(View.GONE);

        }

        if (postimage.length() != 0) {
            Picasso.with(this).load(postimage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(img_place_holder_share);
        }


        card_for_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                screenShot(share_view);

            }
        });

        card_for_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //*.................................screenShot()...................................*//
    private void screenShot(RelativeLayout share_view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        File f = new File(Environment.getExternalStorageDirectory(), "/Mabwe/Shared Profiles");
        if (!f.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory(), "/Mabwe/Shared Profiles");
            wallpaperDirectory.mkdirs();
        }

        try {
            String mPath = f.getAbsoluteFile() + "/" + now + ".png";
            share_view.setDrawingCacheEnabled(true);
            share_view.buildDrawingCache(true);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = Bitmap.createBitmap(share_view.getDrawingCache());
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
            share_view.destroyDrawingCache();
            //onShareClick
            sharOnsocial(imageFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }





       /* try {
          *//*  File f = new File(Environment.getExternalStorageDirectory(), "/Mabwe/Shared Profiles");
            if (!f.exists()) {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory(),"/Mabwe/Shared Profiles");
                wallpaperDirectory.mkdirs();
            }*//*
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";

           // String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".png";
            share_view.setDrawingCacheEnabled(true);
            share_view.buildDrawingCache(true);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = Bitmap.createBitmap(share_view.getDrawingCache());
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
            share_view.destroyDrawingCache();
            sharOnsocial(imageFile);
            //onShareClick(imageFile,text);
            //doShareLink(text,otherProfileInfo.UserDetail.profileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error343", "" + e.toString());
        }*/
    }

   /* private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }*/

    private void sharOnsocial(File imageFile) {
        Uri uri;
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", imageFile);
            sharIntent.setDataAndType(uri, type);
        } else {
            uri = Uri.fromFile(imageFile);
            sharIntent.setDataAndType(uri, type);
        }

        sharIntent.setType("image/png");
        //sharIntent.setType("text/plain");
        sharIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharIntent.putExtra(Intent.EXTRA_SUBJECT, "Mabwe");
        //sharIntent.putExtra(Intent.EXTRA_TEXT, );
        startActivity(Intent.createChooser(sharIntent, "Share:"));


    }

    /*.....................................getDetails_api.........................................*/
    private void getDetails_api() {
        progresDilog.show();
        //  progressBar_detail.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("post_id", id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                // progressBar_detail.setVisibility(View.GONE);
                progresDilog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");


                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        detailstaglist.clear();
                        arrayList.clear();

                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject2 = (JSONObject) data.get(i);
                            detailsModal.user_id = jsonObject2.getString("user_id");
                            detailsModal.postId = jsonObject2.getString("postId");
                            detailsModal.title = jsonObject2.getString("title");
                            detailsModal.post_video = jsonObject2.getString("video");
                            detailsModal.video_thumb = jsonObject2.getString("video_thumb");
                            detailsModal.description = jsonObject2.getString("description");
                            detailsModal.latitude = jsonObject2.getString("latitude");
                            detailsModal.longitude = jsonObject2.getString("longitude");
                            // detailsModal.country = jsonObject2.getString("country");
                            //  detailsModal.state = jsonObject2.getString("state");
                            //   detailsModal.city = jsonObject2.getString("city");
                            detailsModal.address = jsonObject2.getString("address");
                            //   detailsModal.crd = jsonObject2.getString("crd");
                            //     detailsModal.upd = jsonObject2.getString("upd");
                            // detailsModal.CurrentTime = jsonObject2.getString("CurrentTime");
                            detailsModal.categoryName = jsonObject2.getString("categoryName");
                            detailsModal.category_id = jsonObject2.getString("category_id");
                            detailsModal.fullname = jsonObject2.getString("fullname");
                            detailsModal.profileImage = jsonObject2.getString("profile_image");
                            detailsModal.likeId = jsonObject2.getString("likeId");
                            detailsModal.like_count = jsonObject2.getString("like_count");
                            detailsModal.user_like_status = jsonObject2.getString("user_like_status");
                            detailsModal.commentId = jsonObject2.getString("commentId");
                            detailsModal.Comment_count = jsonObject2.getString("comment_count");
                            detailsModal.authorisedToWork = jsonObject2.getString("authorisedToWork");
                            detailsModal.willingToRelocate = jsonObject2.getString("willingToRelocate");
                            detailsModal.whilingToship = jsonObject2.getString("whilingToship");
                            detailsModal.email = jsonObject2.getString("email");
                            detailsModal.contact = jsonObject2.getString("contact");
                            detailsModal.is_create = jsonObject2.getString("is_create");
                            detailsModal.availability = jsonObject2.getString("availability");
                            detailsModal.isHired = jsonObject2.getString("isHired");

                            Object onj = jsonObject2.get("postimage");
                            if (onj instanceof JSONArray) {
                                JSONArray postImage = jsonObject2.getJSONArray("postimage");
                                if (detailsModal.post_video.isEmpty()) {
                                    for (int j = 0; j < postImage.length(); j++) {
                                        JSONObject jsonObject1 = (JSONObject) postImage.get(j);
                                        DetailsModal detailsModal = new DetailsModal();

                                        detailsModal.post_attachment = jsonObject1.getString("post_attachment");
                                        detailsModal.postImagesId = jsonObject1.getString("attchmentId");

                                        arrayList.add(detailsModal);
                                    }
                                } else {
                                    detailsModal.post_attachment = jsonObject2.getString("video_thumb");

                                    arrayList.add(detailsModal);
                                }
                            } else {

                                detailsModal.post_attachment = jsonObject2.getString("video_thumb");
                                arrayList.add(detailsModal);
                            }

                            JSONArray tags = jsonObject2.getJSONArray("tags");
                            for (int t = 0; t < tags.length(); t++) {
                                JSONObject jsonObject1 = (JSONObject) tags.get(t);
                                DetailsModal detailsModal = new DetailsModal();
                                detailsModal.tag_id = jsonObject1.getString("tag_id");
                                detailsModal.tagName = jsonObject1.getString("tagName");
                                //detailstaglist is empty
                                if (!detailsModal.tagName.isEmpty()) {
                                    detailstaglist.add(detailsModal);
                                }

                                detailTagAdapter.notifyDataSetChanged();
                            }

                            detailsSliderAdapter = new DetailsSliderAdapter(DetailsActivity.this, arrayList, new ViewPagerListener() {
                                @Override
                                public void getImagePosition(int position) {

                                    DetailsModal detailsModal1 = arrayList.get(position);
                                    if (detailsModal.post_video.length() == 0) {
                                        listDilog(this, detailsModal1.post_attachment);
                                    } else {
                                        Intent intent = new Intent(DetailsActivity.this, VideoViewActivtiy.class);
                                        intent.putExtra("setVideoURI", detailsModal.post_video);
                                        startActivity(intent);
                                    }
                                }
                            });

                            viewPager.setAdapter(detailsSliderAdapter);
                            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                            viewPager.setCurrentItem(0);
                            setUiPageViewController();
                            viewPager.setOnPageChangeListener(DetailsActivity.this);
                            setdata();
                        }

                    } else {
                        // progressBar_detail.setVisibility(View.VISIBLE);
                        progresDilog.dismiss();
                        if (message.equals("This post does not exist")) {
                            upperViewIv.setVisibility(View.GONE);
                            bottom_sheet.setVisibility(View.GONE);
                            llEditDelete.setVisibility(View.GONE);
                            tv_no_more_exist.setVisibility(View.VISIBLE);
                        }

                        Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        });
        service.callSimpleVolley("/user/postDetail", map);
    }

    /*..............................................................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*.................................................................*/
    private void setUiPageViewController() {

        details_linear_layout.removeAllViews();
        dotsCount = detailsSliderAdapter.getCount();
        dotes = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dotes[i] = new ImageView(DetailsActivity.this);
            dotes[i].setImageResource(R.drawable.inactive_points_ico);

            details_linear_layout.addView(dotes[i]);
        }
        if (dotes.length == 1) {
            details_linear_layout.setVisibility(View.GONE);
        }
        dotes[0].setImageResource(R.drawable.active_point_ico);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        for (int i = 0; i < dotsCount; i++) {
            dotes[i].setImageResource(R.drawable.inactive_points_ico);
        }
        dotes[position].setImageResource(R.drawable.active_point_ico);
        if (position + 1 == dotsCount) {
            //  btnFinish.setVisibility(View.VISIBLE);
        } else if (position == 0) {
        } else {
        }
        if (dotes.length == 1) {
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dotes[i].setImageResource(R.drawable.inactive_points_ico);
        }
        dotes[position].setImageResource(R.drawable.active_point_ico);
        if (position + 1 == dotsCount) {
            //  btnFinish.setVisibility(View.VISIBLE);
        } else if (position == 0) {
        } else {
        }
        if (dotes.length == 1) {
        }
    }

    /*.........................................................*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*.............................like_Api................................................*/
    private void like_Api(String post_id) {

        mdialog = Mabwe.showProgress(this);
        Map<String, String> map = new HashMap<>();
        map.put("post_id", post_id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                Log.e("LIKE", response);
                Mabwe.hideProgress(mdialog);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONObject data = (JSONObject) jsonObject.get("data");

                       /* String post_id = data.getString("post_id");
                        String user_id = data.getString("user_id");*/
                        String statuss = data.getString("status");
                        int like_count = Integer.parseInt(data.getString("like_count"));

                        detail_like_iv.setImageResource(statuss.equals("1") ? R.drawable.heart : R.drawable.inactive_like_ico);
                        if (statuss.equals("1")) {
//                            like_count = like_count +1;
                            tv__details_like_count.setText(like_count + "");
                        } else {
//                            like_count = like_count -1;
                            tv__details_like_count.setText(like_count >= 0 ? like_count + "" : "0");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Mabwe.hideProgress(mdialog);
            }
        });
        service.callSimpleVolley("/user/likes", map);
    }

    /*........................listDilog()...................................*/
    public void listDilog(ViewPagerListener context, String imageView) {
        final Dialog dialog = new Dialog(this, R.style.MyCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_full_image_dilog);

        ImageView detail_full_image = dialog.findViewById(R.id.detail_full_image);
        //VideoView video = dialog.findViewById(R.id.videoView);
        ImageView iv_cross = dialog.findViewById(R.id.iv_cross);


        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (detailsModal.post_video.length() == 0) {
            iv_cross.setVisibility(View.VISIBLE);

            if (imageView != null) {
                Picasso.with(this).load(imageView).fit().centerCrop()
                        .placeholder(R.drawable.myplaceholder)
                        .error(R.drawable.myplaceholder)
                        .into(detail_full_image);
            }
        }/* else {
            //iv_share.setVisibility(View.GONE);
            dialog.setCancelable(true);
            iv_cross.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video);
            Uri uri = Uri.parse(detailsModal.post_video);

            video.setMediaController(null);
            video.setVideoURI(uri);
            video.requestFocus();
            progressBar_detail.setVisibility(View.VISIBLE);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar_detail.setVisibility(View.GONE);
                    mp.setLooping(true);
                }


            });
            video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Utils.openAlertDialog(DetailsActivity.this, "Video file not supported..");

                    return false;
                }
            });
            video.start();
        }*/
        dialog.show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 2 && resultCode == RESULT_OK) {

                String value = data.getStringExtra("dialog");
                if (value.equals("1")) {

                    Toast.makeText(this, "Report Submit successfully", Toast.LENGTH_SHORT).show();
                    //utility.showCustomPopup(getString(R.string.report_submitted_successfully), String.valueOf(R.font.montserrat_medium));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
