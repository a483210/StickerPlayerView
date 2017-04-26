package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

/**
 * 贴纸操作
 *
 * @author Created by jz on 2017/4/18 13:22
 */
public class StickerOperate {

    private StickerOperate() {
    }

    /**
     * 复制贴纸数据
     *
     * @param src       数据来源
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public static StickerBean copyStickerBean(StickerBean src, int fromFrame, int toFrame) {
        return copyStickerBean(src, src.getIndex(), src.getWidth(), src.getHeight(),
                fromFrame, toFrame);
    }

    /**
     * 复制贴纸数据，用于替换
     *
     * @param src    数据来源
     * @param index  索引
     * @param width  宽度
     * @param height 高度
     */
    public static StickerBean copyStickerBean(StickerBean src, String index, int width, int height) {
        return copyStickerBean(src, index, width, height,
                src.getFromFrame(), src.getToFrame());
    }

    /**
     * 复制贴纸数据
     *
     * @param src       数据来源
     * @param index     索引
     * @param width     宽度
     * @param height    高度
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    private static StickerBean copyStickerBean(StickerBean src,
                                               String index, int width, int height,
                                               int fromFrame, int toFrame) {
        if (src instanceof TextStickerBean) {
            return copyTextStickerBean((TextStickerBean) src, index,
                    width, height,
                    fromFrame, toFrame);
        } else {
            StickerBean dst = new StickerBean(index, width, height, fromFrame, toFrame);
            dst.setDx(src.getDx());
            dst.setDy(src.getDy());
            dst.setDegrees(src.getDegrees());
            dst.setScale(src.getScale());
            dst.setFlipHorizontal(src.isFlipHorizontal());
            return dst;
        }
    }

    //复制文字贴纸数据
    private static TextStickerBean copyTextStickerBean(TextStickerBean src, String index,
                                                       int width, int height,
                                                       int fromFrame, int toFrame) {
        TextStickerBean dst = new TextStickerBean(
                index, width, height,
                fromFrame, toFrame,
                src.getText(), src.getTextColor(), src.getTextSize(),
                src.isBold(), src.isItalic(), src.isUnderline(),
                src.getDelayTime(),
                src.getLeftPadding(), src.getTopPadding(),
                src.getRightPadding(), src.getBottomPadding());
        dst.setDx(src.getDx());
        dst.setDy(src.getDy());
        dst.setDegrees(src.getDegrees());
        dst.setScale(src.getScale());
        dst.setFlipHorizontal(src.isFlipHorizontal());
        return dst;
    }

    /**
     * 是否在帧内
     *
     * @param frameIndex 帧
     * @param fromFrame  开始帧
     * @param toFrame    结束帧
     */
    public static boolean isFrameInside(int frameIndex, int fromFrame, int toFrame) {
        return frameIndex >= fromFrame && frameIndex <= toFrame;
    }
}
