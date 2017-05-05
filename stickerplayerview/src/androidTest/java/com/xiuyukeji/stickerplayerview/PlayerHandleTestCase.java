package com.xiuyukeji.stickerplayerview;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 测试PlayerHandle
 *
 * @author Created by jz on 2017/5/3 17:38
 */
@RunWith(AndroidJUnit4.class)
public class PlayerHandleTestCase extends BaseTestCase {

    private PlayerHandle mPlayerHandle;
    private View mView;

    @Before
    public void setUp() {
        super.setUp();
        mView = mock(View.class);

        runMainAndWait(new Runnable() {
            @Override
            public void run() {
                mPlayerHandle = new PlayerHandle(mView);
                mPlayerHandle.setDelayTime(1000 / 60);
            }
        });
    }

    @After
    public void tearDown() {
        super.tearDown();
        mPlayerHandle = null;
    }

    @Test
    public void testStart() {
        mPlayerHandle.start();
        SystemClock.sleep(1);

        verify(mView).invalidate();
    }

    @Test
    public void testStop() {
        mPlayerHandle.start();
        SystemClock.sleep(1);
        mPlayerHandle.start();
        SystemClock.sleep(1);
        mPlayerHandle.stop();
        SystemClock.sleep(1);
        mPlayerHandle.start();
        SystemClock.sleep(1);

        verify(mView, times(2)).invalidate();
    }

    @Test
    public void testPause() {
        mPlayerHandle.start();
        SystemClock.sleep(1);
        mPlayerHandle.pause();
        mPlayerHandle.start();
        SystemClock.sleep(1);

        verify(mView).invalidate();
    }

    @Test
    public void testResume() {
        mPlayerHandle.resume();
        SystemClock.sleep(1);
        mPlayerHandle.start();
        SystemClock.sleep(1);
        mPlayerHandle.resume();
        SystemClock.sleep(1);

        verify(mView).invalidate();
    }

    @Test
    public void testNextFrameAndCancel() {
        mPlayerHandle.nextFrame();
        SystemClock.sleep(1);
        mPlayerHandle.start();
        SystemClock.sleep(1);
        mPlayerHandle.nextFrame();
        mPlayerHandle.cancel();
        mPlayerHandle.nextFrame();
        SystemClock.sleep(20);

        verify(mView, times(2)).invalidate();
    }
}