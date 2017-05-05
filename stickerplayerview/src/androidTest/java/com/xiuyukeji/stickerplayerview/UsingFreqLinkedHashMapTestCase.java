package com.xiuyukeji.stickerplayerview;

import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.cache.impl.map.UsingFreqLinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试UsingFreqLinkedHashMap
 *
 * @author Created by jz on 2017/4/20 13:54
 */
@RunWith(AndroidJUnit4.class)
public class UsingFreqLinkedHashMapTestCase extends BaseTestCase {

    private UsingFreqLinkedHashMap<String, Integer> mMap;
    private final String mDefaultKey = "testKey";

    @Before
    public void setUp() {
        super.setUp();
        mMap = new UsingFreqLinkedHashMap<>();
    }

    @After
    public void tearDown() {
        super.tearDown();
        mMap = null;
    }

    @Test
    public void testPutAndSize() {
        mMap.put(mDefaultKey, 0, 0);
        assertEquals(mMap.size(), 1);

        mMap.put(mDefaultKey + 1, 0, 0);
        assertEquals(mMap.size(), 2);
    }

    @Test
    public void testPutAndGet() {
        mMap.put(mDefaultKey, 0, 0);
        assertEquals(mMap.size(), 1);
        assertEquals(get(mDefaultKey), 0);
    }

    @Test
    public void testRemove() {
        mMap.put(mDefaultKey, 0, 0);
        assertEquals(mMap.size(), 1);

        mMap.remove(mDefaultKey);
        assertNull(mMap.get(mDefaultKey));
        assertEquals(mMap.size(), 0);
    }

    @Test
    public void testRepeatPutAndRemove() {
        mMap.put(mDefaultKey, 0, 100);
        mMap.get(mDefaultKey + 0);

        mMap.put(mDefaultKey + 0, 0, 100);
        mMap.get(mDefaultKey + 0);

        mMap.put(mDefaultKey, 0, 100);
        mMap.get(mDefaultKey + 0);
        mMap.get(mDefaultKey + 0);

        mMap.remove(mDefaultKey);

        mMap.put(mDefaultKey + 1, 0, 100);

        assertEquals(mMap.size(), 2);
    }

    @Test
    public void testContainsKey() {
        mMap.put(mDefaultKey, 0, 0);

        assertTrue(mMap.containsKey(mDefaultKey));
        assertFalse(mMap.containsKey(mDefaultKey + 1));
    }

    @Test
    public void testClear() {
        mMap.put(mDefaultKey, 0, 0);
        assertEquals(mMap.size(), 1);

        mMap.put(mDefaultKey + 1, 0, 0);
        assertEquals(mMap.size(), 2);

        mMap.clear();
        assertEquals(mMap.size(), 0);
    }

    @Test
    public void testValues() {
        int size = 1000;
        for (int i = 0; i < size; i++) {
            mMap.put(mDefaultKey + i, i, 0);
        }
        Integer[] ints = new Integer[size];
        mMap.values(ints);
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (ints[i] != null) {
                count++;
            }
        }
        assertEquals(size, count);
    }

    @Test
    public void testRepeatPut() {
        mMap.put(mDefaultKey, 1, 0);
        assertEquals(mMap.size(), 1);
        assertEquals(get(mDefaultKey), 1);

        mMap.put(mDefaultKey, 2, 0);
        assertEquals(mMap.size(), 1);
        assertEquals(get(mDefaultKey), 2);
    }

    @Test
    public void testMultiPut() {
        mMap.put(mDefaultKey, 1, 0);
        assertEquals(mMap.size(), 1);
        assertNull(mMap.get(mDefaultKey + 1));
        assertEquals(get(mDefaultKey), 1);

        mMap.put(mDefaultKey + 1, 2, 0);
        assertEquals(mMap.size(), 2);
        assertEquals(get(mDefaultKey), 1);
        assertEquals(get(mDefaultKey + 1), 2);

        mMap.put(mDefaultKey + 2, 3, 0);
        assertEquals(mMap.size(), 3);
        assertEquals(get(mDefaultKey), 1);
        assertEquals(get(mDefaultKey + 1), 2);
        assertEquals(get(mDefaultKey + 2), 3);
    }

    @Test
    public void testPutAndGetLeastKey() {
        mMap.put(mDefaultKey, 1, 100);
        String leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey);
        assertEquals(get(leastKey), 1);

        mMap.put(mDefaultKey + 1, 2, 20);
        leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey + 1);
        assertEquals(get(leastKey), 2);

        mMap.put(mDefaultKey + 2, 3, 20);
        leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey + 1);
        assertEquals(get(leastKey), 2);
    }

    @Test
    public void testGetAndGetLeastKey() {
        mMap.put(mDefaultKey, 1, 100);
        mMap.put(mDefaultKey + 1, 2, 100);
        String leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey);
        assertEquals(get(leastKey), 1);

        leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey + 1);

        get(mDefaultKey);
        leastKey = mMap.getLastKey();
        assertEquals(leastKey, mDefaultKey + 1);
        assertEquals(get(leastKey), 2);
    }

    private int get(String key) {
        Integer value = mMap.get(key);
        return value == null ? Integer.MIN_VALUE : value;
    }
}
