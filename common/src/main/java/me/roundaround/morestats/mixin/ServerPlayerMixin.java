package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
  @Inject(
      method = "doTick", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/food/FoodData;tick" +
               "(Lnet/minecraft/server/level/ServerPlayer;)V"
  )
  )
  public void tick(CallbackInfo info) {
    Player self = (Player) (Object) this;

    self.awardStat(MoreStats.PLAYED_HOURS);

    if (self.isOnFire()) {
      self.awardStat(MoreStats.FIRE_TIME);
    }

    if (self.isFullyFrozen()) {
      self.awardStat(MoreStats.FROZEN_TIME);
    }

    if (self.isScoping()) {
      self.awardStat(MoreStats.SPYGLASS_TIME);
    }

    if (self.isCurrentlyGlowing()) {
      self.awardStat(MoreStats.GLOWING_TIME);
    }

    if (!self.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(self) &&
        !self.getAbilities().invulnerable && self.getAirSupply() <= 0) {
      self.awardStat(MoreStats.DROWN_TIME);
    }

    if (self.getFoodData().getFoodLevel() <= 0) {
      self.awardStat(MoreStats.STARVE_TIME);
    }

    if (self.getFoodData().getFoodLevel() <= 6f) {
      self.awardStat(MoreStats.HUNGRY_TIME);
    }

    if (self.getActiveEffects().stream().anyMatch(MobEffectInstance::isAmbient)) {
      self.awardStat(MoreStats.BEACON_TIME);
    }

    if (self.isInPowderSnow) {
      self.awardStat(MoreStats.POWDER_SNOW_TIME);
    }
  }

  @Inject(method = "onEnchantmentPerformed", at = @At(value = "HEAD"))
  public void onEnchantmentPerformed(ItemStack stack, int cost, CallbackInfo info) {
    Player self = (Player) (Object) this;
    self.awardStat(MoreStats.ENCHANT_XP, cost);
  }
}
