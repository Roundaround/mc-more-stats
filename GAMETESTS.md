# Game-test coverage

More Stats ships an end-to-end game-test suite built on the Trove client game-test
harness (`@ClientGameTest` + `ClientTest`/`ClientTestContext`/`ClientWorld`). Each test
spins up a throwaway single-player world, drives a **real** client to produce the in-game
event that awards a stat, then reads the authoritative server-side `StatsCounter` and
asserts the stat advanced.

Run them with a display attached:

```sh
./gradlew :fabric:runClientGameTests      # or :neoforge: / :forge:
```

`./gradlew :<loader>:scanGameTests` lists every discovered test without launching the game.

The mod registers **50 stats** (3 entity `StatType`s + 47 custom stats, all defined in
`MoreStats.java`). **All 50 have an active test** — every one appears in the table below.

## A note for future test authors: the damage-source `==` trap

`CombatTrackerMixin` and `LivingEntityMixin` decide *which* damage/totem stat to award by
comparing the incoming `DamageSource` **by reference** against the cached singletons on
`DamageSources` (`source == damageSources.fall()`, `inFire()`, `drown()`, …). `DamageSources`
caches those instances, so a **natural** hit (a real fall, real lava, real drowning) matches.

The `/damage <target> <amount> <type>` command does **not** — it constructs a *fresh*
`DamageSource`, which is never `==` the cached one. So:

* **Source-specific** stats (`fall_damage`, `fire_*`, `drown_*`, `starve_*`, `powder_snow_*`,
  `*_totem_pop` variants) **must be triggered with natural damage.** Using `/damage <type>`
  would silently never fire them.
* **Source-agnostic** stats (`totem_pop`, `close_call`, `very_close_call`, `damaged_by`,
  `totem_popped_by`) check health thresholds or the *attacker entity*, never the source
  identity, so the `/damage` command is fine for those.

Several source-specific damage/duration tests now also assert their `*_totem_pop` variant in
the same run: the player is dropped to ~1 health with a totem in the off-hand, the natural
hit pops the totem, and `ClientWorld#waitForAbsorption()` (the totem grants Absorption) is the
signal that the pop landed. This folds three former standalone tests
(`FallTotemPopTest`, `FireTotemPopTest`, `EnderPearlTotemPopTest`) into `FallDamageTest`,
`FireTest`, and `EnderPearlTest`.

## Tested stats (50)

Each stat below has an active game-test that produces the event and asserts the server-side
counter advanced. Rows that list several stats share one test.

| Stat(s) | Test | How it's triggered |
| --- | --- | --- |
| `play_hours` | `PlayHoursTest` | every-tick accumulator — wait |
| `pause_time` | `PauseTimeTest` | open the single-player pause screen |
| `spyglass_time` | `SpyglassTimeTest` | hold use-key with a spyglass |
| `glowing_time` | `GlowingTimeTest` | `glowing` effect |
| `breath_of_the_nautilus_time` | `NautilusBreathTest` | `breath_of_the_nautilus` effect |
| `beacon_time` | `BeaconTimeTest` | ambient beacon effect (`ambientEffect`) |
| `fire_time`, `fire_damage`, `fire_totem_pop` | `FireTest` | **natural** fire on a low-health player holding a totem |
| `fall_damage`, `fall_totem_pop` | `FallDamageTest` | lethal **natural** fall (`tp ~ ~10 ~` at 0.5 health) onto a held totem |
| `crunch`, `crunch_damage`, `crunch_totem_pop` | `ElytraCrunchTest` | glide into an obsidian wall at 1 health holding a totem |
| `ender_pearl_one_cm`, `ender_pearl_damage`, `ender_pearl_totem_pop` | `EnderPearlTest` | throw an ender pearl; the teleport's fall is lethal at 0.5 health and pops a held totem |
| `drown_time`, `drown_damage` | `DrownTest` | **natural** submersion |
| `hungry_time`, `starve_time`, `starve_damage` | `HungerTest` | `hunger` effect, normal difficulty |
| `powder_snow_time`, `frozen_time`, `powder_snow_damage` | `PowderSnowTest` | **natural** powder snow |
| `close_call`, `very_close_call` | `CloseCallTest` | calibrated `/damage` 17 then 19 (source-agnostic, pre-source branch) |
| `totem_pop` | `TotemPopTest` | totem + lethal `/damage` |
| `drown_totem_pop` | `DrownTotemPopTest` | `setAir(0)` + `setHealth(1)` over **natural** drown |
| `starve_totem_pop` | `StarveTotemPopTest` | `setFood(0)` + `setHealth(1)`, hard difficulty |
| `powder_snow_totem_pop` | `PowderSnowTotemPopTest` | `setFrozenTicks(140)` + `setHealth(1)` |
| `damaged` | `DamagedTest` | `attackLookedAtEntity()` a summoned cow |
| `damaged_by` | `DamagedByTest` | `/damage … by <zombie>` |
| `totem_popped_by` | `TotemsPoppedByTest` | totem + lethal `/damage … by <zombie>` |
| `anvil_xp`, `item_rename` | `AnvilTest` | open anvil menu + rename + take |
| `anvil_break` | `AnvilBreakTest` | repeated anvil use until it breaks (~12% RNG/use; loops up to 30 uses) |
| `enchant_xp` | `EnchantTest` | enchanting-table menu + click enchant button |
| `mending_repair` | `MendingRepairTest` | damaged Mending tool + XP orb |
| `item_bundled` | `BundleTest` | carry a bundle onto a stack |
| `suspicious_blocks_brushed` | `SuspiciousBrushTest` | brush suspicious gravel |
| `shreiker_trigger` | `ShriekerTest` | step onto a sculk shrieker |
| `toggle_perspective` | `TogglePerspectiveTest` | toggle-perspective keybind |
| `nether_portal` | `NetherPortalTest` | `waitForDimension(NETHER)` |
| `end_portal` | `EndPortalTest` | `waitForDimension(END)` |
| `baby_age_locked` | `BabyAgeLockTest` | golden dandelion `useItemOn` a baby pig in a 2-deep hole |
| `baby_age_unlocked` | `BabyAgeUnlockTest` | golden dandelion `useItemOn` a pre-locked (`AgeLocked:1b`) baby pig |
| `warden_summon` | `WardenSummonTest` | seed warning level to 3, step a `can_summon` shrieker → real warden summon |
| `end_gateway` | `EndGatewayTest` | explicit-exit (`setExitPosition`) gateway, walk in → same-dimension teleport |
| `other_portals` | `OtherPortalsTest` | synthetic non-vanilla `Portal` via `setAsInsidePortal` → `handlePortal` `default` branch |

These last three were the long-untested ones; see the implementation notes below for the
non-obvious bits. All three pass live (verified on Fabric; they compile on all three loaders).

Notes:

* `StatsScreenTest` exists but exercises the stats-screen UI rather than asserting a specific
  stat, so it isn't listed above.
* Several environmental/interaction tests (elytra crunch, drown, powder snow, brush, shrieker,
  hunger, the totem variants, the menu tests, and especially the portal tests) are timing- or
  mechanic-sensitive and carry an `@implNote`; their durations/positions — or, for the portals,
  the launch timeout — may need tuning on a real run.

## Implementation notes for the three hardest stats

`warden_summon`, `end_gateway`, and `other_portals` were the long-untested trio; each needed a
trick to be deterministic in a client gametest. All three are implemented **inline** — no new
`trove-gametest` primitive and no Allay change. Mechanics were read off the 26.1 merged sources.

### `warden_summon` — `WardenSummonTest`

`SculkShriekerBlockEntityMixin#trySummonWarden` awards the stat only when vanilla
`trySummonWarden` returns true, which needs the player's warning level at
`WardenSpawnTracker.MAX_WARNING_LEVEL` (4). Each shriek bumps the level by one, but every
increment arms a 200-tick (`WARNING_LEVEL_INCREASE_COOLDOWN`) refusal window, so four natural
shrieks take ~40 s. The test skips that pure time-gate: it seeds the player's tracker to level 3
(cooldown cleared) inline in a `runOnServerPlayer` block — `reset()` then `setWarningLevel(3)`,
both public — then steps a `can_summon` shrieker (`SculkShriekerBlock#stepOn → tryShriek` bumps
3 → 4). ~90 ticks later the scheduled block tick runs `tryRespond → trySummonWarden`, a real
warden spawns on the flat world, and the stat lands.
- Set `difficulty normal` first: `canRespond()` refuses on PEACEFUL (`spawnWardens` defaults true).
- Keep the shrieker on open ground — `trySummonWarden` needs room for a 1×3 warden or the summon
  (and the stat) silently won't fire.

### `end_gateway` — `EndGatewayTest`

No End trip needed (the earlier "needs a prior End trip" note was wrong). `END_GATEWAY` is
`noCollision()` with a 0-tick transition, and `Entity#handlePortal` still calls `teleport(...)`
for a *same-dimension* transition. A bare gateway only self-configures its exit in the End, but
`TheEndGatewayBlockEntity#setExitPosition` is public, so the test gives it an explicit exit and it
teleports here on the flat world. The mixin's `switch` then matches `EndGatewayBlock`.
- **Place the gateway away from the player (not at their feet) and set the exit before they enter.**
  A gateway entered with no exit yet arms its 40-tick teleport cooldown and the portal processor
  decays before the exit lands — so the test puts the gateway two blocks south, sets the exit via
  `runOnServerPlayer`, then walks the player in.

### `other_portals` — `OtherPortalsTest`

The `default` branch fires only for a `Portal` that is none of the three vanilla blocks
(`NetherPortalBlock`/`EndPortalBlock`/`EndGatewayBlock`) — and exactly those three implement
`Portal` in 26.1, so no vanilla block reaches it; the branch exists for *other mods'* portals.
The test supplies one directly: `setAsInsidePortal` is public, and a `PortalProcessor` built from
a 0-transition portal teleports on the next `handlePortal` tick (`portalTime++ (0) >= 0`). Inside a
`runOnServerPlayer` block it hands the player an anonymous non-vanilla `Portal` whose
`getPortalDestination` returns a same-level `TeleportTransition`, then calls `setAsInsidePortal`.
The real `handlePortal → teleport` path lands in `default`. No block, mixin, or registration — the
most faithful minimal stand-in for a modded portal.

---

**Reconciliation:** all 50 registered stats are listed above, every one tested. (Counts verified
against `MoreStats.java`: 3 entity `StatType`s + 47 custom = 50 registered; 50 distinct stat
constants are asserted across the suite's tests.)
