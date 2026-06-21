# Machine Add Workflow Types

## Requirements by machine type

### A. BlockEntity-only machines

Minimum requirements:

1. Dedicated Block class
2. Dedicated BlockEntity class
3. Registration in ModBlocks
4. BlockItem registration in ModItems
5. BlockEntityType registration in ModBlockEntities
6. The ModBlockEntities register call from CobblestonexXCompressed
7. saveAdditional and loadAdditional if state must persist
8. Tick logic if needed

### B. Machines with a GUI

Everything from A, plus:

1. Menu class
2. MenuType registration in ModMenuType
3. The ModMenuType register call from CobblestonexXCompressed
4. Screen class
5. Screen registration in CobblestonexXCompressedClient
6. A right-click path from the Block to ServerPlayer#openMenu(...)
7. A MenuProvider from the BlockEntity or an equivalent provider
8. ContainerData design for synced integer values
9. stillValid(...) distance and block checks
10. quickMoveStack(...) slot layout

### C. Machines with custom recipes

Everything from B, plus:

1. Recipe class
2. RecipeType registration
3. RecipeSerializer registration
4. The ModRecipeTypes and ModRecipeSerializers register calls from CobblestonexXCompressed
5. RecipeManager.getRecipeFor(...) lookup in the BlockEntity
6. Clear input, output, and progress conditions
7. Recipe JSON entries
8. Datagen support when the generated resources are used

### D. Automation-enabled machines

Everything from C, plus:

1. An IItemHandler or capability-facing handler
2. Side-specific input and output rules
3. A policy for a null side
4. Registration in CobblestonexXCompressed.registerCapabilities(...)
5. No conflict between GUI slot rules and external insertion or extraction rules

Automation handler notes:

- Prefer exposing the real slot count for combined IN_OUT handlers.
- Control insertion or extraction through insertItem(...) and extractItem(...) , not by shrinking getSlots().
- Avoid fake slot layouts that differ from the machine's real inventory layout.
- Follow the automationAccessHandler style used by Melter, Dissolution Chamber, and Reaction Chamber first.

### E. JEI-enabled machines

Everything from C, plus:

1. Add a category ID to ModJeiIds
2. Add a JEI category class
3. Add a MachineJeiDefinition to ModJeiPlugin
4. Pass the target RecipeType list to JEI in registerRecipes(...)
5. Register the machine block as a catalyst
6. Add Screen click area definitions
7. Add Menu recipe transfer definitions
8. Avoid early DeferredHolder.get() calls during static initialization

JEI layout notes:

- Read slot positions from MachineGuiLayouts instead of hardcoding separate JEI coordinates.
- Match background texture cropping to the existing machine family.
- For one-row PoweredMachine layouts, start from the same BACKGROUND_U = 3 and BACKGROUND_V = 6 used by Melter, Crusher, and Furnace style layouts.
- Independent JEI crop values tend to drift away from the real GUI.
