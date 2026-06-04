package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

/**
 * starve_totem_pop is awarded from LivingEntityMixin#onTotemPop when a totem saves the player from
 * lethal STARVE damage (natural source — must match {@code damageSources.starve()} by reference).
 * Starvation is only lethal on HARD difficulty, so set hard, empty the food bar, and drop to 1 hp
 * so the next starve tick kills.
 *
 * @implNote starvation ticks roughly every 80 ticks; the wait is sized generously above that.
 */
@ClientGameTest
public class StarveTotemPopTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      world.runCommand("difficulty hard");
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);
      context.waitTicks(2);

      world.setFood(0);
      world.setSaturation(0.0f);

      var stat = Stats.CUSTOM.get(MoreStats.STARVE_TOTEM_POP);
      int before = world.getStat(stat);

      world.setHealth(1.0f);
      context.waitTicks(120); // wait out a full starvation interval on hard

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
