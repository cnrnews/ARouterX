package com.wangyi.order;

import android.os.Bundle;
import android.util.Log;

import com.wangyi.annotation.ARouter;

import androidx.appcompat.app.AppCompatActivity;

@ARouter(path="/order/Order_DetailActivity",group="")
public class Order_DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        if (getIntent()!=null){
            String name = getIntent().getStringExtra("name");
            Log.e("TAG","OrderMainActivity name>>"+name);
        }
    }
}