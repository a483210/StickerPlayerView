package com.xiuyukeji.stickerplayerview;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.impl.GifResource;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试GifResource
 *
 * @author Created by jz on 2017/5/3 16:31
 */
@RunWith(AndroidJUnit4.class)
public class GifResourceTestCase extends BaseResourceTestCase<GifResource> {

    @Test
    public void testCreateAssetsGif() {
        Bitmap bitmap = initResource(createResource(getDefaultPath()));

        assertNotNull(bitmap);

        bitmap = initResource(createResource(1 + getDefaultPath()));

        assertNull(bitmap);
    }

    @Test
    public void testGetFrameCount() {
        GifResource resource = createAndInitResource(getDefaultPath());

        assertTrue(resource.getFrameCount() > 0);
    }

    @Test
    public void testGetDuration() {
        GifResource resource = createAndInitResource(getDefaultPath());

        assertTrue(resource.getDuration() > 0);
    }

    @Test
    public void testGetDelayTime() {
        GifResource resource = createAndInitResource(getDefaultPath());

        assertTrue(resource.getDelayTime() > 0);
    }

    @Test
    public void testMultiGetBitmap() {
        GifResource resource = createAndInitResource(getDefaultPath());

        for (int i = 0; i < resource.getFrameCount(); i++) {
            Bitmap bitmap = resource.getBitmap(i);
            assertNotNull(bitmap);
        }
    }

    @Test
    public void testReusable() {
        GifResource resource = createAndInitResource(getDefaultPath());

        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(),
                Bitmap.Config.ARGB_8888);

        assertTrue(resource.isReusable(bitmap));

        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        assertFalse(resource.isReusable(bitmap));

        assertFalse(resource.isReusable(null));
    }

    @Test
    public void testGetReusableBitmap() {
        GifResource resource = createAndInitResource(getDefaultPath());

        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(),
                Bitmap.Config.ARGB_8888);
        Bitmap newBitmap = resource.getBitmap(bitmap, 0);

        assertEquals(bitmap.hashCode(), newBitmap.hashCode());

        newBitmap = resource.getBitmap(null, 0);

        assertNotEquals(bitmap.hashCode(), newBitmap.hashCode());
    }

    @Test
    public void testMultiGetReusableBitmap() {
        GifResource resource = createAndInitResource(getDefaultPath());

        Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(),
                Bitmap.Config.ARGB_8888);

        for (int i = 0; i < resource.getFrameCount(); i++) {
            Bitmap newBitmap = resource.getBitmap(bitmap, i);
            assertEquals(bitmap.hashCode(), newBitmap.hashCode());
        }
    }

    @Override
    protected GifResource createResource(String path) {
        return new GifResource(mContext, path, Resource.ASSETS);
    }

    @Override
    protected String getDefaultPath() {
        return "tuzi.gif";
    }
}
