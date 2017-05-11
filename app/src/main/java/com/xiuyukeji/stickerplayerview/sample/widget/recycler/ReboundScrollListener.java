package com.xiuyukeji.stickerplayerview.sample.widget.recycler;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

/**
 * 自动回弹的ScrollListener，需要与FixedItemLayoutManager配合
 *
 * @author Created by jz on 2017/5/11 11:43
 */
public class ReboundScrollListener extends RecyclerView.OnScrollListener {

    private boolean mAutoSet = true;

    @CallSuper
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (mAutoSet && newState == RecyclerView.SCROLL_STATE_IDLE
                && layoutManager instanceof FixedItemLayoutManager) {
            onScrollSelected(((FixedItemLayoutManager) layoutManager).getFirstPosition());
        }
        if (!(layoutManager instanceof FixedItemLayoutManager)) {
            mAutoSet = true;
            return;
        }

        if (!mAutoSet) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                FixedItemLayoutManager manager = ((FixedItemLayoutManager) layoutManager);
                final int scrollNeeded = manager.getProfitOffset();
                recyclerView.smoothScrollBy(scrollNeeded, 0);
                if (scrollNeeded == 0) {
                    onScrollSelected(manager.getFirstPosition());
                }
            }
            mAutoSet = true;
        }
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
            mAutoSet = false;
        }
    }

    public void onScrollSelected(int position) {
    }
}