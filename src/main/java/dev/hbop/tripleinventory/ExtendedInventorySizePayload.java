package dev.hbop.tripleinventory;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ExtendedInventorySizePayload(int size) implements CustomPayload {
    
    public static final CustomPayload.Id<ExtendedInventorySizePayload> ID = new CustomPayload.Id<>(TripleInventory.identifier("extended_inventory_size"));
    public static final PacketCodec<RegistryByteBuf, ExtendedInventorySizePayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ExtendedInventorySizePayload::size, ExtendedInventorySizePayload::new);
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
