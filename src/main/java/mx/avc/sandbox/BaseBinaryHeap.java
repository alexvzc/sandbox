/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import java.util.Comparator;

/**
 *
 * @author alexv
 */
public class BaseBinaryHeap {

    public static <T> T replaceTop(T[] heap, Comparator<? super T> comparator,
            int limit, T newItem) {
        T oldItem = heap[0];
        heap[0] = newItem;
        if(limit > 1 && comparator.compare(newItem, oldItem) > 0) {
            siftDown(heap, comparator, limit, 0);
        }

        return oldItem;
    }

    public static <T> void heapify(T[] heap, Comparator<? super T> comparator,
            int limit) {
        for(int index = limit / 2 - 1; index >= 0; index--) {
            siftDown(heap, comparator, limit, index);
        }
    }

    public static <T> void heapify(T[] heap, Comparator<? super T> comparator,
            int limit, int start) {

        if(start == limit - 1) {
            siftUp(heap, comparator, limit, start);
            return;
        }

        int end = limit / 2 - 1;
        start = (start - 1) / 2;
        while(end >= 0) {
            for(int index = end; index >= start; index--) {
                siftDown(heap, comparator, limit, index);
            }

            end = min(start - 1, (end - 1) / 2);
            start = (start - 1) / 2;
        }
    }

    public static <T> void heapsort(T[] heap, Comparator<? super T> comparator,
            int limit) {
        for(int index = limit - 1; index > 0; index--) {
            T item = heap[index];
            heap[index] = heap[0];
            heap[0] = item;

            siftDown(heap, comparator, index, 0);
        }
    }

    public static <T> void splitHeap(T[] tail, Comparator<? super T> comparator,
            int limit, T[] head, int count) {
        Comparator<? super T> reversed = comparator.reversed();
        final int tail_limit = limit - count;

        arraycopy(tail, 0, head, 0, count);
        arraycopy(tail, count, tail, 0, tail_limit);

        heapify(head, reversed, count);
        heapify(tail, comparator, tail_limit);

        T max_head = head[0];
        T min_tail = tail[0];
        while(comparator.compare(min_tail, max_head) < 0) {
            head[0] = min_tail;
            siftDown(head, reversed, count, 0);
            tail[0] = max_head;
            siftDown(tail, comparator, tail_limit, 0);
            max_head = head[0];
            min_tail = tail[0];
        }

        heapify(head, comparator, count);
    }

    public static <T> void siftUp(T[] heap, Comparator<? super T> comparator,
            int limit, int index) {
        if(index > 0) {
            T item = heap[index];

            do {
                int root = (index - 1) / 2;
                T root_item = heap[root];

                if(comparator.compare(root_item, item) < 0) {
                    break;
                }

                heap[root] = item;
                heap[index] = root_item;
                index = root;
            } while(index > 0);
        }
    }

    public static <T> void siftDown(T[] heap, Comparator<? super T> comparator,
            int limit, int index) {
        int left = index * 2 + 1;

        if(left < limit) {
            T item = heap[index];
            int smallest_index = index;

            do {
                T smallest_item = item;

                T left_item = heap[left];
                if(comparator.compare(left_item, smallest_item) < 0) {
                    smallest_index = left;
                    smallest_item = left_item;
                }

                int right = left + 1;
                if(right < limit) {
                    T right_item = heap[right];
                    if(comparator.compare(right_item, smallest_item) < 0) {
                        smallest_index = right;
                        smallest_item = right_item;
                    }
                }

                if(smallest_index == index) {
                    break;
                }

                heap[index] = smallest_item;
                heap[smallest_index] = item;

                index = smallest_index;
                left = index * 2 + 1;

            } while(left < limit);
        }
    }
}
