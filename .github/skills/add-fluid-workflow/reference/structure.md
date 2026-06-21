# Fluid Add Workflow Structure

## Project structure

### Existing fluid use sites

- Tank block entity: src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneTankBlockEntity.java
- Reaction chamber recipe: src/main/java/com/yukke9265/cobblestone_xx_compressed/recipe/CobblestoneReactionChamberRecipe.java
- Tank screen: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneTankScreen.java
- Reaction chamber screen: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneReactionChamberScreen.java

### Main registrations

- Mod wiring: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Client wiring: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- Datagen entry point: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Item model provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- Language provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java

### Fluid registries

- FluidType registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluidTypes.java
- Fluid, liquid block, and bucket registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluids.java

The existing molten_compressed_cobblestone implementation is the baseline example. Keep the same registration order and responsibility split.

## Recommended structure

Keep fluid-related registration close together:

- FluidType registration: ModFluidTypes
- fluid, liquid block, and bucket registration: ModFluids

Do not spread these across ModBlocks and ModItems unless there is a specific reason.
