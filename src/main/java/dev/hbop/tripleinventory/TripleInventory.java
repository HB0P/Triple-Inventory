package dev.hbop.tripleinventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleInventory implements ModInitializer {

    public static final String MOD_ID = "tripleinventory";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static final GameRules.Key<GameRules.IntRule> EXTENDED_INVENTORY_SIZE = GameRuleRegistry.register(
            "extendedInventorySize", 
            GameRules.Category.PLAYER, 
            GameRuleFactory.createIntRule(
                    3, 0, 9,
                    (server, gameRule) -> {
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            ServerPlayNetworking.send(player, new ExtendedInventorySizePayload(gameRule.get()));
                        }
                    }
            )
    );

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ExtendedInventorySizePayload.ID, ExtendedInventorySizePayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            int size = minecraftServer.getGameRules().getInt(EXTENDED_INVENTORY_SIZE);
            ServerPlayNetworking.send(serverPlayNetworkHandler.player, new ExtendedInventorySizePayload(size));
        });
    }

    public static Identifier identifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    @SuppressWarnings("unused")
    public static void log(Object object) {
        if (object == null) LOGGER.info(null);
        else LOGGER.info(object.toString());
    }
}
