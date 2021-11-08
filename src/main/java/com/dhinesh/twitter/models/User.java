package com.dhinesh.twitter.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String userId;
    private String avatar;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdAt;
    private String username;
    private String displayName;
    private String password;

    public User() {
        super();
    }

    @Override
    public String toString() {
        return "User [avatar=" + avatar + ", createdAt=" + createdAt + ", displayName=" + displayName + ", password="
                + password + ", userId=" + userId + ", username=" + username + "]";
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
