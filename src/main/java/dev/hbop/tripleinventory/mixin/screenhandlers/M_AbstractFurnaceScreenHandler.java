package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class M_AbstractFurnaceScreenHandler extends AbstractRecipeScreenHandler {

    @Shadow @Final protected World world;

    public M_AbstractFurnaceScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }
    
    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(AbstractFurnaceScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(3, stack, i, j, b, this::insertItem, this.world);
    }
}
