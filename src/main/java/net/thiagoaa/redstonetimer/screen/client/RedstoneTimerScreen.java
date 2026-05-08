package net.thiagoaa.redstonetimer.screen.client;

import net.thiagoaa.redstonetimer.block.entity.RedstoneTimerBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.thiagoaa.redstonetimer.RedstoneTimer;
import net.thiagoaa.redstonetimer.network.RedstoneTimerUpdatePayload;
import net.thiagoaa.redstonetimer.screen.RedstoneTimerScreenHandler;

public class RedstoneTimerScreen extends HandledScreen<RedstoneTimerScreenHandler> {

    private static final Identifier GUI_TEXTURE =
            Identifier.of(RedstoneTimer.MOD_ID, "textures/gui/redstone_timer_gui.png");

    private TextFieldWidget secondsField;
    private TextFieldWidget onSecondsField;

    private ButtonWidget loopButton;
    private ButtonWidget enabledButton;
    private ButtonWidget autonomousStartButton;

    private boolean loop;
    private boolean enabled;
    private boolean autonomousStart;

    private int applyFeedbackTicks = 0;

    public RedstoneTimerScreen(RedstoneTimerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.backgroundWidth = 176;
        this.backgroundHeight = 166;

        this.loop = handler.isLoop();
        this.enabled = handler.isEnabled();
        this.autonomousStart = handler.isAutonomousStart();
    }

    @Override
    protected void init() {
        super.init();

        this.titleX = 8;
        this.titleY = 6;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        this.secondsField = new TextFieldWidget(
                this.textRenderer,
                x + 75,
                y + 24,
                40,
                18,
                Text.translatable("screen.redstone_timer.delay")
        );

        this.secondsField.setText(String.valueOf(this.handler.getSeconds()));
        this.secondsField.setMaxLength(4);
        this.secondsField.setTextPredicate(text -> text.matches("\\d*"));
        this.addDrawableChild(this.secondsField);

        this.onSecondsField = new TextFieldWidget(
                this.textRenderer,
                x + 75,
                y + 47,
                40,
                18,
                Text.translatable("screen.redstone_timer.on_time")
        );

        this.onSecondsField.setText(String.valueOf(this.handler.getOnSeconds()));
        this.onSecondsField.setMaxLength(4);
        this.onSecondsField.setTextPredicate(text -> text.matches("\\d*"));
        this.addDrawableChild(this.onSecondsField);

        this.loopButton = ButtonWidget.builder(getLoopText(), button -> {
            this.loop = !this.loop;
            this.loopButton.setMessage(getLoopText());
        }).dimensions(x + 75, y + 70, 24, 20).build();

        this.addDrawableChild(this.loopButton);

        this.enabledButton = ButtonWidget.builder(getEnabledText(), button -> {
            this.enabled = !this.enabled;
            this.enabledButton.setMessage(getEnabledText());
        }).dimensions(x + 75, y + 94, 40, 20).build();

        this.addDrawableChild(this.enabledButton);

        this.autonomousStartButton = ButtonWidget.builder(getAutonomousStartText(), button -> {
            this.autonomousStart = !this.autonomousStart;
            this.autonomousStartButton.setMessage(getAutonomousStartText());
        }).dimensions(x + 8, y + 116, 160, 18).build();

        this.addDrawableChild(this.autonomousStartButton);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.redstone_timer.apply"), button -> {

            String secondsText = this.secondsField.getText();
            String onSecondsText = this.onSecondsField.getText();

            if (!secondsText.isEmpty() && !onSecondsText.isEmpty()) {

                int seconds = Math.min(
                        RedstoneTimerBlockEntity.MAX_SECONDS,
                        Math.max(0, Integer.parseInt(secondsText))
                );

                int onSeconds = Math.min(
                        RedstoneTimerBlockEntity.MAX_SECONDS,
                        Math.max(1, Integer.parseInt(onSecondsText))
                );

                this.secondsField.setText(String.valueOf(seconds));
                this.onSecondsField.setText(String.valueOf(onSeconds));

                ClientPlayNetworking.send(
                        new RedstoneTimerUpdatePayload(
                                this.handler.getPos(),
                                seconds,
                                onSeconds,
                                this.loop,
                                this.enabled,
                                this.autonomousStart
                        )
                );

                this.applyFeedbackTicks = 40;
            }

        }).dimensions(x + 8, y + 139, 160, 18).build());
    }

    private Text getLoopText() {
        return Text.literal(this.loop ? "[X]" : "[ ]");
    }

    private Text getEnabledText() {
        return Text.translatable(this.enabled ? "screen.redstone_timer.on" : "screen.redstone_timer.off");
    }

    private MutableText getAutonomousStartText() {
        return Text.translatable("screen.redstone_timer.autonomous_start")
                .formatted(this.autonomousStart ? Formatting.GREEN : Formatting.WHITE);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(
                GUI_TEXTURE,
                x,
                y,
                0,
                0,
                backgroundWidth,
                backgroundHeight,
                backgroundWidth,
                backgroundHeight
        );
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);

        context.drawText(this.textRenderer, Text.translatable("screen.redstone_timer.delay_label"), 8, 28, 0x404040, false);
        context.drawText(this.textRenderer, Text.translatable("screen.redstone_timer.on_time_label"), 8, 51, 0x404040, false);
        context.drawText(this.textRenderer, Text.translatable("screen.redstone_timer.loop_label"), 8, 74, 0x404040, false);
        context.drawText(this.textRenderer, Text.translatable("screen.redstone_timer.enabled_label"), 8, 98, 0x404040, false);

        context.drawText(
                this.textRenderer,
                Text.translatable("screen.redstone_timer.max", RedstoneTimerBlockEntity.MAX_SECONDS),
                120,
                29,
                0x606060,
                false
        );

        context.drawText(
                this.textRenderer,
                Text.translatable("screen.redstone_timer.max", RedstoneTimerBlockEntity.MAX_SECONDS),
                120,
                52,
                0x606060,
                false
        );

        if (this.applyFeedbackTicks > 0) {

            Text appliedText = Text.translatable("screen.redstone_timer.applied")
                    .formatted(Formatting.GREEN);

            int textWidth = this.textRenderer.getWidth(appliedText);
            int x = (this.backgroundWidth / 2) - (textWidth / 2);

            // Fora da GUI, parte inferior
            int y = this.backgroundHeight + 8;

            context.drawText(
                    this.textRenderer,
                    appliedText,
                    x,
                    y,
                    0x00FF00,
                    true
            );
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();

        if (this.applyFeedbackTicks > 0) {
            this.applyFeedbackTicks--;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);

        this.secondsField.render(context, mouseX, mouseY, delta);
        this.onSecondsField.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}