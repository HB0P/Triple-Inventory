package dev.hbop.tripleinventory.mixin;

import dev.hbop.tripleinventory.WorldInject;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class M_World implements WorldInject {
    
}