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
    private long mStartUptimeMs;
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
        return mCurrentUptimeMs - mStartUptimeMs;
    }

    /**
     * 开始播放
     */
    public void start() {
        if (mState == START) {
            return;
        }
        mState = START;

        mStartUptimeMs = SystemClock.uptimeMillis();
        mCurrentUptimeMs = mStartUptimeMs;
        play();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mState == STOP) {
            return;
        }
        cancel();

        mState = STOP;
    }


    /**
     * 下一帧
     */
    public void nextFrame() {
        long uptimeMs = SystemClock.uptimeMillis();
        while (mCurrentUptimeMs <= uptimeMs) {//计算下一个时间，用于跳帧
            mCurrentUptimeMs += mDelayTime;
        }
        play();
    }

    //播放
    private void play() {
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_FRAME), mCurrentUptimeMs);
    }

    /**
     * 取消帧
     */
    public void cancel() {
        mHandler.removeMessages(MSG_FRAME);
    }

    //handler
    private class FrameHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FRAME:
                    mView.invalidate();
                    break;
                default:
                    break;
            }
        }
    }
}
