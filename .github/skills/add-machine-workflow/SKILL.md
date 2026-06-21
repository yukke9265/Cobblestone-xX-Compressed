---
name: add-machine-workflow
description: "Use when adding a machine block, block entity, menu/screen GUI, machine recipe, automation, or JEI integration to this NeoForge 1.21.1 mod."
---

# Machine Add Workflow

Use this skill when adding or extending machine-style content in this workspace. Keep the skill itself short; start with [reference/overview.md](reference/overview.md), then use [reference/structure.md](reference/structure.md), [reference/types.md](reference/types.md), and [reference/workflow.md](reference/workflow.md) as needed.

## Quick start

1. Confirm the machine shape: block-only, block entity, GUI, recipe, automation, or JEI.
2. Pick the closest existing example, usually Cobblestone Furnace or Cobblestone Crusher.
3. Apply the smallest change that matches the existing registry and UI patterns.
4. Validate with the cheapest useful check first, usually compileJava.

## Core files

- Blocks and block items: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- Block entities: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlockEntities.java
- Menus and screens: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModMenuType.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- Recipes: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeTypes.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeSerializers.java
- JEI: src/main/java/com/yukke9265/cobblestone_xx_compressed/jei/ModJeiPlugin.java

## Validation order

Prefer this order when it applies:

1. compileJava
2. processResources
3. runClient
4. runData

Use the reference file for the full checklist, machine-type requirements, JEI rules, automation rules, and long-value notes.
