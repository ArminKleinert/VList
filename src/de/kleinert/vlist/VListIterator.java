package de.kleinert.vlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

class VListIterator<T> implements Iterator<T> {
    private int offset;
    private Segment segment;

    VListIterator(int offset, Segment segment) {
        this.offset = offset;
        this.segment = segment;
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
        if (offset == segment.elements.length) {
            offset = 0;
            segment = segment.next;
        }
        //noinspection unchecked
        return (T) temp;
    }
}
