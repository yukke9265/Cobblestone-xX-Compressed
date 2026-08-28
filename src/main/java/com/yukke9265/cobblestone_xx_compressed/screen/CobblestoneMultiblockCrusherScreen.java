package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMultiblockCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockCellType;
import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockPattern;
import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockStructureStatus;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * マルチブロッククラッシャー GUI です。
 * 左側の構造パネルは一層ずつ切り替え表示します。
 */
public class CobblestoneMultiblockCrusherScreen extends BaseScreen<CobblestoneMultiblockCrusherMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher.png");
    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher_progress_bar.png");

    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;

    private static final int STRUCTURE_PANEL_WIDTH = 96;
    private static final int STRUCTURE_PANEL_GAP = 6;
    private static final int STRUCTURE_CELL_SIZE = 18;
    private static final int STRUCTURE_CELL_GAP = 3;
    private static final int STRUCTURE_LAYER_BUTTON_WIDTH = 18;
    private static final int STRUCTURE_LAYER_BUTTON_HEIGHT = 16;

    private static final int COLOR_PANEL_BG = 0xC0101010;
    private static final int COLOR_PANEL_BORDER = 0xFF8B8B8B;
    private static final int COLOR_NG = 0xFFDC3D3D;
    private static final int COLOR_CORE = 0xFF4C8DFF;
    private static final int COLOR_AIR = 0xFF2A2A2A;
    private static final int COLOR_CASING = 0xFF8A8A8A;
    private static final int COLOR_PORT = 0xFFD4A017;
    private static final int COLOR_UPGRADE = 0xFF9B59B6;

    private final MultiblockPattern structurePattern = MultiblockPattern.createMultiblockCrusherPattern();
    private final MultiblockPattern.GridBounds structureBounds = this.structurePattern.getGridBounds();
    private final MultiblockCellType[] expectedTypes;
    private int selectedLayer;
    private Button previousLayerButton;
    private Button nextLayerButton;

    public CobblestoneMultiblockCrusherScreen(CobblestoneMultiblockCrusherMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = MachineGuiLayouts.STANDARD_IMAGE_WIDTH;
        this.imageHeight = MachineGuiLayouts.STANDARD_IMAGE_HEIGHT;
        this.titleLabelX = MachineGuiLayouts.STANDARD_TITLE_LABEL_X;
        this.titleLabelY = MachineGuiLayouts.STANDARD_TITLE_LABEL_Y;
        this.inventoryLabelX = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_X;
        this.inventoryLabelY = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_Y;
        this.expectedTypes = new MultiblockCellType[this.structureBounds.cellCount()];
        this.selectedLayer = 0;
        this.buildExpectedTypes();
    }

    private void buildExpectedTypes() {
        for (MultiblockPattern.Cell cell : this.structurePattern.getCells()) {
            int index = this.structureBounds.toIndex(cell.relativeX(), cell.relativeY(), cell.relativeZ());
            if (index >= 0 && index < this.expectedTypes.length) {
                this.expectedTypes[index] = cell.type();
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("gui.cobblestonexxcompressed.start_stop"),
                button -> this.sendMenuButtonClick(0)
            ).bounds(
                this.leftPos + this.imageWidth + 4,
                this.topPos + this.imageHeight - 20,
                62,
                20
            ).build()
        );

        int panelX = this.leftPos - STRUCTURE_PANEL_GAP - STRUCTURE_PANEL_WIDTH;
        int panelY = this.topPos;
        int buttonY = panelY + 42;
        this.previousLayerButton = this.addRenderableWidget(
            Button.builder(
                Component.literal("<"),
                button -> this.changeSelectedLayer(-1)
            ).bounds(panelX + 6, buttonY, STRUCTURE_LAYER_BUTTON_WIDTH, STRUCTURE_LAYER_BUTTON_HEIGHT).build()
        );
        this.nextLayerButton = this.addRenderableWidget(
            Button.builder(
                Component.literal(">"),
                button -> this.changeSelectedLayer(1)
            ).bounds(
                panelX + STRUCTURE_PANEL_WIDTH - 6 - STRUCTURE_LAYER_BUTTON_WIDTH,
                buttonY,
                STRUCTURE_LAYER_BUTTON_WIDTH,
                STRUCTURE_LAYER_BUTTON_HEIGHT
            ).build()
        );
        this.refreshLayerButtons();
    }

    private void changeSelectedLayer(int delta) {
        int nextLayer = this.selectedLayer + delta;
        if (nextLayer < 0 || nextLayer >= this.structureBounds.sizeY()) {
            return;
        }

        this.selectedLayer = nextLayer;
        this.refreshLayerButtons();
    }

    private void refreshLayerButtons() {
        if (this.previousLayerButton != null) {
            this.previousLayerButton.active = this.selectedLayer > 0;
        }
        if (this.nextLayerButton != null) {
            this.nextLayerButton.active = this.selectedLayer < this.structureBounds.sizeY() - 1;
        }
    }

    @Override
    protected void renderMachineBackground(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X, y + MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X, y + MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y);
        this.renderProgressFramePart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X, y + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int width = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH * progress / maxProgress;
            guiGraphics.blit(
                PROGRESS_BAR_TEXTURE,
                x + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X,
                y + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y,
                0,
                0,
                width,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT
            );
        }

        this.renderPowerBar(
            guiGraphics,
            x + MachineGuiLayouts.PoweredMachine.POWER_BAR_X,
            y + MachineGuiLayouts.PoweredMachine.POWER_BAR_Y,
            MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH,
            MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT
        );

        this.renderStructurePanel(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        Component formedText = this.menu.isFormed()
            ? Component.translatable("gui.cobblestonexxcompressed.multiblock.formed")
            : Component.translatable("gui.cobblestonexxcompressed.multiblock.incomplete");
        guiGraphics.drawString(this.font, formedText, 8, 18, this.menu.isFormed() ? 0x55FF55 : 0xFF5555, false);
        guiGraphics.drawString(
            this.font,
            "In: " + NumberDisplayHelper.format(this.menu.getInputCount()),
            8,
            28,
            0x404040,
            false
        );
        guiGraphics.drawString(
            this.font,
            "Out: " + NumberDisplayHelper.format(this.menu.getOutputCount()),
            8,
            38,
            0x404040,
            false
        );
        guiGraphics.drawString(
            this.font,
            "CP: " + NumberDisplayHelper.format(this.menu.getStoredCobblestonePower()),
            8,
            48,
            0x404040,
            false
        );
    }

    private void renderStructurePanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int panelX = this.leftPos - STRUCTURE_PANEL_GAP - STRUCTURE_PANEL_WIDTH;
        int panelY = this.topPos;

        guiGraphics.fill(panelX, panelY, panelX + STRUCTURE_PANEL_WIDTH, panelY + this.imageHeight, COLOR_PANEL_BG);
        guiGraphics.fill(panelX, panelY, panelX + STRUCTURE_PANEL_WIDTH, panelY + 1, COLOR_PANEL_BORDER);
        guiGraphics.fill(panelX, panelY + this.imageHeight - 1, panelX + STRUCTURE_PANEL_WIDTH, panelY + this.imageHeight, COLOR_PANEL_BORDER);
        guiGraphics.fill(panelX, panelY, panelX + 1, panelY + this.imageHeight, COLOR_PANEL_BORDER);
        guiGraphics.fill(panelX + STRUCTURE_PANEL_WIDTH - 1, panelY, panelX + STRUCTURE_PANEL_WIDTH, panelY + this.imageHeight, COLOR_PANEL_BORDER);

        guiGraphics.drawString(
            this.font,
            Component.translatable("gui.cobblestonexxcompressed.multiblock.structure"),
            panelX + 6,
            panelY + 6,
            0xFFFFFF,
            false
        );
        String progressText = this.menu.getStructureMatchedCount() + "/" + this.menu.getStructureRequiredCount();
        guiGraphics.drawString(this.font, progressText, panelX + 6, panelY + 18, this.menu.isFormed() ? 0x55FF55 : 0xFFAA55, false);
        guiGraphics.drawString(
            this.font,
            Component.translatable("gui.cobblestonexxcompressed.multiblock.guide_orientation"),
            panelX + 6,
            panelY + 30,
            0xAAAAAA,
            false
        );

        String layerText = "Y+" + this.selectedLayer;
        int layerTextWidth = this.font.width(layerText);
        guiGraphics.drawString(
            this.font,
            layerText,
            panelX + (STRUCTURE_PANEL_WIDTH - layerTextWidth) / 2,
            panelY + 46,
            0xFFFFFF,
            false
        );

        int gridWidth = STRUCTURE_CELL_SIZE * this.structureBounds.sizeX()
            + STRUCTURE_CELL_GAP * Math.max(0, this.structureBounds.sizeX() - 1);
        int gridStartX = panelX + (STRUCTURE_PANEL_WIDTH - gridWidth) / 2;
        int gridStartY = panelY + 66;

        Optional<HoveredCell> hovered = Optional.empty();
        int sizeX = this.structureBounds.sizeX();
        int sizeZ = this.structureBounds.sizeZ();
        for (int row = 0; row < sizeZ; row++) {
            for (int col = 0; col < sizeX; col++) {
                // 文字列レイヤーと同じ: 下段=正面(相対z小)、左=相対x大。
                int relativeX = this.structureBounds.minX() + (sizeX - 1 - col);
                int relativeY = this.structureBounds.minY() + this.selectedLayer;
                int relativeZ = this.structureBounds.minZ() + (sizeZ - 1 - row);
                int index = this.structureBounds.toIndex(relativeX, relativeY, relativeZ);
                if (index < 0 || index >= this.expectedTypes.length) {
                    continue;
                }

                int cellX = gridStartX + col * (STRUCTURE_CELL_SIZE + STRUCTURE_CELL_GAP);
                int cellY = gridStartY + row * (STRUCTURE_CELL_SIZE + STRUCTURE_CELL_GAP);
                boolean matched = this.menu.isStructureCellMatched(index);
                MultiblockCellType expected = this.expectedTypes[index];
                int color = this.resolveCellColor(expected, matched);

                guiGraphics.fill(cellX, cellY, cellX + STRUCTURE_CELL_SIZE, cellY + STRUCTURE_CELL_SIZE, color);
                guiGraphics.fill(cellX, cellY, cellX + STRUCTURE_CELL_SIZE, cellY + 1, 0xFF000000);
                guiGraphics.fill(cellX, cellY + STRUCTURE_CELL_SIZE - 1, cellX + STRUCTURE_CELL_SIZE, cellY + STRUCTURE_CELL_SIZE, 0xFF000000);
                guiGraphics.fill(cellX, cellY, cellX + 1, cellY + STRUCTURE_CELL_SIZE, 0xFF000000);
                guiGraphics.fill(cellX + STRUCTURE_CELL_SIZE - 1, cellY, cellX + STRUCTURE_CELL_SIZE, cellY + STRUCTURE_CELL_SIZE, 0xFF000000);

                if (mouseX >= cellX && mouseX < cellX + STRUCTURE_CELL_SIZE
                    && mouseY >= cellY && mouseY < cellY + STRUCTURE_CELL_SIZE) {
                    hovered = Optional.of(new HoveredCell(expected, matched, relativeX, relativeY, relativeZ));
                }
            }
        }

        hovered.ifPresent(cell -> {
            Component line1 = MultiblockStructureStatus.typeLabel(cell.type());
            Component line2 = cell.matched()
                ? Component.translatable(
                    "gui.cobblestonexxcompressed.multiblock.cell_matched",
                    cell.relativeX(),
                    cell.relativeY(),
                    cell.relativeZ()
                )
                : Component.translatable(
                    "gui.cobblestonexxcompressed.multiblock.cell_mismatch",
                    cell.relativeX(),
                    cell.relativeY(),
                    cell.relativeZ()
                );
            guiGraphics.renderTooltip(this.font, List.of(line1, line2), Optional.empty(), mouseX, mouseY);
        });
    }

    private int resolveCellColor(MultiblockCellType type, boolean matched) {
        if (!matched) {
            return COLOR_NG;
        }

        if (type == null) {
            return COLOR_AIR;
        }

        return switch (type) {
            case CORE -> COLOR_CORE;
            case AIR -> COLOR_AIR;
            case CASING -> COLOR_CASING;
            case INOUT, ITEM_IN, ITEM_OUT, FLUID_IN, FLUID_OUT, COBBLE_IN -> COLOR_PORT;
            case UPGRADE -> COLOR_UPGRADE;
        };
    }

    private void renderPowerBar(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, POWER_BAR_BORDER_COLOR);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, POWER_BAR_BACKGROUND_COLOR);

        long stored = this.menu.getStoredCobblestonePower();
        long max = this.menu.getMaxCobblestonePower();
        if (max <= 0L || stored <= 0L) {
            return;
        }

        int fillHeight = (int) Math.max(1L, (height - 2L) * stored / max);
        guiGraphics.fill(x + 1, y + height - 1 - fillHeight, x + width - 1, y + height - 1, POWER_BAR_FILL_COLOR);
    }

    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X,
            MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y,
            16,
            16,
            ModJeiIds.COBBLESTONE_CRUSHER
        );
    }

    private record HoveredCell(
        MultiblockCellType type,
        boolean matched,
        int relativeX,
        int relativeY,
        int relativeZ
    ) {
    }
}
