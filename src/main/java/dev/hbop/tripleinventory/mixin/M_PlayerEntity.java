package dev.hbop.tripleinventory.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity extends LivingEntity {

    @Shadow @Final PlayerInventory inventory;

    protected M_PlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // fix for swapping items with offhand from extended hotbar
    @Redirect(
            method = "equipStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;onEquipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 0
            )
    )
    private void equipStack1(PlayerEntity instance, EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
        this.inventory.setStack(this.inventory.selectedSlot, newStack);
        this.onEquipStack(slot, oldStack, newStack);
    }

    // fix for swapping items with offhand from extended hotbar
    @Redirect(
            method = "equipStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    private Object equipStack2(DefaultedList<?> instance, int index, Object element) {
        return this.inventory.getStack(index);
    }
}