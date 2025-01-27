package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreenHandler.class)
public abstract class M_BeaconScreenHandler extends ScreenHandler {

    protected M_BeaconScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(
            method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context, CallbackInfo ci) {
        if (inventory instanceof PlayerInventory playerInventory) {
            InventoryHelper.addExtraSlots(playerInventory, 230, 219, slot -> this.addSlot(slot));
        }
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/BeaconScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(BeaconScreenHandler instance, ItemStack stack, int i, int j, boolean fromLast) {
        if (fromLast) return InventoryHelper.handleQuickMove(1, stack, i, j, true, this::insertItem);
        else return this.insertItem(stack, i, j, false);
    }
}
