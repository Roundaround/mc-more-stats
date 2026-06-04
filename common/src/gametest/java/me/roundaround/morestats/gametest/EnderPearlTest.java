package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

@ClientGameTest
public class EnderPearlTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var distStat = Stats.CUSTOM.get(MoreStats.ENDER_PEARL_ONE_CM);
      var damageStat = Stats.CUSTOM.get(MoreStats.ENDER_PEARL_DAMAGE);
      var totemStat = Stats.CUSTOM.get(MoreStats.ENDER_PEARL_TOTEM_POP);
      int distBefore = world.getStat(distStat);
      int damageBefore = world.getStat(damageStat);
      int totemBefore = world.getStat(totemStat);

      world.setHunger(5, 0f);
      world.setHealth(0.5f);
      world.setMainHandItem(Items.ENDER_PEARL);
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      world.lookHorizon();
      world.useItem();
      world.waitForAbsorption();

      world.assertStatAtLeast(distStat, distBefore + 1);
      world.assertStatAtLeast(damageStat, damageBefore + 1);
      world.assertStatAtLeast(totemStat, totemBefore + 1);
    }
  }
}
