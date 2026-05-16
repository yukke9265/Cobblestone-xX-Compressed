---
name: add-block-workflow
description: "Use when adding a new block, BlockItem, blockstate/model/loot resources, or stateful non-machine block to this NeoForge 1.21.1 mod. Covers required files, current ModBlocks and datagen conventions, implementation order, and validation steps for simple blocks, stateful blocks, and tiered block sets."
---

# ブロック追加ワークフロー

この skill は、このワークスペースに新しいブロックを追加するときに使います。

対象は次のような作業です。

- 単純な立方体ブロックの追加
- BlockItem を持つ通常ブロックの追加
- 向きや点灯状態を持つ状態付きブロックの追加
- tier 違い、色違い、系列ブロックの追加
- datagen で管理している blockstate、block model、item model、lang、loot、tag の更新

機械ブロックの追加は add-machine-workflow を優先してください。
この skill は、BlockEntity や Menu を前提にしないブロック追加を中心に扱います。

## 1. 先に確認すること

ブロック追加の前に、必ず次を確認してください。

1. 追加したいのは単純なフルキューブか、状態付きか、機械系か
2. BlockEntity が必要か
3. BlockItem が必要か
4. 独自クリエイティブタブに表示したいか
5. バニラタブにも表示したいか
6. 適正ツールや loot、タグ設定が必要か
7. 単発追加か、tier や色違いをまとめて追加するか
8. blockstate / model / lang / loot / tag を datagen へ追加するか

ここが曖昧なまま進めると、ModBlocks、ModItems、datagen provider、独自 Block クラスの責務がぶれやすくなります。

## 2. 現在のプロジェクト仕様

このプロジェクトでは、ブロック追加時に主に次の構成を使います。

### 基本登録

- ブロック登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java
- BlockItem 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- 独自タブ / バニラタブ表示: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- datagen 入口: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- blockstate / block model / item model: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModBlockStateProvider.java
- 英語表示名: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- ブロック loot: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/loot/ModBlockLootTableProvider.java
- ブロック tag: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/tag/ModBlockTagProvider.java

### 実装の基準例

- 単純ブロック群: ModBlocks.COMPRESSED_COBBLESTONE と ModBlocks.COMPRESSED_STONE
- tier ブロック群: ModBlocks.TierCompressedCobblestone と ModBlocks.TierCompressedStone
- 状態付き機械寄りブロックの例: block/CobblestoneFurnaceBlock.java

### リソース管理の方針

このプロジェクトでは、表示名、blockstate、block model、item model、loot、tag の多くを datagen 側へ寄せています。
そのため、追加時は次を優先してください。

1. まず provider を更新する
2. その後 runData で generated resources を更新する
3. src/generated/resources に出た内容を確認する

main/resources に同じ役割の JSON を重複して置くと、どこを直すべきか分かりにくくなります。

## 3. ブロックの種類ごとの必須要件

### A. 単純なフルキューブブロック 1 個

最低限必要です。

1. ModBlocks への登録
2. ModItems への BlockItem 登録
3. 必要なら CobblestonexXCompressed の独自タブ表示追加
4. 必要なら CobblestonexXCompressed のバニラタブ表示追加
5. ModEnglishLanguageProvider への表示名追加
6. ModBlockStateProvider への blockstate / model / item model 生成追加
7. 対応するブロックテクスチャ PNG の追加
8. ModBlockLootTableProvider への loot 追加
9. ModBlockTagProvider への適正ツール tag 追加

### B. 状態付きブロック

A に加えて必要です。

1. 独自 Block クラス
2. BlockStateProperties や独自 Property の設計
3. registerDefaultState(...) の初期状態設定
4. createBlockStateDefinition(...) の実装
5. 必要なら getStateForPlacement(...) の実装
6. ModBlocks で registerSimpleBlock ではなく new 独自Block(...) を使うこと
7. ModBlockStateProvider か専用 blockstates 設計で状態ごとの見た目を管理すること

### C. BlockItem だけ追加が必要なケース

A の一部です。

1. 元になる Block が先に登録済みであること
2. ModItems で ITEMS.registerSimpleBlockItem(...) か既存補助メソッドを使うこと
3. 表示先タブが BUILDING_BLOCKS か独自タブかを決めること
4. Block 側の表示名や item model との整合を取ること

### D. tier / 色違い / 系列ブロック

A または B に加えて必要です。

1. enum や定義一覧へまとめられるかを先に確認する
2. 登録名、英語表示名、参照先 Block の 3 点を 1 か所に集約する
3. 共通 BlockBehaviour.Properties を補助メソッドへ寄せる
4. ループで登録できるなら、1 件ずつ直書きしない
5. 言語、model、loot、tag も同じ定義一覧で回せるようにする
6. 色違いブロックのテクスチャは、ベース画像からスクリプトで色差分を生成する方針を優先する
7. 数が少ない場合だけは、無理に抽象化しすぎない

### E. 機械寄りブロック

この skill では入口だけ確認します。

1. BlockEntity が必要なら add-machine-workflow へ切り替える
2. Menu / Screen / capability / RecipeType が必要なら add-machine-workflow へ切り替える
3. ただし、向きや ON/OFF 状態だけの段階ならこの skill で進めてよい

## 4. 実装順

この順で進めてください。

1. 仕様整理
   単純ブロックか、状態付きか、tier 系か、機械系かを決める。
2. 登録方式の判断
   単発追加にするか、enum や定義一覧に寄せるかを決める。
3. Java 登録
   ModBlocks に追加し、必要なら独自 Block クラスや共通 Properties メソッドも追加する。
4. BlockItem 登録
   ModItems に追加する。
5. タブ表示
   独自タブとバニラタブのどちらへ出すかを CobblestonexXCompressed で調整する。
6. 表示名
   ModEnglishLanguageProvider に追加する。
7. blockstate / model
   ModBlockStateProvider に追加する。
8. テクスチャ
   色違い追加時はベース画像と色一覧を決め、PNG はスクリプト生成を優先して追加する。
9. loot / tag
   ModBlockLootTableProvider と ModBlockTagProvider に追加する。
10. datagen
   runData で generated resources を更新する。
11. 確認
   compileJava、processResources、必要なら runClient で確認する。

## 5. 実装時の具体チェックリスト

### 追加前

- 既存のどのブロックに近いか
- 単純ブロックで済むか、それとも独自 Block クラスが必要か
- 複数追加なら enum に寄せた方がよいか
- BlockEntity を使うべきケースではないか
- datagen 側にまとめられるか

### 追加後

- ModBlocks に登録されているか
- ModItems に BlockItem が登録されているか
- CobblestonexXCompressed で表示先タブに追加されているか
- ModEnglishLanguageProvider に表示名があるか
- ModBlockStateProvider に blockstate / model 生成があるか
- ブロックテクスチャ PNG が正しい場所にあるか
- ModBlockLootTableProvider に loot があるか
- ModBlockTagProvider に適正ツール tag があるか
- runData 後に src/generated/resources へ期待どおりの JSON が出ているか
- processResources 後に bin/main へ同期されるか
- ゲーム内で紫黒テクスチャや透明ブロック化が起きていないか
- 壊したときに想定どおりドロップするか

## 6. 実装上の注意

- まず既存コードの命名と構成に合わせること
- Java 初心者が追いやすいように、複雑な抽象化を増やしすぎないこと
- 単発追加なら無理に enum 化しないこと
- ただし、tier や色違いを 5 個以上増やすなら定義一覧へ寄せることを優先する
- 色違いテクスチャは、可能な限りベース画像からスクリプトで生成すること
- 表示名、blockstate、model、loot、tag はまず datagen provider 側に寄せること
- main/resources と generated/resources に同じ役割の JSON を重複配置しないこと
- 状態付きブロックは registerSimpleBlock で無理に済ませないこと
- BlockEntity が必要になった時点で機械用 workflow へ切り替えること

## 7. 既存コードから学ぶポイント

### 単純ブロック

Compressed Cobblestone と Compressed Stone は、共通の BlockBehaviour.Properties を補助メソッドへまとめています。
同系統ブロックを増やすときは、この方式を優先してください。

### tier ブロック

TierCompressedCobblestone と TierCompressedStone は、登録名と英語名を enum に集約しています。
複数系列を追加する場合は、この形が基準です。
色違いテクスチャが増える場合は、ベース画像から色差分を出すスクリプト運用も合わせて前提にしてください。

### datagen

blockstate / block model / item model は ModBlockStateProvider、表示名は ModEnglishLanguageProvider、loot は ModBlockLootTableProvider、tag は ModBlockTagProvider に追加するのが現行方針です。
新規 JSON を直接書くのは、既存 provider で扱えない特別な事情があるときだけにしてください。

### 状態付きブロック

向きや lit 状態を持つ場合は、独自 Block クラスを作って Java 側で状態を定義し、その上で blockstates 側の見た目切り替えを設計します。

## 8. この skill を使ったときの進め方

この skill を呼び出したら、次の順で進めてください。

1. ユーザーに、追加したいブロックの種類と用途を簡潔に確認する
2. 既存実装から最も近いブロックを 1 つ選ぶ
3. A から E の分類で必要要件を整理する
4. 変更対象ファイルを列挙する
5. 小さい差分で 1 段階ずつ実装する
6. Java 変更後は compileJava を先に確認する
7. datagen が絡む場合は runData を実行する
8. 見た目確認が必要なら runClient まで進める

## 9. このワークスペースで優先する確認コマンド

- .\\gradlew.bat compileJava
- .\\gradlew.bat runData
- .\\gradlew.bat processResources
- .\\gradlew.bat runClient

blockstate、model、lang、loot、tag を provider へ追加した場合は、compileJava だけでは generated resources が更新されません。
そのため runData を優先してください。

## 10. 関連ドキュメント

必要に応じて次も参照してください。

- docs/block-addition.md
- docs/block-property-setup.md
- docs/block-state-guide.md
- docs/resource-layout.md
- docs/development-checks.md
- docs/client-server-basics.md
