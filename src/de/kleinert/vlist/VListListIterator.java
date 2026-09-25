package de.kleinert.vlist;

import org.jetbrains.annotations.Nullable;

import java.util.ListIterator;
import java.util.NoSuchElementException;

class VListListIterator<T> implements ListIterator<T> {
    private final @Nullable Segment base;
    private final int originOffset;

    private int i;
    private @Nullable Segment segment;
    private int offset;

    VListListIterator(@Nullable Segment base, int offset, int i) {
        this.base = base;
        this.originOffset = offset;
        advance(i);
    }

    @Override
    public boolean hasNext() {
        return segment != null;
    }

    @Override
    public T next() {
        if (segment == null) throw new NoSuchElementException();
        var temp = segment.elements[offset];
        offset++;
        i++;
        if (offset == segment.elements.length) {
            offset = 0;
            segment = segment.next;
        }
        //noinspection unchecked
        return (T) temp;
    }

    @Override
    public boolean hasPrevious() {
        return i != 0;
    }

    @Override
    public T previous() {
        if (i == 0) throw new NoSuchElementException();

        if (offset > 0) {
            assert segment != null;
            //noinspection unchecked
            return (T) segment.elements[offset];
        }

        // We are at the beginning of a segment and thus need to go to the previous segment.
        // Since VList segments are singly-linked, we need to iterate the list from the beginning up to the index.
        i--;
        advance(i);
        return next();
    }

    private void advance(int index) {
        segment = base;
        offset = originOffset;
        while (index > 0 && segment != null) {
            index--;

            offset++;
            i++;
            if (offset == segment.elements.length) {
                offset = 0;
                segment = segment.next;
            }
        }
    }

    @Override
    public int nextIndex() {
        return i;
    }

    @Override
    public int previousIndex() {
        return i - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }
}
