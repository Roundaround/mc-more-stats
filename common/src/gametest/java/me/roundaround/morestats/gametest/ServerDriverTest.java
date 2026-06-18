package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ServerGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.GameTestAssertionException;
import me.roundaround.trove.gametest.ServerTest;
import me.roundaround.trove.gametest.ServerTestContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Validates the {@code @ServerGameTest} behaviour driver against a headless
 * dedicated server: a directly-manipulable mock player, world mutation that
 * round-trips through the live level, and stat read/write for a real mod stat.
 * (Combat-driven stats like close-call need a real connected player and are
 * covered by the client tests; a headless mock player takes no damage.)
 */
@ServerGameTest
public class ServerDriverTest implements ServerTest {
  @Override
  public void runTest(ServerTestContext context) {
    ServerPlayer player = context.player();

    // The mock player is a real, directly-manipulable subject.
    context.runOnServer(s -> player.setHealth(6.0f));
    if (context.getHealth() >= 20.0f) {
      throw new GameTestAssertionException("setHealth had no effect: " + context.getHealth());
    }

    // World mutation round-trips through the live server level.
    BlockPos pos = new BlockPos(0, 70, 0);
    context.setBlock(pos, Blocks.DIAMOND_BLOCK);
    Block placed = context.computeOnServer(s -> s.overworld().getBlockState(pos).getBlock());
    if (placed != Blocks.DIAMOND_BLOCK) {
      throw new GameTestAssertionException("setBlock: expected diamond_block but was " + placed);
    }

    // Stat counters read and write for a real mod stat.
    var stat = Stats.CUSTOM.get(MoreStats.CLOSE_CALL);
    int before = context.getStat(stat);
    context.runOnServer(s -> player.awardStat(stat, 2));
    context.assertStatAtLeast(stat, before + 2);
  }
}
