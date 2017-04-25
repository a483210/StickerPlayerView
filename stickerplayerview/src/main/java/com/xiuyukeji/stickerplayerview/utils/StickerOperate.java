package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.StickerException;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.resource.Resource;

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
            dst.setFlip(src.isFlip());
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
                src.getLeftPadding(), src.getTopPadding(),
                src.getRightPadding(), src.getBottomPadding());
        dst.setDx(src.getDx());
        dst.setDy(src.getDy());
        dst.setDegrees(src.getDegrees());
        dst.setScale(src.getScale());
        dst.setFlip(src.isFlip());
        return dst;
    }

    /**
     * 判断是否设置帧率
     *
     * @param delayTimeMs 间隔
     */
    public static void checkFrameRateNull(double delayTimeMs) {
        if (delayTimeMs == 0) {
            throw new StickerException("必须先设置帧率(frameRate)！");
        }
    }

    /**
     * 判断帧率是否符合
     *
     * @param delayTimeMs 间隔
     */
    public static void checkFrameRateRange(double delayTimeMs) {
        int frameRate = (int) (1000 / delayTimeMs);
        if (frameRate < 1 || frameRate > 60) {
            throw new StickerException("帧率(frameRate)只能在1到60之间！");
        }
    }

    /**
     * 添加动态贴纸判断是否已经设置帧率
     *
     * @param resource    资源
     * @param delayTimeMs 间隔
     */
    public static void checkDynamicAndFrameRate(Resource resource, double delayTimeMs) {
        checkFrameRateNull(delayTimeMs);
        checkFrameRateRange(delayTimeMs);
    }
}
