package me.roundaround.morestats.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.morestats.MoreStats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
  // PRIMARY insert path #1: clicking a bundle onto a stack in a slot transfers
  // that stack into the bundle via contents.tryTransfer(slot, player), which
  // returns the number of items actually added. The SECONDARY branch of this
  // method only does extract / put-back (tryInsert(remainder)) and is NOT hooked.
  // Both targets are confirmed identical on vanilla, NeoForge, and Forge.
  @WrapOperation(
      method = "overrideStackedOnOther",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/item/component/BundleContents$Mutable;tryTransfer(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/entity/player/Player;)I"
      )
  )
  private int onTryTransfer(
      BundleContents.Mutable contents, Slot slot, Player player, Operation<Integer> original) {
    int added = original.call(contents, slot, player);
    if (added > 0) {
      player.awardStat(MoreStats.ITEM_BUNDLED, added);
    }
    return added;
  }

  // PRIMARY insert path #2: clicking a carried stack onto a bundle inserts it
  // directly via contents.tryInsert(other), returning items added. This is the
  // ONLY tryInsert call in overrideOtherStackedOnMe (the put-back tryInsert lives
  // in overrideStackedOnOther's SECONDARY branch, which we do not touch). Player
  // is captured via @Local(argsOnly) since it is a direct parameter of the target.
  @WrapOperation(
      method = "overrideOtherStackedOnMe",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/item/component/BundleContents$Mutable;tryInsert(Lnet/minecraft/world/item/ItemStack;)I"
      )
  )
  private int onTryInsert(
      BundleContents.Mutable contents, ItemStack other, Operation<Integer> original,
      @Local(argsOnly = true) Player player) {
    int added = original.call(contents, other);
    if (added > 0) {
      player.awardStat(MoreStats.ITEM_BUNDLED, added);
    }
    return added;
  }
}
