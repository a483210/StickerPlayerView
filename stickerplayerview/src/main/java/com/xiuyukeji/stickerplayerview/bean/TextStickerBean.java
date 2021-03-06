package com.xiuyukeji.stickerplayerview.bean;

import android.text.StaticLayout;

/**
 * 记录文字贴纸数据
 *
 * @author Created by jz on 2017/4/11 16:58
 */
public class TextStickerBean extends StickerBean {
    private int textWidth;
    private int textHeight;
    private StaticLayout staticLayout;

    private String text;
    private int textColor;
    private int textSize;
    private int curTextSize;

    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;

    private int delayTime;//延迟帧数显示

    private int leftPadding;
    private int topPadding;
    private int rightPadding;
    private int bottomPadding;

    public TextStickerBean(String index, int width, int height,
                           int fromFrame, int toFrame,
                           String text, int textColor, int textSize, int delayTime,
                           int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this(index, width, height,
                fromFrame, toFrame,
                text, textColor, textSize,
                false, false, false, delayTime,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    public TextStickerBean(String index, int width, int height,
                           int fromFrame, int toFrame,
                           String text, int textColor, int textSize,
                           boolean isBold, boolean isItalic, boolean isUnderline, int delayTime,
                           int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        super(index, width, height, fromFrame, toFrame);
        this.text = text;
        this.textColor = textColor;
        this.textSize = textSize;
        this.curTextSize = textSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.isUnderline = isUnderline;
        this.delayTime = delayTime;
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public StaticLayout getStaticLayout() {
        return staticLayout;
    }

    public void setStaticLayout(StaticLayout staticLayout) {
        this.staticLayout = staticLayout;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getCurTextSize() {
        return curTextSize;
    }

    public void setCurTextSize(int curTextSize) {
        this.curTextSize = curTextSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
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

    public void setPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.textWidth = getWidth() - leftPadding - rightPadding;
        this.textHeight = getHeight() - topPadding - bottomPadding;
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
        this.staticLayout = null;
    }

    @Override
    public String toString() {
        return "TextStickerBean{" +
                "textWidth=" + textWidth +
                ", textHeight=" + textHeight +
                ", text='" + text + '\'' +
                ", textColor=" + textColor +
                ", textSize=" + textSize +
                ", curTextSize=" + curTextSize +
                ", isBold=" + isBold +
                ", isItalic=" + isItalic +
                ", isUnderline=" + isUnderline +
                ", leftPadding=" + leftPadding +
                ", topPadding=" + topPadding +
                ", rightPadding=" + rightPadding +
                ", bottomPadding=" + bottomPadding +
                "} " + super.toString();
    }
}
