package com.xiuyukeji.stickerplayerview.bean;

/**
 * 记录贴纸帧
 *
 * @author Created by jz on 2017/4/17 15:26
 */
public class FrameBean extends MatrixBean {

    private int fromFrame;
    private int toFrame;

    public FrameBean(int width, int height, int fromFrame, int toFrame) {
        super(width, height);
        this.fromFrame = fromFrame;
        this.toFrame = toFrame;
    }

    public int getFromFrame() {
        return fromFrame;
    }

    public void setFromFrame(int fromFrame) {
        this.fromFrame = fromFrame;
    }

    public int getToFrame() {
        return toFrame;
    }

    public void setToFrame(int toFrame) {
        this.toFrame = toFrame;
    }

    @Override
    public String toString() {
        return "FrameBean{" +
                "fromFrame=" + fromFrame +
                ", toFrame=" + toFrame +
                "} " + super.toString();
    }
}
