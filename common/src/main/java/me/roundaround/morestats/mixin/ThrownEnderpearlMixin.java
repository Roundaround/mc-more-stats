package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {
  @Unique
  Optional<Vec3> position = Optional.empty();

  @Inject(
      method = "onHit", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;getOwner()" +
               "Lnet/minecraft/world/entity/Entity;"
  )
  )
  public void onHitBeforeTeleport(HitResult hitResult, CallbackInfo info) {
    ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;

    if (!(self.getOwner() instanceof ServerPlayer player)) {
      return;
    }

    this.position = Optional.of(new Vec3(player.getX(), player.getY(), player.getZ()));
  }

  @Inject(
      method = "onHit", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetCurrentImpulseContext()V"
  )
  )
  public void onHitAfterTeleport(HitResult hitResult, CallbackInfo info) {
    ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;

    if (!(self.getOwner() instanceof ServerPlayer player) || this.position.isEmpty()) {
      return;
    }

    double dx = player.getX() - this.position.get().x();
    double dy = player.getY() - this.position.get().y();
    double dz = player.getZ() - this.position.get().z();

    int distance = Math.round((float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100f);
    if (distance > 0) {
      player.awardStat(MoreStats.ENDER_PEARL_ONE_CM, distance);
    }

    this.position = Optional.empty();
  }
}
