package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.world.entity.EntityType;

@ClientGameTest
public class DamagedByTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = MoreStats.DAMAGED_BY.get(EntityType.ZOMBIE);
      int before = world.getStat(stat);

      world.summon(EntityType.ZOMBIE, world.playerBlockPos().south(2), "{Silent:1b}");
      world.waitUntilDamaged();
      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
