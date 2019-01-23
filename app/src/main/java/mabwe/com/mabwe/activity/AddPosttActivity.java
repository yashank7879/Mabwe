package mabwe.com.mabwe.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mabwe.com.VideoTrim.videoActivity.TrimmerActivity;
import mabwe.com.VideoTrim.videoUtil.FileUtils;
import mabwe.com.cropper.CropImage;
import mabwe.com.cropper.CropImageView;
import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.ImagePickerPackge.ImagePicker;
import mabwe.com.mabwe.ImagePickerPackge.ImageRotator;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.AddMediaAdapter;
import mabwe.com.mabwe.adapter.AddTagAdapter;
import mabwe.com.mabwe.adapter.CatgoryAdapter;
import mabwe.com.mabwe.adapter.TagListAdapter;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.customListner.CustomTagLIstener;
import mabwe.com.mabwe.customListner.GetImageFromGallaryLIstener;
import mabwe.com.mabwe.customListner.RemoveListItemListener;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.helper.PermissionAll;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.ImageBean;
import mabwe.com.mabwe.modals.TagModal;
import mabwe.com.mabwe.multipleFileUpload.MultiPartRequest;
import mabwe.com.mabwe.multipleFileUpload.Template;
import mabwe.com.mabwe.multipleFileUpload.videoUpload.ImageVideoUtils;
import mabwe.com.mabwe.server_task.API;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;
import mabwe.com.mabwe.volley.AppHelper;
import mabwe.com.mabwe.volley.DataPart;
import mabwe.com.mabwe.volley.VolleyMultipartRequest;
import mabwe.com.mabwe.volley.VolleyMySingleton;

import static mabwe.com.mabwe.helper.Constant.EXTRA_VIDEO_PATH;
import static mabwe.com.mabwe.helper.Constant.REQUEST_STORAGE_READ_ACCESS_PERMISSION;
import static mabwe.com.mabwe.helper.Constant.REQUEST_VIDEO_CAPTURE;
import static mabwe.com.mabwe.helper.Constant.REQUEST_VIDEO_TRIMMER;
import static mabwe.com.mabwe.helper.Constant.VIDEO_TOTAL_DURATION;
import static mabwe.com.mabwe.utils.ToastClass.showToast;


public class AddPosttActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    public RecyclerView select_post_category_rv;
    public LinearLayout othrity_container, wiling_container;
    public RelativeLayout show_addicon_view;
    Dialog dialog = null;
    MainActivity mainActivity;
    private RecyclerView add_media_recycler_view, add_post_tag_recycler_view;
    private List<ImageBean> imageBeans;
    private ArrayList<CategoryModal> categarylist;
    private ArrayList<TagModal> tagItemList;   //for dialog
    private ArrayList<TagModal> tagArrarylist;  //for show //SelectedTagModal
    private AddMediaAdapter imageAdapter;
    private AddTagAdapter addTagAdapter;
    private CatgoryAdapter catgoryAdapter;
    private CardView select_post_category;
    private ImageView addpost_iv_down, addpost_addtag, iv_adpost_back;
    private boolean fun = true;
    private View hide_view;
    private ScrollView containier;
    private MultiPartRequest mMultiPartRequest;
    private EditText addpost_post_title, addpost_description, addpost_email, addpost_contect_no;
    private TextView adpost_catgory_name;
    private RelativeLayout addpost_add_location;
    private LinearLayout addpost_add_button;
    private GoogleApiClient mGoogleApiClient;
    private TextView adpost_tv_myLocation;
    private TextView tv_upto_five;
    private RadioGroup radiogoupof_ship, radiogroup_outhority, radiogroup_relocate;
    private RadioGroup radiogoupof_available_sold,radiogroup_seraching_hired;
    private TagListAdapter tagListAdapter;
    private LinearLayout addpost_chackbox;
    private ImageView review_iv;
    private String selectedCategory = "";
    private boolean check = true;
    private Bitmap bitmap;
    private Double lati;
    private Double longi;
    private String address;
    private String city = "";
    private String state;
    private String country;
    private String selectgoryid = "";
    private String relocate = "0";
    private String sold = "0";
    private String hired = "0";
    private String authorised = "0";
    private String whilingToship = "0";
    private RelativeLayout addpost_addpost_show;
    private String finalTagList = "";
    private Dialog mdialog;
    private Notification.Builder mBuilder;
    private NotificationManager mNotifyManager;

    private AddPosttActivity addPosttActivity;
    private static final int SELECT_VIDEO_REQUEST = 1000;
    private static int ID = 100;
    private ArrayList<File> videoFileList;
    private ArrayList<File> videoThumbList;
    private ArrayList<File> fileList;
    private Uri finalVideoUri;
    private String finalVideoFilePath;

    public static int imagevideoSel = 0;

    public AddPosttActivity() throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_postt);
        findItemViewId();

        if (Utils.isNetworkAvailable(this)) {
            category_Api();
        } else {
            startActivity(new Intent(this, InternateConActivity.class));
        }
    }

    /*............................find_Item_View_Id............................*/
    private void findItemViewId() {
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(AddPosttActivity.this);

        categarylist = new ArrayList<>();
        tagArrarylist = new ArrayList<>();
        tagItemList = new ArrayList<>();
        mainActivity = new MainActivity();
        select_post_category_rv = findViewById(R.id.select_post_category_rv);
        add_media_recycler_view = findViewById(R.id.add_media_recycler_view);

        imageBeans = new ArrayList<>();
        imageBeans.add(0, null);

        /*..............................AddTaAddMediaAdapter...........................*/
        imageAdapter = new AddMediaAdapter(imageBeans, this, new GetImageFromGallaryLIstener() {
            @Override
            public void getImagePosition(int pos) {

                if (pos == 0) {
                    //getPermissionAndPicImage();
                    openChooseMediaDialog();
                }
            }

            @Override
            public void removePostImage(ImageBean bean, int pos, boolean value) {

            }

        });

        add_media_recycler_view.setAdapter(imageAdapter);

        add_post_tag_recycler_view = findViewById(R.id.add_post_tag_recycler_view);

        /*..............................AddTagAdapter...........................*/
        addTagAdapter = new AddTagAdapter(tagArrarylist, this, new RemoveListItemListener() {
            @Override
            public void removeListItem(String name, String tagComaName) {

                for (int i = 0; i < tagArrarylist.size(); i++) {
                    if (tagArrarylist.get(i).tagName.equals(name)) {
                        for (int j = 0; j < tagItemList.size(); j++) {
                            if (tagItemList.get(j).tagName.equals(name)) {
                                tagItemList.get(j).istagSelecte = false;
                            }
                        }
                        tagArrarylist.remove(i);
                    }
                }

                finalTagList = tagComaName;
                if (finalTagList.contains("," + name)) {
                    finalTagList = finalTagList.replace("," + name, "");
                } else if (finalTagList.contains(name + ",")) {
                    finalTagList = finalTagList.replace(name + ",", "");
                } else if (finalTagList.contains(name)) {
                    finalTagList = finalTagList.replace(name, "");
                }

                tagListAdapter.notifyDataSetChanged();
                addTagAdapter.notifyDataSetChanged();
            }
        });

        add_post_tag_recycler_view.setAdapter(addTagAdapter);

        select_post_category = findViewById(R.id.select_post_category);
        hide_view = findViewById(R.id.hide_view);
        containier = findViewById(R.id.containier);
        addpost_addtag = findViewById(R.id.addpost_addtag);
        addpost_iv_down = findViewById(R.id.addpost_iv_down);
        addpost_post_title = findViewById(R.id.addpost_post_title);
        addpost_description = findViewById(R.id.addpost_description);
        addpost_add_location = findViewById(R.id.addpost_add_location);
        addpost_add_button = findViewById(R.id.addpost_add_button);
        iv_adpost_back = findViewById(R.id.iv_adpost_back);
        adpost_tv_myLocation = findViewById(R.id.adpost_tv_myLocation);
        tv_upto_five = findViewById(R.id.tv_upto_five);

        radiogoupof_ship = findViewById(R.id.radiogoupof_ship);
        radiogroup_outhority = findViewById(R.id.radiogroup_outhority);
        radiogroup_relocate = findViewById(R.id.radiogroup_relocate);
        radiogroup_seraching_hired = findViewById(R.id.radiogroup_seraching_hired);
        radiogoupof_available_sold = findViewById(R.id.radiogoupof_available_sold);

        addpost_email = findViewById(R.id.addpost_email);
        addpost_contect_no = findViewById(R.id.addpost_contect_no);
        adpost_catgory_name = findViewById(R.id.adpost_catgory_name);
        addpost_chackbox = findViewById(R.id.addpost_chackbox);

        othrity_container = findViewById(R.id.othrity_container);
        wiling_container = findViewById(R.id.wiling_container);
        review_iv = findViewById(R.id.review_iv);
        addpost_addpost_show = findViewById(R.id.addpost_addpost_show);

        setOnclickListener();
        addlocation();


        /*............................CatgoryAdapter..........................*/
        catgoryAdapter = new CatgoryAdapter(categarylist, AddPosttActivity.this, new CategoryClick() {
            @Override
            public void getCategoryClick(int position) {

                hidekeywordOnClick();

                if (categarylist.get(position).categoryName.equals("Connect")) {

                    othrity_container.setVisibility(View.GONE);
                    wiling_container.setVisibility(View.GONE);
                    selectgoryid = categarylist.get(position).categoryId;


                } else if (categarylist.get(position).categoryName.equals("Career")) {
                    wiling_container.setVisibility(View.GONE);
                    othrity_container.setVisibility(View.VISIBLE);
                    selectgoryid = categarylist.get(position).categoryId;


                } else if (categarylist.get(position).categoryName.equals("Marketplace")) {
                    wiling_container.setVisibility(View.VISIBLE);
                    othrity_container.setVisibility(View.GONE);
                    selectgoryid = categarylist.get(position).categoryId;

                } else if (categarylist.get(position).categoryName.equals("Mabwe411")) {
                    othrity_container.setVisibility(View.GONE);
                    wiling_container.setVisibility(View.GONE);
                    selectgoryid = categarylist.get(position).categoryId;
                }
                select_post_category_rv.setVisibility(View.GONE);
                hide_view.setVisibility(View.GONE);
                adpost_catgory_name.setText(categarylist.get(position).categoryName);
                fun = true;
                addpost_iv_down.setImageResource(R.drawable.down_arrow);

                selectedCategory = categarylist.get(position).categoryName;
                selectgoryid = categarylist.get(position).categoryId;
            }
        });
    }

    /*,.,.,..,.,,.,..,,,.,..open choose media dialog  ,.,.,.,,.,.,.,.,.,,.,,.,,,,.,*/

    private void openChooseMediaDialog() {
        final Dialog dialog = new Dialog(AddPosttActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_choose_video);

        LinearLayout ll_video = dialog.findViewById(R.id.ll_video);
        LinearLayout ll_picture = dialog.findViewById(R.id.ll_picture);


        ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagevideoSel == 2 || imagevideoSel == 0 || imageBeans.size() == 1) {
                    imagevideoSel = 2;
                    showSetIntroVideoDialog();
                    dialog.dismiss();
                } else {
                    showToast(AddPosttActivity.this, getString(R.string.select_video_image_at_a_time));
                }

            }
        });

        ll_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagevideoSel == 1 || imagevideoSel == 0 || imageBeans.size() == 1) {
                    imagevideoSel = 1;
                    getPermissionAndPicImage();
                    dialog.dismiss();
                } else {

                    showToast(AddPosttActivity.this, getString(R.string.select_video_image_at_a_time));
                }

            }
        });

        dialog.findViewById(R.id.iv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showSetIntroVideoDialog() {


        final CharSequence[] options = {"Take Video", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPosttActivity.this);

        builder.setTitle("Add Video");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Video")) {

                    Intent intentCaptureVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (intentCaptureVideo.resolveActivity(getPackageManager()) != null) {
                        long maxVideoSize = 18 * 1024 * 1024; // 18 MB
                        intentCaptureVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 53);//120 sec = 2min //10000sec //10 min
                        intentCaptureVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        // intentCaptureVideo.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize);
                        startActivityForResult(intentCaptureVideo, REQUEST_VIDEO_CAPTURE);
                    }

                } else if (options[which].equals("Choose from Gallery")) {

                    if (ActivityCompat.checkSelfPermission(AddPosttActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                    } else {

                      /*  Intent intent = new Intent();
                        intent.setTypeAndNormalize("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);*/
                        //intent.addCategory(Intent.CATEGORY_OPENABLE);

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                        // startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_GET);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER);
                    }
                }
            }
        });
        builder.show();
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_title_rationale));
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(AddPosttActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    /*..........................................addlocation.....................................*/
    private void addlocation() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = AddPosttActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*.................................set_On_click_Listener....................................*/
    private void setOnclickListener() {
        select_post_category.setOnClickListener(this);
        addpost_add_location.setOnClickListener(this);
        addpost_addtag.setOnClickListener(this);
        addpost_add_button.setOnClickListener(this);
        iv_adpost_back.setOnClickListener(this);
        addpost_chackbox.setOnClickListener(this);
        addpost_addpost_show.setOnClickListener(this);

        /*.........................................checkBox............................................*/
        radiogoupof_ship.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radiogoupof_ship = group.findViewById(checkedId);
                if (null != radiogoupof_ship && checkedId > -1) {
                    whilingToship = "1";

                }
            }
        });

        radiogroup_outhority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radiogroup_outhority = (RadioButton) group.findViewById(checkedId);

                String radioButtonAuthority = radiogroup_outhority.getText().toString().trim();

                if (radioButtonAuthority.equals("No")) {
                    authorised = "0";
                } else {
                    authorised = "1";
                }

            }
        });


        radiogroup_relocate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radiogroup_relocate = (RadioButton) group.findViewById(checkedId);

                String radioButtonRelocate = radiogroup_relocate.getText().toString().trim();

                if (radioButtonRelocate.equals("No")) {
                    relocate = "0";
                } else {
                    relocate = "1";
                }

              /*
                if (null != radiogroup_relocate && checkedId > -1) {
                    relocate = "1";
                }*/
            }
        });

        radiogoupof_available_sold.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_available = (RadioButton) group.findViewById(checkedId);

                String radioButtonRelocate = rb_available.getText().toString().trim();

                if (radioButtonRelocate.equals(getString(R.string.available))) { // Available for 0
                    sold = "0";
                } else {
                    sold = "1"; // sold for 1
                }
            }
        });

        radiogroup_seraching_hired.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_still_searching = (RadioButton) group.findViewById(checkedId);

                String radioButtonRelocate = rb_still_searching.getText().toString().trim();

                if (radioButtonRelocate.equals(getString(R.string.still_searching))) { // still searching for 0
                    hired = "0";
                } else {
                    hired = "1"; // hired for 1
                }
            }
        });
    }

    /*................................submit_data_with_validation...............................*/
    private void submit_data_with_validation() {
        String postTitle = addpost_post_title.getText().toString().trim();
        String addLocation = adpost_tv_myLocation.getText().toString().trim();
        String description = addpost_description.getText().toString().trim();
        String email = addpost_email.getText().toString().trim();
        String contect_no = addpost_contect_no.getText().toString().trim();


        Validation validation = new Validation(this);

        if (selectedCategory.isEmpty()) {
            Utils.openAlertDialog(this, getString(R.string.select_category));
        } else {
            switch (selectedCategory) {
                case "Marketplace":
                    if (imageBeans.size() == 1 && !validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(AddPosttActivity.this, getString(R.string.mediaorlocation));
                    } else if (!validation.isEmpty(postTitle)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_post));
                        addpost_post_title.requestFocus();
                    }/* else if (!validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_Location));
                        addpost_add_location.requestFocus();

                    }*/ else if (!validation.isEmpty(description)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_description));
                        addpost_description.requestFocus();

                    } else if (!validation.isEmpty(email)) {
                        Utils.openAlertDialog(this, getString(R.string.email_null));
                        addpost_email.requestFocus();
                    } else if (!validation.isValidEmail(email)) {
                        Utils.openAlertDialog(this, getString(R.string.email_v));
                        addpost_email.requestFocus();
                        return;
                    } else if (!validation.isEmpty(contect_no)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_contect_no));
                        addpost_contect_no.requestFocus();

                    } else if (contect_no.length() < 7) {
                        Utils.openAlertDialog(this, getString(R.string.inter_seven_digit_no));
                        addpost_contect_no.requestFocus();

                    } else if (contect_no.length() > 16) {
                        Utils.openAlertDialog(this, getString(R.string.inter_sixteen_digit_no));
                        addpost_contect_no.requestFocus();

                    } else {
                        addPost_Api();
                        //saveProfileAccount();
                    }
                    break;
                case "Career":
                    if (imageBeans.size() == 1 && !validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(AddPosttActivity.this, getString(R.string.mediaorlocation));

                    } else if (!validation.isEmpty(postTitle)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_post));
                        addpost_post_title.requestFocus();

                    } /*else if (!validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_Location));
                        addpost_add_location.requestFocus();

                    }*/ else if (!validation.isEmpty(description)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_description));
                        addpost_description.requestFocus();

                    } else {
                        addPost_Api();
                        //uploadFile();
                    }
                    break;
                case "Connect":
                    if (imageBeans.size() == 1 && !validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(AddPosttActivity.this, getString(R.string.mediaorlocation));
                    }else if (!validation.isEmpty(postTitle)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_post));
                        addpost_post_title.requestFocus();

                    } /*else if (!validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_Location));
                        adpost_tv_myLocation.requestFocus();

                    }*/ else if (!validation.isEmpty(description)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_description));
                        addpost_description.requestFocus();

                    } else {
                        addPost_Api();
                    }
                    break;

                case "Mabwe411":
                    if (imageBeans.size() == 1 && !validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(AddPosttActivity.this, getString(R.string.mediaorlocation));
                    } else if (!validation.isEmpty(postTitle)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_post));
                        addpost_post_title.requestFocus();

                    } /*else if (!validation.isEmpty(addLocation)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_Location));
                        adpost_tv_myLocation.requestFocus();

                    }*/ else if (!validation.isEmpty(description)) {
                        Utils.openAlertDialog(this, getString(R.string.insert_description));
                        addpost_description.requestFocus();

                    } else {
                        addPost_Api();
                    }
                    break;
            }
        }
    }

    /*.....................................onClick..............................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_post_category:
                if (!fun) {
                    addpost_iv_down.setImageResource(R.drawable.down_arrow);
                    select_post_category_rv.setVisibility(View.GONE);
                    hide_view.setVisibility(View.GONE);
                    fun = true;
                } else {
                    fun = false;
                    addpost_iv_down.setImageResource(R.drawable.up_arrow);
                    hide_view.setVisibility(View.VISIBLE);
                    select_post_category_rv.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.addpost_add_location:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(AddPosttActivity.this);
                    startActivityForResult(intent, 100);
                } catch (Exception e) {

                }

                break;

            case R.id.addpost_add_button:
                submit_data_with_validation();
                break;

            case R.id.iv_adpost_back:
                onBackPressed();
                hidekeywordOnClick();
                break;

            case R.id.addpost_chackbox:

                if (!check) {
                    review_iv.setImageResource(R.drawable.checked);
                    check = true;
                } else {
                    check = false;
                    review_iv.setImageResource(R.drawable.un_checked);
                }
                break;

            case R.id.addpost_addpost_show:
                addtag_dilog(this);
                break;
        }
    }

    /*........................................addtag_dilog......................................*/
    public void addtag_dilog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_tag_dilog);
        RecyclerView tag_list_recyclerview = dialog.findViewById(R.id.tag_list_recyclerview);
        ImageView iv_cancle = dialog.findViewById(R.id.add_tag_iv_cancle);
        ImageView add_icon = dialog.findViewById(R.id.add_icon);
        show_addicon_view = dialog.findViewById(R.id.show_addicon_view);
        final EditText dilog_et_search = dialog.findViewById(R.id.dilog_et_search);

        /*....................onClick.........................*/
        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext = dilog_et_search.getText().toString().trim();

                if (searchtext.equalsIgnoreCase("")) {
                    showToast(AddPosttActivity.this, getString(R.string.enter_tag_name));
                } else {
                    TagModal tagModal = new TagModal();
                    tagModal.tagName = searchtext;
                    tagModal.tagId = "";
                    tagModal.istagSelecte = true;

                    finalTagList = searchtext + "," + finalTagList;

                    if (finalTagList.endsWith(",")) {
                        finalTagList = finalTagList.substring(0, finalTagList.length() - 1);
                    }

                    for (int j = 0; j < tagArrarylist.size(); j++) {
                        if (tagArrarylist.get(j).tagName.equals(searchtext)) {
                            showToast(AddPosttActivity.this, getString(R.string.same_tagnot_allowed));

                            return;
                        }
                    }

                    //when tag is blank
                    if (!tagModal.tagName.isEmpty()) {
                        tagArrarylist.add(tagModal);
                    }

                    addTagAdapter.notifyDataSetChanged();
                    hidekeywordOnClick();
                    dialog.dismiss();
                }
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mainActivity.hidekeywordOnClick();
            }
        });

        /*.............................TagListAdapter..................................*/
        tagListAdapter = new TagListAdapter(tagItemList, this, new CustomTagLIstener() {
            @Override
            public void getTagItem(TagModal tagModal, String tagName, boolean issetselected) {

                tagModal.istagSelecte = issetselected;
                if (tagModal.istagSelecte) {
                    tagArrarylist.add(tagModal);
                } else {
                    tagArrarylist.remove(tagModal);
                }

                finalTagList = tagName + "," + finalTagList;
                if (finalTagList.endsWith(",")) {
                    finalTagList = finalTagList.substring(0, finalTagList.length() - 1);
                }

                tagListAdapter.notifyDataSetChanged();
                addTagAdapter.notifyDataSetChanged();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        tag_list_recyclerview.setAdapter(tagListAdapter);
        if (tagItemList.size() == 0) {

            if (Utils.isNetworkAvailable(context)) {
                try {
                    //Api calling
                    tag_Api(tagListAdapter);
                } catch (NullPointerException e) {
                    showToast(AddPosttActivity.this, getString(R.string.too_slow));
                }
            } else showToast(AddPosttActivity.this, getString(R.string.no_internet_access));

        }

        textwatcherMethod(dilog_et_search, tagListAdapter);
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*................................textwatcherMethod().......................................*/
    private void textwatcherMethod(EditText searchtext, TagListAdapter tagListAdapter) {
        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString().toLowerCase());

                if (tagItemList.size() == 0) {
                    show_addicon_view.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < tagItemList.size(); i++) {
                        if (tagItemList.get(i).tagName.equalsIgnoreCase(editable.toString())) {
                            show_addicon_view.setVisibility(View.GONE);
                            return;
                        } else {
                            show_addicon_view.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void filter(String text) {
        ArrayList<TagModal> filterdNames = new ArrayList<>();
        for (TagModal m : tagItemList) {
            if (m.tagName.toLowerCase().contains(text)) {
                filterdNames.add(m);
            }
            tagListAdapter.setFilter(filterdNames);
        }
    }

    /*.....................................Add Location........................................*/

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage() + "connection fail", Toast.LENGTH_SHORT).show();
    }

    /*.......................................tag_Api...........................................*/
    private void tag_Api(final TagListAdapter tagListAdapter) {
        mdialog = Mabwe.showProgress(this);
        Map<String, String> map = new HashMap<>();
        map.put("search", "");

        final WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("TAGS", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        tagItemList.clear();
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) data.get(i);

                            TagModal tagModal = new TagModal();
                            tagModal.tagName = jsonObject1.getString("tagName");
                            //tag name not empty
                            if (!tagModal.tagName.isEmpty()) {
                                tagItemList.add(tagModal);
                            }
                        }

                        //tagListAdapter.notifyDataSetChanged();

                    } else {
                        Mabwe.hideProgress(mdialog);
                        Utils.openAlertDialog(AddPosttActivity.this, message);
                    }

                    tagListAdapter.notifyDataSetChanged();

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
        service.callSimpleVolley("/user/tags", map);
    }

    /*.....................................category_Api.........................................*/
    private void category_Api() {

        if (Utils.isNetworkAvailable(getApplication())) {
            try {
                mdialog = Mabwe.showProgress(this);

                WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        Mabwe.hideProgress(mdialog);
                        Log.e("CATEGORY", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {

                                JSONArray data = jsonObject.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) data.get(i);

                                    CategoryModal categoryModal = new CategoryModal();
                                    categoryModal.categoryName = jsonObject1.getString("categoryName");
                                    categoryModal.categoryId = jsonObject1.getString("categoryId");
                                    categoryModal.categoryImage = jsonObject1.getString("categoryImage");

                                    categarylist.add(categoryModal);
                                }

                                select_post_category_rv.setAdapter(catgoryAdapter);
                                catgoryAdapter.notifyDataSetChanged();

                            } else {
                                Utils.openAlertDialog(AddPosttActivity.this, message);
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
                service.callGetSimpleVolley("/user/categories");
            } catch (NullPointerException e) {
                showToast(AddPosttActivity.this, getString(R.string.too_slow));
            }
        } else showToast(AddPosttActivity.this, getString(R.string.no_internet_access));


    }


    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(AddPosttActivity.this);
            }
        } else {
            ImagePicker.pickImage(AddPosttActivity.this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    showToast(AddPosttActivity.this, getString(R.string.permission_denied));
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    showToast(AddPosttActivity.this, getString(R.string.your_permission_denied));
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(AddPosttActivity.this);
                } else {
                    showToast(AddPosttActivity.this, getString(R.string.your_permission_denied));
                }
            }
            break;
        }
    }

    /*..................................onActivityResult.........................................*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("tg", "resultCode = " + resultCode + " data " + data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                StringBuilder stBuilder = new StringBuilder();
                address = String.format("%s", place.getAddress());
                stBuilder.append(address);
                adpost_tv_myLocation.setText(stBuilder.toString());

                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(this, Locale.getDefault());

                lati = place.getLatLng().latitude;
                longi = place.getLatLng().longitude;

                try {
                    addresses = geocoder.getFromLocation(lati, longi, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                    if (addresses != null) {
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                    } else {
                        showToast(AddPosttActivity.this, getString(R.string.not_fatch_location));
                    }
                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (Exception t) {
                    t.printStackTrace();
                }

                Log.i("", city + "" + state + "CompleteProfileActivity" + country);
            }
        } else if (requestCode == 234) {
            Uri imageUri = ImagePicker.getImageURIFromResult(AddPosttActivity.this, requestCode, resultCode, data);
            /*try {
                getImageSize(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            if (imageUri != null) {
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(700, 466, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                        .setAspectRatio(4, 3)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            try {
                if (result != null)
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                if (bitmap != null) {
                    tv_upto_five.setVisibility(View.VISIBLE);
                    bitmap = ImagePicker.getImageResized(this, result.getUri());
                    if (imageBeans.size() < 6) {
                        imageBeans.add(1, new ImageBean(null, bitmap, ""));
                        imageAdapter.notifyDataSetChanged();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 20) {

            Bitmap thumbBitmap;
            try {
                Uri videoUri = Uri.parse(data.getStringExtra("result"));

                if (resultCode == RESULT_OK) {

                    String result = data.getStringExtra("result");

                    // if (result.endsWith(".mp4") | result.endsWith(".3gp")) {
                    File videoFile = new File(result);
                    if (getVideoTimeInSec(videoFile) <= 53) {

                        // Get length of file in bytes
                        long fileSizeInBytes = videoFile.length();
                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInMB = fileSizeInKB / 1024;

                        if (fileSizeInMB <= 18) {


                            finalVideoUri = videoUri;
                            finalVideoFilePath = result;
                            finalVideoFilePath = ImageVideoUtils.generatePath(videoUri, this);
                            thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath); //ImageVideoUtil.getCompressBitmap();

                            //Toast.makeText(AddPosttActivity.this, ""+thumbBitmap.getWidth()+" "+thumbBitmap.getHeight(), Toast.LENGTH_SHORT).show();
                            int rotation = ImageRotator.getRotation(this, finalVideoUri, true);
                            thumbBitmap = ImageRotator.rotate(thumbBitmap, rotation);


                            File thumbFile = savebitmap(this, thumbBitmap, UUID.randomUUID() + ".png");


                            if (thumbBitmap != null) {
                                if (imageBeans != null) {
                                    if (imageBeans.size() == 2) imageBeans.remove(1);
                                    tv_upto_five.setVisibility(View.GONE);
                                    imageBeans.add(1, new ImageBean(finalVideoFilePath, thumbBitmap, ""));
                                    imageAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            showToast(AddPosttActivity.this, getString(R.string.lessthen_20mb));
                        }
                    } else {
                        showToast(AddPosttActivity.this, getString(R.string.lessthen_53sec));
                    }
                }
            } catch (Exception e) {
            } catch (OutOfMemoryError e) {
                Toast.makeText(this, "Out of memory", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    showToast(AddPosttActivity.this, getString(R.string.toast_cannot_retrieve_selected_video));
                }
            }

        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    Toast.makeText(AddPosttActivity.this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //get image size
    private void getImageSize(Uri choosen) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), choosen);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;

        Toast.makeText(getApplicationContext(), "Image size " + Long.toString(lengthbmp), Toast.LENGTH_SHORT).show();

    }


    //get time duration
    public long getVideoTimeInSec(File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, Uri.fromFile(videoFile));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time) / 1000;
        retriever.release();

        return timeInMillisec;

    }


    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, TrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 52);
        startActivityForResult(intent, 20);
    }

    private int getMediaDuration(Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(this, uriOfFile);
        int duration = mp.getDuration();
        return duration;
    }

    public File savebitmap(Context mContext, Bitmap bitmap, String name) {
        File filesDir = mContext.getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name);

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e(mContext.getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return null;
    }
//   .............................Add_post_Api............................................

    private void addPost_Api() {
        if (Utils.isNetworkAvailable(getApplication())) {
            try {
                if (lati == null && longi == null) {
                    lati = 0.0;
                    longi = 0.0;
                }
                mdialog = Mabwe.showProgress(this);
                Map<String, String> map = new HashMap<>();
                map.put("title", addpost_post_title.getText().toString().trim());
                map.put("discription", addpost_description.getText().toString().trim());
                map.put("category", selectgoryid);
                map.put("lat", String.valueOf(lati));
                map.put("long", String.valueOf(longi));
                map.put("relocate", relocate);
                map.put("authorised", authorised);
                map.put("whilingToship", whilingToship);
                map.put("email", addpost_email.getText().toString().trim());
                map.put("contact", addpost_contect_no.getText().toString().trim());
                map.put("tags", finalTagList);
                map.put("country", country == null ? "" : country);
                map.put("city", city == null ? "" : city);
                map.put("state", state == null ? "" : state);
                map.put("address", address==null ? "" : address);
                map.put("isHired", hired);
                map.put("availability", sold);

                fileList = new ArrayList<>();
                videoFileList = new ArrayList<>();
                videoThumbList = new ArrayList<>();
                for (ImageBean tmp : imageBeans) {

                    if (tmp != null && tmp.url != null && tmp.bitmap != null) {
                        if (finalVideoFilePath != null) {
                            File file = new File(finalVideoFilePath);
                            videoFileList.add(file);
                            videoThumbList.add(savebitmap(this, tmp.bitmap, UUID.randomUUID() + ".png"));
                        }
                    } else {
                        if (tmp != null && tmp.bitmap != null) {
                            fileList.add(bitmapToFile(tmp.bitmap));
                        }
                    }
                }
                VolleyMySingleton volleySingleton = new VolleyMySingleton(AddPosttActivity.this);
                RequestQueue mRequest = volleySingleton.getInstance().getRequestQueue();
                mRequest.start();
                mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Mabwe.hideProgress(mdialog);
                        Utils.openAlertDialog(AddPosttActivity.this, "Please try again..");

                    }
                }, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Mabwe.hideProgress(mdialog);
                        try {
                            Log.e("RESPONSE multiPart", response.toString());

                            JSONObject result = null;

                            Log.d("AddPostResponce", response + "");

                            result = new JSONObject(response.toString());
                            String status = result.getString("status");
                            String message = result.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                finalVideoFilePath = "";
                                getMultiImage(AddPosttActivity.this, "Post added successfully");

                            } else if (status.equalsIgnoreCase("authFail")) {

                            } else {
                                Utils.openAlertDialog(AddPosttActivity.this, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, videoFileList, videoThumbList, fileList, fileList.size(), map, AddPosttActivity.this);

                //Set tag
                mMultiPartRequest.setTag("MultiRequest");

                //Set retry policy
                mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                        Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mRequest.add(mMultiPartRequest);

            } catch (NullPointerException e) {
                showToast(AddPosttActivity.this, getString(R.string.too_slow));
            }
        } else showToast(AddPosttActivity.this, getString(R.string.no_internet_access));

    }

    public void getMultiImage(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mabwe");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(AddPosttActivity.this, MainActivity.class));
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    //convert bitmap to file
    private File bitmapToFile(Bitmap bitmap) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //*************new upload volley*************
    private void saveProfileAccount() {
        // loading or check internet connection or something...
        // ... then
        mdialog = Mabwe.showProgress(this);

        String url = API.BASE_URL + "user/addPost";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    mdialog.cancel();
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");

                    // if (status.equals(Constant.REQUEST_SUCCESS)) {
                    // tell everybody you have succed upload image and post strings
                    Log.i("Messsage", message);
                    //  } else {
                    //    Log.i("Unexpected", message);
                    // }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError error) {
                mdialog.cancel();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("title", addpost_post_title.getText().toString().trim());
                map.put("discription", addpost_description.getText().toString().trim());
                map.put("category", selectgoryid);
                map.put("lat", String.valueOf(lati));
                map.put("long", String.valueOf(longi));
                map.put("relocate", relocate);
                map.put("authorised", authorised);
                map.put("whilingToship", whilingToship);
                map.put("email", addpost_email.getText().toString().trim());
                map.put("contact", addpost_contect_no.getText().toString().trim());
                map.put("tags", finalTagList);
                map.put("country", country == null ? "" : country);
                map.put("city", city == null ? "" : city);
                map.put("state", state == null ? "" : state);
                map.put("address", address);

                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                fileList = new ArrayList<>();
                videoFileList = new ArrayList<>();
                videoThumbList = new ArrayList<>();
                for (ImageBean tmp : imageBeans) {


                    if (tmp != null && tmp.url != null && tmp.bitmap != null) {
                        if (finalVideoFilePath != null) {
                            File file = new File(finalVideoFilePath);
                            videoFileList.add(file);
                            // videoThumbList.add(bitmapToFile(tmp.bitmap));
                            //params.put("video_thumb", new DataPart("file_cover.jpg", AppHelper.getFileDataFromBitmap(getBaseContext(), tmp.bitmap), "image/jpeg"));
                            params.put("video", new DataPart("video.mp4", getfile(file), "video/mp4"));

                        }
                    } else {
                        if (tmp != null && tmp.bitmap != null) {
                            fileList.add(bitmapToFile(tmp.bitmap));
                            params.put("images[]", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromBitmap(getBaseContext(), tmp.bitmap), "image/jpeg"));

                        }
                    }
                }
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                Session session = new Session(AddPosttActivity.this);

                header.put("authToken", session.getAuthToken());
                return header;
            }
        };


        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Mabwe.getInstance().addToRequestQueue(multipartRequest, "UPLOAD");
    }


    //file to byte
    public byte[] getfile(File pathvalue) {

        int size = (int) pathvalue.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(pathvalue));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }


}
