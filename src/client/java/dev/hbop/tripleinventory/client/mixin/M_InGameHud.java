package dev.hbop.tripleinventory.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.client.ClientSlotData;
import dev.hbop.tripleinventory.client.config.ClientConfig;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed);
    @Shadow @Nullable protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private static Identifier HOTBAR_TEXTURE;
    @Shadow @Final private static Identifier HOTBAR_SELECTION_TEXTURE;
    @Unique private static final Identifier PREVIOUS_HOTBAR_SELECTION_TEXTURE = TripleInventory.identifier("hud/previous_hotbar_selection");

    // render the extended hotbar
    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ClientConfig.HANDLER.instance().showExtendedHotbar) return;
        
        PlayerEntity player = this.getCameraPlayer();
        assert player != null;
        
        // left and right hotbar
        int size = player.getWorld().getExtendedInventorySize();
        if (size > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 0, 0, context.getScaledWindowWidth() / 2 - 97 - 20 * size, context.getScaledWindowHeight() - 22, -19 + 20 * size, 22);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 161, 0, context.getScaledWindowWidth() / 2 - 116, context.getScaledWindowHeight() - 22, 21, 22);

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 0, 0, context.getScaledWindowWidth() / 2 + 95, context.getScaledWindowHeight() - 22, -19 + 20 * size, 22);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 161, 0, context.getScaledWindowWidth() / 2 + 76 + 20 * size, context.getScaledWindowHeight() - 22, 21, 22);
        }
        
        // previous selected slot
        if (ClientSlotData.INSTANCE.hasPreviouslySelectedSlot() && ClientConfig.HANDLER.instance().showPreviousSelectedSlotIndicator) {
            int prevSelectedSlot = ClientSlotData.INSTANCE.getPreviouslySelectedSlot();
            int x;
            if (prevSelectedSlot <= 8) {
                x = context.getScaledWindowWidth() / 2 - 92 + prevSelectedSlot * 20;
            }
            else if (prevSelectedSlot <= 49) {
                x = context.getScaledWindowWidth() / 2 - 118 - (prevSelectedSlot - 41) * 20;
            }
            else {
                x = context.getScaledWindowWidth() / 2 + 94 + (prevSelectedSlot - 50) * 20;
            }
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PREVIOUS_HOTBAR_SELECTION_TEXTURE, x, context.getScaledWindowHeight() - 23, 24, 23);
        }
        
        // selected slot
        int selectedSlot = player.getInventory().getSelectedSlot();
        if (selectedSlot >= 41 && selectedSlot <= 58) {
            int x;
            if (selectedSlot <= 49) {
                x = context.getScaledWindowWidth() / 2 - 118 - (selectedSlot - 41) * 20;
            }
            else {
                x = context.getScaledWindowWidth() / 2 + 94 + (selectedSlot - 50) * 20;
            }
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, x, context.getScaledWindowHeight() - 23, 24, 23);
        }
        
        // items
        for (int i = 0; i < 18; i++) {
            if (i % 9 >= size) continue;
            ItemStack stack = player.getInventory().getStack(i + 41);
            int x;
            if (i < 9) {
                x = context.getScaledWindowWidth() / 2 - 114 - (i % 9 * 20);
            }
            else {
                x = context.getScaledWindowWidth() / 2 + 98 + (i % 9 * 20);
            }
            renderHotbarItem(context, x, context.getScaledWindowHeight() - 19, tickCounter, this.getCameraPlayer(), stack, 1);
        }
    }
    
    // shift the offhand when showing extended hotbar
    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/gui/hud/InGameHud;HOTBAR_OFFHAND_LEFT_TEXTURE:Lnet/minecraft/util/Identifier;"
                    )
            )
    )
    private void renderOffhandLeft(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        int size = this.getCameraPlayer().getWorld().getExtendedInventorySize();
        if (ClientConfig.HANDLER.instance().showExtendedHotbar && size > 0) {
            instance.drawGuiTexture(pipeline, sprite, x - 6 - size * 20, y, width, height);
        }
        else {
            instance.drawGuiTexture(pipeline, sprite, x, y, width, height);
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/gui/hud/InGameHud;HOTBAR_OFFHAND_RIGHT_TEXTURE:Lnet/minecraft/util/Identifier;"
                    )
            )
    )
    private void renderOffhandRight(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        int size = this.getCameraPlayer().getWorld().getExtendedInventorySize();
        if (ClientConfig.HANDLER.instance().showExtendedHotbar && size > 0) {
            instance.drawGuiTexture(pipeline, sprite, x + 6 + size * 20, y, width, height);
        }
        else {
            instance.drawGuiTexture(pipeline, sprite, x, y, width, height);
        }
    }
    
    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
                    ordinal = 1
            )
    )
    private void renderOffhandItemLeft(InGameHud hud, DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed) {
        int size = this.getCameraPlayer().getWorld().getExtendedInventorySize();
        if (ClientConfig.HANDLER.instance().showExtendedHotbar && size > 0) {
            renderHotbarItem(context, x - 6 - size * 20, y, tickCounter, player, stack, seed);
        }
        else {
            renderHotbarItem(context, x, y, tickCounter, player, stack, seed);
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
                    ordinal = 2
            )
    )
    private void renderOffhandItemRight(InGameHud hud, DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed) {
        int size = this.getCameraPlayer().getWorld().getExtendedInventorySize();
        if (ClientConfig.HANDLER.instance().showExtendedHotbar && size > 0) {
            renderHotbarItem(context, x + 6 + size * 20, y, tickCounter, player, stack, seed);
        }
        else {
            renderHotbarItem(context, x, y, tickCounter, player, stack, seed);
        }
    }
}
