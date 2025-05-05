package dev.hbop.tripleinventory.client.mixin;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.client.ModKeyBindings;
import dev.hbop.tripleinventory.client.config.ClientConfig;
import dev.hbop.tripleinventory.helper.InventoryHelper;
import dev.hbop.tripleinventory.helper.ShulkerPreviewSlot;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
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
    @Unique private Pair<Boolean, Boolean> showExtendedInventory = new Pair<>(true, true);

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
            ShulkerPosition position = ClientConfig.HANDLER.instance().shulkerPosition;
            int height = getCorrectedHeight();
            assert this.client != null && this.client.world != null;
            int size = this.client.world.getExtendedInventorySize();
            int x = position.getX(0, this.backgroundWidth, size);
            int y = position.getY(0, height);
            context.drawText(this.textRenderer, getShulkerPreviewTitle(), x + 8, y + 7, 4210752, false);
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
        assert this.client != null && this.client.world != null;
        int size = this.client.world.getExtendedInventorySize();
        
        // show extended inventory background
        if (size > 0) {
            updateShowExtendedInventory(true);
            if (showExtendedInventory.getLeft()) {
                // left inventory
                context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 4 - size * 18, this.y + height - 90, 0, 0, 25, 90, 256, 256);
                for (int i = 0; i < size - 2; i++) {
                    context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 33 - i * 18, this.y + height - 90, 25, 0, 18, 90, 256, 256);
                }
                context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 15, this.y + height - 90, 43, 0, 19, 90, 256, 256);
            }
            if (showExtendedInventory.getRight()) {
                // right inventory
                context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth - 4, this.y + height - 90, 194, 0, 19, 90, 256, 256);
                for (int i = 0; i < size - 2; i++) {
                    context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth + 15 + i * 18, this.y + height - 90, 213, 0, 18, 90, 256, 256);
                }
                context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth - 21 + size * 18, this.y + height - 90, 231, 0, 25, 90, 256, 256);
            }
        }
        
        // show shulker preview background
        if (showShulkerPreview()) {
            ShulkerPosition position = ClientConfig.HANDLER.instance().shulkerPosition;
            int x = position.getX(this.x, this.backgroundWidth, size);
            int y = position.getY(this.y, height);
            context.drawTexture(
                    RenderLayer::getGuiTextured, 
                    position.getTexture(this.backgroundWidth, height, size), 
                    x, y,
                    0, 0, 
                    176, 78, 
                    256, 256
            );
            if (ClientConfig.HANDLER.instance().colorShulkerBackground) {
                for (int dx = 0; dx < 9; dx++) {
                    for (int dy = 0; dy < 3; dy++) {
                        context.fill(
                                x + 8 + dx * 18,
                                y + 18 + dy * 18,
                                x + 8 + dx * 18 + 16,
                                y + 18 + dy * 18 + 16,
                                getShulkerPreviewColor()
                        );
                    }
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
        assert this.client != null && this.client.world != null;
        int size = this.client.world.getExtendedInventorySize();
        if (size > 0) {
            updateShowExtendedInventory(false);
            int leftAdjust = showExtendedInventory.getLeft() ? 4 + size * 18 : 0;
            int rightAdjust = showExtendedInventory.getRight() ? 4 + size * 18 : 0;
            if (mouseX > left - leftAdjust && mouseX < left + backgroundWidth + rightAdjust && mouseY > top + backgroundHeight - 90 && mouseY < top + backgroundHeight) {
                cir.setReturnValue(false);
            }
        }
        if (showShulkerPreview()) {
            ShulkerPosition position = ClientConfig.HANDLER.instance().shulkerPosition;
            int x = position.getX(this.x, this.backgroundWidth, size);
            int y = position.getY(this.y, getCorrectedHeight());
            if (mouseX > x && mouseX < x + 176 && mouseY > y && mouseY < y + 78) {
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
                assert this.client != null && this.client.world != null;
                int size = this.client.world.getExtendedInventorySize();
                for (int i = 0; i < 18; i++) {
                    if (ModKeyBindings.extendedHotbarKeys[i].matchesKey(keyCode, scanCode)) {
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
            if (slot instanceof ShulkerPreviewSlot shulkerSlot) {
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
    private void updateShowExtendedInventory(boolean refreshSlots) {
        Pair<Boolean, Boolean> prevValue = showExtendedInventory;
        
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
                || screen instanceof StonecutterScreen) 
        {
            if (screen instanceof RecipeBookScreen<?> recipeBookScreen 
                    && recipeBookScreen.recipeBook.isOpen() 
                    && !ClientConfig.HANDLER.instance().showExtendedInventoryWithRecipeBook
            ) {
                showExtendedInventory = new Pair<>(false, false);
            }
            else if (showShulkerPreview()) {
                showExtendedInventory = switch (ClientConfig.HANDLER.instance().shulkerPosition) {
                    case LEFT_BOTTOM: yield new Pair<>(false, true);
                    case RIGHT_BOTTOM: yield new Pair<>(true, false);
                    case LEFT_TOP: yield new Pair<>(getCorrectedHeight() >= 166, true);
                    case RIGHT_TOP: yield new Pair<>(true, getCorrectedHeight() >= 166);
                    default: yield new Pair<>(true, true);
                };
            }
            else {
                showExtendedInventory = new Pair<>(true, true);
            }
        } else {
            showExtendedInventory = new Pair<>(false, false);
        }
        
        if (!refreshSlots) return;
        
        if (showExtendedInventory.getLeft() != prevValue.getLeft()) {
            for (Slot slot : this.handler.slots) {
                if (slot instanceof InventoryHelper.ExtendedSlot extendedSlot) {
                    if (!extendedSlot.isRight) extendedSlot.isEnabled = showExtendedInventory.getLeft();
                }
            }
        }
        if (showExtendedInventory.getRight() != prevValue.getRight()) {
            for (Slot slot : this.handler.slots) {
                if (slot instanceof InventoryHelper.ExtendedSlot extendedSlot) {
                    if (extendedSlot.isRight) extendedSlot.isEnabled = showExtendedInventory.getRight();
                }
            }
        }
    }
    
    @Unique
    private boolean showShulkerPreview() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ShulkerPreviewSlot shulkerSlot && shulkerSlot.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private int getShulkerPreviewColor() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ShulkerPreviewSlot shulkerSlot) {
                return shulkerSlot.getShulkerBoxColor();
            }
        }
        return 0;
    }
    
    @Unique
    private Text getShulkerPreviewTitle() {
        for (Slot slot : handler.slots) {
            if (slot instanceof ShulkerPreviewSlot shulkerSlot) {
                return shulkerSlot.getTitle();
            }
        }
        return null;
    }
}
