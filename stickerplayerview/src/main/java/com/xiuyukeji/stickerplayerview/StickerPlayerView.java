package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.intefaces.*;
import com.xiuyukeji.stickerplayerview.utils.BitmapSource;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.utils.BitmapSource.ASSETS;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateSticker;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.attachBackground;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.copyStickerBean;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.dpToPx;

/**
 * 贴纸
 *
 * @author Created by jz on 2017/4/11 16:52
 */
public class StickerPlayerView extends View {

    private boolean mIsRandomLocation;

    private final StickerRenderer mStickerRenderer;
    private final StickerEvent mStickerEvent;

    private final SparseArray<ArrayList<StickerBean>> mStickers;
    private ArrayList<StickerBean> mCurrentStickers;

    private int mFrameIndex = 0;

    public StickerPlayerView(Context context) {
        this(context, null);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStickers = new SparseArray<>();

        mStickerRenderer = new StickerRenderer();
        mStickerEvent = new StickerEvent(this);

        initAttrs(attrs);
        initView();
        setListener();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StickerPlayerView);
        mIsRandomLocation = typedArray.getBoolean(R.styleable.StickerPlayerView_randomLocation, false);

        int frameColor = typedArray.getColor(R.styleable.StickerPlayerView_frameColor, 0xffffffff);
        int frameWidth = (int) typedArray.getDimension(R.styleable.StickerPlayerView_frameWidth,
                dpToPx(getContext(), 1));
        int framePadding = (int) typedArray.getDimension(R.styleable.StickerPlayerView_framePadding,
                dpToPx(getContext(), 4));

        int delResId = typedArray.getResourceId(R.styleable.StickerPlayerView_delSrc,
                R.drawable.ic_close_white_18dp);
        int copyResId = typedArray.getResourceId(R.styleable.StickerPlayerView_copySrc,
                R.drawable.ic_content_copy_white_18dp);
        int dragResId = typedArray.getResourceId(R.styleable.StickerPlayerView_dragSrc,
                R.drawable.ic_zoom_out_map_white_18dp);
        int flipResId = typedArray.getResourceId(R.styleable.StickerPlayerView_flipSrc,
                R.drawable.ic_flip_white_18dp);

        Bitmap delBitmap = BitmapFactory.decodeResource(getResources(), delResId);
        Bitmap copyBitmap = BitmapFactory.decodeResource(getResources(), copyResId);
        Bitmap dragBitmap = BitmapFactory.decodeResource(getResources(), dragResId);
        Bitmap flipBitmap = BitmapFactory.decodeResource(getResources(), flipResId);

        int color = 0xfffa3d5f;
        int padding = dpToPx(getContext(), 3);

        if (delBitmap == null ||
                copyBitmap == null ||
                dragBitmap == null ||
                flipBitmap == null
                ) {
            throw new RuntimeException("图标不能为空！");
        }

        if (delResId == R.drawable.ic_close_white_18dp) {
            delBitmap = attachBackground(delBitmap, color, padding);
        }
        if (copyResId == R.drawable.ic_content_copy_white_18dp) {
            copyBitmap = attachBackground(copyBitmap, color, padding);
        }
        if (dragResId == R.drawable.ic_zoom_out_map_white_18dp) {
            dragBitmap = attachBackground(dragBitmap, color, padding);
        }
        if (flipResId == R.drawable.ic_flip_white_18dp) {
            flipBitmap = attachBackground(flipBitmap, color, padding);
        }

        mStickerRenderer.setFrameStyle(delBitmap, copyBitmap, dragBitmap, flipBitmap,
                frameColor, frameWidth);

        mStickerEvent.setIcon(new IconBean(delBitmap.getWidth(), delBitmap.getHeight()),
                new IconBean(copyBitmap.getWidth(), copyBitmap.getHeight()),
                new IconBean(dragBitmap.getWidth(), dragBitmap.getHeight()),
                new IconBean(flipBitmap.getWidth(), flipBitmap.getHeight()),
                framePadding);

        typedArray.recycle();
    }

    private void initView() {
        setCurrentStickers(mFrameIndex);
    }

    private void setListener() {
        mStickerEvent.setOnCopyListener(new OnCopyListener() {
            @Override
            public void onCopy(StickerBean stickerBean) {
                copySticker(stickerBean);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mStickerEvent.dispatchTouchEvent(event);
    }

    /**
     * 添加图片贴纸
     * 默认添加至当前帧
     * 默认地址从assets获取
     *
     * @param path 地址
     */
    public void addSticker(@NonNull String path) {
        addSticker(mFrameIndex, ASSETS, path);
    }

    /**
     * 添加图片贴纸
     * 默认地址从assets获取
     *
     * @param path 地址
     */
    public void addSticker(@IntRange(from = 0) int frameIndex,
                           @NonNull String path) {
        addSticker(frameIndex, ASSETS, path);
    }

    /**
     * 添加贴纸
     *
     * @param source 来源
     * @param path   地址
     */
    public void addSticker(@IntRange(from = 0) int frameIndex,
                           @BitmapSource int source, @NonNull String path) {
        Bitmap bitmap = mStickerRenderer.getBitmapBuffer(getContext(), source, path);
        if (bitmap == null) {
            return;
        }

        StickerBean stickerBean = new StickerBean(path, bitmap.getWidth(), bitmap.getHeight());

        setStickerLocation(stickerBean);

        getStickers(frameIndex).add(stickerBean);
        invalidate();
    }

    /**
     * 添加文字贴纸，没有背景
     *
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param isBold        是否加粗
     * @param isItalic      是否倾斜
     * @param isUnderline   是否有下划线
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@IntRange(from = 0) int frameIndex,
                               @NonNull String text, int textColor, int textSize,
                               boolean isBold, boolean isItalic, boolean isUnderline,
                               int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        addTextSticker(frameIndex, BitmapSource.ASSETS, null,
                text, textColor, textSize,
                isBold, isItalic, isUnderline,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 添加文字贴纸
     *
     * @param source        来源
     * @param path          地址
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param isBold        是否加粗
     * @param isItalic      是否倾斜
     * @param isUnderline   是否有下划线
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@IntRange(from = 0) int frameIndex,
                               @BitmapSource int source, String path,
                               @NonNull String text, int textColor, int textSize,
                               boolean isBold, boolean isItalic, boolean isUnderline,
                               int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        TextStickerBean textStickerBean;

        if (TextUtils.isEmpty(path)) {//这里的width和height需要调整
            textStickerBean = new TextStickerBean(getWidth(), getHeight(),
                    text, textColor, textSize,
                    isBold, isItalic, isUnderline,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        } else {
            Bitmap bitmap = mStickerRenderer.getBitmapBuffer(getContext(), source, path);
            if (bitmap == null) {
                return;
            }
            textStickerBean = new TextStickerBean(path, bitmap.getWidth(), bitmap.getHeight(),
                    text, textColor, textSize,
                    isBold, isItalic, isUnderline,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        }

        setStickerLocation(textStickerBean);

        ArrayList<StickerBean> stickers = getStickers(frameIndex);
        stickers.add(textStickerBean);

        mStickerEvent.setSelectedPosition(stickers.size() - 1);

        invalidate();
    }

    /**
     * 复制贴纸
     *
     * @param stickerBean 贴纸数据
     */
    public void copySticker(StickerBean stickerBean) {
        copySticker(stickerBean, mFrameIndex, 1);
    }

    /**
     * 复制贴纸
     *
     * @param stickerBean 贴纸数据
     * @param frameIndex  复制到哪一帧
     */
    public void copySticker(StickerBean stickerBean, int frameIndex) {
        copySticker(stickerBean, frameIndex, 1);
    }

    /**
     * 复制贴纸
     *
     * @param stickerBean 贴纸数据
     * @param frameIndex  复制到哪一帧
     * @param count       复制从frameIndex到多少帧
     */
    public void copySticker(StickerBean stickerBean, int frameIndex, @IntRange(from = 1) int count) {
        for (int i = 0; i < count; i++) {
            int index = frameIndex + i;
            ArrayList<StickerBean> stickers = getStickers(index);

            StickerBean newStickerBean = copyStickerBean(stickerBean);

            newStickerBean.setDx(newStickerBean.getDx() + 100);
            newStickerBean.setDy(newStickerBean.getDy() + 100);

            calculateSticker(newStickerBean);

            stickers.add(newStickerBean);
        }
        invalidate();
    }

    /**
     * 获取当前帧
     */
    public int getCurrentFrame() {
        return mFrameIndex;
    }

    /**
     * 设置当前帧，这个帧可以随便设置
     *
     * @param frameIndex 帧
     */
    public void setCurrentFrame(int frameIndex) {
        if (mFrameIndex == frameIndex) {
            return;
        }
        this.mFrameIndex = frameIndex;

        setCurrentStickers(mFrameIndex);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrentStickers.isEmpty()) {
            return;
        }
        int count = mCurrentStickers.size();
        int selectedPosition = mStickerEvent.getSelectedPosition();
        for (int i = 0; i < count; i++) {
            StickerBean stickerBean = mCurrentStickers.get(i);
            if (stickerBean instanceof TextStickerBean) {
                mStickerRenderer.drawTextSticker(canvas, (TextStickerBean) stickerBean);
            } else {
                mStickerRenderer.drawSticker(canvas, stickerBean);
            }
            if (i == selectedPosition) {
                mStickerRenderer.drawSelected(canvas, mStickerEvent.getFramePoint(),
                        mStickerEvent.getDelMatrix(),
                        mStickerEvent.getCopyMatrix(),
                        mStickerEvent.getDragMatrix(),
                        mStickerEvent.getFlipMatrix(),
                        stickerBean);
            }
        }
    }

    private void setStickerLocation(StickerBean stickerBean) {
        int distanceWidth = getWidth() - stickerBean.getWidth();
        int distanceHeight = getHeight() - stickerBean.getHeight();

        if (mIsRandomLocation) {
            stickerBean.setDx((int) (Math.random() * distanceWidth));
            stickerBean.setDy((int) (Math.random() * distanceHeight));
        } else {
            stickerBean.setDx(distanceWidth / 2);
            stickerBean.setDy(distanceHeight / 2);
        }

        calculateSticker(stickerBean);
    }

    private void setCurrentStickers(int frameIndex) {
        mCurrentStickers = getStickers(frameIndex);
        mStickerEvent.setStickers(mCurrentStickers);
    }

    private ArrayList<StickerBean> getStickers(int frameIndex) {
        ArrayList<StickerBean> stickers = mStickers.get(frameIndex);
        if (stickers == null) {
            stickers = new ArrayList<>();
            mStickers.put(frameIndex, stickers);
        }
        return stickers;
    }

    public void setOnDeleteListener(OnDeleteListener l) {
        mStickerEvent.setOnDeleteListener(l);
    }

    public void setClickListener(com.xiuyukeji.stickerplayerview.intefaces.OnClickListener l) {
        mStickerEvent.setOnClickListener(l);
    }

    public void setDoubleClickListener(OnDoubleClickListener l) {
        mStickerEvent.setOnDoubleClickListener(l);
    }

    public void setLongClickListener(com.xiuyukeji.stickerplayerview.intefaces.OnLongClickListener l) {
        mStickerEvent.setOnLongClickListener(l);
    }

    public void setOnSelectedListener(OnSelectedListener l) {
        mStickerEvent.setOnSelectedListener(l);
    }

    public void setOnUnselectedListener(OnUnselectedListener l) {
        mStickerEvent.setOnUnselectedListener(l);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mStickerEvent.onDetached();
    }
}
