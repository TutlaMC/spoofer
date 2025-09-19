package net.tutla.spoofer.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.tutla.spoofer.SpooferConfig;

public class SpooferConfigScreenOld {

    public static Screen create(Screen parent) {
        return buildScreen(parent);
    }

    private static Screen buildScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.literal("Hidden Mods"))
                .setParentScreen(parent);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Mods"));

        for (String mod : SpooferConfig.getHiddenMods()) {
            category.addEntry(entryBuilder.startBooleanToggle(Text.literal(mod), true)
                    .setSaveConsumer(value -> {
                        if (!value) {
                            SpooferConfig.removeHiddenMod(mod);
                            builder.setSavingRunnable(() -> {
                                MinecraftClient.getInstance().setScreen(buildScreen(parent));
                            });
                        }
                    })
                    .build());
        }

        category.addEntry(entryBuilder.startStrField(Text.literal("New Mod"), "")
                .setSaveConsumer(name -> {
                    if (!name.isEmpty()) {
                        SpooferConfig.addHiddenMod(name);
                        builder.setSavingRunnable(() -> {
                            MinecraftClient.getInstance().setScreen(buildScreen(parent));
                        });
                    }
                })
                .setTooltip(Text.literal("Type a mod ID here and save to add it."))
                .build());

        return builder.build();
    }
}

