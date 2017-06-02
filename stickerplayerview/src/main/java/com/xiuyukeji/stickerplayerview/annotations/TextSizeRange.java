package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.TextSizeRange.TEXT_SIZE_FROM;

/**
 * 文字大小区间
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntRange(from = TEXT_SIZE_FROM)
@Retention(RetentionPolicy.SOURCE)
public @interface TextSizeRange {
    int TEXT_SIZE_FROM = 12;
}