# Pyrotechnics

This mod adds custom crafting tables for constructing fireworks in an intuitive way.

## Goals for Initial Release

- [ ] Crafter that constructs fireworks stars, adds colors and fading colors
- [ ] Crafter that changes the shape, twinkle, and trail of firework stars
- [ ] Crafter that turns firework stars, gunpowder, into rockets
- [ ] Datapack that removes crafting table recipes for rockets

Each crafter will require (at least):
* Block
* BlockItem
* 6-sided texture

## Goals for Afterwards

- [ ] New pyrotechnic villager that sells explosives

## How Fireworks Work

Fireworks are composed of the following properties.

- Duration of how long it takes the rocket to explode, and therefore how high the rocket flies before exploding. This is determined by the number of gunpowder used in the recipe (1 to 3).
- A number of firework stars that explode with certain effects all at the same time.  A firework can have up to 7 firework stars, but is over 5 stars requires less gunpowder to be used in the recipe.

Firework stars are composed of the following properties.

- A shape of the explosion, default small ball
- Twinkling effect or not
- Trailing effect or not
- Optionally colors of the explosion
- Optionally colors that the explosion fades into at the end of the animation

## Issue with Crafting Table Method

Firework and firework stars are a long list of NBT tags, which itself is unrestrained and full of flexibility.  Unforunately the method for obtaining these in a crafting table limits availability of certain items.  In particular you cannot in vanilla mincraft create

* A firework star with no defined color
* A firework star with more than 5 stars and 3 duration
* A firework star with more than 8 colors
* A firework star that fades into more than 8 colors
* A firework star with 8 colors and additional properties like twinkling or trailing
* Modify a firework star's colors or properties (you can modify fading colors, however)

This mod aims to diversify the crafting recipes into a more intuitive process that is unrestrained by the limitations of a 9 slot crafting recipe.

## New Blocks

### Explosives Mixing Table

This block will construct firework stars from gunpowder.  It will construct firework stars with the twinkling, shape, and trailing properties.  It can also modify existing firework stars by adding or removing these properties.

### Firework Star Dye Vat

This will add the base color and fading colors to an existing firework star.  It will add additional colors to already colorized firework stars.

### Firework Assembly Table

This final station combines a multiple firework stars, 1 to 3 gunpowder, and paper into the final firework.

# Baby Steps

The first step is to create a crafter that takes in a single firework star, a single paper, and a single gunpowder to create a single firework.  This will require a host of class files to be created and is my opportunity to be comfortable with class and directory structure.

Then I will start modifying NBT tags of the firework rockets based on the inputs of the firework constructor.

Then I will allow the user to insert multiple firework stars together.

That should result in a final crafting block.  Then I can replicate this to the other two machines.
