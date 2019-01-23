package mabwe.com.mabwe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mabwe.com.mabwe.R;

public class ServerNotWrkActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView  server_try_again;
    private ImageView  iv_server_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_not_wrk);

        server_try_again = findViewById(R.id.server_try_again);
        iv_server_back = findViewById(R.id.iv_server_back);
        server_try_again.setOnClickListener(this);
        iv_server_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.server_try_again:
                onBackPressed();
                break;

            case R.id.iv_server_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
