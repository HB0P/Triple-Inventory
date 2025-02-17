package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.client.TripleInventoryClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RecipeBookWidget.class)
public abstract class M_RecipeBookWidget {

    @Shadow private int leftOffset;
    
    // move recipe book to give space for extended slots
    @ModifyConstant(
            method = "reset",
            constant = @Constant(
                    intValue = 86, 
                    ordinal = 0
            )
    )
    private int leftOffset0(int constant) {
        if (!TripleInventoryClient.CONFIG.showExtendedInventoryWithRecipeBook()) return constant;
        return constant + 4 + TripleInventory.extendedInventorySize() * 18;
    }
    
    @ModifyConstant(
            method = "isClickOutsideBounds",
            constant = @Constant(
                    intValue = 147,
                    ordinal = 0
            )
    )
    private int leftOffset1(int constant) {
        if (!TripleInventoryClient.CONFIG.showExtendedInventoryWithRecipeBook()) return constant;
        return constant + 6 + TripleInventory.extendedInventorySize() * 18;
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
