package com.xiuyukeji.stickerplayerview.cache;

import android.graphics.Bitmap;

/**
 * 内存管理
 *
 * @author Created by jz on 2017/4/19 9:51
 */
public interface MemoryCache {
    /**
     * 添加缓存
     *
     * @param index  索引
     * @param bitmap 图像
     * @param weight 权重
     */
    void put(String index, Bitmap bitmap, int weight);

    /**
     * 获取缓存
     *
     * @param index 索引
     */
    Bitmap get(String index);

    /**
     * 删除缓存
     *
     * @param index 索引
     */
    Bitmap remove(String index);

    /**
     * 是否包含索引
     *
     * @param index 索引
     */
    boolean containsIndex(String index);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 缓存大小
     */
    int size();
}
