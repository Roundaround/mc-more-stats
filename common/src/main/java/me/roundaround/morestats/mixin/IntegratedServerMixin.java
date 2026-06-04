package me.roundaround.morestats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.morestats.MoreStats;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerPlayer;

@Mixin(IntegratedServer.class)
@MixinEnv(MixinEnv.Env.CLIENT)
public abstract class IntegratedServerMixin {
  @Inject(method = "tickPaused", at = @At(value = "HEAD"))
  public void onTickPaused(CallbackInfo info) {
    for (ServerPlayer player : ((IntegratedServer) (Object) this).getPlayerList().getPlayers()) {
      player.awardStat(MoreStats.PAUSE_TIME);
    }

    // TODO: MinecraftServer.tick increment this stat if MultiplayerServerPause
    // is installed and game is paused via it
  }
}
