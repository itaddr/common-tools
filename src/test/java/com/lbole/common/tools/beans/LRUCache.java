package com.lbole.common.tools.beans;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author 马嘉祺
 * @Date 2020/10/12 0012 13 46
 * @Description <p></p>
 */
public class LRUCache<K, V> {
    
    private Map<K, Node> map = new HashMap<>();
    
    private List<Node> list = new LinkedList<>();
    
    class Node {
        K key;
        V value;
        Node prev, next;
    }
    
    public V put(K key, V value) {
        return null;
    }
    
}
