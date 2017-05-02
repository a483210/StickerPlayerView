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

    private static final int STOP = 0, START = 1, PAUSE = 2;

    private final View mView;

    private final FrameHandler mHandler;
    private long mStartUptimeMs;
    private long mCurrentUptimeMs;
    private double mDelayTime;

    private int mState = STOP;
    private boolean mIsSendMessage;

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
     * 暂停播放
     */
    public void pause() {
        if (mState != START) {
            return;
        }
        mState = PAUSE;

        cancel();
    }

    /**
     * 恢复播放
     */
    public void resume() {
        if (mState != PAUSE) {
            return;
        }
        mState = START;

        nextFrame();
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
        if (mState != START) {
            return;
        }

        long uptimeMs = SystemClock.uptimeMillis();
        if (mCurrentUptimeMs <= uptimeMs) {//计算下一个时间，用于跳帧
            mCurrentUptimeMs = (long) (uptimeMs + mDelayTime);
        }

        play();
    }

    //播放
    private void play() {
        if (mIsSendMessage) {
            return;
        }
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_FRAME), mCurrentUptimeMs);
        mIsSendMessage = true;
    }

    /**
     * 取消帧
     */
    public void cancel() {
        if (!mIsSendMessage) {
            return;
        }
        mIsSendMessage = false;
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
