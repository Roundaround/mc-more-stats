package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;

@ClientGameTest
public class ShriekerTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      BlockPos p = world.playerBlockPos();
      BlockPos aheadGround = p.below().south(1);
      world.setBlock(
          aheadGround,
          Blocks.SCULK_SHRIEKER.defaultBlockState()
              .setValue(SculkShriekerBlock.CAN_SUMMON, true)
              .setValue(SculkShriekerBlock.SHRIEKING, false)
      );
      context.waitTicks(2);

      var stat = Stats.CUSTOM.get(MoreStats.SHREIKER_TRIGGER);
      int before = world.getStat(stat);

      world.lookSouth();
      world.moveForward(20); // step onto the shrieker -> triggers a shriek
      context.waitTicks(20);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
