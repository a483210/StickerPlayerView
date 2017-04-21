package com.xiuyukeji.stickerplayerview.resource;

import android.support.annotation.IntRange;

/**
 * 动态资源
 *
 * @author Created by jz on 2017/4/21 10:22
 */
public interface DynamicResource {
    /**
     * 返回总帧数
     */
    @IntRange(from = 1)
    int getFrameCount();

    /**
     * 返回总时间
     */
    int getDuration();
}
