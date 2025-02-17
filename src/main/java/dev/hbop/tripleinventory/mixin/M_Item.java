package dev.hbop.tripleinventory.mixin;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class M_Item {
    
    // right-clicking shulkers opens their inventory
    @Inject(
            method = "onClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isIn(ItemTags.SHULKER_BOXES) && clickType == ClickType.RIGHT) {
            ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
            if (component == null) return;
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            component.copyTo(stacks);
            int i = 0;
            for (Slot screenSlot : player.currentScreenHandler.slots) {
                if (screenSlot instanceof InventoryHelper.ShulkerSlot shulkerSlot) {
                    shulkerSlot.enable(stack, i);
                    shulkerSlot.setStack(stacks.get(i));
                    i++;
                }
            }
            cir.setReturnValue(true);
        }
    }
}