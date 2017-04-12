package com.xiuyukeji.stickerplayerview.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.utils.BitmapSource.ASSETS;
import static com.xiuyukeji.stickerplayerview.utils.BitmapSource.FILE;

/**
 * 图像来源
 *
 * @author Created by jz on 2017/4/12 10:15
 */
@IntDef({FILE, ASSETS})
@Retention(RetentionPolicy.SOURCE)
public @interface BitmapSource {
    int FILE = 0, ASSETS = 1;
}