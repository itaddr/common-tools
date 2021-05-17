package com.lbole.common.tools.beans.tree;

import java.util.Iterator;
import java.util.Objects;

/**
 * @Author 马嘉祺
 * @Date 2020/10/5 0005 15 07
 * @Description <p></p>
 */
public class SortedLinked<E extends Comparable<E>> implements Iterable<E> {
    
    /*链表的头和尾结点，不存储数据只作为标记*/
    private final Node first, last;
    private int size;
    
    public SortedLinked() {
        this.first = new Node(null, null, new Node());
        (this.last = first.next).prev = first;
    }
    
    /**
     * 获取链表长度
     *
     * @return
     */
    public int size() {
        return size;
    }
    
    /**
     * 获取链表头结点节点
     *
     * @return
     */
    public E peekFirst() {
        Node node = first.next;
        return last == node ? null : node.value;
    }
    
    /**
     * 获取链表尾结点节点
     *
     * @return
     */
    public E peekLast() {
        Node node = last.prev;
        return first == node ? null : node.value;
    }
    
    /**
     * 删除链表头结点节点
     *
     * @return
     */
    public E removeFirst() {
        Node first = this.first, node = first.next;
        if (last != node) {
            (first.next = node.next).prev = first;
            node.prev = node.next = null;
            --size;
        }
        return node.value;
    }
    
    /**
     * 删除链表尾结点节点
     *
     * @return
     */
    public E removeLast() {
        Node last = this.last, node = last.prev;
        if (first != node) {
            (last.prev = node.prev).next = last;
            node.prev = node.next = null;
            --size;
        }
        return node.value;
    }
    
    /**
     * 向链表中插入节点
     *
     * @param value
     */
    public void add(E value) {
        Node first = this.first, node = first.next, last = this.last;
        if (null == value) {
            if (last == node || null != node.value) {
                first.next = first.next.prev = new Node(null, first, first.next);
                ++size;
            }
        } else {
            int compare = -1;
            while (last != node && (compare = value.compareTo(node.value)) < 0) {
                node = node.next;
            }
            if (0 == compare) {
                node.value = value;
            } else {
                node.next = node.next.prev = new Node(value, node, node.next);
                ++size;
            }
        }
    }
    
    /**
     * 判断链表中是否包含某节点
     *
     * @param value
     * @return
     */
    public boolean contains(E value) {
        for (Node node = first.next, last = this.last; last != node; node = node.next) {
            if (Objects.equals(value, node.value)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 替换链表中的节点
     *
     * @param source
     * @param target
     */
    public boolean replace(E source, E target) {
        for (Node node = first.next, last = this.last; last != node; node = node.next) {
            if (Objects.equals(source, node.value)) {
                node.value = target;
                return true;
            }
        }
        return false;
    }
    
    /**
     * 替换链表中的节点
     *
     * @param source
     * @param target
     */
    public void replaceAll(E source, E target) {
        for (Node node = first.next, last = this.last; last != node; node = node.next) {
            if (Objects.equals(source, node.value)) {
                node.value = target;
            }
        }
    }
    
    /**
     * 删除链表中的节点
     *
     * @param value
     */
    public boolean remove(E value) {
        for (Node prev = first, node = prev.next, last = this.last; last != node; node = (prev = node).next) {
            if (Objects.equals(value, node.value)) {
                (prev.next = node.next).prev = prev;
                node.prev = node.next = null;
                --size;
                return true;
            }
        }
        return false;
    }
    
    /**
     * 删除链表中的节点
     *
     * @param value
     */
    public void removeAll(E value) {
        for (Node prev = first, node = prev.next, last = this.last; last != node; node = (prev = node).next) {
            if (Objects.equals(value, node.value)) {
                (prev.next = node.next).prev = prev;
                node.prev = node.next = null;
                node = prev;
                --size;
            }
        }
    }
    
    /**
     * 创建迭代器
     *
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node current = SortedLinked.this.first;
            
            @Override
            public boolean hasNext() {
                return SortedLinked.this.last != current.next;
            }
            
            @Override
            public E next() {
                return (current = current.next).value;
            }
        };
    }
    
    class Node {
        E value;
        Node prev, next;
        
        public Node() {
        }
        
        public Node(E value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
    
}
