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
import ours.china.hours.Adapter.ProfileModificationAdapter;
import ours.china.hours.Model.Profile;
import ours.china.hours.R;

public class ProfileModificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Profile> mProfileModificationList;
    ProfileModificationAdapter adapter;
    RecyclerView recyclerProfileModification;
    ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modification);

        init();
    }

    public void init() {
        // toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileModificationActivity.super.onBackPressed();
            }
        });

        // recyclerview
        mProfileModificationList = new ArrayList<>();
        mProfileModificationList.add(new Profile("用户名", "Leo"));
        mProfileModificationList.add(new Profile("身份认证", "吴彦祖"));
        mProfileModificationList.add(new Profile("面部认证", "已认证"));
        mProfileModificationList.add(new Profile("手机号", "1860000****"));
        mProfileModificationList.add(new Profile("修改密码", ""));
        mProfileModificationList.add(new Profile("学校", "辽宁科技大学"));
        mProfileModificationList.add(new Profile("班级", "电商16"));
        mProfileModificationList.add(new Profile("学号", "162016"));

        recyclerProfileModification = findViewById(R.id.recyclerProfileModification);
        adapter = new ProfileModificationAdapter(mProfileModificationList, ProfileModificationActivity.this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ProfileModificationActivity.this);
        recyclerProfileModification.setLayoutManager(manager);

        recyclerProfileModification.setAdapter(adapter);

    }
}
