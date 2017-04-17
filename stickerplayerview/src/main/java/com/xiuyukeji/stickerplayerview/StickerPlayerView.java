package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
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
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnCopyListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnSelectedListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnUnselectedListener;
import com.xiuyukeji.stickerplayerview.utils.BitmapSource;
import com.xiuyukeji.stickerplayerview.utils.FrameSource;
import com.xiuyukeji.stickerplayerview.utils.PaddingSource;
import com.xiuyukeji.stickerplayerview.utils.TextSizeSource;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.StickerEvent.STATE_NORMAL;
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

    //初始化属性
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
                copySticker(stickerBean, mFrameIndex, mFrameIndex + 1);
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
     * 添加图片贴纸到当前帧
     * 默认地址从assets获取
     *
     * @param path 地址
     */
    public void addSticker(@NonNull String path) {
        addSticker(mFrameIndex, ASSETS, path);
    }

    /**
     * 添加图片贴纸到指定帧
     * 默认地址从assets获取
     *
     * @param frameIndex 帧
     * @param path       地址
     */
    public void addSticker(@FrameSource int frameIndex,
                           @NonNull String path) {
        addSticker(frameIndex, ASSETS, path);
    }

    /**
     * 添加贴纸到指定帧
     *
     * @param frameIndex 帧
     * @param source     来源
     * @param path       地址
     */
    public void addSticker(@FrameSource int frameIndex,
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
     * 添加文字贴纸到指定帧，没有背景
     *
     * @param frameIndex 帧
     * @param text       文字
     * @param textColor  文字颜色
     * @param textSize   文字大小
     */
    public void addTextSticker(@FrameSource int frameIndex,
                               String text, @ColorInt int textColor, @TextSizeSource int textSize) {
        addTextSticker(frameIndex, BitmapSource.ASSETS, null,
                text, textColor, textSize);
    }

    /**
     * 添加文字贴纸到指定帧，没有背景
     *
     * @param frameIndex    帧
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@FrameSource int frameIndex,
                               String text, @ColorInt int textColor, @TextSizeSource int textSize,
                               @PaddingSource int leftPadding,
                               @PaddingSource int topPadding,
                               @PaddingSource int rightPadding,
                               @PaddingSource int bottomPadding) {
        addTextSticker(frameIndex, BitmapSource.ASSETS, null,
                text, textColor, textSize,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 添加文字贴纸到指定帧
     *
     * @param frameIndex 帧
     * @param source     来源
     * @param path       地址
     * @param text       文字
     * @param textColor  文字颜色
     * @param textSize   文字大小
     */
    public void addTextSticker(@FrameSource int frameIndex,
                               @BitmapSource int source, String path,
                               String text, @ColorInt int textColor, @TextSizeSource int textSize) {
        addTextSticker(frameIndex, source, path,
                text, textColor, textSize,
                0, 0, 0, 0);
    }

    /**
     * 添加文字贴纸到指定帧
     *
     * @param frameIndex    帧
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
    public void addTextSticker(@FrameSource int frameIndex,
                               @BitmapSource int source, String path,
                               String text, @ColorInt int textColor, @TextSizeSource int textSize,
                               @PaddingSource int leftPadding,
                               @PaddingSource int topPadding,
                               @PaddingSource int rightPadding,
                               @PaddingSource int bottomPadding) {

        ArrayList<StickerBean> stickers = getStickers(frameIndex);

        if (isOverMaxNumber(stickers)) {
            return;
        }

        TextStickerBean textStickerBean;

        if (TextUtils.isEmpty(path)) {//这里的width和height需要调整
            textStickerBean = new TextStickerBean(null, getWidth(), getHeight(),
                    text, textColor, textSize,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        } else {
            Bitmap bitmap = mStickerRenderer.getBitmapBuffer(getContext(), source, path);
            if (bitmap == null) {
                return;
            }
            textStickerBean = new TextStickerBean(path, bitmap.getWidth(), bitmap.getHeight(),
                    text, textColor, textSize,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        }

        initStickerLocation(textStickerBean);

        addSticker(stickers, frameIndex, textStickerBean);
    }

    /**
     * 替换当前选中贴纸的背景
     *
     * @param source 来源
     * @param path   地址
     */
    public void replaceSticker(@BitmapSource int source, String path) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        replaceSticker(source, path, mStickerEvent.getSelectedPosition());
    }

    /**
     * 替换当前帧指定贴纸的背景
     *
     * @param source   来源
     * @param path     地址
     * @param position 索引
     */
    public void replaceSticker(@BitmapSource int source, String path, @FrameSource int position) {
        replaceSticker(source, path, mFrameIndex, position);
    }

    /**
     * 替换指定帧指定贴纸的背景
     *
     * @param source     来源
     * @param path       地址
     * @param frameIndex 帧
     * @param position   索引
     */
    public void replaceSticker(@BitmapSource int source, String path,
                               @FrameSource int frameIndex, @FrameSource int position) {
        replaceSticker(getSticker(frameIndex, position),
                source, path,
                frameIndex, position,
                -1, -1, -1, -1);
    }

    /**
     * 替换当前选中文字贴纸背景
     *
     * @param source        来源
     * @param path          地址
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(@BitmapSource int source, String path,
                                   @PaddingSource int leftPadding,
                                   @PaddingSource int topPadding,
                                   @PaddingSource int rightPadding,
                                   @PaddingSource int bottomPadding) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        replaceTextSticker(source, path,
                mStickerEvent.getSelectedPosition(),
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 替换当前帧指定文字贴纸的背景
     *
     * @param source        来源
     * @param path          地址
     * @param position      索引
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(@BitmapSource int source, String path, @FrameSource int position,
                                   @PaddingSource int leftPadding,
                                   @PaddingSource int topPadding,
                                   @PaddingSource int rightPadding,
                                   @PaddingSource int bottomPadding) {
        replaceTextSticker(source, path,
                mFrameIndex, position,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 替换指定帧指定文字贴纸的背景
     *
     * @param source        来源
     * @param path          地址
     * @param frameIndex    帧
     * @param position      索引
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(@BitmapSource int source, String path,
                                   @FrameSource int frameIndex, @FrameSource int position,
                                   @PaddingSource int leftPadding,
                                   @PaddingSource int topPadding,
                                   @PaddingSource int rightPadding,
                                   @PaddingSource int bottomPadding) {
        replaceSticker(getTextSticker(frameIndex, position),
                source, path,
                frameIndex, position,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    //替换贴纸背景
    private void replaceSticker(StickerBean stickerBean,
                                int source, String path,
                                int frameIndex, int position,
                                int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        if (stickerBean == null) {
            return;
        }

        String index = stickerBean.getIndex();

        if (index == null && path == null
                || index != null && index.equals(path)) {
            return;
        }

        mStickerRenderer.cutUserCount(index);

        StickerBean newStickerBean;

        if (TextUtils.isEmpty(path)) {
            newStickerBean = copyStickerBean(stickerBean, null, null);
        } else {
            Bitmap bitmap = mStickerRenderer.getBitmapBuffer(getContext(), source, path);
            if (bitmap == null) {
                return;
            }

            newStickerBean = copyStickerBean(stickerBean, path, bitmap);
        }
        if (leftPadding != -1
                && stickerBean instanceof TextStickerBean) {
            TextStickerBean textStickerBean = (TextStickerBean) newStickerBean;
            textStickerBean.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        }

        calculateSticker(newStickerBean);

        mStickers.set(position, newStickerBean);

        mStickerEvent.updateSelected();

        invalidate(frameIndex);
    }

    /**
     * 复制当前选中的贴纸到当前帧
     */
    public void copySticker() {
        copySticker(mFrameIndex, mFrameIndex + 1);
    }

    /**
     * 复制当前选中的贴纸，从form到to
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameSource int fromFrame, @FrameSource int toFrame) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        copySticker(mStickerEvent.getSelectedPosition(), fromFrame, toFrame);
    }

    /**
     * 复制当前帧的指定贴纸，从form到to
     *
     * @param position  索引
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameSource int position,
                            @FrameSource int fromFrame, @FrameSource int toFrame) {
        copySticker(mFrameIndex, position, fromFrame, toFrame);
    }

    /**
     * 复制指定帧的指定贴纸，从form到to
     *
     * @param frameIndex 帧
     * @param position   索引
     * @param fromFrame  开始帧
     * @param toFrame    结束帧
     */
    public void copySticker(@FrameSource int frameIndex, @FrameSource int position,
                            @FrameSource int fromFrame, @FrameSource int toFrame) {

        ArrayList<StickerBean> stickers = mStickersData.get(frameIndex);

        if (stickers == null || position >= stickers.size()) {
            return;
        }

        if (toFrame - fromFrame <= 0) {
            return;
        }

        copySticker(stickers.get(position), fromFrame, toFrame);
    }

    //复制贴纸
    private void copySticker(StickerBean stickerBean, int fromFrame, int toFrame) {
        int count = toFrame - fromFrame;
        for (int i = 0; i < count; i++) {
            int index = fromFrame + i;

            ArrayList<StickerBean> stickers = getStickers(index);

            if (isOverMaxNumber(stickers)) {
                continue;
            }

            StickerBean newStickerBean = copyStickerBean(stickerBean);

            calculateSticker(newStickerBean);

            mStickerRenderer.addUserCount(newStickerBean.getIndex());

            addSticker(stickers, index, newStickerBean);
        }
    }

    /**
     * 删除当前选择的贴纸
     */
    public void deleteSticker() {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        deleteSticker(mFrameIndex, mStickerEvent.getSelectedPosition());
    }

    /**
     * 删除当前帧的指定贴纸
     *
     * @param position 索引
     */
    public void deleteSticker(@FrameSource int position) {
        deleteSticker(mFrameIndex, position);
    }

    /**
     * 删除指定帧的指定贴纸
     *
     * @param frameIndex 帧
     * @param position   索引
     */
    public void deleteSticker(@FrameSource int frameIndex, @FrameSource int position) {
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
    public void clearSticker(@FrameSource int frameIndex) {
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
     * 设置当前选中贴纸的文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setText(text, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字
     *
     * @param text     文字
     * @param position 索引
     */
    public void setText(String text, @FrameSource int position) {
        setText(text, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字
     *
     * @param text       文字
     * @param frameIndex 帧
     * @param position   索引
     */
    public void setText(String text, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setText(text);
        invalidate(frameIndex);
    }

    /**
     * 设置当前选中贴纸的文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(@ColorInt int color) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setTextColor(color, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字颜色
     *
     * @param color    文字颜色
     * @param position 索引
     */
    public void setTextColor(@ColorInt int color, @FrameSource int position) {
        setTextColor(color, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字颜色
     *
     * @param color      文字颜色
     * @param frameIndex 帧
     * @param position   索引
     */
    public void setTextColor(@ColorInt int color, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setTextColor(color);
        invalidate(frameIndex);
    }

    /**
     * 设置当前选中贴纸的文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(@TextSizeSource int size) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setTextSize(size, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字大小
     *
     * @param size     文字大小
     * @param position 索引
     */
    public void setTextSize(@TextSizeSource int size, @FrameSource int position) {
        setTextSize(size, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字大小
     *
     * @param size       文字大小
     * @param frameIndex 帧
     * @param position   索引
     */
    public void setTextSize(@TextSizeSource int size, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setTextSize(size);
        invalidate(frameIndex);
    }

    /**
     * 设置当前选中贴纸的文字加粗
     *
     * @param isBold 文字加粗
     */
    public void setBold(boolean isBold) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setBold(isBold, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字加粗
     *
     * @param isBold   文字加粗
     * @param position 索引
     */
    public void setBold(boolean isBold, @FrameSource int position) {
        setBold(isBold, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字加粗
     *
     * @param isBold     文字加粗
     * @param frameIndex 帧
     * @param position   索引
     */
    public void setBold(boolean isBold, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setBold(isBold);
        invalidate(frameIndex);
    }

    /**
     * 设置当前选中贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     */
    public void setItalic(boolean isItalic) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setItalic(isItalic, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     * @param position 索引
     */
    public void setItalic(boolean isItalic, @FrameSource int position) {
        setItalic(isItalic, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字倾斜
     *
     * @param isItalic   文字倾斜
     * @param frameIndex 帧
     * @param position   索引
     */
    public void setItalic(boolean isItalic, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setItalic(isItalic);
        invalidate(frameIndex);
    }

    /**
     * 设置当前选中贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     */
    public void setUnderline(boolean isUnderline) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setUnderline(isUnderline, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置当前帧的指定贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     * @param position    索引
     */
    public void setUnderline(boolean isUnderline, @FrameSource int position) {
        setUnderline(isUnderline, mFrameIndex, position);
    }

    /**
     * 设置指定帧的指定贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     * @param frameIndex  帧
     * @param position    索引
     */
    public void setUnderline(boolean isUnderline, @FrameSource int frameIndex, @FrameSource int position) {
        TextStickerBean textStickerBean = getTextSticker(frameIndex, position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setUnderline(isUnderline);
        invalidate(frameIndex);
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

    private void invalidate(int frameIndex) {
        if (mFrameIndex == frameIndex) {
            invalidate();
        }
    }

    //初始化贴图位置
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

    //设置当前贴纸数组
    private void setStickers(int frameIndex) {
        mStickers = getStickers(frameIndex);
        mStickerEvent.setStickers(mStickers);
    }

    private boolean isOverMaxNumber(ArrayList<StickerBean> stickers) {
        return isOverMaxNumber(stickers, 1);
    }

    private boolean isOverMaxNumber(ArrayList<StickerBean> stickers, int count) {
        return stickers.size() + count - 1 >= mStickerMaxNumber;
    }

    //添加贴纸
    private void addSticker(ArrayList<StickerBean> stickers, int frameIndex, StickerBean stickerBean) {
        stickers.add(stickerBean);
        if (frameIndex == mFrameIndex) {
            mStickerEvent.setSelectedPosition(stickers.size() - 1);
            invalidate();
        }
    }

    //获得文字贴纸
    private TextStickerBean getTextSticker(int frameIndex, int position) {
        StickerBean stickerBean = getSticker(frameIndex, position);
        if (stickerBean instanceof TextStickerBean) {
            return (TextStickerBean) stickerBean;
        } else {
            return null;
        }
    }

    //获得贴纸
    private StickerBean getSticker(int frameIndex, int position) {
        ArrayList<StickerBean> stickers = mStickersData.get(frameIndex);
        if (stickers == null) {
            return null;
        }
        if (position < 0 || position >= stickers.size()) {
            return null;
        }
        return stickers.get(position);
    }

    //获得贴纸数组
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
