package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeInventoryScreen.class)
public abstract class M_CreativeInventoryScreen extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    
    
    public M_CreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // do not try to render extended slots in the creative inventory
    @Redirect(
            method = "setSelectedTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;size()I"
            )
    )
    private int getSlotsSize(DefaultedList<?> instance) {
        return instance.size() - (this.client.world.getExtendedInventorySize() * 8);
    }
}