package com.xiuyukeji.stickerplayerview.resource.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.xiuyukeji.stickerplayerview.annotations.ResourceSource;
import com.xiuyukeji.stickerplayerview.resource.BaseResource;
import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.xiuyukeji.stickerplayerview.utils.ImageUtil.canUseForInBitmap;

/**
 * 静态图像资源
 *
 * @author Created by jz on 2017/4/14 16:54
 */
public class BitmapResource extends BaseResource implements BitmapReusable {

    private BitmapFactory.Options mOptions;

    public BitmapResource(@NonNull Context context, @NonNull String path, @ResourceSource int source) {
        super(context, path, source);

        mOptions = new BitmapFactory.Options();
    }

    @Override
    public Bitmap init() {
        Bitmap bitmap = getBitmap(0);

        if (bitmap == null) {
            return null;
        }

        mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mOptions.inSampleSize = 1;
        mOptions.outWidth = bitmap.getWidth();
        mOptions.outHeight = bitmap.getHeight();
        mOptions.inMutable = true;

        return bitmap;
    }

    @Override
    public Bitmap getBitmap(int frameIndex) {
        return readBitmap(mPath);
    }

    @Override
    public boolean isReusable(Bitmap bitmap) {
        return canUseForInBitmap(bitmap, mOptions);
    }

    @Override
    public Bitmap getBitmap(Bitmap bitmap, int frameIndex) {
        mOptions.inBitmap = bitmap;
        bitmap = readBitmap(mPath);
        mOptions.inBitmap = null;
        return bitmap;
    }

    @Override
    public int getWeight() {
        return 100;
    }

    @Override
    public int getWidth() {
        return mOptions.outWidth;
    }

    @Override
    public int getHeight() {
        return mOptions.outHeight;
    }

    @Override
    public void recycle() {
    }

    private Bitmap readBitmap(String path) {
        try {
            InputStream is;
            if (mSource == FILE) {
                is = new BufferedInputStream(new FileInputStream(path));
            } else {
                is = mContext.getResources().getAssets().open(path);
            }
            Bitmap bmp = BitmapFactory.decodeStream(is, null, mOptions);
            is.close();
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
