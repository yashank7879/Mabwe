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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

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
import mabwe.com.mabwe.adapter.AddTagAdapter;
import mabwe.com.mabwe.adapter.GroupCategoryAdapter;
import mabwe.com.mabwe.adapter.TagListAdapter;
import mabwe.com.mabwe.customListner.CategoryClick;
import mabwe.com.mabwe.customListner.CustomTagLIstener;
import mabwe.com.mabwe.customListner.RemoveListItemListener;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.modals.GroupCreateModal;
import mabwe.com.mabwe.modals.TagModal;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.utils.Utils;
import mabwe.com.mabwe.utils.Validation;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout layout_view_group, group_clear_button;
    private AutoCompleteTextView et_serch_post_group;
    private RecyclerView group_recycler_view, group_tag_recycler_view, select_group_category_rv;
    private ImageView iv_group_back;
    private EditText group_title;
    private RelativeLayout group_addpost_show;
    private ImageView group_picture_iv;
    private Bitmap bitmap;
    private ArrayList<CategoryModal> groupCategrylist;
    private ArrayList<TagModal> tagItemList;
    private GroupCategoryAdapter groupCategoryAdapter;
    private ImageView gropu_iv_down;
    private boolean fun = true;
    private View hide_view_group;
    private TextView group_name;
    private String selectedCategory = "";
    private String selectgoryid = "";
    private Dialog dialog = null;
    private RelativeLayout show_addicon_view;
    private MainActivity mainActivity;

    private String finalTagList = "";
    private ArrayList<TagModal> tagArrarylist;
    private AddTagAdapter addTagAdapter;
    private TagListAdapter tagListAdapter;
    private Dialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        category_Api();
        hidekeywordOnClick();
        findWidgetsViewById();
    }

    /*.............................findWidgetsViewById.................................*/
    private void findWidgetsViewById() {
        mainActivity = new MainActivity();
        groupCategrylist = new ArrayList<>();
        tagItemList = new ArrayList<>();
        tagArrarylist = new ArrayList<>();

        layout_view_group = findViewById(R.id.layout_view_group);
        gropu_iv_down = findViewById(R.id.gropu_iv_down);
        group_recycler_view = findViewById(R.id.group_recycler_view);
        group_title = findViewById(R.id.group_title);
        group_addpost_show = findViewById(R.id.group_addpost_show);
        group_tag_recycler_view = findViewById(R.id.group_tag_recycler_view);
        group_clear_button = findViewById(R.id.group_clear_button);
        iv_group_back = findViewById(R.id.iv_group_back);
        group_picture_iv = findViewById(R.id.group_picture_iv);
        hide_view_group = findViewById(R.id.hide_view_group);
        select_group_category_rv = findViewById(R.id.select_group_category_rv);
        group_name = findViewById(R.id.group_name);

        setOnClickListener();

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


        group_tag_recycler_view.setAdapter(addTagAdapter);
        /*............................CatgoryAdapter..........................*/
        groupCategoryAdapter = new GroupCategoryAdapter(groupCategrylist, GroupActivity.this, new CategoryClick() {
            @Override
            public void getCategoryClick(int position) {

                hidekeywordOnClick();

                if (groupCategrylist.get(position).categoryName.equals("Connect")) {
                    selectgoryid = groupCategrylist.get(position).categoryId;
                } else if (groupCategrylist.get(position).categoryName.equals("Career")) {
                    selectgoryid = groupCategrylist.get(position).categoryId;


                } else if (groupCategrylist.get(position).categoryName.equals("Marketplace")) {
                    selectgoryid = groupCategrylist.get(position).categoryId;

                } else if (groupCategrylist.get(position).categoryName.equals("Mabwe411")) {
                    selectgoryid = groupCategrylist.get(position).categoryId;
                }
                select_group_category_rv.setVisibility(View.GONE);
                hide_view_group.setVisibility(View.GONE);
                group_name.setText(groupCategrylist.get(position).categoryName);
                fun = true;
                gropu_iv_down.setImageResource(R.drawable.down_arrow);

                selectedCategory = groupCategrylist.get(position).categoryName;
                selectgoryid = groupCategrylist.get(position).categoryId;
            }
        });
    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = GroupActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*.............................set_On_Click_Listener.................................*/
    private void setOnClickListener() {
        group_clear_button.setOnClickListener(this);
        layout_view_group.setOnClickListener(this);
        iv_group_back.setOnClickListener(this);
        group_picture_iv.setOnClickListener(this);
        group_addpost_show.setOnClickListener(this);

    }


    /*................................submit_data_with_validation...............................*/
    private void submit_data_with_validation() {
        String groupTitle = group_title.getText().toString().trim();
        Validation validation = new Validation(this);

        if (selectedCategory.isEmpty()) {
            Utils.openAlertDialog(this, getString(R.string.select_category));
        } else if (bitmap == null) {
            Utils.openAlertDialog(GroupActivity.this, getString(R.string.please_select_one_image));
        } else if (!validation.isEmpty(groupTitle)) {
            Utils.openAlertDialog(this, "Please Enter Title");
            group_title.requestFocus();
        } else {
            create_Group();
        }
    }


    /*.................................onClick....................................*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_view_group:
                if (!fun) {
                    gropu_iv_down.setImageResource(R.drawable.down_arrow);
                    select_group_category_rv.setVisibility(View.GONE);
                    hide_view_group.setVisibility(View.GONE);
                    fun = true;
                } else {
                    fun = false;
                    gropu_iv_down.setImageResource(R.drawable.up_arrow);
                    hide_view_group.setVisibility(View.VISIBLE);
                    select_group_category_rv.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.group_clear_button:
                submit_data_with_validation();
                break;

            case R.id.iv_group_back:
                hidekeywordOnClick();
                onBackPressed();
                break;

            case R.id.group_picture_iv:
                getPermissionAndPicImage();
                break;
            case R.id.group_addpost_show:
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
                    Toast.makeText(GroupActivity.this, "Please enter tag name", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GroupActivity.this, "Same Tags not allowed", Toast.LENGTH_SHORT).show();
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
            //Api calling
            tag_Api(tagListAdapter);
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
                            //when tag is blank
                            if (!tagModal.tagName.isEmpty()) {
                                tagItemList.add(tagModal);
                            }
                        }

                    } else {
                        Utils.openAlertDialog(GroupActivity.this, message);
                    }
                    tagListAdapter.notifyDataSetChanged();
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


    /*.....................................category_Api.........................................*/
    private void category_Api() {

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

                            groupCategrylist.add(categoryModal);
                        }

                        select_group_category_rv.setAdapter(groupCategoryAdapter);
                        groupCategoryAdapter.notifyDataSetChanged();

                    } else {
                        Utils.openAlertDialog(GroupActivity.this, message);
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
    }


    /*............................get_Permission_And_PicImage............................*/
    public void getPermissionAndPicImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(GroupActivity.this);
            }
        } else {
            ImagePicker.pickImage(GroupActivity.this);
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
                    Toast.makeText(GroupActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(GroupActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(GroupActivity.this);
                } else {
                    Toast.makeText(GroupActivity.this, R.string.your_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    /*................................onActivityResult...................................*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                Uri imageUri = ImagePicker.getImageURIFromResult(GroupActivity.this, requestCode, resultCode, data);

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
                        group_picture_iv.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*................................create_Group...................................*/
    private void create_Group() {
        mdialog = Mabwe.showProgress(GroupActivity.this);
        Map<String, String> param = new HashMap<>();
        param.put("group", group_title.getText().toString().trim());
        param.put("category_id", selectgoryid);
        param.put("tags", finalTagList);

        Log.e("groupcreate param value", "" + param);

        Map<String, Bitmap> map = new HashMap<>();

        if (bitmap != null) {
            map.put("groupImage", bitmap);
        }

        WebService service = new WebService(this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {
                Mabwe.hideProgress(mdialog);


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    Log.e("CREAT GROUP", response);

                    if (status.equals("success")) {

                        JSONObject userDetails = jsonObject.getJSONObject("data");
                        String groupId = userDetails.getString("groupId");
                        String groupName = userDetails.getString("groupName");
                        String groupImage = userDetails.getString("groupImage");
                        String isAdmin = userDetails.getString("isAdmin");
                        String userType = userDetails.getString("userType");

                        GroupCreateModal groupCreateModal = new GroupCreateModal();
                        groupCreateModal.groupId = groupId;
                        groupCreateModal.groupName = groupName;
                        groupCreateModal.groupImage = groupImage;
                        groupCreateModal.isAdmin = isAdmin;
                        groupCreateModal.userType = userType;

                        profileUpdated(GroupActivity.this, message);

                    } else {
                        profileUpdated(GroupActivity.this, message);
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

        service.callMultiPartApi("/user/group", param, map);
    }

    public void profileUpdated(Context context, String message) {
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

    /*.................................onBackPressed....................................*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
