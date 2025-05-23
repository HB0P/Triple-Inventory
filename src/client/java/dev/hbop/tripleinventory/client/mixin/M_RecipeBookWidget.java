package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.client.config.ClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RecipeBookWidget.class)
public abstract class M_RecipeBookWidget {

    @Shadow private int leftOffset;

    @Shadow protected MinecraftClient client;

    // move recipe book to give space for extended slots
    @ModifyConstant(
            method = "reset",
            constant = @Constant(
                    intValue = 86, 
                    ordinal = 0
            )
    )
    private int leftOffset0(int constant) {
        int size = this.client.world.getExtendedInventorySize();
        if (!ClientConfig.HANDLER.instance().showExtendedInventoryWithRecipeBook || size == 0) return constant;
        return constant + 4 + size * 18;
    }
    
    @ModifyConstant(
            method = "isClickOutsideBounds",
            constant = @Constant(
                    intValue = 147,
                    ordinal = 0
            )
    )
    private int leftOffset1(int constant) {
        int size = this.client.world.getExtendedInventorySize();
        if (!ClientConfig.HANDLER.instance().showExtendedInventoryWithRecipeBook || size > 0) return constant;
        return constant + 6 + size * 18;
    }
    
    /**
     * @author HB0P
     * @reason Allow different left offsets for wide screen
     */
    @Overwrite
    private boolean isWide() {
        return this.leftOffset != 0;
    }
}
