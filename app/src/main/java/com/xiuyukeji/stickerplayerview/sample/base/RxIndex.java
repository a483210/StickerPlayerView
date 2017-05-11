package com.xiuyukeji.stickerplayerview.sample.base;

/**
 * 返回索引
 *
 * @author Created by jz on 2017/5/11 11:28
 */
public class RxIndex<E> {
    public int index;
    public E value;

    public RxIndex(int index, E value) {
        this.index = index;
        this.value = value;
    }
}
