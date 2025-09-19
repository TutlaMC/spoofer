package net.tutla.spoofer.client.mixin.client;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import net.tutla.spoofer.SpooferConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public abstract class MixinModsScreen {

    @Shadow
    private ModListWidget modList;

    @Inject(method = "render", at = @At("HEAD"))
    private void renderHeadInjection(CallbackInfo ci) {
        modList.children().removeIf(entry -> SpooferConfig.isHidden(entry.getMod().getId()));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTailInjection(CallbackInfo ci) {

    }
}

