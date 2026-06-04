package me.roundaround.morestats;

import me.roundaround.morestats.generated.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Registry of every custom stat and stat type this mod adds.
 *
 * <p>StatType instances are constructed eagerly (construction touches no
 * registry), and the actual registry insertion is deferred to a loader-supplied
 * {@code registrar}. On Fabric this runs directly during {@code onInitialize};
 * on NeoForge/Forge it runs inside {@code RegisterEvent} while the vanilla
 * registries are unfrozen. Custom-stat formatters are bound via
 * {@link Stats#CUSTOM} at the same time, which only populates the StatType's
 * internal map and is therefore registry-agnostic.
 */
public final class MoreStats {
  private MoreStats() {
  }

  private static final StatFormatter TICKS_TO_HOURS = (ticks) -> {
    double hours = ticks / 20.0 / 60.0 / 60.0;
    return String.format(Locale.ROOT, "%.3f", hours);
  };

  private static final Map<Identifier, StatType<?>> STAT_TYPES = new LinkedHashMap<>();
  private static final Map<Identifier, StatFormatter> CUSTOM_STATS = new LinkedHashMap<>();

  public static final StatType<EntityType<?>> DAMAGED = registerType("damaged");
  public static final StatType<EntityType<?>> DAMAGED_BY = registerType("damaged_by");
  public static final StatType<EntityType<?>> TOTEMS_POPPED_BY = registerType("totem_popped_by");

  public static final Identifier PLAYED_HOURS = register("play_hours", TICKS_TO_HOURS);
  public static final Identifier CRUNCH = register("crunch", StatFormatter.DEFAULT);
  public static final Identifier SHREIKER_TRIGGER = register("shreiker_trigger", StatFormatter.DEFAULT);
  public static final Identifier WARDEN_SUMMON = register("warden_summon", StatFormatter.DEFAULT);
  public static final Identifier TOTEM_POP = register("totem_pop", StatFormatter.DEFAULT);
  public static final Identifier CRUNCH_TOTEM_POP = register("crunch_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier ENDER_PEARL_TOTEM_POP = register("ender_pearl_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier FALL_TOTEM_POP = register("fall_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier DROWN_TOTEM_POP = register("drown_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier STARVE_TOTEM_POP = register("starve_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier FIRE_TOTEM_POP = register("fire_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier POWDER_SNOW_TOTEM_POP = register("powder_snow_totem_pop", StatFormatter.DEFAULT);
  public static final Identifier CLOSE_CALL = register("close_call", StatFormatter.DEFAULT);
  public static final Identifier VERY_CLOSE_CALL = register("very_close_call", StatFormatter.DEFAULT);
  public static final Identifier BEACON_TIME = register("beacon_time", StatFormatter.TIME);
  public static final Identifier FIRE_TIME = register("fire_time", StatFormatter.TIME);
  public static final Identifier FROZEN_TIME = register("frozen_time", StatFormatter.TIME);
  public static final Identifier GLOWING_TIME = register("glowing_time", StatFormatter.TIME);
  public static final Identifier DROWN_TIME = register("drown_time", StatFormatter.TIME);
  public static final Identifier HUNGRY_TIME = register("hungry_time", StatFormatter.TIME);
  public static final Identifier STARVE_TIME = register("starve_time", StatFormatter.TIME);
  public static final Identifier PAUSE_TIME = register("pause_time", StatFormatter.TIME);
  public static final Identifier POWDER_SNOW_TIME = register("powder_snow_time", StatFormatter.TIME);
  public static final Identifier SPYGLASS_TIME = register("spyglass_time", StatFormatter.TIME);
  public static final Identifier FIRE_DAMAGE = register("fire_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier FALL_DAMAGE = register("fall_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier CRUNCH_DAMAGE = register("crunch_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier DROWN_DAMAGE = register("drown_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier STARVE_DAMAGE = register("starve_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier POWDER_SNOW_DAMAGE = register("powder_snow_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier ENDER_PEARL_DAMAGE = register("ender_pearl_damage", StatFormatter.DIVIDE_BY_TEN);
  public static final Identifier ENDER_PEARL_ONE_CM = register("ender_pearl_one_cm", StatFormatter.DISTANCE);
  public static final Identifier ANVIL_XP = register("anvil_xp", StatFormatter.DEFAULT);
  public static final Identifier ENCHANT_XP = register("enchant_xp", StatFormatter.DEFAULT);
  public static final Identifier ANVIL_BREAK = register("anvil_break", StatFormatter.DEFAULT);
  public static final Identifier ITEM_RENAME = register("item_rename", StatFormatter.DEFAULT);
  public static final Identifier MENDING_REPAIR = register("mending_repair", StatFormatter.DEFAULT);
  public static final Identifier TOGGLE_PERSPECTIVE = register("toggle_perspective", StatFormatter.DEFAULT);
  public static final Identifier NETHER_PORTAL = register("nether_portal", StatFormatter.DEFAULT);
  public static final Identifier END_PORTAL = register("end_portal", StatFormatter.DEFAULT);
  public static final Identifier END_GATEWAY = register("end_gateway", StatFormatter.DEFAULT);
  public static final Identifier OTHER_PORTALS = register("other_portals", StatFormatter.DEFAULT);
  public static final Identifier ITEM_BUNDLED = register("item_bundled", StatFormatter.DEFAULT);
  public static final Identifier SUSPICIOUS_BLOCKS_BRUSHED = register("suspicious_blocks_brushed", StatFormatter.DEFAULT);
  public static final Identifier BREATH_OF_THE_NAUTILUS_TIME = register("breath_of_the_nautilus_time", StatFormatter.TIME);
  public static final Identifier BABY_AGE_LOCKED = register("baby_age_locked", StatFormatter.DEFAULT);
  public static final Identifier BABY_AGE_UNLOCKED = register("baby_age_unlocked", StatFormatter.DEFAULT);

  private static Identifier register(String id, StatFormatter formatter) {
    Identifier identifier = Identifier.fromNamespaceAndPath(Constants.MOD_ID, id);
    CUSTOM_STATS.put(identifier, formatter);
    return identifier;
  }

  private static StatType<EntityType<?>> registerType(String id) {
    Identifier identifier = Identifier.fromNamespaceAndPath(Constants.MOD_ID, id);
    MutableComponent text = Component.translatable("stat_type." + Constants.MOD_ID + "." + id);
    StatType<EntityType<?>> type = new StatType<>(BuiltInRegistries.ENTITY_TYPE, text);
    STAT_TYPES.put(identifier, type);
    return type;
  }

  /**
   * Register every stat type with the {@code stat_type} registry. The registrar
   * is the loader-appropriate sink: a direct {@code Registry.register} on Fabric,
   * or a {@code RegisterEvent} helper on NeoForge/Forge.
   */
  public static void registerStatTypes(BiConsumer<Identifier, StatType<?>> registrar) {
    STAT_TYPES.forEach(registrar);
  }

  /**
   * Register every custom-stat identifier with the {@code custom_stat} registry
   * and bind its formatter. Must run while the registry is writable (mod init on
   * Fabric, {@code RegisterEvent} on NeoForge/Forge).
   */
  public static void registerCustomStats(BiConsumer<Identifier, Identifier> registrar) {
    CUSTOM_STATS.forEach((id, formatter) -> {
      registrar.accept(id, id);
      Stats.CUSTOM.get(id, formatter);
    });
  }

  /** Force class-load so the static stat fields above are initialized. */
  public static void load() {
  }
}
