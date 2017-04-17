package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.xiuyukeji.stickerplayerview.bean.BitmapBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

import java.util.HashMap;

import static com.xiuyukeji.stickerplayerview.utils.BitmapSource.FILE;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateTextSticker;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.PAINT_FLAG;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.readAssetsBitmap;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.readBitmap;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.recycleBitmap;

/**
 * 贴纸渲染类
 *
 * @author Created by jz on 2017/4/11 17:16
 */
class StickerRenderer {

    private final HashMap<String, BitmapBean> mBitmapBuffers;

    private final Paint mPaint;
    private final TextPaint mTextPaint;
    private final Paint mFramePaint;

    private final float[] mPoint;

    private Bitmap mDelBitmap;
    private Bitmap mCopyBitmap;
    private Bitmap mDragBitmap;
    private Bitmap mFlipBitmap;

    StickerRenderer() {
        this.mBitmapBuffers = new HashMap<>();

        mPaint = new Paint(PAINT_FLAG);
        mTextPaint = new TextPaint(PAINT_FLAG);
        mFramePaint = new Paint(PAINT_FLAG);

        mPoint = new float[2];
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
        Bitmap bitmap = mBitmapBuffers.get(stickerBean.getIndex()).getBitmap();
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

        String text = textStickerBean.getText();
        if (TextUtils.isEmpty(text)) {
            text = "双击输入内容";
        }

        StaticLayout staticLayout = textStickerBean.getStaticLayout();
        if (staticLayout == null || !staticLayout.getText().equals(text)) {
            staticLayout = new StaticLayout(text, mTextPaint, textStickerBean.getTextWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            textStickerBean.setStaticLayout(staticLayout);
        }

        calculateTextSticker(textStickerBean, mPoint, staticLayout.getHeight());//这里需要重新计算文字位置

        canvas.save();

        canvas.concat(textStickerBean.getMatrix());
        canvas.translate(mPoint[0], mPoint[1]);

        staticLayout.draw(canvas);

        canvas.restore();
    }

    void drawSelected(Canvas canvas, float[] framePoint,
                      Matrix delMatrix, Matrix copyMatrix, Matrix dragMatrix, Matrix flipMatrix,
                      StickerBean stickerBean) {
        if (TextUtils.isEmpty(stickerBean.getIndex())) {//如果没有背景则不绘制选择框
            return;
        }

        drawFrame(canvas, framePoint);

        canvas.drawBitmap(mDelBitmap, delMatrix, mPaint);
        canvas.drawBitmap(mCopyBitmap, copyMatrix, mPaint);
        canvas.drawBitmap(mDragBitmap, dragMatrix, mPaint);
        canvas.drawBitmap(mFlipBitmap, flipMatrix, mPaint);
    }

    private void drawFrame(Canvas canvas, float[] framePoint) {
        canvas.drawLine(framePoint[0], framePoint[1],
                framePoint[2], framePoint[3], mFramePaint);
        canvas.drawLine(framePoint[2], framePoint[3],
                framePoint[6], framePoint[7], mFramePaint);
        canvas.drawLine(framePoint[6], framePoint[7],
                framePoint[4], framePoint[5], mFramePaint);
        canvas.drawLine(framePoint[4], framePoint[5],
                framePoint[0], framePoint[1], mFramePaint);
    }

    Bitmap getBitmapBuffer(Context context, int source, String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapBean bitmapBean = mBitmapBuffers.get(path);
        if (bitmapBean == null) {
            Bitmap bitmap;
            if (source == FILE) {
                bitmap = readBitmap(path);
            } else {
                bitmap = readAssetsBitmap(context, path);
            }
            bitmapBean = new BitmapBean(bitmap);
            mBitmapBuffers.put(path, bitmapBean);
        } else {
            bitmapBean.setUseCount(bitmapBean.getUseCount() + 1);
        }
        return bitmapBean.getBitmap();
    }

    void addUserCount(String index) {
        if (TextUtils.isEmpty(index)) {
            return;
        }
        BitmapBean bitmapBean = mBitmapBuffers.get(index);
        if (bitmapBean == null) {
            return;
        }
        bitmapBean.setUseCount(bitmapBean.getUseCount() + 1);
    }

    void cutUserCount(String index) {
        if (TextUtils.isEmpty(index)) {
            return;
        }
        BitmapBean bitmapBean = mBitmapBuffers.get(index);
        if (bitmapBean == null) {
            return;
        }

        bitmapBean.setUseCount(bitmapBean.getUseCount() - 1);

        if (bitmapBean.getUseCount() <= 0) {
            recycleBitmap(bitmapBean.getBitmap());
            mBitmapBuffers.remove(index);
        }
    }
}