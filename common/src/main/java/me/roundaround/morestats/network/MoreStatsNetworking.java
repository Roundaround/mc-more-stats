package me.roundaround.morestats.network;

import me.roundaround.morestats.MoreStats;
import me.roundaround.morestats.generated.Constants;
import me.roundaround.trove.network.TroveNetworking;
import me.roundaround.trove.network.TrovePacketCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Loader-agnostic networking, backed by Trove's network bridge. Replaces the
 * old Fabric-only {@code Networking}/{@code ClientNetworking}/{@code
 * ServerNetworking} trio. Trove's per-loader bridge runs the handler on the
 * server thread, so the handler body needs no manual {@code execute(...)}.
 */
public final class MoreStatsNetworking {
  private MoreStatsNetworking() {
  }

  /** Register payload types + receivers. Call from every loader's entrypoint. */
  public static void register() {
    TroveNetworking.registerC2S(TogglePerspectiveC2S.TYPE, TogglePerspectiveC2S.CODEC,
        (payload, player) -> player.awardStat(MoreStats.TOGGLE_PERSPECTIVE));
  }

  /** Client-side: tell the server the player changed camera perspective. */
  public static void sendTogglePerspective() {
    TroveNetworking.sendToServer(new TogglePerspectiveC2S());
  }

  public record TogglePerspectiveC2S() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TogglePerspectiveC2S> TYPE = new CustomPacketPayload.Type<>(
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "toggle_perspective_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePerspectiveC2S> CODEC =
        TrovePacketCodecs.empty(TogglePerspectiveC2S::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }
}
