package com.xiuyukeji.stickerplayerview;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.bean.BitmapFrameInfo;
import com.xiuyukeji.stickerplayerview.cache.impl.UsingFreqMemoryCache;
import com.xiuyukeji.stickerplayerview.resource.Resource;
import com.xiuyukeji.stickerplayerview.resource.ResourceHandle;
import com.xiuyukeji.stickerplayerview.resource.impl.BitmapResource;
import com.xiuyukeji.stickerplayerview.resource.impl.GifResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试ResourceHandle
 *
 * @author Created by jz on 2017/5/3 16:48
 */
@RunWith(AndroidJUnit4.class)
public class ResourceHandleTestCase extends BaseTestCase {

    private ResourceHandle mResourceHandle;

    @Before
    public void setUp() {
        super.setUp();
        mResourceHandle = new ResourceHandle();
    }

    @After
    public void tearDown() {
        super.tearDown();
        mResourceHandle = null;
    }

    @Test
    public void testInitResource() {
        Resource resource = createResource();
        Resource newResource = initResource(resource);

        assertNotNull(newResource);
        assertEquals(resource.hashCode(), newResource.hashCode());

        newResource = initResource(null);

        assertNull(newResource);
    }

    @Test
    public void testSetMemoryCache() {
        mResourceHandle.setMemoryCache(new UsingFreqMemoryCache());

        initResource(createResource());

        try {
            mResourceHandle.setMemoryCache(new UsingFreqMemoryCache());
            assertTrue(false);
        } catch (Exception e) {
        }
    }

    @Test
    public void testInitDynamicResource() {
        Resource resource = createDynamicResource();

        Resource newResource = initResource(resource);

        assertNotNull(newResource);
        assertEquals(resource.hashCode(), newResource.hashCode());

        assertEquals(1, mResourceHandle.getDynamicCount());

        initResource(createDynamicResource());

        assertEquals(1, mResourceHandle.getDynamicCount());
    }

    @Test
    public void testUseCount() {
        Resource resource = initResource(createDynamicResource());

        assertEquals(1, mResourceHandle.getDynamicCount());

        mResourceHandle.addedUseCount(resource.getIndex());
        mResourceHandle.addedUseCount(null);

        assertEquals(1, mResourceHandle.getDynamicCount());

        mResourceHandle.decrementUseCount(resource.getIndex());
        mResourceHandle.decrementUseCount(null);

        assertEquals(1, mResourceHandle.getDynamicCount());

        mResourceHandle.decrementUseCount(resource.getIndex());

        assertEquals(0, mResourceHandle.getDynamicCount());
    }

    @Test
    public void testLoadBitmap() {
        Resource resource = initResource(createResource());

        BitmapFrameInfo info = new BitmapFrameInfo();

        mResourceHandle.loadBitmap(resource.getIndex(), SystemClock.uptimeMillis(), info);

        assertNotNull(info.getBitmap());
        assertEquals(info.getFrame(), 0);

        mResourceHandle.loadBitmap(null, 0, info);

        assertNull(info.getBitmap());
        assertEquals(info.getFrame(), 0);

        GifResource dynamicResource = (GifResource) initResource(createDynamicResource());

        mResourceHandle.loadBitmap(dynamicResource.getIndex(), 0, info);

        assertNotNull(info.getBitmap());
        assertEquals(info.getFrame(), 0);

        Bitmap bitmap = info.getBitmap();

        mResourceHandle.loadBitmap(dynamicResource.getIndex(),
                (long) dynamicResource.getDelayTime(), info);

        assertNotNull(info.getBitmap());
        assertEquals(info.getFrame(), 1);
        assertNotEquals(bitmap.hashCode(), info.getBitmap().hashCode());
    }

    private Resource initResource(Resource resource) {
        return mResourceHandle.initResource(resource);
    }

    private Resource createDynamicResource() {
        return new GifResource(mContext, "tuzi.gif", Resource.ASSETS);
    }

    private Resource createResource() {
        return new BitmapResource(mContext, "text.png", Resource.ASSETS);
    }
}
