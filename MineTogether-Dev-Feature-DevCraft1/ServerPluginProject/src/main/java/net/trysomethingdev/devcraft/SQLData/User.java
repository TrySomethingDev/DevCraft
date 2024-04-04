package net.trysomethingdev.devcraft.SQLData;

import lombok.Getter;

@Getter
public class User {


    private int id;
    private String userName;
    private String email;
    private int level;


    public User(int userId, String username, String email, int level) {

        id = userId;
        userName = username;
        this.email = email;
        this.level = level;

    }
}
