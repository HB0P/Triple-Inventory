package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
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
    
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/PlayerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            ),
            slice = @Slice(
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/screen/PlayerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z",
                            ordinal = 6
                    )
            )
    )
    private boolean quickMove(PlayerScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(9, true, stack, i, j, b, this::insertItem);
    }
    
    // block duplication glitch caused by quick-moving from extended inventory to extended inventory
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/PlayerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z",
                    ordinal = 7
            )
    )
    private boolean quickMoveFix(PlayerScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return true;
    }
}
