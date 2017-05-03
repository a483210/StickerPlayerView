package com.xiuyukeji.stickerplayerview.cache.impl;

import android.graphics.Bitmap;

import com.xiuyukeji.stickerplayerview.cache.LimitedMemoryHelper;
import com.xiuyukeji.stickerplayerview.cache.MemoryCache;
import com.xiuyukeji.stickerplayerview.cache.MemoryReusable;
import com.xiuyukeji.stickerplayerview.cache.ReusableMemoryHelper;
import com.xiuyukeji.stickerplayerview.cache.impl.map.UsingFreqLinkedHashMap;
import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

import static com.xiuyukeji.stickerplayerview.utils.ImageUtil.recycleBitmap;

/**
 * 根据使用频率进行内存管理
 *
 * @author Created by jz on 2017/4/18 16:19
 */
public final class UsingFreqMemoryCache implements MemoryCache, MemoryReusable {

    private final UsingFreqLinkedHashMap<String, Bitmap> mBitmapCaches = new UsingFreqLinkedHashMap<>();
    private final LimitedMemoryHelper mLimitedHelper;
    private final ReusableMemoryHelper mReusableHelper;

    public UsingFreqMemoryCache() {
        this.mLimitedHelper = new LimitedMemoryHelper();
        this.mReusableHelper = new ReusableMemoryHelper();
    }

    public UsingFreqMemoryCache(int maxSize, int maxReusableSize) {
        this.mLimitedHelper = new LimitedMemoryHelper(maxSize);
        this.mReusableHelper = new ReusableMemoryHelper(maxReusableSize);
    }

    @Override
    public void put(String index, Bitmap bitmap, int weight) {
        int size = bitmap.getByteCount();
        while (mBitmapCaches.size() > 0
                && mLimitedHelper.getByteCount() + size > mLimitedHelper.getMaxSize()) {
            remove(removeNext());
        }
        mBitmapCaches.put(index, bitmap, weight);
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
        mReusableHelper.add(bitmap);
    }

    @Override
    public boolean containsIndex(String index) {
        return mBitmapCaches.containsKey(index);
    }

    @Override
    public void clear() {
        Bitmap[] bitmaps = new Bitmap[mBitmapCaches.size()];
        for (Bitmap bitmap : mBitmapCaches.values(bitmaps)) {
            recycleBitmap(bitmap);
        }
        mBitmapCaches.clear();
        mLimitedHelper.reset();
        mReusableHelper.clear();
    }

    @Override
    public int size() {
        return mBitmapCaches.size();
    }

    @Override
    public Bitmap getReusableBitmap(BitmapReusable cacheReusable) {
        return mReusableHelper.getReusableBitmap(cacheReusable);
    }

    private String removeNext() {
        return mBitmapCaches.getLastKey();
    }
}
