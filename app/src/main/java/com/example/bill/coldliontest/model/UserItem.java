package com.example.bill.coldliontest.model;

/**
 * Created by Bill Wang on 2015/9/17.
 */
public class UserItem {

    String userId; //"User1,
    String userName; //"User1 Last,
    String language; //"English,
    String theme; //"Default

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
