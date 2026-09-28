package de.kleinert.vlist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @param <T>
 */
public class VList<T> extends AbstractSequentialList<T> {
    private final @Nullable Segment base;
    private final int offset;

    private VList(final @Nullable Segment seg, final int offset) {
        this(List.of(), seg, offset);
    }

    /**
     *
     */
    private VList() {
        this(List.of());
    }

    /**
     *
     * @param elements
     */
    private VList(final T[] elements) {
        this(Arrays.asList(elements));
    }

    /**
     *
     * @param elements
     */
    public VList(final List<T> elements) {
        this(elements, null, 0);
    }

    private VList(final @NotNull List<T> inputList,
                  final @Nullable Segment segment,
                  int offset) {
        if (inputList.isEmpty()) {
            this.base = segment;
            this.offset = offset;
            return;
        }

        var inputIterator = inputList.listIterator(inputList.size());
        Segment seg;

        if (segment == null) {
            var elems = new Object[]{inputIterator.previous()};
            seg = new Segment(null, elems);
            offset = 0;
        } else if (offset == 0) {
            seg = segment;
        } else {
            assert (offset > 0);
            seg = new Segment(segment.next, segment.elements);
        }

        while (true) {
            while (inputIterator.hasPrevious() && offset > 0) {
                offset--;
                seg.elements[offset] = inputIterator.previous();
            }
            if (!inputIterator.hasPrevious())
                break;
            var elements = new Object[seg.elements.length * 2];
            offset = elements.length;
            seg = new Segment(seg, elements);
        }

        this.base = seg;
        this.offset = offset;
    }

    public static <T> VList<T> empty() {
        return new VList<>();
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
        return listIntoSegments(elements, base, offset);
    }

    public VList<T> append(final T element) {
        return appendAll(List.of(element));
    }

    public VList<T> appendAll(final @NotNull List<T> elements) {
        return listToVList(elements).prepend(this);
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
     * @return The list without the first element.
     */
    public VList<T> tail() {
        if (base == null) return this;
        if (offset == base.elements.length) {
            if (base.next == null) return empty();
            else return new VList<>(base.next, 0);
        }
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
            final @Nullable Segment segment,
            int offset) {
        return new VList<>(inputList, segment, offset);
    }

    /**
     *
     * @param inputList
     * @param <T>
     * @return
     */
    public static <T> VList<T> listToVList(final @NotNull List<T> inputList) {
        if (inputList instanceof VList<?>) return (VList<T>) inputList;
        if (inputList.isEmpty()) return empty();
        return listIntoSegments(inputList, null, 0);
    }

    @SafeVarargs
    public static <T> VList<T> of(T... elements) {
        if (elements.length == 0) return empty();
        return new VList<>(elements);
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

    public List<T> toList() {
        var list = new ArrayList<T>();
        var offset = this.offset;
        var segment = base;
        while (segment != null) {
            for (int i = offset; i < segment.elements.length; i++) {
                list.add((T) segment.elements[i]);
            }
            offset = 0;
            segment = segment.next;
        }
        return Collections.unmodifiableList(list);
    }

    /**
     *
     * @param i
     * @return
     */
    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
//        return new VListListIterator<>(base, offset, i);
        return toList().listIterator(i);
    }
}
