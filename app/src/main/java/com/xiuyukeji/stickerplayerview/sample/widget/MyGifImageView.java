package com.xiuyukeji.stickerplayerview.sample.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ScreenUtils;
import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.impl.GifResource;
import com.xiuyukeji.stickerplayerview.sample.utils.scheduler.OnFrameListener;
import com.xiuyukeji.stickerplayerview.sample.utils.scheduler.OnFrameUpdateListener;
import com.xiuyukeji.stickerplayerview.sample.utils.scheduler.Scheduler;
import com.xiuyukeji.stickerplayerview.utils.ImageUtil;
import com.xiuyukeji.stickerplayerview.utils.StickerUtil;

/**
 * Gif进度控制
 *
 * @author Created by jz on 2017/5/8 15:53
 */
public class MyGifImageView extends View {

    private final Paint mPaint;
    private final Rect mSrcRect;
    private final Rect mDstRect;

    private GifResource mGifResource;
    private Bitmap mCacheBitmap;

    private Scheduler mScheduler;
    private final Handler mHandler;

    private OnProgressListener mOnProgressListener;
    private OnStopListener mOnStopListener;

    public MyGifImageView(Context context) {
        this(context, null);
    }

    public MyGifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint(StickerUtil.PAINT_FLAG);
        mSrcRect = new Rect();
        mDstRect = new Rect();

        mHandler = new Handler();
    }

    public void loadAssetsGif(String path) {
        if (mGifResource != null) {
            mGifResource.recycle();
        }
        mGifResource = new GifResource(getContext(), path, Resource.ASSETS);
        Bitmap bitmap = mGifResource.init();
        if (bitmap == null || bitmap.isRecycled()) {
            throw new NullPointerException("初始化GIF失败！");
        }

        ImageUtil.recycleBitmap(mCacheBitmap);

        int width = getGifWidth();
        int height = getGifHeight();

        mCacheBitmap = bitmap;

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = ScreenUtils.getScreenWidth();
        lp.height = lp.width * height / width;

        mSrcRect.set(0, 0, width, height);
        mDstRect.set(0, 0, lp.width, lp.height);

        invalidate();
    }

    public void start() {
        if (mScheduler != null) {
            return;
        }
        mScheduler = new Scheduler(mGifResource.getDuration(), getGifCount(),
                new MyFrameUpdate(), new MyFrame());
        mScheduler.setSkipFrame(true);
        mScheduler.start();
    }

    public void stop() {
        if (mScheduler == null) {
            return;
        }
        mScheduler.stop();
        play(0);
    }

    public void seekTo(int frameIndex) {
        if (mScheduler != null) {
            return;
        }
        play(frameIndex);
    }

    private void play(int frameIndex) {
        mCacheBitmap = mGifResource.getBitmap(mCacheBitmap, frameIndex);
        postInvalidate();
    }

    public int getGifCount() {
        return mGifResource.getFrameCount();
    }

    public float getDelayTime() {
        return mGifResource.getDelayTime();
    }

    public int getGifWidth() {
        return mGifResource.getWidth();
    }

    public int getGifHeight() {
        return mGifResource.getHeight();
    }

    /**
     * 该方法不可与{@link #seekTo(int)}或者{@link #start()}同时进行
     *
     * @param bitmap     复用图像
     * @param frameIndex 帧索引
     * @return 错误码 0代表成功
     */
    public int getThumb(Bitmap bitmap, int frameIndex) {
        if (frameIndex < 0 || frameIndex >= getGifCount()) {
            return -2;
        }

        bitmap = mGifResource.getBitmap(bitmap, frameIndex);
        if (bitmap == null || bitmap.isRecycled()) {
            return -1;
        }

        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCacheBitmap == null) {
            return;
        }
        canvas.drawBitmap(mCacheBitmap, mSrcRect, mDstRect, mPaint);
    }

    private class MyFrameUpdate implements OnFrameUpdateListener {
        @Override
        public void onFrameUpdate(long frameIndex) {
            int index = (int) frameIndex;

            play(index);

            if (mOnProgressListener != null) {
                mHandler.post(() -> mOnProgressListener.onProgress(index));
            }
        }
    }

    private class MyFrame implements OnFrameListener {
        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {
            play(0);
            mScheduler = null;
            if (mOnStopListener != null) {
                mHandler.post(() -> mOnStopListener.onStop());
            }
        }

        @Override
        public void onCancel() {

        }
    }

    public void setOnProgressListener(OnProgressListener l) {
        this.mOnProgressListener = l;
    }

    public interface OnProgressListener {
        void onProgress(int position);
    }

    public void setOnStopListener(OnStopListener l) {
        this.mOnStopListener = l;
    }

    public interface OnStopListener {
        void onStop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mGifResource.recycle();
    }
}
