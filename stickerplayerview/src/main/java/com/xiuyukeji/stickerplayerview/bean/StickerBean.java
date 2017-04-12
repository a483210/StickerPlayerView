package com.xiuyukeji.stickerplayerview.bean;

import android.graphics.Matrix;

/**
 * 记录贴纸数据
 *
 * @author Created by jz on 2017/4/11 16:58
 */
public class StickerBean {
    private final Matrix matrix;

    private final String index;
    private final int width;
    private final int height;

    private int dx;
    private int dy;
    private int degrees;
    private float scale;

    public StickerBean(String index, int width, int height) {
        this.matrix = new Matrix();
        this.index = index;
        this.width = width;
        this.height = height;
        this.scale = 1f;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public String getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "StickerBean{" +
                ", index=" + index +
                ", width=" + width +
                ", height=" + height +
                ", dx=" + dx +
                ", dy=" + dy +
                ", degrees=" + degrees +
                ", scale=" + scale +
                '}';
    }
}
