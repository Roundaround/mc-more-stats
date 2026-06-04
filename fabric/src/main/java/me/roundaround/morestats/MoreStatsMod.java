package me.roundaround.morestats;

import me.roundaround.allay.api.Entrypoint;
import me.roundaround.morestats.network.MoreStatsNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

@Entrypoint(Entrypoint.MAIN)
public final class MoreStatsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    // Fabric leaves the vanilla registries writable during mod init, so we can
    // register directly. (NeoForge/Forge must defer this to RegisterEvent.)
    MoreStats.registerStatTypes((id, type) -> Registry.register(BuiltInRegistries.STAT_TYPE, id, type));
    MoreStats.registerCustomStats((id, value) -> Registry.register(BuiltInRegistries.CUSTOM_STAT, id, value));

    MoreStatsNetworking.register();
  }
}
