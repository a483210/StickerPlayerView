package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.text.TextUtils;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

import java.util.HashMap;

import static com.xiuyukeji.stickerplayerview.utils.BitmapSource.FILE;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.PAINT_FLAG;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.readAssetsBitmap;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.readBitmap;

/**
 * 贴纸渲染类
 *
 * @author Created by jz on 2017/4/11 17:16
 */
class StickerRenderer {

    private final HashMap<String, Bitmap> mBitmapBuffers;

    private final Paint mPaint;
    private final TextPaint mTextPaint;
    private final Paint mFramePaint;

    private Bitmap mDelBitmap;
    private Bitmap mCopyBitmap;
    private Bitmap mDragBitmap;
    private Bitmap mFlipBitmap;

    StickerRenderer() {
        this.mBitmapBuffers = new HashMap<>();

        mPaint = new Paint(PAINT_FLAG);

        mTextPaint = new TextPaint(PAINT_FLAG);

        mFramePaint = new Paint(PAINT_FLAG);
        mFramePaint.setStyle(Paint.Style.STROKE);
    }

    void setFrameStyle(Bitmap delBitmap, Bitmap copyBitmap, Bitmap dragBitmap, Bitmap flipBitmap,
                       int frameColor, int frameWidth) {
        this.mDelBitmap = delBitmap;
        this.mCopyBitmap = copyBitmap;
        this.mDragBitmap = dragBitmap;
        this.mFlipBitmap = flipBitmap;

        this.mFramePaint.setColor(frameColor);
        this.mFramePaint.setStrokeWidth(frameWidth);
    }

    void drawSticker(Canvas canvas, StickerBean stickerBean) {
        Bitmap bitmap = mBitmapBuffers.get(stickerBean.getIndex());
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        canvas.drawBitmap(bitmap, stickerBean.getMatrix(), mPaint);
    }

    void drawTextSticker(Canvas canvas, TextStickerBean textStickerBean) {
        drawSticker(canvas, textStickerBean);

        mTextPaint.setTextSize(textStickerBean.getCurTextSize());
        mTextPaint.setColor(textStickerBean.getTextColor());
        mTextPaint.setFakeBoldText(textStickerBean.isBold());
        mTextPaint.setTextSkewX(textStickerBean.isItalic() ? -0.5f : 0f);
        mTextPaint.setUnderlineText(textStickerBean.isUnderline());

        canvas.drawText(textStickerBean.getText(), 0, 0, mTextPaint);
    }

    void drawSelected(Canvas canvas, Path framePath,
                      Matrix delMatrix, Matrix copyMatrix, Matrix dragMatrix, Matrix flipMatrix,
                      StickerBean stickerBean) {
        if (TextUtils.isEmpty(stickerBean.getIndex())) {//如果没有背景则不绘制选择框
            return;
        }
        canvas.drawPath(framePath, mFramePaint);

        canvas.drawBitmap(mDelBitmap, delMatrix, mPaint);
        canvas.drawBitmap(mCopyBitmap, copyMatrix, mPaint);
        canvas.drawBitmap(mDragBitmap, dragMatrix, mPaint);
        canvas.drawBitmap(mFlipBitmap, flipMatrix, mPaint);
    }

    Bitmap getBitmapBuffer(Context context, int source, String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Bitmap bitmap = mBitmapBuffers.get(path);
        if (bitmap == null) {
            if (source == FILE) {
                bitmap = readBitmap(path);
            } else {
                bitmap = readAssetsBitmap(context, path);
            }
            mBitmapBuffers.put(path, bitmap);
        }
        return bitmap;
    }

}
