package com.lbole.common.tools.beans.tree;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author 马嘉祺
 * @Date 2020/9/25 0025 10 44
 * @Description <p></p>
 */
public class BSTreeTest {
    
    @Test
    public void put() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        BSTree.Entry<Integer, String> root = tree.getRoot();
        System.out.printf("rootKey=%d, rootValue=%s, size=%d, isBSTree=%b\n", root.getKey(), root.getValue(), tree.size(), tree.isBSTree(null));
    }
    
    @Test
    public void remove() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        //
        while (tree.size() > 0) {
            tree.remove(tree.getRoot().getKey());
            System.out.printf("size=%d, isBSTree=%b\n", tree.size(), tree.isBSTree(null));
        }
        
    }
    
    @Test
    public void find() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        System.out.println(tree.find(random.nextInt(100000) % 100));
    }
    
    @Test
    public void findMin() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        System.out.println(tree.findMin());
    }
    
    @Test
    public void findMax() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        System.out.println(tree.findMax());
    }
    
    @Test
    public void foreach() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BSTree<Integer, String> tree = new BSTree<>();
        for (int i = 0; i < 100; ++i) {
            int key = (random.nextInt(100000) & Integer.MAX_VALUE) % 100;
            tree.put(key, String.valueOf(key));
        }
        BSTree.Entry<Integer, String> root = tree.getRoot();
        System.out.printf("rootKey=%d, rootValue=%s, size=%d, isBSTree=%b\n", root.getKey(), root.getValue(), tree.size(), tree.isBSTree(null));
        System.out.println(tree.findMin().getValue() + " " + tree.findMax().getValue());
        tree.forEach(System.out::println);
    }
    
}