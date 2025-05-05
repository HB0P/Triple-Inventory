package dev.hbop.tripleinventory.mixin;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import dev.hbop.tripleinventory.helper.ShulkerPreviewSlot;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class M_ScreenHandler {

    @Shadow protected abstract Slot addSlot(Slot slot);

    // allow swapping items to extended inventory
    @ModifyConstant(
            method = "internalOnSlotClick",
            constant = @Constant(intValue = 9)
    )
    private int modify9(int constant) {
        return 59;
    }
    
    @Inject(
            method = "addPlayerSlots",
            at = @At("TAIL")
    )
    private void addPlayerSlots(Inventory inventory, int left, int top, CallbackInfo ci) {
        if (inventory instanceof PlayerInventory playerInventory) {
            int width;
            if ((ScreenHandler)(Object) this instanceof BeaconScreenHandler) width = 230;
            else if ((ScreenHandler)(Object) this instanceof MerchantScreenHandler) width = 276;
            else width = 176;

            World world = playerInventory.player.getWorld();
            // left hotbar
            for (int i = 0; i < 9; i++) {
                this.addSlot(new InventoryHelper.ExtendedSlot(playerInventory, i + 41, -14 - i * 18, top + 58, false, world, i + 1));
            }
            // right hotbar
            for (int i = 0; i < 9; i++) {
                this.addSlot(new InventoryHelper.ExtendedSlot(playerInventory, i + 50, width - 2 + i * 18, top + 58, true, world, i + 1));
            }
            // left inventory
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 9; x++) {
                    this.addSlot(new InventoryHelper.ExtendedSlot(playerInventory, y * 9 + x + 59, -14 - x * 18, top + y * 18, false, world, x + 1));
                }
            }
            // right inventory
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 9; x++) {
                    this.addSlot(new InventoryHelper.ExtendedSlot(playerInventory, y * 9 + x + 86, width - 2 + x * 18, top + y * 18, true, world, x + 1));
                }
            }
            // shulker inventory
            SimpleInventory shulkerInventory = new SimpleInventory(27);
            ShulkerPosition position = world.getShulkerPosition();
            if (position == null) {
                for (int i = 0; i < 27; i++) {
                    this.addSlot(new ShulkerPreviewSlot(shulkerInventory, i, 0, 0));
                }
            }
            else {
                int x = position.getX(0, width, world.getExtendedInventorySize()) + 8;
                int y = position.getY(0, top + 82) + 18;
                for (int dy = 0; dy < 3; dy++) {
                    for (int dx = 0; dx < 9; dx++) {
                        this.addSlot(new ShulkerPreviewSlot(shulkerInventory, dy * 9 + dx, x + dx * 18, y + dy * 18));
                    }
                }
            }
        }
    }
}