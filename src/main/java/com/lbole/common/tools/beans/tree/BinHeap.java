package com.lbole.common.tools.beans.tree;

import java.util.Arrays;

/**
 * 堆数据结构特点：
 * 1、是一颗完全二叉树
 * 2、使用数组存储所有节点
 * 3、任何节点的值小于左子节点的值，左子节点的值小于右子节点的值（这是小顶堆，如果是大顶堆则正好相反，一般常用的为小顶堆）
 * 4、
 *
 * @Author 马嘉祺
 * @Date 2020/9/24 0024 16 51
 * @Description <p></p>
 */
public class BinHeap<K extends Comparable<K>, V> {
    
    private Node[] nodes;
    
    private int size;
    
    public int size() {
        return size;
    }
    
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    private void growth(int min) {
        int capacity = nodes.length;
        // Double size if small; else grow by 50%
        capacity += capacity < 64 ? capacity + 2 : (capacity >> 1);
        // overflow-conscious code
        if (capacity - MAX_ARRAY_SIZE > 0) {
            if (min < 0) {
                throw new OutOfMemoryError(); // overflow
            }
            capacity = min > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
        }
        nodes = Arrays.copyOf(nodes, capacity);
    }
    
    public void put(K key, V value) {
        if (null == key) {
            throw new NullPointerException();
        }
        final int size = this.size;
        // TODO: 判断是否需要扩容
        if (size >= nodes.length) {
        
        }
        this.size = size + 1;
        if (size == 0) {
            nodes[0] = new Node(key, value);
        } else {
        
        }
        
        /*
        if (e == null)
            throw new NullPointerException();
        modCount++;
        int i = size;
        if (i >= queue.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0)
            queue[0] = e;
        else
            siftUp(i, e);
        */
    }
    
    public V remove(K key) {
        /*
        if (size == 0)
            return null;
        int s = --size;
        modCount++;
        E result = (E) queue[0];
        E x = (E) queue[s];
        queue[s] = null;
        if (s != 0)
            siftDown(0, x);
        return result;
        */
        --size;
        return null;
    }
    
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; --i) {
        
        }
    }
    
    private void resize() {
    
    }
    
    class Node implements Comparable<Node> {
        K key;
        V value;
        
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public int compareTo(Node node) {
            return key.compareTo(node.key);
        }
        
    }
    
}
