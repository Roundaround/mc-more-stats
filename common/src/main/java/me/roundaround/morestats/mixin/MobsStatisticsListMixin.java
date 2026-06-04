package me.roundaround.morestats.mixin;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.morestats.MoreStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(StatsScreen.MobsStatisticsList.class)
@MixinEnv(MixinEnv.Env.CLIENT)
public abstract class MobsStatisticsListMixin {
  @Redirect(
      method = "<init>",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/stats/StatsCounter;getValue(Lnet/minecraft/stats/Stat;)I",
          ordinal = 0
      )
  )
  private int morestats$getValue(StatsCounter statsCounter, Stat<EntityType<?>> stat) {
    EntityType<?> entityType = stat.getValue();
    List<Stat<EntityType<?>>> allStats = List.of(
        Stats.ENTITY_KILLED.get(entityType),
        Stats.ENTITY_KILLED_BY.get(entityType),
        MoreStats.DAMAGED.get(entityType),
        MoreStats.DAMAGED_BY.get(entityType),
        MoreStats.TOTEMS_POPPED_BY.get(entityType)
    );
    return allStats.stream().mapToInt(statsCounter::getValue).max().orElse(0);
  }

  @ModifyArgs(
      method = "<init>",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/gui/components/ObjectSelectionList;<init>" +
                   "(Lnet/minecraft/client/Minecraft;IIII)V"
      )
  )
  private static void morestats$onSuper(Args args) {
    // Must be static: this @ModifyArgs targets the super() ctor invocation, and
    // `this` is not available before the super constructor returns.
    int currentHeight = args.get(4);
    args.set(4, currentHeight + Minecraft.getInstance().font.lineHeight * 3);
  }
}
