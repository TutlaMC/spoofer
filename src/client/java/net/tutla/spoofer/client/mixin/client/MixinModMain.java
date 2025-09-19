package net.tutla.spoofer.client.mixin.client;

import com.google.common.collect.ListMultimap;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.tutla.spoofer.SpooferConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModMenu.class)
public abstract class MixinModMain {
    @Shadow @Final public static Map<String, Mod> MODS;

    @Shadow @Final public static Map<String, Mod> ROOT_MODS;

    @Shadow @Final public static ListMultimap<Mod, Mod> PARENT_MAP;

    @Shadow private static int cachedDisplayedModCount;

    @Inject(method = "onInitializeClient", at = @At("TAIL"), remap = false)
    private void initializeHeadInjection(CallbackInfo ci) {
        MODS.forEach((id, mod) -> {
            System.out.println(mod.getName());
            if (SpooferConfig.isHidden(id)){
                MODS.remove(id);
            }
        });

        ROOT_MODS.forEach((id, mod) -> {
            System.out.println(mod.getName());
            if (SpooferConfig.isHidden(id)){
                MODS.remove(id);
            }
        });

        PARENT_MAP.clear();
    }

    @Inject(method = "getDisplayedModCount", at = @At("RETURN"), cancellable = true, remap = false)
    private static void getDisplayedModCountTailInjection(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("0");
    }
}

