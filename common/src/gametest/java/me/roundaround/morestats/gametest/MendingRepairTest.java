package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * mending_repair is awarded by ExperienceOrbMixin#repairPlayerItems for the
 * durability an XP orb restores to a Mending-enchanted item. Builds a damaged
 * (damage=500) Mending diamond pickaxe as a real ItemStack on the server — so the
 * enchantment resolves against the server's dynamic enchantment registry — puts it
 * in the main hand, then summons a high-value experience orb at the player's feet
 * so it is auto-collected and consumed by the repair path instead of granting XP.
 *
 * @implNote If the orb is not auto-collected (e.g. the player is not within pickup
 * range or the spawn position drifts), the maintainer may need to tune the summon
 * position / wait duration on a real run.
 */
@ClientGameTest
public class MendingRepairTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      var stat = Stats.CUSTOM.get(MoreStats.MENDING_REPAIR);
      int before = world.getStat(stat);

      ItemStack pickaxe = world.computeOnServerPlayer(player -> {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        stack.set(DataComponents.DAMAGE, 500);
        stack.enchant(
            player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING),
            1
        );
        return stack;
      });
      world.setMainHandItem(pickaxe);
      context.waitTicks(2);

      world.summon(EntityType.EXPERIENCE_ORB, world.playerBlockPos(), "{Value:50}");
      context.waitTicks(20);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
