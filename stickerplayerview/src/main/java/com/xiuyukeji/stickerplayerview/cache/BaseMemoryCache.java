package com.xiuyukeji.stickerplayerview.cache;

import android.graphics.Bitmap;

import java.util.HashMap;

import static com.xiuyukeji.stickerplayerview.utils.ImageUtil.recycleBitmap;

/**
 * 内存管理
 *
 * @author Created by jz on 2017/4/19 15:23
 */
public abstract class BaseMemoryCache implements MemoryCache {

    private final HashMap<String, Bitmap> mBitmapCaches = new HashMap<>();
    private final LimitedMemoryHelper mLimitedHelper;

    public BaseMemoryCache() {
        this.mLimitedHelper = new LimitedMemoryHelper();
    }

    public BaseMemoryCache(int maxSize) {
        this.mLimitedHelper = new LimitedMemoryHelper(maxSize);
    }

    @Override
    public void put(String index, Bitmap bitmap, int weight) {
        int size = bitmap.getByteCount();
        while (mBitmapCaches.size() > 0
                && mLimitedHelper.getByteCount() + size > mLimitedHelper.getMaxSize()) {
            remove(removeNext());
        }
        mBitmapCaches.put(index, bitmap);
        mLimitedHelper.amendSize(size);
    }

    @Override
    public Bitmap get(String index) {
        return mBitmapCaches.get(index);
    }

    @Override
    public void remove(String index) {
        Bitmap bitmap = mBitmapCaches.remove(index);
        if (bitmap == null) {
            return;
        }
        mLimitedHelper.amendSize(-bitmap.getByteCount());
    }

    @Override
    public boolean containsIndex(String index) {
        return mBitmapCaches.containsKey(index);
    }

    @Override
    public void clear() {
        for (Bitmap bitmap : mBitmapCaches.values()) {
            recycleBitmap(bitmap);
        }
        mBitmapCaches.clear();
        mLimitedHelper.reset();
    }

    @Override
    public int size() {
        return mBitmapCaches.size();
    }

    /**
     * 删除bitmap
     */
    protected abstract String removeNext();
}