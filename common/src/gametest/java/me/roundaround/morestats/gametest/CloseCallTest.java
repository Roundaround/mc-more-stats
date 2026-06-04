package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;

@ClientGameTest
public class CloseCallTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var closeStat = Stats.CUSTOM.get(MoreStats.CLOSE_CALL);
      var veryCloseStat = Stats.CUSTOM.get(MoreStats.VERY_CLOSE_CALL);
      int closeBefore = world.getStat(closeStat);
      int veryCloseBefore = world.getStat(veryCloseStat);

      world.runCommand("damage @s 17");
      context.waitTicks(5);

      world.assertStatAtLeast(closeStat, closeBefore + 1);

      world.runCommand("effect give @s minecraft:instant_health 1 200");
      context.waitTicks(20);
      world.runCommand("effect clear @s minecraft:instant_health");

      world.runCommand("damage @s 19");
      context.waitTicks(5);

      world.assertStatAtLeast(veryCloseStat, veryCloseBefore + 1);
    }
  }
}
