package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreenHandler.class)
public abstract class M_BrewingStandScreenHandler extends ScreenHandler {

    @Unique
    private World world;
    
    protected M_BrewingStandScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, CallbackInfo ci) {
        world = playerInventory.player.getWorld();
        InventoryHelper.addExtraSlots(playerInventory, slot -> this.addSlot(slot));
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/BrewingStandScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(BrewingStandScreenHandler instance, ItemStack stack, int i, int j, boolean fromLast) {
        if (fromLast) return InventoryHelper.handleQuickMove(5, stack, i, j, true, this::insertItem, this.world);
        else return this.insertItem(stack, i, j, false);
    }
}
