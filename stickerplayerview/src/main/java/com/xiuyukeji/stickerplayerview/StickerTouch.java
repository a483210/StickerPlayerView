package com.xiuyukeji.stickerplayerview;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateRotateX;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateRotateY;

/**
 * 贴纸帮助类
 *
 * @author Created by jz on 2017/4/11 17:18
 */
class StickerTouch {

    private final static int SCROLL_START = 0, SCROLL_STOP = 1;
    final static int STATE_NORMAL = -1, STATE_DELETE = 0, STATE_COPY = 1, STATE_DRAG = 2, STATE_FLIP = 3;

    private final View mView;

    private final Matrix mInvertMatrix;
    private final Rect mRect;

    private final Path mFramePath;
    private int mFramePadding;

    private IconBean mDelIconBean;
    private IconBean mCopyIconBean;
    private IconBean mDragIconBean;
    private IconBean mFlipIconBean;

    private final int mTouchSlop;

    private int mDownX;
    private int mMoveX;
    private int mLastMoveX;
    private int mMoveSecondX;

    private int mDownY;
    private int mMoveY;
    private int mLastMoveY;
    private int mMoveSecondY;

    private int mScrollState = SCROLL_STOP;
    private int mState = STATE_NORMAL;

    private int mSelectedPosition = STATE_NORMAL;

    private ArrayList<StickerBean> mStickers;

    StickerTouch(View view) {
        this.mView = view;

        mInvertMatrix = new Matrix();
        mRect = new Rect();

        mFramePath = new Path();

        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mTouchSlop = vc.getScaledTouchSlop();
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
        this.mStickers = currentStickers;

        mSelectedPosition = STATE_NORMAL;
    }

    void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    int getSelectedPosition() {
        return mSelectedPosition;
    }

    Path getFramePath() {
        return mFramePath;
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
                if (mSelectedPosition != STATE_NORMAL) {
                    if (containPoint(mDelIconBean, mMoveX, mMoveY)) {
                        mState = STATE_DELETE;
                        break;
                    } else if (containPoint(mCopyIconBean, mMoveX, mMoveY)) {
                        mState = STATE_COPY;
                        break;
                    } else if (containPoint(mDragIconBean, mMoveX, mMoveY)) {
                        mState = STATE_DRAG;
                        break;
                    } else if (containPoint(mFlipIconBean, mMoveX, mMoveY)) {
                        mState = STATE_FLIP;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) event.getX();
                mMoveY = (int) event.getY();

                int dx = mDownX - mMoveX;
                int dy = mDownY - mMoveY;

                if (Math.abs(dx) > mTouchSlop ||
                        Math.abs(dy) > mTouchSlop) {
                    mScrollState = SCROLL_START;
                    return onTouchEvent(event);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                Log.i("Tool", "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Tool", "ACTION_UP");
                mScrollState = SCROLL_STOP;
                break;
            default:
                break;
        }
        calculateSelected();
        mView.invalidate();
        return false;
    }

    private void calculateSelected() {
        if (mSelectedPosition == STATE_NORMAL) {
            return;
        }
        StickerBean stickerBean = mStickers.get(mSelectedPosition);
        if (stickerBean == null) {
            return;
        }

        final float scale = (stickerBean.getScale() - 1) * stickerBean.getWidth();
        final float width = stickerBean.getWidth() + scale + mFramePadding * 2;
        final float height = stickerBean.getHeight() + scale + mFramePadding * 2;
        final float dx = stickerBean.getDx() - mFramePadding - scale / 2;
        final float dy = stickerBean.getDy() - mFramePadding - scale / 2;

        final float pointX = dx + width / 2;
        final float pointY = dy + height / 2;

        calculateFrame(stickerBean.getDegrees(),
                dx, dy, dx + width, dy + height,
                pointX, pointY);

        calculateIcon(mDelIconBean, stickerBean.getDegrees(),
                dx, dy,
                pointX, pointY);

        calculateIcon(mCopyIconBean, stickerBean.getDegrees(),
                dx + width, dy,
                pointX, pointY);

        calculateIcon(mDragIconBean, stickerBean.getDegrees(),
                dx + width, dy + height,
                pointX, pointY);

        calculateIcon(mFlipIconBean, stickerBean.getDegrees(),
                dx, dy + height,
                pointX, pointY);
    }

    private void calculateIcon(IconBean iconBean, float degrees, float dx, float dy,
                               float pointX, float pointY) {
        iconBean.getMatrix().setTranslate(dx - iconBean.getWidth() / 2, dy - iconBean.getHeight() / 2);
        iconBean.getMatrix().postRotate(degrees, pointX, pointY);
    }

    private void calculateFrame(float degrees,
                                float left, float top, float right, float bottom,
                                float pointX, float pointY) {

        degrees = (float) (degrees * Math.PI / 180);

        mFramePath.reset();
        mFramePath.moveTo(calculateRotateX(degrees, left, top, pointX, pointY),
                calculateRotateY(degrees, left, top, pointX, pointY));
        mFramePath.lineTo(calculateRotateX(degrees, right, top, pointX, pointY),
                calculateRotateY(degrees, right, top, pointX, pointY));
        mFramePath.lineTo(calculateRotateX(degrees, right, bottom, pointX, pointY),
                calculateRotateY(degrees, right, bottom, pointX, pointY));
        mFramePath.lineTo(calculateRotateX(degrees, left, bottom, pointX, pointY),
                calculateRotateY(degrees, left, bottom, pointX, pointY));
        mFramePath.close();
    }

    private boolean containPoint(IconBean iconBean, float x, float y) {
        return containPoint(iconBean.getMatrix(), iconBean.getWidth(), iconBean.getHeight(), x, y);
    }

    //判断坐标是否在变换过后的矩阵内
    private boolean containPoint(Matrix matrix, int width, int height, float x, float y) {
        if (matrix.invert(mInvertMatrix)) {
            float[] pointsSrc = new float[]{x, y};
            float[] pointsEnd = new float[]{0, 0};

            mInvertMatrix.mapPoints(pointsEnd, pointsSrc);

            x = pointsEnd[0] + x;
            y = pointsEnd[1] + y;
        }

        mRect.set(0, 0, width, height);
        return mRect.contains((int) x, (int) y);
    }
}
