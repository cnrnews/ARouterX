package com.wangyi.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wangyi.annotation.ARouter;
import com.wangyi.annotation.Parameter;
import com.wangyi.api.ParameterManager;
import com.wangyi.api.RouterManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@ARouter(path = "/order/OrderMainActivity")
public class OrderMainActivity extends AppCompatActivity {

    @Parameter
    String name;
    @Parameter
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        // 初始化Intent 参数
        ParameterManager.getInstance().loadParameter(this);

        Log.e("TAG", "OrderMainActivity name>>" + name +", age = "+ age);
    }
    public void jumpApp(View view) {
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withResultString("call","jale")
                .navigation(this);
    }

    public void jumpPersonal(View view) {
        RouterManager.getInstance()
                .build("/personal/PersonalMainActivity")
                .withString("name","jale")
                .navigation(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            Log.e("TAG","OrderMainActivity onActivityResult: "+ data.getStringExtra("call"));
        }
    }
}