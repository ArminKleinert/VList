package de.kleinert.vlist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

class Segment {
    @Nullable Segment next;
    @NotNull Object[] elements;

    public Segment(@Nullable Segment next, @NotNull Object[] elements) {
        this.next = next;
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Segment)) return false;
        return Arrays.equals(elements, ((Segment) o).elements) && Objects.equals(next, ((Segment) o).next);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(next);
        result = 31 * result + Arrays.hashCode(elements);
        return result;
    }
}