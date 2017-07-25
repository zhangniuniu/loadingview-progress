package com.zhangniuniu.loadinganimator;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LoadingView loadingView = (LoadingView) findViewById(R.id.loadingView);
        Button btButton = (Button) findViewById(R.id.bt);
        btButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if (loadingView.isAnim()){
                    loadingView.stopAnimator();
                }else {
                    loadingView.startAnimator();
                }
            }
        });
    }
}
