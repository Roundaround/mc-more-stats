package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

/**
 * spyglass_time is awarded each tick the player {@code isScoping} (ServerPlayerMixin#tick) —
 * i.e. while actively using a spyglass. Puts a spyglass directly in the main hand
 * (via {@code item replace} — {@code give} would only drop it into the inventory) and
 * holds the use key to scope through it, accumulating the per-tick stat.
 */
@ClientGameTest
public class SpyglassTimeTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      world.setMainHandItem(Items.SPYGLASS);
      context.waitTicks(2);

      var stat = Stats.CUSTOM.get(MoreStats.SPYGLASS_TIME);
      int before = world.getStat(stat);

      world.holdKey(mc -> mc.options.keyUse, 40);
      context.waitTicks(2);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
