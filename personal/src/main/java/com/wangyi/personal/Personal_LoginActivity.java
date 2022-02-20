package com.wangyi.personal;

import android.os.Bundle;

import com.wangyi.annotation.ARouter;

import androidx.appcompat.app.AppCompatActivity;

@ARouter(path = "/personal/Personal_LoginActivity", group = "personal")
public class Personal_LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);
    }
}