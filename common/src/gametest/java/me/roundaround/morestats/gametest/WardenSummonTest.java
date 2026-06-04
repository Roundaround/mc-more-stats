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
public class WardenSummonTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.WARDEN_SUMMON);
      int before = world.getStat(stat);

      BlockPos shrieker = world.playerBlockPos().below().south(1);
      world.setBlock(
          shrieker,
          Blocks.SCULK_SHRIEKER.defaultBlockState()
              .setValue(SculkShriekerBlock.CAN_SUMMON, true)
              .setValue(SculkShriekerBlock.SHRIEKING, false)
      );

      world.runOnServerPlayer((player) -> player.getWardenSpawnTracker().ifPresent((tracker) -> {
        tracker.reset();
        tracker.setWarningLevel(3);
      }));

      world.lookSouth();
      world.moveForward(20);
      // The shriek schedules tryRespond -> trySummonWarden ~90 ticks later.
      context.waitTicks(20 * 6);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
