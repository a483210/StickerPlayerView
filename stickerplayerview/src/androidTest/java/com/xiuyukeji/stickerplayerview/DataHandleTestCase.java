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
 * @author Created by jz on 2017/4/24 10:16
 */
@RunWith(AndroidJUnit4.class)
public class DataHandleTestCase {

    private DataHandle dataHandle;

    @Before
    public void setUp() throws Exception {
        dataHandle = new DataHandle();
    }

    @After
    public void tearDown() throws Exception {
        dataHandle = null;
    }

    @Test
    public void testAdd() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
    }

    @Test
    public void testGet() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
        assertEquals(0, dataHandle.getSticker(0).getFromFrame());
        assertEquals(1, dataHandle.getSticker(1).getFromFrame());
        assertNull(dataHandle.getSticker(3));
    }

    @Test
    public void testRemove() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());

        dataHandle.removeSticker(0);
        dataHandle.removeSticker(1);

        assertEquals(1, dataHandle.size());

        dataHandle.removeSticker(3);

        assertEquals(1, dataHandle.size());
    }

    @Test
    public void testContains() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
        assertTrue(dataHandle.containsSticker(0));
        assertFalse(dataHandle.containsSticker(3));

        dataHandle.removeSticker(0);

        assertFalse(dataHandle.containsSticker(0));
    }

    @Test
    public void testReplace() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
        assertEquals(0, dataHandle.getSticker(0).getDx());

        dataHandle.replaceSticker(0, new StickerBean(null, 4, 5, 0, 0));

        assertEquals(4, dataHandle.getSticker(0).getWidth());
    }

    @Test
    public void testModifyFrame() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
        assertEquals(0, dataHandle.getSticker(0).getFromFrame());

        dataHandle.modifyFrameSticker(0, 4, 5);

        assertEquals(4, dataHandle.getSticker(0).getFromFrame());
    }

    @Test
    public void testGetCurrent() throws Exception {
        addItem(3);

        assertEquals(3, dataHandle.size());
        assertEquals(1, dataHandle.getCurrentStickers().size());

        addItem(3);

        assertEquals(6, dataHandle.size());
        assertEquals(2, dataHandle.getCurrentStickers().size());

        dataHandle.removeSticker(0);
        dataHandle.removeSticker(1);

        assertEquals(4, dataHandle.size());
        assertEquals(1, dataHandle.getCurrentStickers().size());
    }

    @Test
    public void testIterator() throws Exception {
        addItem(10);

        assertEquals(10, dataHandle.size());
        assertIterator(10);

        dataHandle.removeSticker(8);
        dataHandle.removeSticker(9);

        assertEquals(8, dataHandle.size());
        assertIterator(8);

        dataHandle.removeSticker(0);
        dataHandle.removeSticker(1);

        assertEquals(6, dataHandle.size());
        assertIterator(new int[]{2, 3, 4, 5, 6, 7});
    }

    @Test
    public void testSetFrame() throws Exception {
        testGetCurrent();

        dataHandle.setFrameIndex(1);

        assertEquals(1, dataHandle.getCurrentStickers().size());

        dataHandle.setFrameIndex(2);

        assertEquals(2, dataHandle.getCurrentStickers().size());

        dataHandle.setFrameIndex(1);

        assertEquals(1, dataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 1, 2, 2});
    }

    @Test
    public void testAddAndGetCurrent() throws Exception {
        testGetCurrent();

        addItem(1);

        assertEquals(5, dataHandle.size());
        assertEquals(2, dataHandle.getCurrentStickers().size());

        dataHandle.addSticker(getStickerBean(5));

        assertEquals(6, dataHandle.size());
        assertEquals(2, dataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 0, 1, 2, 2, 5});
    }

    @Test
    public void testRemoveAndGetCurrent() throws Exception {
        testAddAndGetCurrent();

        dataHandle.removeSticker(3);

        assertEquals(5, dataHandle.size());
        assertEquals(1, dataHandle.getCurrentStickers().size());

        dataHandle.removeSticker(7);

        assertEquals(4, dataHandle.size());
        assertEquals(1, dataHandle.getCurrentStickers().size());

        assertIterator(new int[]{0, 1, 2, 2});
    }

    @Test
    public void testModifyFrameAndGetCurrent() throws Exception {
        testGetCurrent();

        dataHandle.modifyFrameSticker(2, 0, 0);

        assertEquals(2, dataHandle.getCurrentStickers().size());

        dataHandle.modifyFrameSticker(2, 1, 1);

        assertEquals(1, dataHandle.getCurrentStickers().size());

        dataHandle.modifyFrameSticker(3, 1, 1);

        assertEquals(0, dataHandle.getCurrentStickers().size());

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
        Iterator<StickerBean> iterator = dataHandle.getStickers();
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
            dataHandle.addSticker(getStickerBean(i));
        }
    }

    private StickerBean getStickerBean(int i) {
        return new StickerBean(null, i, i, i, i);
    }
}
