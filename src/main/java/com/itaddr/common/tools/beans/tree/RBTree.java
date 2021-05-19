package com.itaddr.common.tools.beans.tree;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * 红黑树特点：
 * 0、是一颗二叉查找树（一般资料中都没有特别声明这一点）
 * 1、每个节点的颜色是红色或者黑色
 * 2、根节点颜色是黑色
 * 3、每个叶子结点（Nil）节点的颜色都是黑色（这里的叶子节点是指不存在的空节点，但是它又需要参与所有路径上黑色节点的个数计算）
 * 4、红色节点的子节点都必须是黑色的（所有路径中都不允许出现连续的两个红色节点）
 * 5、任意节点到其叶子结点的所有路径上黑色节点的个数是相等的（这样可以确保没有任何一条路径长度会超出其他路径长度的两倍，所以红黑树是接近平衡的二叉查找树）
 *
 * @Author 马嘉祺
 * @Date 2020/9/15 0015 14 30
 * @Description <p></p>
 */
public class RBTree<K extends Comparable<K>, V> {
    
    private final static int RED = 0, BLACK = 1;
    
    private Node root;
    
    private int size;
    
    /**
     * 获取根节点的key
     *
     * @return
     */
    public Entry<K, V> getRoot() {
        return null == root ? null : new Entry<>(root.key, root.value);
    }
    
    /**
     * 获取树中节点个数
     *
     * @return
     */
    public int size() {
        return size;
    }
    
    /**
     * 判断以给定节点为根节点的树是否为一颗红黑树
     *
     * @param node
     * @return
     */
    public boolean isRBTree(Node node) {
        // 如果当前节点为空，则从根节点开始递归校验
        node = null == node ? root : node;
        if (null == node) { // 空树也是一棵红黑树
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
            if (RED == nodeLeft.color) {
                if (RED == node.color) {
                    // 当前节点和当前节点的左子节点都红色节点，不满足是一颗红黑树（红黑树中的任何路径上都不能出现连续的两个红色节点）
                    return false;
                }
            } else {
                if (null == nodeRight) {
                    // 左子节点为黑色节点右子节点为空节点，不满足是一个红黑树（从一个节点到它所能到达的任何叶子结点的任何路径上黑色结点个数必须相等）
                    return false;
                } else if (RED == nodeRight.color && (null == nodeRight.left || null == nodeRight.right)) {
                    // 左子节点为黑色，右子节点为红色并且其子节点有一个为Nil节点，不满足是一颗红黑树（从一个节点到它所能到达的任何叶子结点的任何路径上黑色结点个数必须相等）
                    return false;
                }
            }
            if (!isRBTree(nodeLeft)) { // 递归左子节点，判断子树是否满足红黑树特性
                return false;
            }
        }
        if (null != nodeRight) { // 当前节点的右子节点不为空
            if (nodeRight.parent != node || nodeRight.compareTo(node) <= 0) {
                return false;
            }
            if (RED == nodeRight.color) {
                if (RED == node.color) {
                    return false;
                }
            } else {
                if (null == nodeLeft) {
                    return false;
                } else if (RED == nodeLeft.color && (null == nodeLeft.left || null == nodeLeft.right)) {
                    return false;
                }
            }
            if (!isRBTree(nodeRight)) {
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
     * 向红黑树中插入节点
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
            root = new Node(key, value, BLACK, null);
        } else { // 插入新节点
            Node insert = new Node(key, value, RED, parent);
            current = compare < 0 ? parent.left = insert : (parent.right = insert);
            fixAfterPut(current); // 重新平衡插入节点后的树
        }
        ++size;
        return null;
    }
    
    /**
     * 从红黑树中删除节点
     *
     * @param key
     * @return
     */
    public V remove(K key) {
        if (null == key) {
            throw new NullPointerException();
        }
        Node remove, parent, replace;
        if (null == (remove = findNode(key, root))) { // 需要删除的节点在树中找不到
            return null;
        }
        V value = remove.value;
        if (null != remove.left && null != (replace = remove.right)) {
            // 删除节点的左右子节点都不为空节点，将删除节点和后继节点替换
            while (null != replace.left) {
                replace = replace.left;
            }
            remove.key = replace.key;
            remove.value = replace.value;
            remove = replace;
        }
        // 此时子节点最多只有一个非叶子节点
        if (null != (null == (replace = remove.left) ? replace = remove.right : replace)) {
            // 删除节点的左右子节点有一个不为空，将删除节点和子节点替换
            remove.key = replace.key;
            remove.value = replace.value;
            remove = replace;
        }
        // 此时子节点全部为叶子节点
        if (null == (parent = remove.parent)) { // 删除节点为根节点
            root = null;
        } else {
            fixBeforeRemove(remove); // 删除节点之前需要重新将树平衡
            remove.parent = parent.right == remove ? parent.right = null : (parent.left = null); // 最后删除节点
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
    
    /**
     * 左旋转节点
     *
     * @param rotate
     */
    private void rotateLeft(Node rotate) {
        // 获取旋转节点的右子节点
        Node right, parent, broLeft;
        if (null == rotate || null == (right = rotate.right)) {
            return;
        }
        if (null != (broLeft = rotate.right = right.left)) {
            // 将旋转节点的右子节点设置为右子节点的左子节点，并将右子节点的左子节点父节点设置为旋转节点
            broLeft.parent = rotate;
        }
        if (null == (parent = right.parent = rotate.parent)) {
            // 右子节点的父节点设置为旋转节点的父节点，如果父节点为空则将右子节点设置为根节点，并将颜色设置为黑色
            (this.root = right).color = BLACK;
        } else if (parent.left == rotate) {
            parent.left = right;
        } else {
            parent.right = right;
        }
        right.left = rotate;
        rotate.parent = right;
    }
    
    /**
     * 右旋转节点
     *
     * @param rotate
     */
    private void rotateRight(Node rotate) {
        // 获取旋转节点的左子节点
        Node left, parent, broRight;
        if (null == rotate || null == (left = rotate.left)) {
            return;
        }
        if (null != (broRight = rotate.left = left.right)) {
            // 将旋转节点的左子节点设置为左子节点的右子节点，并将左子节点的右子节点父节点设置为旋转节点
            broRight.parent = rotate;
        }
        if (null == (parent = left.parent = rotate.parent)) {
            // 将左子节点的父节点设置为旋转节点的父节点，如果父节点为空则将左子节点设置为根节点，并将颜色置黑
            (this.root = left).color = BLACK;
        } else if (parent.left == rotate) {
            parent.left = left;
        } else {
            parent.right = left;
        }
        left.right = rotate;
        rotate.parent = left;
    }
    
    /**
     * 插入数据之后将树进行平衡
     *
     * @param current
     */
    private void fixAfterPut(Node current) {
        for (Node parent, grandfather, graLeft, graRight; ; ) {
            if (null == (parent = current.parent)) {
                // TODO: 当前节点父节点是空节点，适配【场景1】
                current.color = BLACK;
                break;
            }
            if (BLACK == parent.color || null == (grandfather = parent.parent)) {
                // TODO: 当前节点的父节点是黑色节点，或者祖父节点是空节点（父节点是根节点），适配【场景2】
                break;
            }
            if ((graLeft = grandfather.left) == parent) { // 父节点为祖父节点的左子节点
                /*
                 * 节点情况分析：
                 * 1、当前节点不为空，并且为红色节点
                 * 2、当前节点的父节点不为空，并且为红色节点
                 * 3、当前节点的祖父节点不为空，并且为黑色节点
                 */
                if (null != (graRight = grandfather.right) && RED == graRight.color) {
                    // TODO: 当前节点的叔叔节点是红色节点，适配【场景4】
                    graRight.color = BLACK; // 将叔叔节点颜色置黑
                    parent.color = BLACK; // 将父节点颜色置黑
                    grandfather.color = RED; // 将祖父节点颜色置红
                    current = grandfather; // 将祖父节点设为当前节点
                } else {
                    // TODO: 当前节点的叔叔节点是叶子节点或者黑色节点，适配【场景3】
                    if (current == parent.right) {
                        // 当前节点为父节点的右子节点
                        rotateLeft(current = parent); // 将将父节点设为当前节点并将当前节点左旋转
                        grandfather = (parent = current.parent).parent; // 重新为父节点和祖父节点赋值
                    }
                    parent.color = BLACK; // 将父节点颜色置黑
                    grandfather.color = RED; // 将祖父节点颜色置红
                    rotateRight(grandfather); // 将祖父节点进行右旋转
                }
            } else { // 父节点为祖父节点的右子节点，这里就不做注释了
                if (null != graLeft && RED == graLeft.color) {
                    graLeft.color = BLACK;
                    parent.color = BLACK;
                    grandfather.color = RED;
                    current = grandfather;
                } else {
                    if (current == parent.left) {
                        rotateRight(current = parent);
                        grandfather = (parent = current.parent).parent;
                    }
                    parent.color = BLACK;
                    grandfather.color = RED;
                    rotateLeft(grandfather);
                }
            }
        }
    }
    
    /**
     * 删除节点之前将数平衡
     *
     * @param current 被删除的节点
     */
    private void fixBeforeRemove(Node current) {
        for (Node parent, left, right; null != current // 当前节点不为空
                && null != (parent = current.parent); ) {  // TODO: 当前节点的父节点是空节点，适配【场景1】
            if (RED == current.color) { // TODO: 当前节点为红色节点，适配【场景2】
                current.color = BLACK;
                break;
            }
            if ((left = parent.left) == current) { // 如果当前节点为父节点的左子节点
                /*
                 * 节点情况分析：
                 * 1、当前节点是黑色节点
                 * 2、当前节点的兄弟节点不是叶子结点
                 */
                if (RED == (right = parent.right).color) { // TODO: 当前节点的兄弟节点为红色节点，适配【场景4】
                    /*
                     * 节点情况分析：
                     * 1、父节点为黑色节点；
                     * 2、兄弟节点的左右子节点为黑色节点；
                     */
                    right.color = BLACK; // 将兄弟节点颜色置黑
                    parent.color = RED; // 将父节点颜色置红
                    rotateLeft(parent); // 将父节点左旋转（当前节点仍然是父节点的左子节点）
                    right = parent.right; // 重新获取当前节点的兄弟节点
                }
                /*
                 * 节点情况分析：
                 * 1、当前节点的兄弟节点一定为黑色节点
                 */
                Node broLeft = right.left, broRight = right.right;
                if ((null == broRight || BLACK == broRight.color) && (null == broLeft || BLACK == broLeft.color)) {
                    // TODO: 当前节点兄弟节点的左右子节点不存在红色节点，适配【场景5】
                    /*
                     * 节点情况分析：
                     * 情况1：当前节点兄弟节点的左右子节点都为黑色节点
                     * 情况2：当前节点兄弟节点的左右子节点都为叶子节点
                     */
                    right.color = RED; // 将兄弟节点颜色置红
                    current = parent; // 将父节点设为当前节点
                } else { // TODO: 当前节点的兄弟节点至少有一个红色子节点，适配【场景3】
                    if (null == broRight || BLACK == broRight.color) {
                        // 兄弟节点的右子节点为叶子节点或者黑色节点，则兄弟节点的左子节点一定为红色节点
                        broLeft.color = BLACK; // 将兄弟节点的左子节点颜色置黑
                        right.color = RED; // 将兄弟节点颜色置红
                        rotateRight(right); // 将兄弟节点右旋转
                        right = parent.right; // 重新获取右子节点
                        broRight = right.right;
                    }
                    right.color = parent.color; // 将兄弟节点的颜色置为父节点的颜色
                    broRight.color = BLACK; // 将兄弟节点的右子节点颜色置黑
                    parent.color = BLACK; // 将父节点颜色置黑
                    rotateLeft(parent); // 将父节点左旋转
                    break;
                }
            } else { // 当前节点为右子节点，这里就不做注释了
                if (RED == left.color) {
                    left.color = BLACK;
                    parent.color = RED;
                    rotateRight(parent);
                    left = parent.left;
                }
                Node broLeft = left.left, broRight = left.right;
                if ((null == broLeft || BLACK == broLeft.color) && (null == broRight || BLACK == broRight.color)) {
                    left.color = RED;
                    current = parent;
                } else {
                    if (null == broLeft || BLACK == broLeft.color) {
                        broRight.color = BLACK;
                        left.color = RED;
                        rotateLeft(left);
                        left = parent.left;
                        broLeft = left.left;
                    }
                    left.color = parent.color;
                    broLeft.color = BLACK;
                    parent.color = BLACK;
                    rotateRight(parent);
                    break;
                }
            }
        }
    }
    
    class Node implements Comparable<Node> {
        K key;
        V value;
        int color;
        Node parent, left, right;
        
        public Node(K key, V value, int color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
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
