package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

@ClientGameTest
public class OtherPortalsTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.OTHER_PORTALS);
      int before = world.getStat(stat);

      BlockPos pos = world.playerBlockPos();
      Vec3 exit = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 8.5);

      world.runOnServerPlayer((player) -> {
        Portal portal = (currentLevel, entity, entryPos) -> new TeleportTransition(
            player.level(),
            exit,
            Vec3.ZERO,
            entity.getYRot(),
            entity.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        player.setAsInsidePortal(portal, player.blockPosition());
      });
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
