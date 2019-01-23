package mabwe.com.mabwe.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.LoginActivity;
import mabwe.com.mabwe.fragments.SettingFragment;
import mabwe.com.mabwe.modals.UserInfo;

/**
 * Created by Anil on 27/6/18.
 */

public class Session {

    private static final String REMEMBER_PWD = "password";
    private static final String REMEMBER_NAME = "email";
    private Context context;
    // private Activity activity;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences.Editor editorRem;

    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String Filter_id = "filterId";
    public SharedPreferences myprefRemember;
    private static final String PREF_NAMELAN = "lan";


    public Session(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();

        myprefRemember = this.context.getSharedPreferences(PREF_NAMELAN, Context.MODE_PRIVATE);
        editorRem = myprefRemember.edit();
        editorRem.apply();

    }

    public void createSession(UserInfo userInfo) {
        editor.putString("userId", userInfo.userId);
        editor.putString("fullName", userInfo.fullName);
        editor.putString("email", userInfo.email);
        editor.putString("authToken", userInfo.authToken);
        editor.putString("deviceToken", userInfo.deviceToken);
        editor.putString("deviceType", userInfo.deviceType);
        editor.putString("profileImage", userInfo.profileImage);
        editor.putString("profession", userInfo.profession);
        editor.putString("country", userInfo.country);
        editor.putString("user_status", userInfo.user_status);
        editor.putBoolean(IS_LOGGEDIN, true);
//        editor.putBoolean(IS_FIrebaseLogin,
//                isFirebaseLogin);
//        editor.putString("authToken", signInInfo.userDetail.authToken);
        editor.commit();
    }


    public void session_for_Login(String email, String password) {
        editorRem.putString("email", email);
        editorRem.putString("password", password);
        editorRem.commit();
    }

    public String getName() {
        return sharedPreferences.getString("fullName", "");
    }
    public String getUserId() {
        return sharedPreferences.getString("userId", "");
    }



    public String getCountry() {
        return sharedPreferences.getString("country", "");
    }


     public String getProfession() {
        return sharedPreferences.getString("profession", "");
    }


    public String getProfileImage() {
        return sharedPreferences.getString("profileImage", "");
    }

    public String getEmail() {
        return myprefRemember.getString("email", "");
    }

    public String getPassword() {
        return myprefRemember.getString("password", "");
    }


    public void logout() {
        editor.clear();
        editor.apply();

        Intent showLogin = new Intent(context, LoginActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(showLogin);

        ((Activity) context).finish();
    }

    public String[] getStringPreferenceRem() {
        String arr[] = new String[2];
        String name = myprefRemember.getString(REMEMBER_NAME, "");
        String pwd = myprefRemember.getString(REMEMBER_PWD, "");

        arr[0] = name;
        arr[1] = pwd;
        return arr;
    }

    public void setStringPreferenceRem(String name, String pwd) {
        editorRem.putString(REMEMBER_NAME, name);
        editorRem.putString(REMEMBER_PWD, pwd);
        editorRem.apply();

    }


    public void setSelected(String filterValue) {
        editor.putString(Filter_id, filterValue);
        editor.apply();
        editor.commit();
    }

    public String getSelected() {
        return sharedPreferences.getString(Filter_id, "");
    }


    public String getAuthToken() {
        return sharedPreferences.getString("authToken", "");
    }


    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGEDIN, false);
    }



}
