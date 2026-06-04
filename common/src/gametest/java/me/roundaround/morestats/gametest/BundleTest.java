package me.roundaround.morestats.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.morestats.MoreStats;
import me.roundaround.trove.gametest.ClientMenu;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

/**
 * item_bundled is awarded from BundleItemMixin when items are transferred into a bundle. Drives the
 * vanilla inventory screen: carry the bundle, then left-click it onto a stack of dirt — the PRIMARY
 * path (BundleItem#overrideStackedOnOther -> contents.tryTransfer) that the mixin hooks.
 */
@ClientGameTest
public class BundleTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().survival().create()) {
      world.give(Items.BUNDLE);
      world.give(Items.DIRT, 32);
      context.waitTicks(2);

      world.clickKey(mc -> mc.options.keyInventory);
      context.waitForScreen(InventoryScreen.class);

      ClientMenu inv = world.menu();
      int bundleSlot = inv.findSlot(Items.BUNDLE);
      int dirtSlot = inv.findSlot(Items.DIRT);

      var stat = Stats.CUSTOM.get(MoreStats.ITEM_BUNDLED);
      int before = world.getStat(stat);

      inv.pickup(bundleSlot);  // carry the bundle on the cursor
      inv.pickup(dirtSlot);    // left-click it onto the dirt -> transfer into the bundle
      context.waitTicks(5);

      world.assertStatAtLeast(stat, before + 1);
    }
  }
}
