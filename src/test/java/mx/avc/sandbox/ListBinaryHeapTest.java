/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Comparator;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import static mx.avc.sandbox.TestUtils.getFieldValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class ListBinaryHeapTest {
    private static final Logger LOGGER = getLogger(ListBinaryHeapTest.class);

    private static final List<Integer> TEST_VALUES = asList(
        34, 33, 58, 36, 54, 37, 7, 51, 21, 24, 49, 13, 20, 43, 6, 30, 56, 31,
        60, 23, 15, 61, 25, 11, 47, 17, 62, 22, 44, 38, 45, 48, 27, 3, 9, 28,
        65, 57, 2, 10, 32, 39, 64, 46, 8, 40, 26, 35, 55, 41, 4, 14, 29, 42,
        59, 5, 53, 18, 50, 63, 19, 1, 52, 16, 12
    );

    private static final List<Integer> SORTED_TEST_VALUES;

    static {
        SORTED_TEST_VALUES = asList(
                TEST_VALUES.toArray(new Integer[TEST_VALUES.size()]));
        SORTED_TEST_VALUES.sort(naturalOrder());
    }

    private static <T> void assertHeapConsistency(ListBinaryHeap<T> heap) {

        List<T> h = getFieldValue(heap, "heap");
        Comparator<T> comparator = getFieldValue(heap, "comparator");

        int size = h.size();
        for(int i = 1; i < size; i++) {
            T entry = h.get(i);
            T root = h.get((i - 1) / 2);
            assertTrue(comparator.compare(root, entry) <= 0);
        }
    }


    @Test
    public void testPopEmpty() {
        LOGGER.info("Testing pop() - empty");
        ListBinaryHeap<Entry<String, Integer>> heap = new ListBinaryHeap<>();

        Optional<Entry<String, Integer>> top = heap.pop();

        assertFalse(top.isPresent());
    }

    @Test
    public void testPopOne() {
        LOGGER.info("Testing pop() - one");
        Integer TEST_VALUE = 1;

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();
        heap.add(TEST_VALUE);

        Optional<Integer> top = heap.pop();
        assertHeapConsistency(heap);

        assertTrue(top.isPresent());
        assertEquals(TEST_VALUE, top.get());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testPopAll() {
        LOGGER.info("Testing pop() - all");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        SORTED_TEST_VALUES.forEach(e -> {
            Optional<Integer> item = heap.pop();
            assertHeapConsistency(heap);
            assertTrue(item.isPresent());
            assertEquals(e, item.get());
        });

        assertTrue(heap.isEmpty());
    }

    @Test
    public void testPopSeveralEmpty() {
        LOGGER.info("Testing pop() - several/empty");
        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();

        ListBinaryHeap<Integer> top = heap.pop(10);
        assertHeapConsistency(heap);
        assertHeapConsistency(top);

        assertTrue(top.isEmpty());
    }

    @Test
    public void testPopSeveralOne() {
        LOGGER.info("Testing pop() - several/one");
        Integer TEST_VALUE = 1;

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(asList(1));

        ListBinaryHeap<Integer> top = heap.pop(10);
        assertHeapConsistency(heap);
        assertHeapConsistency(top);

        assertFalse(top.isEmpty());
        assertEquals(TEST_VALUE, top.pop().get());
        assertTrue(top.isEmpty());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testPopSeveralAll() {
        LOGGER.info("Testing pop() - several/all");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        ListBinaryHeap<Integer> top = heap.pop(TEST_VALUES.size());
        assertHeapConsistency(heap);
        assertHeapConsistency(top);

        assertFalse(top.isEmpty());
        assertTrue(heap.isEmpty());

        SORTED_TEST_VALUES.forEach(e -> assertEquals(e, top.pop().get()));

        assertTrue(top.isEmpty());
    }

    @Test
    public void testPopSeveralNotAll() {
        LOGGER.info("Testing pop() - several/not all");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        ListBinaryHeap<Integer> top = heap.pop(35);
        assertHeapConsistency(heap);
        assertHeapConsistency(top);

        assertFalse(top.isEmpty());
        assertFalse(heap.isEmpty());

        SORTED_TEST_VALUES.subList(0, 35).forEach(e ->
                assertEquals(e, top.pop().get()));
        SORTED_TEST_VALUES.subList(35, SORTED_TEST_VALUES.size())
                .forEach(e -> assertEquals(e, heap.pop().get()));

        assertTrue(top.isEmpty());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testPopNone() {
        LOGGER.info("Testing pop() - none");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        ListBinaryHeap<Integer> top = heap.pop(0);
        assertHeapConsistency(heap);
        assertHeapConsistency(top);

        assertTrue(top.isEmpty());
        assertFalse(heap.isEmpty());

        SORTED_TEST_VALUES.forEach(e -> assertEquals(e, heap.pop().get()));

        assertTrue(top.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopInvalid() {
        LOGGER.info("Testing pop() - invalid");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        heap.pop(-1);
    }

    @Test
    public void testPopComprehensive() {
        LOGGER.info("Testing pop(int) - comprehensive");

        for(int i = TEST_VALUES.size(); i > 0 ; i--) {
            for(int j = i - 1; j > 0; j--) {

                List<Integer> VALUES = TEST_VALUES.subList(0, i);
                List<Integer> SORTED_VALUES = new ArrayList<>(VALUES);
                Collections.sort(SORTED_VALUES);

                ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(VALUES);

                ListBinaryHeap<Integer> top = heap.pop(j);
                assertHeapConsistency(heap);
                assertHeapConsistency(top);

                SORTED_VALUES.subList(0, j)
                        .forEach(e -> assertEquals(e, top.pop().get()));
                SORTED_VALUES.subList(j, i)
                        .forEach(e -> assertEquals(e, heap.pop().get()));

                assertTrue(heap.isEmpty());
                assertTrue(top.isEmpty());
            }
        }
    }

    @Test
    public void testPeekEmpty() {
        LOGGER.info("Testing peek() - empty");
        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();

        Optional<Integer> top = heap.peek();

        assertFalse(top.isPresent());
    }

    @Test
    public void testPeekOne() {
        LOGGER.info("Testing peek() - one");
        Integer TEST_VALUE = 1;

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();
        heap.add(TEST_VALUE);

        Optional<Integer> top = heap.peek();

        assertTrue(top.isPresent());
        assertEquals(TEST_VALUE, top.get());
        assertFalse(heap.isEmpty());
    }

    @Test
    public void testUpdateEmpty() {
        LOGGER.info("Testing update() - empty");
        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();

        Optional<Integer> result = heap.update(() -> {
            throw new AssertionError();
        });
        assertHeapConsistency(heap);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateOne() {
        LOGGER.info("Testing update() - one");
        Integer TEST_VALUE = 1;

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(asList(TEST_VALUE));

        Optional<Integer> prev = heap.update(() -> 100);
        assertHeapConsistency(heap);
        Optional<Integer> result = heap.peek();

        assertTrue(prev.isPresent());
        assertEquals(TEST_VALUE, prev.get());
        assertTrue(result.isPresent());
        assertEquals(100, (int)result.get());
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
    }

    @Test
    public void testUpdateRelocateBottom() {
        LOGGER.info("Testing update() - relocate item to the bottom");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);
        Integer EXPECTED = 100;

        Optional<Integer> prev = heap.update(() -> EXPECTED);
        assertHeapConsistency(heap);
        assertTrue(prev.isPresent());
        assertEquals(SORTED_TEST_VALUES.get(0), prev.get());

        SORTED_TEST_VALUES.subList(1, SORTED_TEST_VALUES.size())
                .forEach(e -> assertEquals(e, heap.pop().get()));
        assertEquals(EXPECTED, heap.pop().get());
        assertTrue(heap.isEmpty());

    }

    @Test
    public void testUpdateKeepOnTop() {
        LOGGER.info("Testing update() - keep item to top");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        Integer EXPECTED = 0;
        Optional<Integer> prev = heap.update(() -> EXPECTED);
        assertHeapConsistency(heap);
        assertTrue(prev.isPresent());
        assertEquals(SORTED_TEST_VALUES.get(0), prev.get());

        assertEquals(EXPECTED, heap.pop().get());
        SORTED_TEST_VALUES.subList(1, SORTED_TEST_VALUES.size())
                .forEach(e -> assertEquals(e, heap.pop().get()));
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testAddInEmptyHeap() {
        LOGGER.info("Testing add() - in empty heap");
        Integer TEST_VALUE = 1;

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();

        heap.add(TEST_VALUE);
        assertHeapConsistency(heap);

        Optional<Integer> result = heap.pop();
        assertTrue(result.isPresent());
        assertEquals(TEST_VALUE, result.get());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testAddAllComprehensive() {
        LOGGER.info("Testing addAll() - comprehensive");
        for(int i = TEST_VALUES.size() - 1; i > 0 ; i--) {
            for(int j = i + 1; j <= TEST_VALUES.size(); j++) {
                List<Integer> INITIAL = TEST_VALUES.subList(0, i);
                List<Integer> TRAILING = TEST_VALUES.subList(i, j);

                ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(INITIAL);
                assertHeapConsistency(heap);

                heap.addAll(TRAILING);
                assertHeapConsistency(heap);
            }
        }
    }

    @Test
    public void testAddInPopulatedHeapTop() {
        LOGGER.info("Testing add() - in populated heap to the top");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);
        Integer EXPECTED = 0;

        heap.add(EXPECTED);
        assertHeapConsistency(heap);

        Optional<Integer> result = heap.pop();
        assertTrue(result.isPresent());
        assertEquals(EXPECTED, result.get());

        SORTED_TEST_VALUES.forEach(e -> assertEquals(e, heap.pop().get()));
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testAddInPopulatedHeapBottom() {
        LOGGER.info("Testing add() - in populated heap");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);
        Integer EXPECTED = 100;

        heap.add(EXPECTED);
        assertHeapConsistency(heap);

        SORTED_TEST_VALUES.forEach(e -> assertEquals(e, heap.pop().get()));
        Optional<Integer> result = heap.pop();
        assertTrue(result.isPresent());
        assertEquals(EXPECTED, result.get());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testClear() {
        LOGGER.info("Testing clear()");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>(TEST_VALUES);

        heap.clear();
        assertHeapConsistency(heap);
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testClearEmpty() {
        LOGGER.info("Testing clear() - empty");

        ListBinaryHeap<Integer> heap = new ListBinaryHeap<>();

        heap.clear();
        assertHeapConsistency(heap);
        assertTrue(heap.isEmpty());
    }
}
