package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;

@ClientGameTest
public class StatsScreenTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().create()) {
      context.setScreen(() -> new PauseScreen(true));
      context.waitForScreen(PauseScreen.class);

      context.clickButtonByKey("gui.stats");
      context.waitForScreen(StatsScreen.class);
      context.assertScreen(StatsScreen.class);

      context.waitTicks(20);
      context.screenshot("morestats-stats-screen");
    }
  }
}
