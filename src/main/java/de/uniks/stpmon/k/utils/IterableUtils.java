package de.uniks.stpmon.k.utils;

import java.util.Iterator;

public class IterableUtils {
    public static <E> Iterable<E> concat(final Iterable<? extends E> iterable1,
                                         final Iterable<? extends E> iterable2) {
        return () -> new Iterator<E>() {
            final Iterator<? extends E> iterator1 = iterable1.iterator();
            final Iterator<? extends E> iterator2 = iterable2.iterator();

            @Override
            public boolean hasNext() {
                return iterator1.hasNext() || iterator2.hasNext();
            }

            @Override
            public E next() {
                return iterator1.hasNext() ? iterator1.next() : iterator2.next();
            }
        };
    }
}
