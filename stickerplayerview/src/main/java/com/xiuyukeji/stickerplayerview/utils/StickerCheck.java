package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.StickerException;
import com.xiuyukeji.stickerplayerview.resource.Resource;

/**
 * 贴纸检测
 *
 * @author Created by jz on 2017/4/26 11:59
 */
public class StickerCheck {
    private StickerCheck() {
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
