package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;

/**
 * powder_snow_time / frozen_time / powder_snow_damage are awarded by
 * {@code ServerPlayerMixin#tick}: powder_snow_time ticks up while the player
 * {@code isInPowderSnow}, frozen_time ticks up once {@code isFullyFrozen}
 * (reached after ~140 ticks of accumulating freeze), and the NATURAL freeze
 * damage that follows is recorded as powder_snow_damage.
 *
 * <p>This encloses the player in powder snow at both feet and head level so they
 * sink, stay submerged, and freeze. Difficulty is set to normal because peaceful
 * disables freeze damage entirely. The freeze damage must be caused naturally —
 * the mixin compares the damage source by identity, so a {@code /damage} command
 * would never match the cached source and the stat would never fire.
 *
 * @implNote A fresh survival player wears no leather boots, so nothing prevents
 * them from sinking into and remaining in the powder snow. The full run
 * is ~220 ticks: ~20t to register being in the snow, 140t to fully
 * freeze, then freeze damage begins. The maintainer may need to tune
 * these durations on a real run.
 */
@ClientGameTest
public class PowderSnowTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var snowTime = Stats.CUSTOM.get(MoreStats.POWDER_SNOW_TIME);
      var frozenTime = Stats.CUSTOM.get(MoreStats.FROZEN_TIME);
      var snowDamage = Stats.CUSTOM.get(MoreStats.POWDER_SNOW_DAMAGE);
      int beforeSnow = world.getStat(snowTime);
      int beforeFrozen = world.getStat(frozenTime);
      int beforeDamage = world.getStat(snowDamage);

      BlockPos p = world.playerBlockPos();
      world.setBlock(p, Blocks.POWDER_SNOW);
      world.setBlock(p.above(1), Blocks.POWDER_SNOW);

      context.waitTicks(20);
      world.assertStatAtLeast(snowTime, beforeSnow + 1);

      context.waitTicks(160); // 140t to fully freeze, then freeze damage begins
      world.assertStatAtLeast(frozenTime, beforeFrozen + 1);

      context.waitTicks(40);
      world.assertStatAtLeast(snowDamage, beforeDamage + 1);
    }
  }
}
