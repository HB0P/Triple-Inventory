package dev.hbop.tripleinventory.helper;

import java.util.function.Function;

public enum InventoryArea {
    MAIN_HOTBAR(size -> 36, size -> 45),
    MAIN_INVENTORY(size -> 9, size -> 36),
    LEFT_HOTBAR(size -> 46, size -> 46 + size),
    LEFT_INVENTORY(size -> 46 + size * 2, size -> 46 + size * 5),
    RIGHT_HOTBAR(size -> 46 + size, size -> 46 + size * 2),
    RIGHT_INVENTORY(size -> 46 + size * 5, size -> 46 + size * 8),
    OFFHAND(size -> 45, size -> 46);

    private final Function<Integer, Integer> startIndex;
    private final Function<Integer, Integer> endIndex;

    InventoryArea(Function<Integer, Integer> startIndex, Function<Integer, Integer> endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex(int size) {
        return startIndex.apply(size);
    }

    public int getEndIndex(int size) {
        return endIndex.apply(size);
    }
}