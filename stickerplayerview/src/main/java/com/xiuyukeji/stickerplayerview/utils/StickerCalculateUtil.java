package com.xiuyukeji.stickerplayerview.utils;

import android.graphics.Matrix;

import com.xiuyukeji.stickerplayerview.bean.IconBean;
import com.xiuyukeji.stickerplayerview.bean.MatrixBean;
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
    public static void calculateTextSticker(TextStickerBean textStickerBean, float[] point,
                                            int textHeight) {
        point[0] = textStickerBean.getLeftPadding();
        point[1] = textStickerBean.getTopPadding() + (textStickerBean.getTextHeight() - textHeight) / 2f;
    }

    /**
     * 重新计算贴纸文字位置
     *
     * @param textStickerBean 贴纸数据
     * @param textHeight      文字高度
     * @param srcWidth        来源宽度
     * @param dstWidth        目的宽度
     */
    public static void calculateTextSticker(TextStickerBean textStickerBean, float[] point,
                                            int textHeight, int srcWidth, int dstWidth) {
        float ratio = dstWidth / (float) srcWidth;

        point[0] = textStickerBean.getLeftPadding() * ratio;
        point[1] = (textStickerBean.getTopPadding() + (textStickerBean.getTextHeight() - textHeight) / 2f) * ratio;
    }

    /**
     * 重新计算贴纸位置
     *
     * @param stickerBean 贴纸数据
     */
    public static void calculateSticker(StickerBean stickerBean) {
        float px = stickerBean.getWidth() / 2f;
        float py = stickerBean.getHeight() / 2f;

        Matrix matrix = stickerBean.getMatrix();

        matrix.setRotate(stickerBean.getDegrees(), px, py);
        matrix.postScale(stickerBean.getScale(), stickerBean.getScale(), px, py);
        matrix.postTranslate(stickerBean.getDx(), stickerBean.getDy());

        if (stickerBean.isFlip()) {//反转
            flipMatrix(stickerBean);
        }
    }

    /**
     * 重新计算贴纸位置，当需要缩放时
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

        if (stickerBean.isFlip()) {
            flipMatrix(stickerBean);
        }
    }

    /**
     * 重新计算边框位置
     *
     * @param stickerBean  贴纸数据
     * @param delIconBean  删除图标数据
     * @param copyIconBean 复制图标数据
     * @param dragIconBean 移动图标数据
     * @param flipIconBean 反转图标数据
     * @param sidePoint    边框坐标
     * @param sidePadding  边框间距
     */
    public static void calculateSelected(StickerBean stickerBean,
                                         IconBean delIconBean, IconBean copyIconBean, IconBean dragIconBean, IconBean flipIconBean,
                                         float[] sidePoint, int sidePadding) {
        float scaleWidth = (stickerBean.getScale() - 1) * stickerBean.getWidth();
        float scaleHeight = (stickerBean.getScale() - 1) * stickerBean.getHeight();
        float width = stickerBean.getWidth() + scaleWidth + sidePadding * 2;
        float height = stickerBean.getHeight() + scaleHeight + sidePadding * 2;
        float dx = stickerBean.getDx() - sidePadding - scaleWidth / 2;
        float dy = stickerBean.getDy() - sidePadding - scaleHeight / 2;

        float pointX = dx + width / 2;
        float pointY = dy + height / 2;

        calculateSide(stickerBean, sidePoint, sidePadding);

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

        if (stickerBean.isFlip()) {
            flipMatrix(flipIconBean);
        }
    }

    //设置图标矩阵
    private static void calculateIcon(IconBean iconBean, float degrees, float dx, float dy,
                                      float pointX, float pointY) {
        Matrix matrix = iconBean.getMatrix();

        matrix.setTranslate(dx - iconBean.getWidth() / 2,
                dy - iconBean.getHeight() / 2);
        matrix.postRotate(degrees, pointX, pointY);
    }

    //设置边框矩阵
    private static void calculateSide(StickerBean stickerBean, float[] sidePoint, float sidePadding) {
        sidePadding = sidePadding / stickerBean.getScale();

        sidePoint[0] = -sidePadding;
        sidePoint[1] = -sidePadding;
        sidePoint[2] = stickerBean.getWidth() + sidePadding;
        sidePoint[3] = -sidePadding;
        sidePoint[4] = -sidePadding;
        sidePoint[5] = stickerBean.getHeight() + sidePadding;
        sidePoint[6] = sidePoint[2];
        sidePoint[7] = sidePoint[5];

        stickerBean.getMatrix().mapPoints(sidePoint, sidePoint);
    }

    /**
     * 计算斜边长
     *
     * @param xEdge x边
     * @param yEdge y边
     */
    public static float calculateEdge(float xEdge, float yEdge) {
        return (float) Math.sqrt(xEdge * xEdge + yEdge * yEdge);
    }

    /**
     * 计算角度
     *
     * @param xEdge x边
     * @param yEdge y边
     */
    public static float calculateDegrees(float xEdge, float yEdge) {
        return (float) Math.toDegrees(Math.atan2(yEdge, xEdge));
    }

    /**
     * 反转矩阵
     *
     * @param matrixBean 矩阵数据
     */
    public static void flipMatrix(MatrixBean matrixBean) {
        float px = matrixBean.getWidth() / 2f;
        float py = matrixBean.getHeight() / 2f;

        matrixBean.getMatrix().preScale(-1, 1, px, py);
    }
}
