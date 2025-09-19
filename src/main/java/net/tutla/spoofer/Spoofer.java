package net.tutla.spoofer;

import net.fabricmc.api.ModInitializer;

public class Spoofer implements ModInitializer {

    @Override
    public void onInitialize() {
        SpooferConfig.load();
    }
}
