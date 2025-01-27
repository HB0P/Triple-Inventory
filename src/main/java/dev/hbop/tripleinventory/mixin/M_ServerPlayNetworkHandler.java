package dev.hbop.tripleinventory.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class M_ServerPlayNetworkHandler {
    
    // prevent invalid selected slots on the server
    @Redirect(
            method = "onUpdateSelectedSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;getHotbarSize()I"
            )
    )
    private int getHotbarSize() {
        return 59;
    }
}
