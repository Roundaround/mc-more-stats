package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientMenu;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class EnchantTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.ENCHANT_XP);
      int before = world.getStat(stat);

      BlockPos tablePos = world.playerBlockPos().south(2);
      world.setBlock(tablePos, Blocks.ENCHANTING_TABLE);
      world.runCommand("xp set @s 30 levels");
      world.give(Items.DIAMOND_SWORD);
      world.give(Items.LAPIS_LAZULI, 3);
      context.waitTicks(2);

      ClientMenu table = world.openMenu(tablePos, EnchantmentScreen.class);
      table.pickup(table.findSlot(Items.DIAMOND_SWORD));
      table.pickup(0);
      table.pickup(table.findSlot(Items.LAPIS_LAZULI));
      table.pickup(1);
      context.waitTicks(2);

      table.clickMenuButton(0);
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
