package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.PositionRange.POSITION_FROM;

/**
 * 索引区间
 *
 * @author Created by jz on 2017/4/26 13:41
 */
@IntRange(from = POSITION_FROM)
@Retention(RetentionPolicy.SOURCE)
public @interface PositionRange {
    int POSITION_FROM = -1;
}