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
public class DrownTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var timeStat = Stats.CUSTOM.get(MoreStats.DROWN_TIME);
      var damageStat = Stats.CUSTOM.get(MoreStats.DROWN_DAMAGE);
      int timeBefore = world.getStat(timeStat);
      int damageBefore = world.getStat(damageStat);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p.above(2), Blocks.GLASS);
      world.setBlock(p.above(1), Blocks.WATER);
      world.setBlock(p, Blocks.WATER);
      world.setAir(1);

      world.waitUntilDamaged();
      world.waitUntilDamaged();

      world.assertStatAtLeast(timeStat, timeBefore + 1);
      world.assertStatAtLeast(damageStat, damageBefore + 1);
    }
  }
}
