package dev.hbop.tripleinventory.helper;

import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Function;

public enum InventoryArea {
    MAIN_HOTBAR(size -> List.of(new Pair<>(36, 45))),
    MAIN_INVENTORY(size -> List.of(new Pair<>(9, 36))),
    LEFT_HOTBAR(size -> List.of(new Pair<>(46, 46 + size))),
    LEFT_INVENTORY(size -> List.of(
            new Pair<>(64, 64 + size),
            new Pair<>(73, 73 + size),
            new Pair<>(82, 82 + size)
    )),
    RIGHT_HOTBAR(size -> List.of(new Pair<>(55, 55 + size))),
    RIGHT_INVENTORY(size -> List.of(
            new Pair<>(91, 91 + size),
            new Pair<>(100, 100 + size),
            new Pair<>(109, 109 + size)
    )),
    OFFHAND(size -> List.of(new Pair<>(45, 46)));

    private final Function<Integer, List<Pair<Integer, Integer>>> regions;

    InventoryArea(Function<Integer, List<Pair<Integer, Integer>>> regions) {
        this.regions = regions;
    }

    public List<Pair<Integer, Integer>> getRegions(int size) {
        return regions.apply(size);
    }
    
    public boolean containsSlot(int slot, int size) {
        for (Pair<Integer, Integer> region : getRegions(size)) {
            if (slot >= region.getLeft() && slot < region.getRight()) {
                return true;
            }
        }
        return false;
    }
}