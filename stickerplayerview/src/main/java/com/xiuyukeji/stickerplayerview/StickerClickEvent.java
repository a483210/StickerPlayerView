package com.xiuyukeji.stickerplayerview;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.ViewConfiguration;

/**
 * 点击事件区分
 *
 * @author Created by jz on 2017/4/13 14:44
 */
abstract class StickerClickEvent {

    private static final int CLICK_MIN_TIME = ViewConfiguration.getTapTimeout();
    private static final int DOUBLE_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int LONG_ClICK_TIMEOUT = ViewConfiguration.getLongPressTimeout();

    private static final int MESSAGE_CLICK = 0;

    private static final int DOWN = 0, UP = 1;

    private final Handler mHandler;

    private long mDownTimeMs;
    private long mLastDownTimeMs;
    private long mLastUpTimeMs;

    private int mTouchState = UP;

    StickerClickEvent() {
        mHandler = new ClickHandler();
    }

    protected void clickDown() {
        mTouchState = DOWN;

        mLastDownTimeMs = mDownTimeMs;
        mDownTimeMs = System.currentTimeMillis();
        postLong();
    }

    protected void clickUp() {
        if (mTouchState == UP) {
            return;
        }

        mTouchState = UP;

        long upTimeMs = System.currentTimeMillis();

        //注意单击事件和双击事件互斥，并且这里不会像GestureDetector一样将单击事件延迟
        if (mLastDownTimeMs > 0 &&//有上次事件
                mDownTimeMs - mLastDownTimeMs <= DOUBLE_TIMEOUT &&//二次点击间隔符合
                mLastUpTimeMs - mLastDownTimeMs <= CLICK_MIN_TIME) {//上次事件是单击事件
            doubleClick();
        } else if (upTimeMs - mDownTimeMs <= CLICK_MIN_TIME) {
            click();
            mLastUpTimeMs = upTimeMs;
        }
    }

    private void postLong() {
        Message message = Message.obtain();
        message.what = MESSAGE_CLICK;
        mHandler.sendMessageAtTime(message, SystemClock.uptimeMillis() + LONG_ClICK_TIMEOUT);
    }

    protected abstract void click();

    protected abstract void doubleClick();

    protected abstract void longClick();

    private class ClickHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CLICK:
                    if (mTouchState == DOWN) {
                        mTouchState = UP;
                        longClick();
                    }
                    break;
            }
        }
    }

    void onDetached() {
        mHandler.removeMessages(MESSAGE_CLICK);
    }
}
