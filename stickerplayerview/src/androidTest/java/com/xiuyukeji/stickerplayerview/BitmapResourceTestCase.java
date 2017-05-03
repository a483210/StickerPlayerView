package com.xiuyukeji.stickerplayerview;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.impl.BitmapResource;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试BitmapResource
 *
 * @author Created by jz on 2017/5/3 15:25
 */
@RunWith(AndroidJUnit4.class)
public class BitmapResourceTestCase extends BaseResourceTestCase<BitmapResource> {

    private static final String PATH_PNG = "text.png", PATH_JPG = "text.jpg";

    @Test
    public void testCreateAssetsPng() {
        Bitmap bitmap = initResource(createResource(PATH_PNG));

        assertNotNull(bitmap);

        bitmap = initResource(createResource(1 + PATH_PNG));

        assertNull(bitmap);
    }

    @Test
    public void testCreateAssetsJpg() {
        Bitmap bitmap = initResource(createResource(PATH_JPG));

        assertNotNull(bitmap);

        bitmap = initResource(createResource(1 + PATH_JPG));

        assertNull(bitmap);
    }

    @Test
    public void testReusable() {
        BitmapResource resource = createAndInitResource(getDefaultPath());

        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(),
                Bitmap.Config.ARGB_8888);

        assertTrue(resource.isReusable(bitmap));

        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        assertFalse(resource.isReusable(bitmap));

        assertFalse(resource.isReusable(null));
    }

    @Test
    public void testGetReusableBitmap() {
        BitmapResource resource = createAndInitResource(getDefaultPath());

        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(),
                Bitmap.Config.ARGB_8888);
        Bitmap newBitmap = resource.getBitmap(bitmap, 0);

        assertEquals(bitmap.hashCode(), newBitmap.hashCode());

        newBitmap = resource.getBitmap(null, 0);

        assertNotEquals(bitmap.hashCode(), newBitmap.hashCode());
    }

    @Override
    protected BitmapResource createResource(String path) {
        return new BitmapResource(mContext, path, Resource.ASSETS);
    }

    @Override
    protected String getDefaultPath() {
        return PATH_PNG;
    }
}
