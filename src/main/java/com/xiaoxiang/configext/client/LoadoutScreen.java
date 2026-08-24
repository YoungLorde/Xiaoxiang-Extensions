package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.PreWorldState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * Screen for managing 5 Golden Finger loadout slots.
 * Each slot can be saved (current selection), loaded (restore selection), or deleted.
 * Accessible from the PreWorldPerkScreen via a "Loadouts" button.
 */
public class LoadoutScreen extends Screen {

    private final Screen parent;
    private final Runnable onLoadCallback;

    public LoadoutScreen(Screen parent, Runnable onLoadCallback) {
        super(Component.literal("Golden Finger Loadouts"));
        this.parent = parent;
        this.onLoadCallback = onLoadCallback;
    }

    @Override
    protected void init() {
        // Back button
        addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(parent)
        ).bounds(this.width / 2 - 220, this.height - 28, 80, 20).build());

        // Slot buttons: Save / Load / Delete for each of 5 slots
        int numSlots = LoadoutManager.getNumSlots();
        int slotStartY = 50;
        int slotH = 36;
        int btnW = 60;
        int btnH = 16;
        int gap = 6;

        for (int i = 0; i < numSlots; i++) {
            int y = slotStartY + i * (slotH + gap);
            int centerX = this.width / 2;
            int saveX = centerX - 130;
            int loadX = centerX - 60;
            int delX = centerX + 10;
            final int slotIndex = i;

            // Save button
            addRenderableWidget(Button.builder(
                    Component.literal("Save"),
                    btn -> saveToSlot(slotIndex)
            ).bounds(saveX, y + 16, btnW, btnH).build());

            // Load button
            Button loadBtn = Button.builder(
                    Component.literal("Load"),
                    btn -> loadFromSlot(slotIndex)
            ).bounds(loadX, y + 16, btnW, btnH).build();
            loadBtn.active = !LoadoutManager.isSlotEmpty(slotIndex);
            addRenderableWidget(loadBtn);

            // Delete button
            Button delBtn = Button.builder(
                    Component.literal("Delete"),
                    btn -> {
                        LoadoutManager.deleteSlot(slotIndex);
                        reloadButtons();
                    }
            ).bounds(delX, y + 16, btnW, btnH).build();
            delBtn.active = !LoadoutManager.isSlotEmpty(slotIndex);
            addRenderableWidget(delBtn);
        }
    }

    private void reloadButtons() {
        this.clearWidgets();
        this.init();
    }

    /** Save current perk selection + identity + items + gf count to a slot. */
    private void saveToSlot(int slotIndex) {
        List<Integer> perkIds = new ArrayList<>(PerkSelectionScreen.selectedPerkIds);
        int gfCount = PerkSelectionScreen.maxPerkCount;
        String identityId = PreWorldState.pendingIdentityId != null ? PreWorldState.pendingIdentityId : "";
        String startingItems = PreWorldState.pendingStartingItems != null ? PreWorldState.pendingStartingItems : "";

        // Generate a default name from the perks
        StringBuilder name = new StringBuilder();
        int count = 0;
        for (int id : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(id);
            if (perk != null) {
                if (count > 0) name.append(", ");
                name.append(perk.name);
                count++;
                if (count >= 3) { name.append("..."); break; }
            }
        }
        if (name.length() == 0) name.append("Empty Loadout");

        LoadoutManager.saveSlot(slotIndex, name.toString(), perkIds, identityId, startingItems, gfCount);
        reloadButtons();
    }

    /** Load a slot: restore perks, identity, items, and golden finger count. */
    private void loadFromSlot(int slotIndex) {
        LoadoutManager.Loadout loadout = LoadoutManager.getSlot(slotIndex);
        if (loadout.empty) return;

        // Restore perk selection
        PerkSelectionScreen.selectedPerkIds.clear();
        for (int id : loadout.perkIds) {
            // Only add perks that exist
            if (GoldenFingerPerks.getById(id) != null) {
                PerkSelectionScreen.selectedPerkIds.add(id);
            }
        }

        // Restore golden finger count
        PerkSelectionScreen.maxPerkCount = loadout.goldenFingerCount;

        // Restore identity and starting items
        PreWorldState.pendingIdentityId = loadout.identityId;
        PreWorldState.pendingStartingItems = loadout.startingItems;

        // Notify callback so parent screen can update
        if (onLoadCallback != null) {
            onLoadCallback.run();
        }

        // Return to parent screen
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Dark background
        g.fill(0, 0, this.width, this.height, 0xD0101018);

        // Title
        g.drawCenteredString(this.font, "\u00A7l\u00A76Golden Finger Loadouts",
                this.width / 2, 8, 0xFFFFFF);
        g.drawCenteredString(this.font, "\u00A77Save your current perk selection, identity, and starting items",
                this.width / 2, 22, 0xAAAAAA);
        g.drawCenteredString(this.font, "\u00A77Current GF Count: " + PerkSelectionScreen.maxPerkCount +
                "  |  Selected: " + PerkSelectionScreen.selectedPerkIds.size() + "/" + PerkSelectionScreen.maxPerkCount,
                this.width / 2, 34, 0xAAAAFF);

        // Draw slot info
        int numSlots = LoadoutManager.getNumSlots();
        int slotStartY = 50;
        int slotH = 36;
        int gap = 6;
        int infoX = this.width / 2 - 200;
        int infoW = 400;

        for (int i = 0; i < numSlots; i++) {
            int y = slotStartY + i * (slotH + gap);
            LoadoutManager.Loadout loadout = LoadoutManager.getSlot(i);

            // Slot background
            int bg = loadout.empty ? 0x40101020 : 0x60102010;
            g.fill(infoX - 4, y - 2, infoX + infoW + 4, y + slotH, bg);
            g.renderOutline(infoX - 4, y - 2, infoW + 8, slotH + 2,
                    loadout.empty ? 0xFF303040 : 0xFF406030);

            // Slot label
            String label = "\u00A7eSlot " + (i + 1) + ": ";
            if (loadout.empty) {
                label += "\u00A78[Empty]";
            } else {
                label += "\u00A7f" + (loadout.name.isEmpty() ? "Unnamed" : loadout.name);
            }
            g.drawString(this.font, label, infoX, y, 0xFFFFFF);

            if (!loadout.empty) {
                // Show perk count, identity, gf count
                String details = "\u00A77" + loadout.perkIds.size() + " perks" +
                        "  |  GF: " + loadout.goldenFingerCount +
                        (loadout.identityId.isEmpty() ? "" : "  |  ID: " + loadout.identityId);
                g.drawString(this.font, details, infoX, y + 10, 0xAAAAAA);
            }
        }

        // Render buttons
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
