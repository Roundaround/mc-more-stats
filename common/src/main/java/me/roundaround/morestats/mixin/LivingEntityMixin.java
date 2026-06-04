package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
  @Inject(
      method = "checkTotemDeathProtection", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/advancements/criterion/UsedTotemTrigger;trigger" +
               "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)V"
  )
  )
  public void onTotemPop(DamageSource source, CallbackInfoReturnable<Boolean> info) {
    if (!(this.morestats$self() instanceof ServerPlayer player)) {
      return;
    }

    player.awardStat(MoreStats.TOTEM_POP);

    Entity attacker = source.getEntity();
    if (attacker != null) {
      player.awardStat(MoreStats.TOTEMS_POPPED_BY.get(attacker.getType()));
      return;
    }

    DamageSources damageSources = player.level().damageSources();

    if (source == damageSources.flyIntoWall()) {
      player.awardStat(MoreStats.CRUNCH_TOTEM_POP);
    } else if (source == damageSources.enderPearl()) {
      player.awardStat(MoreStats.ENDER_PEARL_TOTEM_POP);
    } else if (source == damageSources.fall()) {
      player.awardStat(MoreStats.FALL_TOTEM_POP);
    } else if (source == damageSources.drown()) {
      player.awardStat(MoreStats.DROWN_TOTEM_POP);
    } else if (source == damageSources.starve()) {
      player.awardStat(MoreStats.STARVE_TOTEM_POP);
    } else if (source == damageSources.inFire() || source == damageSources.lava() || source == damageSources.onFire() ||
               source == damageSources.hotFloor()) {
      player.awardStat(MoreStats.FIRE_TOTEM_POP);
    } else if (source == damageSources.freeze()) {
      player.awardStat(MoreStats.POWDER_SNOW_TOTEM_POP);
    }
  }

  // Namespaced to avoid colliding with Forge's IForgeLivingEntity.self() entity-extension
  // default method: a plain `self()` merges into LivingEntity as a private method that shadows
  // the interface default, breaking `invokeinterface IForgeLivingEntity.self()` (e.g. from
  // ForgeHooks.onLivingBreathe) for every living entity on Forge — AbstractMethodError on tick 1.
  @Unique
  private LivingEntity morestats$self() {
    return (LivingEntity) (Object) this;
  }
}
