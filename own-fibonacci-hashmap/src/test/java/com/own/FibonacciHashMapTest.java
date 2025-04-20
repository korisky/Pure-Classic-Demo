package com.own;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FibonacciHashMapTest {

    @Test
    public void mainFuncTest() {

        FibonacciHashMap<Object, Object> map = new FibonacciHashMap<>(4, 0.75f);
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3); // now reach threshold: 4 * 0.75 = 3
        assertTrue("Not contain key", map.containsKey("Three"));
        assertTrue("Not contain value", map.containsValue(2));
        System.out.println("Before resize: " + map.size + ", capacity: " + map.table.length);

        map.put("Four", 4);
        assertTrue("Not contain key", map.containsKey("Three"));
        assertTrue("Not contain key", map.containsKey("Four"));
        System.out.println("After resize: " + map.size + ", capacity: " + map.table.length);

        map.remove("Three");
        assertFalse("Still contains key", map.containsKey("Three"));
        assertFalse("Still contains value", map.containsValue(3));

        System.out.println("\nRemaining Entries:");
        map.put("Five", 5);
        map.put("Six", 6);
        map.put("Seven", 7);
        for (Map.Entry<Object, Object> entry : map.entrySet) {
            System.out.println("Key: " + entry.getKey() + ", Val: " + entry.getValue());
        }
    }
}
