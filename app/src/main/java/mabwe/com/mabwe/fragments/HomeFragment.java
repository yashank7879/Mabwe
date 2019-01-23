package mabwe.com.mabwe.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.ImagePickerPackge.ImagePicker;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.activity.RegisterationActivity;
import mabwe.com.mabwe.adapter.DynamicCatgoryAdapter;
import mabwe.com.mabwe.adapter.HomeListAdapter;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.HomePostModal;
import mabwe.com.mabwe.pagination.EndlessRecyclerViewScrollListener;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.PermissionAll;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static mabwe.com.mabwe.utils.ToastClass.showToast;


public class HomeFragment extends Fragment implements View.OnClickListener, HomeListAdapter.ShareHomeListListener {


    private ArrayList<HomePostModal> homePostList;
    private List<HomePostModal.Tags> tagsList;

    private Context context;
    private CardView home_card_view;
    private Dialog dialog;
    private RecyclerView recycler_view;
    private HomeListAdapter homeListAdapter;
    private String setKey = "0";
    private ImageView filter_icon_iv_done;
    private String filter;
    private Session session;
    private TextView no_data_found;
    private ProgressBar progressBar_cyclic;
    private SwipeRefreshLayout swipeToRefresh;
    private String TAG = MainActivity.class.getSimpleName();
    private int offset_def = 5;
    private String searchText = "";
    private StringBuffer selectFilter = new StringBuffer("");

    private RecyclerView select_post_category_rv;
    private DynamicCatgoryAdapter dynamiccatgoryAdapter;
    private Activity activity;
    private StringBuilder stBuilder;
    private ProgresDilog progresDilog;
    private RelativeLayout share_view;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        session = new Session(context);
        filter = session.getSelected();
        String token = session.getAuthToken();
        Log.e("Token..", token);
        setUpUI(view);


        return view;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PermissionAll allPermission= new PermissionAll();
        allPermission.RequestMultiplePermission(getActivity());
    }

    private void setUpUI(View view) {
        tagsList = new ArrayList<>();
        stBuilder = new StringBuilder();
        homePostList = new ArrayList<>();
        AutoCompleteTextView et_serch_post = view.findViewById(R.id.et_serch_post);
        home_card_view = view.findViewById(R.id.home_card_view);
        filter_icon_iv_done = view.findViewById(R.id.filter_icon_iv_done);
        no_data_found = view.findViewById(R.id.no_data_found);
        progressBar_cyclic = view.findViewById(R.id.progressBar_cyclic);
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        progresDilog = new ProgresDilog(context);


        recycler_view = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        homeListAdapter = new HomeListAdapter(homePostList, context, this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setAdapter(homeListAdapter);

    /*    *//*..........................pagination.....................*//*
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAllHomePost_api(filter, searchText);
                Log.e("pagination filter", filter + searchText);


            }
        };*/

        //**************  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                offset_def = offset_def + 5; //load 5 items in recyclerview
                getAllHomePost_api(filter, searchText);
            }
        };
        getAllHomePost_api(filter, searchText);

        recycler_view.addOnScrollListener(scrollListener);
        homeListAdapter.notifyItemRangeInserted(0, homePostList.size());
        homeListAdapter.notifyDataSetChanged();
        // scrollListener.resetState();
        /*..........................Full to refresh.....................*/
        swipeToRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeToRefresh.setRefreshing(false);
                //  homePostList.clear();
                // offset_def = 0;
                getAllHomePost_api(filter, searchText);
            }
        });

        textwatcherMethod(et_serch_post);
        setOnClickListener();
    }

    private void setOnClickListener() {
        filter_icon_iv_done.setOnClickListener(this);
        home_card_view.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        homePostList.clear();
        //offset_def = 0;
        if (Utils.isNetworkAvailable(context)) {
            getAllHomePost_api(filter, searchText);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.filter_icon_iv_done:

                filter_dilog(context);
                break;
        }
    }

    /*.....................................addtag_dilog.................................*/
    public void filter_dilog(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_dilog);


        //findViewbyid
        dilogwidgetfind(dialog);
        ImageView iv_cancle = dialog.findViewById(R.id.home_filter_iv_cancle);
        LinearLayout filter_apply_button = dialog.findViewById(R.id.filter_apply_button);
        LinearLayout filter_clear_button = dialog.findViewById(R.id.filter_clear_button);

        MainActivity mainActivity = (MainActivity) activity;
        //for only multi catagory select and send camma seprator
        filter = session.getSelected();
        String data = filter;
        String[] items = data.split(",");
        selectFilter = new StringBuffer("");
        for (String item : items) {
            System.out.println("item = " + item);
            Log.i("item", "" + item);


            for (CategoryModal categoryModal : mainActivity.categarylist) {
                if (item != null) {
                    if (item.equalsIgnoreCase(categoryModal.categoryId)) {
                        selectFilter.append(categoryModal.categoryId).append(",");
                        categoryModal.isSelected = true;
                    }
                }
            }

        }
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        filter_clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = "";
                selectFilter = new StringBuffer("");
                MainActivity mainActivity = (MainActivity) activity;

                for (CategoryModal categoryModal : mainActivity.categarylist) {
                    categoryModal.isSelected = false;
                }
                // homePostList.clear();
                //  offset_def = 0;
                session.setSelected(filter);
                getAllHomePost_api(filter, "");
                dialog.dismiss();
            }
        });

        filter_apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for only multi catagory select and send camma seprator
                if (!filter.equals("")) {
                    homePostList.clear();
                    /*MainActivity mainActivity = (MainActivity) activity;

                    for (CategoryModal categoryModal : mainActivity.categarylist) {
                        categoryModal.isSelected = false;
                    }*/

                    if (selectFilter.toString().endsWith(",")) {
                        filter = selectFilter.toString().substring(0, selectFilter.length() - 1);
                    }

                    session.setSelected(filter);
                    offset_def = 0;
                    getAllHomePost_api(filter, "");
                    dialog.dismiss();
                } else {
                    Utils.openAlertDialog(context, "Please Select any category");
                }
            }
        });

        dialog.show();
    }

    private void dilogwidgetfind(Dialog dialog) {

        LinearLayout marketplace = dialog.findViewById(R.id.marketplace);
        LinearLayout connect = dialog.findViewById(R.id.connect);
        LinearLayout mabwee = dialog.findViewById(R.id.mabwee);
        LinearLayout carrer = dialog.findViewById(R.id.carrer);

        select_post_category_rv = dialog.findViewById(R.id.select_post_category_rv);

        //set recyclerview in gridview form
        select_post_category_rv.setLayoutManager(new GridLayoutManager(context, 2));


        if (activity instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) activity;
            dynamiccatgoryAdapter = new DynamicCatgoryAdapter(mainActivity.categarylist, context, new CategoryClick() {
                @Override
                public void getCategoryClick(int position) {

                    for (CategoryModal categoryModal : mainActivity.categarylist) {

                        if (mainActivity.categarylist.get(position).categoryId.equals(categoryModal.categoryId)) {

                            filter = mainActivity.categarylist.get(position).categoryId;

                            if (selectFilter.toString().contains(categoryModal.categoryId)) {
                                String tempStr = selectFilter.toString();
                                selectFilter = new StringBuffer(tempStr.replace(categoryModal.categoryId + ",", ""));
                            } else {
                                selectFilter.append(categoryModal.categoryId).append(",");
                            }

                        }
                    }
                }
            });
            select_post_category_rv.setAdapter(dynamiccatgoryAdapter);
            dynamiccatgoryAdapter.notifyDataSetChanged();

        }
        marketplace.setOnClickListener(this);
        connect.setOnClickListener(this);
        mabwee.setOnClickListener(this);
        carrer.setOnClickListener(this);
    }

    /*....................................onAttach()..................................*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;

    }

    /*................................textwatcherMethod().......................................*/
    private void textwatcherMethod(final EditText stext) {
        stext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                homePostList.clear();
                offset_def = 0;
                homeListAdapter.notifyItemRangeInserted(0, homePostList.size());
                homeListAdapter.notifyDataSetChanged();
                Mabwe.getInstance().cancelPendingRequests(Mabwe.TAG);
                searchText = editable.toString();
                getAllHomePost_api(filter, searchText);
            }
        });
    }

    /*.....................................getAllHomePost_api.........................................*/
    private void getAllHomePost_api(String filter, String searchtext) {
        if (Utils.isNetworkAvailable(context)) {
            try {

                if (searchtext.equals("")) {
                    setKey = "1";
                } else {
                    setKey = "0";
                }

                if (setKey.equals("1")) {
                    progresDilog.show();
                    // mdialog = Mabwe.showProgress(context);

                    //  progressBar_cyclic.setVisibility(View.VISIBLE);
                }

                Map<String, String> map = new HashMap<>();
                map.put("limit", "" + offset_def);
                map.put("start", "0");//String.valueOf(offset_def)
                map.put("search", searchtext);
                map.put("filter", filter);

                WebService service = new WebService(context, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        progresDilog.hide();
                        //Mabwe.hideProgress(mdialog);
                        // progressBar_cyclic.setVisibility(View.GONE);
                        Log.e("AllPOst", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            //  homePostList.clear();
                            if (status.equals("success")) {
                                homePostList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject2 = (JSONObject) data.get(i);

                                    HomePostModal homePostModal = new HomePostModal();
                                    homePostModal.tags = new ArrayList<>();
                                    homePostModal.postId = jsonObject2.getString("postId");
                                    homePostModal.title = jsonObject2.getString("title");
                                    homePostModal.video = jsonObject2.getString("video");
                                    homePostModal.video_thumb = jsonObject2.getString("video_thumb");
                                    homePostModal.description = jsonObject2.getString("description");
                                    homePostModal.category_id = jsonObject2.getString("category_id");
                                    homePostModal.Crd = jsonObject2.getString("Crd");
                                    homePostModal.Upd = jsonObject2.getString("Upd");
                                    homePostModal.CurrentTime = jsonObject2.getString("CurrentTime");
                                    //  homePostModal.country = jsonObject2.getString("country");
                                    homePostModal.address = jsonObject2.getString("address");
                                    //  homePostModal.state = jsonObject2.getString("state");
                                    homePostModal.like_count = jsonObject2.getString("like_count");
                                    homePostModal.comment_count = jsonObject2.getString("comment_count");
                                    homePostModal.post_attachment = jsonObject2.getString("post_attachment");
                                    homePostModal.tag_id = jsonObject2.getString("tag_id");
                                    homePostModal.categoryName = jsonObject2.getString("categoryName");
                                    homePostModal.name = jsonObject2.getString("name");
                                    homePostModal.tagName = jsonObject2.getString("tagName");
                                    homePostModal.user_like_status = jsonObject2.getString("user_like_status");
                                    homePostModal.availability = jsonObject2.getString("availability");
                                    homePostModal.isHired = jsonObject2.getString("isHired");
                                    JSONArray tag = jsonObject2.getJSONArray("tags");
                                    for (int j = 0; j < tag.length(); j++) {
                                        JSONObject jsonObject3 = tag.getJSONObject(j);
                                        HomePostModal.Tags tags = new HomePostModal.Tags();
                                        tags.tag_id = jsonObject3.getString("tag_id");
                                        tags.tagName = jsonObject3.getString("tagName");
                                        homePostModal.tags.add(tags);
                                    }
                                    // Log.e(TAG,""+homePostModal.tags.size() );
                                    homePostList.add(homePostModal);
                                }
                                // offset_def += 5;
                            } else showToast(context, message);

                            if (homePostList.size() == 0) {
                                recycler_view.setVisibility(View.GONE);
                                no_data_found.setVisibility(View.VISIBLE);
                            } else {
                                // offset_def += 5;
                                recycler_view.setVisibility(View.VISIBLE);
                                no_data_found.setVisibility(View.GONE);
                            }
                            homeListAdapter.notifyItemRangeInserted(0, homePostList.size());
                            homeListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        progresDilog.hide();
                        // progressBar_cyclic.setVisibility(View.GONE);
                        no_data_found.setVisibility(View.GONE);
                    }
                });
                service.callSimpleVolley("/user/post", map);

            } catch (NullPointerException e) {
                showToast(context, getString(R.string.too_slow));
            }
        } else showToast(context, getString(R.string.no_internet_access));
    }

    @SuppressLint("SetTextI18n")
    private void shareDetailsDialog(HomePostModal detailsModal, String postimage) {

        final Dialog dialog = new Dialog(context);
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
        TextView tv_share_hired_sold = dialog.findViewById(R.id.tv_share_hired_sold);
        TextView details_description_share = dialog.findViewById(R.id.details_description_share);
        TextView details_general_info_share = dialog.findViewById(R.id.details_general_info_share);
        TextView details_contact_info_share = dialog.findViewById(R.id.details_contact_info_share);
        TextView tv_Tag_Detail_share = dialog.findViewById(R.id.tv_Tag_Detail_share);
        ImageView img_place_holder_share = dialog.findViewById(R.id.img_place_holder_share);
        TextView details_contact_info_text_share = dialog.findViewById(R.id.details_contact_info_text_share);
        CardView card_for_share = dialog.findViewById(R.id.card_for_share);
        CardView card_for_close = dialog.findViewById(R.id.card_for_close);
          share_view = dialog.findViewById(R.id.share_view);
        //RecyclerView details_tag_recycler_view_share = dialog.findViewById(R.id.details_tag_recycler_view_share);


        catogary_share.setText(detailsModal.categoryName);
        //details_day_tv_share.setText(getDayDifference(detailsModal.crd, detailsModal.CurrentTime));
        details_title_tv_share.setText(detailsModal.title);
        details_description_share.setText(detailsModal.description);
        // details_contact_info_text_share.setText(detailsModal.contact);

        //cama seprator tag name set on textview on share dialog
        StringBuilder tagBuilder = new StringBuilder();
        for (int i = 0; i < detailsModal.tags.size(); i++) {
            if (i == 0) {
                tagBuilder.append(detailsModal.tags.get(i).tagName);
            } else {
                tagBuilder.append(", ").append(detailsModal.tags.get(i).tagName);
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
            Picasso.with(context).load(postimage).fit().centerCrop()
                    .placeholder(R.drawable.myplaceholder)
                    .error(R.drawable.myplaceholder)
                    .into(img_place_holder_share);
        }

        //  show available sold hired and still searching
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
        card_for_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
                    } else {
                        screenShot(share_view);                    }
                } else {
                    screenShot(share_view);                }


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    screenShot(share_view);

                } else {
                    Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;
/*
            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(RegisterationActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(RegisterationActivity.this);
                } else {
                    Toast.makeText(RegisterationActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }*/

        }
    }

    //*.................................screenShot()...................................*//
    private void screenShot(RelativeLayout share_view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        File f = new File(Environment.getExternalStorageDirectory(), "/Mabwe/Shared Profiles");
        if (!f.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory(),"/Mabwe/Shared Profiles");
            wallpaperDirectory.mkdirs();
        }

        try {
            String mPath = f.getAbsoluteFile()+ "/" + now + ".png";
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
    }

    private void sharOnsocial(File imageFile) {
        Uri uri;
        Intent sharIntent = new Intent(Intent.ACTION_SEND);
        String ext = imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sharIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", imageFile);
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


    @Override
    public void shareClickOn(HomePostModal homePostModal) {
        shareDetailsDialog(homePostModal, homePostModal.post_attachment);
    }
}
