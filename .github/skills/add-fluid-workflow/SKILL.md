---
name: add-fluid-workflow
description: "Use when adding a new fluid, FluidType, source/flowing fluid pair, liquid block, bucket item, fluid textures, or fluid-related datagen to this NeoForge 1.21.1 mod."
---

# Fluid Add Workflow

Use this skill when adding fluid content in this workspace. Start with [reference/overview.md](reference/overview.md), then use [reference/structure.md](reference/structure.md), [reference/types.md](reference/types.md), and [reference/workflow.md](reference/workflow.md) as needed.

## Quick start

1. Confirm the fluid shape: world fluid, bucket fluid, translucent fluid, or machine/tank-only fluid.
2. Pick the closest existing example, usually molten_compressed_cobblestone.
3. Keep the change aligned with ModFluidTypes, ModFluids, and the client extension registration.
4. Validate with compileJava first, then runClient if the visuals changed, and runData if generated resources changed.

## Core files

- Fluid types: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluidTypes.java
- Fluids, blocks, and buckets: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluids.java
- Client fluid rendering: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- Datagen: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Item models and language: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java and src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java

## Validation order

Prefer this order when it applies:

1. compileJava
2. runData
3. processResources
4. runClient

Use the reference file for the full fluid checklist, registry layout, client rendering notes, bucket model notes, texture notes, and long-form implementation guidance.

### 既存 fluid 実装から学ぶ点

molten_compressed_cobblestone では、次の土台がすでに確認済みです。

1. ModFluidTypes で FluidType と tint 基準色を持つ
2. ModFluids で source / flowing / liquid block / bucket をまとめる
3. CobblestonexXCompressed で各 DeferredRegister を modEventBus へ接続する
4. CobblestonexXCompressedClient で custom texture と tint を登録する
5. ModEnglishLanguageProvider で fluid 名と bucket 名を追加する
6. ModItemModelProvider で bucket model を生成し、必要なら item/bucket 配下の overlay PNG を使う

新しい液体を追加するときは、この 6 点がそろっているかを最初の土台確認に使ってください。

## 15. 関連ドキュメント

必要に応じて次も参照してください。

- docs/client-server-basics.md
- docs/development-checks.md
- .github/skills/add-item-workflow/SKILL.md
- .github/skills/add-machine-workflow/SKILL.md
