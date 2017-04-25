package com.xiuyukeji.stickerplayerview.data;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;

/**
 * 双向链表稀缺数组
 * 适用于get大于add的情况
 *
 * @author Created by jz on 2017/4/23 13:13
 */
public class LinkedSparseArray<E> {

    @IntRange(from = 0)
    @Retention(RetentionPolicy.SOURCE)
    private @interface KeyRange {
    }

    private final SparseArray<Node<E>> sparseArray;
    private final Node<E> header;
    private final Comparator<E> comparator;

    public LinkedSparseArray(@NonNull Comparator<E> comparator) {
        this.sparseArray = new SparseArray<>();
        this.header = new Node<>();
        this.comparator = comparator;
    }

    /**
     * 获取
     *
     * @param key 键
     */
    public E get(@KeyRange int key) {
        Node<E> node = sparseArray.get(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    /**
     * 获得节点
     *
     * @param key 键
     */
    public Node<E> getNode(@KeyRange int key) {
        return sparseArray.get(key);
    }

    /**
     * 获得第一个节点
     */
    public Node<E> getFirstNode() {
        return header.next;
    }

    /**
     * 获得最后节点
     */
    public Node<E> getLastNode() {
        return header.last;
    }

    /**
     * 添加
     *
     * @param key   键
     * @param value 值
     */
    public void put(@KeyRange int key, E value) {
        put(key, value, 0, 0);
    }

    /**
     * 添加，通过from和to减少查找次数，必须保证from和to是顺序的
     *
     * @param key     键
     * @param value   值
     * @param fromKey 从开始位置查找，0无效
     * @param toKey   从结束位置查找，0无效
     */
    public void put(@KeyRange int key, E value, @KeyRange int fromKey, @KeyRange int toKey) {
        Node<E> node = sparseArray.get(key);
        if (node != null) {
            node.value = value;
        } else {
            node = new Node<>(key, value);
            sparseArray.put(key, node);

            Node<E> fromNode = null;
            Node<E> toNode = null;
            if (fromKey > 0) {
                fromNode = sparseArray.get(fromKey);
            }
            if (toKey > 0) {
                toNode = sparseArray.get(toKey);
            }

            Node<E> searchNode;
            if (toNode != null) {//从结束2边找
                searchNode = searchNextNode(node, toNode);
                if (searchNode != null) {
                    insertNode(node, searchNode, searchNode.next);
                    return;
                }
                searchNode = searchLastNode(node, toNode);
                if (searchNode != null) {
                    insertNode(node, searchNode.last, searchNode);
                    return;
                }
            }
            if (fromNode != null) {//从开始2边找
                searchNode = searchNextNode(node, fromNode);
                if (searchNode != null) {
                    insertNode(node, searchNode, searchNode.next);
                    return;
                }
                searchNode = searchLastNode(node, fromNode);
                if (searchNode != null) {
                    insertNode(node, searchNode.last, searchNode);
                    return;
                }
            }
            //如果还是空从头找起
            searchNode = searchNextNode(node, header.next);
            if (searchNode == null) {//如果是空插入头部
                if (header.next == null) {
                    insertHeader(node);
                } else {
                    insertNode(node, header, header.next);
                }
            } else {
                insertNode(node, searchNode, searchNode.next);
            }
        }
    }

    /**
     * 删除
     *
     * @param key 键
     */
    public E remove(@KeyRange int key) {
        Node<E> node = sparseArray.get(key);
        if (node == null) {
            return null;
        }
        sparseArray.remove(key);
        removeNode(node);
        if (sparseArray.size() == 0) {
            clear();
        }
        return node.value;
    }

    /**
     * 清空
     */
    public void clear() {
        sparseArray.clear();
        header.last = null;
        header.next = null;
    }

    /**
     * 获得大小
     */
    public int size() {
        return sparseArray.size();
    }

    /**
     * 替换某个值
     * 必须保证要替换的数据与顺序无关，否则顺序会被打乱
     * 如果有关顺序请用{@link #order(int)}
     *
     * @param key   键
     * @param value 值
     */
    public void replace(@KeyRange int key, E value) {
        Node<E> node = sparseArray.get(key);
        if (node == null) {
            return;
        }
        node.value = value;
    }

    /**
     * 替换某个值然后重新排序
     * 无关顺序请用{@link #replace(int, E)}
     *
     * @param key   键
     * @param value 值
     */
    public void order(@KeyRange int key, E value) {
        Node<E> node = sparseArray.get(key);
        if (node == null) {
            return;
        }
        node.value = value;
        order(node);
    }

    /**
     * 让某个值重新排序
     * 无关顺序请用{@link #replace(int, E)}
     *
     * @param key 键
     */
    public void order(@KeyRange int key) {
        Node<E> node = sparseArray.get(key);
        if (node == null) {
            return;
        }
        order(node);
    }

    //重新排序
    private void order(Node<E> node) {
        Node<E> searchNode = searchNextNode(node, node.next);
        if (searchNode != null) {//如果找到比大的
            removeNode(node);
            insertNode(node, searchNode, searchNode.next);
            return;
        }
        searchNode = searchLastNode(node, node.last);
        if (searchNode != null) {//如果找到比小的
            removeNode(node);
            insertNode(node, searchNode.last, searchNode);
        }
        //没有找到，不动
    }

    /**
     * 迭代，从头开始
     */
    public Iterator<E> iterator() {
        return iterator(0);
    }

    /**
     * 迭代，从from开始
     *
     * @param fromKey 开始键
     */
    public Iterator<E> iterator(@KeyRange int fromKey) {
        Node<E> node = null;
        if (fromKey > 0) {
            node = sparseArray.get(fromKey);
        }
        if (node != null) {
            return new Iterator<>(node);
        } else {
            return new Iterator<>(header.next);
        }
    }

    /**
     * 反向迭代，从尾开始
     */
    public IteratorReverse<E> iteratorReverse() {
        return iteratorReverse(0);
    }

    /**
     * 反向迭代，从from开始
     *
     * @param toKey 结束键
     */
    public IteratorReverse<E> iteratorReverse(@KeyRange int toKey) {
        Node<E> node = null;
        if (toKey > 0) {
            node = sparseArray.get(toKey);
        }
        if (node != null) {
            return new IteratorReverse<>(node);
        } else {
            return new IteratorReverse<>(header.last);
        }
    }

    public final static class Iterator<E> {

        private Node<E> node;

        private Iterator(Node<E> node) {
            this.node = node;
        }

        public boolean hasNext() {
            return node != null && node.value != null;
        }

        public Node<E> next() {
            node = node.next;
            return node.last;
        }
    }

    public final static class IteratorReverse<E> {

        private Node<E> node;

        private IteratorReverse(Node<E> node) {
            this.node = node;
        }

        public boolean hasLast() {
            return node != null && node.value != null;
        }

        public Node<E> last() {
            node = node.last;
            return node.next;
        }
    }

    //查找上一个节点
    private Node<E> searchLastNode(Node<E> node, Node<E> comparatorNode) {
        return searchLastNode(node, comparatorNode, null);
    }

    private Node<E> searchLastNode(Node<E> node, Node<E> comparatorNode, Node<E> searchNode) {
        if (comparatorNode == null) {//如果没有结点
            return searchNode;
        }
        if (comparatorNode.value == null) {//如果到达头结点
            return searchNode;
        }
        int rel = comparator.compare(node.value, comparatorNode.value);
        if (rel > 0) {
            return searchLastNode(node, comparatorNode.last, comparatorNode);
        } else {
            return searchNode;
        }
    }

    //查找下一个节点
    private Node<E> searchNextNode(Node<E> node, Node<E> comparatorNode) {
        return searchNextNode(node, comparatorNode, null);
    }

    private Node<E> searchNextNode(Node<E> node, Node<E> comparatorNode, Node<E> searchNode) {
        if (comparatorNode == null) {//如果没有结点
            return searchNode;
        }
        if (comparatorNode.value == null) {//如果到达头结点
            return searchNode;
        }
        int rel = comparator.compare(node.value, comparatorNode.value);
        if (rel <= 0) {
            return searchNextNode(node, comparatorNode.next, comparatorNode);
        } else {
            return searchNode;
        }
    }

    //插入第一个节点
    private void insertHeader(Node<E> node) {
        header.last = node;
        header.next = node;
        node.last = header;
        node.next = header;
    }

    //插入节点到last和next之间
    private void insertNode(Node<E> node, Node<E> lastNode, Node<E> nextNode) {
        lastNode.next = node;
        node.last = lastNode;
        node.next = nextNode;
        nextNode.last = node;
    }

    //删除节点
    private void removeNode(Node<E> node) {
        Node<E> lastNode = node.last;
        Node<E> nextNode = node.next;
        lastNode.next = nextNode;
        nextNode.last = lastNode;
    }

    /**
     * 节点
     *
     * @param <E>
     */
    public static class Node<E> {
        private Node<E> last;
        private Node<E> next;
        private int key;
        private E value;

        //header构建
        private Node() {
        }

        private Node(int key, E value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 获得键
         */
        public int getKey() {
            return key;
        }

        /**
         * 获得值
         */
        public E getValue() {
            return value;
        }
    }
}
