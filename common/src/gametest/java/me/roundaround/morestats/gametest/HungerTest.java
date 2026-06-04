package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;

@ClientGameTest
public class HungerTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var hungry = Stats.CUSTOM.get(MoreStats.HUNGRY_TIME);
      var starveTime = Stats.CUSTOM.get(MoreStats.STARVE_TIME);
      var starveDamage = Stats.CUSTOM.get(MoreStats.STARVE_DAMAGE);
      int beforeHungry = world.getStat(hungry);
      int beforeStarveTime = world.getStat(starveTime);
      int beforeStarveDamage = world.getStat(starveDamage);

      world.runCommand("effect give @s minecraft:hunger 60 255");
      world.waitUntilDamaged();

      world.assertStatAtLeast(hungry, beforeHungry + 1);
      world.assertStatAtLeast(starveTime, beforeStarveTime + 1);
      world.assertStatAtLeast(starveDamage, beforeStarveDamage + 1);
    }
  }
}
