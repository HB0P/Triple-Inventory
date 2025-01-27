package dev.hbop.tripleinventory.mixin.screenhandlers;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class M_AbstractFurnaceScreenHandler extends AbstractRecipeScreenHandler {
    
    public M_AbstractFurnaceScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/recipe/book/RecipeBookType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
            at = @At("TAIL")
    )
    private void init(ScreenHandlerType<?> type, RecipeType<?> recipeType, RegistryKey<?> recipePropertySetKey, RecipeBookType category, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, CallbackInfo ci) {
        InventoryHelper.addExtraSlots(playerInventory, slot -> this.addSlot(slot));
    }

    @Redirect(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            )
    )
    private boolean quickMove(AbstractFurnaceScreenHandler instance, ItemStack stack, int i, int j, boolean b) {
        return InventoryHelper.handleQuickMove(3, stack, i, j, b, this::insertItem);
    }
}
