package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.resource.Resource.ASSETS;
import static com.xiuyukeji.stickerplayerview.resource.Resource.FILE;

/**
 * 资源来源
 *
 * @author Created by jz on 2017/4/19 11:57
 */
@IntDef({FILE, ASSETS})
@Retention(RetentionPolicy.SOURCE)
public @interface ResourceSource {
}