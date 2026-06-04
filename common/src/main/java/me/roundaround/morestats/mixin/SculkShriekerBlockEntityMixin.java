package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SculkShriekerBlockEntity.class)
public abstract class SculkShriekerBlockEntityMixin {
  @Unique
  Optional<Player> whoDoneIt = Optional.empty();

  @Inject(
      method = "shriek(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V",
      at = @At(value = "HEAD")
  )
  public void shriek(ServerLevel level, Entity sourceEntity, CallbackInfo info) {
    if (sourceEntity instanceof Player) {
      whoDoneIt = Optional.of((Player) sourceEntity);
      whoDoneIt.get().awardStat(MoreStats.SHREIKER_TRIGGER);
    }
  }

  @Inject(
      method = "trySummonWarden(Lnet/minecraft/server/level/ServerLevel;)Z",
      at = @At(value = "RETURN")
  )
  private void trySummonWarden(ServerLevel level, CallbackInfoReturnable<Boolean> info) {
    if (whoDoneIt.isPresent() && info.getReturnValue()) {
      whoDoneIt.get().awardStat(MoreStats.WARDEN_SUMMON);
    }
    whoDoneIt = Optional.empty();
  }
}
