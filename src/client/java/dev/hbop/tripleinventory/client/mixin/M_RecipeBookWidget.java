package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RecipeBookWidget.class)
public abstract class M_RecipeBookWidget {

    @Shadow private int leftOffset;
    
    // move recipe book to give space for tool slots
    @ModifyConstant(
            method = "reset",
            constant = @Constant(
                    intValue = 86, 
                    ordinal = 0
            )
    )
    private int leftOffset(int constant) {
        return constant + 58;
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
