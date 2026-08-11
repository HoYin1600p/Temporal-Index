package com.hoyin1600p.temporalindex.client.config;

import com.hoyin1600p.temporalindex.client.config.TemporalIndexRenderTransformConfig.Parameter;
import com.hoyin1600p.temporalindex.client.config.TemporalIndexRenderTransformConfig.RenderContext;
import com.hoyin1600p.temporalindex.client.config.TemporalIndexRenderTransformConfig.Transform;
import com.hoyin1600p.temporalindex.client.TemporalIndexItemRenderer;
import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;

/** Temporary in-game authoring UI. Runtime config loading does not depend on this class. */
public final class TemporalIndexRenderCalibrationScreen extends Screen {
    private static final int DROPDOWN_ROW_HEIGHT = 11;
    private static final int DROPDOWN_WIDTH = 190;
    private static final int TARGET_DROPDOWN_WIDTH = 92;
    private static final int CONTEXT_DROPDOWN_LEFT = 114;
    private static final int CONTEXT_DROPDOWN_WIDTH = 94;
    private static final int CONTROLS_TOP = 70;
    private static final int CONTROL_ROW_HEIGHT = 21;

    private final TemporalIndexRenderTransformConfig config =
            TemporalIndexRenderTransformConfig.getInstance();
    private final List<ItemChoice> items = createItemChoices();
    private final EnumMap<Parameter, ValueSlider> sliders = new EnumMap<>(Parameter.class);
    private final EnumMap<Parameter, EditBox> entries = new EnumMap<>(Parameter.class);

    private Button itemButton;
    private Button targetButton;
    private Button contextButton;
    private int selectedItemIndex;
    private CalibrationTarget selectedTarget = CalibrationTarget.COVER_ICON;
    private RenderContext selectedContext = RenderContext.FIRST_PERSON;
    private boolean itemDropdownOpen;
    private boolean targetDropdownOpen;
    private boolean contextDropdownOpen;
    private boolean synchronizing;
    private String status = "Changes apply live. Save writes the JSON file.";

    public TemporalIndexRenderCalibrationScreen() {
        super(new TextComponent("Temporal Index Render Calibration"));
    }

    @Override
    protected void init() {
        int left = 18;
        itemButton = addRenderableWidget(new Button(
                left,
                18,
                DROPDOWN_WIDTH,
                20,
                itemButtonText(),
                ignored -> {
                    itemDropdownOpen = !itemDropdownOpen;
                    targetDropdownOpen = false;
                    contextDropdownOpen = false;
                }
        ));
        targetButton = addRenderableWidget(new Button(
                left,
                42,
                TARGET_DROPDOWN_WIDTH,
                20,
                targetButtonText(),
                ignored -> {
                    targetDropdownOpen = !targetDropdownOpen;
                    itemDropdownOpen = false;
                    contextDropdownOpen = false;
                }
        ));
        contextButton = addRenderableWidget(new Button(
                CONTEXT_DROPDOWN_LEFT,
                42,
                CONTEXT_DROPDOWN_WIDTH,
                20,
                contextButtonText(),
                ignored -> {
                    contextDropdownOpen = !contextDropdownOpen;
                    itemDropdownOpen = false;
                    targetDropdownOpen = false;
                }
        ));

        int sliderX = 100;
        int entryX = 226;
        int row = 0;
        for (Parameter parameter : Parameter.values()) {
            int y = CONTROLS_TOP + row * CONTROL_ROW_HEIGHT;
            ValueSlider slider = addRenderableWidget(new ValueSlider(
                    sliderX,
                    y,
                    120,
                    18,
                    parameter,
                    value -> applyValue(parameter, value, true)
            ));
            EditBox entry = addRenderableWidget(new EditBox(
                    font,
                    entryX,
                    y,
                    64,
                    18,
                    new TextComponent(parameter.displayName())
            ));
            entry.setMaxLength(16);
            entry.setResponder(text -> applyEntry(parameter, text));
            sliders.put(parameter, slider);
            entries.put(parameter, entry);
            row++;
        }

        int buttonY = height - 22;
        addRenderableWidget(new Button(18, buttonY, 55, 20, new TextComponent("Save"), ignored -> save()));
        addRenderableWidget(new Button(77, buttonY, 65, 20, new TextComponent("Reload"), ignored -> reload()));
        addRenderableWidget(new Button(146, buttonY, 88, 20, new TextComponent("Reset View"), ignored -> reset()));
        addRenderableWidget(new Button(238, buttonY, 55, 20, new TextComponent("Done"), ignored -> done()));
        synchronizeControls();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Keep the live world and target book clearly visible while tuning.
        // Only expanded dropdowns use an opaque backing for legibility.
        fill(poseStack, 8, 2, Math.min(width - 8, 300), height - 2, 0x30182028);
        drawCenteredString(poseStack, font, title, 154, 5, 0xFFD7B85A);

        int row = 0;
        for (Parameter parameter : Parameter.values()) {
            drawString(poseStack, font, new TextComponent(parameter.displayName()),
                    18, CONTROLS_TOP + row * CONTROL_ROW_HEIGHT + 5, 0xFFE4EBEF);
            row++;
        }
        drawString(poseStack, font, new TextComponent(status), 18, height - 36, 0xFF9FC9D8);

        renderSelectedPreview(poseStack);

        super.render(poseStack, mouseX, mouseY, partialTick);
        if (itemDropdownOpen) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 500.0D);
            renderItemDropdown(poseStack, mouseX, mouseY);
            poseStack.popPose();
        } else if (targetDropdownOpen) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 500.0D);
            renderTargetDropdown(poseStack, mouseX, mouseY);
            poseStack.popPose();
        } else if (contextDropdownOpen) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 500.0D);
            renderContextDropdown(poseStack, mouseX, mouseY);
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (itemDropdownOpen) {
            int selected = dropdownIndex(mouseX, mouseY, 18, 38, DROPDOWN_WIDTH, items.size());
            if (selected >= 0) {
                selectedItemIndex = selected;
                itemDropdownOpen = false;
                itemButton.setMessage(itemButtonText());
                synchronizeControls();
                return true;
            }
            itemDropdownOpen = false;
        }
        if (targetDropdownOpen) {
            CalibrationTarget[] targets = CalibrationTarget.values();
            int selected = dropdownIndex(
                    mouseX,
                    mouseY,
                    18,
                    62,
                    TARGET_DROPDOWN_WIDTH,
                    targets.length
            );
            if (selected >= 0) {
                selectedTarget = targets[selected];
                targetDropdownOpen = false;
                targetButton.setMessage(targetButtonText());
                itemButton.active = selectedTarget == CalibrationTarget.COVER_ICON;
                synchronizeControls();
                return true;
            }
            targetDropdownOpen = false;
        }
        if (contextDropdownOpen) {
            RenderContext[] contexts = RenderContext.values();
            int selected = dropdownIndex(
                    mouseX,
                    mouseY,
                    CONTEXT_DROPDOWN_LEFT,
                    62,
                    CONTEXT_DROPDOWN_WIDTH,
                    contexts.length
            );
            if (selected >= 0) {
                selectedContext = contexts[selected];
                contextDropdownOpen = false;
                contextButton.setMessage(contextButtonText());
                synchronizeControls();
                return true;
            }
            contextDropdownOpen = false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void applyEntry(Parameter parameter, String text) {
        if (synchronizing || text.isBlank() || text.equals("-") || text.equals(".")) {
            return;
        }
        try {
            double value = Double.parseDouble(text);
            if (!Double.isFinite(value)) {
                return;
            }
            value = clamp(value, parameter.minimum(), parameter.maximum());
            applyValue(parameter, value, false);
        } catch (NumberFormatException ignored) {
            status = "Invalid number for " + parameter.displayName();
        }
    }

    private void applyValue(Parameter parameter, double value, boolean fromSlider) {
        if (synchronizing) {
            return;
        }
        Transform updated = selectedTransform().with(parameter, value);
        setSelectedTransform(updated);
        synchronizing = true;
        if (fromSlider) {
            entries.get(parameter).setValue(format(value));
        } else {
            sliders.get(parameter).setActualValue(value);
        }
        synchronizing = false;
        status = selectionLabel() + " changed (not saved)";
    }

    private void synchronizeControls() {
        if (sliders.isEmpty()) {
            return;
        }
        Transform transform = selectedTransform();
        synchronizing = true;
        for (Parameter parameter : Parameter.values()) {
            double value = transform.get(parameter);
            sliders.get(parameter).setActualValue(value);
            entries.get(parameter).setValue(format(value));
        }
        synchronizing = false;
        status = selectionLabel();
    }

    private void save() {
        config.save();
        status = "Saved " + selectionLabel();
    }

    private void reload() {
        config.reload();
        synchronizeControls();
        status = "Reloaded JSON from disk";
    }

    private void reset() {
        if (selectedTarget == CalibrationTarget.BOOK_MODEL) {
            config.resetBook(selectedContext);
        } else {
            config.reset(selectedItem().key(), selectedContext);
        }
        synchronizeControls();
        status = "Reset current context to packaged defaults (not saved)";
    }

    private Transform selectedTransform() {
        return selectedTarget == CalibrationTarget.BOOK_MODEL
                ? config.getBook(selectedContext)
                : config.get(selectedItem().key(), selectedContext);
    }

    private void setSelectedTransform(Transform transform) {
        if (selectedTarget == CalibrationTarget.BOOK_MODEL) {
            config.setBook(selectedContext, transform);
        } else {
            config.set(selectedItem().key(), selectedContext, transform);
        }
    }

    private String selectionLabel() {
        String subject = selectedTarget == CalibrationTarget.BOOK_MODEL
                ? selectedTarget.displayName()
                : selectedItem().label();
        return subject + " / " + selectedContext.displayName();
    }

    private void done() {
        save();
        if (minecraft != null) {
            minecraft.setScreen(null);
        }
    }

    private void renderItemDropdown(PoseStack poseStack, int mouseX, int mouseY) {
        int top = 38;
        int bottom = top + items.size() * DROPDOWN_ROW_HEIGHT;
        fill(poseStack, 18, top, 18 + DROPDOWN_WIDTH, bottom, 0xFF111820);
        for (int index = 0; index < items.size(); index++) {
            int y = top + index * DROPDOWN_ROW_HEIGHT;
            boolean hovered = mouseX >= 18 && mouseX < 18 + DROPDOWN_WIDTH
                    && mouseY >= y && mouseY < y + DROPDOWN_ROW_HEIGHT;
            if (hovered || index == selectedItemIndex) {
                fill(poseStack, 19, y, 18 + DROPDOWN_WIDTH - 1, y + DROPDOWN_ROW_HEIGHT, 0xFF35505D);
            }
            drawString(poseStack, font, new TextComponent(items.get(index).label()), 22, y + 1, 0xFFF0F4F6);
        }
    }

    private void renderContextDropdown(PoseStack poseStack, int mouseX, int mouseY) {
        RenderContext[] contexts = RenderContext.values();
        int top = 62;
        int bottom = top + contexts.length * DROPDOWN_ROW_HEIGHT;
        fill(poseStack, CONTEXT_DROPDOWN_LEFT, top,
                CONTEXT_DROPDOWN_LEFT + CONTEXT_DROPDOWN_WIDTH, bottom, 0xFF111820);
        for (int index = 0; index < contexts.length; index++) {
            int y = top + index * DROPDOWN_ROW_HEIGHT;
            boolean hovered = mouseX >= CONTEXT_DROPDOWN_LEFT
                    && mouseX < CONTEXT_DROPDOWN_LEFT + CONTEXT_DROPDOWN_WIDTH
                    && mouseY >= y && mouseY < y + DROPDOWN_ROW_HEIGHT;
            if (hovered || contexts[index] == selectedContext) {
                fill(poseStack, CONTEXT_DROPDOWN_LEFT + 1, y,
                        CONTEXT_DROPDOWN_LEFT + CONTEXT_DROPDOWN_WIDTH - 1,
                        y + DROPDOWN_ROW_HEIGHT, 0xFF35505D);
            }
            drawString(poseStack, font, new TextComponent(contexts[index].displayName()),
                    CONTEXT_DROPDOWN_LEFT + 4, y + 1, 0xFFF0F4F6);
        }
    }

    private void renderTargetDropdown(PoseStack poseStack, int mouseX, int mouseY) {
        CalibrationTarget[] targets = CalibrationTarget.values();
        int top = 62;
        int bottom = top + targets.length * DROPDOWN_ROW_HEIGHT;
        fill(poseStack, 18, top, 18 + TARGET_DROPDOWN_WIDTH, bottom, 0xFF111820);
        for (int index = 0; index < targets.length; index++) {
            int y = top + index * DROPDOWN_ROW_HEIGHT;
            boolean hovered = mouseX >= 18 && mouseX < 18 + TARGET_DROPDOWN_WIDTH
                    && mouseY >= y && mouseY < y + DROPDOWN_ROW_HEIGHT;
            if (hovered || targets[index] == selectedTarget) {
                fill(poseStack, 19, y, 18 + TARGET_DROPDOWN_WIDTH - 1,
                        y + DROPDOWN_ROW_HEIGHT, 0xFF35505D);
            }
            drawString(poseStack, font, new TextComponent(targets[index].displayName()),
                    22, y + 1, 0xFFF0F4F6);
        }
    }

    private void renderSelectedPreview(PoseStack poseStack) {
        if (minecraft == null || width < 360) {
            return;
        }

        int previewCenterX = width - 62;
        drawCenteredString(poseStack, font, new TextComponent("Live Preview"), previewCenterX, 42, 0xFFE4EBEF);
        drawCenteredString(poseStack, font, new TextComponent(selectedTarget.displayName()),
                previewCenterX, 53, 0xFF9FC9D8);
        drawCenteredString(poseStack, font, new TextComponent(selectedContext.displayName()),
                previewCenterX, 64, 0xFF9FC9D8);

        ItemStack previewBook = new ItemStack(TemporalIndexRegistry.TEMPORAL_INDEX.get());
        TemporalIndexStorage.setCount(
                previewBook,
                selectedItemIndex,
                1,
                TemporalRelics.DEFAULT_DURATION
        );

        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(width - 112.0D, 82.0D, 0.0D);
        modelView.scale(6.0F, 6.0F, 6.0F);
        RenderSystem.applyModelViewMatrix();
        TemporalIndexItemRenderer.renderPreview(selectedContext, () ->
                minecraft.getItemRenderer().renderAndDecorateItem(previewBook, 0, 0));
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static int dropdownIndex(
            double mouseX,
            double mouseY,
            int left,
            int top,
            int width,
            int count
    ) {
        if (mouseX < left || mouseX >= left + width || mouseY < top) {
            return -1;
        }
        int index = (int) ((mouseY - top) / DROPDOWN_ROW_HEIGHT);
        return index >= 0 && index < count ? index : -1;
    }

    private ItemChoice selectedItem() {
        return items.get(Math.max(0, Math.min(selectedItemIndex, items.size() - 1)));
    }

    private Component itemButtonText() {
        return new TextComponent("Item: " + selectedItem().label() + " ▾");
    }

    private Component targetButtonText() {
        return new TextComponent(selectedTarget.displayName() + " ▾");
    }

    private Component contextButtonText() {
        return new TextComponent(selectedContext.displayName() + " ▾");
    }

    private static List<ItemChoice> createItemChoices() {
        List<ItemChoice> choices = new ArrayList<>(TemporalIndexStorage.SLOT_COUNT);
        choices.add(new ItemChoice("temporal_shard", "Temporal Shard"));
        for (int slot = 1; slot < TemporalIndexStorage.SLOT_COUNT; slot++) {
            String key = TemporalIndexRenderTransformConfig.keyForSlot(slot);
            String label = TemporalRelics.getIndexDisplayName(TemporalRelics.createRelicStack(
                    TemporalRelics.DEFINITIONS.get(slot - 1),
                    TemporalRelics.DEFAULT_DURATION,
                    1
            )).getString();
            choices.add(new ItemChoice(key, label));
        }
        return List.copyOf(choices);
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.4f", value);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private record ItemChoice(String key, String label) {
    }

    private enum CalibrationTarget {
        COVER_ICON("Cover Icon"),
        BOOK_MODEL("Book Model");

        private final String displayName;

        CalibrationTarget(String displayName) {
            this.displayName = displayName;
        }

        private String displayName() {
            return displayName;
        }
    }

    private static final class ValueSlider extends AbstractSliderButton {
        private final Parameter parameter;
        private final DoubleConsumer responder;

        private ValueSlider(
                int x,
                int y,
                int width,
                int height,
                Parameter parameter,
                DoubleConsumer responder
        ) {
            super(x, y, width, height, new TextComponent(""), 0.5D);
            this.parameter = parameter;
            this.responder = responder;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(new TextComponent(format(actualValue())));
        }

        @Override
        protected void applyValue() {
            responder.accept(actualValue());
        }

        private double actualValue() {
            return parameter.minimum() + value * (parameter.maximum() - parameter.minimum());
        }

        private void setActualValue(double actual) {
            value = (clamp(actual, parameter.minimum(), parameter.maximum()) - parameter.minimum())
                    / (parameter.maximum() - parameter.minimum());
            updateMessage();
        }
    }
}
