# Machine Add Workflow Checklist

## Implementation order

1. Define the machine shape: input count, output count, processing time, GUI need, JEI need, automation need.
2. Add the block foundation: Block and BlockItem registration.
3. Add the BlockEntity foundation.
4. Implement the right-click entry point.
5. Add Menu, MenuType, Screen, and Screen registration if needed.
6. Add ContainerData, stillValid(...), and quickMoveStack(...) if needed.
7. Add the recipe layer: Recipe, RecipeType, RecipeSerializer, and lookup logic.
8. Add capability and side-specific automation if needed.
9. Add JEI category, plugin definition, click area, and transfer definition if needed.
10. Add blockstates, models, textures, lang, and recipe JSON resources.
11. Validate with compileJava, processResources, runClient, and runData as needed.

## Pre- and post-change checklist

### Before adding code

- Find the closest existing machine
- Confirm whether it can use the same responsibility split as Cobblestone Furnace
- Decide whether a new class is actually needed or whether a shared base class is enough
- Consider whether a GUI-free first pass is safer

### After adding code

- The block is registered
- The BlockItem is registered
- The BlockEntityType is registered
- The Block returns the correct BlockEntity
- Tick logic only reaches the target BlockEntityType
- The GUI opens
- Menu and Screen are paired correctly
- Save and load do not lose state
- The recipe loads correctly
- Fluid-only recipes use the right size() and isEmpty() logic
- The machine does not misbehave when the output is full
- The JEI category appears
- JEI background cropping and slot positions match the Screen
- The JEI plugin does not crash on startup
- Hopper and pipe directions behave as expected

## Implementation notes

- Keep the existing naming and structure.
- Avoid unnecessary abstraction.
- Do not mix client-only code with server-side code.
- Do not put JEI API directly into shared base classes.
- Do not call DeferredHolder.get() too early in static initialization.
- Start with the smallest working version, then add JEI or automation later.

### CP and long value note

The current CP machines use a 4096 CP base limit.

- Energized cube multipliers scale from 4x for the normal version, 16x for the first tiered step, and then 4x per tier.
- The current enum has 12 tiered energized cube steps, so the largest multiplier is 67,108,864x.
- 4096 CP times that multiplier gives 274,877,906,944 CP.
- That value is larger than the int limit of 2,147,483,647, so internal CP storage and maximum values must use long.
- ContainerData only syncs int values, so split long values into lower and upper ints.
- Use LongDataHelper.lowerInt(...), LongDataHelper.upperInt(...), and LongDataHelper.toLong(...) for splitting and restoration.
- Keep storedCobblestonePower, MAX_COBBLESTONE_POWER, conversion values, arithmetic, and NBT save/load as long on the BlockEntity side.
- Rebuild the long value in the Menu with getLongFromData(...).
- In the Screen, calculate the gauge ratio with long input and convert only the ratio to double for drawing.
- Before adding a new tier, calculate base CP times the largest energized cube multiplier and confirm the result still fits in long.

## Recommended workflow

1. Ask for a concise machine specification.
2. Pick the closest existing implementation.
3. Group the requirements using A through E.
4. List the files to change.
5. Make the smallest possible step-by-step edit.
6. Use compileJava as the first validation after code changes.
7. Use runClient only when GUI or JEI behavior needs visual verification.

## Preferred checks in this workspace

- .\gradlew.bat compileJava
- .\gradlew.bat processResources
- .\gradlew.bat runClient
- .\gradlew.bat runData

Use runData only when generated JSON needs to be updated. GUI and JEI changes often need runClient in addition to compileJava.

## Related documents

- docs/machine-block-guide.md
- docs/machine-recipe-guide.md
- docs/gui-guide.md
- docs/inventory-gui-guide.md
- docs/jei-machine-recipe-guide.md
- docs/client-server-basics.md
- docs/development-checks.md
