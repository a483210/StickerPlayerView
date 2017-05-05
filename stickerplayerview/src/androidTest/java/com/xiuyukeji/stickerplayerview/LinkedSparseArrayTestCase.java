package com.xiuyukeji.stickerplayerview;

import android.support.test.runner.AndroidJUnit4;

import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.Iterator;
import com.xiuyukeji.stickerplayerview.data.LinkedSparseArray.IteratorReverse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试LinkedSparseArray
 *
 * @author Created by jz on 2017/4/24 10:16
 */
@RunWith(AndroidJUnit4.class)
public class LinkedSparseArrayTestCase extends BaseTestCase {

    private LinkedSparseArray<TestItem> mSparseArray;

    @Before
    public void setUp() {
        super.setUp();
        mSparseArray = new LinkedSparseArray<>(new Comparator<TestItem>() {
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
    public void tearDown() {
        super.tearDown();
        mSparseArray = null;
    }

    @Test
    public void testPut() {
        addItem(3);

        assertEquals(3, mSparseArray.size());
    }

    @Test
    public void testRepeatPut() {
        mSparseArray.put(0, new TestItem(0, "0"));

        assertEquals(1, mSparseArray.size());
        assertEquals(0, mSparseArray.get(0).frame);

        mSparseArray.put(0, new TestItem(3, "3"));

        assertEquals(1, mSparseArray.size());
        assertEquals(3, mSparseArray.get(0).frame);
    }

    @Test
    public void testPutAndFromToKey() {
        mSparseArray.put(0, new TestItem(0, "0"));
        mSparseArray.put(1, new TestItem(1, "1"));
        mSparseArray.put(2, new TestItem(3, "3"));
        mSparseArray.put(3, new TestItem(4, "4"));
        mSparseArray.put(4, new TestItem(5, "5"));

        assertEquals(5, mSparseArray.size());

        mSparseArray.put(5, new TestItem(2, "2"), 1, 2);

        assertEquals(6, mSparseArray.size());
        assertIterator(6);

        mSparseArray.put(6, new TestItem(2, "2"), 2, 4);

        assertEquals(7, mSparseArray.size());
        assertIterator(new int[]{0, 1, 2, 2, 3, 4, 5});

        mSparseArray.put(7, new TestItem(-1, "2"), 7, 4);

        assertEquals(8, mSparseArray.size());
        assertIterator(new int[]{-1, 0, 1, 2, 2, 3, 4, 5});
    }

    @Test
    public void testGet() {
        addItem(2);

        assertEquals(2, mSparseArray.size());

        assertEquals(mSparseArray.get(1).value, "1");

        assertNull(mSparseArray.get(2));
    }

    @Test
    public void testGetNode() {
        addItem(2);

        assertEquals(2, mSparseArray.size());

        assertEquals(mSparseArray.getNode(1).getValue().value, "1");
        assertEquals(mSparseArray.getNode(1).getKey(), 1);

        assertNull(mSparseArray.getNode(2));
    }

    @Test
    public void testGetFirstNode() {
        assertNull(mSparseArray.getFirstNode());

        addItem(2);

        assertEquals(2, mSparseArray.size());
        assertNotNull(mSparseArray.getFirstNode());
        assertEquals(mSparseArray.getFirstNode().getKey(), 0);
    }

    @Test
    public void testGetLastNode() {
        assertNull(mSparseArray.getLastNode());

        addItem(2);

        assertEquals(2, mSparseArray.size());
        assertNotNull(mSparseArray.getLastNode());
        assertEquals(mSparseArray.getLastNode().getKey(), 1);
    }

    @Test
    public void testRemove() {
        addItem(2);

        assertEquals(2, mSparseArray.size());

        mSparseArray.remove(0);

        assertEquals(1, mSparseArray.size());

        mSparseArray.remove(0);

        assertEquals(1, mSparseArray.size());
    }

    @Test
    public void testClear() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        mSparseArray.clear();

        assertEquals(0, mSparseArray.size());
    }

    @Test
    public void testContainsKey() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        assertTrue(mSparseArray.containsKey(0));
        assertTrue(mSparseArray.containsKey(5));
        assertFalse(mSparseArray.containsKey(10));

        mSparseArray.clear();

        assertFalse(mSparseArray.containsKey(0));
        assertFalse(mSparseArray.containsKey(5));
    }

    @Test
    public void testOrderAddAndIterator() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        assertIterator(10);
    }

    @Test
    public void testReverseAddAndIterator() {
        reverseAddItem(10);

        assertEquals(10, mSparseArray.size());

        assertIterator(10);
    }

    @Test
    public void testPositionIterator() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        assertIterator(5, new int[]{5, 6, 7, 8, 9});
    }

    @Test
    public void testReverseIterator() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        assertReverseIterator(0, 10);
    }

    @Test
    public void testPositionReverseIterator() {
        addItem(10);

        assertEquals(10, mSparseArray.size());

        assertReverseIterator(5, new int[]{5, 4, 3, 2, 1, 0});
    }

    @Test
    public void testReplace() {
        addItem(3);

        assertEquals(3, mSparseArray.size());
        assertEquals("0", mSparseArray.get(0).value);

        mSparseArray.replace(0, new TestItem(0, "1"));

        assertEquals(3, mSparseArray.size());
        assertEquals("1", mSparseArray.get(0).value);
    }

    @Test
    public void testOrder() {
        addItem(3);

        assertEquals(3, mSparseArray.size());
        assertIterator(3);

        TestItem testItem = mSparseArray.get(0);
        testItem.frame = 3;
        mSparseArray.order(0);

        assertEquals(3, mSparseArray.size());
        assertIterator(new int[]{1, 2, 3});
        assertEquals(3, mSparseArray.get(0).frame);

        mSparseArray.order(2, new TestItem(0, "0"));

        assertEquals(3, mSparseArray.size());
        assertIterator(new int[]{0, 1, 3});
        assertEquals(0, mSparseArray.get(2).frame);

        mSparseArray.order(2, new TestItem(1, "1"));

        assertEquals(3, mSparseArray.size());
        assertIterator(new int[]{1, 1, 3});
        assertEquals(1, mSparseArray.get(2).frame);
    }

    private void assertIterator(int count) {
        assertIterator(0, count);
    }

    private void assertIterator(int position, int count) {
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = i;
        }
        assertIterator(position, values);
    }

    private void assertIterator(int[] values) {
        assertIterator(0, values);
    }

    private void assertIterator(int position, int[] values) {
        int i = 0;
        Iterator<TestItem> iterator = mSparseArray.iterator(position);
        while (iterator.hasNext()) {
            TestItem testItem = iterator.next().getValue();

            assertEquals(testItem.frame, values[i]);
            i++;
        }

        assertEquals(values.length, i);
    }

    private void assertReverseIterator(int position, int count) {
        int[] values = new int[count];
        int value = count - 1;
        for (int i = 0; i < count; i++) {
            values[i] = value - i;
        }
        assertReverseIterator(position, values);
    }

    private void assertReverseIterator(int position, int[] values) {
        int i = 0;
        IteratorReverse<TestItem> iterator = mSparseArray.iteratorReverse(position);
        while (iterator.hasLast()) {
            TestItem testItem = iterator.last().getValue();

            assertEquals(testItem.frame, values[i]);
            i++;
        }

        assertEquals(values.length, i);
    }

    private void addItem(int count) {
        for (int i = 0; i < count; i++) {
            mSparseArray.put(i, new TestItem(i, String.valueOf(i)));
        }
    }

    private void reverseAddItem(int count) {
        for (int i = count - 1; i >= 0; i--) {
            mSparseArray.put(i, new TestItem(i, String.valueOf(i)));
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
