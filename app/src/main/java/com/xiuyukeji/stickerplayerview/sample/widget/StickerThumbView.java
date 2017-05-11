package com.xiuyukeji.stickerplayerview.sample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 贴纸缩略图显示
 *
 * @author Created by jz on 2017/5/15 15:26
 */
public class StickerThumbView extends View {

    private int mPosition;
    private OnDrawThumbListener mOnDrawThumbListener;

    public StickerThumbView(Context context) {
        this(context, null);
    }

    public StickerThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerThumbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnDrawThumbListener != null)
            mOnDrawThumbListener.onDrawThumb(canvas, mPosition);
    }

    public void setOnDrawThumbListener(OnDrawThumbListener l) {
        this.mOnDrawThumbListener = l;
    }

    public interface OnDrawThumbListener {
        void onDrawThumb(Canvas canvas, int position);
    }
}
