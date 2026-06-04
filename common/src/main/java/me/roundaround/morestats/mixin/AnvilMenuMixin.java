package me.roundaround.morestats.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.morestats.MoreStats;
import me.roundaround.morestats.util.ItemStackHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
  @Inject(
      method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
      at = @At(value = "HEAD")
  )
  public void onTake(Player player, ItemStack stack, CallbackInfo info) {
    AnvilMenu self = (AnvilMenu) (Object) this;

    player.awardStat(MoreStats.ANVIL_XP, self.getCost());

    ItemStack input = self.getSlot(0).getItem();
    if (ItemStackHelper.hasCustomName(stack) && !stack.getHoverName().equals(input.getHoverName())) {
      player.awardStat(MoreStats.ITEM_RENAME, stack.getCount());
    }
  }

  // The anvil-break logic runs inside an `access.execute(...)` lambda in onTake.
  // Forge's binpatched runtime inserts an extra float (break chance) into the
  // lambda -> lambda$onTake$0(Player, float, Level, BlockPos), while Fabric and
  // NeoForge use (Player, Level, BlockPos). Mixin requires an @Inject handler to
  // match the full target descriptor, so we capture nothing and grab the single
  // Player argument via @Local(argsOnly) -- unambiguous on every loader. This keeps
  // a single common mixin instead of a per-loader split.
  @Inject(
      method = "lambda$onTake$0",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"
      )
  )
  private static void onBreakAnvil(CallbackInfo info, @Local(argsOnly = true) Player player) {
    player.awardStat(MoreStats.ANVIL_BREAK);
  }
}
