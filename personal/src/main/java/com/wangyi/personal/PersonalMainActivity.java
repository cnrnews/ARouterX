package com.wangyi.personal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wangyi.annotation.ARouter;
import com.wangyi.annotation.Parameter;
import com.wangyi.api.ParameterManager;
import com.wangyi.api.RouterManager;

import androidx.appcompat.app.AppCompatActivity;

@ARouter(path = "/personal/PersonalMainActivity", group = "personal")
public class PersonalMainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        ParameterManager.getInstance().loadParameter(this);

        Log.e("TAG","PersonalMainActivity name : >>" +name);
    }

    public void jumpApp(View view) {
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withResultString("call","jale")
                .navigation(this);
    }

    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/order/OrderMainActivity")
                .withResultString("call","PersonalMainActivity call")
                .navigation(this);
    }
}