package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Bitmap;

/**
 * 存储图标数据
 *
 * @author Created by jz on 2017/4/12 17:37
 */
public class IconBean extends MatrixBean {

    private final Bitmap bitmap;

    public IconBean(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "IconBean{" +
                "bitmap=" + bitmap +
                "} " + super.toString();
    }
}
