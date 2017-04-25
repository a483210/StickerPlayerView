package com.xiuyukeji.stickerplayerview.data;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Iterator;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.IteratorReverse;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.xiuyukeji.stickerplayerview.utils.StickerOperate.isFrameInside;

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

    private int mCacheFromPosition;
    private int mCacheToPosition;

    private final FrameNodeComparator mFrameNodeComparator;

    public DataHandle() {
        mStickers = new LinkedSparseArray<>(new FrameComparator());
        mCacheStickers = new ArrayList<>();

        mFrameNodeComparator = new FrameNodeComparator();
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

        //设置帧后重新取得缓存
        mCacheStickers.clear();
        resetCurrentStickers(frameIndex);

        this.mFrameIndex = frameIndex;
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
            mStickers.put(mArrayCount, stickerBean, mCacheFromPosition, mCacheToPosition);
        }

        if (isFrameInside(mFrameIndex,
                stickerBean.getFromFrame(), stickerBean.getToFrame())) {//如果在当前帧内将其加入
            mCacheStickers.add(mStickers.getNode(mArrayCount));
            Collections.sort(mCacheStickers, mFrameNodeComparator);//重新排序
        }

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
     * 是否包含索引
     *
     * @param position 索引
     */
    public boolean containsSticker(int position) {
        return mStickers.containsKey(position);
    }

    /**
     * 删除贴纸
     *
     * @param position 索引
     */
    public StickerBean removeSticker(int position) {
        StickerBean stickerBean = mStickers.remove(position);
        if (isFrameInside(mFrameIndex,
                stickerBean.getFromFrame(), stickerBean.getToFrame())) {//如果在当前帧内将其删除
            removeCache(position);
        }
        return stickerBean;
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
        int oldToFrame = stickerBean.getToFrame();
        stickerBean.setFromFrame(fromFrame);
        stickerBean.setToFrame(toFrame);

        if (oldFromFrame != fromFrame) {
            mStickers.order(position);
        }

        boolean oldIsFrameInside = isFrameInside(mFrameIndex, oldFromFrame, oldToFrame);//之前是否在帧内
        boolean isFrameInside = isFrameInside(mFrameIndex, stickerBean.getFromFrame(),
                stickerBean.getToFrame());//现在是否在帧内

        if (oldIsFrameInside) {
            if (isFrameInside) {//如果都在帧内
                Collections.sort(mCacheStickers, mFrameNodeComparator);//重新排序
            } else {//如果现在不在删除
                removeCache(position);
            }
        } else {
            if (isFrameInside) {//如果现在在添加
                mCacheStickers.add(mStickers.getNode(position));
                Collections.sort(mCacheStickers, mFrameNodeComparator);//重新排序
            }
        }
    }

    /**
     * 获得当前帧的贴纸
     */
    public ArrayList<Node<StickerBean>> getCurrentStickers() {
        //缓存防止重复遍历
        if (!mCacheStickers.isEmpty()) {
            return mCacheStickers;
        } else {
            return resetCurrentStickers(mFrameIndex);
        }
    }

    //重新获取当前帧贴纸
    private ArrayList<Node<StickerBean>> resetCurrentStickers(int frameIndex) {
        mCacheStickers.clear();

        if (mStickers.size() == 0) {
            return mCacheStickers;
        }

        if (frameIndex >= mFrameIndex) {
            Iterator<StickerBean> iterator;
            if (frameIndex == mFrameIndex) {//只有初始化调用
                iterator = mStickers.iterator();
            } else {//如果大于
                iterator = mStickers.iterator(mCacheToPosition);
            }
            while (iterator.hasNext()) {
                Node<StickerBean> node = iterator.next();
                StickerBean stickerBean = node.getValue();

                if (mFrameIndex < stickerBean.getFromFrame()) {
                    continue;
                }
                if (mFrameIndex > stickerBean.getToFrame()) {
                    break;
                }

                mCacheStickers.add(node);
            }
        } else {//如果小于
            IteratorReverse<StickerBean> iterator = mStickers.iteratorReverse(mCacheFromPosition);
            while (iterator.hasLast()) {
                Node<StickerBean> node = iterator.last();
                StickerBean stickerBean = node.getValue();

                if (mFrameIndex > stickerBean.getToFrame()) {
                    continue;
                }
                if (mFrameIndex < stickerBean.getFromFrame()) {
                    break;
                }

                mCacheStickers.add(0, node);
            }
        }

        int size = mCacheStickers.size();
        if (size >= 1) {
            mCacheFromPosition = mCacheStickers.get(0).getKey();
            mCacheToPosition = mCacheStickers.get(size - 1).getKey();
        } else {
            mCacheFromPosition = 0;
            mCacheToPosition = 0;
        }

        return mCacheStickers;
    }

    /**
     * 获得全部贴纸
     */
    public Iterator<StickerBean> getStickers() {
        return mStickers.iterator();
    }

    /**
     * 贴纸数量
     */
    public int size() {
        return mStickers.size();
    }

    //根据索引删除缓存
    private void removeCache(int position) {
        int count = mCacheStickers.size();
        for (int i = 0; i < count; i++) {
            Node<StickerBean> node = mCacheStickers.get(i);
            if (node.getKey() == position) {
                mCacheStickers.remove(i);
                break;
            }
        }
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

    //根据fromFrame排序
    private static class FrameNodeComparator implements Comparator<Node<StickerBean>> {
        @Override
        public int compare(Node<StickerBean> last, Node<StickerBean> next) {
            if (last.getValue().getFromFrame() < next.getValue().getFromFrame()) {
                return 1;
            } else if (last.getValue().getFromFrame() > next.getValue().getFromFrame()) {
                return -1;
            }
            return 0;
        }
    }
}
