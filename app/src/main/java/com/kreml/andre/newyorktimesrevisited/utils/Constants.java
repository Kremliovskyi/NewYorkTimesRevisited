package com.kreml.andre.newyorktimesrevisited.utils;

public interface Constants {
    String IS_USER_LOGGED_IN = "is_user_logged_in";
    String PREFERENCE_FILE = "NY_preference";
    String USERNAME = "username";
    String CLICKED_POSITION = "mClickedPosition";
    String PHOTO_FILE_PROVIDER_AUTHORITY = "com.kreml.andre.fileprovider";
    String PAGE_NUMBER = "page_number";
    String QUERY = "query";
    String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    String URL = "url";
    String ARTICLE_BASE = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String ARTICLE_KEY = "c88d9a64ba384e4ba2a4242df4e36610";
    String API_KEY = "api-key";
    String FILTERED_QUERY = "fq";
    String PAGE = "page";
    String SORT = "sort";
    String NEWEST = "newest";
    String RESPONSE = "response";
    String DOCS = "docs";
    String HEADLINE = "headline";
    String WEB_URL = "web_url";
    String SNIPPET = "snippet";
    String MAIN = "main";
    String MULTIMEDIA = "multimedia";
    String NEW_YORK_SITE = "http://www.nytimes.com/";

    int ARTICLES = 0;
    int REFRESH_ARTICLES = 1;
    int TIMES_TO_TRY_DOWNLOADING = 7;
    int PAGE_LIMIT = 2;
    //int VISIBLE_THRESHOLD = 5;
    int PERMISSION_REQUEST_CAMERA = 20;
    int SELECT_PICTURE = 1;
    int CAPTURE_PICTURE = 2;
    int BITMAP_SIZE = 600;
    int WORKING_ACTIVITY = 0x00;
    int LOGIN_FRAGMENT = 0x01;
    int SIGN_UP_FRAGMENT = 0x02;
    int LINEAR_LAYOUT_MARGIN = 16;
    int FILTER_VIEW_MARGIN = 16;
    int FILTER_VIEW_TEXT_SIZE = 50;
    int FILTER_VIEW_HEIGHT = 100;
    int FILTER_VIEW_CHECKBOX_WIDTH = 150;
    int RECTF_LEFT_X = 5;
    int RECTF_TOP_Y = 5;
    int RECTF_RIGHT_X = 10;
    int RECTF_BOTTOM_Y = 10;
    int FILTER_VIEW_TEXT_X = 100;
    int FILTER_VIEW_TEXT_Y = 65;
    int FILTER_VIEW_ARC_START_ANGLE = 100;
    int FILTER_VIEW_ARC_SWEEP_ANGLE = 200;
    int MAIN_PHOTO_WIDTH = 350;
    int MAIN_PHOTO_HEIGHT = 200;
    int SMALL_PHOTO_SIZE = 200;
    int ARTICLE_VIEW_HOLDER = 0;
    int LOADING_VIEW_HOLDER = 1;
}
