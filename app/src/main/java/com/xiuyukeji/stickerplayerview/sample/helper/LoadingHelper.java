package com.xiuyukeji.stickerplayerview.sample.helper;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;

import com.xiuyukeji.stickerplayerview.sample.R;
import com.xiuyukeji.stickerplayerview.sample.helper.intefaces.OnCancelListener;
import com.xiuyukeji.stickerplayerview.sample.helper.intefaces.OnDismissListener;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Loading帮助类
 *
 * @author Created by jz on 2016/12/28 15:29
 */
public class LoadingHelper {

    private Context mContext;

    private SweetAlertDialog mLoadingDialog;

    private boolean mIsCancel = true;//是否可以被取消
    private boolean mIsCancelled = true;//是否取消了
    private Disposable mDisposable;
    private long mShowUptimeMs;

    private OnCancelListener mOnCancelListener;
    private OnDismissListener mOnDismissListener;

    public LoadingHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void showLoading(String msg) {
        showLoading(msg, 0);
    }

    /**
     * 显示loading
     *
     * @param msg      内容
     * @param waitTime 等待显示时间
     */
    public void showLoading(String msg, long waitTime) {
        if (mDisposable != null) {
            return;
        }

        if (mLoadingDialog != null) {
            return;
        }

        if (waitTime > 0) {
            mDisposable = Single
                    .timer(waitTime, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnDispose(() -> mDisposable = null)
                    .subscribe((aLong, throwable) -> openLoading(msg));
        } else {
            openLoading(msg);
        }
    }

    private void openLoading(String msg) {
        if (mContext instanceof Activity && ((Activity) mContext).isDestroyed()) {
            return;
        }
        mLoadingDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mLoadingDialog.getProgressHelper().setBarColor(mContext.getResources().getColor(R.color.redTextColor));
        mLoadingDialog.setTitleText(msg);
        if (mIsCancel) {
            mLoadingDialog.setCanceledOnTouchOutside(true);
        } else {
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.setOnDismissListener(dialog -> {
            mLoadingDialog = null;
            if (mIsCancelled && mOnCancelListener != null) {
                mOnCancelListener.onCancel();
            }
            mIsCancelled = true;
            if (mOnDismissListener != null) {
                mOnDismissListener.onDismiss();
            }
        });
        mLoadingDialog.show();

        mShowUptimeMs = SystemClock.uptimeMillis();
    }

    public void hideLoading() {
        hideLoading(0);
    }

    /**
     * 隐藏Loading
     *
     * @param waitTime 等待隐藏时间
     */
    public void hideLoading(long waitTime) {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
        if (mLoadingDialog == null) {
            return;
        }

        long delayTime = SystemClock.uptimeMillis() - mShowUptimeMs;
        if (waitTime > delayTime) {
            mDisposable = Single
                    .timer(waitTime - delayTime, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnDispose(() -> mDisposable = null)
                    .subscribe((aLong, throwable) -> closeLoading());
        } else {
            closeLoading();
        }
    }

    private void closeLoading() {
        mIsCancelled = false;
        mLoadingDialog.dismiss();
    }

    /**
     * 设置是否可以被取消
     *
     * @param isCancel 是否
     */
    public void setCancel(boolean isCancel) {
        this.mIsCancel = isCancel;
    }

    /**
     * 取消时回调
     *
     * @param l 回调
     */
    public void setOnCancelListener(OnCancelListener l) {
        this.mOnCancelListener = l;
    }

    /**
     * 绑定Dismiss
     *
     * @param l 回调
     */
    public void setOnDismissListener(OnDismissListener l) {
        this.mOnDismissListener = l;
    }

    /**
     * 解除回调
     */
    public void unbindListener() {
        this.mOnCancelListener = null;
        this.mOnDismissListener = null;
    }
}
