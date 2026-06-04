package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class EndPortalTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.END_PORTAL);
      int before = world.getStat(stat);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p, Blocks.END_PORTAL);

      world.waitForDimension(Level.END, 20 * 25);
      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
