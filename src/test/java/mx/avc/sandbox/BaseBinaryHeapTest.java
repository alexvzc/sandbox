/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.avc.sandbox;

import static java.lang.System.arraycopy;
import java.util.Arrays;
import java.util.Comparator;
import static java.util.Comparator.naturalOrder;
import static mx.avc.sandbox.BaseBinaryHeap.heapify;
import static mx.avc.sandbox.BaseBinaryHeap.heapsort;
import static mx.avc.sandbox.BaseBinaryHeap.replaceTop;
import static mx.avc.sandbox.BaseBinaryHeap.splitHeap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class BaseBinaryHeapTest {
    private static final Logger LOGGER = getLogger(BaseBinaryHeapTest.class);

    private static final Integer[] TEST_VALUES = new Integer[] {
        34, 33, 58, 36, 54, 37, 7, 51, 21, 24, 49, 13, 20, 43, 6, 30, 56, 31,
        60, 23, 15, 61, 25, 11, 47, 17, 62, 22, 44, 38, 45, 48, 27, 3, 9, 28,
        65, 57, 2, 10, 32, 39, 64, 46, 8, 40, 26, 35, 55, 41, 4, 14, 29, 42,
        59, 5, 53, 18, 50, 63, 19, 1, 52, 16, 12
    };

    public static <T> void assertHeapConsistency(T[] heap,
            Comparator<? super T> comparator, int limit) {
        for(int i = 1; i < limit; i++) {
            T entry = heap[i];
            T root = heap[(i - 1) / 2];
            assertTrue(comparator.compare(root, entry) <= 0);
        }
    }

    public static <T> void assertArrayEquals(T[] expected, int expected_base,
            T[] actual, int actual_base, int length) {
        for(int i = 0; i < length; i++) {
            assertEquals(expected[expected_base + i], actual[actual_base + i]);
        }
    }

    public static <T> void assertArrayLessThan(T[] head, int head_limit,
            T[] tail, int tail_limit, Comparator<? super T> comparator) {
        T head_max = head[0];
        for(int index = 1; index < head_limit; index++) {
            T item = head[index];
            if(comparator.compare(head_max, item) < 0) {
                head_max = item;
            }
        }
        for(int index = 0; index < tail_limit; index++) {
            if(comparator.compare(head_max, tail[index]) > 0) {
                fail();
            }
        }
    }

    @Test
    public void testHeapify() {
        LOGGER.info("Testing heapify()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.length;
        Integer[] test_values = new Integer[max_length];

        final int max_base = max_length - 1;
        for(int base = 0; base < max_base; base++) {
            final int max_limit = max_length - base;
            for(int limit = 1; limit <= max_limit; limit++) {
                arraycopy(TEST_VALUES, base, test_values, 0, limit);
                heapify(test_values, natural_order, limit);
                assertHeapConsistency(test_values, natural_order, limit);
            }
        }
   }

    @Test
    public void testHeapifyPartial() {
        LOGGER.info("Testing heapify() - partial");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.length;
        Integer[] test_values = new Integer[max_length];

        final int max_base = max_length - 2;
        for(int base = 0; base < max_base; base++) {
            final int max_limit = max_length - base;
            for(int limit = 2; limit <= max_limit; limit++) {
                final int max_index = limit - 1;
                for(int index = 1; index <= max_index; index++) {
                    arraycopy(TEST_VALUES, base, test_values, 0, limit);
                    heapify(test_values, natural_order, index);
                    assertHeapConsistency(test_values, natural_order, index);
                    heapify(test_values, natural_order, limit, index);
                    assertHeapConsistency(test_values, natural_order, limit);
                }
            }
        }
    }

    @Test
    public void testHeapsort() {
        LOGGER.info("Testing heapsort()");

        Comparator<Integer> natural_order = naturalOrder();
        Comparator<Integer> reverse_order = natural_order.reversed();
        final int max_length = TEST_VALUES.length;
        Integer[] test_values = new Integer[max_length];
        Integer[] expected = new Integer[max_length];

        final int max_base = max_length - 2;
        for(int base = 0; base < max_base; base++) {
            final int max_limit = max_length - base;

            for(int limit = 2; limit <= max_limit; limit++) {
                arraycopy(TEST_VALUES, base, test_values, 0, limit);
                arraycopy(TEST_VALUES, base, expected, 0, limit);
                Arrays.sort(expected, 0, limit, reverse_order);

                heapify(test_values, natural_order, limit);
                assertHeapConsistency(test_values, natural_order, limit);
                heapsort(test_values, natural_order, limit);
                assertArrayEquals(expected, 0, test_values, 0, limit);
            }
        }
    }

    @Test
    public void testSplitHeap() {
        LOGGER.info("Testing splitHeap()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.length;
        Integer[] test_values = new Integer[max_length];
        Integer[] head = new Integer[max_length];

        final int max_base = max_length - 2;
        for(int base = 0; base < max_base; base++) {
            final int max_limit = max_length - base;
            arraycopy(TEST_VALUES, base, test_values, 0, max_limit);

            for(int limit = 2; limit <= max_limit; limit++) {
                final int max_index = limit - 1;
                for(int index = 1; index <= max_index; index++) {
                    splitHeap(test_values, natural_order, limit, head,  index);
                    assertHeapConsistency(head, natural_order, index);
                    assertHeapConsistency(test_values, natural_order,
                            limit - index);
                    assertArrayLessThan(head, index, test_values, limit - index,
                            natural_order);
                }
            }
        }
    }

    @Test
    public void testReplaceTop() {
        LOGGER.info("Testing replaceTop()");

        Comparator<Integer> natural_order = naturalOrder();
        final int max_length = TEST_VALUES.length;
        Integer[] test_values = new Integer[max_length];

        final int max_base = max_length - 1;
        for(int base = 0; base < max_base; base++) {
            final int max_limit = max_length - base;
            for(int limit = 1; limit <= max_limit; limit++) {
                for(int i = 0; i < max_length; i++) {
                    arraycopy(TEST_VALUES, base, test_values, 0, limit);
                    heapify(test_values, natural_order, limit);
                    replaceTop(test_values, natural_order, limit, TEST_VALUES[i]);
                    assertHeapConsistency(test_values, natural_order, limit);

                }
            }
        }
   }}
