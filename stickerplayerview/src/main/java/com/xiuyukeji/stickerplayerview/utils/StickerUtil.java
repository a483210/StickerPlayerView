package com.xiuyukeji.stickerplayerview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 贴纸工具类
 *
 * @author Created by jz on 2017/4/12 9:32
 */
public final class StickerUtil {

    public static final int PAINT_FLAG = Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

    private StickerUtil() {
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     */
    public static int dpToPx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 添加背景
     *
     * @param srcBitmap 来源图片
     * @param padding   边距
     */
    public static Bitmap attachBackground(Bitmap srcBitmap, int color, int padding) {
        if (srcBitmap == null || srcBitmap.isRecycled()) {
            return null;
        }
        Paint paint = new Paint(PAINT_FLAG);
        paint.setColor(color);

        int width = srcBitmap.getWidth() + padding * 2;
        int height = srcBitmap.getHeight() + padding * 2;

        Bitmap dstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, paint);
        canvas.drawBitmap(srcBitmap, padding, padding, paint);

        srcBitmap.recycle();
        return dstBitmap;
    }
}
