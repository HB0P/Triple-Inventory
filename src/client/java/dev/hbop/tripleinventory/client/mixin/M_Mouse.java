package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.client.ClientSlotData;
import dev.hbop.tripleinventory.client.config.ClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public abstract class M_Mouse {

    @Shadow @Final private MinecraftClient client;

    @Redirect(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/input/Scroller;scrollCycling(DII)I"
            )
    )
    private int scrollCycling(double amount, int selectedSlot, int hotbarSize) {
        ClientSlotData.INSTANCE.reset();
        int size = this.client.world.getExtendedInventorySize();
        
        if (ClientConfig.HANDLER.instance().scrollToExtendedHotbar && ClientConfig.HANDLER.instance().showExtendedHotbar && size > 0) {
            int slotPosition;
            if (selectedSlot >= 0 && selectedSlot <= 8) {
                slotPosition = selectedSlot + size;
            } else if (selectedSlot >= 41 && selectedSlot < 41 + size) {
                slotPosition = 40 + size - selectedSlot;
            } else if (selectedSlot >= 50 && selectedSlot < 50 + size) {
                slotPosition = selectedSlot - 41 + size;
            } else {
                if (amount > 0) return 8;
                else return 0;
            }
            
            slotPosition = Scroller.scrollCycling(amount, slotPosition, 9 + size * 2);

            if (slotPosition >= size && slotPosition < size + 9) {
                selectedSlot = slotPosition - size;
            } else if (slotPosition >= 0 && slotPosition < size) {
                selectedSlot = 40 + size - slotPosition;
            } else if (slotPosition >= size + 9) {
                selectedSlot = slotPosition + 41 - size;
            }
            
            return selectedSlot;
        }
        else if (selectedSlot <= 8) {
            return Scroller.scrollCycling(amount, selectedSlot, hotbarSize);
        }
        else {
            if (amount > 0) return 8;
            else return 0;
        }
    }
}
