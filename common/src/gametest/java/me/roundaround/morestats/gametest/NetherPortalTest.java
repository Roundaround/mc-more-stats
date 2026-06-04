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

/**
 * nether_portal is awarded from EntityMixin#onTeleport when a portal teleport sends the player to the
 * Nether. Place portal blocks around the player and wait out the portal timer, then confirm the
 * dimension change with the {@link ClientWorld#waitForDimension} primitive.
 *
 * @implNote heavy: a real portal teleport plus generating/loading the Nether can take many seconds.
 * The maintainer may need to raise the launch timeout for this test.
 */
@ClientGameTest
public class NetherPortalTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.NETHER_PORTAL);
      int before = world.getStat(stat);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p, Blocks.NETHER_PORTAL);
      world.setBlock(p.above(1), Blocks.NETHER_PORTAL);

      world.waitForDimension(Level.NETHER, 20 * 25);
      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
