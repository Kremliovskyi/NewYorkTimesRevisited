package com.kreml.andre.newyorktimesrevisited.db;

import android.provider.BaseColumns;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * The RealmObject to hold the info of the user
 */

public class User extends RealmObject {

    @PrimaryKey
    private long _id;
    private String username;
    private String email;
    private String password;
    private String pathToImage;

    public interface Columns {
        String USERNAME = "username";
        String EMAIL = "email";
        String PASSWORD = "password";
        String PATH_TO_IMAGE = "pathToImage";
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }


    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public User setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
        return this;
    }

    public static int getNextKey(Realm realm) {
        try {
            Number number = realm.where(User.class).max(BaseColumns._ID);
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
