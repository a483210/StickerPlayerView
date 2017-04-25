package com.xiuyukeji.stickerplayerview;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

/**
 * 动态贴图播放控制
 *
 * @author Created by jz on 2017/4/18 13:52
 */
public class PlayerHandle {

    private static final int MSG_FRAME = 0;

    private static final int STOP = 0, START = 1;

    private final View mView;

    private final FrameHandler mHandler;
    private long mCurrentUptimeMs;
    private double mDelayTime;

    private int mState = STOP;

    public PlayerHandle(View view) {
        this.mView = view;

        mHandler = new FrameHandler();
    }

    /**
     * 设置播放间隔
     *
     * @param delayTime 时间
     */
    public void setDelayTime(double delayTime) {
        this.mDelayTime = delayTime;
    }

    /**
     * 获得播放间隔
     */
    public double getDelayTime() {
        return mDelayTime;
    }

    /**
     * 获得当前时间
     */
    public long getCurrentUptime() {
        return mCurrentUptimeMs;
    }

    /**
     * 开始播放
     */
    public void start() {
        if (isPlaying()) {
            return;
        }
        mState = START;

        mCurrentUptimeMs = SystemClock.uptimeMillis();
        play();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (!isPlaying()) {
            return;
        }
        mHandler.removeMessages(MSG_FRAME);

        mState = STOP;
    }

    /**
     * 是否播放中
     */
    public boolean isPlaying() {
        return mState == START;
    }

    //播放
    private void play() {
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_FRAME), mCurrentUptimeMs);
    }

    //下一帧
    private void nextFrame() {
        mCurrentUptimeMs += mDelayTime;
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_FRAME), mCurrentUptimeMs);
    }

    //handler
    private class FrameHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FRAME:
                    mView.invalidate();
                    nextFrame();
                    break;
            }
        }
    }
}
