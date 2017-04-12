package com.xiuyukeji.stickerplayerview.bean;

/**
 * 记录文字贴纸数据
 *
 * @author Created by jz on 2017/4/11 16:58
 */
public class TextStickerBean extends StickerBean {

    private String text;
    private int textColor;
    private int textSize;
    private int curTextSize;

    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;

    private int leftPadding;
    private int topPadding;
    private int rightPadding;
    private int bottomPadding;

    public TextStickerBean(String text, int textColor, int textSize,
                           boolean isBold, boolean isItalic, boolean isUnderline,
                           int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this(null, 0, 0,
                text, textColor, textSize,
                isBold, isItalic, isUnderline,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    public TextStickerBean(String index, int width, int height,
                           String text, int textColor, int textSize,
                           boolean isBold, boolean isItalic, boolean isUnderline,
                           int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        super(index, width, height);
        this.text = text;
        this.textColor = textColor;
        this.textSize = textSize;
        this.curTextSize = textSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.isUnderline = isUnderline;
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
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

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    @Override
    public String toString() {
        return "TextStickerBean{" +
                "text='" + text + '\'' +
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
