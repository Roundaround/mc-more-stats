package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * drown_totem_pop is awarded from LivingEntityMixin#onTotemPop when a totem saves the player from
 * lethal DROWN damage. The mixin compares the damage source by reference to the cached
 * {@code damageSources.drown()}, so the kill must be natural. Submerge the player, then use the
 * vitals primitives to empty their air and drop them to 1 hp so the next natural drown tick is lethal.
 */
@ClientGameTest
public class DrownTotemPopTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.DROWN_TOTEM_POP);
      int before = world.getStat(stat);

      world.runCommand("difficulty normal");
      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p.above(2), Blocks.GLASS); // cap so the player can't surface
      world.setBlock(p.above(1), Blocks.WATER);
      world.setBlock(p, Blocks.WATER);
      context.waitTicks(5);

      world.setAir(0);       // already out of air -> drown damage next tick
      world.setHealth(1.0f); // brink -> the natural drown tick is lethal -> totem pops on drown()
      context.waitTicks(40);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
