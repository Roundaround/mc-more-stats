package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;

@ClientGameTest
public class EndGatewayTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.END_GATEWAY);
      int before = world.getStat(stat);

      BlockPos here = world.playerBlockPos();
      BlockPos gateway = here.south(2);
      BlockPos exit = here.south(12);

      world.setBlock(gateway, Blocks.END_GATEWAY);
      world.runOnServerPlayer((player) -> {
        if (player.level().getBlockEntity(gateway) instanceof TheEndGatewayBlockEntity gw) {
          gw.setExitPosition(exit, true);
        }
      });
      context.waitTicks(2);

      world.lookSouth();
      world.moveForward(20);
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
