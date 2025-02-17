package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.village.Merchant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreenHandler.class)
public abstract class M_MerchantScreenHandler extends ScreenHandler {
    
    protected M_MerchantScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/village/Merchant;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, PlayerInventory playerInventory, Merchant merchant, CallbackInfo ci) {
        InventoryHelper.addExtraSlots(playerInventory, 276, 166, 100, slot -> this.addSlot(slot));
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/MerchantScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(MerchantScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(3, stack, i, j, b, this::insertItem);
    }
    
    // allow autofill trade items from extended inventory
    @ModifyConstant(
            method = "autofill",
            constant = @Constant(intValue = 39)
    )
    private int modify39(int constant) {
        return constant + 24;
    }
}
