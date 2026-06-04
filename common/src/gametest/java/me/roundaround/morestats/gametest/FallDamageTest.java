package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

@ClientGameTest
public class FallDamageTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var damageStat = Stats.CUSTOM.get(MoreStats.FALL_DAMAGE);
      var totemStat = Stats.CUSTOM.get(MoreStats.FALL_TOTEM_POP);
      int damageBefore = world.getStat(damageStat);
      int totemBefore = world.getStat(totemStat);

      world.setHunger(5, 0f);
      world.setHealth(0.5f);
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      world.runCommand("tp @s ~ ~10 ~");
      world.waitForAbsorption();

      world.assertStatAtLeast(damageStat, damageBefore + 1);
      world.assertStatAtLeast(totemStat, totemBefore + 1);
    }
  }
}
