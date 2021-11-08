package com.dhinesh.twitter.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Tweet {
    private int tweetId;
    private String ownerId;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdAt;
    private String content;
    private int likes;
    private boolean isPublic;

    public Tweet() {
        super();
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "Tweet [content=" + content + ", createdAt=" + createdAt + ", isPublic=" + isPublic + ", likes=" + likes
                + ", ownerId=" + ownerId + ", tweetId=" + tweetId + "]";
    }

}
