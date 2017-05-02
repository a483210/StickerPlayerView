package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.FrameRateRange.FRAME_RATE_FROM;
import static com.xiuyukeji.stickerplayerview.annotations.FrameRateRange.FRAME_RATE_TO;

/**
 * 帧率区间
 *
 * @author Created by jz on 2017/4/26 13:49
 */
@IntRange(from = FRAME_RATE_FROM, to = FRAME_RATE_TO)
@Retention(RetentionPolicy.SOURCE)
public @interface FrameRateRange {
    int FRAME_RATE_FROM = 1, FRAME_RATE_TO = 60;
}