package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Matrix;

/**
 * 存储图标数据
 *
 * @author Created by jz on 2017/4/12 17:37
 */
public class IconBean {
    private final Matrix matrix;
    private final int width;
    private final int height;

    public IconBean(int width, int height) {
        this.matrix = new Matrix();
        this.width = width;
        this.height = height;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "IconBean{" +
                "matrix=" + matrix +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
