package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Bitmap;

/**
 * 图片类
 *
 * @author Created by jz on 2017/4/14 16:54
 */
public class BitmapBean {
    private final Bitmap bitmap;
    private int useCount;

    public BitmapBean(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.useCount = 1;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    @Override
    public String toString() {
        return "BitmapBean{" +
                "bitmap=" + bitmap +
                ", useCount=" + useCount +
                '}';
    }
}
