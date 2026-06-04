package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.stats.Stats;

@ClientGameTest
public class PauseTimeTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.PAUSE_TIME);
      int before = world.getStat(stat);

      context.setScreen(() -> new PauseScreen(true));
      context.waitForScreen(PauseScreen.class);
      context.waitTicks(60);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
