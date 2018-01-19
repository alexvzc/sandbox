/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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
public class HeapTest {
    private static final Logger LOGGER = getLogger(HeapTest.class);

    public static final List<Integer> TEST_ITEMS = unmodifiableList(asList(
        34, 33, 58, 36, 54, 37, 7, 51, 21, 24, 49, 13, 20, 43, 6, 30, 56, 31,
        60, 23, 15, 61, 25, 11, 47, 17, 62, 22, 44, 38, 45, 48, 27, 3, 9, 28,
        65, 57, 2, 10, 32, 39, 64, 46, 8, 40, 26, 35, 55, 41, 4, 14, 29, 42,
        59, 5, 53, 18, 50, 63, 19, 1, 52, 16, 12
    ));

    public static final List<Integer> SORTED_ITEMS;

    static {
        List<Integer> sorted_items  = asList(
                TEST_ITEMS.toArray(new Integer[TEST_ITEMS.size()]));
        sorted_items.sort(naturalOrder());
        SORTED_ITEMS = unmodifiableList(sorted_items);
    }

    public static <T> T fail() {
        throw new AssertionError();
    }

    static class DelegatedHeap<T> implements Heap<T> {
        public final Heap<T> delegate;

        public DelegatedHeap(Heap<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Optional<T> peek() {
            return delegate.peek();
        }

        @Override
        public Optional<T> pop() {
            return delegate.pop();
        }

        @Override
        public Heap<T> pop(int count) {
            return delegate.pop(count);
        }


        @Override
        public Optional<T> update(Supplier<T> value) {
            return delegate.update(value);
        }


        @Override
        public void add(T value) {
            delegate.add(value);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public void clear() {
            delegate.clear();
        }
    }

    @Test
    public void testAddAll() {
        LOGGER.info("Testing Head.addAll() ");
        Heap<Integer> heap = new DelegatedHeap<>(new BinaryHeap<>());

        heap.addAll(TEST_ITEMS);

        assertFalse(heap.isEmpty());
        SORTED_ITEMS.forEach(i ->
                assertEquals(i, heap.pop().orElseGet(HeapTest::fail)));
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testIsEmptyEmpty() {
        LOGGER.info("Testing HeapMap.isEmpty() - empty heap");
        Heap<Integer> heap = new DelegatedHeap<>(new BinaryHeap<>());

        boolean result = heap.isEmpty();

        assertTrue(result);
    }

    @Test
    public void testIsEmptyNonEmpty() {
        LOGGER.info("Testing HeapMap.isEmpty() - non empty");
        Heap<Integer> heap = new DelegatedHeap<>(new BinaryHeap<>(TEST_ITEMS));

        boolean result = heap.isEmpty();

        assertFalse(result);
    }
}
