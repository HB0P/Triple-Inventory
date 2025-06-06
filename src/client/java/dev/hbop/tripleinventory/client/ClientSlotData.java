package dev.hbop.tripleinventory.client;

import dev.hbop.tripleinventory.client.config.ClientConfig;

public class ClientSlotData {
    
    public static final ClientSlotData INSTANCE = new ClientSlotData();
    private int previousSelectedSlot = -1;
    private int selectedSlotResetCooldown = -1;
    
    private ClientSlotData() {
    }
    
    public boolean hasPreviouslySelectedSlot() {
        return previousSelectedSlot >= 0;
    }
    
    public int getPreviouslySelectedSlot() {
        return previousSelectedSlot;
    }
    
    public boolean isSelectedSlotResetCooldownElapsed() {
        return selectedSlotResetCooldown == 0;
    }
    
    public void reset() {
        previousSelectedSlot = -1;
        selectedSlotResetCooldown = -1;
    }
    
    public void set(int selectedSlot, boolean returnAfterCooldown) {
        if (!hasPreviouslySelectedSlot()) {
            previousSelectedSlot = selectedSlot;
            if (returnAfterCooldown && ClientConfig.HANDLER.instance().autoReturnAfterCooldown) {
                selectedSlotResetCooldown = ClientConfig.HANDLER.instance().autoReturnCooldown;
            }
        }
    }
    
    public void decrementSelectedSlotResetCooldown() {
        if (selectedSlotResetCooldown >= 0) {
            selectedSlotResetCooldown--;
        }
    }
    
    public void boostSelectedSlotResetCooldown() {
        if (selectedSlotResetCooldown >= 0) {
            selectedSlotResetCooldown = ClientConfig.HANDLER.instance().autoReturnCooldown;
        }
    }
}