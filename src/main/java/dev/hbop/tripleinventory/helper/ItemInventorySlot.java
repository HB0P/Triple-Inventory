package dev.hbop.tripleinventory.helper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemInventorySlot extends Slot {

    private boolean isEnabled;
    private ItemStack tiedStack;
    private Slot tiedSlot;
    private Consumer<ItemStack> onModify;
    private Predicate<ItemStack> canInsert;
    private int color;

    private static final Map<Item, Integer> COLORS = new HashMap<>();

    static {
        COLORS.put(Items.SHULKER_BOX, 0x976997);
        COLORS.putAll(Map.of(
                Items.WHITE_SHULKER_BOX, 0xF9FFFE,
                Items.ORANGE_SHULKER_BOX, 0xF9801D,
                Items.MAGENTA_SHULKER_BOX, 0xC74EBD,
                Items.LIGHT_BLUE_SHULKER_BOX, 0x3AB3DA,
                Items.YELLOW_SHULKER_BOX, 0xFED83D,
                Items.LIME_SHULKER_BOX, 0x80C71F,
                Items.PINK_SHULKER_BOX, 0xF38BAA,
                Items.GRAY_SHULKER_BOX, 0x474F52
        ));
        COLORS.putAll(Map.of(
                Items.LIGHT_GRAY_SHULKER_BOX, 0x9D9D97,
                Items.CYAN_SHULKER_BOX, 0x169C9C,
                Items.PURPLE_SHULKER_BOX, 0x8932B8,
                Items.BLUE_SHULKER_BOX, 0x3C44AA,
                Items.BROWN_SHULKER_BOX, 0x835432,
                Items.GREEN_SHULKER_BOX, 0x5E7C16,
                Items.RED_SHULKER_BOX, 0xB02E26,
                Items.BLACK_SHULKER_BOX, 0x1D1D21
        ));
        COLORS.put(Items.ENDER_CHEST, 0xF6FABD);
    }

    ItemInventorySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public void enable(ItemStack tiedStack, Slot tiedSlot, Consumer<ItemStack> onModify, Predicate<ItemStack> canInsert) {
        isEnabled = true;
        this.tiedStack = tiedStack;
        this.tiedSlot = tiedSlot;
        this.onModify = onModify;
        this.canInsert = canInsert;
        for (Item item : COLORS.keySet()) {
            if (tiedStack.isOf(item)) {
                color = COLORS.get(item);
                break;
            }
        }
    }

    public void disable() {
        isEnabled = false;
    }

    public int getShulkerBoxColor() {
        return color;
    }

    @Override
    public void markDirty() {
        if (!isEnabled) return;
        onModify.accept(this.getStack());
    }

    @Override
    public boolean isEnabled() {
        if (isEnabled) {
            if (tiedSlot.getStack() == tiedStack) return true;
            isEnabled = false;
        }
        return false;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return canInsert.test(stack);
    }
}