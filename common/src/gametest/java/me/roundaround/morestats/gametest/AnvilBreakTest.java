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
public class AnvilBreakTest implements ClientTest {
  private static final int MAX_USES = 30;

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.ANVIL_BREAK);
      int before = world.getStat(stat);

      BlockPos anvilPos = world.playerBlockPos().south(2);
      world.setBlock(anvilPos, Blocks.DAMAGED_ANVIL);
      world.runCommand("xp set @s 99 levels");

      for (int i = 0; i < MAX_USES && world.getStat(stat) < before + 1; i++) {
        world.give(Items.DIAMOND_SWORD);
        context.waitTicks(2);

        ClientMenu anvil = world.openMenu(anvilPos, AnvilScreen.class);
        int swordSlot = anvil.findSlot(Items.DIAMOND_SWORD);
        anvil.pickup(swordSlot);
        anvil.pickup(AnvilMenu.INPUT_SLOT);
        anvil.rename("Anvil Buster " + i);
        anvil.take(AnvilMenu.RESULT_SLOT);
        context.waitTicks(2);

        if (!world.getBlock(anvilPos).equals(Blocks.DAMAGED_ANVIL)) {
          break;
        }

        context.setScreen(() -> null);
        context.waitTicks(2);
      }

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
