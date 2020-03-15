package com.example.rickandmortyapi.Listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;

    protected RecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * Used to load next page if the bottom is reached
     */
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount < getTotalItemsCount()
            ) {
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    protected abstract int getTotalItemsCount();

    protected abstract boolean isLastPage();

    protected abstract boolean isLoading();
}
