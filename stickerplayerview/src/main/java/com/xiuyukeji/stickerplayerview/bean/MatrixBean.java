package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Matrix;

/**
 * 基类，存储矩阵
 *
 * @author Created by jz on 2017/4/14 10:56
 */
public abstract class MatrixBean {
    private final Matrix matrix;
    private final int width;
    private final int height;

    public MatrixBean(int width, int height) {
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
        return "MatrixBean{" +
                "matrix=" + matrix.toString() +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
