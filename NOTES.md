# Pyrotechnics — Design Notes

## Premise

Like the Loom did for banners, add dedicated crafting blocks for fireworks that replace the crafting table recipes. No new materials, no changed consumption — just a better crafting process.

Optional: the crafting table recipes can be removed (datapack-style), forcing use of the new blocks.

## The Three Blocks

### Color Vat 🎨

**Job:** Create a firework star with base and fade colors.

**Inputs:**
- Gunpowder (1, consumed — the star is born here)
- Dyes in a 4×4 grid (16 slots matching the 16 dye types in vanilla)

**UI:**
- 4×4 dye grid — drop dyes in, each slot accepts any dye
- Two color swatch panels:
  - **Base colors** — click loaded dye swatches to select (any number)
  - **Fade colors** — click loaded dye swatches to select (any number)
- Preview of the colored explosion
- Output slot: unfinished firework star (Colors + FadeColors set, no shape/effects)

**Logic:**
- When taking output: consume gunpowder + one of each selected dye
- Write `Colors` and `FadeColors` int arrays to the `Explosion` NBT tag
- Output the firework star item with those NBT tags

**Why 4×4:** There are 16 dye items in vanilla. Each slot holds one type. Player fills what they have.

### Effect Bench ✦

**Job:** Apply shape and special effects to a star.

**Inputs:**
- Unfinished star from Color Vat (or any firework star)
- Shape modifier (optional, one):
  - Feather → Burst (cone)
  - Gold Nugget → Star shape
  - Fire Charge → Large ball (big boom)
  - Mob Head (any) → Creeper face
- Diamond → Trail effect (optional)
- Glowstone Dust → Flicker/twinkle effect (optional)

**UI:**
- Slot for the star
- Slot for shape modifier
- Checkboxes or indicator slots for Trail / Flicker
- Preview updates to show shape + effects
- Output slot: finished firework star

**Logic:**
- Copy the input star's NBT
- Overwrite `Type`, `Flicker`, `Trail` in the `Explosion` tag
- Consume the shape item if present, diamond if checked, glowstone if checked

### Assembly Bench 🚀

**Job:** Combine paper, gunpowder, and finished stars into a rocket.

**Inputs:**
- Paper (1 slot, required)
- Gunpowder (1 slot — uses as many as inserted, caps at 3. Put in 1→short, 3→long, 5→still 3)
- Firework stars (8 slots — up to 7 used, 8th is buffer/overflow)

**UI:**
- Paper slot
- Gunpowder slot (auto-consumes up to 3)
- 8 star slots (7 for stars, 1 extra)
- Info panel: flight duration, explosion count, combined effects summary
- Output slot: firework rocket

**Logic:**
- Consume 1 paper
- Consume min(gunpowder count, 3) gunpowder
- Consume up to 7 stars (uses all present, max 7)
- Write `Flight` byte and `Explosions` list to the rocket's `Fireworks` NBT

## Workshop Pipeline

```
Color Vat ──→ Effect Bench ──→ Assembly Bench
```

Move through stations like a real workshop. Each block has one focused job, like the loom, stonecutter, or smithing table.

## Vanilla+ Design Goals

- **No new materials** — uses the same gunpowder, dyes, paper, feathers, nuggets, fire charges, mob heads, diamonds, glowstone
- **No changed consumption** — same material costs as vanilla
- **Vanilla-style UI** — slot-based, with preview, like the loom
- **Crafting table recipes are removable** — the blocks are the intended way to make fireworks

## Block Vibe

Artisanal. Wood, stone, cloth — think mortar & pestle, workbench, not machinery. These feel like a craftsman's tools, not a factory.

## Implementation Order

1. Assembly Bench — establishes the block → entity → handler → screen pipeline
2. Effect Bench — learn from assembly bench patterns
3. Color Vat — most complex UI with dye grid + color swatches

## How Firework NBT Works

**Firework Star item NBT:**
```
Explosion: {
  Colors:     [I; firework_color_ids...]   // base colors
  FadeColors: [I; firework_color_ids...]   // fade colors (optional)
  Type:       0b (small), 1b (large), 2b (star), 3b (creeper), 4b (burst)
  Flicker:    0b or 1b
  Trail:      0b or 1b
}
```

**Firework Rocket item NBT:**
```
Fireworks: {
  Flight:     1b, 2b, or 3b
  Explosions: [ ... list of Explosion compounds from stars ... ]
}
```

Color IDs are from `DyeColor.getFireworkColor()` — each vanilla dye maps to an int.

## Block Registration (Fabric 26.2 style)

In 26.2, blocks and items use separate ID registries (BlockIds, ItemIds, BlockItemIds).
Each block needs:
- Block class (extends Block, implements BlockEntityProvider)
- BlockEntity class
- ScreenHandler class
- Screen class (client-only)
- BlockItem
- Lang entry
- Blockstate JSON
- Block model JSON
- Item model JSON
- Block texture(s)
- GUI texture
- Loot table JSON

## Code Structure

```
src/main/java/net/rutrum/pyrotechnics/
├── Pyrotechnics.java              ← Main mod class (registration)
├── mod/
│   ├── ColorVatBlock.java
│   ├── ColorVatBlockEntity.java
│   ├── ColorVatScreenHandler.java
│   ├── EffectBenchBlock.java
│   ├── EffectBenchBlockEntity.java
│   ├── EffectBenchScreenHandler.java
│   ├── AssemblyBenchBlock.java
│   ├── AssemblyBenchBlockEntity.java
│   └── AssemblyBenchScreenHandler.java
├── mixin/                         ← (only if needed later)
│
src/client/java/net/rutrum/pyrotechnics/client/
├── PyrotechnicsClient.java        ← Client registration (screens)
├── screen/
│   ├── ColorVatScreen.java
│   ├── EffectBenchScreen.java
│   └── AssemblyBenchScreen.java
```

## Resources Per Block

```
src/main/resources/assets/pyrotechnics/
├── blockstates/
│   ├── color_vat.json
│   ├── effect_bench.json
│   └── assembly_bench.json
├── lang/en_us.json
├── loot_tables/blocks/
│   ├── color_vat.json
│   ├── effect_bench.json
│   └── assembly_bench.json
├── models/
│   ├── block/
│   │   ├── color_vat.json
│   │   ├── effect_bench.json
│   │   └── assembly_bench.json
│   └── item/
│       ├── color_vat.json
│       ├── effect_bench.json
│       └── assembly_bench.json
├── textures/
│   ├── block/
│   │   ├── color_vat.png
│   │   ├── color_vat_top.png
│   │   ├── effect_bench.png
│   │   └── assembly_bench.png
│   └── gui/
│       ├── color_vat.png
│       ├── effect_bench.png
│       └── assembly_bench.png
├── data/pyrotechnics/recipes/     ← recipe removal datapack
```
