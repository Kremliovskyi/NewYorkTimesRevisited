package com.kreml.andre.newyorktimesrevisited.models;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kreml.andre.newyorktimesrevisited.models.LoaderResult;
import com.kreml.andre.newyorktimesrevisited.models.NYItem;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.utils.InternetChangeReceiver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Utility class to load articles from the server and parse them
 */

public class NYFragmentModel extends ViewModel{

    private List<NYItem> mNYItemList = new ArrayList<>();
    private List<NYItem> mDataList = new ArrayList<>();
    private OkHttpClient mClient = new OkHttpClient();
    private AtomicBoolean mHasRun = new AtomicBoolean();
    private int mTries = 0;
    private int mPageNum = 0;

    public NYFragmentModel() {
    // empty ctor
    }

    public List<NYItem> getDataList() {
        return mDataList;
    }

    public void addToDataList(List<NYItem> list) {
        mDataList.addAll(list);
    }

    public void resetDataList(List<NYItem> list) {
        mDataList.addAll(0, list);
    }

    public int getTries() {
        return mTries;
    }

    public int getPageNum() {
        return mPageNum;
    }

    public void clearPageNum() {
        mPageNum = 0;
    }

    public void clearTries() {
        mTries = 0;
    }

    public void incrementTries() {
        mTries++;
    }

    public void incrementPageNum() {
        mPageNum++;
    }

    public boolean wasNotRun() {
        return mHasRun.compareAndSet(false, true);
    }


    public Observable<LoaderResult> loadArticles(Bundle bundle, int mode) {

        return Observable.create((ObservableOnSubscribe<LoaderResult>) e -> {
            mNYItemList.clear();
            String query = bundle.getString(Constants.QUERY);
            int pageNum = bundle.getInt(Constants.PAGE_NUMBER);
            if (InternetChangeReceiver.isNetworkAvailable()) {
                loadArticles(query, pageNum);
                if (mNYItemList.isEmpty()) {
                   e.onError(new Exception("Empty data"));
                } else {
                    e.onNext(new LoaderResult(mNYItemList, mode));
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private synchronized void loadArticles(String query, int pageNum) {
        String articleUrl = constructArticleUrl(query, pageNum);
        try {
            Response response = makeRequest(articleUrl);
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(body.byteStream())).getAsJsonObject();
                    JsonArray array = jsonObject.getAsJsonObject(Constants.RESPONSE).getAsJsonArray(Constants.DOCS);
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject articleObject = (JsonObject) array.get(i);
                        if (!articleObject.get(Constants.HEADLINE).isJsonObject()) {
                            continue;
                        }
                        NYItem nyItem = parseItem(articleObject);
                        if (nyItem != null) {
                            mNYItemList.add(nyItem);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response makeRequest(String articleUrl) throws IOException {
        URL url = new URL(articleUrl);
        Request request = new Request.Builder()
                .url(url)
                .build();
        return mClient.newCall(request).execute();
    }

    private String constructArticleUrl(String query, int pageNum) {
        return Uri.parse(Constants.ARTICLE_BASE)
                .buildUpon()
                .appendQueryParameter(Constants.API_KEY, Constants.ARTICLE_KEY)
                .appendQueryParameter(Constants.FILTERED_QUERY, "section_name:(\"" + query + "\")")
                .appendQueryParameter(Constants.PAGE, String.valueOf(pageNum))
                .appendQueryParameter(Constants.SORT, Constants.NEWEST)
                .build().toString();
    }

    private NYItem parseItem(JsonObject articleObject) {
        String webUrl = articleObject.get(Constants.WEB_URL).getAsJsonPrimitive().getAsString();
        String snippet = articleObject.get(Constants.SNIPPET).isJsonPrimitive() ? articleObject.get(Constants.SNIPPET).getAsJsonPrimitive().getAsString() : "";
        String headline = articleObject.get(Constants.HEADLINE).getAsJsonObject().get(Constants.MAIN).isJsonPrimitive() ?
                articleObject.get(Constants.HEADLINE).getAsJsonObject().get(Constants.MAIN).getAsJsonPrimitive().getAsString()
                : "";
        String image = articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().size() > 0 &&
                articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(0).getAsJsonObject().get(Constants.URL).isJsonPrimitive() ?
                articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().size() > 1 ?
                        articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(1).getAsJsonObject().get(Constants.URL).getAsJsonPrimitive().getAsString() :
                        articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(0).getAsJsonObject().get(Constants.URL).getAsJsonPrimitive().getAsString() :
                "";

        if (!TextUtils.isEmpty(image)) {
            image = Uri.parse(Constants.NEW_YORK_SITE).buildUpon().appendEncodedPath(image).toString();
        } else {
            image = "";
        }
        NYItem.NYItemBuilder builder = new NYItem.NYItemBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            snippet = Html.fromHtml(snippet, Html.FROM_HTML_MODE_LEGACY).toString();
            headline = Html.fromHtml(headline, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            snippet = Html.fromHtml(snippet).toString();
            headline = Html.fromHtml(headline).toString();
        }
        return builder.setWebUrl(webUrl).setSnippet(snippet).setHeadLine(headline).setPhoto(image).build();
    }
}
