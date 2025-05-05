package dev.hbop.tripleinventory;

import dev.hbop.tripleinventory.helper.ShulkerPosition;

public interface WorldInject {
    
    default int getExtendedInventorySize() {
        return 0;
    }
    
    default ShulkerPosition getShulkerPosition() {
        return null;
    }
}
