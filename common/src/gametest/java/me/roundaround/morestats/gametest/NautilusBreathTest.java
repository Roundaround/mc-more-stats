package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;

@ClientGameTest
public class NautilusBreathTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.BREATH_OF_THE_NAUTILUS_TIME);
      int before = world.getStat(stat);
      world.effect("minecraft:breath_of_the_nautilus", 30, 0);
      context.waitTicks(40);
      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
