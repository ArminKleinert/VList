package de.kleinert.vlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class VListTest {
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
    void stream() {
        // General test; Order of insertion
        Assertions.assertEquals(
                List.of(1, 2, 3),
                VList.of(1, 3, 3, 3, 3, 3, 2, 2, 4).stream()
                        .sorted().distinct()
                        .limit(3)
                        .collect(Collectors.toUnmodifiableList()));
    }
}