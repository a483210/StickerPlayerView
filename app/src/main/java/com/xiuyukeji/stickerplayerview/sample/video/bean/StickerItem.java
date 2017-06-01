package com.xiuyukeji.stickerplayerview.sample.video.bean;

/**
 * 贴纸数据
 *
 * @author Created by jz on 2017/6/1 11:03
 */
public class StickerItem {
    public static final int NORMAL = 0, TEXT = 1;

    private final int type;
    private final String path;
    private final String text;
    private final int textColor;
    private final int textSize;
    private final int leftPadding;
    private final int topPadding;
    private final int rightPadding;
    private final int bottomPadding;

    public StickerItem(int type, String path, String text, int textColor, int textSize,
                       int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.type = type;
        this.path = path;
        this.text = text;
        this.textColor = textColor;
        this.textSize = textSize;
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
    }

    public int getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    @Override
    public String toString() {
        return "StickerItem{" +
                "type=" + type +
                ", path='" + path + '\'' +
                ", text='" + text + '\'' +
                ", textColor=" + textColor +
                ", textSize=" + textSize +
                ", leftPadding=" + leftPadding +
                ", topPadding=" + topPadding +
                ", rightPadding=" + rightPadding +
                ", bottomPadding=" + bottomPadding +
                '}';
    }
}
