package com.bytedance.croissantapp.presentation.follow;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.croissantapp.R;
import com.bytedance.croissantapp.data.local.UserPreferencesRepository;
import com.bytedance.croissantapp.data.local.dao.FollowedUserDao;
import com.bytedance.croissantapp.data.local.entity.FollowedUserEntity;
import com.bytedance.croissantapp.data.model.FollowUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * 关注列表页面
 */
@AndroidEntryPoint
public class FollowListActivity extends AppCompatActivity {

    @Inject
    FollowedUserDao followedUserDao;

    @Inject
    UserPreferencesRepository preferencesRepository;

    private RecyclerView rvFollowList;
    private LinearLayout layoutEmpty;
    private TextView tvFollowCount;
    private ImageView btnBack;

    private FollowListAdapter adapter;
    private List<FollowUser> followUserList;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        initViews();
        initData();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次页面恢复时重新加载数据，确保显示最新的关注列表
        loadDataFromDatabase();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        rvFollowList = findViewById(R.id.rv_follow_list);
        layoutEmpty = findViewById(R.id.layout_empty);
        tvFollowCount = findViewById(R.id.tv_follow_count);
        btnBack = findViewById(R.id.btn_back);

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        followUserList = new ArrayList<>();
    }

    /**
     * 从数据库加载关注的用户列表
     */
    private void loadDataFromDatabase() {
        // 在后台线程查询数据库
        executor.execute(() -> {
            try {
                // 从数据库查询所有关注的用户
                List<FollowedUserEntity> entities = followedUserDao.getAllFollowedUsersOnce();

                // 转换为FollowUser对象
                List<FollowUser> users = new ArrayList<>();
                for (FollowedUserEntity entity : entities) {
                    users.add(new FollowUser(
                        entity.getUserId(),
                        entity.getNickname(),
                        entity.getBio(),
                        entity.getAvatar(),
                        true  // 列表中的用户都是已关注状态
                    ));
                }

                // 在主线程更新UI
                runOnUiThread(() -> {
                    followUserList.clear();
                    followUserList.addAll(users);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    updateUI();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(FollowListActivity.this, "加载关注列表失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new FollowListAdapter();
        adapter.setUserList(followUserList);

        // 设置关注按钮点击监听
        adapter.setFollowClickListener(this::handleFollowClick);

        rvFollowList.setLayoutManager(new LinearLayoutManager(this));
        rvFollowList.setAdapter(adapter);
    }

    /**
     * 处理关注/取消关注操作
     */
    private void handleFollowClick(FollowUser user, int position) {
        // 切换关注状态
        boolean newFollowState = !user.isFollowing();
        user.setFollowing(newFollowState);

        // 同步更新 SharedPreferences
        preferencesRepository.setFollowStatus(user.getUserId(), newFollowState);

        // 在后台线程更新数据库
        executor.execute(() -> {
            try {
                if (newFollowState) {
                    // 重新关注：插入数据库
                    FollowedUserEntity entity = new FollowedUserEntity(
                        user.getUserId(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        user.getBio(),
                        System.currentTimeMillis()
                    );
                    followedUserDao.insertFollowedUser(entity);
                } else {
                    // 取消关注：从数据库删除
                    followedUserDao.deleteFollowedUserById(user.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 更新列表项
        adapter.updateUser(position);

        // 显示提示
        String message = newFollowState ? "已关注 " + user.getUsername() : "已取消关注 " + user.getUsername();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // 更新计数
        updateUI();
    }

    /**
     * 更新UI显示
     */
    private void updateUI() {
        // 计算当前关注的人数
        int followingCount = 0;
        for (FollowUser user : followUserList) {
            if (user.isFollowing()) {
                followingCount++;
            }
        }

        // 更新关注计数
        tvFollowCount.setText("关注 " + followingCount);

        // 根据列表是否为空显示不同的UI
        if (followingCount == 0) {
            rvFollowList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvFollowList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}
