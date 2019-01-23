package mabwe.com.mabwe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.helper.Constant;
import mabwe.com.mabwe.utils.Utils;

public class InternateConActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView network_try_again;
    private ImageView network_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internate_con);

        network_try_again = findViewById(R.id.network_try_again);
        network_back = findViewById(R.id.network_back);
        network_try_again.setOnClickListener(this);
        network_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.network_try_again:
                if (Utils.isNetworkAvailable(this)) {
                    Constant.checkIntenateKey = 1;
                    onBackPressed();

                }else {
                    Toast.makeText(this, "No Internete Connection", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.network_back:
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

