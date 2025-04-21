package dev.hbop.tripleinventory;

public interface InventorySizeProvider {
    
    default int getExtendedInventorySize() {
        return 0;
    }
}
