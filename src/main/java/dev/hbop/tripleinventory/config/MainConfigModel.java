package dev.hbop.tripleinventory.config;

import dev.hbop.tripleinventory.TripleInventory;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Sync;

@Config(name = TripleInventory.MOD_ID, wrapperName = "MainConfig")
public class MainConfigModel {
    
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean restrictExtendedInventoryToEquipment = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean restrictExtendedHotbarToEquipment = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public int extendedInventorySize = 3;
}