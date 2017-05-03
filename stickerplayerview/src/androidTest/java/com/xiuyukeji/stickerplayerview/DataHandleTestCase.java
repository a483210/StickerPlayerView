package com.xiuyukeji.stickerplayerview;

import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.bean.StickerBean;
import com.xiuyukeji.stickerplayerview.data.DataHandle;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Iterator;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Node;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试DataHandle
 *
 * @author Created by jz on 2017/5/3 14:21
 */
@RunWith(AndroidJUnit4.class)
public class DataHandleTestCase {

    private DataHandle mDataHandle;

    @Before
    public void setUp() throws Exception {
        mDataHandle = new DataHandle();
    }

    @After
    public void tearDown() throws Exception {
        mDataHandle = null;
    }

    @Test
    public void testAdd() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
    }

    @Test
    public void testGet() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
        assertEquals(0, mDataHandle.getSticker(0).getFromFrame());
        assertEquals(1, mDataHandle.getSticker(1).getFromFrame());
        assertNull(mDataHandle.getSticker(3));
    }

    @Test
    public void testRemove() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());

        mDataHandle.removeSticker(0);
        mDataHandle.removeSticker(1);

        assertEquals(1, mDataHandle.size());

        mDataHandle.removeSticker(3);

        assertEquals(1, mDataHandle.size());
    }

    @Test
    public void testContains() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
        assertTrue(mDataHandle.containsSticker(0));
        assertFalse(mDataHandle.containsSticker(3));

        mDataHandle.removeSticker(0);

        assertFalse(mDataHandle.containsSticker(0));
    }

    @Test
    public void testReplace() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
        assertEquals(0, mDataHandle.getSticker(0).getDx());

        mDataHandle.replaceSticker(0, new StickerBean(null, 4, 5, 0, 0));

        assertEquals(4, mDataHandle.getSticker(0).getWidth());
    }

    @Test
    public void testModifyFrame() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
        assertEquals(0, mDataHandle.getSticker(0).getFromFrame());

        mDataHandle.modifyFrameSticker(0, 4, 5);

        assertEquals(4, mDataHandle.getSticker(0).getFromFrame());
    }

    @Test
    public void testGetCurrent() throws Exception {
        addItem(3);

        assertEquals(3, mDataHandle.size());
        assertEquals(1, mDataHandle.getCurrentStickers().size());

        addItem(3);

        assertEquals(6, mDataHandle.size());
        assertEquals(2, mDataHandle.getCurrentStickers().size());

        mDataHandle.removeSticker(0);
        mDataHandle.removeSticker(1);

        assertEquals(4, mDataHandle.size());
        assertEquals(1, mDataHandle.getCurrentStickers().size());
    }

    @Test
    public void testIterator() throws Exception {
        addItem(10);

        assertEquals(10, mDataHandle.size());
        assertIterator(10);

        mDataHandle.removeSticker(8);
        mDataHandle.removeSticker(9);

        assertEquals(8, mDataHandle.size());
        assertIterator(8);

        mDataHandle.removeSticker(0);
        mDataHandle.removeSticker(1);

        assertEquals(6, mDataHandle.size());
        assertIterator(new int[]{2, 3, 4, 5, 6, 7});
    }

    @Test
    public void testSetFrame() throws Exception {
        testGetCurrent();

        mDataHandle.setFrameIndex(1);

        assertEquals(1, mDataHandle.getCurrentStickers().size());

        mDataHandle.setFrameIndex(2);

        assertEquals(2, mDataHandle.getCurrentStickers().size());

        mDataHandle.setFrameIndex(1);

        assertEquals(1, mDataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 1, 2, 2});
    }

    @Test
    public void testAddAndGetCurrent() throws Exception {
        testGetCurrent();

        addItem(1);

        assertEquals(5, mDataHandle.size());
        assertEquals(2, mDataHandle.getCurrentStickers().size());

        mDataHandle.addSticker(getStickerBean(5));

        assertEquals(6, mDataHandle.size());
        assertEquals(2, mDataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 0, 1, 2, 2, 5});
    }

    @Test
    public void testRemoveAndGetCurrent() throws Exception {
        testAddAndGetCurrent();

        mDataHandle.removeSticker(3);

        assertEquals(5, mDataHandle.size());
        assertEquals(1, mDataHandle.getCurrentStickers().size());

        mDataHandle.removeSticker(7);

        assertEquals(4, mDataHandle.size());
        assertEquals(1, mDataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 1, 2, 2});
    }

    @Test
    public void testModifyFrameAndGetCurrent() throws Exception {
        testGetCurrent();

        mDataHandle.modifyFrameSticker(2, 0, 0);

        assertEquals(2, mDataHandle.getCurrentStickers().size());

        mDataHandle.modifyFrameSticker(2, 1, 1);

        assertEquals(1, mDataHandle.getCurrentStickers().size());

        mDataHandle.modifyFrameSticker(3, 1, 1);

        assertEquals(0, mDataHandle.getCurrentStickers().size());

        assertIterator(new int[]{1, 1, 1, 2});
    }

    private void assertIterator(int count) {
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = i;
        }
        assertIterator(values);
    }

    private void assertIterator(int[] values) {
        int i = 0;
        Iterator<StickerBean> iterator = mDataHandle.getStickers();
        while (iterator.hasNext()) {
            Node<StickerBean> node = iterator.next();
            StickerBean stickerBean = node.getValue();

            assertEquals(values[i], stickerBean.getFromFrame());

            i++;
        }
        assertEquals(values.length, i);
    }

    private void addItem(int count) {
        for (int i = 0; i < count; i++) {
            mDataHandle.addSticker(getStickerBean(i));
        }
    }

    private StickerBean getStickerBean(int i) {
        return new StickerBean(null, i, i, i, i);
    }
}
