package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CartographyTableScreenHandler.class)
public abstract class M_CartographyTableScreenHandler extends ScreenHandler {
    
    @Unique private World world;
    
    protected M_CartographyTableScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At("TAIL")
    )
    private void init(int syncId, PlayerInventory inventory, ScreenHandlerContext context, CallbackInfo ci) {
        this.world = inventory.player.getWorld();
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/CartographyTableScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(CartographyTableScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(3, stack, i, j, b, this::insertItem, this.world);
    }
}
