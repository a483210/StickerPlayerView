package com.xiuyukeji.stickerplayerview.data;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * 数据管理
 *
 * @author Created by jz on 2017/4/21 15:48
 */
public class DataHandle {
    private final LinkedSparseArray<StickerBean> mStickers;
    private final ArrayList<Node<StickerBean>> mCacheStickers;
    private int mArrayCount;//保证key值不重复

    private int mFrameIndex;

    private int mCacheFromKey;
    private int mCacheToKey;

    public DataHandle() {
        mStickers = new LinkedSparseArray<>(new FrameComparator());
        mCacheStickers = new ArrayList<>();

        clearCache();
    }

    /**
     * 设置当前帧
     *
     * @param frameIndex 帧
     */
    public void setFrameIndex(int frameIndex) {
        if (mFrameIndex == frameIndex) {
            return;
        }

        clearCache();
        mFrameIndex = frameIndex;
    }

    /**
     * 添加贴纸
     *
     * @param stickerBean 数据
     */
    public int addSticker(StickerBean stickerBean) {
        if (mCacheStickers.isEmpty()) {
            mStickers.put(mArrayCount, stickerBean);
        } else {
            mStickers.put(mArrayCount, stickerBean, mCacheFromKey, mCacheToKey);
        }
        clearCache();
        return mArrayCount++;
    }

    /**
     * 获得贴纸
     *
     * @param position 索引
     */
    public StickerBean getSticker(int position) {
        return mStickers.get(position);
    }

    /**
     * 删除贴纸
     *
     * @param position 索引
     */
    public StickerBean removeSticker(int position) {
        clearCache();
        return mStickers.remove(position);
    }

    /**
     * 替换贴纸
     *
     * @param position       索引
     * @param newStickerBean 数据
     */
    public void replaceSticker(int position, StickerBean newStickerBean) {
        mStickers.replace(position, newStickerBean);
    }

    /**
     * 修改贴纸帧
     *
     * @param position  索引
     * @param fromFrame 开始帧
     * @param toFrame   结束帧
     */
    public void swapFrameSticker(int position, int fromFrame, int toFrame) {
        StickerBean stickerBean = mStickers.get(position);
        int oldFromFrame = stickerBean.getFromFrame();
        stickerBean.setFromFrame(fromFrame);
        stickerBean.setToFrame(toFrame);

        if (oldFromFrame != fromFrame) {
            mStickers.order(position);
        }

        clearCache();
    }

    /**
     * 获得当前帧的贴纸
     */
    public ArrayList<Node<StickerBean>> getCurrentStickers() {
        if (!mCacheStickers.isEmpty()) {//缓存防止重复遍历
            return mCacheStickers;
        }

        Iterator<Node<StickerBean>> iterator = mStickers.iterator();
        while (iterator.hasNext()) {
            Node<StickerBean> entry = iterator.next();
            StickerBean stickerBean = entry.getValue();

            if (mFrameIndex < stickerBean.getFromFrame()) {
                continue;
            }
            if (mFrameIndex > stickerBean.getToFrame()) {
                break;
            }
            mCacheStickers.add(entry);
        }

        int size = mCacheStickers.size();
        if (size >= 1) {
            mCacheFromKey = mCacheStickers.get(0).getKey();
            if (size >= 2) {
                mCacheToKey = mCacheStickers.get(size - 1).getKey();
            }
        }

        return mCacheStickers;
    }

    /**
     * 获得全部贴纸
     */
    public Iterator<Node<StickerBean>> getStickers() {
        return mStickers.iterator();
    }

    /**
     * 贴纸数量
     */
    public int size() {
        return mStickers.size();
    }

    private void clearCache() {
        mCacheStickers.clear();
        mCacheFromKey = 0;
        mCacheToKey = 0;
    }

    //根据fromFrame排序
    private static class FrameComparator implements Comparator<StickerBean> {
        @Override
        public int compare(StickerBean last, StickerBean next) {
            if (last.getFromFrame() < next.getFromFrame()) {
                return 1;
            } else if (last.getFromFrame() > next.getFromFrame()) {
                return -1;
            }
            return 0;
        }
    }
}
