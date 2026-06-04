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
 * powder_snow_totem_pop is awarded from LivingEntityMixin#onTotemPop when a totem saves the player
 * from lethal FREEZE damage (natural source — must match {@code damageSources.freeze()}). Enclose
 * the player in powder snow, jump the freeze counter to fully-frozen with {@link ClientWorld#setFrozenTicks},
 * and drop to 1 hp so the next freeze tick is lethal.
 */
@ClientGameTest
public class PowderSnowTotemPopTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.POWDER_SNOW_TOTEM_POP);
      int before = world.getStat(stat);

      world.setOffHandItem(Items.TOTEM_OF_UNDYING);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p, Blocks.POWDER_SNOW);
      world.setBlock(p.above(1), Blocks.POWDER_SNOW);
      context.waitTicks(5);

      world.setFrozenTicks(140);
      world.setHunger(5, 0f);
      world.setHealth(1f);
      context.waitTicks(60);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
