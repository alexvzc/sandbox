/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import static java.util.Comparator.naturalOrder;
import java.util.Optional;
import java.util.function.Supplier;
import static mx.avc.sandbox.BaseBinaryHeap.heapify;
import static mx.avc.sandbox.BaseBinaryHeap.replaceTop;
import static mx.avc.sandbox.BaseBinaryHeap.siftUp;
import static mx.avc.sandbox.BaseBinaryHeap.splitHeap;

/**
 * Implements a min-heap.
 * @author alexv
 */
public class BinaryHeap<T> implements Heap<T> {

    private static final int INITIAL_CAPACITY = 15;

    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    private static final Object[] DEFAULT_HEAP = new Object[] {};

    /**
     * The heap implemented as an array.
     */
    private T[] heap;

    /**
     * The used space within the heap.
     */
    private int size;

    /**
     * The {@link java.util.Comparator} used to order the items in the heap.
     */
    private final Comparator<T> comparator;

    /**
     * Constructs an empty heap.
     * @param c the comparator used to sort the heap items.
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap(Comparator<T> c) {
        heap = (T[])DEFAULT_HEAP;
        size = 0;
        comparator = c;
    }

    /**
     * Raw constructs a heap.
     */
    BinaryHeap(T[] h, int s, Comparator<T> c) {
        heap = h;
        size = s;
        comparator = c;
    }

    /**
     * Constructs an empty heap.
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap() {
        this((Comparator<T>)naturalOrder());
    }

    /**
     * Constructs a pre-populated heap.
     * @param c the comparator used to sort the heap items.
     * @param initial the items used to populate the heap.
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap(Comparator<T> c, Collection<? extends T> initial) {
        heap = (T[])initial.toArray();
        size = heap.length;
        comparator = c;

        if(size > 1) {
            heapify(heap, comparator, size);
        }
    }

    /**
     * Constructs a pre-populated heap.
     * @param initial the items used to populate the heap.
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap(Collection<? extends T> initial) {
        this((Comparator<T>)naturalOrder(), initial);
    }

    /**
     * Retrieve the top most item of the heap without removing it.
     * @return the top-most item in the heap; {@link java.util.Optional#empty()}
     *          if the heap is empty
     */
    @Override
    public Optional<T> peek() {
        return size == 0 ? Optional.empty() : Optional.of(heap[0]);
    }

    /**
     * Retrieve and extract the top most item of the heap.
     * @return the top-most item in the heap; {@link java.util.Optional#empty()}
     *          if the heap is empty
     */
    @Override
    public Optional<T> pop() {
        if(size == 0) {
            return Optional.empty();
        }

        if(size == 1) {
            T top = heap[0];
            heap[0] = null;
            size = 0;
            return Optional.of(top);
        }

        T bottom = heap[--size];
        heap[size] = null;
        return Optional.of(replaceTop(heap, comparator, size, bottom));
    }

    /**
     * Retrieve and extract the top most items of the heap.
     * @param count the amount of items to "pop" from the top of the heap
     * @return a list of all the top-most items requested.
     */
    @Override
    @SuppressWarnings("unchecked")
    public BinaryHeap<T> pop(int count) {
        if(count < 0) {
            throw new IllegalArgumentException();
        }

        if(count == 0 || size == 0) {
            return new BinaryHeap<>(comparator);
        }

        if(count >= size) {
            T[] head = heap;
            int head_size = size;
            heap = (T[])DEFAULT_HEAP;
            size = 0;
            return new BinaryHeap<>(head, head_size, comparator);
        }

        if(count == 1) {
            T bottom = heap[--size];
            heap[size] = null;
            T top = replaceTop(heap, comparator, size, bottom);
            T[] head = (T[])new Object[] { top };
            return new BinaryHeap<>(head, 1, comparator);
        }

        T[] head = (T[])new Object[count];
        splitHeap(heap, comparator, size, head, count);
        size = size - count;
        return new BinaryHeap<>(head, count, comparator);
    }

    /**
     * Updates the top-most element's value.
     * @param value a supplier for the new value of the top most item; the value
     * is not retrieved if the heap is empty.
     * @return the old value of the top most item is the heap wasn't empty
     */
    @Override
    public Optional<T> update(Supplier<T> value) {
        return size == 0 ? Optional.empty() :
                Optional.of(replaceTop(heap, comparator, size, value.get()));
    }

    /**
     * Adds an item to the heap.
     * @param value the item to add
     */
    @Override
    public void add(T value) {
        ensureExtraCapacity(1);
        int index = size++;
        heap[index] = value;
        siftUp(heap, comparator, size, index);
    }

    /**
     * Adds an item to the heap.
     * @param values the items to add
     */
    @Override
    public void addAll(Collection<? extends T> values) {
        @SuppressWarnings("unchecked")
        T[] v = (T[])values.toArray();
        ensureExtraCapacity(v.length);
        int index = size;
        System.arraycopy(v, 0, heap, size, v.length);
        size = size + v.length;
        heapify(heap, comparator, size, index);
    }

    /**
     * Indicates if the heap is empty.
     * @return true if empty
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the heap
     * @return the items count
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all the items from the heap
     */
    @Override
    public void clear() {
        if(size > 0) {
            Arrays.fill(heap, 0, size, null);
            heap = (T[])DEFAULT_HEAP;
            size = 0;
        }
    }

    /**
     * Trims array storage to fit only current items.
     */
    public void trimToSize() {
        if(size < heap.length) {
            T[] oldheap = heap;
            heap = Arrays.copyOf(heap, size);
            Arrays.fill(oldheap, 0, size, null);
        }
    }

    private void ensureExtraCapacity(int excess) {
        int newCapacity = size + excess;
        if(newCapacity > heap.length) {
            newCapacity = Math.min(MAX_CAPACITY, Math.max(INITIAL_CAPACITY,
                            computeNewCapacity(newCapacity)));
            T[] oldheap = heap;
            heap = Arrays.copyOf(heap, newCapacity);
            Arrays.fill(oldheap, 0, size, null);
        }
    }

    private static int computeNewCapacity(int capacity) {
        int v = capacity;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return capacity | v >> 2;
    }
}
