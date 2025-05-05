package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookScreen.class)
public abstract class M_RecipeBookScreen<T extends AbstractRecipeScreenHandler> extends HandledScreen<T> {

    @Shadow @Final public RecipeBookWidget<?> recipeBook;

    public M_RecipeBookScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * @author HB0P
     * @reason Use super method
     */
    @Overwrite
    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return super.isClickOutsideBounds(mouseX, mouseY, left, top,  button) && this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button);
    }
}
