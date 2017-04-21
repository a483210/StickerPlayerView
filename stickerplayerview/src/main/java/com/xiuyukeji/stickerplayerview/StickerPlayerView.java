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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiuyukeji.stickerplayerview.annotations.FrameRange;
import com.xiuyukeji.stickerplayerview.annotations.PaddingRange;
import com.xiuyukeji.stickerplayerview.annotations.TextSizeRange;
import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.cache.MemoryCache;
import com.xiuyukeji.stickerplayerview.event.StickerEvent;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnCopyListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnSelectedListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnUnselectedListener;
import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.ResourceHandle;

import java.util.ArrayList;

import static com.xiuyukeji.stickerplayerview.event.StickerEvent.STATE_NORMAL;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateSticker;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.checkDynamicAndFrameRate;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.checkFrameRateNull;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.checkFrameRateRange;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.copyStickerBean;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.searchStickerLocation;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.attachBackground;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.dpToPx;

/**
 * 贴纸
 *
 * @author Created by jz on 2017/4/11 16:52
 */
public class StickerPlayerView extends View {

    public static final String TAG = "StickerPlayerView";

    private static final int EDIT = 0, PLAYER = 1;

    private boolean mIsRandomLocation;

    private final ResourceHandle mResourceHandle;
    private final StickerRenderer mStickerRenderer;
    private final StickerEvent mStickerEvent;

    private final ArrayList<StickerBean> mStickers;

    private int mFrameIndex = 0;//当前帧
    private long mFrameTimeMs = 0;//当前时间
    private float mDelayTimeMs = 0;//帧间隔时间

    private int mState = EDIT;

    private OnDeleteListener mOnDeleteListener;

    public StickerPlayerView(Context context) {
        this(context, null);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStickers = new ArrayList<>();

        mResourceHandle = new ResourceHandle();
        mStickerRenderer = new StickerRenderer();
        mStickerEvent = new StickerEvent(this, mStickers);

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

        if (delBitmap == null
                || copyBitmap == null
                || dragBitmap == null
                || flipBitmap == null) {
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

        int sideColor = typedArray.getColor(R.styleable.StickerPlayerView_sideColor, 0xffffffff);
        int sideWidth = (int) typedArray.getDimension(R.styleable.StickerPlayerView_sideWidth,
                dpToPx(getContext(), 1));
        int sidePadding = (int) typedArray.getDimension(R.styleable.StickerPlayerView_sidePadding,
                dpToPx(getContext(), 4));

        IconBean delIconBean = new IconBean(delBitmap);
        IconBean copyIconBean = new IconBean(copyBitmap);
        IconBean dragIconBean = new IconBean(dragBitmap);
        IconBean flipIconBean = new IconBean(flipBitmap);
        float[] sidePoint = new float[8];

        mStickerRenderer.setSelectedStyle(delIconBean, copyIconBean, dragIconBean, flipIconBean,
                sidePoint, sideColor, sideWidth);

        mStickerEvent.setIcon(delIconBean, copyIconBean, dragIconBean, flipIconBean,
                sidePoint, sidePadding);

        mIsRandomLocation = typedArray.getBoolean(R.styleable.StickerPlayerView_randomLocation, false);

        mDelayTimeMs = typedArray.getInteger(R.styleable.StickerPlayerView_frameRate, 0);
        if (mDelayTimeMs != 0) {
            mDelayTimeMs = 1000 / mDelayTimeMs;
        }

        typedArray.recycle();
    }

    private void initView() {
    }

    private void setListener() {
        mStickerEvent.setOnCopyListener(new OnCopyListener() {
            @Override
            public void onCopy(StickerBean stickerBean) {
                copySticker(stickerBean, stickerBean.getFromFrame(), stickerBean.getToFrame());
            }
        });
        mStickerEvent.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(StickerBean stickerBean) {
                mResourceHandle.decrementUseCount(stickerBean.getIndex());
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
     * 添加贴纸从from到to帧
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     * @param resource  资源
     */
    public void addSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                           @NonNull Resource resource) {

        resource = mResourceHandle.initResource(resource);
        if (resource == null) {
            return;
        }

        checkDynamicAndFrameRate(resource, 1000 / mDelayTimeMs);

        StickerBean stickerBean = new StickerBean(resource.getIndex(),
                fromFrame, toFrame,
                resource.getWidth(), resource.getHeight());

        initStickerLocation(stickerBean);

        addSticker(stickerBean);
    }

    /**
     * 添加文字贴纸从from到to帧，没有背景
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     * @param text      文字
     * @param textColor 文字颜色
     * @param textSize  文字大小
     */
    public void addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                               String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        addTextSticker(fromFrame, toFrame, null,
                text, textColor, textSize);
    }

    /**
     * 添加文字贴纸从from到to帧，没有背景
     *
     * @param fromFrame     开始帧
     * @param toFrame       结束帧
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                               String text, @ColorInt int textColor, @TextSizeRange int textSize,
                               @PaddingRange int leftPadding,
                               @PaddingRange int topPadding,
                               @PaddingRange int rightPadding,
                               @PaddingRange int bottomPadding) {
        addTextSticker(fromFrame, toFrame, null,
                text, textColor, textSize,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 添加文字贴纸从from到to帧
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     * @param resource  资源
     * @param text      文字
     * @param textColor 文字颜色
     * @param textSize  文字大小
     */
    public void addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame, Resource resource,
                               String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        addTextSticker(fromFrame, toFrame, resource,
                text, textColor, textSize,
                0, 0, 0, 0);
    }

    /**
     * 添加文字贴纸从from到to帧
     *
     * @param fromFrame     开始帧
     * @param toFrame       结束帧
     * @param resource      资源
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                               Resource resource,
                               String text, @ColorInt int textColor, @TextSizeRange int textSize,
                               @PaddingRange int leftPadding,
                               @PaddingRange int topPadding,
                               @PaddingRange int rightPadding,
                               @PaddingRange int bottomPadding) {
        TextStickerBean textStickerBean;

        if (resource == null) {//todo 这里的width和height需要调整
            textStickerBean = new TextStickerBean(null, getWidth(), getHeight(),
                    fromFrame, toFrame,
                    text, textColor, textSize,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        } else {
            resource = mResourceHandle.initResource(resource);
            if (resource == null) {
                return;
            }

            checkDynamicAndFrameRate(resource, 1000 / mDelayTimeMs);

            textStickerBean = new TextStickerBean(resource.getIndex(),
                    resource.getWidth(), resource.getHeight(),
                    fromFrame, toFrame,
                    text, textColor, textSize,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        }

        initStickerLocation(textStickerBean);

        addSticker(textStickerBean);
    }

    /**
     * 替换当前选中贴纸的背景
     *
     * @param resource 资源
     */
    public void replaceSticker(Resource resource) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        replaceSticker(resource, mStickerEvent.getSelectedPosition());
    }

    /**
     * 替换指定贴纸的背景
     *
     * @param resource 资源
     * @param position 索引
     */
    public void replaceSticker(Resource resource, @FrameRange int position) {
        replaceSticker(getSticker(position), resource, position,
                -1, -1, -1, -1);
    }

    /**
     * 替换当前选中文字贴纸背景
     *
     * @param resource      资源
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(Resource resource,
                                   @PaddingRange int leftPadding,
                                   @PaddingRange int topPadding,
                                   @PaddingRange int rightPadding,
                                   @PaddingRange int bottomPadding) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        replaceTextSticker(resource, mStickerEvent.getSelectedPosition(),
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 替换当前帧指定文字贴纸的背景
     *
     * @param resource      资源
     * @param position      索引
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(Resource resource, @FrameRange int position,
                                   @PaddingRange int leftPadding,
                                   @PaddingRange int topPadding,
                                   @PaddingRange int rightPadding,
                                   @PaddingRange int bottomPadding) {
        replaceSticker(getTextSticker(position), resource, position,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    //替换贴纸背景
    private void replaceSticker(StickerBean stickerBean, Resource resource, int position,
                                int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        if (stickerBean == null) {
            return;
        }

        String index = stickerBean.getIndex();

        if (index == null && resource == null
                || index != null && resource != null && index.equals(resource.getIndex())) {//如果图像是相同的则跳出
            return;
        }

        mResourceHandle.decrementUseCount(index);

        StickerBean newStickerBean;

        if (resource == null) {//todo 需要重新计算大小
            newStickerBean = copyStickerBean(stickerBean, null, getWidth(), getHeight());
        } else {
            resource = mResourceHandle.initResource(resource);
            if (resource == null) {
                return;
            }

            newStickerBean = copyStickerBean(stickerBean, resource.getIndex(),
                    resource.getWidth(), resource.getHeight());
        }
        if (leftPadding != -1
                && stickerBean instanceof TextStickerBean) {
            TextStickerBean textStickerBean = (TextStickerBean) newStickerBean;
            textStickerBean.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        }

        calculateSticker(newStickerBean);

        mStickers.set(position, newStickerBean);

        mStickerEvent.updateSelected(position);

        invalidate(newStickerBean);
    }

    /**
     * 复制指定贴纸到当前帧
     *
     * @param position 索引
     */
    public void copySticker(@FrameRange int position) {
        copySticker(position, mFrameIndex, mFrameIndex);
    }

    /**
     * 复制当前选中的贴纸，从fromFrame到toFrame
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameRange int fromFrame, @FrameRange int toFrame) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        copySticker(mStickerEvent.getSelectedPosition(), fromFrame, toFrame);
    }

    /**
     * 复制指定贴纸，从fromFrame到toFrame
     *
     * @param position  索引
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameRange int position,
                            @FrameRange int fromFrame, @FrameRange int toFrame) {

        if (position >= mStickers.size()
                || toFrame > fromFrame) {
            return;
        }

        copySticker(mStickers.get(position), fromFrame, toFrame);
    }

    //复制贴纸
    private void copySticker(StickerBean stickerBean, int fromFrame, int toFrame) {
        StickerBean newStickerBean = copyStickerBean(stickerBean, fromFrame, toFrame);

        calculateSticker(newStickerBean);

        mResourceHandle.addedUseCount(newStickerBean.getIndex());

        addSticker(newStickerBean);
    }

    /**
     * 删除当前选中的贴纸
     */
    public void deleteSticker() {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        deleteSticker(mStickerEvent.getSelectedPosition());
    }

    /**
     * 删除指定贴纸
     *
     * @param position 索引
     */
    public void deleteSticker(@FrameRange int position) {
        if (position >= mStickers.size()) {
            return;
        }
        mStickerEvent.delete(position);
    }

    /**
     * 删除全部贴纸
     */
    public void clearAllSticker() {
        int count = mStickers.size();
        for (int i = 0; i < count; i++) {
            mStickerEvent.delete(0);
        }
        invalidate();
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
     * 设置指定贴纸的文字
     *
     * @param text     文字
     * @param position 索引
     */
    public void setText(String text, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setText(text);
        invalidate(textStickerBean);
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
     * 设置指定贴纸的文字颜色
     *
     * @param color    文字颜色
     * @param position 索引
     */
    public void setTextColor(@ColorInt int color, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setTextColor(color);
        invalidate(textStickerBean);
    }

    /**
     * 设置当前选中贴纸的文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(@TextSizeRange int size) {
        if (mStickerEvent.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setTextSize(size, mStickerEvent.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字大小
     *
     * @param size     文字大小
     * @param position 索引
     */
    public void setTextSize(@TextSizeRange int size, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setTextSize(size);
        invalidate(textStickerBean);
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
     * 设置指定贴纸的文字加粗
     *
     * @param isBold   文字加粗
     * @param position 索引
     */
    public void setBold(boolean isBold, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setBold(isBold);
        invalidate(textStickerBean);
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
     * 设置指定贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     * @param position 索引
     */
    public void setItalic(boolean isItalic, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setItalic(isItalic);
        invalidate(textStickerBean);
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
     * 设置指定贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     * @param position    索引
     */
    public void setUnderline(boolean isUnderline, @FrameRange int position) {
        TextStickerBean textStickerBean = getTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        textStickerBean.setUnderline(isUnderline);
        invalidate(textStickerBean);
    }

    /**
     * 获取当前帧
     */
    public int getCurrentFrame() {
        return mFrameIndex;
    }

    /**
     * 设置当前时间，最终会被转化为帧
     *
     * @param timeMs 时间，单位毫秒
     */
    public void setCurrentTime(@IntRange(from = 0) int timeMs) {
        checkFrameRateNull(1000 / mDelayTimeMs);
        checkFrameRateRange(1000 / mDelayTimeMs);

        setCurrentFrame(Math.round(timeMs / mDelayTimeMs));
    }

    /**
     * 设置当前帧
     *
     * @param frameIndex 帧
     */
    public void setCurrentFrame(@FrameRange int frameIndex) {
        if (frameIndex == mFrameIndex) {
            return;
        }
        this.mFrameIndex = frameIndex;

        invalidate();
    }

    /**
     * 设置帧率(fps)，区间{1-60}
     *
     * @param frameRate 帧率
     */
    public void setFrameRate(@IntRange(from = 1, to = 60) int frameRate) {
        checkFrameRateRange(frameRate);

        this.mDelayTimeMs = 1000 / (float) frameRate;
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mStickerEvent.setEnabled(enabled);
    }

    /**
     * 设置内存管理，必须在没有添加贴纸之前设置
     *
     * @param memoryCache 内存管理
     */
    public void setMemoryCache(MemoryCache memoryCache) {
        mResourceHandle.setMemoryCache(memoryCache);
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

            if (mFrameIndex < stickerBean.getFromFrame()) {
                continue;
            }
            if (mFrameIndex > stickerBean.getToFrame()) {
                break;
            }

            Bitmap bitmap = mResourceHandle.getBitmap(stickerBean.getIndex(), mFrameIndex);

            if (stickerBean instanceof TextStickerBean) {
                mStickerRenderer.drawTextSticker(canvas, (TextStickerBean) stickerBean, bitmap);
            } else {
                mStickerRenderer.drawSticker(canvas, stickerBean, bitmap);
            }

            if (mState == EDIT//编辑模式才显示边框
                    && i == selectedPosition) {
                mStickerRenderer.drawSelected(canvas, stickerBean);
            }
        }
    }

    //如果在当前帧内则刷新
    private void invalidate(StickerBean stickerBean) {
        if (isFrameRange(stickerBean)) {
            invalidate();
        }
    }

    private boolean isFrameRange(StickerBean stickerBean) {
        return mFrameIndex >= stickerBean.getFromFrame()
                && mFrameIndex <= stickerBean.getToFrame();
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

    //添加贴纸
    private void addSticker(StickerBean stickerBean) {
        int position = searchStickerLocation(mStickers, stickerBean.getFromFrame());
        mStickers.add(position, stickerBean);

        if (isFrameRange(stickerBean)) {
            mStickerEvent.selectPosition(position);
            invalidate();
        }
    }

    //获得文字贴纸
    private TextStickerBean getTextSticker(int position) {
        StickerBean stickerBean = getSticker(position);
        if (stickerBean instanceof TextStickerBean) {
            return (TextStickerBean) stickerBean;
        } else {
            return null;
        }
    }

    //获得贴纸
    private StickerBean getSticker(int position) {
        if (position < 0 || position >= mStickers.size()) {
            return null;
        }
        return mStickers.get(position);
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
