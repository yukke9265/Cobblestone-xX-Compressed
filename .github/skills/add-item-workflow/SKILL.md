---
name: add-item-workflow
description: "Use when adding a new item, food item, BlockItem, item recipe, or tiered item set to this NeoForge 1.21.1 mod. Covers required files, current ModItems and datagen conventions, implementation order, and validation steps for normal items, foods, and variant items."
---

# アイテム追加ワークフロー

この skill は、このワークスペースに新しいアイテムを追加するときに使います。

対象は次のような作業です。

- 通常アイテムの追加
- 食料アイテムの追加
- BlockItem の追加
- クラフトレシピ付きアイテムの追加
- tier 違い、色違い、系列アイテムの追加
- datagen で管理している item model、lang、recipe の更新

このプロジェクトでは、単発の通常アイテムだけでなく、tier 系のまとまった追加もすでに使っています。
まずは既存の ModItems と datagen provider を確認し、既存パターンに乗せられるかを先に判断してください。

## 1. 先に確認すること

アイテム追加の前に、必ず次を確認してください。

1. 追加したいのは通常アイテムか、食料アイテムか、BlockItem か
2. 1 個だけ追加するのか、tier や色違いを複数まとめて追加するのか
3. 独自 Item クラスが必要か、それとも Item.Properties だけで足りるか
4. クリエイティブタブに表示したいか
5. バニラタブにも表示したいか
6. クラフトレシピが必要か
7. item model、表示名、レシピを datagen へ追加するか、例外的に手書きリソースにするか

ここが曖昧なまま進めると、ModItems、言語、model、recipe の追加場所がぶれやすくなります。

## 2. 現在のプロジェクト仕様

このプロジェクトでは、アイテム追加時に主に次の構成を使います。

### 基本登録

- アイテム登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- 独自タブ / バニラタブ表示: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- datagen 入口: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- 英語表示名: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- item model: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- 通常レシピ / 機械レシピ生成: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/recipe/ModRecipeProvider.java

### 実装の基準例

- 通常食料アイテム: ModItems.COBBLESTONE_BREAD
- tier 食料アイテム: ModItems.TierCobblestoneBread
- 通常素材アイテム: ModItems.COBBLESTONE_GEM
- tier 素材アイテム: ModItems.TierCobblestoneGem
- BlockItem 系: ModItems.COMPRESSED_COBBLESTONE_ITEM など

### リソース管理の方針

このプロジェクトでは、英語名、item model、通常レシピのかなりの部分を datagen 側へ寄せています。
そのため、追加時は次を優先してください。

1. まず provider を更新する
2. その後 runData で generated resources を更新する
3. src/generated/resources に出た内容を確認する

main/resources に同じ役割の JSON を重複して置くと、どちらが正なのか分かりにくくなります。

## 3. アイテムの種類ごとの必須要件

### A. 通常アイテム 1 個

最低限必要です。

1. ModItems への登録
2. 必要なら CobblestonexXCompressed の独自タブ表示追加
3. 必要なら CobblestonexXCompressed のバニラタブ表示追加
4. ModEnglishLanguageProvider への表示名追加
5. ModItemModelProvider への model 生成追加、または既存共通メソッドへの登録追加
6. 対応する PNG テクスチャの追加
7. 必要ならレシピ追加

### B. 食料アイテム

A に加えて必要です。

1. Item.Properties().food(...) の設計
2. nutrition の決定
3. saturationModifier の決定
4. 必要なら alwaysEdible、fast、effect、usingConvertsTo の設計
5. 同系統アイテムと共通設定にできるなら補助メソッドへ寄せること

### C. BlockItem

A とは少し起点が違います。

1. 元になる Block が先に登録済みであること
2. ModItems で ITEMS.registerSimpleBlockItem(...) か既存補助メソッドを使うこと
3. 表示名は基本的に Block 側を基準に管理すること
4. item model は block model 側との関係を確認すること
5. クリエイティブタブ表示先が BUILDING_BLOCKS か独自タブかを決めること

### D. tier / 色違い / 系列アイテム

A または B に加えて必要です。

1. enum や定義一覧へまとめられるかを先に確認する
2. 登録名、英語表示名、参照先 Item の 3 点を 1 か所に集約する
3. 共通 Properties を補助メソッドへ寄せる
4. ループで登録できるなら、1 件ずつ直書きしない
5. 言語、model、recipe も同じ定義一覧で回せるようにする
6. 色違いテクスチャは手作業で 1 枚ずつ複製するより、ベース画像からスクリプトで色差分を生成する方針を優先する
7. 数が少ない場合だけは、無理に抽象化しすぎない

### E. レシピ付きアイテム

A または B に加えて必要です。

1. 出力先アイテムが先に登録済みであること
2. ModRecipeProvider に builder 追加、または定義配列追加
3. shaped か shapeless かを決めること
4. unlock 条件を決めること
5. 必要なら圧縮解除など逆レシピも用意すること
6. runData で generated recipe を更新すること

## 4. 実装順

この順で進めてください。

1. 仕様整理
   通常アイテムか、食料か、BlockItem か、tier 系か、レシピ有無を決める。
2. 登録方式の判断
   単発追加にするか、enum や定義一覧に寄せるかを決める。
3. Java 登録
   ModItems に追加し、必要なら共通 Properties メソッドも追加する。
4. タブ表示
   独自タブとバニラタブのどちらへ出すかを CobblestonexXCompressed で調整する。
5. 表示名
   ModEnglishLanguageProvider に追加する。
6. item model
   ModItemModelProvider に追加する。
7. テクスチャ
   色違い追加時はベース画像と色一覧を決め、PNG はスクリプト生成を優先して追加する。
8. レシピ
   必要なら ModRecipeProvider に追加する。
9. datagen
   runData で generated resources を更新する。
10. 確認
   compileJava、processResources、必要なら runClient で確認する。

## 5. 実装時の具体チェックリスト

### 追加前

- 既存のどのアイテムに近いか
- Item.Properties だけで足りるか
- 複数追加なら enum に寄せた方がよいか
- 画像配置ルールは既存フォルダ構成に乗せられるか
- 表示名や model を datagen にまとめられるか

### 追加後

- ModItems に登録されているか
- CobblestonexXCompressed で表示先タブに追加されているか
- ModEnglishLanguageProvider に表示名があるか
- ModItemModelProvider に model 生成があるか
- テクスチャ PNG が正しい場所にあるか
- レシピが必要なら ModRecipeProvider に追加されているか
- runData 後に src/generated/resources へ期待どおりの JSON が出ているか
- processResources 後に bin/main へ同期されるか
- ゲーム内で紫黒テクスチャや翻訳キー生表示になっていないか

## 6. 実装上の注意

- まず既存コードの命名と構成に合わせること
- Java 初心者が追いやすいように、複雑な抽象化を増やしすぎないこと
- 単発追加なら無理に enum 化しないこと
- ただし、tier や色違いを 5 個以上増やすなら定義一覧へ寄せることを優先する
- 色違いテクスチャは、可能な限りベース画像からスクリプトで生成すること
- 英語表示名と item model は、まず datagen provider 側に寄せること
- main/resources と generated/resources に同じ役割の JSON を重複配置しないこと
- BlockItem は block 側の追加手順と整合させること

## 7. 既存コードから学ぶポイント

### 通常食料アイテム

Cobblestone Bread は、共通の FoodProperties を補助メソッドへまとめています。
同系統アイテムを増やすときは、この方式を優先してください。

### tier アイテム

TierCobblestoneBread と TierCobblestoneGem は、登録名と英語名を enum に集約しています。
複数系列を追加する場合は、この形が基準です。
色違いテクスチャが増える場合は、ベース画像から色差分を出すスクリプト運用も合わせて前提にしてください。

### BlockItem

圧縮ブロック系の item は、registerSimpleBlockItem を補助メソッド経由で登録しています。
対応する Block 側 enum がすでにあるなら、それに揃えて item 側も列挙してください。

### datagen

英語名は ModEnglishLanguageProvider、item model は ModItemModelProvider、レシピは ModRecipeProvider に追加するのが現行方針です。
新規 JSON を直接書くのは、既存 provider で扱えない特別な事情があるときだけにしてください。

## 8. この skill を使ったときの進め方

この skill を呼び出したら、次の順で進めてください。

1. ユーザーに、追加したいアイテムの種類と用途を簡潔に確認する
2. 既存実装から最も近いアイテムを 1 つ選ぶ
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

item model、lang、recipe を provider へ追加した場合は、compileJava だけでは generated resources が更新されません。
そのため runData を優先してください。

## 10. 関連ドキュメント

必要に応じて次も参照してください。

- docs/item-addition.md
- docs/food-item-setup.md
- docs/recipe-setup.md
- docs/variant-content-guide.md
- docs/resource-layout.md
- docs/development-checks.md
