package me.roundaround.morestats.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public final class ItemStackHelper {
  private ItemStackHelper() {
  }

  public static boolean hasCustomName(ItemStack stack) {
    return stack.get(DataComponents.CUSTOM_NAME) != null;
  }
}
