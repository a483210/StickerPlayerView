package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.PaddingRange.PADDING_FROM;

/**
 * 边距区间
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntRange(from = PADDING_FROM)
@Retention(RetentionPolicy.SOURCE)
public @interface PaddingRange {
    int PADDING_FROM = 0;
}