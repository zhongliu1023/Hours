package ours.china.hours.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.ProfileAdapter;
import ours.china.hours.Model.Profile;
import ours.china.hours.R;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Profile> mProfileList;
    ProfileAdapter adapter;
    RecyclerView recyclerProfile;
    ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    public void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.super.onBackPressed();
            }
        });

        mProfileList = new ArrayList<>();
        mProfileList.add(new Profile("我的关注", "5"));
        mProfileList.add(new Profile("个人信息修改", ""));
        mProfileList.add(new Profile(" 软件声明", ""));
        mProfileList.add(new Profile("常见问题", ""));
        mProfileList.add(new Profile("意见反馈", ""));
        mProfileList.add(new Profile("关于我们", ""));
        mProfileList.add(new Profile("检测更新", "新版本"));
        mProfileList.add(new Profile("换账号登录", ""));
        mProfileList.add(new Profile("退出", ""));

        recyclerProfile = findViewById(R.id.recyclerProfile);
        adapter = new ProfileAdapter(mProfileList, ProfileActivity.this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ProfileActivity.this);
        recyclerProfile.setLayoutManager(manager);

        recyclerProfile.setAdapter(adapter);

    }
}
