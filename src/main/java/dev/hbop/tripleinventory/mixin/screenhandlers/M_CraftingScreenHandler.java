package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingScreenHandler.class)
public abstract class M_CraftingScreenHandler extends AbstractRecipeScreenHandler {
    
    public M_CraftingScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
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
                    target = "Lnet/minecraft/screen/CraftingScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(CraftingScreenHandler instance, ItemStack stack, int i, int j, boolean fromLast) {
        if (fromLast) return InventoryHelper.handleQuickMove(10, stack, i, j, true, this::insertItem);
        else return this.insertItem(stack, i, j, false);
    }
    
    // allow quick-move from crafting grid to extended inventory
    @Inject(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot >= 1 && slot < 10) {
            if (!InventoryHelper.handleQuickMove(10, this.slots.get(slot).getStack(), 10, 46, false, this::insertItem)) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
    
    // allow quick-move from extended inventory to crafting grid
    @ModifyConstant(
            method = "quickMove",
            constant = @Constant(intValue = 46, ordinal = 1)
    )
    private int modify46(int constant) {
        return constant + 24;
    }
}
