package com.xiuyukeji.stickerplayerview.sample.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器基类
 *
 * @author Created by jz on 2017/5/11 11:17
 */
public abstract class BaseAdapter<VH extends ViewHolder, E> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<E> mList;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BaseAdapter(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BaseAdapter(Context context, List<E> list) {
        this.mContext = context;
        this.mList = list == null ? new ArrayList<>() : list;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 返回数据总数
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 返回数据集
     */
    public List<E> getList() {
        return mList;
    }

    /**
     * 判断数据集是否为空
     */
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    /**
     * 添加数据集
     *
     * @param list 数据集
     */
    public void addAll(List<E> list) {
        if (list == null)
            return;
        mList.addAll(list);
    }

    /**
     * 添加数据到最后
     *
     * @param bean 数据
     */
    public void add(E bean) {
        mList.add(bean);
    }

    /**
     * 添加数据到指定位置
     *
     * @param bean     数据
     * @param position 索引
     */
    public void add(E bean, int position) {
        mList.add(position, bean);
    }

    /**
     * 设置数据集
     *
     * @param list 数据集
     */
    public void setList(List<E> list) {
        mList = list;
    }

    /**
     * 删除指定位置的数据
     *
     * @param position 索引
     */
    public void remove(int position) {
        if (position < 0 || position >= getItemCount())
            return;
        mList.remove(position);
    }

    public void remove(E obj) {
        mList.remove(obj);
    }

    /**
     * 获得指定位置的数据
     *
     * @param position 索引
     */
    public E get(int position) {
        return mList.get(position);
    }

    public void set(E bean, int position) {
        mList.set(position, bean);
    }

    /**
     * 清空数据集
     */
    public void clear() {
        mList.clear();
    }

    /**
     * 注册单击事件
     *
     * @param v View
     */
    @SuppressWarnings("unchecked")
    protected final void registerClick(View v) {
        v.setOnClickListener(clickV -> {
            VH clickHolder = (VH) clickV.getTag();
            if (clickHolder == null)
                return;

            int position = clickHolder.getLayoutPosition();
            if (position < 0 || position >= getItemCount())
                return;

            if (mOnItemClickListener == null)
                return;

            mOnItemClickListener.onItemClick(clickHolder, clickV, clickHolder.getLayoutPosition());
        });
    }

    /**
     * 注册长按事件
     *
     * @param v View
     */
    @SuppressWarnings("unchecked")
    protected final void registerLongClick(View v) {
        v.setOnLongClickListener(clickView -> {
            VH clickHolder = (VH) clickView.getTag();

            if (clickHolder == null)
                return false;

            int position = clickHolder.getLayoutPosition();
            if (position < 0 || position >= getItemCount())
                return false;

            if (mOnItemLongClickListener == null)
                return false;

            return mOnItemLongClickListener.onItemLongClick(clickHolder, clickView, clickHolder.getLayoutPosition());
        });
    }

    protected OnItemClickListener<VH> mOnItemClickListener;

    public final void setOnItemClickListener(OnItemClickListener<VH> listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener<VH extends ViewHolder> {

        void onItemClick(VH holder, View v, int position);
    }


    protected OnItemLongClickListener<VH> mOnItemLongClickListener;

    public final void setOnItemLongClickListener(OnItemLongClickListener<VH> listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnItemLongClickListener<VH extends ViewHolder> {

        boolean onItemLongClick(VH holder, View v, int position);
    }

}