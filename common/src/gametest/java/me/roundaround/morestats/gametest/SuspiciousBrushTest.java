package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * suspicious_blocks_brushed is awarded when a player finishes brushing a
 * suspicious block, via {@code BrushableBlockEntityMixin#morestats$awardSuspiciousBlocksBrushed}.
 * Places suspicious gravel one step ahead of the player (supported by the flat
 * ground), arms the player with a brush via {@code item replace ... weapon.mainhand},
 * aims at the block with {@link ClientWorld#lookAt}, and holds the use key
 * ({@link ClientWorld#holdKey}) long enough for the multi-second brushing animation
 * to run to completion.
 *
 * @implNote brushing duration / aim is sensitive; the maintainer may need to tune
 *     the hold length on a real run.
 */
@ClientGameTest
public class SuspiciousBrushTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      BlockPos p = world.playerBlockPos();
      BlockPos block = p.south(2);
      world.setBlock(block, Blocks.SUSPICIOUS_GRAVEL);
      world.setMainHandItem(Items.BRUSH);
      context.waitTicks(2);

      var stat = Stats.CUSTOM.get(MoreStats.SUSPICIOUS_BLOCKS_BRUSHED);
      int before = world.getStat(stat);

      world.lookAt(block);
      world.holdKey(mc -> mc.options.keyUse, 120); // brushing takes several seconds
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
