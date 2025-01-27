package dev.hbop.tripleinventory.client;

import dev.hbop.tripleinventory.client.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;

public class TripleInventoryClient implements ClientModInitializer {

    public static final ClientConfig CONFIG = ClientConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
    }
}
