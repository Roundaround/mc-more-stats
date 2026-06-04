package me.roundaround.morestats.mixin;

import me.roundaround.morestats.MoreStats;
import me.roundaround.morestats.util.ItemStackHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuMixin {
  @Unique
  Optional<ItemStack> input = Optional.empty();

  @Inject(method = "quickMoveStack", at = @At(value = "HEAD"))
  public void beforeTakeItem(Player player, int index, CallbackInfoReturnable<ItemStack> info) {
    ItemCombinerMenu self = (ItemCombinerMenu) (Object) this;
    if (!(self instanceof AnvilMenu) || index != 2) {
      return;
    }

    this.input = Optional.of(self.getSlot(0).getItem().copy());
  }

  @Inject(method = "quickMoveStack", at = @At(value = "RETURN"))
  public void onTakeItem(Player player, int index, CallbackInfoReturnable<ItemStack> info) {
    ItemCombinerMenu self = (ItemCombinerMenu) (Object) this;
    if (!(self instanceof AnvilMenu) || index != 2) {
      return;
    }

    if (this.input.isEmpty()) {
      return;
    }

    ItemStack stack = info.getReturnValue();
    if (ItemStackHelper.hasCustomName(stack) && !stack.getHoverName().equals(this.input.get().getHoverName())) {
      player.awardStat(MoreStats.ITEM_RENAME, stack.getCount());
    }

    this.input = Optional.empty();
  }
}
