package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.StonecutterScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StonecutterScreenHandler.class)
public abstract class M_StonecutterScreenHandler extends ScreenHandler {
    
    protected M_StonecutterScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
        InventoryHelper.addExtraSlots(playerInventory, slot -> this.addSlot(slot));
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/StonecutterScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(StonecutterScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(2, stack, i, j, b, this::insertItem);
    }
}
