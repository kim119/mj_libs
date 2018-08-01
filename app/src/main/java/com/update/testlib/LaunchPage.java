package com.update.testlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kim
 * 2018/1/26.
 */

public class LaunchPage extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.update.updatelib.MainActivity.launchMainActivity(LaunchPage.this,MainActivity.class,8,R.mipmap.ic_launcher);
    }

}
