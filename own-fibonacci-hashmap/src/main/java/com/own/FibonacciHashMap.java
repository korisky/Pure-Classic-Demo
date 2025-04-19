package com.own;

import java.io.Serializable;
import java.util.*;

/**
 * A basic HashMap implementation using Fibonacci Hashing & Separate Chaining
 * Compatible with JDK 17+
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class FibonacciHashMap<K, V> extends AbstractMap<K, V>
        implements Map<K, V>, Cloneable, Serializable {


    /* --- Constants --- */
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INIT_CAPACITY = 16; // power of 2
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int FIBONACCI_HASH_CONSTANT = 0x9e3779b9;


    /* --- Instance Variables --- */
    transient Node<K, V>[] table; // bucket array
    transient int size;
    transient int modCount;
    int threshold;
    final float loadFactor;
    transient int capacityExponent; // exponent 'w' where capacity = 2 ^ w


    /* --- Views (cached) --- */
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet; // keep transient for default serialization


    /* --- Constructors --- */
    public FibonacciHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }


    public FibonacciHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor " + loadFactor);
        }

        this.loadFactor = loadFactor;
    }


    /* --- Hashing (CORE) --- */

    /**
     * Computes key.hashCode and applies Fibonacci Hashing to get Index
     */
    final int hash(Object key) {
        // 1) get the raw 32-bit hashCode
        int h = (key == null) ? 0 : key.hashCode();
        // 2) figure our how many idx-bits we need, is log_2{(table.len)}
        int currentExponent = (table == null) ? calculateExponent(DEFAULT_INIT_CAPACITY) : capacityExponent;
        // corner case: only 1 slot, always result in idx 0
        if (currentExponent == 0) {
            return 0;
        }
        // product = h × A (mod 2^(32))
        // x Golden ratio, then unsigned-right-shift, resulting in 'slot' of the array we use
        return (h * FIBONACCI_HASH_CONSTANT) >>> (Integer.SIZE - currentExponent);
    }


    /**
     * Returns power of 2 size for the given target capacity
     */
    static final int tableSizeFor(int cap) {
        // cap-1: if the cap is already power of 2, don't overshot
        // Integer.numberOfLeadingZeros: how many 0 before the highest 1-bit
        // -1: all 1s in unsigned-right-shift, produce a mask (e.g. 00...011111111111)
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return n < 0 ? 1
                : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Calculates the exponent 'w' for a given power-of-2 capacity
     */
    private static int calculateExponent(int cap) {
        if (cap <= 0) return 0;
        // efficient way to get 'w' where capacity = 2^w
        // e.g. if cap = 16 (2^4, 10000 in bit), log2 = 4
        // Integer.numberOfTrailingZeros() works for power of 2
        return Integer.numberOfTrailingZeros(cap);
    }

    /**
     * Calculate threshold, with overflow prevention
     */
    private int calculateThreshold(int cap, float lf) {
        if (cap >= MAXIMUM_CAPACITY) {
            return Integer.MAX_VALUE;
        }
        // ensure threshold is at least 1, if needed threshold overflow
        return Math.max(1, Math.min((int) (cap * lf), MAXIMUM_CAPACITY + 1));
    }


    /**
     * Node is the inner entry implementation
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public V setValue(V value) {
            V tmp = this.value;
            this.value = value;
            return tmp;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }


        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            // check same type-reference or not
            if (obj instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Entry<?, ?>) obj;
                // for comparing-with-null safety
                return Objects.equals(key, e.getKey())
                        && Objects.equals(value, e.getValue());
            }
            // different type, must not-equal
            return false;
        }
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        new HashMap<>()
        return Set.of();
    }

}
