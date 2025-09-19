package net.tutla.spoofer.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.tutla.spoofer.SpooferAssets;
import net.tutla.spoofer.SpooferConfig;

import java.util.HashMap;

public class SpooferConfigScreen {
    public enum CoordinateMode {
        DEFAULT,
        RANDOM,
        FIXED,
        OFFSET
    }
    public static HashMap<String, HashMap<String, Object>> spoof = SpooferConfig.getSpoof();

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Spoofer+ Config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        general.addEntry(entryBuilder.startIntField(Text.literal("Toggle Key"), SpooferConfig.getToggleKey())
                .setSaveConsumer(SpooferConfig::setToggleKey)
                .setDefaultValue(0)
                .build());

        ConfigCategory gameplay = builder.getOrCreateCategory(Text.literal("F3 Spoofer"));
        SpooferConfig.getSpoof().forEach((spoofId, v) -> {
            Object val = v.get("value");

            HashMap<String, Object> spoofConfigRule = spoof.get(spoofId);
            Object rawDefaultValue = spoofConfigRule.get("default");
            MutableText spoofConfigRuleTitle = Text.literal((String) spoofConfigRule.get("title"));

            if (val instanceof Boolean) {
                gameplay.addEntry(entryBuilder
                        .startBooleanToggle(spoofConfigRuleTitle, (Boolean) val)
                        .setSaveConsumer(n -> spoofConfigRule.put("value", n))
                        .setDefaultValue((Boolean) rawDefaultValue)
                        .build());
            } else if (val instanceof Number) {
                gameplay.addEntry(entryBuilder
                        .startIntField(spoofConfigRuleTitle, ((Number) val).intValue())
                        .setSaveConsumer(n -> spoofConfigRule.put("value", n))
                        .setDefaultValue(((Number) rawDefaultValue).intValue())
                        .build());
            } else if (spoofConfigRule.containsKey("values")){
                switch (spoofId){
                    case "coordinate_mode" -> {
                        CoordinateMode currentValue = CoordinateMode.valueOf((String) val);
                        CoordinateMode defaultValue = CoordinateMode.valueOf((String) rawDefaultValue);
                        gameplay.addEntry(entryBuilder
                                .startEnumSelector(spoofConfigRuleTitle, CoordinateMode.class, currentValue)
                                .setSaveConsumer(n -> spoofConfigRule.put("value", n))
                                .setDefaultValue(defaultValue)
                                .build());
                    }
                    default -> {
                        String currentValue = (String) val;
                        String defaultValue = (String) spoofConfigRule.get("default");
                        gameplay.addEntry(entryBuilder
                                .startDropdownMenu(
                                        spoofConfigRuleTitle,
                                        currentValue,
                                        s -> s,
                                        Text::literal
                                )
                                .setSaveConsumer(n -> spoofConfigRule.put("value", n))
                                .setDefaultValue(defaultValue)
                                .build());
                    }
                }


            } else {
                gameplay.addEntry(entryBuilder
                        .startTextField(spoofConfigRuleTitle, (String) val)
                        .setSaveConsumer(n -> spoofConfigRule.put("value", n))
                        .setDefaultValue((String) rawDefaultValue)
                        .build());
            }
        });

        ConfigCategory mods = builder.getOrCreateCategory(Text.literal("Mods"));
        for (String mod : SpooferConfig.getHiddenMods()) {
            mods.addEntry(entryBuilder.startBooleanToggle(Text.literal(mod), true)
                    .setSaveConsumer(value -> {
                        if (!value) {
                            SpooferConfig.removeHiddenMod(mod);
                            builder.setSavingRunnable(() -> {
                            });
                        }
                    })
                    .build());
        }

        mods.addEntry(entryBuilder.startStrField(Text.literal("New Mod"), "")
                .setSaveConsumer(name -> {
                    if (!name.isEmpty()) {
                        SpooferConfig.addHiddenMod(name);
                    }
                })
                .setTooltip(Text.literal("Type a mod ID here and save to add it."))
                .build());

        builder.setSavingRunnable(() -> {
            SpooferAssets conf = new SpooferAssets();

            conf.hiddenMods = SpooferConfig.getHiddenMods();
            conf.toggleKey = SpooferConfig.getToggleKey();
            conf.spoof = spoof;
            SpooferConfig.save(conf);
        });

        return builder.build();
    }
}
