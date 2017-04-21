package com.xiuyukeji.stickerplayerview.resource;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;

/**
 * 资源
 *
 * @author Created by jz on 2017/4/19 11:38
 */
public interface Resource {
    int FILE = 0, ASSETS = 1;

    /**
     * 初始化
     */
    Bitmap init();

    /**
     * 返回索引
     */
    String getIndex();

    /**
     * 返回权重
     */
    @IntRange(from = 1, to = 100)
    int getWeight();

    /**
     * 返回宽度
     */
    int getWidth();

    /**
     * 返回高度
     */
    int getHeight();

    /**
     * 获取图片
     */
    Bitmap getBitmap(int frameIndex);

    /**
     * 使用次数加一
     */
    int addedUseCount();

    /**
     * 使用次数减一
     */
    int decrementUseCount();

    /**
     * 释放
     */
    void recycle();
}
