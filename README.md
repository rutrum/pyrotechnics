# Pyrotechnics

A Minecraft mod that adds dedicated crafting stations for fireworks — like the Loom did for banners.

No new materials. No changed consumption. Just a better, more intuitive crafting process.

## Blocks

| Block | Job |
|---|---|
| **Color Vat** | Mix dyes (base + fade) with gunpowder to create a colored firework star |
| **Effect Bench** | Apply shape modifier and special effects (trail, twinkle) to a star |
| **Assembly Bench** | Combine paper, gunpowder, and finished stars into a rocket |

## Design

Each block has one focused job, like the loom, stonecutter, or smithing table.
The intended workflow moves through the stations in order:

```
Color Vat → Effect Bench → Assembly Bench
```

The crafting table recipes for fireworks can be removed via datapack, forcing use of these blocks.

## Building

```bash
# Enter dev shell
nix develop

# Build the mod
gradle build
```

## Target

Minecraft 26.2 (Fabric), Java 25
