package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.client.ModKeyBindings;
import dev.hbop.tripleinventory.client.TripleInventoryClient;
import dev.hbop.tripleinventory.helper.ItemInventorySlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
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
    
    @Unique private static final Identifier EXTENSION_TEXTURE = TripleInventory.identifier("textures/gui/container/inventory_extension.png");
    @Unique private static final Identifier SHULKER_PREVIEW_TEXTURE = TripleInventory.identifier("textures/gui/container/shulker_preview.png");

    protected M_HandledScreen(Text title) {
        super(title);
    }

    // add shulker preview title
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V"
            )
    )
    private void drawForeground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (showShulkerPreview()) {
            context.drawText(this.textRenderer, getShulkerPreviewTitle(), 8 + getShulkerPreviewShift(), getCorrectedHeight() - 4, 4210752, false);
        }
    }
    
    // render extended slots area
    @Inject(
            method = "renderBackground",
            at = @At("TAIL")
    )
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if ((Screen) this instanceof CreativeInventoryScreen) return;
        int height = getCorrectedHeight();
        int shulkerPreviewShift = getShulkerPreviewShift();
        
        // show extended inventory background
        if (showExtendedInventory()) {
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
        
        // show shulker preview background
        if (showShulkerPreview()) {
            context.drawTexture(RenderLayer::getGuiTextured, SHULKER_PREVIEW_TEXTURE, this.x + shulkerPreviewShift, this.y + height - 4, 0, 0, 176, 71, 256, 256);
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 3; y++) {
                    context.fill(
                            this.x + shulkerPreviewShift + 8 + x * 18,
                            this.y + height + 7 + y * 18,
                            this.x + shulkerPreviewShift + 8 + x * 18 + 16,
                            this.y + height + 7 + y * 18 + 16,
                            getShulkerPreviewColor()
                    );
                }
            }
        }
    }
    
    // stop extended slots being considered "out of bounds"
    @Inject(
            method = "isClickOutsideBounds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (showExtendedInventory()) {
            int size = TripleInventory.extendedInventorySize();
            if (mouseX > (left - 4 - size * 18) && mouseX < left + backgroundWidth + 4 + size * 18 && mouseY > top + backgroundHeight - 90 && mouseY < top + backgroundHeight) {
                cir.setReturnValue(false);
            }
        }
        if (showShulkerPreview()) {
            if (mouseX > left && mouseX < left + backgroundWidth && mouseY > top + backgroundHeight && mouseY < top + backgroundHeight + 58) {
                cir.setReturnValue(false);
            }
        }
    }
    
    // allow switching items to the extended hotbar by pressing hotkeys
    @Inject(
            method = "handleHotbarKeyPressed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void handleHotbarKeyPressed(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
                for (int i = 0; i < 18; i++) {
                    if (ModKeyBindings.extendedHotbarKeys[i].matchesKey(keyCode, scanCode)) {
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
    
    // disable shulker slots on exit
    @Inject(
            method = "close",
            at = @At("TAIL")
    )
    private void close(CallbackInfo ci) {
        for (Slot slot : handler.slots) {
            if (slot instanceof ItemInventorySlot shulkerSlot) {
                shulkerSlot.disable();
            }
        }
    }
    
    @Unique
    private int getCorrectedHeight() {
        if ((Screen) this instanceof ShulkerBoxScreen) return this.backgroundHeight - 1;
        else if ((Screen) this instanceof GenericContainerScreen) return this.backgroundHeight - 1;
        else return this.backgroundHeight;
    }
    
    @Unique
    private int getShulkerPreviewShift() {
        if ((Screen) this instanceof BeaconScreen) return 28;
        else if ((Screen) this instanceof MerchantScreen) return 100;
        else return 0;
    }
    
    @Unique
    private boolean showExtendedInventory() {
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        if (
                screen instanceof AbstractFurnaceScreen<?>
                || screen instanceof BeaconScreen
                || screen instanceof BrewingStandScreen
                || screen instanceof CartographyTableScreen
                || screen instanceof CrafterScreen
                || screen instanceof CraftingScreen
                || screen instanceof EnchantmentScreen
                || screen instanceof ForgingScreen<?>
                || screen instanceof Generic3x3ContainerScreen
                || screen instanceof GenericContainerScreen
                || screen instanceof GrindstoneScreen
                || screen instanceof HopperScreen
                || screen instanceof HorseScreen
                || screen instanceof InventoryScreen
                || screen instanceof LoomScreen
                || screen instanceof MerchantScreen
                || screen instanceof ShulkerBoxScreen
                || screen instanceof StonecutterScreen
        ) {
            if (screen instanceof RecipeBookScreen<?> recipeBookScreen) {
                return !recipeBookScreen.recipeBook.isOpen() || TripleInventoryClient.CONFIG.showExtendedInventoryWithRecipeBook();
            }
            return true;
        }
        return false;
    }
    
    @Unique
    private boolean showShulkerPreview() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ItemInventorySlot shulkerSlot && shulkerSlot.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private int getShulkerPreviewColor() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ItemInventorySlot shulkerSlot) {
                return shulkerSlot.getShulkerBoxColor();
            }
        }
        return 0;
    }
    
    @Unique
    private Text getShulkerPreviewTitle() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ItemInventorySlot shulkerSlot) {
                return shulkerSlot.getTitle();
            }
        }
        return null;
    }
}
