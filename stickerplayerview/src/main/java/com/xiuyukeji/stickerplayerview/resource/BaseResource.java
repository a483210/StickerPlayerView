package com.xiuyukeji.stickerplayerview.resource;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xiuyukeji.stickerplayerview.annotations.ResourceSource;

/**
 * 资源，如果是动态资源必须保证每帧的宽高一致
 * 本身不要存bitmap
 *
 * @author Created by jz on 2017/4/17 14:19
 */
public abstract class BaseResource implements Resource {
    protected final Context mContext;
    protected final String mPath;
    protected final int mSource;

    private int mUseCount;

    public BaseResource(@NonNull Context context, @NonNull String path, @ResourceSource int source) {
        this.mContext = context;
        this.mPath = path;
        this.mSource = source;
        this.mUseCount = 1;
    }

    @Override
    public final String getIndex() {
        return mPath;
    }

    @Override
    public final int addedUseCount() {
        return ++mUseCount;
    }

    @Override
    public final int decrementUseCount() {
        return --mUseCount;
    }
}
