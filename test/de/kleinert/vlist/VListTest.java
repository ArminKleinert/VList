package de.kleinert.vlist;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class VListTest {
    @Test
    void of() {
        var vl = VList.of(1, 2, 3);
        Assertions.assertEquals(3, vl.size());
        Assertions.assertEquals(List.of(1, 2, 3), vl);
    }

    @Test
    void size() {
        Assertions.assertEquals(0, VList.of().size());
        Assertions.assertEquals(0, VList.listToVList(List.of()).size());

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
        var list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        var vlst = VList.listToVList(list);

        // Forwards get(...) check
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(list.get(i), vlst.get(i));
        }

        // Backwards get(...) check
        for (int i = list.size() - 1; i >= 0; i--) {
            Assertions.assertEquals(list.get(i), vlst.get(i));
        }
    }

    @Test
    void getOutOfBounds() {
        var vlst = VList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlst.get(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> vlst.get(vlst.size()));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> VList.of().get(0));
    }

    @Test
    void equalityToVList() {
        Assertions.assertEquals(VList.of(), VList.of());
        Assertions.assertEquals(VList.of(1), VList.of(1));

        Assertions.assertNotEquals(VList.of(1), VList.of());
        Assertions.assertNotEquals(VList.of(), VList.of(1));
    }

    @Test
    void equalityToOtherLists() {
        Assertions.assertEquals(List.of(), VList.of());
        Assertions.assertEquals(List.of(1), VList.of(1));

        Assertions.assertNotEquals(List.of(1), VList.of());
        Assertions.assertNotEquals(List.of(), VList.of(1));
    }

    @Test
    void cons() {
        // General test; Order of insertion
        Assertions.assertEquals(List.of(1, 2, 3), VList.of().cons(3).cons(2).cons(1));

        // Test immutability
        var vlst = VList.of().cons(3).cons(2);
        Assertions.assertEquals(List.of(2, 3), vlst);
        vlst.cons(1);
        Assertions.assertEquals(List.of(2, 3), vlst);
    }

    @Test
    public void prepend() {
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
    public void append() {
        Assertions.assertEquals(
                List.of(1, 4), VList.of(1).append(4));
        Assertions.assertEquals(
                List.of(1, 2, 3, 4, 5), VList.of(1, 2, 3).append(4).append(5));
    }

    @Test
    public void appendAll() {
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
    public void toArray() {
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
    public void containsAll() {
        Assertions.assertTrue(VList.of().containsAll(List.of()));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.<Integer>of()));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.of(1, 3)));
        Assertions.assertTrue(VList.of(1, 2, 3).containsAll(List.of(1, 2, 3)));

        Assertions.assertFalse(VList.of().containsAll(List.of(1, 2)));// Empty list contains nothing
        Assertions.assertFalse(VList.of(1, 2, 3).containsAll(List.of(4, 5))); // No common subset
        Assertions.assertFalse(VList.of(1, 2, 3).containsAll(List.of(1, 2, 3, 4))); // Common subset, but not full subset
    }

    @Test
    public void isEmpty() {
        Assertions.assertTrue(VList.empty().isEmpty());
        Assertions.assertTrue(VList.of().isEmpty());
        Assertions.assertTrue(VList.listToVList(List.of()).isEmpty());

        Assertions.assertFalse(VList.of(1, 2, 3).isEmpty());
        Assertions.assertFalse(VList.listToVList(List.of(1, 2, 3)).isEmpty());
    }

    @Test
    public void contains() {
        Assertions.assertTrue(VList.of(1, 2, 3).contains(1));
        Assertions.assertTrue(VList.of(1, 2, 3).contains(3));

        Assertions.assertFalse(VList.of().contains(1));
        Assertions.assertFalse(VList.of(1, 2, 3).contains(4));
    }

    @Test
    public void indexOf() {

    }

    @Test
    public void lastIndexOf() {

    }

    @Test
    public void listIterator() {

    }

    @Test
    public void add() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).add(4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).add(2, 4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).add(4));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).add(2, 4));
    }

    @Test
    public void addAll() {
        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.of(1, 2, 3).addAll(List.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(List.of(4, 5, 6)));
        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.of(1, 2, 3).addAll(VList.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).addAll(VList.of(4, 5, 6)));

        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.listToVList(List.of(1, 2, 3)).addAll(List.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(List.of(4, 5, 6)));
        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.listToVList(List.of(1, 2, 3)).addAll(VList.of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.listToVList(List.of(1, 2, 3)).addAll(VList.of(4, 5, 6)));
    }


    @Test
    public void removeAll() {
        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.of(1, 2, 3).removeAll(List.<Integer>of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).removeAll(List.of(1, 2, 3)));
    }

    @Test
    public void retainAll() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.<Integer>of()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.of(4)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).retainAll(List.of(3)));
    }

    @Test
    public void clear() {
        Assertions.assertThrows(UnsupportedOperationException.class,() -> VList.of().clear());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).clear());
    }

    @Test
    public void set() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).set(0, 2));
    }


    @Test
    public void remove() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).remove(2));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> VList.of(1, 2, 3).remove((Integer) 2));
    }
}