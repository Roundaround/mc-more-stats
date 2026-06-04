package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

@ClientGameTest
public class TotemPopTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.TOTEM_POP);
      int before = world.getStat(stat);

      world.setOffHandItem(Items.TOTEM_OF_UNDYING);
      context.waitTicks(2);
      world.runCommand("damage @s 1000");
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
