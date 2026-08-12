# Block Add Workflow Structure

## Project structure

### Main registrations

- Block registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java
- BlockItem registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- Creative tab wiring: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Datagen entry point: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Blockstate and model provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModBlockStateProvider.java
- Language provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- Loot provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/loot/ModBlockLootTableProvider.java
- Tag provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/tag/ModBlockTagProvider.java

### Baseline examples

- Simple blocks: ModBlocks.COMPRESSED_COBBLESTONE and ModBlocks.COMPRESSED_STONE
- Tiered blocks: ModBlocks.TierCompressedCobblestone and ModBlocks.TierCompressedStone
- Stateful example: src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneFurnaceBlock.java

## Resource rules

This project keeps most display names, blockstates, models, loot, and tags in datagen.

1. Update the provider first.
2. Run runData.
3. Check src/generated/resources.

Avoid duplicating the same JSON role in src/main/resources unless there is a specific exception.
