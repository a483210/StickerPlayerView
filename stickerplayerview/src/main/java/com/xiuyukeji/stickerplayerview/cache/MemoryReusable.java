package com.xiuyukeji.stickerplayerview.cache;

import android.graphics.Bitmap;

import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

/**
 * 内存复用
 *
 * @author Created by jz on 2017/4/19 15:41
 */
public interface MemoryReusable {
    /**
     * 获得复用图像
     *
     * @param cacheReusable 比较条件
     */
    Bitmap getReusableBitmap(BitmapReusable cacheReusable);
}
