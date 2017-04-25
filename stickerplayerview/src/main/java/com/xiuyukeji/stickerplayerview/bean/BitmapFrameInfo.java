package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Bitmap;

/**
 * 图片帧信息
 *
 * @author Created by jz on 2017/4/25 15:54
 */
public class BitmapFrameInfo {
    private Bitmap bitmap;
    private int frame;

    public void reset(Bitmap bitmap, int frame) {
        this.bitmap = bitmap;
        this.frame = frame;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        return "BitmapFrameInfo{" +
                "bitmap=" + bitmap +
                ", frame=" + frame +
                '}';
    }
}
