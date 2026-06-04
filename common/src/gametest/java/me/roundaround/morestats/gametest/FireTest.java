package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class FireTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var timeStat = Stats.CUSTOM.get(MoreStats.FIRE_TIME);
      var damageStat = Stats.CUSTOM.get(MoreStats.FIRE_DAMAGE);
      var totemStat = Stats.CUSTOM.get(MoreStats.FIRE_TOTEM_POP);
      int timeBefore = world.getStat(timeStat);
      int damageBefore = world.getStat(damageStat);
      int totemBefore = world.getStat(totemStat);

      world.setHunger(5, 0f);
      world.setHealth(4f);
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      world.setBlock(world.playerBlockPos(), Blocks.FIRE);
      world.waitForAbsorption();

      world.assertStatAtLeast(timeStat, timeBefore + 1);
      world.assertStatAtLeast(damageStat, damageBefore + 1);
      world.assertStatAtLeast(totemStat, totemBefore + 1);
    }
  }
}
