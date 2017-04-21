package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 文字大小区间
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntRange(from = 12, to = 120)
@Retention(RetentionPolicy.SOURCE)
public @interface TextSizeRange {
}