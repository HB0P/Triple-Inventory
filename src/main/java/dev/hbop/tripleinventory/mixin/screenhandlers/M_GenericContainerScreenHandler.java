package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreenHandler.class)
public abstract class M_GenericContainerScreenHandler extends ScreenHandler {

    @Shadow @Final private int rows;
    @Unique private World world;

    protected M_GenericContainerScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    @Inject(
            method = "<init>(Lnet/minecraft/screen/ScreenHandlerType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;I)V",
            at = @At("TAIL")
    )
    private void init(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, CallbackInfo ci) {
        this.world = playerInventory.player.getWorld();
        InventoryHelper.addExtraSlots(playerInventory, 176, 113 + this.rows * 18, slot -> this.addSlot(slot));
    }
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/GenericContainerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(GenericContainerScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(this.rows * 9, stack, i, j == this.slots.size() ? i + 36 : j, b, this::insertItem, this.world);
    }
    
}
