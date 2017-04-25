package com.xiuyukeji.stickerplayerview.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiuyukeji.stickerplayerview.annotations.PlayerSource.EDIT;
import static com.xiuyukeji.stickerplayerview.annotations.PlayerSource.PLAYER;

/**
 * 贴纸播放器状态
 *
 * @author Created by jz on 2017/4/24 17:55
 */
@IntDef({EDIT, PLAYER})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerSource {
    int EDIT = 0, PLAYER = 1;
}