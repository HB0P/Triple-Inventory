package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.client.ClientSlotData;
import net.minecraft.client.MinecraftClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class M_MinecraftClient {

    // reset previous selected slot when changing slot manually
    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"
            )
    )
    private void handleInputEvents(CallbackInfo ci) {
        ClientSlotData.INSTANCE.reset();
    }

    // reset previous selected slot when changing slot manually
    @Inject(
            method = "doItemPick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private void doItemPick(CallbackInfo ci) {
        ClientSlotData.INSTANCE.reset();
    }
}