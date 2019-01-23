package mabwe.com.mabwe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mabwe.com.cropper.CropImage;
import mabwe.com.cropper.CropImageView;
import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.ImagePickerPackge.ImagePicker;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.SelectedAddTagAdapter;
import mabwe.com.mabwe.adapter.TagListAdapter;
import mabwe.com.mabwe.customListner.CustomTagLIstener;
import mabwe.com.mabwe.customListner.RemoveListItemListener;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.modals.GroupDetailModal;
import mabwe.com.mabwe.modals.TagModal;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView update_group_name;
    private MainActivity mainActivity;
    private ImageView iv_update_back;
    private RoundedImageView update_group_picture_iv;
    private EditText update_group_title;
    private RelativeLayout update_group_addpost_show;
    private RecyclerView update_group_tag_recycler_view;
    private LinearLayout update_group_submit_button;

    private ArrayList<TagModal> updateTagArrarylist;
    private ArrayList<TagModal> tagItemList;
    private String finalTagList = "";
    //    private SelectedTagAdapter selectedTagAdapter;
    private Dialog dialog;
    private Dialog mdialog;
    private RelativeLayout show_addicon_view;
    private Bitmap bitmap;
    //    private GroupDetailModal groupDetailModal;
    private ArrayList<GroupDetailModal> updateTaglist;
    private String group_Id;
    private TagListAdapter tagListAdapter;
    private ArrayList<TagModal> tagArrarylist;
    private GroupDetailModal groupDetailModal;

    private SelectedAddTagAdapter selectedAddTagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        hidekeywordOnClick();
        findWidgetsViewById();
    }

    /*.............................findWidgetsViewById.................................*/
    private void findWidgetsViewById() {
        groupDetailModal = new GroupDetailModal();
        mainActivity = new MainActivity();
        tagItemList = new ArrayList<>();
        updateTagArrarylist = new ArrayList<>();
        updateTaglist = new ArrayList<>();
        tagArrarylist = new ArrayList<>();


        iv_update_back = findViewById(R.id.iv_update_back);
        update_group_name = findViewById(R.id.update_group_name);
        update_group_picture_iv = findViewById(R.id.update_group_picture_iv);
        update_group_title = findViewById(R.id.update_group_title);
        update_group_addpost_show = findViewById(R.id.update_group_addpost_show);
        update_group_tag_recycler_view = findViewById(R.id.update_group_tag_recycler_view);
        update_group_submit_button = findViewById(R.id.update_group_submit_button);

        if (getIntent().getStringExtra("group_id") != null) {
            group_Id = getIntent().getStringExtra("group_id");
            getDetails_api(group_Id);
        }

        setOnClickLIstener();
    }

    private void setOnClickLIstener() {
        update_group_picture_iv.setOnClickListener(this);
        update_group_submit_button.setOnClickListener(this);
        update_group_addpost_show.setOnClickListener(this);
        iv_update_back.setOnClickListener(this);
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = UpdateActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*...............................onClick.....................................*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_group_submit_button:
                submit_data_with_validation();
                break;

            case R.id.iv_update_back:
                hidekeywordOnClick();
                onBackPressed();
                break;

            case R.id.update_group_picture_iv:
                getPermissionAndPicImage();
                break;
            case R.id.update_group_addpost_show:
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
                    Toast.makeText(UpdateActivity.this, "Please enter tag name", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UpdateActivity.this, "Same Tags not allowed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    //if tag name is blank
                    if (!tagModal.tagName.equalsIgnoreCase("")) {
                        tagArrarylist.add(tagModal);
                    }
                    selectedAddTagAdapter.notifyDataSetChanged();
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
                selectedAddTagAdapter.notifyDataSetChanged();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        tag_list_recyclerview.setAdapter(tagListAdapter);

        //if tagItemList is  empty
        if (tagItemList.size() == 0) {
            //Api calling
            tag_Api(tagListAdapter);
        } else {
            //compaire of 2 list tagName
            for (int i = 0; i < tagArrarylist.size(); i++) {
                String selectTag = tagArrarylist.get(i).tagName;
                for (int j = 0; j < tagItemList.size(); j++) {
                    if (selectTag.equalsIgnoreCase(tagItemList.get(j).tagName)) {
                        tagItemList.get(j).istagSelecte = true;
                    }
                }
            }
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
                            tagItemList.add(tagModal);
                        }

                        tagListAdapter.notifyDataSetChanged();

                        //if tagItemList is not empty
                        for (int i = 0; i < tagArrarylist.size(); i++) {
                            String selectTag = tagArrarylist.get(i).tagName;
                            for (int j = 0; j < tagItemList.size(); j++) {
                                if (selectTag.equalsIgnoreCase(tagItemList.get(j).tagName)) {
                                    tagItemList.get(j).istagSelecte = true;
                                }
                            }
                        }
                    } else {
                        Utils.openAlertDialog(UpdateActivity.this, message);
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
        service.callSimpleVolley("/user/tags", map);
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


                for (int i = 0; i < tagItemList.size(); i++) {
                    if (tagItemList.get(i).tagName.equalsIgnoreCase(editable.toString())) {
                        show_addicon_view.setVisibility(View.GONE);
                        return;
                    } else show_addicon_view.setVisibility(View.VISIBLE);

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

    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(UpdateActivity.this);
            }
        } else {
            ImagePicker.pickImage(UpdateActivity.this);
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
                    Toast.makeText(UpdateActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(UpdateActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(UpdateActivity.this);
                } else {
                    Toast.makeText(UpdateActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }


    /*................................submit_data_with_validation...............................*/
    private void submit_data_with_validation() {
        String groupTitle = update_group_title.getText().toString().trim();
        Validation validation = new Validation(this);

        if (!validation.isEmpty(groupTitle)) {
            Utils.openAlertDialog(this, "Please Enter Title");
            update_group_title.requestFocus();
        } else {
            update_Group();
        }
    }

    private void update_Group() {
        mdialog = Mabwe.showProgress(UpdateActivity.this);
        Map<String, String> param = new HashMap<>();
        param.put("group_id", group_Id);
        param.put("groupName", update_group_title.getText().toString().trim());
        param.put("groupImage", groupDetailModal.post_image);
        param.put("tagName", finalTagList);

        Map<String, Bitmap> map = new HashMap<>();

        if (bitmap != null) {
            map.put("groupImage", bitmap);
        }

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {

            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);
                Log.e("UPDATE GROUP", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {

                        dilog(UpdateActivity.this, message);
                    } else {
                        dilog(UpdateActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Mabwe.hideProgress(mdialog);
                Log.e("GroupActivity", error.getMessage());
            }
        });

        service.callMultiPartApi("/user/updateGroups", param, map);
    }

    public void dilog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mabwe");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*.....................................getDetails_api.........................................*/
    private void getDetails_api(String group_post_Id) {
//        progressBar_cyclic.setVisibility(View.VISIBLE);
//        no_found_group_comt.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("offset", "1");
        map.put("group_id", group_post_Id);

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
//                progressBar_cyclic.setVisibility(View.GONE);
//                no_found_group_comt.setVisibility(View.GONE);
                Log.e("GroupDetails", response);
                updateTagArrarylist.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");


                    if (status.equals("success")) {


                        JSONObject data = null;
                        try {
                            data = (JSONObject) jsonObject.get("data");

                            groupDetailModal.groupId = data.getString("groupId");
                            groupDetailModal.groupName = data.getString("groupName");
                            groupDetailModal.category_id = data.getString("category_id");
                            groupDetailModal.Date = data.getString("Date");
                            groupDetailModal.like_count = data.getString("like_count");
                            groupDetailModal.comment_count = data.getString("comment_count");
                            groupDetailModal.post_image = data.getString("post_image");
                            groupDetailModal.members_count = data.getString("members_count");
                            groupDetailModal.categoryName = data.getString("categoryName");
                            groupDetailModal.user_like_status = data.getString("user_like_status");
                            groupDetailModal.isAdmin = data.getString("isAdmin");

                            JSONArray tags = data.getJSONArray("tags");

                            for (int i = 0; i < tags.length(); i++) {

                                JSONObject jsonObject1 = (JSONObject) tags.get(i);
                                TagModal tagModal = new TagModal();
                                tagModal.tagId = jsonObject1.getString("tag_id");
                                tagModal.tagName = jsonObject1.getString("tagName");

                                //when tag is blank
                                if (!tagModal.tagName.isEmpty()) {
                                    tagArrarylist.add(tagModal);
                                }
                            }

                            setData(groupDetailModal);
                            selectedAddTagAdapter.notifyDataSetChanged();

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    } else {
//                        progressBar_cyclic.setVisibility(View.GONE);
//                        no_found_group_comt.setVisibility(View.VISIBLE);
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
        //service.callSimpleVolley("/user/get_group_details", map);
        service.callSimpleVolley("/user/getGroupDetails", map);
    }

    private void setData(GroupDetailModal groupDetailModal) {
        update_group_name.setText(groupDetailModal.categoryName);
        update_group_title.setText(groupDetailModal.groupName);
        update_group_title.setSelection(update_group_title.getText().length());
        Picasso.with(this).load(groupDetailModal.post_image).fit().centerCrop()
                .placeholder(R.drawable.myplaceholder)
                .error(R.drawable.myplaceholder)
                .into(update_group_picture_iv);


        for (int i = 0; i < tagArrarylist.size(); i++) {

                  /*  String name = tagArrarylist.get(i).tagName;
                    finalTagList = name + "," + finalTagList;

                    if (finalTagList.endsWith(",")) {
                        finalTagList = finalTagList.substring(0,finalTagList.length()-1);
                    }*/

            finalTagList = finalTagList + tagArrarylist.get(i).tagName + ",";
        }
        if (finalTagList.endsWith(",")) {
            finalTagList = finalTagList.substring(0, finalTagList.length() - 1);
        }
        Log.e("What", finalTagList);

        /*..............................AddTagAdapter...........................*/

        selectedAddTagAdapter = new SelectedAddTagAdapter(tagArrarylist, this, new RemoveListItemListener() {
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
//                tagListAdapter.notifyDataSetChanged();
                selectedAddTagAdapter.notifyDataSetChanged();
            }
        });

        update_group_tag_recycler_view.setAdapter(selectedAddTagAdapter);
    }

    /*................................onActivityResult...................................*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(UpdateActivity.this, requestCode, resultCode, data);

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setRequestedSize(600, 600, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                            .setAspectRatio(3, 2)
                            .start(this);
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                try {
                    if (result != null)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (bitmap != null) {

                        bitmap = ImagePicker.getImageResized(this, result.getUri());
                        update_group_picture_iv.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*.................................onBackPressed....................................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
