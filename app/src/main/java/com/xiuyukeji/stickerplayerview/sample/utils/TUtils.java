package com.xiuyukeji.stickerplayerview.sample.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 文本工具类
 *
 * @author Created by jz on 2017/6/1 16:46
 */
public final class TUtils {

    private TUtils() {
    }

    public static class OnTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
