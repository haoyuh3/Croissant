package com.bytedance.croissantapp.presentation.follow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.croissantapp.R;
import com.bytedance.croissantapp.data.model.FollowUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注列表RecyclerView适配器
 */
public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.FollowViewHolder> {

    private List<FollowUser> userList;
    private OnFollowClickListener followClickListener;

    /**
     * 关注按钮点击回调接口
     */
    public interface OnFollowClickListener {
        void onFollowClick(FollowUser user, int position);
    }

    public FollowListAdapter() {
        this.userList = new ArrayList<>();
    }

    public void setFollowClickListener(OnFollowClickListener listener) {
        this.followClickListener = listener;
    }

    public void setUserList(List<FollowUser> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void updateUser(int position) {
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_user, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        FollowUser user = userList.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder类
     */
    class FollowViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvBio;
        private TextView btnFollow;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvBio = itemView.findViewById(R.id.tv_bio);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }

        public void bind(FollowUser user, int position) {
            // 设置用户信息
            tvUsername.setText(user.getUsername());
            tvBio.setText(user.getBio());

            // 加载头像
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                // 使用 Coil 加载网络图片
                coil.ImageLoader imageLoader = new coil.ImageLoader.Builder(itemView.getContext()).build();
                coil.request.ImageRequest request = new coil.request.ImageRequest.Builder(itemView.getContext())
                        .data(user.getAvatarUrl())
                        .target(ivAvatar)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .build();
                imageLoader.enqueue(request);
            } else {
                // 如果没有头像URL，显示默认图片
                ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // 根据关注状态更新按钮样式
            updateFollowButton(user.isFollowing());

            // 设置关注按钮点击事件
            btnFollow.setOnClickListener(v -> {
                if (followClickListener != null) {
                    followClickListener.onFollowClick(user, position);
                }
            });

            // 设置整个item的点击事件（可以跳转到用户详情页）
            itemView.setOnClickListener(v -> {
                // TODO: 跳转到用户详情页
            });
        }

        /**
         * 更新关注按钮的显示状态
         */
        private void updateFollowButton(boolean isFollowing) {
            if (isFollowing) {
                btnFollow.setText("已关注");
                btnFollow.setTextColor(Color.parseColor("#666666"));
                btnFollow.setBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
                btnFollow.setText("关注");
                btnFollow.setTextColor(Color.parseColor("#FFFFFF"));
                btnFollow.setBackgroundColor(Color.parseColor("#FF6B6B"));
            }
        }
    }
}
