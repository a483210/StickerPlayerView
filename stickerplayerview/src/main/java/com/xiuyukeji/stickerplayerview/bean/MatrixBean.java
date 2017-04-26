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
    private boolean isFlipHorizontal;
    private boolean isFlipVertical;

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

    public boolean isFlipHorizontal() {
        return isFlipHorizontal;
    }

    public void setFlipHorizontal(boolean flipHorizontal) {
        this.isFlipHorizontal = flipHorizontal;
    }

    public boolean isFlipVertical() {
        return isFlipVertical;
    }

    public void setFlipVertical(boolean flipVertical) {
        isFlipVertical = flipVertical;
    }

    @Override
    public String toString() {
        return "MatrixBean{" +
                "matrix=" + matrix.toString() +
                ", width=" + width +
                ", height=" + height +
                ", isFlipHorizontal=" + isFlipHorizontal +
                ", isFlipVertical=" + isFlipVertical +
                '}';
    }
}
