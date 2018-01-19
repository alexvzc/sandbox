/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.util.ArrayList;
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

    /**
     * The heap implemented as an array.
     */
    private final ArrayList<T> heap;
    /**
     * The {@link java.util.Comparator} used to order the items in the heap.
     */
    private final Comparator<T> comparator;

    /**
     * Constructs an empty heap.
     * @param c the comparator used to sort the heap items.
     */
    public BinaryHeap(Comparator<T> c) {
        heap = new ArrayList<>();
        comparator = c;
    }

    /**
     * Raw constructs a heap.
     */
    BinaryHeap(ArrayList<T> h, Comparator<T> c) {
        heap = h;
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
     * Constructs a prepopulated heap.
     * @param c the comparator used to sort the heap items.
     * @param initial the items used to populate the heap.
     */
    public BinaryHeap(Comparator<T> c, Collection<? extends T> initial) {
        this(c);

        heap.addAll(initial);

        if(heap.size() > 1) {
            heapify(heap, comparator);
        }
    }

    /**
     * Constructs a prepopulated heap.
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
        return heap.isEmpty() ? Optional.empty() : Optional.of(heap.get(0));
    }

    /**
     * Retrieve and extract the top most item of the heap.
     * @return the top-most item in the heap; {@link java.util.Optional#empty()}
     *          if the heap is empty
     */
    @Override
    public Optional<T> pop() {
        if(heap.isEmpty()) {
            return Optional.empty();
        }

        int size = heap.size();
        if(size == 1) {
            return Optional.of(heap.remove(0));
        }

        return Optional.of(replaceTop(heap, comparator, heap.remove(size - 1)));
    }

    /**
     * Retrieve and extract the top most items of the heap.
     * @param count the amount of items to "pop" from the top of the heap
     * @return a list of all the top-most items requested.
     */
    @Override
    public BinaryHeap<T> pop(int count) {
        if(count < 0) {
            throw new IllegalArgumentException();
        }

        if(count == 0 || heap.isEmpty()) {
            return new BinaryHeap<>(comparator);
        }

        int size = heap.size();

        if(count >= size) {
            ArrayList<T> head = new ArrayList<>(heap);
            heap.clear();
            return new BinaryHeap<>(head, comparator);
        }

        if(count == 1) {
            ArrayList<T> head = new ArrayList<>();
            head.add(replaceTop(heap, comparator, heap.remove(size - 1)));
            return new BinaryHeap<>(head, comparator);
        }

        ArrayList<T> head = new ArrayList<>(count);
        splitHeap(heap, comparator, head, count);
        return new BinaryHeap<>(head, comparator);
    }

    /**
     * Updates the top-most element's value.
     * @param value a supplier for the new value of the top most item; the value
     * is not retrieved if the heap is empty.
     * @return the old value of the top most item is the heap wasn't empty
     */
    @Override
    public Optional<T> update(Supplier<T> value) {
        return heap.isEmpty() ? Optional.empty() :
                Optional.of(replaceTop(heap, comparator, value.get()));
    }

    /**
     * Adds an item to the heap.
     * @param value the item to add
     */
    @Override
    public void add(T value) {
        int index = heap.size();
        heap.add(value);
        siftUp(heap, comparator, index);
    }

    /**
     * Adds an item to the heap.
     * @param values the items to add
     */
    @Override
    public void addAll(Collection<? extends T> values) {
        int index = heap.size();
        heap.addAll(values);
        heapify(heap, comparator, index);
    }

    /**
     * Indicates if the heap is empty.
     * @return true if empty
     */
    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Returns the number of items in the heap.
     * @return the items count
     */
    @Override
    public int size() {
        return heap.size();
    }

    /**
     * Removes all the items from the heap.
     */
    @Override
    public void clear() {
        heap.clear();
    }

    /**
     * Trims array storage to fit only current items.
     */
    public void trimToSize() {
        heap.trimToSize();
    }
}
