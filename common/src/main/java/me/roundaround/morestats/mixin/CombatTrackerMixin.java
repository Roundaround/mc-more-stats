package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin {
  @Shadow
  @Final
  private LivingEntity mob;

  @Inject(method = "recordDamage", at = @At(value = "HEAD"))
  public void onRecordDamage(DamageSource source, float damage, CallbackInfo info) {
    int amount = Math.round(damage * 10f);
    Entity attacker = source.getEntity();

    if (!(this.mob instanceof Player player)) {
      if (attacker instanceof Player attackerPlayer) {
        attackerPlayer.awardStat(MoreStats.DAMAGED.get(this.mob.getType()), amount);
      }
      return;
    }

    float originalHealth = player.getHealth();
    float remaining = originalHealth - damage;

    if (originalHealth > 4f && remaining <= 4f && remaining > 1f) {
      player.awardStat(MoreStats.CLOSE_CALL);
      return;
    } else if (originalHealth > 1f && remaining <= 1f && remaining > 0f) {
      player.awardStat(MoreStats.VERY_CLOSE_CALL);
      return;
    }

    if (attacker != null) {
      player.awardStat(MoreStats.DAMAGED_BY.get(attacker.getType()), amount);
      return;
    }

    DamageSources damageSources = player.level().damageSources();

    if (source == damageSources.flyIntoWall()) {
      player.awardStat(MoreStats.CRUNCH);
      player.awardStat(MoreStats.CRUNCH_DAMAGE, amount);
    } else if (source == damageSources.enderPearl()) {
      player.awardStat(MoreStats.ENDER_PEARL_DAMAGE, amount);
    } else if (source == damageSources.fall()) {
      player.awardStat(MoreStats.FALL_DAMAGE, amount);
    } else if (source == damageSources.drown()) {
      player.awardStat(MoreStats.DROWN_DAMAGE, amount);
    } else if (source == damageSources.starve()) {
      player.awardStat(MoreStats.STARVE_DAMAGE, amount);
    } else if (source == damageSources.inFire() || source == damageSources.lava() || source == damageSources.onFire() ||
               source == damageSources.hotFloor()) {
      player.awardStat(MoreStats.FIRE_DAMAGE, amount);
    } else if (source == damageSources.freeze()) {
      player.awardStat(MoreStats.POWDER_SNOW_DAMAGE, amount);
    }
  }
}
