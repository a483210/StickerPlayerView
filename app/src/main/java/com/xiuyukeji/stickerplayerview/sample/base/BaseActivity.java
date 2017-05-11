package com.xiuyukeji.stickerplayerview.sample.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiuyukeji.stickerplayerview.sample.helper.LoadingHelper;
import com.xiuyukeji.stickerplayerview.sample.helper.intefaces.OnCancelListener;
import com.xiuyukeji.stickerplayerview.sample.helper.intefaces.OnDismissListener;

import butterknife.ButterKnife;

/**
 * Activity基类
 *
 * @author Created by jz on 2017/5/11 10:53
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    private LoadingHelper mLoadingHelper;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        findView();
    }

    /**
     * 初始加载
     */
    @CallSuper
    protected void findView() {
        ButterKnife.bind(this);

        mLoadingHelper = new LoadingHelper(this);

        initView();
        setListener();
    }

    /**
     * 返回布局Id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void initView();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    protected final void showLoading(String msg, boolean isCancel) {
        showLoading(msg, 0, isCancel, null, null);
    }

    protected final void showLoading(String msg, long waitTime, boolean isCancel) {
        showLoading(msg, waitTime, isCancel, null, null);
    }

    protected final void showLoading(String msg, OnCancelListener cancelL) {
        showLoading(msg, 0, false, cancelL, null);
    }

    protected final void showLoading(String msg, long waitTime, OnCancelListener cancelL) {
        showLoading(msg, waitTime, false, cancelL, null);
    }

    protected final void showLoading(String msg,
                                     OnCancelListener cancelL, OnDismissListener dismissL) {
        showLoading(msg, 0, false, cancelL, dismissL);
    }

    /**
     * 显示loading
     *
     * @param msg 内容
     */
    protected final void showLoading(String msg, long waitTime, boolean isCancel, OnCancelListener
            cancelL, OnDismissListener dismissL) {
        mLoadingHelper.showLoading(msg, waitTime);
        mLoadingHelper.setCancel(isCancel);
        mLoadingHelper.setOnCancelListener(cancelL);
        mLoadingHelper.setOnDismissListener(() -> {
            mLoadingHelper.unbindListener();
            if (dismissL != null)
                dismissL.onDismiss();
        });
    }

    protected final void hideLoading() {
        mLoadingHelper.hideLoading();
    }

    /**
     * 隐藏loading
     */
    protected final void hideLoading(long waitTime) {
        mLoadingHelper.hideLoading(waitTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }
}
