package com.xiuyukeji.stickerplayerview;

import com.xiuyukeji.stickerplayerview.resource.DynamicResource;

import java.util.ArrayList;

/**
 * 动态贴图播放控制
 *
 * @author Created by jz on 2017/4/18 13:52
 */
public class PlayerHandle {

    private static final int STOP = 0, START = 1;

    private final ArrayList<DynamicResource> mDynamicResources;

    private int mState = STOP;

    public PlayerHandle() {
        this.mDynamicResources = new ArrayList<>();
    }

    public void start() {
        if (isPlaying()) {
            return;
        }


        mState = START;
    }

    public void stop() {
        if (!isPlaying()) {
            return;
        }

        mState = STOP;
    }

    public boolean isPlaying() {
        return mState == START;
    }

}
