package net.tutla.spoofer.client.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.tutla.spoofer.SpooferConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, CallbackInfo ci) {

    }
    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    private void onGetLeftText(CallbackInfoReturnable<List<String>> cir) {
        HashMap<String, HashMap<String, Object>> spoof = SpooferConfig.getSpoof();
        List<String> leftText = cir.getReturnValue();

        for (int i = 0; i < leftText.size(); i++) {
            String line = leftText.get(i);

            if (line.contains("fps")) {
                Pattern pattern = Pattern.compile("^(\\d+) fps");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int fps = Integer.parseInt(matcher.group(1));
                    fps += (Double) spoof.get("fps").get("value");
                    line = matcher.replaceFirst(fps + " fps");
                }
            } else if (line.startsWith("XYZ:")) {
                Pattern pattern = Pattern.compile("XYZ: ([\\d.-]+) / ([\\d.-]+) / ([\\d.-]+)");
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    double x = Double.parseDouble(matcher.group(1));
                    double y = Double.parseDouble(matcher.group(2));
                    double z = Double.parseDouble(matcher.group(3));

                    switch ((String) spoof.get("coordinate_mode").get("value")) {
                        case "FIXED":
                            x = (double) spoof.get("x").get("value");
                            y = (double) spoof.get("y").get("value");
                            z = (double) spoof.get("z").get("value");
                            break;
                        case "OFFSET":
                            x += (double) spoof.get("x").get("value");
                            y += (double) spoof.get("y").get("value");
                            z += (double) spoof.get("z").get("value");
                            break;
                        case "RANDOM":
                            x = Math.random() * 1000;
                            y = Math.random() * 1000;
                            z = Math.random() * 1000;
                            break;
                        default:
                            break;
                    }
                    line = String.format("XYZ: %.3f / %.3f / %.3f", x, y, z);
                }
            }
            leftText.set(i, line);
        }

        cir.setReturnValue(leftText);
    }
}