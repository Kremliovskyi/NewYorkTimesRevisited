package com.kreml.andre.newyorktimesrevisited.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.content.NYRecyclerAdapter;
import com.kreml.andre.newyorktimesrevisited.models.LoaderResult;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.utils.InternetChangeReceiver;
import com.kreml.andre.newyorktimesrevisited.models.NYFragmentModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Fragment holding the list of NY articles with endless scroll
 */
public class NYFragment extends Fragment implements NYRecyclerAdapter.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener, InternetChangeReceiver.OnInternetListener,
        Observer<LoaderResult> {

    private NYRecyclerAdapter mRecyclerAdapter;
    private Context mContext;
    private SwipeRefreshLayout mFragment;
    private NYFragmentModel mFragmentModel;
    private InternetChangeReceiver mInternetReceiver = new InternetChangeReceiver();

    @Override
    public void onScrollEnd() {
        initiateNewLoading();
    }

    public static NYFragment newInstance(String query) {
        NYFragment nyFragment = new NYFragment();
        Bundle sBundle = new Bundle();
        //Page number is associated with queries in assets
        sBundle.putString(Constants.QUERY, query);
        sBundle.putInt(Constants.PAGE_NUMBER, 0);
        nyFragment.setArguments(sBundle);
        return nyFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mFragmentModel = ViewModelProviders.of(this).get(NYFragmentModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(mInternetReceiver, new IntentFilter(Constants.CONNECTIVITY_CHANGE));
        mInternetReceiver.setInternetListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mInternetReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment, container, false);
        mFragment.setOnRefreshListener(this);
        RecyclerView recyclerView = mFragment.findViewById(R.id.recycler);
        mRecyclerAdapter = new NYRecyclerAdapter(getActivity(), mFragmentModel.getDataList());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setOnScrollListener(this);
        startFirstLoading();
        return mFragment;
    }

    private void startFirstLoading() {
        if(mFragmentModel.wasNotRun()){
            mRecyclerAdapter.setLoading(true);
            mFragmentModel.loadArticles(getArguments(), Constants.ARTICLES).subscribe(this);
        }
    }

    public void initiateNewLoading() {
        mFragmentModel.incrementPageNum();
        Bundle bundle = getArguments();
        bundle.putInt(Constants.PAGE_NUMBER, mFragmentModel.getPageNum());
        mRecyclerAdapter.setLoading(true);
        mFragmentModel.loadArticles(bundle, Constants.ARTICLES).subscribe(this);
    }

    @Override
    public void onRefresh() {
        if (InternetChangeReceiver.isNetworkAvailable()) {
            mFragmentModel.clearPageNum();
            mFragmentModel.clearTries();
            Bundle bundle = getArguments();
            bundle.putInt(Constants.PAGE_NUMBER, 0);
            mRecyclerAdapter.setLoading(true);
            mFragmentModel.loadArticles(bundle, Constants.REFRESH_ARTICLES).subscribe(this);
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            mRecyclerAdapter.setLoading(false);
            mFragment.setRefreshing(false);
        }

    }

    public void retryLoading() {
        mFragmentModel.incrementTries();
        if (mFragmentModel.getTries() == Constants.TIMES_TO_TRY_DOWNLOADING) {
            mRecyclerAdapter.setLoading(false);
            return;
        }
        Bundle bundle = getArguments();
        bundle.putInt(Constants.PAGE_NUMBER, mFragmentModel.getPageNum());
        mRecyclerAdapter.setLoading(true);
        mFragmentModel.loadArticles(bundle, Constants.ARTICLES).subscribe(this);
    }

    @Override
    public void initiateLoading() {
        retryLoading();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(LoaderResult result) {
        if (!mFragmentModel.getDataList().isEmpty() && mFragmentModel.getDataList().containsAll(result.getNYItemList())) {
            onComplete();
            return;
        }
        mFragmentModel.clearTries();
        onComplete();
        if (result.getMode() == Constants.ARTICLES) {
            mFragmentModel.addToDataList(result.getNYItemList());
        } else if (result.getMode() == Constants.REFRESH_ARTICLES) {
            mFragmentModel.resetDataList(result.getNYItemList());
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e) {
        Log.e(this.getClass().getSimpleName(), e.getMessage());
        if (InternetChangeReceiver.isNetworkAvailable()) {
            retryLoading();
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            onComplete();
        }
    }

    @Override
    public void onComplete() {
        mRecyclerAdapter.setLoading(false);
        mFragment.setRefreshing(false);
    }
}
