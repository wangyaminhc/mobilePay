package com.sssoft_lib.mobile.activity;


import android.os.Bundle;
import android.app.Activity;
import android.view.WindowManager;


public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            this.getWindow().setType(
                    WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        }
        super.onWindowFocusChanged(hasFocus);
    }

}
