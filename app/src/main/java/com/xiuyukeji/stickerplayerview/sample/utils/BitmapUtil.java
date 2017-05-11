package com.xiuyukeji.stickerplayerview.sample.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * 图片加载
 *
 * @author Created by jz on 2017/5/11 11:37
 */
public class BitmapUtil {

    public static void bind(Context context, ImageView v, String url) {
        Glide.with(context)
                .load(url)
                .into(v);
    }
}
