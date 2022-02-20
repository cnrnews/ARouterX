package com.netease.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wangyi.annotation.ARouter;
import com.wangyi.annotation.Parameter;
import com.wangyi.api.RouterManager;
import com.wangyi.common.order.drawable.OrderDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@ARouter(path = "/app/MainActivity")
public class MainActivity2 extends AppCompatActivity {

    @Parameter
    String name;
    @Parameter
    int age;

    @Parameter(name = "/order/getDrawable")
    OrderDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        name = getIntent().getStringExtra("name");
        age = getIntent().getIntExtra("age", 0);

    }

    /**
     * 页面跳转 OrderMainActivity
     *
     * @param view
     */
    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/order/OrderMainActivity")
                .withString("name", "ordername")
                .navigation(this, 10);
    }

    public void jumpPersonal(View view) {
        RouterManager.getInstance()
                .build("/personal/PersonalMainActivity")
                .withString("name", "ordername")
                .navigation(this, 190);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.e("TAG", "MainActivity2 onActivityResult: " + data.getStringExtra("call"));
        }
    }
}
