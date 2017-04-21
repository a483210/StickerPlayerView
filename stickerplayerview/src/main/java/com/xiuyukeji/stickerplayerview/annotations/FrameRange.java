package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 帧区间
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntRange(from = 0)
@Retention(RetentionPolicy.SOURCE)
public @interface FrameRange {
}