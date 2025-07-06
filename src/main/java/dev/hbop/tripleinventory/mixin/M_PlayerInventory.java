package dev.hbop.tripleinventory.mixin;

import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class M_PlayerInventory {

    @Shadow @Final private DefaultedList<ItemStack> main;

    @Shadow @Final public PlayerEntity player;
    @Unique private final DefaultedList<ItemStack> extended = DefaultedList.ofSize(72, ItemStack.EMPTY);

    @Shadow public abstract ItemStack getStack(int slot);
    @Shadow protected abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow private int selectedSlot;

    // allow getting selected stack from extended inventory
    @Redirect(
            method = "getSelectedStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"
            )
    )
    private Object getSelectedStack(DefaultedList<ItemStack> instance, int index) {
        return getStack(index);
    }
    
    // allow setting selected stack to extended inventory
    @Redirect(
            method = "setSelectedStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object setSelectedStack(DefaultedList<ItemStack> instance, int index, Object element) {
        ItemStack previousStack  = getStack(index);
        setStack(index, (ItemStack) element);
        return previousStack;
    }
    
    // getHotbarSize()
    // getMainStacks()

    // include extended inventory in empty slots (old, removed restrict to equipment checks)
    @Inject(
            method = "getEmptySlot",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getEmptySlot(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 43; i < 43 + extended.size(); i++) {
                if (InventoryHelper.isSlotEnabled(i, this.player.getWorld()) && this.getStack(i).isEmpty()) {
                    cir.setReturnValue(i);
                    return;
                }
            }
        }
    }

    // fix to prevent adding creative pick block into extended inventory 
    @Redirect(
            method = "swapStackWithHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    private Object swapStackWithHotbar(DefaultedList<?> instance, int index, Object element) {
        if (index < this.main.size()) {
            this.setStack(index, (ItemStack) element);
        }
        return null;
    }

    // fix to allow pick-block to extended inventory 
    @Redirect(
            method = "swapSlotWithHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    @SuppressWarnings("SameReturnValue")
    private Object swapSlotWithHotbarSet(DefaultedList<?> instance, int index, Object element) {
        this.setStack(index, (ItemStack) element);
        return null;
    }

    // fix to allow pick-block from extended inventory 
    @Redirect(
            method = "swapSlotWithHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private Object swapSlotWithHotbarGet(DefaultedList<?> instance, int index) {
        return this.getStack(index);
    }
    
    // allow extended hotbar indexes 
    @Inject(
            method = "isValidHotbarIndex",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void isValidHotbarIndex(int slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot >= 41 && slot <= 58) { 
            cir.setReturnValue(true);
        }
    }

    // allow pick-block from extended inventory 
    @Inject(
            method = "getSlotWithStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getSlotWithStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 43; i < 43 + extended.size(); i++) {
                if (InventoryHelper.isSlotEnabled(i, this.player.getWorld()) && !this.getStack(i).isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, this.getStack(i))) {
                    cir.setReturnValue(i);
                    return;
                }
            }
        }
    }
    
    // include extended inventory
    @Inject(
            method = "getMatchingSlot",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getMatchingSlot(RegistryEntry<Item> item, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 43; i < 43 + extended.size(); i++) {
                if (InventoryHelper.isSlotEnabled(i, this.player.getWorld())) {
                    ItemStack itemStack = getStack(i);
                    if (!itemStack.isEmpty()
                            && itemStack.itemMatches(item)
                            && PlayerInventory.usableWhenFillingSlot(itemStack)
                            && (stack.isEmpty() || ItemStack.areItemsAndComponentsEqual(stack, itemStack))) {
                        cir.setReturnValue(i);
                    }
                }
            }
        }
    }
    
    // getSwappableHotbarSlot()

    // allow picking up items into extended inventory 
    @Inject(
            method = "getOccupiedSlotWithRoomForStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 43; i < 43 + extended.size(); i++) {
                if (InventoryHelper.isSlotEnabled(i, this.player.getWorld()) && this.canStackAddMore(this.getStack(i), stack)) {
                    cir.setReturnValue(i);
                    return;
                }
            }
        }
    }
    
    // update items in extended inventory
    @Inject(
            method = "updateItems",
            at = @At("TAIL")
    )
    private void updateItems(CallbackInfo ci) {
        for (int i = 43; i < 43 + this.extended.size(); i++) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                itemStack.inventoryTick(this.player.getWorld(), this.player, i == this.selectedSlot ? EquipmentSlot.MAINHAND : null);
            }
        }
    }

    // fix to allow inserting items into extended inventory 
    @Redirect(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object insertStackSet(DefaultedList<?> instance, int index, Object element) {
        this.setStack(index, (ItemStack) element);
        return null;
    }

    // fix to allow inserting items into extended inventory 
    @Redirect(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"
            )
    )
    private Object insertStackGet(DefaultedList<?> instance, int index) {
        return this.getStack(index);
    }
    
    @Inject(
            method = "removeStack(II)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (slot >= 43 && slot < 43 + extended.size()) {
            cir.setReturnValue(Inventories.splitStack(this.extended, slot - 43, amount));
        }
    }
    
    @Inject(
            method = "removeOne",
            at = @At("TAIL"),
            cancellable = true
    )
    private void removeOne(ItemStack stack, CallbackInfo ci) {
        for (int i = 43; i < 43 + this.extended.size(); i++) {
            if (this.getStack(i) == stack) {
                this.setStack(i, ItemStack.EMPTY);
                ci.cancel();
            }
        }
    }
    
    @Inject(
            method = "removeStack(I)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot >= 43 && slot < 43 + extended.size()) {
            ItemStack itemStack = this.getStack(slot);
            this.setStack(slot, ItemStack.EMPTY);
            cir.setReturnValue(itemStack);
        }
    }
    
    // include extended inventory when setting stacks
    @Inject(
            method = "setStack",
            at = @At("TAIL")
    )
    private void setStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (slot >= 43) {
            this.extended.set(slot - 43, stack);
        }
    }
    
    @Inject(
            method = "writeData",
            at = @At("TAIL")
    )
    private void writeData(WriteView.ListAppender<StackWithSlot> list, CallbackInfo ci) {
        for (int i = 0; i < this.extended.size(); i++) {
            ItemStack itemStack = this.extended.get(i);
            if (!itemStack.isEmpty()) {
                list.add(new StackWithSlot(i + 36, itemStack));
            }
        }
    }
    
    @Inject(
            method = "readData",
            at = @At("TAIL")
    )
    public void readData(ReadView.TypedListReadView<StackWithSlot> list, CallbackInfo ci) {
        for (StackWithSlot stackWithSlot : list) {
            int slot = stackWithSlot.slot();
            if (slot >= 36 && slot < 36 + extended.size()) {
                this.extended.set(slot - 36, stackWithSlot.stack());
            }
        }
    }

    // include extended slots inventory size 
    @Inject(
            method = "size",
            at = @At("RETURN"),
            cancellable = true
    )
    private void size(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + extended.size());
    }

    // include extended slots in empty calculation (old, added check at start)
    @Inject(
            method = "isEmpty",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            for (ItemStack itemStack : extended) {
                if (!itemStack.isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    
    // include extended slots when getting stack
    @Inject(
            method = "getStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot >= 43) {
            cir.setReturnValue(this.extended.get(slot - 43));
        }
    }
    
    // include extended slots when dropping all slots
    @Inject(
            method = "dropAll",
            at = @At("TAIL")
    )
    private void dropAll(CallbackInfo ci) {
        for (int i = 43; i < 43 + this.extended.size(); i++) {
            ItemStack itemStack = getStack(i);
            if (!itemStack.isEmpty()) {
                this.player.dropItem(itemStack, true, false);
                setStack(i, ItemStack.EMPTY);
            }
        }
    }
    
    // include extended slots when clearing
    @Inject(
            method = "clear",
            at = @At("TAIL")
    )
    private void clear(CallbackInfo ci) {
        this.extended.clear();
    }
    
    // populateRecipeFinder()
}
