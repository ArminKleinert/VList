package de.kleinert.vlist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @param <T>
 */
public class VList<T> extends AbstractList<T> {
    private final @Nullable Segment base;
    private final int offset;

    private VList(final @Nullable Segment seg, final int offset) {
        this.base = seg;
        this.offset = offset;
    }

    /**
     *
     */
    public VList() {
        this(List.of());
    }

    /**
     *
     * @param elements
     */
    public VList(final T[] elements) {
        this(Arrays.asList(elements));
    }

    /**
     *
     * @param elements
     */
    public VList(final List<T> elements) {
        if (elements.isEmpty()) {
            this.base = null;
            this.offset = 0;
            return;
        }

        var inputIterator = elements.listIterator(elements.size());
        var seg = new Segment(null, new Object[1]);
        var offset = 0;

        while (true) {
            while (inputIterator.hasPrevious() && offset >= 0) {
                seg.elements[offset] = inputIterator.previous();
                offset--;
            }
            if (!inputIterator.hasPrevious()) break;
            var segElements = new Object[seg.elements.length * 2];
            offset = segElements.length - 1;
            seg = new Segment(seg, segElements);
        }

        base = seg;
        this.offset = offset + 1;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new VListIterator<>(offset, base);
    }

    /**
     * <pre>
     *     size([])                          = 0
     *     size([segmentN, segmentN-1, ...]) = size(segmentN)*2 - 1 - offset
     * </pre>
     *
     * @return
     */
    @Override
    public int size() {
        return base == null
                ? 0
                : base.elements.length * 2 - 1 - offset;
    }

    /**
     * <pre>
     *     cons([], e)              = [[e]]
     *     cons([fullSegment, ...]) = [[e], fullSegment, ...]
     *     cons([segmentN, ...])    = [[e & segmentN], ...]
     * </pre>
     *
     * @param element
     * @return
     */
    public VList<T> cons(final T element) {
        if (offset > 0) {
            assert base != null;
            var newSegmentElements = Arrays.copyOf(base.elements, base.elements.length);
            newSegmentElements[offset - 1] = element;
            return new VList<>(new Segment(base.next, newSegmentElements), offset - 1);
        }

        var newSegmentElements = new Object[base == null ? 1 : base.elements.length << 1];
        var off = newSegmentElements.length - 1;
        newSegmentElements[off] = element;
        return new VList<>(new Segment(base, newSegmentElements), off);
    }

    /**
     * The same as a repeated application of the {@link cons} operation.
     *
     * @param elements
     * @return
     */
    public VList<T> prepend(final @NotNull List<T> elements) {
        return listIntoSegments(elements, base, offset - 1);
    }

    /**
     * The list without the first element.
     * <pre>
     *     tail([])                     = []
     *     tail([[e]])                  = []
     *     tail([[e], segmentN-1, ...]) = [segmentN-1, ...]
     *     tail([[eN, eN-1, ...], ...]) = [[eN-1, ...], ...]
     * </pre>
     *
     * @return
     */
    public VList<T> tail() {
        if (base == null) return this;
        if (offset == base.elements.length) return new VList<>(base.next, 0);
        return new VList<>(base, offset + 1);
    }

    /**
     *
     * @param index
     * @return
     */
    @Override
    public T get(final int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException("Index: " + index + "; Size: " + size());

        var i = index + offset;
        var segment = base;
        while (segment != null) {
            if (i < segment.elements.length) {
                //noinspection unchecked
                return (T) segment.elements[i];
            }
            i -= segment.elements.length;
            segment = segment.next;
        }

        throw new IllegalStateException("Impossible state: Index " + index + " out of bounds.");
    }

    private static <T> VList<T> listIntoSegments(
            final @NotNull List<T> inputList,
            final @Nullable Segment mutableSegment,
            int offset) {
        if (inputList.isEmpty())
            return new VList<>(null, 0);

        var inputIterator = inputList.listIterator(inputList.size());
        var seg = mutableSegment == null ? new Segment(null, new Object[1]) : mutableSegment;

        while (true) {
            while (inputIterator.hasPrevious() && offset >= 0) {
                seg.elements[offset] = inputIterator.previous();
                offset--;
            }
            if (!inputIterator.hasPrevious()) break;
            var elements = new Object[seg.elements.length * 2];
            offset = elements.length - 1;
            seg = new Segment(seg, elements);
        }

        return new VList<>(seg, offset + 1);
    }

    private <T> VList<T> listIntoSegments1(
            final @NotNull List<T> inputList,
            final @Nullable Segment mutableSegment,
            int offset) {
        if (inputList.isEmpty()) {
            return new VList<>(null, 0);
        }

        var inputIterator = inputList.listIterator(inputList.size());
        var seg = mutableSegment == null ? new Segment(null, new Object[1]) : mutableSegment;

        while (true) {
            while (inputIterator.hasPrevious() && offset >= 0) {
                seg.elements[offset] = inputIterator.previous();
                offset--;
            }
            if (!inputIterator.hasPrevious()) break;
            var elements = new Object[seg.elements.length * 2];
            offset = elements.length - 1;
            seg = new Segment(seg, elements);
        }

        return new VList<>(seg, offset + 1);
    }

    /**
     *
     * @param inputList
     * @param <T>
     * @return
     */
    public static <T> VList<T> listToVList(final @NotNull List<T> inputList) {
        return listIntoSegments(inputList, null, 0);
    }

    /**
     *
     * @return
     */
    public @NotNull List<@NotNull List<T>> getSegments() {
        var res = new ArrayList<List<T>>();
        var seg = base;
        while (seg != null) {
            //noinspection unchecked
            res.add((List<T>) Arrays.asList(seg.elements));
            seg = seg.next;
        }
        return Collections.unmodifiableList(res);
    }

    /**
     *
     * @param i
     * @return
     */
    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
        return new VListListIterator<>(base, offset, i);
    }
}
