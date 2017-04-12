package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;

/**
 * 贴纸计算类
 *
 * @author Created by jz on 2017/4/12 16:56
 */
public class StickerCalculateUtil {

    private StickerCalculateUtil() {
    }

    /**
     * 重新计算贴纸位置
     *
     * @param stickerBean 贴纸数据
     */
    private void calculateSticker(StickerBean stickerBean) {
        calculateSticker(stickerBean, 1, 1);
    }

    /**
     * 重新计算贴纸位置
     *
     * @param stickerBean 贴纸数据
     * @param srcWidth    来源宽度
     * @param dstWidth    目的宽度
     */
    private void calculateSticker(StickerBean stickerBean,
                                  int srcWidth, int dstWidth) {
        float ratio = dstWidth / (float) srcWidth;

        float px = stickerBean.getWidth() / 2f;
        float py = stickerBean.getHeight() / 2f;

        float scale = stickerBean.getScale() * ratio;

        float pWidth = stickerBean.getWidth() - scale * stickerBean.getWidth();
        float pHeight = stickerBean.getHeight() - scale * stickerBean.getHeight();

        float w = stickerBean.getWidth() - stickerBean.getScale() * stickerBean.getWidth();
        float h = stickerBean.getHeight() - stickerBean.getScale() * stickerBean.getHeight();

        float dx = -pWidth / 2f + (stickerBean.getDx() + w / 2) * ratio;
        float dy = -pHeight / 2f + (stickerBean.getDy() + h / 2) * ratio;

        stickerBean.getMatrix().setRotate(stickerBean.getDegrees(), px, py);
        stickerBean.getMatrix().postScale(scale, scale, px, py);
        stickerBean.getMatrix().postTranslate(dx, dy);
    }

    /**
     * 计算变换角度X
     *
     * @param degrees 角度
     * @param dx      偏移量x
     * @param dy      偏移量y
     * @param pointX  中心点x
     * @param pointY  中心点y
     */

    public static float calculateRotateX(float degrees, float dx, float dy, float pointX, float pointY) {
        return (float) (Math.cos(degrees) * (dx - pointX) - Math.sin(degrees) * (dy - pointY) + pointX);
    }

    /**
     * 计算变换角度Y
     *
     * @param degrees 角度
     * @param dx      偏移量x
     * @param dy      偏移量y
     * @param pointX  中心点x
     * @param pointY  中心点y
     */
    public static float calculateRotateY(float degrees, float dx, float dy, float pointX, float pointY) {
        return (float) (Math.sin(degrees) * (dx - pointX) + Math.cos(degrees) * (dy - pointY) + pointY);
    }
}
