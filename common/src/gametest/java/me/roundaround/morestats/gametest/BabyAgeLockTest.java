package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

@ClientGameTest
public class BabyAgeLockTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.BABY_AGE_LOCKED);
      int before = world.getStat(stat);

      world.summon(
          EntityType.PIG,
          world.playerBlockPos().south(),
          "{Age:-24000,NoAI:1b,PersistenceRequired:1b,Silent:1b,Tags:[\"msbabyagelock\"]}"
      );
      world.setMainHandItem(Items.GOLDEN_DANDELION, 1);
      context.waitTicks(5);

      AgeableMob pig = world.nearestEntity(AgeableMob.class, 6.0);
      world.lookAt(pig);
      world.useItemOn(pig);
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
