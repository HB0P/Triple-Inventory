package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class M_InventoryScreen extends RecipeBookScreen<PlayerScreenHandler> {
    public M_InventoryScreen(PlayerScreenHandler handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
        super(handler, recipeBook, inventory, title);
    }

    @Inject(
            method = "onRecipeBookToggled",
            at = @At("HEAD")
    )
    private void onRecipeBookToggled(CallbackInfo ci) {
        super.onRecipeBookToggled();
    }
}