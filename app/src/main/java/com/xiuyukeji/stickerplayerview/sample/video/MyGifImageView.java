package com.xiuyukeji.stickerplayerview.sample.video;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Gif进度控制
 *
 * @author Created by jz on 2017/5/8 15:53
 */
public class MyGifImageView extends GifImageView {

    private GifDrawable mGifDrawable;

    private int mLastPosition = -1;

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
    }

    public void loadAssetsGif(String path) {
        try {
            mGifDrawable = new GifDrawable(getContext().getAssets(), path);
            mGifDrawable.setLoopCount(1);
            mGifDrawable.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setImageDrawable(mGifDrawable);

        mGifDrawable.addAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                if (mOnStopListener != null) {
                    mOnStopListener.onStop();
                }
                mLastPosition = -1;
                seekTo(0);
            }
        });
    }

    public void start() {
        mGifDrawable.start();
        mLastPosition = 0;
        seekTo(0);
    }

    public void stop() {
        mGifDrawable.stop();
        if (mOnStopListener != null) {
            mOnStopListener.onStop();
        }
        mLastPosition = -1;
        seekTo(0);
    }

    public void seekTo(int position) {
        mGifDrawable.seekToFrame(position);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLastPosition == -1) {
            updateProgress(0);
        } else {
            updateProgress(mGifDrawable.getCurrentFrameIndex());
        }
    }

    private void updateProgress(int position) {
        if (position != mLastPosition) {
            if (mOnProgressListener != null) {
                mOnProgressListener.onProgress(position);
            }
        }
        mLastPosition = position;
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
}
