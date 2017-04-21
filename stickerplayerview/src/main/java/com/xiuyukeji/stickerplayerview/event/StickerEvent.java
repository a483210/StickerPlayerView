package com.xiuyukeji.stickerplayerview.event;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.MatrixBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnCopyListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnSelectedListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnUnselectedListener;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateDegrees;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateEdge;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateSelected;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.flipMatrix;

/**
 * 贴纸帮助类
 *
 * @author Created by jz on 2017/4/11 17:18
 */
public class StickerEvent extends StickerClickEvent {

    public final static int STATE_CANCEL = -2, STATE_NORMAL = -1;
    private final static int STATE_DELETE = 0, STATE_COPY = 1, STATE_DRAG = 2, STATE_FLIP = 3, STATE_TRANSLATE = 4;

    private final static int SCROLL_START = 0, SCROLL_STOP = 1;
    private final static int ACTION_TRANSLATE = 0, ACTION_DRAG = 1, ACTION_DRAG_SECOND = 2;

    private final View mView;

    private final Matrix mInvertMatrix;
    private final Rect mRect;
    private final float[] mPoint;

    private float[] mSidePoint;
    private int mSidePadding;

    private IconBean mDelIconBean;
    private IconBean mCopyIconBean;
    private IconBean mDragIconBean;
    private IconBean mFlipIconBean;

    private final int mTouchSlop;

    private int mDownX;
    private int mMoveX;
    private int mLastMoveX;

    private int mDownY;
    private int mMoveY;
    private int mLastMoveY;

    private float mLastEdge;
    private float mLastDegrees;

    private final int mStickerMaxSize;
    private final int mStickerMinSize;
    private final int mMinBorder;

    private int mScrollState = SCROLL_STOP;
    private int mState = STATE_NORMAL;
    private int mAction = STATE_NORMAL;

    private final ArrayList<StickerBean> mStickers;
    private StickerBean mStickerBean;
    private int mSelectedPosition = STATE_NORMAL;

    private boolean mIsEnabled = true;//是否可用

    private OnDeleteListener mOnDeleteListener;
    private OnCopyListener mOnCopyListener;

    private OnClickStickerListener mOnClickStickerListener;
    private OnDoubleClickStickerListener mOnDoubleClickStickerListener;
    private OnLongClickStickerListener mOnLongClickStickerListener;

    private OnSelectedListener mOnSelectedListener;
    private OnUnselectedListener mOnUnselectedListener;

    public StickerEvent(View view, ArrayList<StickerBean> stickers) {
        this.mView = view;
        this.mStickers = stickers;

        mInvertMatrix = new Matrix();
        mRect = new Rect();
        mPoint = new float[2];

        mSidePoint = new float[8];

        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mTouchSlop = vc.getScaledTouchSlop();

        mStickerMaxSize = 1080;
        mStickerMinSize = 100;
        mMinBorder = 100;
    }

    //初始化设置图标数据
    public void setIcon(IconBean delIconBean, IconBean copyIconBean, IconBean dragIconBean, IconBean flipIconBean,
                        float[] sidePoint, int sidePadding) {
        this.mDelIconBean = delIconBean;
        this.mCopyIconBean = copyIconBean;
        this.mDragIconBean = dragIconBean;
        this.mFlipIconBean = flipIconBean;
        this.mSidePoint = sidePoint;
        this.mSidePadding = sidePadding;
    }

    //设置是否可用
    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
    }

    //设置当前选中索引
    public void selectPosition(int selectedPosition) {
        selected(selectedPosition);
    }

    //获得当前选中
    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mIsEnabled) {
            return true;
        }
        if (mScrollState == SCROLL_START) {
            return onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                if (mSelectedPosition != STATE_NORMAL) {//判断点击上是否为图标
                    if (containPoint(mDelIconBean)) {
                        mState = STATE_DELETE;
                        break;
                    } else if (containPoint(mCopyIconBean)) {
                        mState = STATE_COPY;
                        break;
                    } else if (containPoint(mDragIconBean)) {
                        mState = STATE_DRAG;
                        break;
                    } else if (containPoint(mFlipIconBean)) {
                        mState = STATE_FLIP;
                        break;
                    }
                }
                int count = mStickers.size();
                boolean isSelected = false;
                for (int i = count - 1; i >= 0; i--) {// 判断当前点击是否为贴纸区域
                    StickerBean stickerBean = mStickers.get(i);
                    if (containPoint(stickerBean)) {
                        selected(i);
                        isSelected = true;
                        break;
                    }
                }
                if (isSelected) {// 如果有选择
                    mState = STATE_TRANSLATE;
                    clickDown();
                } else {
                    unselected();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2 && isDragSecond(event)) {
                    startScroll();
                    mAction = ACTION_DRAG_SECOND;
                    return onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) event.getX();
                mMoveY = (int) event.getY();
                mLastMoveX = mMoveX;
                mLastMoveY = mMoveY;

                if (mState == STATE_DRAG) {
                    startScroll();
                    mAction = ACTION_DRAG;
                    return onTouchEvent(event);
                } else if (mState == STATE_TRANSLATE) {
                    int dx = mDownX - mMoveX;
                    int dy = mDownY - mMoveY;

                    if (Math.abs(dx) > mTouchSlop ||
                            Math.abs(dy) > mTouchSlop) {
                        startScroll();
                        mAction = ACTION_TRANSLATE;
                        return onTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX(0);
                float upY = event.getY(0);

                if (mState == STATE_DELETE
                        && containPoint(mDelIconBean, upX, upY)) {
                    delete();
                } else if (mState == STATE_COPY
                        && containPoint(mCopyIconBean, upX, upY)) {
                    copy();
                } else if (mState == STATE_FLIP
                        && containPoint(mFlipIconBean, upX, upY)) {
                    flip();
                } else if (mState == STATE_TRANSLATE &&
                        mScrollState == SCROLL_STOP) {
                    clickUp();
                }
                mState = STATE_NORMAL;
                mScrollState = SCROLL_STOP;
                break;
            default:
                break;
        }
        return true;
    }

    private boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:// 第二点按下
                if (event.getPointerCount() > 2
                        || mAction != ACTION_TRANSLATE
                        && mAction != ACTION_DRAG_SECOND) {
                    break;
                }
                if (isDragSecond(event)) {
                    mAction = ACTION_DRAG_SECOND;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:// 第二点弹起
                if (event.getPointerCount() > 2
                        || mAction != ACTION_DRAG_SECOND) {
                    break;
                }

                mAction = ACTION_TRANSLATE;
                mLastEdge = 0;
                mLastDegrees = 0;

                if (event.getActionIndex() != 0)
                    break;
                mMoveX = (int) event.getX(1);
                mLastMoveX = mMoveX;
                mMoveY = (int) event.getY(1);
                mLastMoveY = mMoveY;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) event.getX(0);
                mMoveY = (int) event.getY(0);

                if (mAction == ACTION_DRAG) {
                    drag(mStickerBean);
                } else if (mAction == ACTION_DRAG_SECOND) {
                    drag(mStickerBean, (int) event.getX(1), (int) event.getY(1));
                } else if (mAction == ACTION_TRANSLATE) {
                    translate(mStickerBean);
                }

                invalidateSelected();
                mView.invalidate();

                mLastMoveX = mMoveX;
                mLastMoveY = mMoveY;
                break;
            case MotionEvent.ACTION_UP:
                mLastEdge = 0;
                mLastDegrees = 0;

                mState = STATE_NORMAL;
                mAction = STATE_NORMAL;
                mScrollState = SCROLL_STOP;
                break;
            default:
                break;
        }
        return true;
    }

    private void startScroll() {
        mScrollState = SCROLL_START;
        cancelLong();
    }

    private boolean isDragSecond(MotionEvent event) {
        if (mStickerBean == null || mState == STATE_CANCEL) {
            return false;
        }
        return containPoint(mStickerBean, event.getX(1), event.getY(1));
    }

    private void drag(StickerBean stickerBean) {
        int px = stickerBean.getDx() + stickerBean.getWidth() / 2;
        int py = stickerBean.getDy() + stickerBean.getHeight() / 2;
        drag(stickerBean, px, py, px, py);
    }

    private void drag(StickerBean stickerBean, int x, int y) {
        int px = stickerBean.getDx() + stickerBean.getWidth() / 2;
        int py = stickerBean.getDy() + stickerBean.getHeight() / 2;
        drag(stickerBean, x, y, px, py);
    }

    private void drag(StickerBean stickerBean, int x, int y, int px, int py) {
        int xEdge = mMoveX - x;
        int yEdge = mMoveY - y;

        float scaleDiff = calculateScaleDiff(stickerBean, calculateEdge(xEdge, yEdge));

        stickerBean.getMatrix().postScale(scaleDiff, scaleDiff, px, py);
        stickerBean.setScale(stickerBean.getScale() * scaleDiff);

        float degreesDiff = calculateDegreesDiff(calculateDegrees(xEdge, yEdge));

        stickerBean.getMatrix().postRotate(degreesDiff, px, py);
        stickerBean.setDegrees(stickerBean.getDegrees() + degreesDiff);
    }

    private float calculateScaleDiff(StickerBean stickerBean, float edge) {
        if (mLastEdge == 0) {
            mLastEdge = edge;
        }
        float scaleDiff = isBeyond(stickerBean, edge / mLastEdge);
        mLastEdge = edge;
        return scaleDiff;
    }

    private float isBeyond(StickerBean stickerBean, float scaleDiff) {
        float maxScale = mStickerMaxSize / (float) stickerBean.getWidth();
        float minScale = mStickerMinSize / (float) stickerBean.getWidth();
        float scale = stickerBean.getScale() * scaleDiff;
        if (scale > maxScale) {// 缩放大小限制
            return maxScale / stickerBean.getScale();
        } else if (scale < minScale) {
            return minScale / stickerBean.getScale();
        }
        return scaleDiff;
    }

    private float calculateDegreesDiff(float degrees) {
        if (mLastDegrees == 0) {
            mLastDegrees = degrees;
        }
        float degreesDiff = degrees - mLastDegrees;
        mLastDegrees = degrees;
        return degreesDiff;
    }

    private void translate(StickerBean stickerBean) {
        int dx = isBorderX(stickerBean, mMoveX - mLastMoveX);
        int dy = isBorderY(stickerBean, mMoveY - mLastMoveY);

        stickerBean.setDx(stickerBean.getDx() + dx);
        stickerBean.setDy(stickerBean.getDy() + dy);
        stickerBean.getMatrix().postTranslate(dx, dy);
    }

    private int isBorderX(StickerBean stickerBean, int dx) {
        int stickerDx = stickerBean.getDx();
        int leftBorder = mMinBorder - stickerBean.getWidth();
        int rightBorder = mView.getWidth() - mMinBorder;

        if (stickerDx + dx < leftBorder) {
            return leftBorder - stickerDx;
        } else if (stickerDx + dx > rightBorder) {
            return rightBorder - stickerDx;
        }
        return dx;
    }

    private int isBorderY(StickerBean stickerBean, int dy) {
        int stickerDy = stickerBean.getDy();
        int topBorder = mMinBorder - stickerBean.getHeight();
        int bottomBorder = mView.getHeight() - mMinBorder;

        if (stickerDy + dy < topBorder) {
            return topBorder - stickerDy;
        } else if (stickerDy + dy > bottomBorder) {
            return bottomBorder - stickerDy;
        }
        return dy;
    }

    private void delete() {
        if (mStickerBean == null) {
            return;
        }
        delete(mSelectedPosition);
    }

    //外部调用删除
    public void delete(int position) {
        StickerBean stickerBean = mStickers.remove(position);
        if (position == mSelectedPosition) {
            unselected();
        }

        if (mOnDeleteListener != null) {
            mOnDeleteListener.onDelete(stickerBean);
        }
    }

    private void copy() {
        if (mStickerBean == null) {
            return;
        }
        if (mOnCopyListener != null) {
            mOnCopyListener.onCopy(mStickerBean);
        }
    }

    private void flip() {
        if (mStickerBean == null) {
            return;
        }

        mStickerBean.setFlip(!mStickerBean.isFlip());

        flipMatrix(mStickerBean);
        flipMatrix(mFlipIconBean);

        mView.invalidate();
    }

    @Override
    protected void click() {
        if (mStickerBean == null) {
            return;
        }
        if (mOnClickStickerListener != null) {
            mOnClickStickerListener.onClick(mStickerBean);
        }
    }

    @Override
    protected void doubleClick() {
        if (mStickerBean != null && mOnDoubleClickStickerListener != null) {
            mOnDoubleClickStickerListener.onDoubleClick(mStickerBean);
        }
    }

    @Override
    protected void longClick() {
        mState = STATE_CANCEL;
        if (mStickerBean != null && mOnLongClickStickerListener != null) {
            mOnLongClickStickerListener.onLongClick(mStickerBean);
        }
    }

    //替换背景刷新
    public void updateSelected(int position) {
        if (mSelectedPosition != position) {
            return;
        }
        mStickerBean = mStickers.get(mSelectedPosition);
        invalidateSelected();
    }

    private void selected(int position) {
        unselected();

        mSelectedPosition = position;
        mStickerBean = mStickers.get(mSelectedPosition);

        invalidateSelected();
        mView.invalidate();

        if (mOnSelectedListener != null) {
            mOnSelectedListener.onSelected(mStickerBean);
        }
    }

    private void unselected() {
        if (mStickerBean == null) {
            return;
        }

        mSelectedPosition = STATE_NORMAL;

        if (mOnUnselectedListener != null) {
            mOnUnselectedListener.onUnselected(mStickerBean);
        }
        mStickerBean = null;

        mView.invalidate();
    }

    private void invalidateSelected() {
        if (mStickerBean == null) {
            return;
        }

        calculateSelected(mStickerBean,
                mDelIconBean, mCopyIconBean, mDragIconBean, mFlipIconBean,
                mSidePoint, mSidePadding);
    }

    private boolean containPoint(MatrixBean iconBean) {
        return containPoint(iconBean.getMatrix(), iconBean.getWidth(), iconBean.getHeight(), mDownX, mDownY);
    }

    private boolean containPoint(MatrixBean stickerBean, float x, float y) {
        return containPoint(stickerBean.getMatrix(), stickerBean.getWidth(), stickerBean.getHeight(), x, y);
    }

    //判断坐标是否在变换过后的矩阵内
    private boolean containPoint(Matrix matrix, int width, int height, float x, float y) {
        if (matrix.invert(mInvertMatrix)) {
            mPoint[0] = x;
            mPoint[1] = y;

            mInvertMatrix.mapPoints(mPoint, mPoint);

            x = mPoint[0];
            y = mPoint[1];
        }

        mRect.set(0, 0, width, height);
        return mRect.contains((int) x, (int) y);
    }

    public void setOnDeleteListener(OnDeleteListener l) {
        this.mOnDeleteListener = l;
    }

    public void setOnCopyListener(OnCopyListener l) {
        this.mOnCopyListener = l;
    }

    public void setOnClickStickerListener(OnClickStickerListener l) {
        this.mOnClickStickerListener = l;
    }

    public void setOnDoubleClickStickerListener(OnDoubleClickStickerListener l) {
        this.mOnDoubleClickStickerListener = l;
    }

    public void setOnLongClickStickerListener(OnLongClickStickerListener l) {
        this.mOnLongClickStickerListener = l;
    }

    public void setOnSelectedListener(OnSelectedListener l) {
        this.mOnSelectedListener = l;
    }

    public void setOnUnselectedListener(OnUnselectedListener l) {
        this.mOnUnselectedListener = l;
    }
}
