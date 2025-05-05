package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgingScreenHandler.class)
public abstract class M_ForgingScreenHandler extends ScreenHandler {

    @Shadow protected abstract int getPlayerInventoryStartIndex();

    @Shadow protected abstract int getPlayerHotbarEndIndex();

    @Shadow @Final protected PlayerEntity player;

    protected M_ForgingScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ForgingScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(ForgingScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(getPlayerInventoryStartIndex(), stack, i, j, b, this::insertItem, this.player.getWorld());
    }
    
    // allow quick-move from extended inventory to input
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ForgingScreenHandler;getPlayerHotbarEndIndex()I",
                    ordinal = 1
            )
    )
    private int quickMoveFix(ForgingScreenHandler instance) {
        return getPlayerHotbarEndIndex() + 24;
    }
}
