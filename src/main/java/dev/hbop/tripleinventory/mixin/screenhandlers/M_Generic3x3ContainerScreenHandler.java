package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
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

@Mixin(Generic3x3ContainerScreenHandler.class)
public abstract class M_Generic3x3ContainerScreenHandler extends ScreenHandler {
    
    @Unique private World world;
    
    protected M_Generic3x3ContainerScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, PlayerInventory playerInventory, Inventory inventory, CallbackInfo ci) {
        this.world = playerInventory.player.getWorld();
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/Generic3x3ContainerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(Generic3x3ContainerScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(9, stack, i, j, b, this::insertItem, this.world);
    }
}
