package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.helper.InventoryArea;
import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class M_PlayerScreenHandler extends AbstractRecipeScreenHandler {

    public M_PlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void init(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        InventoryHelper.addExtraSlots(inventory, slot -> this.addSlot(slot));
    }

    /**
     * @author HB0P
     * @reason I literally cannot work out a better way to make quick moves work
     */
    @Overwrite
    public ItemStack quickMove(PlayerEntity player, int slot) {
        int size = TripleInventory.extendedInventorySize();
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            EquipmentSlot equipmentSlot = player.getPreferredEquipmentSlot(itemStack);
            // crafting output -> main, extended
            if (slot == 0) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    if (!this.insertItem(itemStack2, 46, 46 + size * 8, true)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            }
            // crafting input, armor -> main, extended
            else if (slot >= 1 && slot < 9) {
                if (!this.insertItemToWhole(size, itemStack2)) {
                    return ItemStack.EMPTY;
                }
            }
            // anywhere -> armor
            else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !this.slots.get(8 - equipmentSlot.getEntitySlotId()).hasStack()) {
                int i = 8 - equipmentSlot.getEntitySlotId();
                if (!this.insertItem(itemStack2, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } 
            // anywhere -> offhand
            else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasStack()) {
                if (!this.insertItem(size, itemStack2, InventoryArea.OFFHAND)) {
                    return ItemStack.EMPTY;
                }
            } 
            // main inventory -> hotbar
            else if (slot >= 9 && slot < 36) {
                if (!this.insertItem(size, itemStack2, InventoryArea.MAIN_HOTBAR, InventoryArea.LEFT_HOTBAR, InventoryArea.RIGHT_HOTBAR)) {
                    return ItemStack.EMPTY;
                }
            } 
            // main hotbar -> inventory
            else if (slot >= 36 && slot < 45) {
                if (!this.insertItem(size, itemStack2, InventoryArea.MAIN_INVENTORY, InventoryArea.LEFT_INVENTORY, InventoryArea.RIGHT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            }
            // left hotbar -> inventory
            else if (slot >= 46 && slot < 46 + size) {
                if (!this.insertItem(size, itemStack2, InventoryArea.LEFT_INVENTORY, InventoryArea.MAIN_INVENTORY, InventoryArea.RIGHT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            } 
            // right hotbar -> inventory
            else if (slot >= 46 + size && slot < 46 + size * 2) {
                if (!this.insertItem(size, itemStack2, InventoryArea.RIGHT_INVENTORY, InventoryArea.MAIN_INVENTORY, InventoryArea.LEFT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            } 
            // left inventory -> hotbar
            else if (slot >= 46 + size * 2 && slot < 46 + size * 5) {
                if (!this.insertItem(size, itemStack2, InventoryArea.LEFT_HOTBAR, InventoryArea.MAIN_HOTBAR, InventoryArea.RIGHT_HOTBAR)) {
                    return ItemStack.EMPTY;
                }
            } 
            // right inventory -> hotbar
            else if (slot >= 46 + size * 5 && slot < 46 + size * 8) {
                if (!this.insertItem(size, itemStack2, InventoryArea.RIGHT_HOTBAR, InventoryArea.MAIN_HOTBAR, InventoryArea.LEFT_HOTBAR)) {
                    return ItemStack.EMPTY;
                }
            } 
            // anywhere -> main, extended
            else if (!this.insertItemToWhole(size, itemStack2)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY, itemStack);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
            if (slot == 0) {
                player.dropItem(itemStack2, false);
            }
        }

        return itemStack;
    }
    
    @Unique
    private boolean insertItem(int size, ItemStack stack, InventoryArea ... areas) {
        for (InventoryArea area : areas) {
            if (this.insertItem(stack, area.getStartIndex(size), area.getEndIndex(size), false)) {
                return true;
            }
        }
        return false;
    }
    
    @Unique
    private boolean insertItemToWhole(int size, ItemStack stack) {
        return insertItem(size, stack, 
                InventoryArea.MAIN_INVENTORY, 
                InventoryArea.LEFT_INVENTORY, 
                InventoryArea.RIGHT_INVENTORY,
                InventoryArea.MAIN_HOTBAR,
                InventoryArea.LEFT_HOTBAR,
                InventoryArea.RIGHT_HOTBAR
        );
    }
}
