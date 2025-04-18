package dev.hbop.tripleinventory.client.config;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = TripleInventory.MOD_ID)
@Config(name = TripleInventory.MOD_ID + "-client", wrapperName = "ClientConfig")
public class ClientConfigModel {
    
    @SectionHeader("visuals")
    public boolean showExtendedHotbar = true;
    public boolean showPreviousSelectedSlotIndicator = true;
    public boolean showExtendedInventoryWithRecipeBook = true;
    
    @SectionHeader("hotbarNavigation")
    public boolean scrollToExtendedHotbar = true;
    public boolean autoSelectTools = false;
    public boolean autoReturnOnUse = false;
    public boolean autoReturnAfterCooldown = false;
    @RangeConstraint(min = 1, max = 1200)
    public int autoReturnCooldown = 40;
    
    @SectionHeader("shulkerPreview")
    public boolean colorShulkerBackground = true;
    @Sync(Option.SyncMode.INFORM_SERVER)
    public ShulkerPosition shulkerPosition = ShulkerPosition.BOTTOM_MIDDLE;
}
