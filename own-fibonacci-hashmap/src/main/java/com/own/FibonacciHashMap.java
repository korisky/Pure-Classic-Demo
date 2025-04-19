package com.own;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        return Set.of();
    }

}
