package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

import com.xiuyukeji.stickerplayerview.annotations.FrameRange;
import com.xiuyukeji.stickerplayerview.annotations.FrameRateRange;
import com.xiuyukeji.stickerplayerview.annotations.PaddingRange;
import com.xiuyukeji.stickerplayerview.annotations.PlayerSource;
import com.xiuyukeji.stickerplayerview.annotations.TextSizeRange;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;
import com.xiuyukeji.stickerplayerview.cache.MemoryCache;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnLeftBottomListener;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnLeftTopListener;
import com.xiuyukeji.stickerplayerview.event.intefaces.OnRightTopListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDeleteListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnDoubleClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnInvalidateListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnLongClickStickerListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnSelectedListener;
import com.xiuyukeji.stickerplayerview.intefaces.OnUnselectedListener;
import com.xiuyukeji.stickerplayerview.resource.Resource;

import java.util.ArrayList;

/**
 * 贴纸控件，以帧为单位
 *
 * @author Created by jz on 2017/5/30 13:35
 */
@UiThread
public class StickerFramePlayerView extends FrameLayout {

    private final StickerPlayerView mStickerPlayerView;

    private final SparseArray<ArrayList<Integer>> mFramePositions;

    private OnDeleteListener mOnDeleteListener;

    public StickerFramePlayerView(Context context) {
        this(context, null, 0, R.style.SickerPlayerViewStyle);
    }

    public StickerFramePlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, R.style.SickerPlayerViewStyle);
    }

    public StickerFramePlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.SickerPlayerViewStyle);
    }

    public StickerFramePlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        mStickerPlayerView = new StickerPlayerView(context, attrs, defStyleAttr, defStyleRes);
        addView(mStickerPlayerView);

        mFramePositions = new SparseArray<>();

        initView();
        setListener();
    }

    private void initView() {
    }

    private void setListener() {
        mStickerPlayerView.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(StickerBean stickerBean, int position) {
                int currentPosition = mStickerPlayerView.getCurrentFrame();

                ArrayList<Integer> positions = mFramePositions.get(currentPosition);
                boolean del = positions.remove(Integer.valueOf(position));
                if (!del) {//如果没有删除成功则需要遍历
                    int count = mFramePositions.size();
                    for (int i = 0; i < count; i++) {
                        int key = mFramePositions.keyAt(i);
                        if (key == currentPosition) {//已经删除过跳过
                            continue;
                        }
                        positions = mFramePositions.get(currentPosition);
                        if (positions.remove(Integer.valueOf(position))) {//删除成功跳出
                            break;
                        }
                    }
                }

                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(stickerBean, position);
                }
            }
        });
    }

    /**
     * 通过索引获取文字贴纸数据，如果不是文字贴纸将为Null
     *
     * @param position 索引
     */
    public TextStickerBean getTextSticker(int position) {
        return mStickerPlayerView.getTextSticker(position);
    }

    /**
     * 通过索引获取贴纸数据
     *
     * @param position 索引
     */
    public StickerBean getSticker(int position) {
        return mStickerPlayerView.getSticker(position);
    }

    /**
     * 添加贴纸从from到to帧
     *
     * @param frame    帧
     * @param resource 资源
     */
    public int addSticker(@FrameRange int frame, @NonNull Resource resource) {
        int position = mStickerPlayerView.addSticker(frame, frame, resource);
        addFramePosition(frame, position);
        return position;
    }

    /**
     * 添加文字贴纸从from到to帧，没有背景
     *
     * @param frame     帧
     * @param text      文字
     * @param textColor 文字颜色
     * @param textSize  文字大小
     */
    public int addTextSticker(@FrameRange int frame,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        return addTextSticker(frame, null,
                text, textColor, textSize);
    }

    /**
     * 添加文字贴纸从from到to帧，没有背景
     *
     * @param frame         帧
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public int addTextSticker(@FrameRange int frame,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        return addTextSticker(frame, null,
                text, textColor, textSize, 0,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 添加文字贴纸从from到to帧
     *
     * @param frame     帧
     * @param resource  资源
     * @param text      文字
     * @param textColor 文字颜色
     * @param textSize  文字大小
     */
    public int addTextSticker(@FrameRange int frame, Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize) {
        return addTextSticker(frame, resource,
                text, textColor, textSize, 0, 0, 0, 0, 0);
    }

    /**
     * 添加文字贴纸从from到to帧
     *
     * @param frame         帧
     * @param resource      资源
     * @param text          文字
     * @param textColor     文字颜色
     * @param textSize      文字大小
     * @param leftPadding   左边距
     * @param topPadding    上边距
     * @param rightPadding  右边距
     * @param bottomPadding 下边距
     */
    public int addTextSticker(@FrameRange int frame, Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        return addTextSticker(frame, resource,
                text, textColor, textSize, 0,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 添加文字贴纸从from到to帧
     *
     * @param frame         帧
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
    public int addTextSticker(@FrameRange int frame, Resource resource,
                              String text, @ColorInt int textColor, @TextSizeRange int textSize,
                              @FrameRange int delayFrame,
                              @PaddingRange int leftPadding,
                              @PaddingRange int topPadding,
                              @PaddingRange int rightPadding,
                              @PaddingRange int bottomPadding) {
        int position = mStickerPlayerView.addTextSticker(frame, frame, resource,
                text, textColor, textSize, delayFrame,
                leftPadding, topPadding, rightPadding, bottomPadding);
        addFramePosition(frame, position);
        return position;
    }

    /**
     * 替换当前选中贴纸的背景
     *
     * @param resource 资源
     */
    public void replaceSticker(Resource resource) {
        mStickerPlayerView.replaceSticker(resource);
    }

    /**
     * 替换指定贴纸的背景
     *
     * @param resource 资源
     * @param position 索引
     */
    public void replaceSticker(Resource resource, @FrameRange int position) {
        mStickerPlayerView.replaceSticker(resource, position);
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
        mStickerPlayerView.replaceTextSticker(resource, leftPadding, topPadding, rightPadding, bottomPadding);
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
        mStickerPlayerView.replaceTextSticker(resource, position,
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
        mStickerPlayerView.replaceTextSticker(resource, position, delayFrame,
                leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 复制当前选中贴纸到当前帧
     */
    public void copySticker() {
        mStickerPlayerView.copySticker();
    }

    /**
     * 复制指定贴纸到当前帧
     *
     * @param position 索引
     */
    public void copySticker(@FrameRange int position) {
        mStickerPlayerView.copySticker(position);
    }

    /**
     * 复制当前选中的贴纸，从fromFrame到toFrame
     *
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void copySticker(@FrameRange int fromFrame, @FrameRange int toFrame) {
        mStickerPlayerView.copySticker(fromFrame, toFrame);
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
        mStickerPlayerView.copySticker(position, fromFrame, toFrame);
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
        mStickerPlayerView.copySticker(position, dx, dy, fromFrame, toFrame);
    }

    /**
     * 删除当前选中的贴纸
     */
    public void deleteSticker() {
        mStickerPlayerView.deleteSticker();
    }

    /**
     * 删除指定贴纸
     *
     * @param position 索引
     */
    public void deleteSticker(@FrameRange int position) {
        mStickerPlayerView.deleteSticker(position);
    }

    /**
     * 删除全部贴纸
     */
    public void clearAllSticker() {
        mStickerPlayerView.clearAllSticker();
    }

    /**
     * 获得当前选中贴纸索引
     */
    public int getCurrentPosition() {
        return mStickerPlayerView.getCurrentPosition();
    }

    /**
     * 水平翻转当前选中贴纸
     */
    public void flipHorizontal() {
        mStickerPlayerView.flipHorizontal();
    }

    /**
     * 水平翻转指定贴纸
     *
     * @param position 索引
     */
    public void flipHorizontal(@FrameRange int position) {
        mStickerPlayerView.flipHorizontal(position);
    }

    /**
     * 垂直翻转当前选中贴纸
     */
    public void flipVertical() {
        mStickerPlayerView.flipVertical();
    }

    /**
     * 垂直翻转指定贴纸
     *
     * @param position 索引
     */
    public void flipVertical(@FrameRange int position) {
        mStickerPlayerView.flipVertical(position);
    }

    /**
     * 设置当前选中贴纸的文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        mStickerPlayerView.setText(text);
    }

    /**
     * 设置指定贴纸的文字
     *
     * @param text     文字
     * @param position 索引
     */
    public void setText(String text, @FrameRange int position) {
        mStickerPlayerView.setText(text, position);
    }

    /**
     * 设置当前选中贴纸的文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(@ColorInt int color) {
        mStickerPlayerView.setTextColor(color);
    }

    /**
     * 设置指定贴纸的文字颜色
     *
     * @param color    文字颜色
     * @param position 索引
     */
    public void setTextColor(@ColorInt int color, @FrameRange int position) {
        mStickerPlayerView.setTextColor(color, position);
    }

    /**
     * 设置当前选中贴纸的文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(@TextSizeRange int size) {
        mStickerPlayerView.setTextSize(size);
    }

    /**
     * 设置指定贴纸的文字大小
     *
     * @param size     文字大小
     * @param position 索引
     */
    public void setTextSize(@TextSizeRange int size, @FrameRange int position) {
        mStickerPlayerView.setTextSize(size, position);
    }

    /**
     * 设置当前选中贴纸的文字加粗
     *
     * @param isBold 文字加粗
     */
    public void setBold(boolean isBold) {
        mStickerPlayerView.setBold(isBold);
    }


    /**
     * 设置指定贴纸的文字加粗
     *
     * @param isBold   文字加粗
     * @param position 索引
     */
    public void setBold(boolean isBold, @FrameRange int position) {
        mStickerPlayerView.setBold(isBold, position);
    }

    /**
     * 设置当前选中贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     */
    public void setItalic(boolean isItalic) {
        mStickerPlayerView.setItalic(isItalic);
    }

    /**
     * 设置指定贴纸的文字倾斜
     *
     * @param isItalic 文字倾斜
     * @param position 索引
     */
    public void setItalic(boolean isItalic, @FrameRange int position) {
        mStickerPlayerView.setItalic(isItalic, position);
    }

    /**
     * 设置当前选中贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     */
    public void setUnderline(boolean isUnderline) {
        mStickerPlayerView.setUnderline(isUnderline);
    }

    /**
     * 设置指定贴纸的文字下划线
     *
     * @param isUnderline 文字下划线
     * @param position    索引
     */
    public void setUnderline(boolean isUnderline, @FrameRange int position) {
        mStickerPlayerView.setUnderline(isUnderline, position);
    }

    /**
     * 获取当前帧
     */
    public int getCurrentFrame() {
        return mStickerPlayerView.getCurrentFrame();
    }

    /**
     * 设置当前时间，最终会被转化为帧
     *
     * @param timeMs 时间，单位毫秒
     */
    public void setCurrentTime(@IntRange(from = 0) int timeMs) {
        mStickerPlayerView.setCurrentTime(timeMs);
    }

    /**
     * 设置当前帧
     *
     * @param frameIndex 帧
     */
    public void setCurrentFrame(@FrameRange int frameIndex) {
        mStickerPlayerView.setCurrentFrame(frameIndex);
    }

    /**
     * 设置帧率(fps)，区间{1-60}
     *
     * @param frameRate 帧率
     */
    public void setFrameRate(@FrameRateRange int frameRate) {
        mStickerPlayerView.setFrameRate(frameRate);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mStickerPlayerView.setEnabled(enabled);
    }

    /**
     * 设置播放器状态，编辑或者播放
     *
     * @param state {@link PlayerSource#EDIT} and {@link PlayerSource#PLAYER}
     */
    public void setPlayerState(@PlayerSource int state) {
        mStickerPlayerView.setPlayerState(state);
    }

    /**
     * 设置内存管理，必须在没有添加贴纸之前设置
     *
     * @param memoryCache 内存管理
     */
    public void setMemoryCache(MemoryCache memoryCache) {
        mStickerPlayerView.setMemoryCache(memoryCache);
    }

    /**
     * 暂停播放，在{@link PlayerSource#EDIT}下有效
     */
    public void pause() {
        mStickerPlayerView.pause();
    }

    /**
     * 恢复播放，在{@link PlayerSource#EDIT}下有效
     */
    public void resume() {
        mStickerPlayerView.resume();
    }

    /**
     * 绘制指定帧的贴纸到画布上
     *
     * @param canvas     画布
     * @param frameIndex 帧序列
     */
    public void drawCanvas(Canvas canvas, @FrameRange int frameIndex) {
        ArrayList<Integer> positions = mFramePositions.get(frameIndex);
        if (positions == null) {
            return;
        }

        mStickerPlayerView.drawCanvas(canvas, positions);
    }

    /**
     * 获得全部贴纸数据
     *
     * @return 迭代器
     */
    public ArrayList<StickerBean> getStickers() {
        return mStickerPlayerView.getStickers();
    }

    //记录frame的position，提高某些函数查找效率
    private void addFramePosition(int frame, int position) {
        ArrayList<Integer> positions = mFramePositions.get(frame);
        if (positions == null) {
            positions = new ArrayList<>();
            mFramePositions.put(frame, positions);
        }
        positions.add(position);
    }

    /**
     * 左上按钮回调
     *
     * @param l 回调
     */
    public void setOnLeftTopListener(OnLeftTopListener l) {
        mStickerPlayerView.setOnLeftTopListener(l);
    }

    /**
     * 右上按钮回调
     *
     * @param l 回调
     */
    public void setOnRightTopListener(OnRightTopListener l) {
        mStickerPlayerView.setOnRightTopListener(l);
    }

    /**
     * 左下按钮回调
     *
     * @param l 回调
     */
    public void setOnLeftBottomListener(OnLeftBottomListener l) {
        mStickerPlayerView.setOnLeftBottomListener(l);
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
        mStickerPlayerView.setOnClickStickerListener(l);
    }

    /**
     * 双击贴纸时回调，第一下会触发单击
     *
     * @param l 回调
     */
    public void setOnDoubleClickStickerListener(OnDoubleClickStickerListener l) {
        mStickerPlayerView.setOnDoubleClickStickerListener(l);
    }

    /**
     * 长按贴纸时回调
     *
     * @param l 回调
     */
    public void setOnLongClickStickerListener(OnLongClickStickerListener l) {
        mStickerPlayerView.setOnLongClickStickerListener(l);
    }

    /**
     * 选择贴纸时回调
     *
     * @param l 回调
     */
    public void setOnSelectedListener(OnSelectedListener l) {
        mStickerPlayerView.setOnSelectedListener(l);
    }

    /**
     * 取消下载贴纸时回调，如果选中新的贴纸，会回调旧的贴纸
     *
     * @param l 回调
     */
    public void setOnUnselectedListener(OnUnselectedListener l) {
        mStickerPlayerView.setOnUnselectedListener(l);
    }

    /**
     * 当invalidate被调用时回调
     *
     * @param l 回调
     */
    public void setOnInvalidateListener(OnInvalidateListener l) {
        mStickerPlayerView.setOnInvalidateListener(l);
    }

}
