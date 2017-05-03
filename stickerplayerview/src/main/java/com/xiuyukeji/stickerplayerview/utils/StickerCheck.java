package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.StickerException;
import com.xiuyukeji.stickerplayerview.annotations.FrameRange;
import com.xiuyukeji.stickerplayerview.annotations.FrameRateRange;
import com.xiuyukeji.stickerplayerview.annotations.PaddingRange;
import com.xiuyukeji.stickerplayerview.annotations.PositionRange;
import com.xiuyukeji.stickerplayerview.annotations.TextSizeRange;
import com.xiuyukeji.stickerplayerview.resource.Resource;

import java.util.Locale;

/**
 * 贴纸检测
 *
 * @author Created by jz on 2017/4/26 11:59
 */
public class StickerCheck {
    private StickerCheck() {
    }

    /**
     * 检查文本大小
     *
     * @param textSize 文本大小
     */
    public static void checkTextSize(int textSize) {
        if (textSize < TextSizeRange.TEXT_SIZE_FROM || textSize > TextSizeRange.TEXT_SIZE_TO) {
            throw new StickerException(String.format(Locale.getDefault(), "文本大小(textSize)必须在[%d-%d]之间！",
                    TextSizeRange.TEXT_SIZE_FROM, TextSizeRange.TEXT_SIZE_TO));
        }
    }

    /**
     * 检查索引
     *
     * @param position 索引
     */
    public static void checkPosition(int position) {
        if (position < PositionRange.POSITION_FROM) {
            throw new StickerException(String.format(Locale.getDefault(), "索引(position)必须大于%d！",
                    PositionRange.POSITION_FROM));
        }
    }

    /**
     * 检查帧序列
     *
     * @param fromFrame 开始帧序列
     * @param toFrame   结束帧序列
     */
    public static void checkFrame(int fromFrame, int toFrame) {
        checkFrame(fromFrame);
        checkFrame(toFrame);
        if (toFrame < fromFrame) {
            throw new StickerException("开始帧序列(fromFrame)必须小于结束帧序列(toFrame)！");
        }
    }

    /**
     * 检查帧序列
     *
     * @param frameIndex 帧序列
     */
    public static void checkFrame(int frameIndex) {
        if (frameIndex < FrameRange.FRAME_FROM) {
            throw new StickerException(String.format(Locale.getDefault(), "帧序列(frameIndex)必须大于%d！",
                    FrameRange.FRAME_FROM));
        }
    }

    /**
     * 检查边距
     *
     * @param padding 边距
     */
    public static void checkPadding(int padding) {
        if (padding < PaddingRange.PADDING_FROM) {
            throw new StickerException(String.format(Locale.getDefault(), "边距(padding)必须大于%d！",
                    PaddingRange.PADDING_FROM));
        }
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
        if (frameRate < FrameRateRange.FRAME_RATE_FROM || frameRate > FrameRateRange.FRAME_RATE_TO) {
            throw new StickerException(String.format(Locale.getDefault(), "帧率(frameRate)必须在[%d-%d]之间！",
                    FrameRateRange.FRAME_RATE_FROM, FrameRateRange.FRAME_RATE_TO));
        }
    }

    /**
     * 判断资源是否为空
     *
     * @param resource 资源
     */
    public static void checkResourceNotNull(Resource resource) {
        if (resource == null) {
            throw new StickerException("资源(resource)不允许为空！");
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
