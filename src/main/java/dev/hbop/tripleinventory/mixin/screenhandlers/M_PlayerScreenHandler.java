package dev.hbop.tripleinventory.mixin.screenhandlers;

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
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.hbop.tripleinventory.helper.InventoryArea.*;

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
        int size = player.getWorld().getExtendedInventorySize();
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            EquipmentSlot equipmentSlot = player.getPreferredEquipmentSlot(itemStack);
            // crafting output -> main, extended
            if (slot == 0) {
                if (!this.insertItemToWhole(size, itemStack2)) {
                    return ItemStack.EMPTY;
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
                if (!this.insertItem(size, itemStack2, OFFHAND)) {
                    return ItemStack.EMPTY;
                }
            } 
            // main inventory -> hotbar
            else if (MAIN_INVENTORY.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, MAIN_HOTBAR, LEFT_HOTBAR, RIGHT_HOTBAR)) {
                    return ItemStack.EMPTY;
                }
            } 
            // main hotbar -> inventory
            else if (MAIN_HOTBAR.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, MAIN_INVENTORY, LEFT_INVENTORY, RIGHT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            }
            // left hotbar -> inventory
            else if (LEFT_HOTBAR.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, LEFT_INVENTORY, MAIN_INVENTORY, RIGHT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            } 
            // right hotbar -> inventory
            else if (RIGHT_HOTBAR.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, RIGHT_INVENTORY, MAIN_INVENTORY, LEFT_INVENTORY)) {
                    return ItemStack.EMPTY;
                }
            } 
            // left inventory -> hotbar
            else if (LEFT_INVENTORY.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, LEFT_HOTBAR, MAIN_HOTBAR, RIGHT_HOTBAR)) {
                    return ItemStack.EMPTY;
                }
            } 
            // right inventory -> hotbar
            else if (RIGHT_INVENTORY.containsSlot(slot, size)) {
                if (!this.insertItem(size, itemStack2, RIGHT_HOTBAR, MAIN_HOTBAR, LEFT_HOTBAR)) {
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
            for (Pair<Integer, Integer> region : area.getRegions(size)) {
                if (this.insertItem(stack, region.getLeft(), region.getRight(), false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Unique
    private boolean insertItemToWhole(int size, ItemStack stack) {
        return insertItem(size, stack, 
                MAIN_INVENTORY, 
                LEFT_INVENTORY, 
                RIGHT_INVENTORY,
                MAIN_HOTBAR,
                LEFT_HOTBAR,
                RIGHT_HOTBAR
        );
    }
}
