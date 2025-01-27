package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.client.ModKeyBindings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class M_HandledScreen<T extends ScreenHandler> extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundHeight;
    @Shadow protected int backgroundWidth;
    @Shadow @Final protected T handler;
    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Unique
    private static final Identifier EXTENSION_TEXTURE = TripleInventory.identifier("textures/gui/container/inventory_extension.png");

    protected M_HandledScreen(Text title) {
        super(title);
    }
    
    // render tool slots area
    @Inject(
            method = "renderBackground",
            at = @At("TAIL")
    )
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if ((Screen) this instanceof CreativeInventoryScreen) return;
        int height = this.backgroundHeight;
        if ((Screen) this instanceof ShulkerBoxScreen) height--;
        else if ((Screen) this instanceof GenericContainerScreen) height--;
        // left inventory
        int size = TripleInventory.extendedInventorySize();
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 4 - size * 18, this.y + height - 90, 0, 0, 25, 90, 256, 256);
        for (int i = 0; i < size - 2; i++) {
            context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 33 - i * 18, this.y + height - 90, 25, 0, 18, 90, 256, 256);
        }
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 15, this.y + height - 90, 43, 0, 19, 90, 256, 256);
        // right inventory
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth - 4, this.y + height - 90, 194, 0, 19, 90, 256, 256);
        for (int i = 0; i < size - 2; i++) {
            context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth + 15 + i * 18, this.y + height - 90, 213, 0, 18, 90, 256, 256);
        }
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth - 21 + size * 18, this.y + height - 90, 231, 0, 25, 90, 256, 256);
    }
    
    // stop tool slots being considered "out of bounds"
    @Inject(
            method = "isClickOutsideBounds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        int size = TripleInventory.extendedInventorySize();
        if (mouseX > (left - 4 - size * 18) && mouseX < left + backgroundWidth + 4 + size * 18 && mouseY > top + backgroundHeight - 90 && mouseY < top + backgroundHeight) {
            cir.setReturnValue(false);
        }
    }
    
    // allow switching items to tool hotbar by pressing hotkeys
    @Inject(
            method = "handleHotbarKeyPressed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void handleHotbarKeyPressed(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
                for (int i = 0; i < 18; i++) {
                    if (ModKeyBindings.toolHotbarKeys[i].matchesKey(keyCode, scanCode)) {
                        int size = TripleInventory.extendedInventorySize();
                        if ((i % 9) >= size) continue;
                        int slot = i < 9 ? i + 46 : i + 37 + size;
                        if (!this.focusedSlot.hasStack() || this.handler.getSlot(slot).canInsert(this.focusedSlot.getStack())) {
                            this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i + 41, SlotActionType.SWAP);
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
