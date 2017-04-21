package com.xiuyukeji.stickerplayerview.resource;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xiuyukeji.stickerplayerview.resource.impl.BitmapResource;
import com.xiuyukeji.stickerplayerview.resource.impl.GifResource;

/**
 * 简单工厂
 *
 * @author Created by jz on 2017/4/20 15:44
 */
public class ResourceFactory {

    public static Resource createAssetsResource(@NonNull Context context, @NonNull String path) {
        if (path.endsWith(".gif")) {
            return new GifResource(context, path, Resource.ASSETS);
        } else {
            return new BitmapResource(context, path, Resource.ASSETS);
        }
    }

    public static Resource createFileResource(@NonNull Context context, @NonNull String path) {
        if (path.endsWith(".gif")) {
            return new GifResource(context, path, Resource.FILE);
        } else {
            return new BitmapResource(context, path, Resource.FILE);
        }
    }
}
