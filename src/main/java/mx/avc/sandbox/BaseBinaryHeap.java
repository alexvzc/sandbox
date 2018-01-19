/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import static java.lang.Math.min;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author alexv
 */
public interface BaseBinaryHeap {

    public static <T> T replaceTop(List<T> heap,
            Comparator<? super T> comparator, T newItem) {
        T oldItem = heap.set(0, newItem);
        if(heap.size() > 1 && comparator.compare(newItem, oldItem) > 0) {
            siftDown(heap, comparator, 0);
        }

        return oldItem;
    }

    public static <T> void heapify(List<T> heap,
            Comparator<? super T> comparator) {
        for(int index = heap.size() / 2 - 1; index >= 0; index--) {
            siftDown(heap, comparator, index);
        }
    }

    public static <T> void heapify(List<T> heap,
            Comparator<? super T> comparator, int start) {
        int limit = heap.size();

        if(start == limit - 1) {
            siftUp(heap, comparator, start);
            return;
        }

        int end = limit / 2 - 1;
        start = (start - 1) / 2;
        while(end >= 0) {
            for(int index = end; index >= start; index--) {
                siftDown(heap, comparator, index);
            }

            end = min(start - 1, (end - 1) / 2);
            start = (start - 1) / 2;
        }
    }

    public static <T> void splitHeap(List<T> tail,
            Comparator<? super T> comparator, List<T> head, int count) {
        Comparator<? super T> reversed = comparator.reversed();

        head.clear();
        List<T> head_range = tail.subList(0, count);
        head.addAll(head_range);
        head_range.clear();

        heapify(head, reversed);
        heapify(tail, comparator);

        T max_head = head.get(0);
        T min_tail = tail.get(0);
        while(comparator.compare(min_tail, max_head) < 0) {
            head.set(0, min_tail);
            siftDown(head, reversed, 0);
            tail.set(0, max_head);
            siftDown(tail, comparator, 0);
            max_head = head.get(0);
            min_tail = tail.get(0);
        }

        heapify(head, comparator);
    }

    public static <T> void siftUp(List<T> heap,
            Comparator<? super T> comparator, int index) {
        if(index > 0) {
            T item = heap.get(index);

            do {
                int root = (index - 1) / 2;
                T root_item = heap.get(root);

                if(comparator.compare(root_item, item) < 0) {
                    break;
                }

                heap.set(root, item);
                heap.set(index, root_item);
                index = root;
            } while(index > 0);
        }
    }

    public static <T> void siftDown(List<T> heap,
            Comparator<? super T> comparator, int index) {
        int limit = heap.size();
        int left = index * 2 + 1;

        if(left < limit) {
            T item = heap.get(index);
            int smallest_index = index;

            do {
                T smallest_item = item;

                T left_item = heap.get(left);
                if(comparator.compare(left_item, smallest_item) < 0) {
                    smallest_index = left;
                    smallest_item = left_item;
                }

                int right = left + 1;
                if(right < limit) {
                    T right_item = heap.get(right);
                    if(comparator.compare(right_item, smallest_item) < 0) {
                        smallest_index = right;
                        smallest_item = right_item;
                    }
                }

                if(smallest_index == index) {
                    break;
                }

                heap.set(index, smallest_item);
                heap.set(smallest_index, item);

                index = smallest_index;
                left = index * 2 + 1;

            } while(left < limit);
        }
    }
}
