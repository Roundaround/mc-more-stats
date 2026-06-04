package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;

@ClientGameTest
public class TogglePerspectiveTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.TOGGLE_PERSPECTIVE);
      int before = world.getStat(stat);

      world.clickKey(mc -> mc.options.keyTogglePerspective);
      world.clickKey(mc -> mc.options.keyTogglePerspective);
      world.clickKey(mc -> mc.options.keyTogglePerspective);
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
