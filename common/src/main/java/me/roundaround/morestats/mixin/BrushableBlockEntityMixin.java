package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
  @Inject(method = "brushingCompleted", at = @At("HEAD"))
  private void morestats$awardSuspiciousBlocksBrushed(
      ServerLevel level, LivingEntity user, ItemStack brush, CallbackInfo ci) {
    if (user instanceof ServerPlayer player) {
      player.awardStat(MoreStats.SUSPICIOUS_BLOCKS_BRUSHED);
    }
  }
}
