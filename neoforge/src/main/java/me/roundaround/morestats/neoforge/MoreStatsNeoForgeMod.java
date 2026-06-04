package me.roundaround.morestats.neoforge;

import me.roundaround.morestats.MoreStats;
import me.roundaround.morestats.network.MoreStatsNetworking;
import me.roundaround.trove.neoforge.TroveNeoForge;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("morestats")
public final class MoreStatsNeoForgeMod {
  public MoreStatsNeoForgeMod(IEventBus modBus, ModContainer container) {
    TroveNeoForge.bootstrap(modBus, container);

    // Vanilla registries are frozen during construction; defer to RegisterEvent.
    modBus.addListener(RegisterEvent.class, event -> {
      event.register(Registries.STAT_TYPE, helper -> MoreStats.registerStatTypes(helper::register));
      event.register(Registries.CUSTOM_STAT, helper -> MoreStats.registerCustomStats(helper::register));
    });

    MoreStatsNetworking.register();
  }
}
