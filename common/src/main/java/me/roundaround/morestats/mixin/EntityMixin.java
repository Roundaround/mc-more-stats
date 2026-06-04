package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
  @Shadow
  public PortalProcessor portalProcess;

  @Inject(
      method = "handlePortal", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/Entity;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)" +
               "Lnet/minecraft/world/entity/Entity;"
  )
  )
  private void onTeleport(CallbackInfo ci) {
    this.getCurrentPortal().ifPresent((portal) -> {
      switch (portal) {
        case NetherPortalBlock ignored -> this.awardStat(MoreStats.NETHER_PORTAL);
        case EndPortalBlock ignored -> this.awardStat(MoreStats.END_PORTAL);
        case EndGatewayBlock ignored -> this.awardStat(MoreStats.END_GATEWAY);
        default -> this.awardStat(MoreStats.OTHER_PORTALS);
      }
    });
  }

  @Unique
  private Optional<Portal> getCurrentPortal() {
    return Optional.ofNullable(this.portalProcess)
        .map((portalProcess) -> ((PortalProcessorAccessor) portalProcess).getPortal());
  }

  @Unique
  private void awardStat(Identifier stat) {
    Entity self = (Entity) (Object) this;
    if (self instanceof Player player) {
      player.awardStat(stat);
      return;
    }
    self.getPassengers().forEach((entity) -> {
      if (entity instanceof Player rider) {
        rider.awardStat(stat);
      }
    });
  }
}
