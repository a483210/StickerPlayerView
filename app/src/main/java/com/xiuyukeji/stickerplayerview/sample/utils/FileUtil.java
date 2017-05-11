package com.xiuyukeji.stickerplayerview.sample.utils;

import android.content.Context;
import android.text.TextUtils;

import com.xiuyukeji.stickerplayerview.sample.CustomApplication;

import java.io.File;

import static com.blankj.utilcode.util.SDCardUtils.isSDCardEnable;

/**
 * 文件工具类
 *
 * @author Created by jz on 2017/5/11 10:47
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 创建文件夹
     */
    public static String createDir(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return path;
    }

    /**
     * 获得缓存目录
     */
    public static String getCacheDir(String name) {
        Context context = CustomApplication.getAppContext();
        String path = null;
        if (isSDCardEnable()) {
            File df = context.getExternalCacheDir();
            if (df != null)
                path = df.getPath();
        }
        if (TextUtils.isEmpty(path))
            path = context.getCacheDir().getPath();
        if (!TextUtils.isEmpty(name))
            path += File.separator + name;
        return createDir(path);
    }

    /**
     * 获得缓存目录
     */
    public static String getCacheDir() {
        return getCacheDir(null);
    }

    /**
     * 获得图片缓存目录
     */
    public static String getImgCacheDir() {
        return getCacheDir("images");
    }
}
