package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
  @Inject(
      method = "entityInside", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;showEndCredits()V"
  )
  )
  private void onShowCredits(
      BlockState state,
      Level level,
      BlockPos pos,
      Entity entity,
      InsideBlockEffectApplier effectApplier,
      boolean isPrecise,
      CallbackInfo ci
  ) {
    if (!(entity instanceof ServerPlayer player)) {
      return;
    }
    player.awardStat(MoreStats.END_PORTAL);
  }
}
