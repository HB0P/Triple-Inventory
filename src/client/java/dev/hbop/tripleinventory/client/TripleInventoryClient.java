package dev.hbop.tripleinventory.client;

import dev.hbop.tripleinventory.ExtendedInventorySizePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class TripleInventoryClient implements ClientModInitializer {
    
    public static int syncedExtendedInventorySize;
    
    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
        ClientPlayNetworking.registerGlobalReceiver(ExtendedInventorySizePayload.ID, (payload, context) ->
            context.client().execute(() ->
                syncedExtendedInventorySize = payload.size()
            )
        );
    }
}
