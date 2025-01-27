package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LoomScreen.class)
public abstract class M_LoomScreen extends HandledScreen<LoomScreenHandler> {
    public M_LoomScreen(LoomScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * @author HB0P
     * @reason Uses super method
     */
    @Overwrite
    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button);
    }
}
