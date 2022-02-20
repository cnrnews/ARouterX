package com.netease.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.recyclerview.apt.ARouter$$Group$$app;
import com.netease.recyclerview.apt.ARouter$$Group$$order;
import com.squareup.picasso.Picasso;
import com.wangyi.annotation.ARouter;
import com.wangyi.annotation.Parameter;
import com.wangyi.annotation.model.RouterBean;
import com.wangyi.api.core.ARouterLoadGroup;
import com.wangyi.api.core.ARouterLoadPath;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FeedAdapter mFeedAdapter;
    private RelativeLayout mSuspensionBar;
    private TextView mSuspensionTv;
    private ImageView mSuspensionIv;

    private int mSuspensionHeight;
    private int mCurrentPosition;

    @Parameter
    String name;
    @Parameter
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name");
        age = getIntent().getIntExtra("age",0);

        mSuspensionBar = findViewById(R.id.suspension_bar);
        mSuspensionTv = findViewById(R.id.tv_nickname);
        mSuspensionIv = findViewById(R.id.iv_avatar);

        mRecyclerView = findViewById(R.id.recyclerView);
         final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mFeedAdapter = new FeedAdapter();
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //获取悬浮条的高度
                mSuspensionHeight = mSuspensionBar.getHeight();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //对悬浮条的位置进行调整
                //找到下一个itemView
                View view = layoutManager.findViewByPosition(mCurrentPosition + 1);
                if (view != null){
                    if (view.getTop() <= mSuspensionHeight){
                        //需要对悬浮条进行移动
                        mSuspensionBar.setY(-(mSuspensionHeight-view.getTop()));
                    }else{
                        //保持在原来的位置
                        mSuspensionBar.setY(0);
                    }
                }

                if (mCurrentPosition != layoutManager.findFirstVisibleItemPosition()){
                    mCurrentPosition = layoutManager.findFirstVisibleItemPosition();
                    updateSuspensionBar();
                }

            }
        });
        updateSuspensionBar();
    }

    private void updateSuspensionBar() {
        Picasso.with(this)
                .load(getAvatarResId(mCurrentPosition))
                .centerInside()
                .fit()
                .into(mSuspensionIv);
        mSuspensionTv.setText("NetEase "  + mCurrentPosition);
    }

    private int getAvatarResId(int position){
        switch (position % 4){
            case 0:
                return R.drawable.avatar1;
            case 1:
                return R.drawable.avatar2;
            case 2:
                return R.drawable.avatar3;
            case 3:
                return R.drawable.avatar4;
        }
        return 0;
    }

    /**
     * 页面跳转
     * @param view
     */
    public void jumpOrder(View view){
        ARouterLoadGroup loadGroup = new ARouter$$Group$$order();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();

        // app -> ARouterLoadPath
        Class<? extends ARouterLoadPath> clazz = groupMap.get("order");

        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            // path -> RouterBean
            RouterBean routerBean = pathMap.get("/order/OrderMainActivity");
            if(routerBean!=null){
                Intent intent = new Intent(this,routerBean.getClazz());
                intent.putExtra("name","simon");
                intent.putExtra("age",90);
                startActivity(intent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
    public void jumpPersonal(View view){

        ARouterLoadGroup loadGroup = new ARouter$$Group$$app();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();

        // app -> ARouterLoadPath
        Class<? extends ARouterLoadPath> clazz = groupMap.get("personal");

        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            // path -> RouterBean
            RouterBean routerBean = pathMap.get("/personal/PersonalMainActivity");
            if(routerBean!=null){
                Intent intent = new Intent(this,routerBean.getClazz());
                intent.putExtra("name","simon");
                startActivity(intent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
