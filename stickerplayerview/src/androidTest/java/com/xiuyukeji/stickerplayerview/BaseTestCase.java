package com.xiuyukeji.stickerplayerview;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

/**
 * 测试基类
 *
 * @author Created by jz on 2017/5/5 13:17
 */
public abstract class BaseTestCase {

    protected Context mContext;
    protected Instrumentation mInstrumentation;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @After
    public void tearDown() {
        mContext = null;
        mInstrumentation = null;
    }

    protected void runMainAndWait(Runnable runnable) {
        runMain(runnable);
        mInstrumentation.waitForIdleSync();
    }

    protected void runMain(Runnable runnable) {
        mInstrumentation.runOnMainSync(runnable);
    }
}
