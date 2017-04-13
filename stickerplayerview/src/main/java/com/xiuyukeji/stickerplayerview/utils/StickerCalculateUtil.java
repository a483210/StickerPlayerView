package com.xiuyukeji.stickerplayerview.utils;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.bean.TextStickerBean;

/**
 * 贴纸计算类
 *
 * @author Created by jz on 2017/4/12 16:56
 */
public class StickerCalculateUtil {

    private StickerCalculateUtil() {
    }

    /**
     * 重新计算贴纸文字位置
     *
     * @param textStickerBean 贴纸数据
     * @param textHeight      文字高度
     */
    public static void calculateTextSticker(TextStickerBean textStickerBean, int textHeight) {
        calculateTextSticker(textStickerBean, textHeight, 1, 1);
    }

    /**
     * 重新计算贴纸文字位置
     *
     * @param textStickerBean 贴纸数据
     * @param textHeight      文字高度
     * @param srcWidth        来源宽度
     * @param dstWidth        目的宽度
     */
    public static void calculateTextSticker(TextStickerBean textStickerBean, int textHeight,
                                            int srcWidth, int dstWidth) {
        float ratio = dstWidth / (float) srcWidth;

        textStickerBean.getTextMatrix().setTranslate(textStickerBean.getLeftPadding() * ratio,
                (textStickerBean.getTopPadding() + (textStickerBean.getTextHeight() - textHeight) / 2f) * ratio);
    }

    /**
     * 重新计算贴纸位置
     *
     * @param stickerBean 贴纸数据
     */
    public static void calculateSticker(StickerBean stickerBean) {
        calculateSticker(stickerBean, 1, 1);
    }

    /**
     * 重新计算贴纸位置
     *
     * @param stickerBean 贴纸数据
     * @param srcWidth    来源宽度
     * @param dstWidth    目的宽度
     */
    public static void calculateSticker(StickerBean stickerBean,
                                        int srcWidth, int dstWidth) {
        float stickerScale = stickerBean.getScale();
        float stickerWidth = stickerBean.getWidth();
        float stickerHeight = stickerBean.getHeight();

        float ratio = dstWidth / (float) srcWidth;

        float scale = stickerScale * ratio;

        float pWidth = stickerWidth - scale * stickerWidth;
        float pHeight = stickerHeight - scale * stickerHeight;

        float width = stickerWidth - stickerScale * stickerWidth;
        float height = stickerHeight - stickerScale * stickerHeight;

        float dx = -pWidth / 2f + (stickerBean.getDx() + width / 2) * ratio;
        float dy = -pHeight / 2f + (stickerBean.getDy() + height / 2) * ratio;

        float px = stickerWidth / 2f;
        float py = stickerHeight / 2f;

        stickerBean.getMatrix().setRotate(stickerBean.getDegrees(), px, py);
        stickerBean.getMatrix().postScale(scale, scale, px, py);
        stickerBean.getMatrix().postTranslate(dx, dy);
    }

    /**
     * 重新计算边框位置
     *
     * @param stickerBean  贴纸数据
     * @param delIconBean  删除图标数据
     * @param copyIconBean 复制图标数据
     * @param dragIconBean 移动图标数据
     * @param flipIconBean 反转图标数据
     * @param framePoint   边框路径
     * @param framePadding 边框间距
     */
    public static void calculateSelected(StickerBean stickerBean,
                                         IconBean delIconBean, IconBean copyIconBean, IconBean dragIconBean, IconBean flipIconBean,
                                         float[][] framePoint, int framePadding) {
        float scale = (stickerBean.getScale() - 1) * stickerBean.getWidth();
        float width = stickerBean.getWidth() + scale + framePadding * 2;
        float height = stickerBean.getHeight() + scale + framePadding * 2;
        float dx = stickerBean.getDx() - framePadding - scale / 2;
        float dy = stickerBean.getDy() - framePadding - scale / 2;

        float pointX = dx + width / 2;
        float pointY = dy + height / 2;

        calculateFrame(framePoint, stickerBean.getDegrees(),
                dx, dy, dx + width, dy + height,
                pointX, pointY);

        calculateIcon(delIconBean, stickerBean.getDegrees(),
                dx, dy,
                pointX, pointY);

        calculateIcon(copyIconBean, stickerBean.getDegrees(),
                dx + width, dy,
                pointX, pointY);

        calculateIcon(dragIconBean, stickerBean.getDegrees(),
                dx + width, dy + height,
                pointX, pointY);

        calculateIcon(flipIconBean, stickerBean.getDegrees(),
                dx, dy + height,
                pointX, pointY);
    }

    //设置矩阵
    private static void calculateIcon(IconBean iconBean, float degrees, float dx, float dy,
                                      float pointX, float pointY) {
        iconBean.getMatrix().setTranslate(dx - iconBean.getWidth() / 2, dy - iconBean.getHeight() / 2);
        iconBean.getMatrix().postRotate(degrees, pointX, pointY);
    }

    //设置路径
    private static void calculateFrame(float[][] framePoint, float degrees,
                                       float left, float top, float right, float bottom,
                                       float pointX, float pointY) {

        degrees = (float) (degrees * Math.PI / 180);

        framePoint[0][0] = calculateRotateX(degrees, left, top, pointX, pointY);
        framePoint[0][1] = calculateRotateY(degrees, left, top, pointX, pointY);
        framePoint[1][0] = calculateRotateX(degrees, right, top, pointX, pointY);
        framePoint[1][1] = calculateRotateY(degrees, right, top, pointX, pointY);
        framePoint[2][0] = calculateRotateX(degrees, right, bottom, pointX, pointY);
        framePoint[2][1] = calculateRotateY(degrees, right, bottom, pointX, pointY);
        framePoint[3][0] = calculateRotateX(degrees, left, bottom, pointX, pointY);
        framePoint[3][1] = calculateRotateY(degrees, left, bottom, pointX, pointY);
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
    private static float calculateRotateX(float degrees, float dx, float dy, float pointX, float pointY) {
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
    private static float calculateRotateY(float degrees, float dx, float dy, float pointX, float pointY) {
        return (float) (Math.sin(degrees) * (dx - pointX) + Math.cos(degrees) * (dy - pointY) + pointY);
    }

    /**
     * 计算初始角度
     *
     * @param stickerBean 贴纸数据
     */
    public static int calculateAngle(StickerBean stickerBean) {
        int xEdge = stickerBean.getWidth() / 2;
        int yEdge = stickerBean.getHeight() / 2;

        float edge = (float) Math.sqrt(xEdge * xEdge + yEdge * yEdge);

        return (int) Math.round((Math.asin(yEdge / edge) / Math.PI * 180));
    }

    /**
     * 计算象限转换为360制
     *
     * @param degrees 角度
     * @param xEdge   邻边
     * @param yEdge   斜边
     */
    public static int degreesWith(int degrees, int xEdge, int yEdge) {
        if (xEdge >= 0 && yEdge <= 0) {// 第一象限
            return 360 + degrees;
        } else if (xEdge <= 0 && yEdge <= 0) {// 第二象限
            return 180 - degrees;
        } else if (xEdge <= 0 && yEdge >= 0) {// 第三象限
            return 180 - degrees;
        } else {//if (xEdge >= 0 && yEdge >= 0) {// 第四象限
            return degrees;
        }
    }
}
