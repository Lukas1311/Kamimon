package de.uniks.stpmon.k.world.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public abstract class IdSource implements Supplier<Collection<Integer>> {

    protected final int startIndex;

    public IdSource(int startIndex) {
        this.startIndex = startIndex;
    }


    public static class Single extends IdSource {

        public Single(int startIndex) {
            super(startIndex);
        }

        @Override
        public Collection<Integer> get() {
            return List.of(startIndex);
        }

    }

    public static class Rectangle extends IdSource {

        private final int width;
        private final int height;
        private final int columns;

        public Rectangle(int startIndex, int width, int height, int columns) {
            super(startIndex);
            this.width = width;
            this.height = height;
            this.columns = columns;
        }

        @Override
        public Collection<Integer> get() {
            List<Integer> values = new LinkedList<>();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    values.add(startIndex + x + y * columns);
                }
            }
            return values;
        }

    }

}
