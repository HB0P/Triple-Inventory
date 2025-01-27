package dev.hbop.tripleinventory.client.config;

import dev.hbop.tripleinventory.TripleInventory;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = TripleInventory.MOD_ID)
@Config(name = TripleInventory.MOD_ID + "-client", wrapperName = "ClientConfig")
public class ClientConfigModel {
    
    public boolean showToolHotbar = true;
    public boolean scrollToToolHotbar = true;
    public boolean autoSelectTools = false;
    public boolean autoReturnOnUse = false;
    public boolean autoReturnAfterCooldown = false;
    @RangeConstraint(min = 1, max = 1200)
    public int autoReturnCooldown = 40;
}
