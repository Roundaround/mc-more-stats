package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class BounceTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.BOUNCE);
      int before = world.getStat(stat);

      BlockPos pos = world.playerBlockPos();
      world.fill(
          new BlockPos(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1),
          new BlockPos(pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1),
          Blocks.SLIME_BLOCK
      );

      world.runCommand("tp @s ~ ~8 ~");
      context.waitTicks(60);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
