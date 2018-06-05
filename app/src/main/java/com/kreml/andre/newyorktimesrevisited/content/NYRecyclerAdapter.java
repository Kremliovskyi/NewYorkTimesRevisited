package com.kreml.andre.newyorktimesrevisited.content;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.activities.WebViewActivity;
import com.kreml.andre.newyorktimesrevisited.models.NYItem;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapter for list of NY articles
 */

public class NYRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NYItem> mList;
    private OnScrollListener mOnScrollListener;
    private AtomicBoolean mLoading = new AtomicBoolean();

    public interface OnScrollListener {
        void onScrollEnd();
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public NYRecyclerAdapter(Context mContext, List<NYItem> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!mLoading.get() && totalItemCount == lastVisibleItem + 1) {
                    mOnScrollListener.onScrollEnd();
                    mLoading.set(true);
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType) {
            case Constants.LOADING_VIEW_HOLDER:
                view = inflater.inflate(R.layout.loading_item, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.items_layout, parent, false);
                viewHolder = new ArticleViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleViewHolder) {
            ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
            String imageUrl = mList.get(position).getPhoto();
            if (position == 0 && !TextUtils.isEmpty(imageUrl)) {
                articleViewHolder.mFirstItemPhoto.setVisibility(View.VISIBLE);
                articleViewHolder.mItemPhoto.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .override(Constants.MAIN_PHOTO_WIDTH, Constants.MAIN_PHOTO_HEIGHT)
                                .centerInside())
                        .into(articleViewHolder.mFirstItemPhoto);
            } else {
                if (!TextUtils.isEmpty(imageUrl)) {
                    articleViewHolder.mFirstItemPhoto.setVisibility(View.GONE);
                    articleViewHolder.mItemPhoto.setVisibility(View.VISIBLE);
                    articleViewHolder.mItemPhoto.setImageDrawable(null);
                    Glide.with(mContext)
                            .load(imageUrl)
                            .apply(new RequestOptions()
                                    .override(Constants.SMALL_PHOTO_SIZE, Constants.SMALL_PHOTO_SIZE)
                                    .centerInside())
                            .into(articleViewHolder.mItemPhoto);
                } else {
                    articleViewHolder.mItemPhoto.setVisibility(View.GONE);
                    articleViewHolder.mFirstItemPhoto.setVisibility(View.GONE);
                }
            }
            articleViewHolder.mHeadLine.setText(mList.get(position).getHeadLine());
            articleViewHolder.mSnippet.setText(mList.get(position).getSnippet());

            articleViewHolder.mHeadLine.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Constants.URL, mList.get(holder.getAdapterPosition()).getWebUrl());
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) == null ? Constants.LOADING_VIEW_HOLDER : Constants.ARTICLE_VIEW_HOLDER;
    }

    public void setLoading(boolean loading) {
        if(mLoading.compareAndSet(!loading, loading)){
            if (loading) {
                mList.add(null);
                notifyItemInserted(mList.size());
            } else {
                mList.remove(mList.size() - 1);
                notifyItemRemoved(mList.size());
            }
        }
    }

    private class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ImageView mItemPhoto;
        private CardView mCardView;
        private TextView mHeadLine;
        private TextView mSnippet;
        private ImageView mFirstItemPhoto;

        private ArticleViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_view);
            mItemPhoto = mCardView.findViewById(R.id.item_photo);
            mHeadLine = mCardView.findViewById(R.id.headline);
            mSnippet = mCardView.findViewById(R.id.snippet);
            mFirstItemPhoto = mCardView.findViewById(R.id.first_item_photo);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
