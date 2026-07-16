---
name: add-block-workflow
description: "Use when adding a new block, BlockItem, blockstate/model/loot resources, or stateful non-machine block to this NeoForge 1.21.1 mod."
---

# Block Add Workflow

Use this skill when adding non-machine block content in this workspace. Start with [reference/overview.md](reference/overview.md), then use [reference/structure.md](reference/structure.md), [reference/types.md](reference/types.md), and [reference/workflow.md](reference/workflow.md) as needed.

## Quick start

1. Confirm the block shape: simple block, stateful block, tiered set, or BlockItem-only addition.
2. Pick the closest existing example.
3. Keep the change aligned with ModBlocks, ModItems, and datagen providers.
4. Validate with compileJava first, then runData if generated resources change.

## Core files

- Blocks and block items: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- Datagen: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Models and blockstates: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModBlockStateProvider.java
- Language: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- Loot and tags: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/loot/ModBlockLootTableProvider.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/tag/ModBlockTagProvider.java

## Validation order

Prefer this order when it applies:

1. compileJava
2. runData
3. processResources
4. runClient

Use the reference file for the full block checklist, stateful block notes, tiered set notes, and resource rules.
