/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;

/*-[
#include "java/lang/IndexOutOfBoundsException.h"
]-*/

/**
 * A {@code long} array in which elements may be updated atomically.
 * See the {@link java.util.concurrent.atomic} package specification
 * for description of the properties of atomic variables.
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicLongArray implements java.io.Serializable {
    private static final long serialVersionUID = -2308431214976778248L;

    private final long[] array;

    /**
     * Creates a new AtomicLongArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicLongArray(int length) {
        array = new long[length];
    }

    /**
     * Creates a new AtomicLongArray with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicLongArray(long[] array) {
        // Visibility guaranteed by final field guarantees
        this.array = array.clone();
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public final int length() {
        return array.length;
    }

    /*-[
    static void CheckIdx(JavaUtilConcurrentAtomicAtomicLongArray *self, jint i) {
      if (i < 0 || i >= self->array_->size_) {
        @throw [new_JavaLangIndexOutOfBoundsException_initWithNSString_(
            JreStrcat("$I", @"index ", i)) autorelease];
      }
    }

    static inline volatile_jlong *GetPtrUnchecked(
        JavaUtilConcurrentAtomicAtomicLongArray *self, jint i) {
      return (volatile_jlong *)&self->array_->buffer_[i];
    }

    static inline volatile_jlong *GetPtrChecked(
        JavaUtilConcurrentAtomicAtomicLongArray *self, jint i) {
      CheckIdx(self, i);
      return (volatile_jlong *)&self->array_->buffer_[i];
    }
    ]-*/

    /**
     * Gets the current value at position {@code i}.
     *
     * @param i the index
     * @return the current value
     */
    public final native long get(int i) /*-[
      return __c11_atomic_load(GetPtrChecked(self, i), __ATOMIC_SEQ_CST);
    ]-*/;

    private final native long getUnchecked(int i) /*-[
      return __c11_atomic_load(GetPtrUnchecked(self, i), __ATOMIC_SEQ_CST);
    ]-*/;

    /**
     * Sets the element at position {@code i} to the given value.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final native void set(int i, long newValue) /*-[
      __c11_atomic_store(GetPtrChecked(self, i), newValue, __ATOMIC_SEQ_CST);
    ]-*/;

    /**
     * Eventually sets the element at position {@code i} to the given value.
     *
     * @param i the index
     * @param newValue the new value
     * @since 1.6
     */
    public final native void lazySet(int i, long newValue) /*-[
      __c11_atomic_store(GetPtrChecked(self, i), newValue, __ATOMIC_RELEASE);
    ]-*/;

    /**
     * Atomically sets the element at position {@code i} to the given value
     * and returns the old value.
     *
     * @param i the index
     * @param newValue the new value
     * @return the previous value
     */
    public final native long getAndSet(int i, long newValue) /*-[
      return __c11_atomic_exchange(GetPtrChecked(self, i), newValue, __ATOMIC_SEQ_CST);
    ]-*/;

    /**
     * Atomically sets the element at position {@code i} to the given
     * updated value if the current value {@code ==} the expected value.
     *
     * @param i the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final native boolean compareAndSet(int i, long expect, long update) /*-[
      return __c11_atomic_compare_exchange_strong(
          GetPtrChecked(self, i), &expect, update, __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
    ]-*/;

    /**
     * Atomically sets the element at position {@code i} to the given
     * updated value if the current value {@code ==} the expected value.
     *
     * <p><a href="package-summary.html#weakCompareAndSet">May fail
     * spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param i the index
     * @param expect the expected value
     * @param update the new value
     * @return true if successful
     */
    public final native boolean weakCompareAndSet(int i, long expect, long update) /*-[
      return __c11_atomic_compare_exchange_weak(
          GetPtrChecked(self, i), &expect, update, __ATOMIC_RELAXED, __ATOMIC_RELAXED);
    ]-*/;

    /**
     * Atomically increments by one the element at index {@code i}.
     *
     * @param i the index
     * @return the previous value
     */
    public final long getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    /**
     * Atomically decrements by one the element at index {@code i}.
     *
     * @param i the index
     * @return the previous value
     */
    public final long getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the previous value
     */
    public final native long getAndAdd(int i, long delta) /*-[
      return __c11_atomic_fetch_add(GetPtrChecked(self, i), delta, __ATOMIC_SEQ_CST);
    ]-*/;

    /**
     * Atomically increments by one the element at index {@code i}.
     *
     * @param i the index
     * @return the updated value
     */
    public final long incrementAndGet(int i) {
        return addAndGet(i, 1);
    }

    /**
     * Atomically decrements by one the element at index {@code i}.
     *
     * @param i the index
     * @return the updated value
     */
    public final long decrementAndGet(int i) {
        return addAndGet(i, -1);
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the updated value
     */
    public native long addAndGet(int i, long delta) /*-[
      return __c11_atomic_fetch_add(GetPtrChecked(self, i), delta, __ATOMIC_SEQ_CST) + delta;
    ]-*/;

    /**
     * Returns the String representation of the current values of array.
     * @return the String representation of the current values of array
     */
    public String toString() {
        int iMax = array.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(getUnchecked(i));
            if (i == iMax)
                return b.append(']').toString();
            b.append(',').append(' ');
        }
    }

}
