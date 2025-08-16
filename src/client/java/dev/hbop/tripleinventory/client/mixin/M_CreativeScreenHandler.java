package dev.hbop.tripleinventory.client.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public abstract class M_CreativeScreenHandler extends ScreenHandler {
    
    protected M_CreativeScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    /*@Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void addExtendedHotbarSlots(PlayerEntity player, CallbackInfo ci) {
        int size = player.getWorld().getExtendedInventorySize();
        for (int i = 0; i < size; i++) {
            Slot slot = new Slot(player.getInventory(), i + 41, 78 - 18 * i, 173);
            this.slots.add(slot);
        }

        for (int i = 0; i < size; i++) {
            Slot slot = new Slot(player.getInventory(), i + 50, 101 + 18 * i, 173);
            this.slots.add(slot);
        }
    }*/

    /*@Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void addSlots(PlayerEntity player, CallbackInfo ci) {
        int size = player.getWorld().getExtendedInventorySize();
        for (int i = 0; i < size; i++) {
            Slot slot = new CreativeInventoryScreen.CreativeSlot(player.playerScreenHandler.slots.get(i + 45), i + 45, 78 - 18 * i, 173);
            this.slots.add(slot);
        }

        for (int i = 0; i < size; i++) {
            Slot slot = new CreativeInventoryScreen.CreativeSlot(player.playerScreenHandler.slots.get(i + 54), i + 54, 101 + 18 * i, 173);
            this.slots.add(slot);
        }
    }*/
}