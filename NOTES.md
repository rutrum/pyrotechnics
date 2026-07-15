# Pyrotechnics — Design Notes

## Premise

Like the Loom did for banners, add dedicated crafting blocks for fireworks that replace the crafting table recipes. No new materials, no changed consumption — just a better crafting process.

Optional: the crafting table recipes can be removed (datapack-style), forcing use of the new blocks.

## The Three-Block Split

A firework star has three distinct aspects, each gets its own block:

### Block 1 — Color Vat 🎨

**Job:** Set base colors and fade colors on a firework star.

- Drop in dyes (any number, not limited to 8)
- Drop in the **gunpowder** — this is where the star is *born*
- UI shows two color pickers built from the loaded dyes:
  - **Base colors** — pick which colors the explosion shows
  - **Fade colors** — pick which colors it fades into
- Preview of the star's color composition
- Output: a firework star with colors and fade colors set, but **no shape or effects yet**

The fade is no longer a weird post-craft — it's just a second palette in the same UI.

### Block 2 — Effect Bench ✦

**Job:** Apply shape modifier and special effects to a star.

- Slot for the unfinished star from the Color Vat
- Slot for a **shape modifier** (choose one):
  - Feather → Burst (cone upward)
  - Gold Nugget → Star shape
  - Fire Charge → Large ball
  - Mob Head → Creeper face
- Slots/checkboxes for **effects** (stackable):
  - Diamond → Trail
  - Glowstone Dust → Twinkle/flicker
- Preview updates with shape and effects
- Output: a finished firework star

Keeps shape distinct from color — they're conceptually different decisions.

### Block 3 — Assembly Bench 🚀

**Job:** Combine paper, gunpowder, and finished stars into a rocket.

- Slot for **paper** (1, required)
- Slots for **gunpowder** (1-3, determines flight duration)
- Slots for **firework stars** (up to 7)
- Info panel shows:
  - Flight duration (short / medium / long)
  - Number of explosions / damage potential
  - Summary of combined effects
- Output: firework rocket

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

## Open Questions

- Block crafting recipes: what materials should the blocks themselves be made of? (stone + iron? wood + gunpowder?)
- Should the Color Vat and Effect Bench be one block with two tabs, or stay separate?
- GUI mockups needed for each block
- How to handle the "no crafting table recipe" toggle — baked into the mod config or separate datapack?
