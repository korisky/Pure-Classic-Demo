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


    // --- Bucket Node ---

    /**
     * Basic hash bucket node, used for chaining. Implements Map.Entry.
     */
    private static class Node<K, V> implements Map.Entry<K, V> {
        final int hash; // original key.hashCode()
        final K key;
        V value;
        Node<K, V> next; // pointer for linked list (separate chaining)

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

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }


    // --- Constants ---
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INIT_CAPACITY = 16; // power of 2
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // The golden ratio conjugate (~0.618) scaled to 32 bits.
    // Used in Fibonacci hashing: index = (key.hashCode() * CONSTANT) >>> shift.
    private static final int FIBONACCI_HASH_CONSTANT = 0x9e3779b9;


    // --- Instance Variables ---
    transient Node<K, V>[] table; // bucket array
    transient int size; // num of key-value pairs
    transient int modCount; // modification count for fail-fast
    int threshold; // Next size value at which to resize (capacity * load factor)
    final float loadFactor; // Load factor for resizing
    transient int capacityExponent; // exponent 'w' where capacity = 2 ^ w


    // --- Views (cached) ---
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet; // keep transient for default serialization


    // --- Constructors ---
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
        int capacity = tableSizeFor(initialCapacity);
        this.capacityExponent = calculateExponent(capacity);
        this.threshold = calculateThreshold(capacity, loadFactor);
    }

    public FibonacciHashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false); // false, don't check threshold for init
    }


    // --- Hashing & Indexing ---

    /**
     * Calculate original hash code for a key
     */
    static int keyHash(Object key) {
        return (key == null) ? 0 : key.hashCode();
    }

    /**
     * Computes the bucket index for a key, using Fibonacci Hashing
     * index = (keyHash * CONSTANT) >>> (32 - w), where table size = 2^w
     */
    static int fibonacciIndex(int keyHash, int exponent) {
        if (exponent <= 0) {
            // for capacity = 1, shift is 32, resulting in 0
            return 0;
        }
        return (keyHash * FIBONACCI_HASH_CONSTANT) >>> (Integer.SIZE - exponent);
    }

    /**
     * Returns power of 2 size for the given target capacity
     */
    static int tableSizeFor(int cap) {
        // cap-1: if the cap is already power of 2, don't overshot
        // Integer.numberOfLeadingZeros: how many 0 before the highest 1-bit
        // -1: all 1s in unsigned-right-shift, produce a mask (e.g. 00...011111111111)
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return n < 0 ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
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
     * Create table
     */
    @SuppressWarnings({"unchecked"})
    private Node<K, V>[] createTable(int capacity) {
        return (Node<K, V>[]) new Node[capacity];
    }


    // --- Internal Node/Entry Helpers
    final Node<K, V> getNode(Object key) {

    }


    /**
     * Computes key.hashCode and applies Fibonacci Hashing to get Index
     */
    final int hash(Object key) {
        // 1) get the raw 32-bit hashCode
        int h = (key == null) ? 0 : key.hashCode();
        // 2) calculate product = h × A (mod 2^(32))
        // x Golden ratio, then unsigned-right-shift, resulting in 'slot' of the array we use
        return (h * FIBONACCI_HASH_CONSTANT) >>> (Integer.SIZE - capacityExponent);
    }


    // --- Helper Classes ---

    // Base class for iterators
    abstract class HashIterator {
        Node<K, V> nxt;
        Node<K, V> cur;
        int expectModCnt; // fail-fast usage
        int idx;

        // construct according to FibonacciHashMap's param
        HashIterator() {
            expectModCnt = modCount;
            if (table != null && size > 0) {
                // find first not-null node
                for (idx = 0; idx < table.length && (nxt = table[idx]) == null; idx++) ;
            }
        }

        public final boolean hasNext() {
            return nxt != null;
        }

        public final void remove() {
            if (modCount != expectModCnt) {
                throw new ConcurrentModificationException();
            }
            if (cur == null) {
                throw new IllegalStateException();
            }
        }

        final Node<K, V> nextNode() {
            if (modCount != expectModCnt) {
                throw new ConcurrentModificationException();
            }
            if (nxt == null) {
                throw new NoSuchElementException();
            }
            Node<K, V> e = nxt;
            cur = e;
            // find nxt node
            if ((nxt = e.next) == null && table != null) {
                // find first not-null node
                for (idx = 0; idx < table.length && (nxt = table[idx]) == null; idx++) ;
            }
            return e;
        }
    }


    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        // TODO
    }


}
