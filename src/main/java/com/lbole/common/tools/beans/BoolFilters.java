package com.lbole.common.tools.beans;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author 马嘉祺
 * @Date 2020/12/9 0009 17 43
 * @Description <p></p>
 */
public class BoolFilters {
    
    private final int capacity;
    
    private final Bool[] bools;
    
    public BoolFilters(int capacity) {
        int n = capacity - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        this.capacity = n + 1;
        this.bools = new Bool[capacity];
        for (int i = 0; i < capacity; ++i) {
            bools[i] = new Bool();
        }
    }
    
    public boolean get(int hash) {
        int idx = hash & capacity;
        final Bool bool = bools[idx];
        final Lock lock = bool.read;
        lock.lock();
        try {
            return bool.exists;
        } finally {
            lock.unlock();
        }
    }
    
    public void put(int hash) {
        int idx = hash & capacity;
        final Bool bool = bools[idx];
        final Lock lock = bool.write;
        lock.lock();
        try {
        
        } finally {
            lock.unlock();
        }
    }
    
    public void del(int hash) {
        int idx = hash & capacity;
        final Bool bool = bools[idx];
        final Lock lock = bool.write;
        lock.lock();
        try {
        
        } finally {
            lock.unlock();
        }
    }
    
    class Bool {
        
        private boolean exists;
        
        private int counter;
        
        private ReadWriteLock lock;
        
        private Lock read, write;
        
        public Bool() {
            this.exists = false;
            this.counter = 0;
            this.lock = new ReentrantReadWriteLock();
            this.read = lock.readLock();
            this.write = lock.writeLock();
        }
        
    }
    
}
