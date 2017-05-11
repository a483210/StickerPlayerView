package com.xiuyukeji.stickerplayerview.sample.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 横向滚动的LayoutManager，有BUG，目前只有添加和重置没有问题
 *
 * @author Created by jz on 2017/5/11 11:43
 */
public class FixedItemLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "FixedItemLayoutManager";

    private static int LEFT = 0, RIGHT = 1;

    private int mItemCount;
    private int mScrollOffset;

    private final SparseBooleanArray mItemAttached;
    private final SparseArray<Rect> mItemFrames;

    public FixedItemLayoutManager() {
        mItemAttached = new SparseBooleanArray();
        mItemFrames = new SparseArray<>();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }

        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        //Todo 该写法有问题，正常情况下应重新计算所有大小，然后定位到最后的位置
        if (mItemCount > itemCount) {
            mScrollOffset = 0;
            mItemFrames.clear();
        }
        mItemCount = itemCount;

        if (mItemCount == 0) {
            return;
        }

        for (int i = 0; i < mItemCount; i++) {
            mItemAttached.put(i, false);
        }

        fill(recycler, state, RIGHT);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mScrollOffset + dx < 0) {
            dx = -mScrollOffset;
        } else {
            Rect rect = mItemFrames.get(mItemCount - 1);
            if (rect != null) {
//                int horizontalSpace = getHorizontalSpace();
//                if (mScrollOffset + dx > rect.right - horizontalSpace) {
//                    dx = rect.right - horizontalSpace - mScrollOffset;
//                }
                if (mScrollOffset + dx > rect.left) {
                    dx = rect.left - mScrollOffset;
                }
            }
        }
        mScrollOffset += dx;

        offsetChildrenHorizontal(-dx);

        if (dx < 0) {
            fill(recycler, state, LEFT);
        } else {
            fill(recycler, state, RIGHT);
        }

        return dx;
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state, int orientation) {
        if (state.isPreLayout()) {
            return;
        }

        // 当前scroll offset状态下的显示区域
        Rect displayFrame = new Rect(mScrollOffset, 0, mScrollOffset + getHorizontalSpace(), getVerticalSpace());

        //回收滑出屏幕的Item
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);

            Rect rect = mItemFrames.get(position);
            //如果不在范围内则回收
            if (rect.right < displayFrame.left || rect.left > displayFrame.right) {
                removeAndRecycleView(child, recycler);
                mItemAttached.put(position, false);
            }
        }

        int firstPosition = getFirstPosition() - 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }

        int offset = 0;
        Rect lastRect = mItemFrames.get(firstPosition);
        if (lastRect != null) {
            offset = lastRect.left;
        }

        for (int i = firstPosition; i < mItemCount; i++) {
            if (offset > mScrollOffset + getHorizontalSpace()) {
                break;
            }

            Rect rect = mItemFrames.get(i);

            if (mItemAttached.get(i)//如果已经附加
                    || rect != null && (rect.right <= displayFrame.left || rect.left >= displayFrame.right)) {//如果不在范围内
                offset = rect.right;
                continue;
            }

            View scrap = recycler.getViewForPosition(i);

            if (orientation == LEFT) {
                addView(scrap, 0);
            } else {
                addView(scrap);
            }

            measureChildWithMargins(scrap, 0, 0);

            int width = getDecoratedMeasuredWidth(scrap);
            int height = getDecoratedMeasuredHeight(scrap);

            if (rect == null) {
                rect = new Rect();
                mItemFrames.put(i, rect);
            }
            rect.set(offset, 0, offset + width, height);

            layoutDecoratedWithMargins(scrap, rect.left - mScrollOffset, rect.top, rect.right - mScrollOffset, rect.bottom);

            mItemAttached.put(i, true);

            offset += width;
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (getChildCount() == 0) {
            return;
        }

        Rect rect = mItemFrames.get(position);
        if (rect == null) {
            return;
        }

        int firstPosition = getPosition(getChildAt(0));
        if (position == firstPosition) {
            return;
        }

        Rect firstRect = mItemFrames.get(firstPosition);
        if (firstRect == null) {
            return;
        }

        mScrollOffset = rect.left;
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (getChildCount() == 0) {
            return;
        }

        Rect rect = mItemFrames.get(position);
        if (rect == null) {
            return;
        }

        int firstPosition = getPosition(getChildAt(0));
        if (position == firstPosition) {
            return;
        }

        Rect firstRect = mItemFrames.get(firstPosition);
        if (firstRect == null) {
            return;
        }

        recyclerView.smoothScrollBy(rect.left - firstRect.left, 0);
    }

    public int getFirstPosition() {
        if (getChildCount() == 0)
            return 0;
        for (int i = getLastPosition(); i >= 0; i--) {
            Rect rect = mItemFrames.get(i);
            if (rect != null && rect.left <= mScrollOffset && rect.right >= mScrollOffset) {
                return i;
            }
        }
        return 0;
    }

    public int getLastPosition() {
        return getPosition(getChildAt(getChildCount() - 1));
    }

    public int getProfitOffset() {
        int position = getFirstPosition();
        Rect rect = mItemFrames.get(position);
        if (rect == null) {
            return 0;
        }
        int leftOffset = mScrollOffset - rect.left;
        int rightOffset = rect.right - mScrollOffset;
        if (leftOffset < rightOffset) {
            return -leftOffset;
        } else {
            return rightOffset;
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}