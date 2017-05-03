package com.xiuyukeji.stickerplayerview.cache;

import com.xiuyukeji.stickerplayerview.StickerException;

/**
 * 内存管理帮助
 *
 * @author Created by jz on 2017/4/20 10:11
 */
public class LimitedMemoryHelper {
    public static final int DEFAULT_MAX_CACHE_SIZE = 24 * 1024 * 1024;//最大内存

    private final int mMaxSize;
    private int mSize;

    public LimitedMemoryHelper() {
        this(DEFAULT_MAX_CACHE_SIZE);
    }

    public LimitedMemoryHelper(int maxSize) {
        if (maxSize <= 0) {
            throw new StickerException("最大缓存内存不能小于等于0！");
        }
        this.mMaxSize = maxSize;
    }

    /**
     * 获得最大大小
     */
    public int getMaxSize() {
        return mMaxSize;
    }

    /**
     * 获得当前总大小
     */
    public int getByteCount() {
        return mSize;
    }

    /**
     * 修改当前总大小
     *
     * @param size bitmap大小
     */
    public void amendSize(int size) {
        mSize += size;
    }

    /**
     * 重置size
     */
    public void reset() {
        mSize = 0;
    }
}
