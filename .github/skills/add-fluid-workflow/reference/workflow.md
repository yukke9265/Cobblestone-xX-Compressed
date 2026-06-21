# Fluid Add Workflow Checklist

## Implementation order

1. Define the fluid shape: name, color, temperature, density, viscosity, translucency, and bucket support.
2. Register the FluidType.
3. Register the fluid itself in ModFluids.
4. Wire ModFluidTypes and ModFluids into the mod bootstrap.
5. Register the client fluid extension.
6. Add a translucent render layer if needed.
7. Add the bucket to the creative tab if desired.
8. Add bucket models and names in datagen.
9. Add textures.
10. Validate with compileJava, runData, and runClient as needed.

## File targets

### Common updates

- src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java

### Resource targets

- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_still.png
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_flow.png
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_still.png.mcmeta
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_flow.png.mcmeta
- Optional overlay texture under the same folder

## Datagen guidance

This project prefers datagen for item models and language.

### A. Bucket item models

Choose the model style based on the goal:

1. Use a simple single look when the bucket can share one appearance.
2. Use layered item models when the bucket needs fluid-specific variation.

For layered bucket models:

1. Add a bucket helper in ModItemModelProvider.
2. Use minecraft:item/generated as the parent.
3. Use minecraft:item/bucket as layer0.
4. Use an overlay texture for layer1.

### B. Language files

Add at least:

1. The fluid name
2. The bucket name

Keep the fluid name aligned with the FluidType descriptionId.

### C. Fluid tags

The project does not currently rely on a fluid tag provider by default.
Add one only when there is a clear need, such as:

1. Grouping multiple fluids together
2. Accepting a family of fluids in recipes
3. Supporting cross-mod fluid tags

## Texture and visual notes

- Use separate still and flowing textures.
- Add .png.mcmeta files for animated textures.
- Use translucent rendering when the texture has transparency.
- Decide early whether color lives in the texture or in the client tint.
- A strong base texture can hide tint changes.
- Avoid overlays unless there is a visual reason to keep them.
- Use layered bucket textures if the bucket needs strong per-fluid differences.

### Existing lesson from molten_compressed_cobblestone

The first implementation reused a vanilla lava texture, so tint changes were hard to notice.

1. Check the IClientFluidTypeExtensions tint first.
2. If the color change is weak, check the base texture.
3. If the texture is animated, make sure the .png.mcmeta file exists.

Without .png.mcmeta, vertical fluid textures can be interpreted as a single static image.

## Pre- and post-change checklist

### Before adding code

- Confirm whether both source and flowing fluids are needed
- Separate FluidType from the fluid implementation
- Decide where the bucket and liquid block belong
- Decide whether translucent rendering is needed
- Decide the translation key early
