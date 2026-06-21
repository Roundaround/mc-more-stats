package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;

@ClientGameTest
public class GeyserLaunchTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.GEYSER_LAUNCH);
      int before = world.getStat(stat);

      // Build the geyser a few blocks off the player so construction can't disturb them.
      BlockPos origin = world.playerBlockPos();
      int x = origin.getX();
      int y = origin.getY();
      int z = origin.getZ() - 6;
      BlockPos sulfur = new BlockPos(x, y + 1, z);

      // Encase a 1-wide shaft so the fluids can't spread, then carve the column:
      // lava (continuous-eruption trigger) -> potent sulfur -> 3 water -> air. Placing
      // the water above the sulfur last flips it to CONTINUOUS (lava below + water
      // above), which runs the launch ticker every tick — no eruption wait needed.
      world.fill(new BlockPos(x - 1, y - 1, z - 1), new BlockPos(x + 1, y + 6, z + 1), Blocks.STONE);
      world.setBlock(new BlockPos(x, y, z), Blocks.LAVA);
      world.setBlock(sulfur, Blocks.POTENT_SULFUR);
      world.setBlock(new BlockPos(x, y + 2, z), Blocks.WATER);
      world.setBlock(new BlockPos(x, y + 3, z), Blocks.WATER);
      world.setBlock(new BlockPos(x, y + 4, z), Blocks.WATER);
      world.setBlock(new BlockPos(x, y + 5, z), Blocks.AIR);
      world.setBlock(new BlockPos(x, y + 6, z), Blocks.AIR);

      context.waitFor(mc -> mc.level != null
          && mc.level.getBlockState(sulfur).is(Blocks.POTENT_SULFUR)
          && mc.level.getBlockState(sulfur).getValue(PotentSulfurBlock.STATE) == PotentSulfurState.CONTINUOUS, 40);

      world.teleport(x + 0.5, y + 5, z + 0.5);
      context.waitTicks(40);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
