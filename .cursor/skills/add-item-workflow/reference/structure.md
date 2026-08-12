# Item Add Workflow Structure

## Project structure

### Main registrations

- Item registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- Creative tab wiring: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Datagen entry point: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Language provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- Item model provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- Recipe provider: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/recipe/ModRecipeProvider.java

### Baseline examples

- Food items: ModItems.COBBLESTONE_BREAD and ModItems.TierCobblestoneBread
- Material items: ModItems.COBBLESTONE_GEM and ModItems.TierCobblestoneGem
- BlockItems: ModItems.COMPRESSED_COBBLESTONE_ITEM and similar entries

## Resource rules

This project keeps most display names, item models, and recipes in datagen.

1. Update the provider first.
2. Run runData.
3. Check src/generated/resources.

Avoid duplicating the same JSON role in src/main/resources unless there is a specific exception.
