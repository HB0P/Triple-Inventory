package dev.hbop.tripleinventory.client.config;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

public class ClientConfig {
    
    public static final ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(TripleInventory.identifier("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("tripleinventory.json"))
                    .build()
            )
            .build();
    
    // visuals
    @SerialEntry public boolean showExtendedHotbar = true;
    @SerialEntry public boolean showPreviousSelectedSlotIndicator = true;
    @SerialEntry public boolean showExtendedInventoryWithRecipeBook = true;
    
    // hotbar navigation
    @SerialEntry public boolean scrollToExtendedHotbar = true;
    @SerialEntry public boolean autoSelectTools = false;
    @SerialEntry public boolean autoReturnOnUse = false;
    @SerialEntry public boolean autoReturnAfterCooldown = false;
    @SerialEntry public int autoReturnCooldown = 40;
    
    // shulker preview
    @SerialEntry public boolean colorShulkerBackground = true;
    @SerialEntry public ShulkerPosition shulkerPosition = ShulkerPosition.BOTTOM;
}
