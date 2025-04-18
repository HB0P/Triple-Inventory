package dev.hbop.tripleinventory.helper;

import dev.hbop.tripleinventory.TripleInventory;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public enum ShulkerPosition {
    
    TOP_LEFT((left, width) -> left, (top, height) -> top - 68, (width, height) -> width == 176 ? "top_fill" : "top_left"),
    TOP_MIDDLE((left, width) -> left + width / 2 - 88, (top, height) -> top - 68, (width, height) -> width == 176 ? "top_fill" : "top_middle"),
    TOP_RIGHT((left, width) -> left + width - 176, (top, height) -> top - 68, (width, height) -> width == 176 ? "top_fill" : "top_right"),
    BOTTOM_LEFT((left, width) -> left, (top, height) -> top + height - 11, (width, height) -> width == 176 ? "bottom_fill" : "bottom_left"),
    BOTTOM_MIDDLE((left, width) -> left + width / 2 - 88, (top, height) -> top + height - 11, (width, height) -> width == 176 ? "bottom_fill" : "bottom_middle"),
    BOTTOM_RIGHT((left, width) -> left + width - 176, (top, height) -> top + height - 11, (width, height) -> width == 176 ? "bottom_fill" : "bottom_right"),
    LEFT_TOP((left, width) -> left - 166, (top, height) -> top, (width, height) -> "left_top"),
    LEFT_BOTTOM((left, width) -> left - 166, (top, height) -> top + height - 78, (width, height) -> "left_bottom"),
    RIGHT_TOP((left, width) -> left + width - 10, (top, height) -> top, (width, height) -> "right_top"),
    RIGHT_BOTTOM((left, width) -> left + width - 10, (top, height) -> top + height - 78, (width, height) -> "right_bottom")
    ;
    
    private final BiFunction<Integer, Integer, Integer> x;
    private final BiFunction<Integer, Integer, Integer> y;
    private final BiFunction<Integer, Integer, String> name;
    
    ShulkerPosition(BiFunction<Integer, Integer, Integer> x, BiFunction<Integer, Integer, Integer> y, BiFunction<Integer, Integer, String> name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
    
    public int getX(int left, int width) {
        return x.apply(left, width);
    }
    
    public int getY(int top, int height) {
        return y.apply(top, height);
    }
    
    public Identifier getTexture(int width, int height) {
        return TripleInventory.identifier("textures/gui/container/shulker_preview_" + name.apply(width, height) + ".png");
    }
}
