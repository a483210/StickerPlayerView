package com.xiuyukeji.stickerplayerview.cache;

import android.graphics.Bitmap;

import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.utils.ImageUtil.recycleBitmap;

/**
 * 内存复用帮助
 *
 * @author Created by jz on 2017/4/20 10:06
 */
public class ReusableMemoryHelper implements MemoryReusable {
    public static final int DEFAULT_MAX_REUSABLE_CACHE_SIZE = 8 * 1024 * 1024;//复用最大内存

    private final int mMaxReusableSize;
    private int mReusableSize;

    private final ArrayList<Bitmap> mReusableCaches;

    public ReusableMemoryHelper() {
        this(DEFAULT_MAX_REUSABLE_CACHE_SIZE);
    }

    public ReusableMemoryHelper(int maxReusableSize) {
        this.mMaxReusableSize = maxReusableSize;

        mReusableCaches = new ArrayList<>();
    }

    /**
     * 添加图像到复用池
     *
     * @param bitmap 图像
     */
    public void add(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        int size = bitmap.getByteCount();

        if (mReusableSize + size < mMaxReusableSize) {
            mReusableCaches.add(bitmap);
            mReusableSize += size;
        } else {
            bitmap.recycle();
        }
    }

    /**
     * 清空复用池
     */
    public void clear() {
        for (Bitmap bitmap : mReusableCaches) {
            recycleBitmap(bitmap);
        }
        mReusableCaches.clear();
        mReusableSize = 0;
    }

    @Override
    public final Bitmap getReusableBitmap(BitmapReusable cacheReusable) {
        int count = mReusableCaches.size();
        for (int i = 0; i < count; i++) {
            Bitmap bitmap = mReusableCaches.get(i);
            if (cacheReusable.isReusable(bitmap)) {
                return mReusableCaches.remove(i);
            }
        }
        return null;
    }
}
