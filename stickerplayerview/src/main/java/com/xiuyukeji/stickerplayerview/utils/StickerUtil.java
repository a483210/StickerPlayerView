package com.xiuyukeji.stickerplayerview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 贴纸工具类
 *
 * @author Created by jz on 2017/4/12 9:32
 */
public class StickerUtil {

    public static final int PAINT_FLAG = Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG;

    private StickerUtil() {
    }

    /**
     * 复制贴纸数据
     *
     * @param src 数据来源
     */
    public static StickerBean copyStickerBean(StickerBean src) {
        return copyStickerBean(src, "", null);
    }

    /**
     * 复制贴纸数据
     *
     * @param src   数据来源
     * @param index 图片索引
     */
    public static StickerBean copyStickerBean(StickerBean src, String index, Bitmap bitmap) {
        String newIndex;
        int width;
        int height;
        if ("".equals(index)) {
            newIndex = src.getIndex();
            width = src.getWidth();
            height = src.getHeight();
        } else {
            if (index != null) {
                newIndex = index;
            } else {
                newIndex = src.getIndex();
            }
            if (bitmap != null) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
            } else {
                width = src.getWidth();
                height = src.getHeight();
            }
        }
        if (src instanceof TextStickerBean) {
            return copyTextStickerBean((TextStickerBean) src, newIndex, width, height);
        } else {
            StickerBean dst = new StickerBean(newIndex, width, height);
            dst.setDx(src.getDx());
            dst.setDy(src.getDy());
            dst.setDegrees(src.getDegrees());
            dst.setScale(src.getScale());
            dst.setFlip(src.isFlip());
            return dst;
        }
    }

    //复制文字贴纸数据
    private static TextStickerBean copyTextStickerBean(TextStickerBean src, String index, int width, int height) {
        TextStickerBean dst = new TextStickerBean(
                index, width, height,
                src.getText(), src.getTextColor(), src.getTextSize(),
                src.isBold(), src.isItalic(), src.isUnderline(),
                src.getLeftPadding(), src.getTopPadding(), src.getRightPadding(), src.getBottomPadding());
        dst.setDx(src.getDx());
        dst.setDy(src.getDy());
        dst.setDegrees(src.getDegrees());
        dst.setScale(src.getScale());
        dst.setFlip(src.isFlip());
        return dst;
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
        if (srcBitmap == null) {
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

    /**
     * 从本地目录读取图片
     *
     * @param path 地址
     */
    public static Bitmap readBitmap(String path) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(path));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从Assets读取图片
     *
     * @param context 上下文
     * @param path    地址
     */
    public static Bitmap readAssetsBitmap(Context context, String path) {
        try {
            InputStream is = context.getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 回收Bitmap内存
     *
     * @param bitmap 图片
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
