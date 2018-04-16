/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import java.util.Optional;
import static mx.avc.sandbox.BaseListBinaryHeap.heapify;
import static mx.avc.sandbox.BaseListBinaryHeap.replaceTop;
import static mx.avc.sandbox.BaseListBinaryHeap.reverseHeapify;
import static mx.avc.sandbox.BaseListBinaryHeap.splitHeap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class BaseListBinaryHeapTest {
    private static final Logger LOGGER = getLogger(BaseListBinaryHeapTest.class);

    private static final List<Integer> TEST_VALUES = Arrays.asList(
        34, 33, 58, 36, 54, 37, 7, 51, 21, 24, 49, 13, 20, 43, 6, 30, 56, 31,
        60, 23, 15, 61, 25, 11, 47, 17, 62, 22, 44, 38, 45, 48, 27, 3, 9, 28,
        65, 57, 2, 10, 32, 39, 64, 46, 8, 40, 26, 35, 55, 41, 4, 14, 29, 42,
        59, 5, 53, 18, 50, 63, 19, 1, 52, 16, 12
    );

    public static <T> void assertHeapConsistency(List<T> heap,
            Comparator<? super T> comparator) {
        int limit = heap.size();
        for(int i = 1; i < limit; i++) {
            assertTrue(comparator.compare(
                    heap.get((i - 1) / 2), heap.get(i)) <= 0);
        }
    }

    public static <T> void assertReverseHeapConsistency(List<T> heap,
            Comparator<? super T> comparator) {
        int limit = heap.size();
        for(int i = limit - 2; i >= 0; i--) {
            assertTrue(comparator.compare(heap.get((limit + i + 1) / 2),
                    heap.get(i)) >= 0);
        }
    }

    public static <T> void assertArrayLessThan(List<T> head, List<T> tail,
            Comparator<? super T> comparator) {
        Optional<T> gotcha = head.stream().max(comparator)
                .flatMap(head_max -> tail.stream()
                        .filter(item -> comparator.compare(head_max, item) > 0)
                        .findAny());
        assertFalse(gotcha.isPresent());
    }

    @Test
    public void testHeapify() {
        LOGGER.info("Testing heapify()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.size();
        List<Integer> test_values = new ArrayList<>(max_length);

        final int max_base = max_length - 1;
        for(int base = 0; base < max_base; base++) {
            for(int limit = base + 1; limit <= max_length; limit++) {
                test_values.clear();
                test_values.addAll(TEST_VALUES.subList(base, limit));
                heapify(test_values, natural_order);
                assertHeapConsistency(test_values, natural_order);
            }
        }
   }

    @Test
    public void testHeapifyPartial() {
        LOGGER.info("Testing heapify() - partial");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.size();
        List<Integer> test_values = new ArrayList<>(max_length);

        final int max_base = max_length - 2;
        for(int base = 0; base < max_base; base++) {
            for(int limit = base + 2; limit <= max_length; limit++) {
                final int max_index = limit - base;
                for(int index = 1; index < max_index; index++) {
                    test_values.clear();
                    test_values.addAll(TEST_VALUES.subList(base, limit));
                    heapify(test_values.subList(0, index), natural_order);
                    assertHeapConsistency(test_values.subList(0, index),
                            natural_order);
                    heapify(test_values, natural_order, index);
                    assertHeapConsistency(test_values, natural_order);
                }
            }
        }
    }

    @Test
    public void testReverseHeapify() {
        LOGGER.info("Testing reverseHeapify()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.size();
        List<Integer> test_values = new ArrayList<>(max_length);

        final int max_base = max_length - 1;
        for(int base = 0; base < max_base; base++) {
            for(int limit = base + 1; limit <= max_length; limit++) {
                test_values.clear();
                test_values.addAll(TEST_VALUES.subList(base, limit));
                reverseHeapify(test_values, natural_order);
                assertReverseHeapConsistency(test_values, natural_order);
            }
        }
   }

    @Test
    public void testSplitHeap() {
        LOGGER.info("Testing splitHeap()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.size();
        List<Integer> test_values = new ArrayList<>(max_length);
        List<Integer> head = new ArrayList<>(max_length);

        final int max_base = max_length - 2;
        for(int base = 0; base < max_base; base++) {

            for(int limit = 2; limit <= max_length; limit++) {
                final int max_index = limit - base;
                for(int index = 1; index < max_index; index++) {
                    test_values.clear();
                    test_values.addAll(TEST_VALUES.subList(base, limit));

                    splitHeap(test_values, natural_order, head, index);
                    assertHeapConsistency(head, natural_order);
                    assertHeapConsistency(test_values, natural_order);
                    assertArrayLessThan(head, test_values, natural_order);
                }
            }
        }
    }

    @Test
    public void testReplaceTop() {
        LOGGER.info("Testing replaceTop()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.size();
        List<Integer> test_values = new ArrayList<>(max_length);

        final int max_base = max_length - 1;
        for(int base = 0; base < max_base; base++) {
            for(int limit = base + 1; limit <= max_length; limit++) {
                for(int i = 0; i < max_length; i++) {
                    test_values.clear();
                    test_values.addAll(TEST_VALUES.subList(base, limit));
                    heapify(test_values, natural_order);
                    replaceTop(test_values, natural_order, TEST_VALUES.get(i));
                    assertHeapConsistency(test_values, natural_order);
                }
            }
        }
   }
}
