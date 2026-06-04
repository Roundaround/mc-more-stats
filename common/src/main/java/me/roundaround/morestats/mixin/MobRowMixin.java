package me.roundaround.morestats.mixin;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.morestats.MoreStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$MobsStatisticsList$MobRow")
@MixinEnv(MixinEnv.Env.CLIENT)
public abstract class MobRowMixin {
  @Unique
  private Component morestats$damagedText;
  @Unique
  private boolean morestats$damagedAny = false;
  @Unique
  private Component morestats$damagedByText;
  @Unique
  private boolean morestats$damagedByAny = false;
  @Unique
  private Component morestats$totemsPoppedByText;
  @Unique
  private boolean morestats$totemsPoppedByAny = false;

  @Final
  @Shadow
  private Component mobName;

  @Inject(
      method = "<init>(Lnet/minecraft/client/gui/screens/achievement/StatsScreen$MobsStatisticsList;" +
               "Lnet/minecraft/world/entity/EntityType;)V", at = @At(value = "RETURN")
  )
  private void morestats$onInit(StatsScreen.MobsStatisticsList list, EntityType<?> type, CallbackInfo info) {
    // The leading `list` param mirrors the synthetic outer reference of this
    // non-static inner class's constructor; Mixin requires the handler args to
    // match the target <init> descriptor exactly. It is otherwise unused.
    Minecraft client = Minecraft.getInstance();
    assert client.player != null;
    StatsCounter statsCounter = client.player.getStats();
    String name = this.mobName.getString();

    int damaged = statsCounter.getValue(MoreStats.DAMAGED.get(type));
    if (damaged == 0) {
      this.morestats$damagedText = Component.translatable("stat_type.morestats.damaged.none", name);
    } else {
      this.morestats$damagedText = Component.translatable("stat_type.morestats.damaged",
          StatFormatter.DIVIDE_BY_TEN.format(damaged),
          name
      );
      this.morestats$damagedAny = true;
    }

    int damagedBy = statsCounter.getValue(MoreStats.DAMAGED_BY.get(type));
    if (damagedBy == 0) {
      this.morestats$damagedByText = Component.translatable("stat_type.morestats.damaged_by.none", name);
    } else {
      this.morestats$damagedByText = Component.translatable("stat_type.morestats.damaged_by",
          name,
          StatFormatter.DIVIDE_BY_TEN.format(damagedBy)
      );
      this.morestats$damagedByAny = true;
    }

    int totemsPoppedBy = statsCounter.getValue(MoreStats.TOTEMS_POPPED_BY.get(type));
    if (totemsPoppedBy == 0) {
      this.morestats$totemsPoppedByText = Component.translatable("stat_type.morestats.totems_popped_by.none", name);
    } else {
      this.morestats$totemsPoppedByText = Component.translatable("stat_type.morestats.totems_popped_by",
          name,
          totemsPoppedBy
      );
      this.morestats$totemsPoppedByAny = true;
    }
  }

  @Inject(
      method = "extractContent(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZF)V",
      at = @At(value = "RETURN")
  )
  private void morestats$onExtractContent(
      GuiGraphicsExtractor graphics,
      int mouseX,
      int mouseY,
      boolean hovered,
      float a,
      CallbackInfo info
  ) {
    Font font = Minecraft.getInstance().font;
    int x = this.morestats$self().getContentX();
    int y = this.morestats$self().getContentY();

    graphics.text(font,
        this.morestats$damagedText,
        x + 2 + 10,
        y + 1 + font.lineHeight * 3,
        this.morestats$damagedAny ? CommonColors.LIGHTER_GRAY : CommonColors.GRAY
    );
    graphics.text(font,
        this.morestats$damagedByText,
        x + 2 + 10,
        y + 1 + font.lineHeight * 4,
        this.morestats$damagedByAny ? CommonColors.LIGHTER_GRAY : CommonColors.GRAY
    );
    graphics.text(font,
        this.morestats$totemsPoppedByText,
        x + 2 + 10,
        y + 1 + font.lineHeight * 5,
        this.morestats$totemsPoppedByAny ? CommonColors.LIGHTER_GRAY : CommonColors.GRAY
    );
  }

  @Unique
  private ObjectSelectionList.Entry<?> morestats$self() {
    return (ObjectSelectionList.Entry<?>) (Object) this;
  }
}
