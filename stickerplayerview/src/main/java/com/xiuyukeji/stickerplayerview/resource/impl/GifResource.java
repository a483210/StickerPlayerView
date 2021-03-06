package com.xiuyukeji.stickerplayerview.resource.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.xiuyukeji.stickerplayerview.annotations.ResourceSource;
import com.xiuyukeji.stickerplayerview.resource.BaseResource;
import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;
import com.xiuyukeji.stickerplayerview.resource.DynamicResource;

import java.io.IOException;

import pl.droidsonroids.gif.GifDecoder;
import pl.droidsonroids.gif.InputSource;

/**
 * gif资源
 *
 * @author Created by jz on 2017/4/17 14:38
 */
public class GifResource extends BaseResource implements DynamicResource, BitmapReusable {
    private GifDecoder mGifDecoder;
    private int mWidth;
    private int mHeight;
    private int mFrameCount;
    private int mDuration;
    private float mDelayTime;

    public GifResource(@NonNull Context context, @NonNull String path, @ResourceSource int source) {
        super(context, path, source);
    }

    @Override
    public Bitmap init() {
        mGifDecoder = readGifDecoder(mPath);

        if (mGifDecoder == null) {
            return null;
        }

        mWidth = mGifDecoder.getWidth();
        mHeight = mGifDecoder.getHeight();
        mFrameCount = mGifDecoder.getNumberOfFrames();
        mDuration = mGifDecoder.getDuration();
        mDelayTime = mDuration / (float) mFrameCount;

        return getBitmap(0);
    }

    @Override
    public Bitmap getBitmap(int frameIndex) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mGifDecoder.seekToFrame(frameIndex, bitmap);
        return bitmap;
    }

    @Override
    public boolean isReusable(Bitmap bitmap) {
        return bitmap != null
                && bitmap.getWidth() == mWidth
                && bitmap.getHeight() == mHeight
                && bitmap.getConfig() == Bitmap.Config.ARGB_8888;
    }

    @Override
    public Bitmap getBitmap(Bitmap bitmap, int frameIndex) {
        if (bitmap == null) {
            bitmap = getBitmap(frameIndex);
        } else {
            mGifDecoder.seekToFrame(frameIndex, bitmap);
        }
        return bitmap;
    }

    @Override
    public int getFrameCount() {
        return mFrameCount;
    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    @Override
    public float getDelayTime() {
        return mDelayTime;
    }

    @Override
    public int getWeight() {
        return 20;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public void recycle() {
        mGifDecoder.recycle();
    }

    //从本地目录读取gif
    private GifDecoder readGifDecoder(String path) {
        try {
            InputSource inputSource;
            if (mSource == FILE) {
                inputSource = new InputSource.FileSource(path);
            } else {
                inputSource = new InputSource.AssetSource(mContext.getAssets(), path);
            }
            return new GifDecoder(inputSource);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
