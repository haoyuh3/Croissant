package com.bytedance.croissantapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Post 数据库实体类 (Room Entity)
 * 用于缓存 Feed 中的帖子数据
 */
@Entity(tableName = "posts")
public class PostEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "post_id")
    private String postId;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    // 复杂类型需要序列化为 JSON 字符串存储
    @NonNull
    @ColumnInfo(name = "hashtags_json")
    private String hashtagsJson;

    @ColumnInfo(name = "create_time")
    private long createTime;

    // Author 对象序列化为 JSON
    @NonNull
    @ColumnInfo(name = "author_json")
    private String authorJson;

    // Clips 列表序列化为 JSON
    @NonNull
    @ColumnInfo(name = "clips_json")
    private String clipsJson;

    // Music 可空，序列化为 JSON
    @Nullable
    @ColumnInfo(name = "music_json")
    private String musicJson;

    @ColumnInfo(name = "like_count")
    private int likeCount;

    @ColumnInfo(name = "is_liked")
    private boolean isLiked;

    // 无参构造函数
    public PostEntity() {
    }

    // 全参构造函数
    public PostEntity(@NonNull String postId, @NonNull String title, @NonNull String content,
                      @NonNull String hashtagsJson, long createTime, @NonNull String authorJson,
                      @NonNull String clipsJson, @Nullable String musicJson,
                      int likeCount, boolean isLiked) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.hashtagsJson = hashtagsJson;
        this.createTime = createTime;
        this.authorJson = authorJson;
        this.clipsJson = clipsJson;
        this.musicJson = musicJson;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    // Getters and Setters
    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public String getHashtagsJson() {
        return hashtagsJson;
    }

    public void setHashtagsJson(@NonNull String hashtagsJson) {
        this.hashtagsJson = hashtagsJson;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @NonNull
    public String getAuthorJson() {
        return authorJson;
    }

    public void setAuthorJson(@NonNull String authorJson) {
        this.authorJson = authorJson;
    }

    @NonNull
    public String getClipsJson() {
        return clipsJson;
    }

    public void setClipsJson(@NonNull String clipsJson) {
        this.clipsJson = clipsJson;
    }

    @Nullable
    public String getMusicJson() {
        return musicJson;
    }

    public void setMusicJson(@Nullable String musicJson) {
        this.musicJson = musicJson;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}