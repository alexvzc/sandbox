/*
 * Copyright (C) 2018 Alejandro Vazquez

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package mx.avc.sandbox;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements a min-heap.
 * @author alexv
 */
public interface Heap<T> {

    /**
     * Retrieve the top most item of the heap without removing it.
     * @return the top-most item in the heap; {@link java.util.Optional#empty()}
     *          if the heap is empty
     */
    public Optional<T> peek();

    /**
     * Retrieve and extract the top most item of the heap.
     * @return the top-most item in the heap; {@link java.util.Optional#empty()}
     *          if the heap is empty
     */
    public Optional<T> pop();

    /**
     * Retrieve and extract the top most items of the heap.
     * @param count the amount of items to "pop" from the top of the heap
     * @return a list of all the top-most items requested.
     */
    public Heap<T> pop(int count);

    /**
     * Updates the top-most element's value.
     * @param value a supplier for the new value of the top most item; the value
     * is not retrieved if the heap is empty.
     * @return the old value of the top most item is the heap wasn't empty
     */
    public Optional<T> update(Supplier<T> value);

    /**
     * Adds an item to the heap.
     * @param value the item to add
     */
    public void add(T value);

    /**
     * Returns the number of items in the heap
     * @return the items count
     */
    public int size();

    /**
     * Removes all the items from the heap
     */
    public void clear();

    /**
     * Indicates if the heap is empty.
     * @return true if empty
     */
    default public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Adds an item to the heap.
     * @param values the items to add
     */
    default public void addAll(Collection<? extends T> values) {
        Objects.requireNonNull(values);
        values.forEach(this::add);
    }
}
