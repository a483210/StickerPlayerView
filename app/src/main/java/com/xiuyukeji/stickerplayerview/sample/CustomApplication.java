package com.xiuyukeji.stickerplayerview.sample;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * Application
 *
 * @author Created by jz on 2017/5/11 10:46
 */
public class CustomApplication extends Application {

    private static CustomApplication mApplication;

    public static CustomApplication getInstance() {
        return mApplication;
    }

    public static Context getAppContext() {
        return mApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        Utils.init(getApplicationContext());
    }
}
