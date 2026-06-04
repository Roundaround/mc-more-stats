package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;

@ClientGameTest
public class DamagedTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = MoreStats.DAMAGED.get(EntityType.COW);
      int before = world.getStat(stat);

      world.summon(EntityType.COW, world.playerBlockPos().south(2), "{NoAI:1b,Silent:1b,Tags:[\"msdamaged\"]}");
      Cow cow = world.nearestEntity(Cow.class, 6.0);
      world.lookAt(cow);
      context.waitTicks(2);
      world.attackLookedAtEntity();

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
