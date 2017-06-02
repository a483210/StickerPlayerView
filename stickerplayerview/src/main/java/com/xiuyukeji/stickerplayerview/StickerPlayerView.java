package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiuyukeji.stickerplayerview.annotations.FrameRange;
import com.xiuyukeji.stickerplayerview.annotations.FrameRateRange;
import com.xiuyukeji.stickerplayerview.annotations.PaddingRange;
import com.xiuyukeji.stickerplayerview.annotations.PlayerSource;
import com.xiuyukeji.stickerplayerview.annotations.TextSizeRange;
import com.xiuyukeji.stickerplayerview.bean.BitmapFrameInfo;
import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.cache.MemoryCache;
import com.xiuyukeji.stickerplayerview.data.DataHandle;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Iterator;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Node;
import com.xiuyukeji.stickerplayerview.event.EventHandle;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnLeftBottomListener;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnLeftTopListener;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnRightTopListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnCopyListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnInvalidateListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnSelectedListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnUnselectedListener;
import com.xiuyukeji.stickerplayerview.resource.DynamicResource;
import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.ResourceHandle;
import com.xiuyukeji.stickerplayerview.utils.StickerOperate;

import java.util.ArrayList;
import java.util.List;

import static com.xiuyukeji.stickerplayerview.annotations.PlayerSource.EDIT;
import static com.xiuyukeji.stickerplayerview.event.EventHandle.STATE_NORMAL;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.calculateSticker;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.flipHorizontalMatrix;
import static com.xiuyukeji.stickerplayerview.utils.StickerCalculateUtil.flipVerticalMatrix;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkDynamicAndFrameRate;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkFrame;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkFrameRateNull;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkFrameRateRange;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkPadding;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkPosition;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkPositions;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkResourceNotNull;
import static com.xiuyukeji.stickerplayerview.utils.StickerCheck.checkTextSize;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.copyStickerBean;
import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.isFrameInside;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.attachBackground;
import static com.xiuyukeji.stickerplayerview.utils.StickerUtil.dpToPx;

/**
 * 贴纸
 *
 * @author Created by jz on 2017/4/11 16:52
 */
public class StickerPlayerView extends View {

    public static final String TAG = "StickerPlayerView";

    private boolean mIsRandomLocation;

    private final ResourceHandle mResourceHandle;
    private final DataHandle mDataHandle;

    private final RendererHandle mRendererHandle;
    private final EventHandle mEventHandle;
    private final PlayerHandle mPlayerHandle;

    private final BitmapFrameInfo mBitmapFrameInfo;

    private final Matrix mDrawMatrix;

    private int mFrameIndex = 0;//当前帧

    private int mState = EDIT;

    private OnCopyListener mOnCopyListener;
    private OnDeleteListener mOnDeleteListener;
    private OnInvalidateListener mOnInvalidateListener;

    public StickerPlayerView(Context context) {
        this(context, null, 0, R.style.SickerPlayerViewStyle);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, R.style.SickerPlayerViewStyle);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.SickerPlayerViewStyle);
    }

    public StickerPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        mResourceHandle = new ResourceHandle();
        mDataHandle = new DataHandle();

        mRendererHandle = new RendererHandle();
        mEventHandle = new EventHandle(this, mDataHandle);
        mPlayerHandle = new PlayerHandle(this);

        mBitmapFrameInfo = new BitmapFrameInfo();

        mDrawMatrix = new Matrix();

        initAttrs(attrs, defStyleAttr, defStyleRes);
        initView();
        setListener();
    }

    //初始化属性
    private void initAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.StickerPlayerView, defStyleAttr, defStyleRes);

        int leftTopResId = typedArray.getResourceId(R.styleable.StickerPlayerView_leftTopSrc, 0);
        int rightTopResId = typedArray.getResourceId(R.styleable.StickerPlayerView_rightTopSrc, 0);
        int dragResId = typedArray.getResourceId(R.styleable.StickerPlayerView_dragSrc, 0);
        int leftBottomResId = typedArray.getResourceId(R.styleable.StickerPlayerView_leftBottomSrc, 0);

        Bitmap leftTopBitmap = BitmapFactory.decodeResource(getResources(), leftTopResId);
        Bitmap rightTopBitmap = BitmapFactory.decodeResource(getResources(), rightTopResId);
        Bitmap dragBitmap = BitmapFactory.decodeResource(getResources(), dragResId);
        Bitmap leftBottomBitmap = BitmapFactory.decodeResource(getResources(), leftBottomResId);

        if (dragBitmap == null) {
            throw new StickerException("拖动图标必须存在！");
        }

        int color = 0xfffa3d5f;
        int padding = dpToPx(getContext(), 3);

        if (leftTopResId == R.drawable.ic_close_white_18dp) {
            leftTopBitmap = attachBackground(leftTopBitmap, color, padding);
        }
        if (rightTopResId == R.drawable.ic_content_copy_white_18dp) {
            rightTopBitmap = attachBackground(rightTopBitmap, color, padding);
        }
        if (dragResId == R.drawable.ic_zoom_out_map_white_18dp) {
            dragBitmap = attachBackground(dragBitmap, color, padding);
        }
        if (leftBottomResId == R.drawable.ic_flip_white_18dp) {
            leftBottomBitmap = attachBackground(leftBottomBitmap, color, padding);
        }

        int sideColor = typedArray.getColor(R.styleable.StickerPlayerView_sideColor, 0xffffffff);
        int sideWidth = (int) typedArray.getDimension(R.styleable.StickerPlayerView_sideWidth,
                dpToPx(getContext(), 1));
        int sidePadding = (int) typedArray.getDimension(R.styleable.StickerPlayerView_sidePadding,
                dpToPx(getContext(), 4));

        IconBean leftTopIconBean = null;
        if (leftTopBitmap != null) {
            leftTopIconBean = new IconBean(leftTopBitmap);
        }

        IconBean rightTopIconBean = null;
        if (rightTopBitmap != null) {
            rightTopIconBean = new IconBean(rightTopBitmap);
        }

        IconBean dragIconBean = new IconBean(dragBitmap);

        IconBean leftBottomIconBean = null;
        if (leftBottomBitmap != null) {
            leftBottomIconBean = new IconBean(leftBottomBitmap);
        }

        float[] sidePoint = new float[8];

        mRendererHandle.setSelectedStyle(leftTopIconBean, rightTopIconBean, dragIconBean, leftBottomIconBean,
                sidePoint, sideColor, sideWidth);

        mEventHandle.setIcon(leftTopIconBean, rightTopIconBean, dragIconBean, leftBottomIconBean,
                sidePoint, sidePadding);

        mIsRandomLocation = typedArray.getBoolean(R.styleable.StickerPlayerView_randomLocation, false);

        int frameRate = typedArray.getInteger(R.styleable.StickerPlayerView_frameRate, 0);
        if (frameRate != 0) {
            mPlayerHandle.setDelayTime(1000 / (double) frameRate);
        }

        typedArray.recycle();
    }

    private void initView() {
    }

    private void setListener() {
        mEventHandle.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(StickerBean stickerBean, int position) {
                mResourceHandle.decrementUseCount(stickerBean.getIndex());
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(copyStickerBean(stickerBean), position);
                }
                if (mResourceHandle.getDynamicCount() == 0) {
                    mPlayerHandle.stop();
                }
            }
        });
        mEventHandle.setOnLeftTopListener(new OnLeftTopListener() {
            @Override
            public void onLeftTop() {
                deleteSticker();
            }
        });
        mEventHandle.setOnRightTopListener(new OnRightTopListener() {
            @Override
            public void onRightTop() {
                int position = mEventHandle.getSelectedPosition();
                if (position <= STATE_NORMAL) {
                    return;
                }
                StickerBean stickerBean = mDataHandle.getSticker(position);
                copySticker(position, 20, 20, stickerBean.getFromFrame(), stickerBean.getToFrame());
            }
        });
        mEventHandle.setOnLeftBottomListener(new OnLeftBottomListener() {
            @Override
            public void onLeftBottom() {
                mEventHandle.fliHorizontalLeftBottom();
                flipHorizontal();
            }
        });
    }

    /**
     * 通过索引获取文字贴纸数据，如果不是文字贴纸将为Null
     *
     * @param position 索引
     */
    public TextStickerBean getTextSticker(int position) {
        StickerBean stickerBean = obtainSticker(position);
        if (stickerBean instanceof TextStickerBean) {
            return (TextStickerBean) StickerOperate.copyStickerBean(stickerBean);
        } else {
            return null;
        }
    }

    /**
     * 通过索引获取贴纸数据
     *
     * @param position 索引
     */
    public StickerBean getSticker(int position) {
        return StickerOperate.copyStickerBean(mDataHandle.getSticker(position));
    }

    /**
     * 添加贴纸从from到to帧
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     * @param resource  资源
     */
    public int addSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                          @NonNull Resource resource) {
        checkResourceNotNull(resource);
        checkFrame(fromFrame, toFrame);

        resource = mResourceHandle.initResource(resource);
        if (resource == null) {
            return STATE_NORMAL;
        }

        if (resource instanceof DynamicResource) {
            checkDynamicAndFrameRate(resource, mPlayerHandle.getDelayTime());
            mPlayerHandle.start();
        }

        StickerBean stickerBean = new StickerBean(resource.getIndex(),
                fromFrame, toFrame,
                resource.getWidth(), resource.getHeight());

        initStickerLocation(stickerBean);

        return addSticker(stickerBean);
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
    public int addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        return addTextSticker(fromFrame, toFrame, null,
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
    public int addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        return addTextSticker(fromFrame, toFrame, null,
                text, textColor, textSize, 0,
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
    public int addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame, Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        return addTextSticker(fromFrame, toFrame, resource,
                text, textColor, textSize, 0, 0, 0, 0, 0);
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
    public int addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                              Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        return addTextSticker(fromFrame, toFrame, resource,
                text, textColor, textSize, 0,
                leftPadding, topPadding, rightPadding, bottomPadding);
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
     * @param delayFrame    延迟帧数显示
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public int addTextSticker(@FrameRange int fromFrame, @FrameRange int toFrame,
                              Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @FrameRange int delayFrame,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        checkFrame(fromFrame, toFrame);
        checkFrame(delayFrame);
        checkTextSize(textSize);
        checkPadding(leftPadding);
        checkPadding(topPadding);
        checkPadding(rightPadding);
        checkPadding(bottomPadding);

        TextStickerBean textStickerBean;

        if (resource == null) {//todo 这里的width和height需要调整
            textStickerBean = new TextStickerBean(null, getWidth(), getHeight(),
                    fromFrame, toFrame,
                    text, textColor, textSize, 0,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        } else {
            resource = mResourceHandle.initResource(resource);
            if (resource == null) {
                return STATE_NORMAL;
            }

            if (resource instanceof DynamicResource) {
                checkDynamicAndFrameRate(resource, mPlayerHandle.getDelayTime());
                mPlayerHandle.start();
            } else {
                delayFrame = 0;
            }

            textStickerBean = new TextStickerBean(resource.getIndex(),
                    resource.getWidth(), resource.getHeight(),
                    fromFrame, toFrame,
                    text, textColor, textSize, delayFrame,
                    leftPadding, topPadding, rightPadding, bottomPadding);
        }

        initStickerLocation(textStickerBean);

        return addSticker(textStickerBean);
    }

    /**
     * 替换当前选中贴纸的背景
     *
     * @param resource 资源
     */
    public void replaceSticker(Resource resource) {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        replaceSticker(resource, mEventHandle.getSelectedPosition());
    }

    /**
     * 替换指定贴纸的背景
     *
     * @param resource 资源
     * @param position 索引
     */
    public void replaceSticker(Resource resource, @FrameRange int position) {
        checkPosition(position);

        replaceSticker(obtainSticker(position), resource, position,
                -1, -1, -1, -1, -1);
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
        int position = mEventHandle.getSelectedPosition();
        if (position == STATE_NORMAL) {
            return;
        }
        replaceSticker(obtainTextSticker(position), resource, position, -1,
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
        checkPosition(position);

        replaceSticker(obtainTextSticker(position), resource, position, -1,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 替换当前帧指定文字贴纸的背景
     *
     * @param resource      资源
     * @param position      索引
     * @param delayFrame    延迟帧数显示
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public void replaceTextSticker(Resource resource, @FrameRange int position,
                                   @FrameRange int delayFrame,
                                   @PaddingRange int leftPadding,
                                   @PaddingRange int topPadding,
                                   @PaddingRange int rightPadding,
                                   @PaddingRange int bottomPadding) {
        checkPosition(position);

        replaceSticker(obtainTextSticker(position), resource, position, delayFrame,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    //替换贴纸背景
    private void replaceSticker(StickerBean stickerBean, Resource resource, int position, int delayFrame,
                                int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        checkFrame(delayFrame);
        checkPadding(leftPadding);
        checkPadding(topPadding);
        checkPadding(rightPadding);
        checkPadding(bottomPadding);

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

            if (resource instanceof DynamicResource) {
                checkDynamicAndFrameRate(resource, mPlayerHandle.getDelayTime());
                mPlayerHandle.start();
            } else {
                delayFrame = 0;
                if (mResourceHandle.getDynamicCount() == 0) {
                    mPlayerHandle.stop();
                }
            }

            newStickerBean = copyStickerBean(stickerBean, resource.getIndex(),
                    resource.getWidth(), resource.getHeight());

            newStickerBean.setScale(newStickerBean.getScale() * mEventHandle.isBeyond(newStickerBean, 1f));
        }
        if (stickerBean instanceof TextStickerBean) {
            TextStickerBean textStickerBean = (TextStickerBean) newStickerBean;
            if (leftPadding != -1) {
                textStickerBean.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            }
            textStickerBean.setDelayTime(delayFrame);
        }

        //重新计算大小，防止超出大小
        newStickerBean.setScale(mEventHandle.isBeyond(newStickerBean,
                newStickerBean.getScale()));

        calculateSticker(newStickerBean);

        mDataHandle.replaceSticker(position, newStickerBean);

        mEventHandle.updateSelected(position);

        invalidate(newStickerBean);
    }

    /**
     * 复制当前选中贴纸到当前帧
     */
    public void copySticker() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        copySticker(mEventHandle.getSelectedPosition(), mFrameIndex, mFrameIndex);
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
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        copySticker(mEventHandle.getSelectedPosition(), 0, 0, fromFrame, toFrame);
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
        copySticker(position, 0, 0, fromFrame, toFrame);
    }

    /**
     * 复制指定贴纸，从fromFrame到toFrame
     *
     * @param position  索引
     * @param dx        平移x
     * @param dy        平移y
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameRange int position, int dx, int dy,
                            @FrameRange int fromFrame, @FrameRange int toFrame) {
        checkPosition(position);
        checkFrame(fromFrame, toFrame);

        copySticker(mDataHandle.getSticker(position), dx, dy, fromFrame, toFrame);
    }

    //复制贴纸
    private void copySticker(StickerBean stickerBean, int dx, int dy, int fromFrame, int toFrame) {
        if (stickerBean == null) {
            return;
        }

        StickerBean newStickerBean = copyStickerBean(stickerBean, fromFrame, toFrame);

        newStickerBean.setDx(newStickerBean.getDx() + dx);
        newStickerBean.setDy(newStickerBean.getDy() + dy);

        calculateSticker(newStickerBean);

        mResourceHandle.addedUseCount(newStickerBean.getIndex());

        int position = addSticker(newStickerBean);

        if (mOnCopyListener != null) {
            mOnCopyListener.onCopy(fromFrame, toFrame, position);
        }
    }

    /**
     * 删除当前选中的贴纸
     */
    public void deleteSticker() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        deleteSticker(mEventHandle.getSelectedPosition());
    }

    /**
     * 删除指定贴纸
     *
     * @param position 索引
     */
    public void deleteSticker(@FrameRange int position) {
        checkPosition(position);

        if (!mDataHandle.containsSticker(position)) {
            return;
        }
        mEventHandle.delete(position);
    }

    /**
     * 删除全部贴纸
     */
    public void clearAllSticker() {
        Iterator<StickerBean> iterator = mDataHandle.getStickers();
        while (iterator.hasNext()) {
            int position = iterator.next().getKey();
            mEventHandle.delete(position);
        }
        mPlayerHandle.stop();
        invalidate();
    }

    /**
     * 修改当前选中贴纸帧
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void modifyFrameSticker(@FrameRange int fromFrame, @FrameRange int toFrame) {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        modifyFrameSticker(mEventHandle.getSelectedPosition(), fromFrame, toFrame);
    }

    /**
     * 修改贴纸帧
     *
     * @param position  索引
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void modifyFrameSticker(@FrameRange int position,
                                   @FrameRange int fromFrame, @FrameRange int toFrame) {
        checkPosition(position);
        checkFrame(fromFrame, toFrame);

        mDataHandle.modifyFrameSticker(position, fromFrame, toFrame);
    }

    /**
     * 水平翻转当前选中贴纸
     */
    public void flipHorizontal() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        flipHorizontal(mEventHandle.getSelectedPosition());
    }

    /**
     * 水平翻转指定贴纸
     *
     * @param position 索引
     */
    public void flipHorizontal(@FrameRange int position) {
        checkPosition(position);

        StickerBean stickerBean = mDataHandle.getSticker(position);
        if (stickerBean == null) {
            return;
        }
        stickerBean.setFlipHorizontal(!stickerBean.isFlipHorizontal());
        flipHorizontalMatrix(stickerBean);

        invalidate(stickerBean);
    }

    /**
     * 垂直翻转当前选中贴纸
     */
    public void flipVertical() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        flipVertical(mEventHandle.getSelectedPosition());
    }

    /**
     * 垂直翻转指定贴纸
     *
     * @param position 索引
     */
    public void flipVertical(@FrameRange int position) {
        checkPosition(position);

        StickerBean stickerBean = mDataHandle.getSticker(position);
        if (stickerBean == null) {
            return;
        }
        stickerBean.setFlipVertical(!stickerBean.isFlipVertical());
        flipVerticalMatrix(stickerBean);

        invalidate(stickerBean);
    }

    /**
     * 设置当前选中贴纸的文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setText(text, mEventHandle.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字
     *
     * @param text     文字
     * @param position 索引
     */
    public void setText(String text, @FrameRange int position) {
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        String oldText = textStickerBean.getText();
        if (text == null && oldText == null
                || text != null && oldText != null && text.equals(oldText)) {
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
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setTextColor(color, mEventHandle.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字颜色
     *
     * @param color    文字颜色
     * @param position 索引
     */
    public void setTextColor(@ColorInt int color, @FrameRange int position) {
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        if (textStickerBean.getTextColor() == color) {
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
        if (mEventHandle.getSelectedPosition() <= STATE_NORMAL) {
            return;
        }
        setTextSize(size, mEventHandle.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字大小
     *
     * @param size     文字大小
     * @param position 索引
     */
    public void setTextSize(@TextSizeRange int size, @FrameRange int position) {
        checkTextSize(size);
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        if (textStickerBean.getTextSize() == size) {
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
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setBold(isBold, mEventHandle.getSelectedPosition());
    }


    /**
     * 设置指定贴纸的文字加粗
     *
     * @param isBold   文字加粗
     * @param position 索引
     */
    public void setBold(boolean isBold, @FrameRange int position) {
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        if (textStickerBean.isBold() == isBold) {
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
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setItalic(isItalic, mEventHandle.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     * @param position 索引
     */
    public void setItalic(boolean isItalic, @FrameRange int position) {
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        if (textStickerBean.isItalic() == isItalic) {
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
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return;
        }
        setUnderline(isUnderline, mEventHandle.getSelectedPosition());
    }

    /**
     * 设置指定贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     * @param position    索引
     */
    public void setUnderline(boolean isUnderline, @FrameRange int position) {
        checkPosition(position);

        TextStickerBean textStickerBean = obtainTextSticker(position);
        if (textStickerBean == null) {
            return;
        }
        if (textStickerBean.isUnderline() == isUnderline) {
            return;
        }

        textStickerBean.setUnderline(isUnderline);
        invalidate(textStickerBean);
    }

    /**
     * 获得当前选中贴纸索引
     */
    public int getCurrentPosition() {
        return mEventHandle.getSelectedPosition();
    }

    /**
     * 当前是否选中贴纸
     */
    public boolean isSelectedSticker() {
        return mEventHandle.getSelectedPosition() != STATE_NORMAL;
    }

    /**
     * 判断当前选中贴纸是否为动态贴纸
     */
    public boolean isDynamicSticker() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return false;
        }
        return isDynamicSticker(mEventHandle.getSelectedPosition());
    }

    /**
     * 判断指定贴纸是否为动态贴纸
     *
     * @param position 索引
     */
    public boolean isDynamicSticker(int position) {
        StickerBean stickerBean = obtainSticker(position);
        if (stickerBean == null) {
            return false;
        }
        return mResourceHandle.isDynamic(stickerBean.getIndex());
    }

    /**
     * 获得当前选中贴纸总帧数
     */
    public int getStickerFrameCount() {
        if (mEventHandle.getSelectedPosition() == STATE_NORMAL) {
            return STATE_NORMAL;
        }
        return getStickerFrameCount(mEventHandle.getSelectedPosition());
    }

    /**
     * 获得指定贴纸总帧数
     */
    public int getStickerFrameCount(int position) {
        StickerBean stickerBean = obtainSticker(position);
        if (stickerBean == null) {
            return STATE_NORMAL;
        }
        return mResourceHandle.getFrameCount(stickerBean.getIndex());
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
        checkFrameRateNull(mPlayerHandle.getDelayTime());
        checkFrameRateRange(mPlayerHandle.getDelayTime());

        setCurrentFrame((int) Math.round(timeMs / mPlayerHandle.getDelayTime()));
    }

    /**
     * 设置当前帧
     *
     * @param frameIndex 帧
     */
    public void setCurrentFrame(@FrameRange int frameIndex) {
        checkFrame(frameIndex);

        if (frameIndex == mFrameIndex) {
            return;
        }

        this.mFrameIndex = frameIndex;

        mDataHandle.setFrameIndex(frameIndex);
        mEventHandle.selectPosition(STATE_NORMAL);

        invalidate();
    }

    /**
     * 设置帧率(fps)，区间{1-60}
     *
     * @param frameRate 帧率
     */
    public void setFrameRate(@FrameRateRange int frameRate) {
        double delayTime = 1000 / (double) frameRate;

        checkFrameRateNull(delayTime);
        checkFrameRateRange(delayTime);

        mPlayerHandle.setDelayTime(delayTime);

        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEventHandle.setEnabled(enabled);
    }

    /**
     * 设置播放器状态，编辑或者播放
     *
     * @param state {@link PlayerSource#EDIT} and {@link PlayerSource#PLAYER}
     */
    public void setPlayerState(@PlayerSource int state) {
        if (mState == state) {
            return;
        }

        this.mState = state;
        if (state == EDIT) {
            mPlayerHandle.start();
        } else {
            mPlayerHandle.stop();
        }
    }

    /**
     * 设置内存管理，必须在没有添加贴纸之前设置
     *
     * @param memoryCache 内存管理
     */
    public void setMemoryCache(MemoryCache memoryCache) {
        mResourceHandle.setMemoryCache(memoryCache);
    }

    /**
     * 暂停播放，在{@link PlayerSource#EDIT}下有效
     */
    public void pause() {
        mPlayerHandle.pause();
    }

    /**
     * 恢复播放，在{@link PlayerSource#EDIT}下有效
     */
    public void resume() {
        mPlayerHandle.resume();
    }

    /**
     * 绘制指定索引贴纸到画布上
     *
     * @param canvas    画布
     * @param positions 贴纸索引集合
     */
    public void drawCanvas(Canvas canvas, @NonNull List<Integer> positions) {
        checkPositions(positions);

        if (mDataHandle.size() == 0) {
            return;
        }

        long uptimeMs = Math.round(mFrameIndex * mPlayerHandle.getDelayTime());

        for (int position : positions) {
            StickerBean stickerBean = obtainSticker(position);
            if (stickerBean == null) {
                continue;
            }

            mResourceHandle.loadBitmap(stickerBean.getIndex(), uptimeMs, mBitmapFrameInfo);

            Bitmap bitmap = mBitmapFrameInfo.getBitmap();
            int frame = mBitmapFrameInfo.getFrame();

            calculateSticker(stickerBean, mDrawMatrix, getWidth(), canvas.getWidth());

            if (stickerBean instanceof TextStickerBean) {
                TextStickerBean textStickerBean = (TextStickerBean) stickerBean;
                mRendererHandle.drawTextSticker(canvas, mDrawMatrix, textStickerBean, bitmap, frame);
            } else {
                mRendererHandle.drawSticker(canvas, mDrawMatrix, stickerBean, bitmap);
            }
        }
    }

    /**
     * 绘制指定帧的贴纸到画布上，需要遍历全局，效率较低
     *
     * @param canvas     画布
     * @param frameIndex 帧序列
     */
    public void drawCanvas(Canvas canvas, @FrameRange int frameIndex) {
        checkFrame(frameIndex);

        if (mDataHandle.size() == 0) {
            return;
        }

        long uptimeMs = Math.round(mFrameIndex * mPlayerHandle.getDelayTime());

        for (Node<StickerBean> node : mDataHandle.searchStickers(frameIndex)) {
            StickerBean stickerBean = node.getValue();

            mResourceHandle.loadBitmap(stickerBean.getIndex(), uptimeMs, mBitmapFrameInfo);

            Bitmap bitmap = mBitmapFrameInfo.getBitmap();
            int frame = mBitmapFrameInfo.getFrame();

            calculateSticker(stickerBean, mDrawMatrix, getWidth(), canvas.getWidth());

            if (stickerBean instanceof TextStickerBean) {
                TextStickerBean textStickerBean = (TextStickerBean) stickerBean;
                mRendererHandle.drawTextSticker(canvas, mDrawMatrix, textStickerBean, bitmap, frame);
            } else {
                mRendererHandle.drawSticker(canvas, mDrawMatrix, stickerBean, bitmap);
            }
        }
    }

    /**
     * 获得全部贴纸数据
     *
     * @return 迭代器
     */
    public ArrayList<StickerBean> getStickers() {
        ArrayList<StickerBean> stickers = new ArrayList<>();
        Iterator<StickerBean> iterator = mDataHandle.getStickers();
        while (iterator.hasNext()) {
            StickerBean stickerBean = iterator.next().getValue();
            stickers.add(StickerOperate.copyStickerBean(stickerBean));//克隆数据防止外部修改
        }
        return stickers;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (mOnInvalidateListener != null) {
            mOnInvalidateListener.onInvalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mEventHandle.dispatchTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPlayerHandle.cancel();

        if (mDataHandle.size() == 0) {
            return;
        }

        int selectedPosition = mEventHandle.getSelectedPosition();
        for (Node<StickerBean> node : mDataHandle.getCurrentStickers()) {
            StickerBean stickerBean = node.getValue();

            long uptimeMs;
            if (mState == EDIT) {
                uptimeMs = mPlayerHandle.getCurrentUptime();
            } else {
                uptimeMs = Math.round(mFrameIndex * mPlayerHandle.getDelayTime());
            }

            mResourceHandle.loadBitmap(stickerBean.getIndex(), uptimeMs, mBitmapFrameInfo);

            Bitmap bitmap = mBitmapFrameInfo.getBitmap();
            int frame = mBitmapFrameInfo.getFrame();

            if (stickerBean instanceof TextStickerBean) {
                mRendererHandle.drawTextSticker(canvas, (TextStickerBean) stickerBean, bitmap, frame);
            } else {
                mRendererHandle.drawSticker(canvas, stickerBean, bitmap);
            }

            if (mState == EDIT//编辑模式才显示边框
                    && node.getKey() == selectedPosition) {
                mRendererHandle.drawSelected(canvas, stickerBean);
            }
        }

        mPlayerHandle.nextFrame();
    }

    //如果在当前帧内则刷新
    private void invalidate(StickerBean stickerBean) {
        if (isFrameInside(mFrameIndex,
                stickerBean.getFromFrame(), stickerBean.getToFrame())) {
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

    //添加贴纸
    private int addSticker(StickerBean stickerBean) {
        int position = mDataHandle.addSticker(stickerBean);

        if (isFrameInside(mFrameIndex,
                stickerBean.getFromFrame(), stickerBean.getToFrame())) {
            mEventHandle.selectPosition(position);
            invalidate();
        }

        return position;
    }

    //获取文字贴纸数据
    public TextStickerBean obtainTextSticker(int position) {
        StickerBean stickerBean = obtainSticker(position);
        if (stickerBean instanceof TextStickerBean) {
            return (TextStickerBean) stickerBean;
        } else {
            return null;
        }
    }

    //获取贴纸数据
    private StickerBean obtainSticker(int position) {
        return mDataHandle.getSticker(position);
    }

    /**
     * 左上按钮回调
     *
     * @param l 回调
     */
    public void setOnLeftTopListener(OnLeftTopListener l) {
        mEventHandle.setOnLeftTopListener(l);
    }

    /**
     * 右上按钮回调
     *
     * @param l 回调
     */
    public void setOnRightTopListener(OnRightTopListener l) {
        mEventHandle.setOnRightTopListener(l);
    }

    /**
     * 左下按钮回调
     *
     * @param l 回调
     */
    public void setOnLeftBottomListener(OnLeftBottomListener l) {
        mEventHandle.setOnLeftBottomListener(l);
    }

    /**
     * 复制贴纸时回调
     *
     * @param l 回调
     */
    public void setOnCopyListener(OnCopyListener l) {
        this.mOnCopyListener = l;
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
        mEventHandle.setOnClickStickerListener(l);
    }

    /**
     * 双击贴纸时回调，第一下会触发单击
     *
     * @param l 回调
     */
    public void setOnDoubleClickStickerListener(OnDoubleClickStickerListener l) {
        mEventHandle.setOnDoubleClickStickerListener(l);
    }

    /**
     * 长按贴纸时回调
     *
     * @param l 回调
     */
    public void setOnLongClickStickerListener(OnLongClickStickerListener l) {
        mEventHandle.setOnLongClickStickerListener(l);
    }

    /**
     * 选择贴纸时回调
     *
     * @param l 回调
     */
    public void setOnSelectedListener(OnSelectedListener l) {
        mEventHandle.setOnSelectedListener(l);
    }

    /**
     * 取消下载贴纸时回调，如果选中新的贴纸，会回调旧的贴纸
     *
     * @param l 回调
     */
    public void setOnUnselectedListener(OnUnselectedListener l) {
        mEventHandle.setOnUnselectedListener(l);
    }

    /**
     * 当invalidate被调用时回调
     *
     * @param l 回调
     */
    public void setOnInvalidateListener(OnInvalidateListener l) {
        this.mOnInvalidateListener = l;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllSticker();
        mPlayerHandle.stop();
        mEventHandle.onDetached();
    }
}
