package com.xiuyukeji.stickerplayerview.cache;

import android.graphics.Bitmap;

import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

/**
 * 可重用的内存管理
 *
 * @author Created by jz on 2017/4/19 15:48
 */
public abstract class BaseReusableMemoryCache extends BaseMemoryCache implements MemoryReusable {

    private final ReusableMemoryHelper mReusableHelper;

    public BaseReusableMemoryCache() {
        super();

        this.mReusableHelper = new ReusableMemoryHelper();
    }

    public BaseReusableMemoryCache(int maxSize, int maxReusableSize) {
        super(maxSize);

        this.mReusableHelper = new ReusableMemoryHelper(maxReusableSize);
    }

    @Override
    public Bitmap remove(String index) {
        Bitmap bitmap = super.remove(index);
        mReusableHelper.add(bitmap);
        return bitmap;
    }

    @Override
    public void clear() {
        super.clear();
        mReusableHelper.clear();
    }

    @Override
    public final Bitmap getReusableBitmap(BitmapReusable cacheReusable) {
        return mReusableHelper.getReusableBitmap(cacheReusable);
    }
}
