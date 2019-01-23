package mabwe.com.mabwe.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.captain_miao.optroundcardview.OptRoundCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mabwe.com.mabwe.Application.Mabwe;
import mabwe.com.mabwe.R;
import mabwe.com.mabwe.adapter.CatgoryAdapter;
import mabwe.com.mabwe.adapter.DynamicCatgoryAdapter;
import mabwe.com.mabwe.fragments.ConnectionFragment;
import mabwe.com.mabwe.fragments.GroupFragment;
import mabwe.com.mabwe.fragments.HomeFragment;
import mabwe.com.mabwe.fragments.NotificationFragment;
import mabwe.com.mabwe.fragments.ProfileFragment;
import mabwe.com.mabwe.fragments.SettingFragment;
import mabwe.com.mabwe.modals.CategoryModal;
import mabwe.com.mabwe.server_task.WebService;
import mabwe.com.mabwe.session.Session;
import mabwe.com.mabwe.utils.ProgresDilog;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    public static final String TAG = Mabwe.class.getSimpleName();
    public ImageView float_addpost;

    boolean hideeicon = false;
    public boolean isclick = false;

    private String floatin_id = "1";
    private String type = "";
    private String notify_id = "";
    private ImageView iv_users, iv_user, iv_home, iv_bell, iv_setting, filter_icon_iv_done;
    private boolean doubleBackToExitPressedOnce;
    private LinearLayout layout_users, layout_user, layout_home, layout_bell, layout_setting;
    private ImageView iv_view;
    private View colrview, view;
    private View view_line;
    private RelativeLayout container;
    private HomeFragment homeFragment;

    private CategoryModal categoryModal;
    private DynamicCatgoryAdapter dynamiccatgoryAdapter;
    private Dialog mdialog;
    public ArrayList<CategoryModal> categarylist;

    private String filter;
    private Session session;
    private String connection;
    private ProgresDilog progresDilog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();


        showTab();
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        //include fragment via layout xml
        HomeFragment fragment = (HomeFragment)fragmentManager.findFragmentById(R.id.frame_layout);
        fragment.category_Api();*/

    }



    /*..........................setUpUI()...................................*/

    private void setUpUI() {
        categarylist = new ArrayList<>();
        progresDilog = new ProgresDilog(this);
        category_Api();

        filter_icon_iv_done = findViewById(R.id.filter_icon_iv_done);
        colrview = findViewById(R.id.colrview);
        iv_view = findViewById(R.id.iv_view);
        container = findViewById(R.id.container);
        view_line = findViewById(R.id.view_line);
        float_addpost = findViewById(R.id.float_addpost);

        setOnclickListener();

        if (getIntent() != null) {
            String body = getIntent().getStringExtra("body");
            type = getIntent().getStringExtra("type");
            String title = getIntent().getStringExtra("title");
            String sound = getIntent().getStringExtra("sound");
            String userId = getIntent().getStringExtra("userId");
            notify_id = getIntent().getStringExtra("notify_id");
            String profileImage = getIntent().getStringExtra("profileImage");
            String click_action = getIntent().getStringExtra("click_action");
        }

        if (getIntent().getStringExtra("type") != null) {
            switch (type) {
                case "mabwe_comment": {
                    Intent comentIntent = new Intent(this, CommentActivity.class);
                    comentIntent.putExtra("notify_id", notify_id);
                    startActivity(comentIntent);
                    break;
                }
                case "mabwe_like":
                    replaceFragment(homeFragment, false, R.id.frame_layout);
                    break;
                case "mabwe_groupComment": {
                    Intent comentIntent = new Intent(this, ShowGroupDetailsActivity.class);
                    comentIntent.putExtra("notify_id", notify_id);
                    startActivity(comentIntent);
                    break;
                }
                case "mabwe_groupLike":
                    GroupFragment groupFragment = new GroupFragment();
                    replaceFragment(groupFragment, false, R.id.frame_layout);
                    break;

                case "Mabweconnect":
                    connection = "Connection";
                    showTab();
                    break;
                default:
                    homeFragment = new HomeFragment();
                    replaceFragment(homeFragment, false, R.id.frame_layout);
                    break;
            }
        } else {
            homeFragment = new HomeFragment();
            replaceFragment(homeFragment, false, R.id.frame_layout);
        }
    }

    private void setOnclickListener() {

        float_addpost.setOnClickListener(this);

    }

    /*....................................hidekeywordOnClick().............................*/
    public void hidekeywordOnClick() {
        View view = MainActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /*.........................onClick()....................................*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.float_addpost:
                Intent intent = null;
                if (floatin_id.equals("1")) {
                    intent = new Intent(this, AddPosttActivity.class);
                    startActivity(intent);
                } else if (floatin_id.equals("2")) {

                    intent = new Intent(this, GroupActivity.class);
                    startActivity(intent);
                }
                break;

        }


    }

    /*....................setInActiveAll()....................................*/

    private void setInActiveAll() {
        iv_users.setImageResource(R.drawable.users_inactive);
        iv_user.setImageResource(R.drawable.user_inactive);
        iv_setting.setImageResource(R.drawable.setting_inactive);
        iv_home.setImageResource(R.drawable.home_inactive);
        iv_bell.setImageResource(R.drawable.bell_inactive);
        float_addpost.setVisibility(View.GONE);
    }

    /*....................replaceFragment()....................................*/
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        while (i > 0) {
            fm.popBackStackImmediate();
            i--;
        }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    /*....................onBackPressed()....................................*/
    @Override
    public void onBackPressed() {
        colrview.setVisibility(View.VISIBLE);
        iv_view.setVisibility(View.VISIBLE);
        view_line.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(getApplicationContext(), R.string.press_back_again, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 1000);
            } else {
                super.onBackPressed();
            }
        }
    }


    /*....................onBackPressed()....................................*/


    //////////////////////////////////////new code for tab////////////////////////////////////////////////
    TabLayout tabLayout;

    private void showTab() {


        tabLayout = findViewById(R.id.contentView);
        tabLayout.removeAllTabs();
        // add  the tab at in the TabLayout

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.users_inactive));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.connection_inactive_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home_inactive));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.bell_inactive));
        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_inactive));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.user_inactive));
        float_addpost.setVisibility(View.GONE);

        tabLayout.addOnTabSelectedListener(this);
        //default selected

        if (connection != null && connection.equals("Connection")) {
            addFragment(new ConnectionFragment(), false, R.id.frame_layout);
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) {
                tab.setIcon(R.drawable.connection_active_tab);
                tab.select();
            }
        } else {
            addFragment(new HomeFragment(), false, R.id.frame_layout);
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            if (tab != null) {
                tab.setIcon(R.drawable.home);
                tab.select();
            }
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
            case 0:
                floatin_id = "2";
                float_addpost.setVisibility(View.VISIBLE);
                tab.setIcon(R.drawable.users_active);
                replaceFragment(new GroupFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;
            case 1:

                tab.setIcon(R.drawable.connection_active_tab);
                replaceFragment(new ConnectionFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;

            case 2:
                floatin_id = "1";
                float_addpost.setVisibility(View.VISIBLE);
                tab.setIcon(R.drawable.home); // set the icon
                replaceFragment(new HomeFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;

            case 3:
                tab.setIcon(R.drawable.bellactive);
                replaceFragment(new NotificationFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;

            case 4:
                tab.setIcon(R.drawable.user);
                replaceFragment(new ProfileFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;
               /* tab.setIcon(R.drawable.setting);
                replaceFragment(new SettingFragment(), false, R.id.frame_layout);
                view_line.setVisibility(View.VISIBLE);
                break;*/
        }

    }

    public void initSettingFrag() {
        colrview.setVisibility(View.GONE);
        iv_view.setVisibility(View.GONE);
        view_line.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        replaceFragment(new SettingFragment(), true, R.id.frame_layout);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

        view_line.setVisibility(View.GONE);
        float_addpost.setVisibility(View.GONE);
        switch (tab.getPosition()) {

            case 0:
                tab.setIcon(R.drawable.users_inactive);

                break;
            case 1:
                tab.setIcon(R.drawable.connection_inactive_tab);

                break;
            case 2:
                tab.setIcon(R.drawable.home_inactive); // set the icon

                break;
            case 3:
                tab.setIcon(R.drawable.bell_inactive);

                break;
            case 4:
                tab.setIcon(R.drawable.user_inactive);
                break;
        }

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {

            /*case 3:
                tab.setIcon(R.drawable.select_logout);
                showDialogConformation();
                break;*/
        }

    }


    ///////////////////fragment clear////////////////////

    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }

    /*.....................................category_Api.........................................*/
    public void category_Api() {

        if (Utils.isNetworkAvailable(MainActivity.this)) {
            try {
                progresDilog.show();
               // mdialog = Mabwe.showProgress(MainActivity.this);

                WebService service = new WebService(MainActivity.this, Mabwe.TAG, new WebService.LoginRegistrationListener() {
                    @Override
                    public void onResponse(String response) {
                        progresDilog.hide();
                       // Mabwe.hideProgress(mdialog);
                        Log.e("CATEGORY", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {

                                JSONArray data = jsonObject.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) data.get(i);

                                    categoryModal = new CategoryModal();
                                    categoryModal.categoryName = jsonObject1.getString("categoryName");
                                    categoryModal.categoryId = jsonObject1.getString("categoryId");
                                    categoryModal.categoryImage = jsonObject1.getString("categoryImage");

                                    /*session = new Session(MainActivity.this);
                                    filter = session.getSelected();
                                    if (filter != null){
                                        if (session.getSelected().equalsIgnoreCase(categoryModal.categoryId))
                                        categoryModal.isSelected=true;
                                    }*/
                                    categarylist.add(categoryModal);

                                }

                            } else {
                                Utils.openAlertDialog(MainActivity.this, message);
                              //  Mabwe.hideProgress(mdialog);
                                progresDilog.hide();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progresDilog.hide();
                            //Mabwe.hideProgress(mdialog);
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        progresDilog.hide();
                      //  Mabwe.hideProgress(mdialog);
                    }
                });
                service.callGetSimpleVolley("/user/categories");
            } catch (NullPointerException e) {
                showToast(MainActivity.this, getString(R.string.too_slow));
            }
        } else showToast(MainActivity.this, getString(R.string.no_internet_access));
    }


}
