package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;

@ClientGameTest
public class GlowingTimeTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.GLOWING_TIME);
      int before = world.getStat(stat);

      world.effect("minecraft:glowing", 30, 0);
      context.waitTicks(40);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
