package com.hackathon.accelerator.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("Registered")
public class BaseActivity extends Activity {

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        int SDK = android.os.Build.VERSION.SDK_INT;
        if (SDK > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        return super.onCreateView(parent, name, context, attrs);
    }
}
