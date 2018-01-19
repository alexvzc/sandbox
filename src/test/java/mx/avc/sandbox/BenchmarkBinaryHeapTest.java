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
import java.util.ArrayList;
import static java.util.Comparator.naturalOrder;
import java.util.List;
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
    static final List<Integer> TEST_VALUES;

    static {
        SecureRandom random = new SecureRandom("TEST_SEED".getBytes());

        TEST_VALUES = new ArrayList<>(TEST_VALUE_SIZE);
        random.ints(TEST_VALUE_SIZE, 1, TEST_VALUE_SIZE << 2)
                .forEach(TEST_VALUES::add);
        heapify(TEST_VALUES, naturalOrder());
    }

    List<Integer> test_values = new ArrayList<>(TEST_VALUE_SIZE);
    List<Integer> head = new ArrayList<>(TEST_VALUE_SIZE);

    @Rule
    public BenchmarkRule rule = new BenchmarkRule();

    @Before
    public void setup() {
        test_values.clear();
        test_values.addAll(TEST_VALUES);
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1000)
    public void testSplitHeap() {
        splitHeap(test_values, naturalOrder(), head, SPLIT_INDEX);
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 1000)
    public void testSort() {
        test_values.sort(naturalOrder());

        head.clear();
        List<Integer> head_range = test_values.subList(0, SPLIT_INDEX);
        head.addAll(head_range);
        head_range.clear();
    }
}
