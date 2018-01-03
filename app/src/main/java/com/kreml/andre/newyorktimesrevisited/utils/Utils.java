package com.kreml.andre.newyorktimesrevisited.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kreml.andre.newyorktimesrevisited.activities.NYApp;
import com.kreml.andre.newyorktimesrevisited.db.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class Utils {

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!])(?!.*\\s).{8,20})";

    public static String generateHash(String input) {
        input = input.concat("I_am_just_learning");
        StringBuilder hash = new StringBuilder();
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f' };
            for (byte b : hashedBytes) {
                hash.append(digits[(b & 0xf0) >> 4]);
                hash.append(digits[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(Utils.class.getSimpleName(), e.getMessage());
        }

        return hash.toString();
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
    }

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void createUserAccount(String username, String email, String password) {
        new Thread(() -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.beginTransaction();
                User user = realm.createObject(User.class, User.getNextKey(realm));
                user.setUsername(username).setEmail(email).setPassword(password).setPathToImage("");
                realm.commitTransaction();
            }
        }).start();
    }

    public static LinearLayout createNextLinearLayout(Context context) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        return layout;
    }

    public static ArrayList<String> readAssets() {
        ArrayList<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NYApp.getApp().getAssets().open("articles.txt")));
            while (true) {
                String articleName = bufferedReader.readLine();
                if (articleName == null) {
                    break;
                }
                list.add(articleName.trim());
            }
            bufferedReader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
