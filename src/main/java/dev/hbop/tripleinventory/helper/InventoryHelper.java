package dev.hbop.tripleinventory.helper;

import dev.hbop.tripleinventory.TripleInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventoryHelper {

    public static boolean isSlotEnabled(int index) {
        if (index < 41) return true;
        return (index - 41) % 9 < TripleInventory.extendedInventorySize();
    }
    
    /**
     * Get the first slot in the hotbar & tool hotbar matching a predicate<br>
     * @param inventory The inventory to test in
     * @param predicate The predicate to test stacks against
     * @return The first matching slot, or -1 if none are found
     */
    public static int getSlotInHotbarMatching(PlayerInventory inventory, Predicate<ItemStack> predicate) {
        for (int i = 41; i <= 58; i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                return i;
            }
        }
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                return i;
            }
        }
        return -1;
    }
    
    public static void addExtraSlots(PlayerInventory inventory, int width, int height, Consumer<Slot> consumer) {
        int size = TripleInventory.extendedInventorySize();
        // left hotbar
        for (int i = 0; i < size; i++) {
            consumer.accept(new EquipmentSlot(inventory, i + 41, -14 - i * 18, height - 24, TripleInventory.restrictExtendedHotbarToEquipment()));
        }
        // right hotbar
        for (int i = 0; i < size; i++) {
            consumer.accept(new EquipmentSlot(inventory, i + 50, width - 2 + i * 18, height - 24, TripleInventory.restrictExtendedHotbarToEquipment()));
        }
        // left inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < size; x++) {
                consumer.accept(new EquipmentSlot(inventory, y * 9 + x + 59, -14 - x * 18, height - 82 + y * 18, TripleInventory.restrictExtendedInventoryToEquipment()));
            }
        }
        // right inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < size; x++) {
                consumer.accept(new EquipmentSlot(inventory, y * 9 + x + 86, width - 2 + x * 18, height - 82 + y * 18, TripleInventory.restrictExtendedInventoryToEquipment()));
            }
        }
    }
    
    public static void addExtraSlots(PlayerInventory inventory, Consumer<Slot> consumer) {
        addExtraSlots(inventory, 176, 166, consumer);
    }
    
    public static boolean handleQuickMove(int start, boolean hasOffhand, ItemStack stack, int i, int j, boolean b, InsertItemFunction function) {
        int offhand = hasOffhand ? 1 : 0;
        boolean changed = function.apply(stack, i, j, b);
        if (i == start && j == start + 36 && !stack.isEmpty()) {
            if (function.apply(stack, start + 36 + offhand, start + 36 + offhand + TripleInventory.extendedInventorySize() * 8, false)) {
                changed = true;
            }
        }
        return changed;
    }

    public static boolean handleQuickMove(int start, ItemStack stack, int i, int j, boolean b, InsertItemFunction function) {
        return handleQuickMove(start, false, stack, i, j, b, function);
    }

    private static class EquipmentSlot extends Slot {
        
        private final boolean restrictedToEquipment;
        
        private EquipmentSlot(Inventory inventory, int index, int x, int y, boolean restrictedToEquipment) {
            super(inventory, index, x, y);
            this.restrictedToEquipment = restrictedToEquipment;
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return !restrictedToEquipment || stack.isDamageable();
        }
    }
    
    public interface InsertItemFunction {
        boolean apply(ItemStack stack, int i, int j, boolean b);
    }
}
