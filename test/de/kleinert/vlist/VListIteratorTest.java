package de.kleinert.vlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class VListIteratorTest {
    @Test
    void testEmptyIterator() {
        var vlEmpty = VList.of();
        var iterEmpty = vlEmpty.iterator();
        Assertions.assertFalse(iterEmpty.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterEmpty::next);
    }

    @Test
    void testSizedIterator() {
        var vl2 = VList.of(1, 2);
        var iter2 = vl2.iterator();
        Assertions.assertTrue(iter2.hasNext());
        Assertions.assertEquals(1, iter2.next());
        Assertions.assertTrue(iter2.hasNext());
        Assertions.assertEquals(2, iter2.next());
        Assertions.assertFalse(iter2.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iter2::next);

        var vl3 = VList.of(1, 2, 3);
        var iter3 = vl3.iterator();
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(1, iter3.next());
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(2, iter3.next());
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(3, iter3.next());
        Assertions.assertFalse(iter3.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iter3::next);
    }

    @Test
    void testMultipleIterators() {
        var vlN = VList.of(1, 2, 3);
        var iterN1 = vlN.iterator();
        var iterN2 = vlN.iterator();
        Assertions.assertTrue(iterN1.hasNext());
        Assertions.assertTrue(iterN2.hasNext());

        Assertions.assertEquals(1, iterN1.next());
        Assertions.assertEquals(1, iterN2.next());

        iterN1.next();
        iterN1.next();

        Assertions.assertThrows(NoSuchElementException.class, iterN1::next);
        Assertions.assertEquals(2, iterN2.next());
    }

    @Test
    void testEmptyListIterator() {
        var vlEmpty = VList.of();
        var iterEmpty = vlEmpty.listIterator();
        Assertions.assertFalse(iterEmpty.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iterEmpty::next);
    }

    @Test
    void testSizedListIterator() {
        var vl2 = VList.of(1, 2);
        var iter2 = vl2.listIterator();
        Assertions.assertTrue(iter2.hasNext());
        Assertions.assertEquals(1, iter2.next());
        Assertions.assertTrue(iter2.hasNext());
        Assertions.assertEquals(2, iter2.next());
        Assertions.assertFalse(iter2.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iter2::next);

        var vl3 = VList.of(1, 2, 3);
        var iter3 = vl3.listIterator();
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(1, iter3.next());
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(2, iter3.next());
        Assertions.assertTrue(iter3.hasNext());
        Assertions.assertEquals(3, iter3.next());
        Assertions.assertFalse(iter3.hasNext());
        Assertions.assertThrows(NoSuchElementException.class, iter3::next);
    }

    @Test
    void testMultipleListIterators() {
        var vlN = VList.of(1, 2, 3);
        var iterN1 = vlN.listIterator();
        var iterN2 = vlN.listIterator();
        Assertions.assertTrue(iterN1.hasNext());
        Assertions.assertTrue(iterN2.hasNext());

        Assertions.assertEquals(1, iterN1.next());
        Assertions.assertEquals(1, iterN2.next());

        iterN1.next();
        iterN1.next();

        Assertions.assertThrows(NoSuchElementException.class, iterN1::next);
        Assertions.assertEquals(2, iterN2.next());
    }

    @Test
    void testListIteratorNextPrevious() {
        var vl = VList.of(1, 2, 3);
        var iter = vl.listIterator();

        Assertions.assertFalse(iter.hasPrevious());
        Assertions.assertTrue(iter.hasNext());

        Assertions.assertEquals(1, iter.next());

        Assertions.assertTrue(iter.hasPrevious());
        Assertions.assertTrue(iter.hasNext());

        Assertions.assertEquals(1, iter.previous());
        Assertions.assertEquals(1, iter.next());

        iter.next();
        iter.next();

        Assertions.assertTrue(iter.hasPrevious());
        Assertions.assertFalse(iter.hasNext());

        Assertions.assertEquals(3, iter.previous());

        Assertions.assertTrue(iter.hasPrevious());
        Assertions.assertTrue(iter.hasNext());

        Assertions.assertEquals(3, iter.next());
    }
}
