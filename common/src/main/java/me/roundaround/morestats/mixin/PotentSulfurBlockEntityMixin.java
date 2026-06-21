package me.roundaround.morestats.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(PotentSulfurBlockEntity.class)
public abstract class PotentSulfurBlockEntityMixin {
  // A single eruption re-applies the upward impulse across its ~80-tick window (each
  // time deltaY dips back below the cap), so a per-impulse increment would overcount.
  // Discrete eruptions are >=15s apart (DORMANT countdown), so collapse a burst into
  // one launch via a per-player last-push game-time with a 1s gap.
  @Unique
  private static final Map<ServerPlayer, Long> morestats$lastLaunchTick = new WeakHashMap<>();

  @Unique
  private static final long morestats$LAUNCH_GAP_TICKS = 20L;

  @Inject(
      method = "lambda$static$5", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/Entity;addDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
  )
  )
  private static void onGeyserLaunch(CallbackInfo info, @Local(ordinal = 0, type = Entity.class) Entity entity) {
    if (!(entity instanceof ServerPlayer player)) {
      return;
    }

    long now = player.level().getGameTime();
    Long last = morestats$lastLaunchTick.put(player, now);
    if (last == null || now - last > morestats$LAUNCH_GAP_TICKS) {
      player.awardStat(MoreStats.GEYSER_LAUNCH);
    }
  }
}
