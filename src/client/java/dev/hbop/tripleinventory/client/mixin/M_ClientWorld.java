package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.client.InventorySizeSetter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientWorld.class)
public abstract class M_ClientWorld extends World implements InventorySizeSetter {
    
    @Unique private int extendedInventorySize;
    
    protected M_ClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }
    
    @Override
    public int getExtendedInventorySize() {
        return extendedInventorySize;
    }
    
    @Override
    public void setExtendedInventorySize(int size) {
        this.extendedInventorySize = size;
    }
}