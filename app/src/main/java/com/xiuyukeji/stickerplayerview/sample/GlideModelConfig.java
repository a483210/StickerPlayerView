package com.xiuyukeji.stickerplayerview.sample;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.request.target.ViewTarget;
import com.xiuyukeji.stickerplayerview.sample.utils.FileUtil;

import java.io.File;

/**
 * Glide配置
 *
 * @author Created by jz on 2017/5/11 10:50
 */
public class GlideModelConfig extends OkHttpGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 定义缓存大小和位置
        builder.setDiskCache(() -> DiskLruCacheWrapper.get(new File(FileUtil.getImgCacheDir()), 300 * 1024 * 1024));
        ViewTarget.setTagId(R.id.img_tag);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}