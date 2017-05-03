package com.xiuyukeji.stickerplayerview;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.cache.impl.UsingFreqMemoryCache;
import com.xiuyukeji.stickerplayerview.resource.BitmapReusable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试UsingFreqMemoryCache
 *
 * @author Created by jz on 2017/5/3 14:20
 */
@RunWith(AndroidJUnit4.class)
public class UsingFreqMemoryCacheTestCase {

    private static final int MAX_MEMORY_SIZE = 1000, MAX_REUSABLE_SIZE = 500;

    private UsingFreqMemoryCache mMemoryCache;
    private final String mDefaultKey = "testKey";

    @Before
    public void setUp() {
        mMemoryCache = new UsingFreqMemoryCache(MAX_MEMORY_SIZE, MAX_REUSABLE_SIZE);
    }

    @After
    public void tearDown() {
        mMemoryCache = null;
    }

    @Test
    public void testPut() {
        mMemoryCache.put(mDefaultKey, getBitmap(1), 0);

        assertEquals(1, mMemoryCache.size());

        mMemoryCache.put(mDefaultKey, getBitmap(1), 20);

        assertEquals(1, mMemoryCache.size());

        mMemoryCache.put(mDefaultKey + 1, getBitmap(1), 0);

        assertEquals(2, mMemoryCache.size());
    }

    @Test
    public void testGet() {
        mMemoryCache.put(mDefaultKey, getBitmap(1), 0);

        assertEquals(1, mMemoryCache.size());
        assertNotNull(mMemoryCache.get(mDefaultKey));
        assertEquals(4, mMemoryCache.get(mDefaultKey).getByteCount());

        assertNull(mMemoryCache.get(mDefaultKey + 1));
    }

    @Test
    public void testRemove() {
        mMemoryCache.put(mDefaultKey, getBitmap(1), 0);

        assertEquals(1, mMemoryCache.size());

        mMemoryCache.remove(mDefaultKey + 1);

        assertEquals(1, mMemoryCache.size());

        mMemoryCache.remove(mDefaultKey);

        assertEquals(0, mMemoryCache.size());
    }

    @Test
    public void testContains() {
        mMemoryCache.put(mDefaultKey, getBitmap(1), 0);

        assertEquals(1, mMemoryCache.size());
        assertTrue(mMemoryCache.containsIndex(mDefaultKey));
        assertFalse(mMemoryCache.containsIndex(mDefaultKey + 1));
    }

    @Test
    public void testClear() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            mMemoryCache.put(mDefaultKey + i, getBitmap(1), 0);
        }

        assertEquals(10, mMemoryCache.size());

        mMemoryCache.clear();

        assertEquals(0, mMemoryCache.size());
    }

    @Test
    public void testPutBeyondMaxSize() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            mMemoryCache.put(mDefaultKey + i, getBitmap(4), 0);
        }

        assertEquals(15, mMemoryCache.size());

        mMemoryCache.put(mDefaultKey, getBitmap(4), 0);

        assertEquals(15, mMemoryCache.size());

        mMemoryCache.put(mDefaultKey + 1001, getBitmap(16), 0);

        assertEquals(1, mMemoryCache.size());

        mMemoryCache.put(mDefaultKey + 1002, getBitmap(16), 0);

        assertEquals(1, mMemoryCache.size());
    }

    @Test
    public void testReusable() {
        mMemoryCache.put(mDefaultKey, getBitmap(5), 0);
        mMemoryCache.put(mDefaultKey + 1, getBitmap(5), 0);

        assertEquals(2, mMemoryCache.size());

        mMemoryCache.remove(mDefaultKey);

        assertEquals(1, mMemoryCache.size());

        Bitmap bitmap = mMemoryCache.getReusableBitmap(new MyBitmapReusable(5));

        assertNotNull(bitmap);

        bitmap = mMemoryCache.getReusableBitmap(new MyBitmapReusable(1));

        assertNull(bitmap);

        mMemoryCache.clear();

        bitmap = mMemoryCache.getReusableBitmap(new MyBitmapReusable(5));

        assertNull(bitmap);
    }

    private static class MyBitmapReusable implements BitmapReusable {
        private final int size;

        public MyBitmapReusable(int size) {
            this.size = size;
        }

        @Override
        public boolean isReusable(Bitmap bitmap) {
            return bitmap.getWidth() == size
                    && bitmap.getHeight() == size;
        }

        @Override
        public Bitmap getBitmap(Bitmap bitmap, int frameIndex) {
            return bitmap;
        }
    }

    private Bitmap getBitmap(int size) {
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    }
}
