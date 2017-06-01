package com.xiuyukeji.stickerplayerview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateTextSticker;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.PAINT_FLAG;

/**
 * 贴纸渲染
 *
 * @author Created by jz on 2017/4/11 17:16
 */
public class RendererHandle {

    private final Paint mPaint;
    private final TextPaint mTextPaint;
    private final Paint mSidePaint;

    private final float[] mTextPoint;

    private IconBean mLeftTopIconBean;
    private IconBean mRightTopIconBean;
    private IconBean mDragIconBean;
    private IconBean mLeftBottomIconBean;

    private float[] mSidePoint;

    public RendererHandle() {
        mPaint = new Paint(PAINT_FLAG);
        mTextPaint = new TextPaint(PAINT_FLAG);
        mSidePaint = new Paint(PAINT_FLAG);

        mTextPoint = new float[2];
    }

    //设置边框属性
    public void setSelectedStyle(IconBean leftTopIconBean, IconBean rightTopIconBean,
                                 IconBean dragIconBean, IconBean leftBottomIconBean,
                                 float[] sidePoint, int sideColor, int sideWidth) {
        this.mLeftTopIconBean = leftTopIconBean;
        this.mRightTopIconBean = rightTopIconBean;
        this.mDragIconBean = dragIconBean;
        this.mLeftBottomIconBean = leftBottomIconBean;

        this.mSidePoint = sidePoint;
        this.mSidePaint.setColor(sideColor);
        this.mSidePaint.setStrokeWidth(sideWidth);
    }

    public void drawSticker(Canvas canvas, StickerBean stickerBean, Bitmap bitmap) {
        drawSticker(canvas, stickerBean.getMatrix(), stickerBean, bitmap);
    }

    //绘制贴纸
    public void drawSticker(Canvas canvas, Matrix matrix, StickerBean stickerBean, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        canvas.drawBitmap(bitmap, matrix, mPaint);
    }

    public void drawTextSticker(Canvas canvas, TextStickerBean textStickerBean, Bitmap bitmap, int frame) {
        drawTextSticker(canvas, textStickerBean.getMatrix(), textStickerBean, bitmap, frame);
    }

    //绘制文字贴纸
    public void drawTextSticker(Canvas canvas, Matrix matrix, TextStickerBean textStickerBean, Bitmap bitmap, int frame) {
        drawSticker(canvas, matrix, textStickerBean, bitmap);

        if (frame < textStickerBean.getDelayTime()) {
            return;
        }

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

        calculateTextSticker(textStickerBean, mTextPoint, staticLayout.getHeight());//这里需要重新计算文字位置

        canvas.save();

        canvas.concat(matrix);
        canvas.translate(mTextPoint[0], mTextPoint[1]);

        staticLayout.draw(canvas);

        canvas.restore();
    }

    //绘制图标
    public void drawSelected(Canvas canvas, StickerBean stickerBean) {
        if (TextUtils.isEmpty(stickerBean.getIndex())) {//如果没有背景则不绘制选择框
            return;
        }

        drawSide(canvas);

        if (mLeftTopIconBean != null) {
            canvas.drawBitmap(mLeftTopIconBean.getBitmap(), mLeftTopIconBean.getMatrix(), mPaint);
        }
        if (mRightTopIconBean != null) {
            canvas.drawBitmap(mRightTopIconBean.getBitmap(), mRightTopIconBean.getMatrix(), mPaint);
        }
        canvas.drawBitmap(mDragIconBean.getBitmap(), mDragIconBean.getMatrix(), mPaint);
        if (mLeftBottomIconBean != null) {
            canvas.drawBitmap(mLeftBottomIconBean.getBitmap(), mLeftBottomIconBean.getMatrix(), mPaint);
        }
    }

    //绘制边框
    private void drawSide(Canvas canvas) {
        canvas.drawLine(mSidePoint[0], mSidePoint[1],
                mSidePoint[2], mSidePoint[3], mSidePaint);
        canvas.drawLine(mSidePoint[2], mSidePoint[3],
                mSidePoint[6], mSidePoint[7], mSidePaint);
        canvas.drawLine(mSidePoint[6], mSidePoint[7],
                mSidePoint[4], mSidePoint[5], mSidePaint);
        canvas.drawLine(mSidePoint[4], mSidePoint[5],
                mSidePoint[0], mSidePoint[1], mSidePaint);
    }
}
