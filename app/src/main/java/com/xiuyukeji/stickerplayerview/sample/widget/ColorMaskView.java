package com.xiuyukeji.stickerplayerview.sample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiuyukeji.stickerplayerview.utils.StickerUtil;

/**
 * 颜色遮挡view
 *
 * @author Created by jz on 2017/5/15 14:05
 */
public class ColorMaskView extends View {

    private final Paint mPaint;
    private final RectF mRect;

    private int mMaskPadding;

    public ColorMaskView(Context context) {
        this(context, null);
    }

    public ColorMaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(StickerUtil.PAINT_FLAG);
        mRect = new RectF();

        mMaskPadding = StickerUtil.dpToPx(getContext(), 2.5f);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRect.set(0, 0, getWidth(), getHeight());

        canvas.drawRoundRect(mRect, mMaskPadding, mMaskPadding, mPaint);
    }
}
