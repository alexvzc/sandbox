/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import java.security.SecureRandom;
import java.util.Arrays;
import static java.util.Comparator.naturalOrder;
import static mx.avc.sandbox.BaseBinaryHeap.heapify;
import static mx.avc.sandbox.BaseBinaryHeap.splitHeap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author alexv
 */
public class BenchmarkBinaryHeapTest {

    static final int TEST_VALUE_SIZE = 1 << 17;
    static final int SPLIT_INDEX = TEST_VALUE_SIZE >> 1;
    static final Integer[] TEST_VALUES;

    static {
        SecureRandom random = new SecureRandom("TEST_SEED".getBytes());

        TEST_VALUES = new Integer[TEST_VALUE_SIZE];
        Arrays.setAll(TEST_VALUES, i -> random.nextInt(TEST_VALUE_SIZE << 2) + 1);
        heapify(TEST_VALUES, naturalOrder(), TEST_VALUE_SIZE);
    }

    Integer[] test_values = new Integer[TEST_VALUE_SIZE];
    Integer[] head = new Integer[TEST_VALUE_SIZE];

    @Rule
    public BenchmarkRule rule = new BenchmarkRule();

    @Before
    public void setup() {
        System.arraycopy(TEST_VALUES, 0, test_values, 0, TEST_VALUE_SIZE);
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1000)
    public void testSplitHeap() {
        splitHeap(test_values, naturalOrder(), TEST_VALUE_SIZE,
                head, SPLIT_INDEX);
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1000)
    public void testSort() {
        Arrays.sort(test_values, naturalOrder());

        System.arraycopy(test_values, 0, head, 0, SPLIT_INDEX);
        System.arraycopy(test_values, SPLIT_INDEX, test_values, 0,
                TEST_VALUE_SIZE - SPLIT_INDEX);
    }
}
