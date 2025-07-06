package dev.hbop.tripleinventory.helper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class InventoryHelper {
    
    public static boolean isSlotEnabled(int index, World world) {
        if (index < 41) return true;
        return (index - 41) % 9 < world.getExtendedInventorySize();
    }
    
    /**
     * Get the first slot in the hotbar & extended hotbar matching a predicate<br>
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

    public static boolean handleQuickMove(int start, ItemStack stack, int i, int j, boolean b, InsertItemFunction function, World world) {
        boolean changed = function.apply(stack, i, j, b);
        if (i == start && j == start + 36 && !stack.isEmpty()) {
            for (int row = 0; row < 8; row++) {
                if (function.apply(stack, start + 36 + (9 * row), start + 36 + (9 * row) + world.getExtendedInventorySize(), false)) {
                    changed = true;
                    break;
                }
            }
        }
        return changed;
    }

    public static class ExtendedSlot extends Slot {
        
        private final World world;
        private final int maxSize;
        public boolean isEnabled = true;
        public boolean isRight;
        
        public ExtendedSlot(Inventory inventory, int index, int x, int y, boolean isRight, World world, int maxSize) {
            super(inventory, index, x, y);
            this.world = world;
            this.maxSize = maxSize;
            this.isRight = isRight;
        }
        
        @Override
        public boolean isEnabled() {
            return isEnabled && this.world.getExtendedInventorySize() >= maxSize;
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return isEnabled();
        }
    }
    
    public interface InsertItemFunction {
        boolean apply(ItemStack stack, int i, int j, boolean b);
    }
}
