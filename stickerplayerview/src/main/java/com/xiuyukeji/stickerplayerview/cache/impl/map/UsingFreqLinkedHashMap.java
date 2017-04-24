package com.xiuyukeji.stickerplayerview.cache.impl.map;

import android.util.SparseArray;

import java.util.HashMap;

/**
 * 双向环形链表
 * get会重新调整位置
 *
 * @author Created by jz on 2017/4/20 9:30
 */
public class UsingFreqLinkedHashMap<K, V> {
    private final HashMap<K, Node<K, V>> map;
    private final SparseArray<K> leastKeys;
    private final Node<K, V> header;

    public UsingFreqLinkedHashMap() {
        this.map = new HashMap<>();
        this.leastKeys = new SparseArray<>();
        this.header = new Node<>();
    }

    /**
     * 获得数据，
     *
     * @param key 键
     */
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }
        Node<K, V> lastNode = node.last;
        if (node.compareNode(lastNode)) {//如果相等位置变动
            swapNode(node, lastNode);
            if (node.isLeast) {
                node.isLeast = false;
                setNodeLeast(lastNode);
            }
        }
        return node.value;
    }

    /**
     * 加入数据，会根据权重调整位置
     *
     * @param key    键
     * @param value  值
     * @param weight 权重
     */
    public void put(K key, V value, int weight) {
        Node<K, V> node = map.get(key);
        if (node != null) {
            node.value = value;
            node.weight = weight;
        } else {
            node = new Node<>(key, value, weight);

            K leastKey = leastKeys.get(weight);
            if (leastKey == null) {
                //寻找最接近的节点
                int count = leastKeys.size();
                for (int i = 0; i < count; i++) {
                    int weightKey = leastKeys.keyAt(i);
                    if (weight > weightKey) {
                        leastKey = leastKeys.get(weightKey);
                    }
                }
            }

            if (leastKey == null) {//如果没有找到，将其加入到最后
                Node<K, V> lastNode = header.last;
                if (lastNode == null) {//说明没有任何节点
                    insertHeader(node);
                } else {
                    insertNodeToLast(node, lastNode);
                }
                setNodeLeast(node);
            } else {
                Node<K, V> lastNode = map.get(leastKey);
                insertNode(node, lastNode.last, lastNode);//默认加在最低之上
            }
            map.put(key, node);
        }
    }

    /**
     * 删除
     *
     * @param key 键
     */
    public V remove(K key) {
        Node<K, V> node = map.remove(key);
        if (node == null) {
            return null;
        }
        if (map.size() == 0) {//如果已经被清空
            header.last = null;
            header.next = null;
            leastKeys.clear();
        } else {
            Node<K, V> lastNode = node.last;
            Node<K, V> nextNode = node.next;
            lastNode.next = nextNode;
            nextNode.last = lastNode;
            if (node.isLeast) {//如果是最后一个
                if (node.compareNode(lastNode)) {
                    setNodeLeast(lastNode);
                } else {//如果不相同则说明已经是最后一个节点
                    leastKeys.remove(node.weight);
                }
            }
        }
        return node.value;
    }

    /**
     * 是否包含key
     *
     * @param key 键
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * 清空集合
     */
    public void clear() {
        map.clear();
        leastKeys.clear();
        header.last = null;
        header.next = null;
    }

    /**
     * 集合大小
     */
    public int size() {
        return map.size();
    }

    /**
     * 返回所有数据，无顺序
     *
     * @param values 接收数据的数组
     */
    public V[] values(V[] values) {
        int i = 0;
        for (Node<K, V> node : map.values()) {
            values[i++] = node.value;
        }
        return values;
    }

    /**
     * 获得最后一个key值
     */
    public K getLastKey() {
        if (header.last == null) {//说明没有值
            return null;
        }
        return header.last.key;
    }

    //交换节点
    private void swapNode(Node<K, V> node, Node<K, V> lastNode) {
        Node<K, V> lastNodeL = lastNode.last;
        Node<K, V> nodeN = node.next;
        lastNodeL.next = node;
        nodeN.last = lastNode;
        lastNode.last = node;
        lastNode.next = nodeN;
        node.last = lastNodeL;
        node.next = lastNode;
    }

    //插入第一个节点
    private void insertHeader(Node<K, V> node) {
        header.last = node;
        header.next = node;
        node.last = header;
        node.next = header;
    }

    //插入节点到最后一个
    private void insertNodeToLast(Node<K, V> node, Node<K, V> lastNode) {
        insertNode(node, lastNode, header);
    }

    //插入节点到last和next之间
    private void insertNode(Node<K, V> node, Node<K, V> lastNode, Node<K, V> nextNode) {
        lastNode.next = node;
        node.last = lastNode;
        node.next = nextNode;
        nextNode.last = node;
    }

    //修改最后节点索引
    private void setNodeLeast(Node<K, V> node) {
        node.isLeast = true;
        leastKeys.put(node.weight, node.key);
    }

    /**
     * 节点
     *
     * @param <K>
     * @param <V>
     */
    private static class Node<K, V> {
        private Node<K, V> last;
        private Node<K, V> next;
        private K key;
        private V value;
        private int weight;
        private boolean isLeast;

        //header构建
        Node() {
            this.weight = Integer.MIN_VALUE;
        }

        Node(K key, V value, int weight) {
            this.key = key;
            this.value = value;
            this.weight = weight;
        }

        //是否相等
        boolean compareNode(Node<K, V> lastNode) {
            return lastNode.weight != Integer.MIN_VALUE//如果不是header
                    && weight == lastNode.weight;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        Node<K, V> logNode = header;
        int count = map.size();
        for (int i = 0; i < count; i++) {
            logNode = logNode.next;
            builder.append(String.valueOf(logNode.key));
            if (i + 1 < count) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
