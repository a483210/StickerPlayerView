package com.xiuyukeji.stickerplayerview;

import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * 测试LinkedSparseArray
 *
 * @author Created by jz on 2017/4/24 10:16
 */
@RunWith(AndroidJUnit4.class)
public class LinkedSparseArrayTestCase {

    private LinkedSparseArray<TestItem> sparseArray;

    @Before
    public void setUp() throws Exception {
        sparseArray = new LinkedSparseArray<>(new Comparator<TestItem>() {
            @Override
            public int compare(TestItem last, TestItem next) {
                if (last.frame < next.frame) {
                    return 1;
                } else if (last.frame > next.frame) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        sparseArray = null;
    }

    @Test
    public void testPut() throws Exception {
        addItem(3);

        assertEquals(3, sparseArray.size());
    }

    @Test
    public void testRepeatPut() throws Exception {
        sparseArray.put(0, new TestItem(0, "0"));

        assertEquals(1, sparseArray.size());
        assertEquals(0, sparseArray.get(0).frame);

        sparseArray.put(0, new TestItem(3, "3"));

        assertEquals(1, sparseArray.size());
        assertEquals(3, sparseArray.get(0).frame);
    }

    @Test
    public void testPutAndFromToKey() throws Exception {
        sparseArray.put(0, new TestItem(0, "0"));
        sparseArray.put(1, new TestItem(1, "1"));
        sparseArray.put(2, new TestItem(3, "3"));
        sparseArray.put(3, new TestItem(4, "4"));
        sparseArray.put(4, new TestItem(5, "5"));

        assertEquals(5, sparseArray.size());

        sparseArray.put(5, new TestItem(2, "2"), 1, 2);

        assertEquals(6, sparseArray.size());
        assertIterator(6);

        sparseArray.put(6, new TestItem(2, "2"), 2, 4);

        assertEquals(7, sparseArray.size());
        assertIterator(new int[]{0, 1, 2, 2, 3, 4, 5});

        sparseArray.put(7, new TestItem(-1, "2"), 7, 4);

        assertEquals(8, sparseArray.size());
        assertIterator(new int[]{-1, 0, 1, 2, 2, 3, 4, 5});
    }

    @Test
    public void testGet() throws Exception {
        addItem(2);

        assertEquals(2, sparseArray.size());

        assertEquals(sparseArray.get(1).value, "1");

        assertNull(sparseArray.get(2));
    }

    @Test
    public void testGetNode() throws Exception {
        addItem(2);

        assertEquals(2, sparseArray.size());

        assertEquals(sparseArray.getNode(1).getValue().value, "1");
        assertEquals(sparseArray.getNode(1).getKey(), 1);

        assertNull(sparseArray.getNode(2));
    }

    @Test
    public void testGetFirstNode() throws Exception {
        assertNull(sparseArray.getFirstNode());

        addItem(2);

        assertEquals(2, sparseArray.size());
        assertNotNull(sparseArray.getFirstNode());
        assertEquals(sparseArray.getFirstNode().getKey(), 0);
    }

    @Test
    public void testGetLastNode() throws Exception {
        assertNull(sparseArray.getLastNode());

        addItem(2);

        assertEquals(2, sparseArray.size());
        assertNotNull(sparseArray.getLastNode());
        assertEquals(sparseArray.getLastNode().getKey(), 1);
    }

    @Test
    public void testRemove() throws Exception {
        addItem(2);

        assertEquals(2, sparseArray.size());

        sparseArray.remove(0);

        assertEquals(1, sparseArray.size());

        sparseArray.remove(0);

        assertEquals(1, sparseArray.size());
    }

    @Test
    public void testClear() throws Exception {
        addItem(10);

        assertEquals(10, sparseArray.size());

        sparseArray.clear();

        assertEquals(0, sparseArray.size());
    }

    @Test
    public void testOrderAddAndIterator() throws Exception {
        addItem(10);

        assertEquals(10, sparseArray.size());

        assertIterator(10);
    }

    @Test
    public void testReverseAddAndIterator() throws Exception {
        reverseAddItem(10);

        assertEquals(10, sparseArray.size());

        assertIterator(10);
    }

    @Test
    public void testReplace() throws Exception {
        addItem(3);

        assertEquals(3, sparseArray.size());
        assertEquals("0", sparseArray.get(0).value);

        sparseArray.replace(0, new TestItem(0, "1"));

        assertEquals(3, sparseArray.size());
        assertEquals("1", sparseArray.get(0).value);
    }

    @Test
    public void testOrder() throws Exception {
        addItem(3);

        assertEquals(3, sparseArray.size());
        assertIterator(3);

        TestItem testItem = sparseArray.get(0);
        testItem.frame = 3;
        sparseArray.order(0);

        assertEquals(3, sparseArray.size());
        assertIterator(new int[]{1, 2, 3});
        assertEquals(3, sparseArray.get(0).frame);

        sparseArray.order(2, new TestItem(0, "0"));

        assertEquals(3, sparseArray.size());
        assertIterator(new int[]{0, 1, 3});
        assertEquals(0, sparseArray.get(2).frame);

        sparseArray.order(2, new TestItem(1, "1"));

        assertEquals(3, sparseArray.size());
        assertIterator(new int[]{1, 1, 3});
        assertEquals(1, sparseArray.get(2).frame);
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
        Iterator<TestItem> iterator = sparseArray.iterator();
        while (iterator.hasNext()) {
            TestItem testItem = iterator.next().getValue();

            assertEquals(testItem.frame, values[i]);
            i++;
        }

        assertEquals(values.length, i);
    }

    private void addItem(int count) {
        for (int i = 0; i < count; i++) {
            sparseArray.put(i, new TestItem(i, String.valueOf(i)));
        }
    }

    private void reverseAddItem(int count) {
        for (int i = count - 1; i >= 0; i--) {
            sparseArray.put(i, new TestItem(i, String.valueOf(i)));
        }
    }

    private static class TestItem {
        TestItem(int frame, String value) {
            this.frame = frame;
            this.value = value;
        }

        int frame;
        String value;

        @Override
        public String toString() {
            return "TestItem{" +
                    "frame=" + frame +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
