package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.TripleInventory;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class M_CreativeInventoryScreen extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    @Shadow protected abstract boolean isClickInTab(ItemGroup group, double mouseX, double mouseY);

    @Shadow private static ItemGroup selectedTab;
    @Unique private static final Identifier HOTBAR_TEXTURE = TripleInventory.identifier("textures/gui/container/creative_inventory_hotbar.png");
    
    public M_CreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // do not try to render extended slots in the creative inventory
    @Redirect(
            method = "setSelectedTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;size()I"
            )
    )
    private int getSlotsSize(DefaultedList<?> instance) {
        return 45;
    }
    
    // add extended hotbar slots to inventory screen
    @Inject(
            method = "setSelectedTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z",
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;deleteItemSlot:Lnet/minecraft/screen/slot/Slot;"
                    )
            )
    )
    private void addSlots(ItemGroup group, CallbackInfo ci) {
        int size = this.client.world.getExtendedInventorySize();
        for (int i = 0; i < size; i++) {
            Slot slot = new CreativeInventoryScreen.CreativeSlot(this.client.player.playerScreenHandler.slots.get(i + 45), i + 45, 78 - 18 * i, 173);
            this.handler.slots.add(slot);
        }

        for (int i = 0; i < size; i++) {
            Slot slot = new CreativeInventoryScreen.CreativeSlot(this.client.player.playerScreenHandler.slots.get(i + 54), i + 54, 101 + 18 * i, 173);
            this.handler.slots.add(slot);
        }
    }
    
    // add extended hotbar texture
    @Inject(
            method = "drawBackground",
            at = @At("TAIL")
    )
    private void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (selectedTab.getType() != ItemGroup.Type.INVENTORY) return;
        int size = this.client.world.getExtendedInventorySize();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, this.x + 88 - size * 18, this.y + 165, 0, 0, 7, 32, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, this.x + 100 + size * 18, this.y + 165, 48, 0, 7, 32, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, this.x + 95, this.y + 165, 25, 0, 5, 32, 256, 256);
        for (int i = 0; i < size; i++) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, this.x + 77 - i * 18, this.y + 165, 7, 0, 18, 32, 256, 256);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, this.x + 100 + i * 18, this.y + 165, 30, 0, 18, 32, 256, 256);
        }
    }
    
    // ensure extended hotbar is not considered outside bounds
    @Redirect(
            method = "isClickOutsideBounds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;isClickInTab(Lnet/minecraft/item/ItemGroup;DD)Z"
            )
    )
    private boolean isClickInHotbar(CreativeInventoryScreen instance, ItemGroup group, double mouseX, double mouseY) {
        if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
            int size = this.client.world.getExtendedInventorySize();
            double x = mouseX - this.x;
            double y = mouseY - this.y;
            if (x > 88 - size * 18 && x < 107 + size * 18 && y > 165 && y < 197) {
                return true;
            }
        }
        return this.isClickInTab(group, mouseX, mouseY);
    }
}