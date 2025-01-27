package dev.hbop.tripleinventory.client;

import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.helper.InventoryHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    
    public static final KeyBinding selectWeaponKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tripleinventory.select_weapon",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "key.categories.tripleinventory.tool_hotbar"
    ));
    public static final KeyBinding[] toolHotbarKeys = new KeyBinding[18];
    
    public static void registerKeyBindings() {
        for (int i = 0; i < 18; i++) {
            toolHotbarKeys[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.tripleinventory.tool_hotbar." + (i + 1),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories.tripleinventory.tool_hotbar"
            ));
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            assert player != null;
            while (selectWeaponKey.wasPressed()) {
                int i = InventoryHelper.getSlotInHotbarMatching(player.getInventory(), stack -> 
                        stack.isIn(ItemTags.SWORDS)
                        || stack.isIn(ItemTags.AXES)
                        || stack.isOf(Items.MACE)
                        || stack.isOf(Items.TRIDENT)
                );
                if (i != -1) {
                    ClientSlotData.INSTANCE.set(player.getInventory().selectedSlot, false);
                    player.getInventory().selectedSlot = i;
                }
            }
            for (int i = 0; i < 18; i++) {
                while (toolHotbarKeys[i].wasPressed()) {
                    if ((i % 9) >= TripleInventory.extendedInventorySize()) break;
                    ClientSlotData.INSTANCE.set(player.getInventory().selectedSlot, false);
                    player.getInventory().selectedSlot = 41 + i;
                }
            }
        });
    }
}