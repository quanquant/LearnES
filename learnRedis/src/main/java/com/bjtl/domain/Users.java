package com.bjtl.domain;

import java.io.Serializable;
import java.util.Objects;

public class Users implements Serializable {
    private static final long serialVersionUID = 3570644049472044849L;

    public Users() {
    }

    public Users(int userId, String userName, int userSex) {
        this.userId = userId;
        this.userName = userName;
        this.userSex = userSex;
    }

    private int userId;
    private String userName;
    private int userSex;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userSex=" + userSex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return userId == users.userId &&
                userSex == users.userSex &&
                Objects.equals(userName, users.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, userSex);
    }
}
