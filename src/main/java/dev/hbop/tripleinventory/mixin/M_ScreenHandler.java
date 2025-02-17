package dev.hbop.tripleinventory.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ScreenHandler.class)
public abstract class M_ScreenHandler {

    // allow swapping items to extended inventory
    @ModifyConstant(
            method = "internalOnSlotClick",
            constant = @Constant(intValue = 9)
    )
    private int modify9(int constant) {
        return 59;
    }
}