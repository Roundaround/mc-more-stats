package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientMenu;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class AnvilTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var xpStat = Stats.CUSTOM.get(MoreStats.ANVIL_XP);
      var renameStat = Stats.CUSTOM.get(MoreStats.ITEM_RENAME);
      int xpBefore = world.getStat(xpStat);
      int renameBefore = world.getStat(renameStat);

      BlockPos anvilPos = world.playerBlockPos().south(2);
      world.setBlock(anvilPos, Blocks.ANVIL);
      world.runCommand("xp set @s 30 levels");
      world.give(Items.DIAMOND_SWORD);
      context.waitTicks(2);

      ClientMenu anvil = world.openMenu(anvilPos, AnvilScreen.class);
      int swordSlot = anvil.findSlot(Items.DIAMOND_SWORD);
      anvil.pickup(swordSlot);
      anvil.pickup(AnvilMenu.INPUT_SLOT);
      anvil.rename("Excalibur");

      anvil.take(AnvilMenu.RESULT_SLOT);
      context.waitTicks(5);

      world.assertStatAtLeast(xpStat, xpBefore + 1);
      world.assertStatAtLeast(renameStat, renameBefore + 1);
    }
  }
}
