package com.xiuyukeji.stickerplayerview.resource;

import android.graphics.Bitmap;

/**
 * 图像复用
 *
 * @author Created by jz on 2017/4/19 11:02
 */
public interface BitmapReusable {

    /**
     * 比较是否符合复用条件
     *
     * @param bitmap 图像
     */
    boolean isReusable(Bitmap bitmap);

    /**
     * 通过复用获得图像
     *
     * @param bitmap 图像
     */
    Bitmap getBitmap(Bitmap bitmap, int frameIndex);
}
