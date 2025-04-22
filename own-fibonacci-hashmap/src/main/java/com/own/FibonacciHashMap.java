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
     * Static in case external need access
     */
    static class Node<K, V> implements Map.Entry<K, V> {
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
            if (obj instanceof Entry<?, ?> e) {
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


    // --- Internal Node/Entry Helpers

    /**
     * Get(find) a node by key
     */
    final Node<K, V> getNode(Object key) {
        Node<K, V>[] tab = table;
        Node<K, V> firstNode;
        int kh = keyHash(key);
        // check table existence
        if (tab != null && tab.length > 0
                && (firstNode = tab[fibonacciIndex(kh, capacityExponent)]) != null) {
            // 1. check first node in bucket
            if (firstNode.hash == kh && Objects.equals(firstNode.key, key)) {
                return firstNode;
            }
            // 2. check subsequent nodes in the chain
            Node<K, V> p = firstNode.next;
            while (p != null) {
                if (p.hash == kh && Objects.equals(p.key, key)) {
                    return p;
                }
                p = p.next;
            }
        }
        return null; // not found
    }

    /**
     * New node
     */
    final Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
        return new Node<>(hash, key, value, next);
    }

    /**
     * Remove node, return node if found
     */
    final Node<K, V> removeNode(Object key) {
        Node<K, V>[] tab = table;
        Node<K, V> p;
        int index, kh = keyHash(key);

        if (tab != null && tab.length > 0 &&
                (p = tab[index = fibonacciIndex(kh, capacityExponent)]) != null) {
            // 1. try to find the node
            Node<K, V> nodeToRemove = null;
            Node<K, V> prev = null;
            if (p.hash == kh && Objects.equals(p.key, key)) {
                // if the head matches
                nodeToRemove = p;
            } else {
                // search rest of the chain
                prev = p;
                Node<K, V> iter = p.next;
                while (iter != null) {
                    if (iter.hash == kh && Objects.equals(iter.key, key)) {
                        // found, break
                        nodeToRemove = iter;
                        break;
                    }
                    iter = iter.next;
                }
            }
            // 2. remove the node if found
            if (nodeToRemove != null) {
                if (prev == null) {
                    tab[index] = nodeToRemove.next;
                } else {
                    prev.next = nodeToRemove.next;
                }
                modCount++;
                size--;
                afterNodeRemoval(nodeToRemove); // hook for removal
                return nodeToRemove;
            }
        }
        return null; // key not found
    }


    // --- Core Map Functions ---
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = getNode(key);
        return (node == null) ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (table != null && size > 0) {
            // TODO traversing the whole map (might could be enhanced with extra Set / Map)
            for (Node<K, V> kvNode : table) {
                Node<K, V> n = kvNode;
                while (n != null) {
                    if (Objects.equals(n.value, value)) {
                        return true;
                    }
                    n = n.next;
                }
            }
        }
        return false;
    }

    @Override
    public V put(K key, V value) {
        return putVal(key, value, true);
    }

    final V putVal(K key, V value, boolean allowOverwrite) {
        int kh = keyHash(key);

        if (table == null || table.length == 0) {
            table = resize();
        }

        int i = fibonacciIndex(kh, capacityExponent);
        Node<K, V> p = table[i];

        // insertion logic
        if (p == null) {
            // empty bucket
            table[i] = newNode(kh, key, value, null);
        } else {
            // add new node on chain
            Node<K, V> existingNode = null;
            if (p.hash == kh && Objects.equals(p.key, key)) {
                existingNode = p;
            } else {
                Node<K, V> e = p.next;
                while (e != null) {
                    if (e.hash == kh && Objects.equals(e.key, key)) {
                        existingNode = e;
                        break;
                    }
                    e = e.next;
                }
            }
            // if key already exist, update value
            if (existingNode != null) {
                V oldVal = existingNode.value;
                if (allowOverwrite || oldVal == null) {
                    existingNode.value = value;
                }
                afterNodeAccess(existingNode); // hook
                return oldVal;
            }
            // not exist, insertion node to head, point next to current head
            table[i] = newNode(kh, key, value, p);
        }

        // after insertion logic
        modCount++;
        size++;
        if (size > threshold && table.length < MAXIMUM_CAPACITY) {
            resize();
        }
        afterNodeInsertion(table[i]); // hook
        return null; // no previous value
    }

    @Override
    public V remove(Object key) {
        Node<K, V> e = removeNode(key);
        return (e == null) ? null : e.value;
    }

    @Override
    public void clear() {
        modCount++;
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null); // totally removal
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m, true);
    }

    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s <= 0) {
            return;
        }
        // 1) ensure table initialized
        if (table == null) {
            // calculate capacity base on size & loadFactor
            float ft = ((float) s / loadFactor) + 1.0F;
            int t = (ft < (float) MAXIMUM_CAPACITY) ? (int) ft : MAXIMUM_CAPACITY;
            // smallest power of 2 >= t
            int newCap = tableSizeFor(t);
            this.capacityExponent = calculateExponent(newCap);
            this.threshold = calculateThreshold(newCap, loadFactor);
        } else if (s + size > threshold && table.length < MAXIMUM_CAPACITY) {
            // resize existing table if exceed threshold
            resize();
        }
        // 2) add entries from map
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            // putVal, and allow overwrite
            putVal(key, value, true);
        }
    }


    // --- Resizing ---
    final Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;

        if (oldCap > 0) {
            // already contains value
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // double capacity (and threshold, if meet)
            newCap = oldCap << 1;
            if (newCap < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INIT_CAPACITY) {
                // double it only if it doesn't overflow
                int doubledThr = oldThr << 1;
                newThr = (doubledThr > 0) ? doubledThr : Integer.MAX_VALUE;
            }
        } else if (oldThr > 0) {
            // not value, but with threshold
            newCap = oldThr;
        } else {
            // default setting
            newCap = DEFAULT_INIT_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * newCap);
        }

        // calculate threshold if not already set
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY)
                    ? (int) ft
                    : Integer.MAX_VALUE;
//            newThr = calculateThreshold(newCap, loadFactor);
        }

        // update reference
        threshold = newThr;
        capacityExponent = calculateExponent(newCap);
        @SuppressWarnings("{unchecked}")
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;

        // transfer nodes
        if (oldTab != null) {
            for (int j = 0; j < oldCap; j++) {
                Node<K, V> p = oldTab[j];
                // only care non-empty bucket
                if (p != null) {
                    oldTab[j] = null; // help gc

                    // TODO


                    if (p.next == null) {
                        // single node new-table-tapping
                        newTab[fibonacciIndex(p.hash, capacityExponent)] = p;
                    } else {
                        // multiple nodes on the chain
                        // IMP: 由于我们每次都是对大小x2, 这里idx要么在原地, 要么在idx + oldCap
                        Node<K, V> loHead = null, loTail = null;
                        Node<K, V> hiHead = null, hiTail = null;
                        Node<K, V> next;
                        // first old-chain to multiple new-chains
                        do {
                            next = p.next;
                            if ((p.hash & oldCap) == 0) {
                                // stays same index
                                if (loTail == null) {
                                    loHead = p;
                                } else {
                                    loTail.next = p;
                                }
                                loTail = p;
                            } else {
                                // moves to another index
                                if (hiTail == null) {
                                    hiHead = p;
                                } else {
                                    hiTail.next = p;
                                }
                                hiTail = p;
                            }
                        } while ((p = next) != null);
                        // link new low chain to new table at idx j
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // link new high chain to new table at idx j
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }

            }
        }
        return newTab;
    }


    // --- View Functions ---
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet; // cached view
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    @Override
    public Collection<V> values() {
        Collection<V> vs = values;// cached view
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es = entrySet;
        return (es == null) ? (es = new EntrySet()) : es;
    }


    // --- Iterator & View Implementations ---

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

        public final void remove() {
            if (modCount != expectModCnt) {
                throw new ConcurrentModificationException();
            }
            Node<K, V> p = cur;
            if (p == null) {
                throw new IllegalStateException();
            }
            // removing
            cur = null;
            removeNode(p.key);
            expectModCnt = modCount;
        }
    }

    // Concrete iterator implementations
    final class KeyIterator extends HashIterator implements Iterator<K> {
        public K next() {
            return nextNode().key;
        }
    }

    final class ValueIterator extends HashIterator implements Iterator<V> {
        @Override
        public V next() {
            return nextNode().value;
        }
    }

    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>> {
        @Override
        public Map.Entry<K, V> next() {
            return nextNode();
        }
    }

    // Concrete view implementations
    final class KeySet extends AbstractSet<K> {
        @Override
        public int size() {
            return FibonacciHashMap.this.size;
        }

        @Override
        public void clear() {
            FibonacciHashMap.this.clear();
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return removeNode(keySet) != null;
        }
    }

    final class Values extends AbstractCollection<V> {

        @Override
        public int size() {
            return FibonacciHashMap.this.size;
        }

        @Override
        public void clear() {
            FibonacciHashMap.this.clear();
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public int size() {
            return FibonacciHashMap.this.size;
        }

        @Override
        public void clear() {
            FibonacciHashMap.this.clear();
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e)) {
                return false;
            }
            Node<K, V> candidate = getNode(e.getKey());
            return candidate != null && candidate.equals(o); // equals -> check both Key & Value
        }


        @Override
        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                return FibonacciHashMap.this.remove(e.getKey(), e.getValue());
            }
            return false;
        }
    }


    // --- Overwrite for Performance & Clarification ---


    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K, V> node = getNode(key);
        return (node == null) ? defaultValue : node.value;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(key, value, false);
    }

    @Override
    public boolean remove(Object key, Object value) {
        Node<K, V> node = getNode(key);
        if (node != null && Objects.equals(node.value, value)) {
            removeNode(key); // internal remove
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Node<K, V> e = getNode(key);
        if (e != null && Objects.equals(e.value, oldValue)) {
            e.value = newValue;
            afterNodeAccess(e); // hook
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        Node<K, V> e = getNode(key);
        if (e != null) {
            V oldVal = e.value;
            e.value = value;
            afterNodeAccess(e); // hook
            return oldVal;
        }
        return null;
    }

    // --- Hooks for subclasses (like LinkedHashMap) ---
    // These are empty here but allow extension without modifying core logic.
    void afterNodeAccess(Node<K, V> p) {
    }

    void afterNodeInsertion(Node<K, V> p) {
    }

    void afterNodeRemoval(Node<K, V> p) {
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object clone() throws CloneNotSupportedException {
        FibonacciHashMap<K, V> result = (FibonacciHashMap<K, V>) super.clone();
        // reset fields that should not be shared
        result.table = null;
        result.entrySet = null;
        result.keySet = null;
        result.values = null;
        result.modCount = 0;
        result.size = 0;
        // internal state -> copy entries
        result.putMapEntries(this, false);
        return result;
    }
}
