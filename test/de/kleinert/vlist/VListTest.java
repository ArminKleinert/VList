package de.kleinert.vlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

class VListTest {
    @Test
    void of() {
        // Size is predecessor of a power of 2 (offset == 0)
        var vl3 = VList.of(1, 2, 3);
        Assertions.assertEquals(3, vl3.size());
        Assertions.assertEquals(List.of(1, 2, 3), vl3);

        // Size is not predecessor of a power of 2 (offset != 0)
        var vl2 = VList.of(1, 2);
        Assertions.assertEquals(2, vl2.size());
        Assertions.assertEquals(List.of(1, 2), vl2);

        // Empty (obviously)
        var vlEmpty = VList.of();
        Assertions.assertEquals(0, vlEmpty.size());
        Assertions.assertEquals(List.of(), vlEmpty);
    }

    @Test
    void size() {
        Assertions.assertEquals(0, VList.of().size());
        Assertions.assertEquals(0, VList.listToVList(List.of()).size());

        Assertions.assertEquals(2, VList.of(1, 2).size());
        Assertions.assertEquals(2, VList.listToVList(List.of(1, 2)).size());

        Assertions.assertEquals(3, VList.of(1, 2, 3).size());
        Assertions.assertEquals(3, VList.listToVList(List.of(1, 2, 3)).size());

        // Test that prepending and removing doesn't change size of the original.
        var vl = VList.of(1, 2, 3);
        Assertions.assertEquals(4, vl.prepend(List.of(4)).size());
        Assertions.assertEquals(3, vl.size());
        Assertions.assertEquals(5, vl.prepend(List.of(4, 5)).size());
        Assertions.assertEquals(3, vl.size());
        Assertions.assertEquals(1, vl.tail().tail().size());
        Assertions.assertEquals(3, vl.size());
    }

    @Test
    void get() {
        // Size is predecessor of a power of 2 (offset == 0)
        var list15 = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        var vlist15 = VList.listToVList(list15);

        // Forwards get(...) check
        for (int i = 0; i < list15.size(); i++) {
            Assertions.assertEquals(list15.get(i), vlist15.get(i));
        }

        // Backwards get(...) check
        for (int i = list15.size() - 1; i >= 0; i--) {
            Assertions.assertEquals(list15.get(i), vlist15.get(i));
        }

        // Size is not predecessor of a power of 2 (offset != 0)

        var list12 = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        var vlist12 = VList.listToVList(list12);

        // Forwards get(...) check
        for (int i = 0; i < list12.size(); i++) {
            Assertions.assertEquals(list12.get(i), vlist12.get(i));
        }

        // Backwards get(...) check
        for (int i = list12.size() - 1; i >= 0; i--) {
            Assertions.assertEquals(list12.get(i), vlist12.get(i));
        }
    }

    @Test
    void getOutOfBounds() {
        // Size is predecessor of a power of 2 (offset == 0)
        var vlist15 = VList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlist15.get(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlist15.get(vlist15.size()));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> VList.of().get(0));

        // Size is not predecessor of a power of 2 (offset != 0)
        var vlist12 = VList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlist12.get(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlist12.get(vlist12.size()));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> VList.of().get(0));
    }

    @Test
    void equalityToVList() {
        Assertions.assertEquals(VList.of(), VList.of());
        Assertions.assertEquals(VList.of(1), VList.of(1));
        Assertions.assertEquals(VList.of(1, 2), VList.of(1, 2));

        Assertions.assertNotEquals(VList.of(1), VList.of());
        Assertions.assertNotEquals(VList.of(), VList.of(1));
    }

    @Test
    void equalityToOtherLists() {
        Assertions.assertEquals(List.of(), VList.of());
        Assertions.assertEquals(List.of(1), VList.of(1));
        Assertions.assertEquals(List.of(1, 2), VList.of(1, 2));

        Assertions.assertNotEquals(List.of(1), VList.of());
        Assertions.assertNotEquals(List.of(), VList.of(1));
    }

    @Test
    void cons() {
        // General test; Order of insertion
        Assertions.assertEquals(List.of(1, 2, 3), VList.of().cons(3).cons(2).cons(1));

        // Test immutability
        var vlist = VList.of().cons(3).cons(2);
        Assertions.assertEquals(List.of(2, 3), vlist);
        vlist.cons(1);
        Assertions.assertEquals(List.of(2, 3), vlist);
    }

    @Test
    void prepend() {
        Assertions.assertEquals(
                List.of(1, 2, 3), VList.of().prepend(List.of(1, 2, 3)));
        Assertions.assertEquals(
                List.of(3, 4, 5), VList.of(4, 5).prepend(List.of(3)));
        Assertions.assertEquals(
                List.of(3, 4, 5), VList.of(5).prepend(List.of(3, 4)));
        Assertions.assertEquals(
                List.of(1, 2, 3, 4, 5), VList.of(3, 4, 5).prepend(List.of(1, 2)));
        Assertions.assertEquals(
                List.of(3, 4, 5), VList.of(3, 4, 5).prepend(List.of()));
    }

    @Test
    void append() {
        Assertions.assertEquals(
                List.of(1, 4), VList.of(1).append(4));
        Assertions.assertEquals(
                List.of(1, 2, 3, 4, 5), VList.of(1, 2, 3).append(4).append(5));
    }

    @Test
    void appendAll() {
        Assertions.assertEquals(
                List.of(1, 2, 3), VList.of(1, 2, 3).appendAll(List.of()));
        Assertions.assertEquals(
                List.of(1, 2, 3, 4, 5), VList.of(1, 2, 3).appendAll(List.of(4, 5)));
    }

    @Test
    void stream() {
        // General test; Order of insertion
        Assertions.assertEquals(
                List.of(1, 2, 3),
                VList.of(1, 3, 3, 3, 3, 3, 2, 2, 4).stream()
                        .sorted().distinct()
                        .limit(3)
                        .collect(Collectors.toUnmodifiableList()));
    }

    @Test
    void toArray() {
        Assertions.assertArrayEquals(
                new Object[]{},
                VList.of().toArray());
        Assertions.assertArrayEquals(
                new Integer[]{},
                VList.<Integer>of().toArray(new Integer[0]));
        Assertions.assertArrayEquals(
                new Object[]{1, 2, 3},
                VList.of(1, 2, 3).toArray());
        Assertions.assertArrayEquals(
                new Integer[]{1, 2, 3},
                VList.of(1, 2, 3).toArray(new Integer[0]));
    }

    @Test
    void containsAll() {
        Assertions.assertTrue(VList.of().containsAll(List.of()));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.<Integer>of()));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.of(1, 3)));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.of(1, 2, 3)));

        Assertions.assertFalse(VList.of().containsAll(List.of(1, 2)));// Empty list contains nothing
        Assertions.assertFalse(VList.of(1, 2, 3).containsAll(List.of(4, 5))); // No common subset
        Assertions.assertFalse(VList.of(1, 2, 3).containsAll(List.of(1, 2, 3, 4))); // Common subset, but not full subset
    }

    @Test
    void isEmpty() {
        Assertions.assertTrue(VList.empty().isEmpty());
        Assertions.assertTrue(VList.of().isEmpty());
        Assertions.assertTrue(VList.listToVList(List.of()).isEmpty());

        Assertions.assertFalse(VList.of(1, 2, 3).isEmpty());
        Assertions.assertFalse(VList.listToVList(List.of(1, 2, 3)).isEmpty());
    }

    @Test
    void contains() {
        Assertions.assertTrue(VList.of(1, 2, 3).contains(1));
        Assertions.assertTrue(VList.of(1, 2, 3).contains(3));
        Assertions.assertTrue(VList.of(1, 2, 3, 4).contains(1));
        Assertions.assertTrue(VList.of(1, 2, 3, 4).contains(3));

        Assertions.assertFalse(VList.of().contains(1));
        Assertions.assertFalse(VList.of(1, 2, 3).contains(4));
    }

    @Test
    void indexOf() {
        Assertions.assertEquals(0, VList.of(1, 2, 3).indexOf(1));
        Assertions.assertEquals(2, VList.of(1, 2, 3).indexOf(3));
        Assertions.assertEquals(0, VList.of(1, 2, 3, 4).indexOf(1));
        Assertions.assertEquals(2, VList.of(1, 2, 3, 4).indexOf(3));

        Assertions.assertEquals(0, VList.of(1, 2, 2, 1).indexOf(1));
        Assertions.assertEquals(1, VList.of(1, 2, 2, 1).indexOf(2));

        Assertions.assertEquals(-1, VList.of().indexOf(1));
        Assertions.assertEquals(-1, VList.of(1, 2, 3).indexOf(4));
    }

    @Test
    void lastIndexOf() {
        Assertions.assertEquals(0, VList.of(1, 2, 3).lastIndexOf(1));
        Assertions.assertEquals(2, VList.of(1, 2, 3).lastIndexOf(3));
        Assertions.assertEquals(0, VList.of(1, 2, 3, 4).lastIndexOf(1));
        Assertions.assertEquals(2, VList.of(1, 2, 3, 4).lastIndexOf(3));

        Assertions.assertEquals(3, VList.of(1, 2, 2, 1).lastIndexOf(1));
        Assertions.assertEquals(2, VList.of(1, 2, 2, 1).lastIndexOf(2));

        Assertions.assertEquals(-1, VList.of().lastIndexOf(1));
        Assertions.assertEquals(-1, VList.of(1, 2, 3).lastIndexOf(4));
    }

    @Test
    void getSegments() {
        Assertions.assertEquals(List.of(), VList.of().getSegments()); // Empty list => no segments
        Assertions.assertEquals(List.of(List.of(1)), VList.of(1).getSegments()); // Only one element
        Assertions.assertEquals(List.of(List.of(1, 2), List.of(3)), VList.of(1, 2, 3).getSegments()); // size is power of 2 minus 1

        Assertions.assertEquals(List.of(List.of(1), List.of(2)), VList.of(1, 2).getSegments()); // offset != 0

        // Bigger list of segments, offset=0
        Assertions.assertEquals(
                List.of(List.of(1, 2, 3, 4, 5, 6, 7, 8), List.of(9, 10, 11, 12), List.of(13, 14), List.of(15)),
                VList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).getSegments());

        // Bigger list of segments, offset=15
        Assertions.assertEquals(
                List.of(List.of(1),
                        List.of(2, 3, 4, 5, 6, 7, 8, 9), List.of(10, 11, 12, 13), List.of(14, 15), List.of(16)),
                VList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16).getSegments());
    }

    @Test
    void add() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).add(4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).add(2, 4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).add(4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).add(2, 4));
    }

    @Test
    void addAll() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(List.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(List.of(4, 5, 6)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(VList.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(VList.of(4, 5, 6)));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(List.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(List.of(4, 5, 6)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(VList.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(VList.of(4, 5, 6)));
    }

    @Test
    void removeAll() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).removeAll(List.<Integer>of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).removeAll(List.of(1, 2, 3)));
    }

    @Test
    void retainAll() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.<Integer>of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.of(4)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.of(3)));
    }

    @Test
    void clear() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of().clear());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).clear());
    }

    @Test
    void set() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).set(0, 2));
    }

    @Test
    void remove() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).remove(2));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).remove((Integer) 2));
    }
}