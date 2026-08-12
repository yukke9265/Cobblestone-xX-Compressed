# Fluid Add Workflow Types

## Requirements by fluid type

### A. Minimum registration elements

Each new fluid usually needs:

1. FluidType
2. source fluid
3. flowing fluid
4. LiquidBlock
5. BucketItem
6. still texture
7. flowing texture
8. client-side IClientFluidTypeExtensions registration
9. display name
10. bucket item model

### B. Optional extras

Add these only when needed:

1. overlay texture
2. translucent render layer
3. fluid tag
4. fluid ingredient or FluidStack JSON in recipes
5. creative tab entry
6. world generation or natural placement

### C. Project-specific note

Tank and reaction chamber screens display displayedFluid.getHoverName().
Make sure the FluidType descriptionId is correct so the in-game text stays readable.

## Base implementation pattern

NeoForge 1.21.1 fluids usually use:

1. DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID)
2. DeferredRegister.create(Registries.FLUID, MODID)
3. BaseFlowingFluid.Properties
4. new BaseFlowingFluid.Source(...)
5. new BaseFlowingFluid.Flowing(...)
6. new LiquidBlock(...)
7. new BucketItem(...)
8. RegisterClientExtensionsEvent for IClientFluidTypeExtensions

Fluid registration is not a single-object registration like a normal item or block.
