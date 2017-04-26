package com.xiuyukeji.stickerplayerview.bean;

/**
 * 记录贴纸数据
 *
 * @author Created by jz on 2017/4/11 16:58
 */
public class StickerBean extends FrameBean {
    private final String index;

    private int dx;
    private int dy;
    private float degrees;
    private float scale;

    public StickerBean(String index, int width, int height, int fromFrame, int toFrame) {
        super(width, height, fromFrame, toFrame);
        this.index = index;
        this.scale = 1f;
    }

    public String getIndex() {
        return index;
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

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
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
                "index='" + index + '\'' +
                ", dx=" + dx +
                ", dy=" + dy +
                ", degrees=" + degrees +
                ", scale=" + scale +
                "} " + super.toString();
    }
}
