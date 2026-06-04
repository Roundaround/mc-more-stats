package me.roundaround.morestats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.morestats.network.MoreStatsNetworking;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
@MixinEnv(MixinEnv.Env.CLIENT)
public abstract class MinecraftMixin {
  @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
  public void onPerspectiveChange(CallbackInfo info) {
    MoreStatsNetworking.sendTogglePerspective();
  }
}
