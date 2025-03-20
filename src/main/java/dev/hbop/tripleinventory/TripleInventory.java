package dev.hbop.tripleinventory;

import dev.hbop.tripleinventory.config.MainConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleInventory implements ModInitializer {

    public static final String MOD_ID = "tripleinventory";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final MainConfig CONFIG = MainConfig.createAndLoad();

    @Override
    public void onInitialize() {

    }

    public static boolean restrictExtendedInventoryToEquipment() {
        return CONFIG.restrictExtendedInventoryToEquipment();
    }

    public static boolean restrictExtendedHotbarToEquipment() {
        return CONFIG.restrictExtendedHotbarToEquipment();
    }

    public static int extendedInventorySize() {
        int size = CONFIG.extendedInventorySize();
        if (size < 0 || size > 9) return 0;
        return size;
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
