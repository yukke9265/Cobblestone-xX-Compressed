---
name: add-machine-workflow
description: "Use when adding a new machine block, block entity, machine recipe, GUI, automation, or JEI integration to this NeoForge 1.21.1 mod. Covers required files, current project conventions, implementation order, and validation steps for machine features like Cobblestone Furnace, compressor, or crusher."
---

# 機械追加ワークフロー

この skill は、このワークスペースに新しい機械を追加するときに使います。

対象は次のような作業です。

- 機械ブロックの追加
- BlockEntity を持つ装置の追加
- Menu / Screen を持つ GUI 機械の追加
- 独自 RecipeType / RecipeSerializer を使う加工機の追加
- ホッパーやアイテムパイプ対応の追加
- JEI で機械レシピを表示する連携の追加

このプロジェクトでは、Cobblestone Furnace が機械実装の基準例です。
まずは既存実装を確認し、そこから不足分だけを増やしてください。

## 1. 先に確認すること

機械追加の前に、必ず次を確認してください。

1. この機械は GUI が必要か
2. この機械は内部インベントリを持つか
3. この機械は独自レシピを使うか
4. この機械は外部自動化に対応するか
5. この機械は JEI 表示が必要か
6. 処理進行、燃焼時間、稼働状態など、同期が必要な整数値を持つか
7. 既存の Cobblestone Furnace と同じ 1 入力 1 出力構成か、それとも複数スロット構成か

ここが曖昧なまま作業を始めると、後で Menu、Screen、BlockEntity、Recipe の責務がずれやすくなります。

## 2. 現在のプロジェクト仕様

このプロジェクトでは、機械追加時に主に次の構成を使います。

### 基本登録

- ブロック登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlocks.java
- ブロック対応アイテム登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- BlockEntityType 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModBlockEntities.java
- MenuType 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModMenuType.java
- RecipeType 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeTypes.java
- RecipeSerializer 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeSerializers.java
- mod 全体への登録接続: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Screen 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java

### 実装の基準例

- Block: src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneCrusherBlock.java
- BlockEntity: src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java
- Menu: src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneCrusherMenu.java
- Screen: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneCrusherScreen.java
- Recipe: src/main/java/com/yukke9265/cobblestone_xx_compressed/recipe/CobblestoneCrusherRecipe.java
- JEI plugin: src/main/java/com/yukke9265/cobblestone_xx_compressed/jei/ModJeiPlugin.java
- JEI category: src/main/java/com/yukke9265/cobblestone_xx_compressed/jei/category/CobblestoneCrusherRecipeCategory.java

### JEI の共通化ルール

このプロジェクトでは、JEI API を BaseMenu や BaseScreen に直接持ち込みません。
代わりに次の JEI 非依存定義を返す構成にしています。

- Menu 側: JeiRecipeTransferDefinition
- Screen 側: JeiClickableAreaDefinition

つまり、新しい機械を増やすときは、必要なら Menu と Screen で JEI 用定義を返し、JEI 実登録は ModJeiPlugin 側で行います。

## 3. 機械の種類ごとの必須要件

### A. BlockEntity だけ持つ機械

最低限必要です。

1. 専用 Block クラス
2. 専用 BlockEntity クラス
3. ModBlocks への登録
4. ModItems への BlockItem 登録
5. ModBlockEntities への BlockEntityType 登録
6. CobblestonexXCompressed で ModBlockEntities の register が呼ばれていること
7. 保存対象があるなら BlockEntity の saveAdditional / loadAdditional
8. 必要なら tick 処理

### B. GUI を持つ機械

A に加えて必要です。

1. Menu クラス
2. ModMenuType への MenuType 登録
3. CobblestonexXCompressed で ModMenuType の register が呼ばれていること
4. Screen クラス
5. CobblestonexXCompressedClient で Screen 登録
6. Block の右クリックから ServerPlayer#openMenu(...) へ到達すること
7. BlockEntity が MenuProvider を実装するか、同等の MenuProvider を返せること
8. ContainerData で同期したい整数値の設計
9. stillValid(...) の距離・設置ブロック確認
10. quickMoveStack(...) の領域設計

### C. 独自レシピを使う加工機

B に加えて必要です。

1. Recipe クラス
2. RecipeType 登録
3. RecipeSerializer 登録
4. CobblestonexXCompressed で ModRecipeTypes / ModRecipeSerializers の register が呼ばれていること
5. BlockEntity 側で RecipeManager.getRecipeFor(...) を使った検索
6. 入力条件、出力条件、進行条件の整理
7. レシピ JSON の追加
8. 必要なら src/generated/resources を含む datagen 対応

### fluid-only / fluid-main レシピの注意

- 液体だけを材料にする機械でも、RecipeInput は Minecraft 側の共通判定を通る
- そのため、独自 RecipeInput で size() を 0 のままにしないこと
- item を使わない機械でも、入力の論理個数に合わせて size() を返すこと
- さらに isEmpty() を明示的に override し、「液体入力が空かどうか」で判定すること
- これを怠ると、JEI にはレシピが出ていても RecipeManager.getRecipeFor(...) の入口で空入力扱いされ、実機が開始しないことがある
- 例:
   - 液体 1 入力機なら size() は 1、isEmpty() は fluidInput.isEmpty()
   - 液体 2 入力機なら size() は 2、isEmpty() は firstFluidInput.isEmpty() && secondFluidInput.isEmpty()

### D. 自動化対応する機械

C に加えて必要になることがあります。

1. IItemHandler か capability 公開用のハンドラ
2. 面ごとの入出力ルール
3. side が null の場合の方針
4. CobblestonexXCompressed の registerCapabilities(...) での登録
5. GUI スロットルールと外部入出力ルールが矛盾しないこと

### 自動化ハンドラの注意

- IN_OUT 用の複合 IItemHandler は、できるだけ「実スロット数」をそのまま公開すること
- 挿入禁止・搬出禁止の制御は getSlots() を減らすのではなく、insertItem(...) と extractItem(...) で行うこと
- 既存機械と違う仮想スロット構成にすると、ホッパーや外部パイプ側で正常に扱えないことがある
- まず Melter、Dissolution Chamber、Reaction Chamber の automationAccessHandler の形に合わせること

### E. JEI 対応する機械

C に加えて必要です。

1. ModJeiIds にカテゴリ ID を追加
2. JEI category クラスの追加
3. ModJeiPlugin に MachineJeiDefinition を追加
4. ModJeiPlugin の registerRecipes(...) で対象 RecipeType の一覧を JEI に渡すこと
5. 触媒として機械ブロックを登録すること
6. Screen 側のクリック領域定義
7. Menu 側のレシピ転送定義
8. JEI plugin の static 初期化で DeferredHolder.get() を早すぎるタイミングで呼ばないこと

### JEI レイアウトの注意

- JEI category の slot 座標は、Screen と別に直書きせず MachineGuiLayouts を参照すること
- 背景テクスチャの切り出しは、同系統の既存機械と同じ基準で合わせること
- PoweredMachine 系の 1 行レイアウト機械は、まず既存の Melter / Crusher / Furnace 系と同じ BACKGROUND_U = 3、BACKGROUND_V = 6 を基準に確認すること
- JEI だけ独自の切り出し値を使うと、実 GUI と JEI の見た目や slot 位置がずれやすい

## 4. 実装順

この順で進めてください。

1. 仕様整理
   この機械の入力数、出力数、処理時間、GUI 有無、JEI 有無、自動化有無を決める。
2. ブロック土台
   Block と BlockItem を追加し、ModBlocks / ModItems に登録する。
3. BlockEntity 土台
   BlockEntity と ModBlockEntities 登録を追加する。
4. 右クリック入口
   Block 側で GUI を開くか、状態切り替えだけにするかを実装する。
5. GUI 土台
   必要なら Menu / MenuType / Screen / Screen 登録を追加する。
6. データ同期
   ContainerData と stillValid(...)、必要なら quickMoveStack(...) を実装する。
7. レシピ基盤
   Recipe、RecipeType、RecipeSerializer、BlockEntity の検索処理を追加する。
8. 自動化
   必要なら capability と面別入出力を追加する。
9. JEI
   必要なら category、plugin 定義、クリック領域、転送定義を追加する。
10. リソース
   blockstates、models、textures、lang、必要なら recipe JSON を追加する。
11. 確認
   compileJava、processResources、runClient、必要なら runData で確認する。

## 5. 実装時の具体チェックリスト

作業前と作業後に、次を順に確認してください。

### 追加前

- 既存機械に近い構成があるか
- その機械は Cobblestone Furnace と同じ責務分けで作れるか
- 新規クラスが本当に必要か、それとも BaseMenu / BaseScreen の共通化で足りるか
- GUI なしで先に動作確認した方が安全ではないか

### 追加後

- ブロックが登録されているか
- BlockItem が登録されているか
- BlockEntityType が登録されているか
- Block が正しい BlockEntity を返すか
- tick が対象 BlockEntityType にだけ流れるか
- GUI が開くか
- Menu と Screen が正しく結び付いているか
- 保存 / 読み込みで中身が消えないか
- レシピが読み込まれるか
- fluid-only レシピなら RecipeInput の size() / isEmpty() が液体入力前提になっているか
- 出力先に積めないときに誤作動しないか
- JEI カテゴリが表示されるか
- JEI の背景切り出しと slot 位置が Screen と一致しているか
- JEI plugin が起動時に落ちていないか
- ホッパーやパイプの入出力方向が想定どおりか

## 6. 実装上の注意

- まず既存コードの命名と構成に合わせること
- Java 初心者が追いやすいように、複雑な抽象化を増やしすぎないこと
- クライアント専用処理とサーバー処理を混ぜないこと
- JEI API を共通基底クラスへ直接混ぜないこと
- DeferredHolder.get() を static 初期化で早取りしないこと
- まず最小構成で通してから、JEI や自動化を足すこと

### CP 上限と long 運用の注意

- 現在の CP 機械は、基礎上限が 4096 CP の実装を基準にしている
- energized cube は通常版が 4 倍、tier 版の 1 段目が 16 倍で、その後は tier が 1 段上がるたびに 4 倍ずつ増える
- そのため multiplier は通常版が 4、copper が 16、iron が 64 ... obsidian が 67,108,864 になる
- 現在の enum には tier energized cube が 12 段あるため、最大倍率は obsidian の 67,108,864 倍である
- 基礎上限 4096 CP に最大倍率を掛けると、現在の理論上限は 274,877,906,944 CP になる
- この値は int の上限 2,147,483,647 を大きく超えるため、CP の内部保持値と最大値は int ではなく long を使うこと
- ContainerData は int しか同期できないため、long をそのまま流さず lower / upper の 2 つの int に分けて同期すること
- このプロジェクトでは LongDataHelper.lowerInt(...)、LongDataHelper.upperInt(...)、LongDataHelper.toLong(...) を使って分解と復元を行う
- BlockEntity 側では storedCobblestonePower、MAX_COBBLESTONE_POWER、変換値、加算減算、NBT 保存読込を long にそろえること
- Menu 側では getLongFromData(...) で ContainerData の 2 要素から long を復元すること
- Screen 側ではゲージ計算時に long を受け取り、比率計算だけ double にして描画幅へ変換すること
- 新しい tier を増やす場合は、実装前に base CP × 最大 energized cube 倍率 を計算し、long の範囲内に収まるかを先に確認すること

## 7. この skill を使ったときの進め方

この skill を呼び出したら、次の順で進めてください。

1. ユーザーに、追加したい機械の仕様を簡潔に確認する
2. 既存実装から最も近い機械を 1 つ選ぶ
3. 追加に必要な要件を A から E の分類で整理する
4. 変更対象ファイルを列挙する
5. できるだけ小さい差分で 1 段階ずつ実装する
6. 変更後は compileJava を最初の検証に使う
7. GUI や JEI が絡む場合は runClient まで必要か判断する

## 8. このワークスペースで優先する確認コマンド

- .\\gradlew.bat compileJava
- .\\gradlew.bat processResources
- .\\gradlew.bat runClient
- .\\gradlew.bat runData

runData は JSON 生成が必要なときだけ使ってください。
JEI 連携や GUI の確認は compileJava だけでは不十分な場合があるため、必要なら runClient まで進めてください。

## 9. 関連ドキュメント

必要に応じて次も参照してください。

- docs/machine-block-guide.md
- docs/machine-recipe-guide.md
- docs/gui-guide.md
- docs/inventory-gui-guide.md
- docs/jei-machine-recipe-guide.md
- docs/client-server-basics.md
- docs/development-checks.md
