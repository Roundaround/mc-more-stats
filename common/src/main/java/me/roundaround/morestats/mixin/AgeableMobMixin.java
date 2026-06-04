package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {
  @Shadow
  public abstract boolean isAgeLocked();

  @Inject(
      method = "mobInteract", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/AgeableMob;setAgeLocked(Lnet/minecraft/world/entity/Mob;" +
               "Ljava/util/function/Supplier;Lnet/minecraft/world/entity/player/Player;" +
               "Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)V"
  )
  )
  private void onSetAgeLock(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    // Value of locked is the state BEFORE applying the item
    boolean locked = this.isAgeLocked();
    if (locked) {
      player.awardStat(MoreStats.BABY_AGE_UNLOCKED);
    } else {
      player.awardStat(MoreStats.BABY_AGE_LOCKED);
    }
  }
}
