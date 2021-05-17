package com.lbole.common.tools.beans.tree;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * 二叉查找树特点：
 * 1、是一颗二叉树
 * 2、任何节点的值大于左子节点的值，小于右子节点的值
 *
 * @Author 马嘉祺
 * @Date 2020/9/15 0015 15 02
 * @Description <p></p>
 */
public class BSTree<K extends Comparable<K>, V> {
    
    private Node root;
    
    private int size;
    
    /**
     * 获取树中节点个数
     *
     * @return
     */
    public int size() {
        return size;
    }
    
    /**
     * 获取根节点
     *
     * @return
     */
    public Entry<K, V> getRoot() {
        return null == root ? null : new Entry<>(root.key, root.value);
    }
    
    /**
     * 判断以给定节点为根节点的树是否为一颗二叉查找树
     *
     * @param node
     * @return
     */
    public boolean isBSTree(Node node) {
        // 如果当前节点为空，则从根节点开始递归校验
        node = null == node ? root : node;
        if (null == node) { // 空树也是一棵二叉查找树
            return true;
        }
        // 获取当前节点的父节点、左子节点、右子节点
        Node parent = node.parent, nodeLeft = node.left, nodeRight = node.right;
        if (null != parent && parent.left != node && parent.right != node) {
            // 父节不为空，但是父节点的左右子节点引用都没有指向当前节点，所以不是一颗二叉树
            return false;
        }
        if (null != nodeLeft) { // 当前节点的左子节点不为空
            if (nodeLeft.parent != node || nodeLeft.compareTo(node) >= 0) {
                // 左子节点父节点引用没有指向当前节点，不满足是一颗二叉树。
                // 或者左子节点的key值大于等于当前节点的key值，不满足是一颗二叉查找树（当前节点的key值大于左子节点小于右子节点）
                return false;
            }
            if (!isBSTree(nodeLeft)) { // 递归左子节点，判断子树是否满足红黑树特性
                return false;
            }
        }
        if (null != nodeRight) { // 当前节点的右子节点不为空
            if (nodeRight.parent != node || nodeRight.compareTo(node) <= 0) {
                return false;
            }
            if (!isBSTree(nodeRight)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 根据key查找value
     *
     * @param key
     * @return
     */
    public V find(K key) {
        if (null == key) {
            throw new NullPointerException();
        }
        Node node = findNode(key, root);
        return null == node ? null : node.value;
    }
    
    /**
     * 查找Key值最小的节点
     *
     * @return
     */
    public Entry<K, V> findMin() {
        Node node = root;
        while (null != node && null != node.left) {
            node = node.left;
        }
        return null == node ? null : new Entry<>(node.key, node.value);
    }
    
    /**
     * 查找Key值最大的节点
     *
     * @return
     */
    public Entry<K, V> findMax() {
        Node node = root;
        while (null != node && null != node.right) {
            node = node.right;
        }
        return null == node ? null : new Entry<>(node.key, node.value);
    }
    
    /**
     * 插入一个节点
     *
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {
        if (null == key) {
            throw new NullPointerException();
        }
        Node current = root, parent = null;
        // 获取节点要插入的位置的父节点
        int compare = 0;
        while (null != current && 0 != (compare = key.compareTo(current.key))) {
            current = compare > 0 ? (parent = current).right : (parent = current).left;
        }
        if (null != current) { // 要插入的key已存在
            V ov = current.value;
            current.value = value;
            return ov;
        }
        if (null == parent) { // 要插入的树为空树
            this.root = new Node(key, value, null);
        } else { // 插入新节点
            if (compare < 0) {
                parent.left = new Node(key, value, parent);
            } else {
                parent.right = new Node(key, value, parent);
            }
        }
        ++size;
        return null;
    }
    
    /**
     * 删除节点
     *
     * @param key
     * @return
     */
    public V remove(K key) {
        if (null == key) {
            throw new NullPointerException();
        }
        Node remove, replace;
        if (null == (remove = findNode(key, root))) { // 需要删除的节点在树中找不到
            return null;
        }
        V value = remove.value;
        if (null != remove.left && null != (replace = remove.right)) {
            // 删除节点的左右子节点都不为空节点，将删除节点和后继节点替换（也可以使用前继节点替换删除节点）
            while (null != replace.left) {
                replace = replace.left;
            }
            remove.key = replace.key;
            remove.value = replace.value;
            remove = replace;
        }
        // 此时最多只有一个子节点
        Node parent = remove.parent, child = null == (child = remove.left) ? remove.right : child; // 获取父节点和子节点
        if (null != parent && null != child) { // 父节点不为空，并且有一个不为空的子节点
            (parent.left == remove ? parent.left = child : (parent.right = child)).parent = parent;
            remove.parent = remove.left = null;
        } else if (null != parent) { // 父节点不为空，并且子节点都为空
            remove.parent = parent.right == remove ? parent.right = null : (parent.left = null);
        } else if (null != child) { // 父节点为空，但是子节点不为空
            root = child;
            remove.left = remove.right = child.parent = null;
        } else { // 父节点和子节点都为空
            root = null;
        }
        --size;
        return value;
    }
    
    /**
     * 采用中序遍历二叉树，保证数据从小到大遍历
     *
     * @return
     */
    public void forEach(Consumer<Entry<K, V>> action) {
        Deque<Node> deque = new LinkedList<>();
        for (Node node = root; node != null || !deque.isEmpty(); ) {
            for (; node != null; node = node.left) {
                deque.push(node);
            }
            Node pop = deque.pop();
            action.accept(new Entry<>(pop.key, pop.value));
            node = pop.right;
        }
    }
    
    /**
     * 查找节点
     *
     * @param key
     * @param current
     * @return
     */
    private Node findNode(K key, Node current) {
        Node found = null;
        for (int compare; null != current && null == found; ) {
            if ((compare = key.compareTo(current.key)) > 0) {
                current = current.right;
            } else if (compare < 0) {
                current = current.left;
            } else {
                found = current;
            }
        }
        return found;
    }
    
    class Node implements Comparable<Node> {
        K key;
        V value;
        Node parent, left, right;
        
        public Node(K key, V value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
        
        @Override
        public int compareTo(Node node) {
            return key.compareTo(node.key);
        }
        
    }
    
    public static class Entry<K, V> {
        private final K key;
        private final V value;
        
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() {
            return key;
        }
        
        public V getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }
    
}
