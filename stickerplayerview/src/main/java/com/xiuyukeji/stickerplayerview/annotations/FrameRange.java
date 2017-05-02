package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.FrameRange.FRAME_FROM;

/**
 * 帧区间
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntRange(from = FRAME_FROM)
@Retention(RetentionPolicy.SOURCE)
public @interface FrameRange {
    int FRAME_FROM = 0;
}