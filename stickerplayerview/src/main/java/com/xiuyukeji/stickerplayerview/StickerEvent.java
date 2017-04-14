package com.xiuyukeji.stickerplayerview;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnCopyListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickListener;
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
class StickerEvent extends StickerClickEvent {

    private final static int SCROLL_START = 0, SCROLL_STOP = 1;
    private final static int STATE_NORMAL = -1, STATE_DELETE = 0, STATE_COPY = 1, STATE_DRAG = 2, STATE_FLIP = 3, STATE_TRANSLATE = 4;
    private final static int ACTION_TRANSLATE = 0, ACTION_DRAG = 1, ACTION_DRAG_SECOND = 2;

    private final View mView;

    private final Matrix mInvertMatrix;
    private final Rect mRect;

    private final float[] mFramePoint;
    private int mFramePadding;

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

    private ArrayList<StickerBean> mStickers;
    private StickerBean mStickerBean;
    private int mSelectedPosition = STATE_NORMAL;

    private OnDeleteListener mOnDeleteListener;
    private OnCopyListener mOnCopyListener;

    private OnClickListener mOnClickListener;
    private OnDoubleClickListener mOnDoubleClickListener;
    private OnLongClickListener mOnLongClickListener;

    private OnSelectedListener mOnSelectedListener;
    private OnUnselectedListener mOnUnselectedListener;

    StickerEvent(View view) {
        this.mView = view;

        mInvertMatrix = new Matrix();
        mRect = new Rect();

        mFramePoint = new float[8];

        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mTouchSlop = vc.getScaledTouchSlop();

        mStickerMaxSize = 1080;
        mStickerMinSize = 100;
        mMinBorder = 100;
    }

    void setIcon(IconBean delIconBean, IconBean copyIconBean, IconBean dragIconBean, IconBean flipIconBean,
                 int framePadding) {
        this.mDelIconBean = delIconBean;
        this.mCopyIconBean = copyIconBean;
        this.mDragIconBean = dragIconBean;
        this.mFlipIconBean = flipIconBean;
        this.mFramePadding = framePadding;
    }

    void setStickers(ArrayList<StickerBean> currentStickers) {
        unselected();
        this.mStickers = currentStickers;
    }

    void setSelectedPosition(int selectedPosition) {
        selected(selectedPosition);
    }

    int getSelectedPosition() {
        return mSelectedPosition;
    }

    float[] getFramePoint() {
        return mFramePoint;
    }

    Matrix getDelMatrix() {
        return mDelIconBean.getMatrix();
    }

    Matrix getCopyMatrix() {
        return mCopyIconBean.getMatrix();
    }

    Matrix getDragMatrix() {
        return mDragIconBean.getMatrix();
    }

    Matrix getFlipMatrix() {
        return mFlipIconBean.getMatrix();
    }

    boolean dispatchTouchEvent(MotionEvent event) {
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
                    mScrollState = SCROLL_START;
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
                    mScrollState = SCROLL_START;
                    mAction = ACTION_DRAG;
                    return onTouchEvent(event);
                } else if (mState == STATE_TRANSLATE) {
                    int dx = mDownX - mMoveX;
                    int dy = mDownY - mMoveY;

                    if (Math.abs(dx) > mTouchSlop ||
                            Math.abs(dy) > mTouchSlop) {
                        mScrollState = SCROLL_START;
                        mAction = ACTION_TRANSLATE;
                        return onTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mState == STATE_DELETE) {
                    delete();
                } else if (mState == STATE_COPY) {
                    copy();
                } else if (mState == STATE_FLIP) {
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

    private boolean isDragSecond(MotionEvent event) {
        if (mStickerBean == null) {
            return false;
        }
        int downX = (int) event.getX(1);
        int downY = (int) event.getY(1);
        return containPoint(mStickerBean, downX, downY);
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

        StickerBean stickerBean = mStickers.remove(mSelectedPosition);
        unselected();

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
        if (mOnClickListener != null) {
            mOnClickListener.onClick(mStickerBean);
        }
    }

    @Override
    protected void doubleClick() {
        if (mStickerBean != null && mOnDoubleClickListener != null) {
            mOnDoubleClickListener.onDoubleClick(mStickerBean);
        }
    }

    @Override
    protected void longClick() {
        if (mStickerBean != null && mOnLongClickListener != null) {
            mOnLongClickListener.onLongClick(mStickerBean);
        }
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

        mView.invalidate();

        if (mOnUnselectedListener != null) {
            mOnUnselectedListener.onUnselected(mStickerBean);
        }
        mStickerBean = null;
    }

    private void invalidateSelected() {
        if (mStickerBean == null) {
            return;
        }

        calculateSelected(mStickerBean,
                mDelIconBean, mCopyIconBean, mDragIconBean, mFlipIconBean,
                mFramePoint, mFramePadding);
    }

    private boolean containPoint(IconBean iconBean) {
        return containPoint(iconBean.getMatrix(), iconBean.getWidth(), iconBean.getHeight(), mDownX, mDownY);
    }

    private boolean containPoint(StickerBean stickerBean) {
        return containPoint(stickerBean.getMatrix(), stickerBean.getWidth(), stickerBean.getHeight(), mDownX, mDownY);
    }

    private boolean containPoint(StickerBean stickerBean, float x, float y) {
        return containPoint(stickerBean.getMatrix(), stickerBean.getWidth(), stickerBean.getHeight(), x, y);
    }

    //判断坐标是否在变换过后的矩阵内
    private boolean containPoint(Matrix matrix, int width, int height, float x, float y) {
        if (matrix.invert(mInvertMatrix)) {
            float[] pointsSrc = new float[]{x, y};
            float[] pointsEnd = new float[]{0, 0};

            mInvertMatrix.mapPoints(pointsEnd, pointsSrc);

            x = pointsEnd[0];
            y = pointsEnd[1];
        }

        mRect.set(0, 0, width, height);
        return mRect.contains((int) x, (int) y);
    }

    void setOnDeleteListener(OnDeleteListener l) {
        this.mOnDeleteListener = l;
    }

    void setOnCopyListener(OnCopyListener l) {
        this.mOnCopyListener = l;
    }

    void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    void setOnDoubleClickListener(OnDoubleClickListener l) {
        this.mOnDoubleClickListener = l;
    }

    void setOnLongClickListener(OnLongClickListener l) {
        this.mOnLongClickListener = l;
    }

    void setOnSelectedListener(OnSelectedListener l) {
        this.mOnSelectedListener = l;
    }

    void setOnUnselectedListener(OnUnselectedListener l) {
        this.mOnUnselectedListener = l;
    }
}
