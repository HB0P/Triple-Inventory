package dev.hbop.tripleinventory.mixin;

import dev.hbop.tripleinventory.helper.ItemInventorySlot;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class M_Item {
    
    // right-clicking shulkers and ender chests opens their inventory
    @Inject(
            method = "onClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (clickType != ClickType.RIGHT) return;
        if (slot instanceof ItemInventorySlot) return;
        
        if (stack.isIn(ItemTags.SHULKER_BOXES)) {
            ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
            if (component == null) return;
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            component.copyTo(stacks);
            int i = 0;
            for (Slot screenSlot : player.currentScreenHandler.slots) {
                if (screenSlot instanceof ItemInventorySlot shulkerSlot) {
                    int index = i;
                    shulkerSlot.enable(stack, slot, 
                            newStack -> {
                                ContainerComponent com = stack.get(DataComponentTypes.CONTAINER);
                                assert com != null;
                                DefaultedList<ItemStack> stks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                                com.copyTo(stks);
                                stks.set(index, newStack);
                                stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stks));
                                },
                            newStack -> !newStack.isIn(ItemTags.SHULKER_BOXES)
                    );
                    shulkerSlot.setStack(stacks.get(i));
                    i++;
                }
            }
            cir.setReturnValue(true);
        }
        
        else if (stack.isOf(Items.ENDER_CHEST)) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler containerScreenHandler) {
                if (containerScreenHandler.getInventory() == player.getEnderChestInventory()) return;
            }
            DefaultedList<ItemStack> stacks = player.getEnderChestInventory().getHeldStacks();
            int i = 0;
            for (Slot screenSlot : player.currentScreenHandler.slots) {
                if (screenSlot instanceof ItemInventorySlot enderSlot) {
                    int index = i;
                    enderSlot.enable(stack, slot,
                            newStack -> player.getEnderChestInventory().setStack(index, newStack),
                            newStack -> true
                    );
                    enderSlot.setStack(stacks.get(i));
                    i++;
                }
            }
            cir.setReturnValue(true);
        }
    }
}