package mabwe.com.mabwe.splashScreen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.activity.LoginActivity;
import mabwe.com.mabwe.activity.VideoClipActivity;
import mabwe.com.mabwe.activity.MainActivity;
import mabwe.com.mabwe.session.Session;

public class SplashScreen extends AppCompatActivity {
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        session = new Session(this);
        //without layout splash
       Runnable runable= new Runnable(){
            @Override
            public void run() {
                Intent intent;
                if (session.isLoggedIn()) {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        new Handler().postDelayed(runable, 2000);

    }
}






