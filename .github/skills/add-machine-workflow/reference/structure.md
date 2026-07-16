# Machine Add Workflow Structure

## Project structure

### Main registrations

- Block registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java
- BlockItem registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- BlockEntityType registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlockEntities.java
- MenuType registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModMenuType.java
- RecipeType registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeTypes.java
- RecipeSerializer registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeSerializers.java
- Mod wiring: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Screen registration: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java

### Baseline examples

- Block: src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneCrusherBlock.java
- BlockEntity: src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java
- Menu: src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneCrusherMenu.java
- Screen: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneCrusherScreen.java
- Recipe: src/main/java/com/yukke9265/cobblestone_xx_compressed/recipe/CobblestoneCrusherRecipe.java
- JEI plugin: src/main/java/com/yukke9265/cobblestone_xx_compressed/jei/ModJeiPlugin.java
- JEI category: src/main/java/com/yukke9265/cobblestone_xx_compressed/jei/category/CobblestoneCrusherRecipeCategory.java

## JEI rules

Do not put JEI API directly into base Menu or Screen classes. Instead, expose JEI-neutral definitions from those classes and let ModJeiPlugin do the actual JEI registration.

- Menu side: JeiRecipeTransferDefinition
- Screen side: JeiClickableAreaDefinition

When adding a new machine, return JEI-specific definitions only where needed, and keep the actual JEI registration in ModJeiPlugin.
