package com.xiuyukeji.stickerplayerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.resource.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试Resource基类
 *
 * @author Created by jz on 2017/5/3 16:03
 */
@RunWith(AndroidJUnit4.class)
public abstract class BaseResourceTestCase<R extends Resource> {

    public static final String PATH_PNG = "text.png", PATH_JPG = "text.jpg";

    protected Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
    }

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
    public void testWidthAndHeight() {
        R resource = createAndInitResource(PATH_PNG);

        assertTrue(resource.getWidth() > 0);
        assertTrue(resource.getHeight() > 0);
    }

    @Test
    public void testGetIndex() {
        R resource = createAndInitResource(PATH_PNG);

        assertEquals(resource.getIndex(), PATH_PNG);
    }

    @Test
    public void testGetBitmap() {
        R resource = createAndInitResource(PATH_PNG);

        Bitmap bitmap = resource.getBitmap(0);

        assertNotNull(bitmap);
    }

    protected R createAndInitResource(String path) {
        R resource = createResource(path);
        resource.init();
        return resource;
    }

    private Bitmap initResource(R resource) {
        return resource.init();
    }

    protected abstract R createResource(String path);
}
