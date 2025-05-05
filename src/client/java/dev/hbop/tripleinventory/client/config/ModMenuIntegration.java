package dev.hbop.tripleinventory.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.hbop.tripleinventory.TripleInventory;
import dev.hbop.tripleinventory.helper.ShulkerPosition;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModMenuIntegration implements ModMenuApi {
    
    private final OptionFlag GAME_LEAVE = client -> {
        if (client.world == null) return;

        boolean singlePlayer = client.isInSingleplayer();
        ServerInfo serverInfo = client.getCurrentServerEntry();
        client.world.disconnect();
        if (singlePlayer) {
            client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        } else {
            client.disconnect();
        }

        TitleScreen titleScreen = new TitleScreen();
        if (singlePlayer) {
            client.setScreen(titleScreen);
        } else if (serverInfo != null && serverInfo.isRealm()) {
            client.setScreen(new RealmsMainScreen(titleScreen));
        } else {
            client.setScreen(new MultiplayerScreen(titleScreen));
        }
    };
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        ClientConfig.HANDLER.load();
        return parent -> YetAnotherConfigLib.create(ClientConfig.HANDLER, this::getScreenBuilder).generateScreen(parent);
    }
    
    private YetAnotherConfigLib.Builder getScreenBuilder(ClientConfig defaults, ClientConfig config, YetAnotherConfigLib.Builder builder) {
        Option<Boolean> showExtendedInventoryWithRecipeBookOption = createBooleanOption(
                "showExtendedInventoryWithRecipeBook",
                defaults.showExtendedInventoryWithRecipeBook,
                () -> config.showExtendedInventoryWithRecipeBook,
                val -> config.showExtendedInventoryWithRecipeBook = val
        ).build();
        Option<Boolean> scrollToExtendedHotbarOption = createBooleanOption(
                "scrollToExtendedHotbar",
                defaults.scrollToExtendedHotbar,
                () -> config.scrollToExtendedHotbar,
                val -> config.scrollToExtendedHotbar = val
        ).build();
        Option<Boolean> showExtendedHotbarOption = createBooleanOption(
                "showExtendedHotbar",
                defaults.showExtendedHotbar,
                () -> config.showExtendedHotbar,
                val -> config.showExtendedHotbar = val
        )
                .addListener((option, event) ->
                    scrollToExtendedHotbarOption.setAvailable(option.pendingValue())
                )
                .build();
        
        ConfigCategory extendedInventoryCategory = ConfigCategory.createBuilder()
                .name(Text.translatable("config.tripleinventory.category.extendedInventory"))
                .option(showExtendedHotbarOption)
                .option(scrollToExtendedHotbarOption)
                .option(showExtendedInventoryWithRecipeBookOption)
                .option(LabelOption.createBuilder().line(Text.empty()).build())
                .option(ButtonOption.createBuilder()
                        .name(Text.translatable("config.tripleinventory.option.hotkeys"))
                        .text(Text.empty())
                        .description(OptionDescription.of(Text.translatable("config.tripleinventory.option.hotkeys.description")))
                        .action((screen, option) -> MinecraftClient.getInstance().setScreen(new KeybindsScreen(screen, MinecraftClient.getInstance().options)))
                        .build()
                )
                .build();

        Option<Integer> autoReturnCooldownOption = createOption(
                "autoReturnCooldown",
                defaults.autoReturnCooldown,
                () -> config.autoReturnCooldown,
                val -> config.autoReturnCooldown = val,
                opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 200)
                        .step(1)
                        .formatValue(val -> Text.literal(val + " ticks"))
        ).build();
        Option<Boolean> autoReturnAfterCooldownOption = createBooleanOption(
                "autoReturnAfterCooldown",
                defaults.autoReturnAfterCooldown,
                () -> config.autoReturnAfterCooldown,
                val -> {
                    autoReturnCooldownOption.setAvailable(val);
                    config.autoReturnAfterCooldown = val;
                }
        )
                .addListener((option, event) -> 
                        autoReturnCooldownOption.setAvailable(option.available() && option.pendingValue()))
                .build();
        Option<Boolean> autoReturnOnUseOption = createBooleanOption(
                "autoReturnOnUse",
                defaults.autoReturnOnUse,
                () -> config.autoReturnOnUse,
                val -> config.autoReturnOnUse = val
        ).build();
        Option<Boolean> showPreviousSelectedSlotIndicatorOption = createBooleanOption(
                "showPreviousSelectedSlotIndicator",
                defaults.showPreviousSelectedSlotIndicator,
                () -> config.showPreviousSelectedSlotIndicator,
                val -> config.showPreviousSelectedSlotIndicator = val
        ).build();
        Option<Boolean> autoSelectToolsOption = createBooleanOption(
                "autoSelectTools",
                defaults.autoSelectTools,
                () -> config.autoSelectTools,
                val -> config.autoSelectTools = val
        )
                .addListener((option, event) -> {
                    showPreviousSelectedSlotIndicatorOption.setAvailable(option.pendingValue());
                    autoReturnOnUseOption.setAvailable(option.pendingValue());
                    autoReturnAfterCooldownOption.setAvailable(option.pendingValue());
                })
                .build();
        
        ConfigCategory autoToolSelectionCategory = ConfigCategory.createBuilder()
                .name(Text.translatable("config.tripleinventory.category.autoToolSelection"))
                .option(autoSelectToolsOption)
                .option(showPreviousSelectedSlotIndicatorOption)
                .option(autoReturnOnUseOption)
                .option(autoReturnAfterCooldownOption)
                .option(autoReturnCooldownOption)
                .build();

        ConfigCategory shulkerPreviewCategory = ConfigCategory.createBuilder()
                .name(Text.translatable("config.tripleinventory.category.shulkerPreview"))
                .option(createBooleanOption(
                        "colorShulkerBackground",
                        defaults.colorShulkerBackground,
                        () -> config.colorShulkerBackground,
                        val -> config.colorShulkerBackground = val
                ).build())
                .option(
                        Option.<ShulkerPosition>createBuilder()
                                .name(Text.translatable("config.tripleinventory.option.shulkerPosition"))
                                .description((position) -> {
                                    OptionDescription.Builder desc = OptionDescription.createBuilder()
                                        .webpImage(TripleInventory.identifier("textures/config/shulker_preview_" + position.toString().toLowerCase() + ".webp"))
                                        .text(Text.translatable("config.tripleinventory.option.shulkerPosition.description"));
                                    if (position == ShulkerPosition.LEFT_BOTTOM || position == ShulkerPosition.RIGHT_BOTTOM) {
                                        desc.text(
                                                Text.empty(),
                                                Text.translatable("config.tripleinventory.option.shulkerPosition.description.warning.side_bottom").formatted(Formatting.GOLD)
                                        );
                                    }
                                    else if (position == ShulkerPosition.LEFT_TOP || position == ShulkerPosition.RIGHT_TOP) {
                                        desc.text(
                                                Text.empty(),
                                                Text.translatable("config.tripleinventory.option.shulkerPosition.description.warning.side_top").formatted(Formatting.GOLD)
                                        );
                                    }
                                    if (position != config.shulkerPosition) {
                                        desc.text(
                                                Text.empty(),
                                                Text.translatable("config.tripleinventory.option.shulkerPosition.description.warning.game_leave").formatted(Formatting.RED)
                                        );
                                    }
                                    return desc.build();
                                }
                                )
                                .binding(
                                        defaults.shulkerPosition,
                                        () -> config.shulkerPosition,
                                        val -> config.shulkerPosition = val
                                )
                                .controller(opt -> EnumControllerBuilder
                                        .create(opt)
                                        .enumClass(ShulkerPosition.class)
                                        .formatValue((position) -> Text.translatable("config.tripleinventory.enum.shulkerPosition." + position.toString().toLowerCase()))
                                )
                                .flag(GAME_LEAVE)
                                .build()
                )
                .build();

        return builder
                .title(Text.translatable("config.tripleinventory.title"))
                .category(extendedInventoryCategory)
                .category(autoToolSelectionCategory)
                .category(shulkerPreviewCategory);
    }
    
    private <T> Option.Builder<T> createOption(String id, T defaultValue, Supplier<T> getter, Consumer<T> setter, Function<Option<T>, ControllerBuilder<T>> controller) {
        return Option.<T>createBuilder()
                .name(Text.translatable("config.tripleinventory.option." + id))
                .description(OptionDescription.of(Text.translatable("config.tripleinventory.option." + id + ".description")))
                .binding(defaultValue, getter, setter)
                .controller(controller);
    }
    
    private Option.Builder<Boolean> createBooleanOption(String id, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return createOption(id, defaultValue, getter, setter, TickBoxControllerBuilder::create);
    }
}