package me.roundaround.morestats.forge;

import me.roundaround.morestats.MoreStats;
import me.roundaround.morestats.network.MoreStatsNetworking;
import me.roundaround.trove.forge.TroveForge;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod("morestats")
public final class MoreStatsForgeMod {
  public MoreStatsForgeMod(FMLJavaModLoadingContext context) {
    TroveForge.bootstrap(context);

    // Vanilla registries are frozen during construction; defer to RegisterEvent.
    RegisterEvent.getBus(context.getModBusGroup()).addListener(event -> {
      event.register(Registries.STAT_TYPE, helper -> MoreStats.registerStatTypes(helper::register));
      event.register(Registries.CUSTOM_STAT, helper -> MoreStats.registerCustomStats(helper::register));
    });

    MoreStatsNetworking.register();
  }
}
