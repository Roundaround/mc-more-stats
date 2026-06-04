package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
  @Inject(method = "repairPlayerItems", at = @At(value = "RETURN"))
  private void repairPlayerItems(ServerPlayer player, int amount, CallbackInfoReturnable<Integer> info) {
    int consumed = amount - info.getReturnValueI();
    if (consumed > 0) {
      player.awardStat(MoreStats.MENDING_REPAIR, consumed);
    }
  }
}
