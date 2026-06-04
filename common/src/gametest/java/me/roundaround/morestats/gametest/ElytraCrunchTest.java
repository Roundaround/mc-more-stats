package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@ClientGameTest
public class ElytraCrunchTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var countStat = Stats.CUSTOM.get(MoreStats.CRUNCH);
      var damageStat = Stats.CUSTOM.get(MoreStats.CRUNCH_DAMAGE);
      var totemStat = Stats.CUSTOM.get(MoreStats.CRUNCH_TOTEM_POP);
      int countBefore = world.getStat(countStat);
      int damageBefore = world.getStat(damageStat);
      int totemBefore = world.getStat(totemStat);

      world.setHunger(5, 0f);
      world.setHealth(1f);
      world.setEquipment(EquipmentSlot.CHEST, Items.ELYTRA);
      world.setMainHandItem(Items.FIREWORK_ROCKET);
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      world.runCommand("tp @s ~ ~40 ~");
      BlockPos pos = world.playerBlockPos();
      int wallZ = pos.getZ() - 10;
      int yTop = pos.getY() + 4;
      int yBottom = Math.max(pos.getY() - 40, -62);
      world.fill(
          new BlockPos(pos.getX() - 8, yBottom, wallZ),
          new BlockPos(pos.getX() + 8, yTop, wallZ),
          Blocks.OBSIDIAN
      );

      world.look(180.0f, 25.0f);
      world.pressKey(mc -> mc.options.keyJump);
      world.useItem();
      world.waitForAbsorption();

      world.assertStatAtLeast(countStat, countBefore + 1);
      world.assertStatAtLeast(damageStat, damageBefore + 1);
      world.assertStatAtLeast(totemStat, totemBefore + 1);
    }
  }
}
