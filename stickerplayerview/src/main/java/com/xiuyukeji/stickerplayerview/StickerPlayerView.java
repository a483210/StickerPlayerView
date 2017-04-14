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

    private static final int DEFAULT_STICKER_MAX_NUMBER = 120;

    private int mStickerMaxNumber;
    private boolean mIsRandomLocation;

    private final StickerRenderer mStickerRenderer;
    private final StickerEvent mStickerEvent;

    private final SparseArray<ArrayList<StickerBean>> mStickersData;
    private ArrayList<StickerBean> mStickers;

    private int mFrameIndex = 0;

    private OnDeleteListener mOnDeleteListener;

    public StickerPlayerView(Context context) {
        this(context, null);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStickersData = new SparseArray<>();

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
        mStickerMaxNumber = typedArray.getInteger(R.styleable.StickerPlayerView_maxNumber,
                DEFAULT_STICKER_MAX_NUMBER);

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
        setStickers(mFrameIndex);
    }

    private void setListener() {
        mStickerEvent.setOnCopyListener(new OnCopyListener() {
            @Override
            public void onCopy(StickerBean stickerBean) {
                copySticker(stickerBean);
            }
        });
        mStickerEvent.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(StickerBean stickerBean) {
                mStickerRenderer.cutUserCount(stickerBean.getIndex());
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(stickerBean);
                }
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
        ArrayList<StickerBean> stickers = getStickers(frameIndex);

        if (isOverMaxNumber(stickers)) {
            return;
        }

        Bitmap bitmap = mStickerRenderer.getBitmapBuffer(getContext(), source, path);
        if (bitmap == null) {
            return;
        }

        StickerBean stickerBean = new StickerBean(path, bitmap.getWidth(), bitmap.getHeight());

        initStickerLocation(stickerBean);

        addSticker(stickers, frameIndex, stickerBean);
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
     * 添加文字贴纸，没有背景
     *
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@IntRange(from = 0) int frameIndex,
                               @NonNull String text, int textColor, int textSize,
                               int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        addTextSticker(frameIndex, BitmapSource.ASSETS, null,
                text, textColor, textSize,
                false, false, false,
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
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@IntRange(from = 0) int frameIndex,
                               @BitmapSource int source, String path,
                               @NonNull String text, int textColor, int textSize,
                               int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        addTextSticker(frameIndex, source, path,
                text, textColor, textSize,
                false, false, false,
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

        ArrayList<StickerBean> stickers = getStickers(frameIndex);

        if (isOverMaxNumber(stickers)) {
            return;
        }

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

        initStickerLocation(textStickerBean);

        addSticker(stickers, frameIndex, textStickerBean);
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
    public void copySticker(StickerBean stickerBean, @IntRange(from = 0) int frameIndex) {
        copySticker(stickerBean, frameIndex, 1);
    }

    /**
     * 复制贴纸
     *
     * @param stickerBean 贴纸数据
     * @param frameIndex  复制到哪一帧
     * @param count       复制从frameIndex到多少帧
     */
    public void copySticker(StickerBean stickerBean, @IntRange(from = 0) int frameIndex, @IntRange(from = 1) int count) {
        for (int i = 0; i < count; i++) {
            int index = frameIndex + i;

            ArrayList<StickerBean> stickers = getStickers(frameIndex);

            if (isOverMaxNumber(stickers)) {
                continue;
            }

            StickerBean newStickerBean = copyStickerBean(stickerBean);

            newStickerBean.setDx(newStickerBean.getDx() + 100);
            newStickerBean.setDy(newStickerBean.getDy() + 100);

            calculateSticker(newStickerBean);

            mStickerRenderer.addUserCount(newStickerBean.getIndex());

            addSticker(stickers, index, newStickerBean);
        }
    }

    /**
     * 删除当前帧的某个贴纸
     *
     * @param position 索引
     */
    public void deleteSticker(int position) {
        deleteSticker(mFrameIndex, position);
    }

    /**
     * 删除某个帧的某个贴纸
     *
     * @param frameIndex 帧
     * @param position   索引
     */
    public void deleteSticker(int frameIndex, int position) {
        if (position < 0) {
            return;
        }
        ArrayList<StickerBean> stickers = mStickersData.get(frameIndex);

        if (stickers == null || position >= stickers.size()) {
            return;
        }

        if (frameIndex == mFrameIndex) {
            mStickerEvent.delete(position);
        } else {
            StickerBean stickerBean = stickers.remove(position);
            mStickerRenderer.cutUserCount(stickerBean.getIndex());
            if (mOnDeleteListener != null) {
                mOnDeleteListener.onDelete(stickerBean);
            }
            invalidate();
        }
    }

    /**
     * 删除当前帧的贴纸
     */
    public void clearSticker() {
        clearSticker(mFrameIndex);
    }

    /**
     * 删除指定帧的贴纸
     *
     * @param frameIndex 帧
     */
    public void clearSticker(int frameIndex) {
        ArrayList<StickerBean> stickers = mStickersData.get(frameIndex);
        if (stickers == null) {
            return;
        }
        for (int i = 0; i < stickers.size(); i++) {
            if (frameIndex == mFrameIndex) {
                mStickerEvent.delete(i);
            } else {
                StickerBean stickerBean = stickers.get(i);
                mStickerRenderer.cutUserCount(stickerBean.getIndex());
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(stickerBean);
                }
            }
        }
        stickers.clear();
        invalidate();
    }

    /**
     * 删除全部贴纸
     */
    public void clearAllSticker() {
        int count = mStickersData.size();
        for (int i = 0; i < count; i++) {
            int key = mStickersData.keyAt(i);
            clearSticker(key);
        }
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
    public void setCurrentFrame(@IntRange(from = 0) int frameIndex) {
        if (frameIndex == mFrameIndex) {
            return;
        }
        this.mFrameIndex = frameIndex;

        setStickers(mFrameIndex);

        invalidate();
    }

    /**
     * 当前贴纸是否已经超过最大数量
     */
    public boolean isOverMaxNumber() {
        return isOverMaxNumber(mStickers);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStickers.isEmpty()) {
            return;
        }
        int count = mStickers.size();
        int selectedPosition = mStickerEvent.getSelectedPosition();
        for (int i = 0; i < count; i++) {
            StickerBean stickerBean = mStickers.get(i);
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

    private void initStickerLocation(StickerBean stickerBean) {
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

    private void setStickers(int frameIndex) {
        mStickers = getStickers(frameIndex);
        mStickerEvent.setStickers(mStickers);
    }

    private boolean isOverMaxNumber(ArrayList<StickerBean> stickers) {
        return stickers.size() >= mStickerMaxNumber;
    }

    private void addSticker(ArrayList<StickerBean> stickers, int frameIndex, StickerBean stickerBean) {
        stickers.add(stickerBean);
        if (frameIndex == mFrameIndex) {
            mStickerEvent.setSelectedPosition(stickers.size() - 1);
            invalidate();
        }
    }

    private ArrayList<StickerBean> getStickers(int frameIndex) {
        ArrayList<StickerBean> stickers = mStickersData.get(frameIndex);
        if (stickers == null) {
            stickers = new ArrayList<>();
            mStickersData.put(frameIndex, stickers);
        }
        return stickers;
    }

    /**
     * 删除贴纸时回调
     *
     * @param l 回调
     */
    public void setOnDeleteListener(OnDeleteListener l) {
        this.mOnDeleteListener = l;
    }

    /**
     * 单击贴纸时回调，第二次单击会和doubleClick事件互斥
     *
     * @param l 回调
     */
    public void setOnClickStickerListener(OnClickStickerListener l) {
        mStickerEvent.setOnClickStickerListener(l);
    }

    /**
     * 双击贴纸时回调，第一下会触发单击
     *
     * @param l 回调
     */
    public void setOnDoubleClickStickerListener(OnDoubleClickStickerListener l) {
        mStickerEvent.setOnDoubleClickStickerListener(l);
    }

    /**
     * 长按贴纸时回调
     *
     * @param l 回调
     */
    public void setOnLongClickStickerListener(OnLongClickStickerListener l) {
        mStickerEvent.setOnLongClickStickerListener(l);
    }

    /**
     * 选择贴纸时回调
     *
     * @param l 回调
     */
    public void setOnSelectedListener(OnSelectedListener l) {
        mStickerEvent.setOnSelectedListener(l);
    }

    /**
     * 取消下载贴纸时回调，如果选中新的贴纸，会回调旧的贴纸
     *
     * @param l 回调
     */
    public void setOnUnselectedListener(OnUnselectedListener l) {
        mStickerEvent.setOnUnselectedListener(l);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mStickerEvent.onDetached();
    }
}
