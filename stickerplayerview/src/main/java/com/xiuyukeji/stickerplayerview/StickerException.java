package com.xiuyukeji.stickerplayerview;

/**
 * 贴纸异常类
 *
 * @author Created by jz on 2016/12/7 17:29
 */
public class StickerException extends RuntimeException {

    public StickerException(String detailMessage) {
        super(detailMessage);
    }

    public StickerException(Throwable throwable) {
        super(throwable);
    }

    public StickerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}

