package dev.hbop.tripleinventory.helper;

import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Function;

public enum InventoryArea {
    MAIN_HOTBAR(size -> List.of(new Pair<>(36, 45))),
    MAIN_INVENTORY(size -> List.of(new Pair<>(9, 36))),
    LEFT_HOTBAR(size -> List.of(new Pair<>(45, 45 + size))),
    LEFT_INVENTORY(size -> List.of(
            new Pair<>(63, 63 + size),
            new Pair<>(72, 72 + size),
            new Pair<>(81, 81 + size)
    )),
    RIGHT_HOTBAR(size -> List.of(new Pair<>(54, 54 + size))),
    RIGHT_INVENTORY(size -> List.of(
            new Pair<>(90, 90 + size),
            new Pair<>(99, 99 + size),
            new Pair<>(108, 108 + size)
    )),
    OFFHAND(size -> List.of(new Pair<>(144, 145)));

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