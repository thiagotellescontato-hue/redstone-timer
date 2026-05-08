package net.thiagoaa.redstonetimer.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.thiagoaa.redstonetimer.config.RedstoneTimerConfig;

public class RedstoneTimerModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return new ConfigScreenFactory<Screen>() {
            @Override
            public Screen create(Screen parent) {
                return new RedstoneTimerConfigScreen(parent);
            }
        };
    }

    private static class RedstoneTimerConfigScreen extends Screen {

        private final Screen parent;
        private ButtonWidget soundButton;

        protected RedstoneTimerConfigScreen(Screen parent) {
            super(Text.translatable("config.redstone_timer.title"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.soundButton = ButtonWidget.builder(getSoundButtonText(), button -> {
                RedstoneTimerConfig.blockSound = !RedstoneTimerConfig.blockSound;
                RedstoneTimerConfig.save();

                this.soundButton.setMessage(getSoundButtonText());
            }).dimensions(
                    this.width / 2 + 10,
                    this.height / 2 - 20,
                    60,
                    20
            ).build();

            this.addDrawableChild(this.soundButton);

            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("config.redstone_timer.done"), button -> {
                        if (this.client != null) {
                            this.client.setScreen(this.parent);
                        }
                    }).dimensions(
                            this.width / 2 - 50,
                            this.height / 2 + 40,
                            100,
                            20
                    ).build()
            );
        }

        private Text getSoundButtonText() {
            return Text.translatable(RedstoneTimerConfig.blockSound ? "config.redstone_timer.yes" : "config.redstone_timer.no");
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    this.title,
                    this.width / 2,
                    this.height / 2 - 55,
                    0xFFFFFF
            );

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("config.redstone_timer.block_sound"),
                    this.width / 2 - 90,
                    this.height / 2 - 14,
                    0xFFFFFF
            );
        }
    }
}