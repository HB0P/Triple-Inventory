package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.client.config.ClientConfig;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import dev.hbop.tripleinventory.helper.ShulkerPreviewSlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    
    // don't draw status effect boxes if they would overlap with the shulker preview
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StatusEffectsDisplay;drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V"
            )
    )
    private void drawStatusEffects(StatusEffectsDisplay instance, DrawContext context, int mouseX, int mouseY) {
        if (ClientConfig.HANDLER.instance().shulkerPosition == ShulkerPosition.RIGHT_TOP && showShulkerPreview()) {
            return;
        }
        instance.drawStatusEffects(context, mouseX, mouseY);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StatusEffectsDisplay;drawStatusEffectTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"
            )
    )
    private void drawStatusEffectTooltip(StatusEffectsDisplay instance, DrawContext context, int mouseX, int mouseY) {
        if (ClientConfig.HANDLER.instance().shulkerPosition == ShulkerPosition.RIGHT_TOP && showShulkerPreview()) {
            return;
        }
        instance.drawStatusEffectTooltip(context, mouseX, mouseY);
    }

    @Unique
    private boolean showShulkerPreview() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ShulkerPreviewSlot shulkerSlot && shulkerSlot.isEnabled()) {
                return true;
            }
        }
        return false;
    }
}