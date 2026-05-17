---
name: add-fluid-workflow
description: "Use when adding a new fluid, FluidType, source/flowing fluid pair, liquid block, bucket item, fluid textures, or fluid-related datagen to this NeoForge 1.21.1 mod. Covers the current project state, recommended registry structure, client extension setup, and validation steps."
---

# 液体追加ワークフロー

この skill は、このワークスペースに新しい液体を追加するときに使います。

対象は次のような作業です。

- 新しい FluidType の追加
- source fluid / flowing fluid の追加
- 液体ブロックの追加
- バケツアイテムの追加
- 液体用テクスチャと tint の追加
- fluid bucket の item model や表示名の追加
- 機械レシピやタンクで使うための液体登録

このプロジェクトには、流体を扱う機械や GUI に加えて、
mod 独自の液体を登録するための基本土台も入っています。
そのため、この skill では「既存の fluid 基盤へ新しい液体を追加する手順」を中心に扱います。

## 1. 先に確認すること

液体追加の前に、必ず次を確認してください。

1. 追加したいのはワールドに置ける通常液体か
2. バケツで汲める必要があるか
3. 半透明描画が必要か
4. 独自の still / flowing texture が必要か
5. tint 色だけ変えれば十分か
6. タンクや機械レシピで使うだけで、自然生成やワールド配置は不要か
7. 複数の液体を今後増やす予定があるか
8. fluid tag や recipe 連携も今回必要か

ここが曖昧なまま始めると、FluidType、fluid、block、bucket、client 表示の責務が混ざりやすくなります。

## 2. 現在のプロジェクト仕様

このプロジェクトでは、流体を使う側の実装に加えて、
fluid 登録用の基本クラスもすでにあります。

### 既存の流体利用コード

- タンク本体: src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneTankBlockEntity.java
- 反応槽レシピ: src/main/java/com/yukke9265/cobblestone_xx_compressed/recipe/CobblestoneReactionChamberRecipe.java
- タンク画面: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneTankScreen.java
- 反応槽画面: src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneReactionChamberScreen.java

特に画面側では IClientFluidTypeExtensions から tint 色を読んでいます。
そのため、独自液体を追加するときは client 側の fluid extension 登録を省略しないでください。
省略すると、色やテクスチャが期待どおりに出ない原因になります。

### 既存の登録入口

- mod 全体登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- client 側登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- datagen 入口: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- item model datagen: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- 言語 datagen: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java

### 既存の fluid 基盤

- FluidType 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluidTypes.java
- fluid / liquid block / bucket 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluids.java

現在は molten_compressed_cobblestone が実装済みで、
この 1 件が今後の液体追加の基準実装になります。
新しい液体を追加するときは、まずこの実装の並び方と責務分担を崩さないことを優先してください。

### この skill で推奨する構成

この repo では、液体ブロックとバケツが fluid と強く結び付くため、
次のように fluid 用の登録クラスへまとめる構成を推奨します。

- FluidType 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluidTypes.java
- fluid / liquid block / bucket 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModFluids.java

ModBlocks と ModItems に無理に分散すると、
source fluid、flowing fluid、liquid block、bucket の相互参照が追いにくくなります。
Java 初心者が追いやすくするためにも、最初は fluid 関連を近い場所へまとめる方が安全です。

## 3. 液体追加で必要になる要素

### A. 最低限の登録要素

新しい液体 1 種類につき、基本的に次が必要です。

1. FluidType
2. source fluid
3. flowing fluid
4. LiquidBlock
5. BucketItem
6. still texture
7. flowing texture
8. client 側の IClientFluidTypeExtensions 登録
9. 表示名
10. bucket item model

### B. 必要に応じて追加する要素

必要な場合だけ追加します。

1. overlay texture
2. 半透明 render layer 設定
3. fluid tag
4. recipe で使う fluid ingredient / FluidStack JSON
5. クリエイティブタブ表示
6. ワールド生成や自然配置

### C. このプロジェクト特有の注意点

このプロジェクトでは、タンク画面や反応槽画面で displayedFluid.getHoverName() を表示します。
そのため、FluidType 側の descriptionId を正しく設定しないと、
ゲーム内表示が分かりにくくなる可能性があります。

## 4. 実装の基準パターン

NeoForge 1.21.1 では、次の組み合わせが基本です。

1. DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID)
2. DeferredRegister.create(Registries.FLUID, MODID)
3. BaseFlowingFluid.Properties
4. new BaseFlowingFluid.Source(...)
5. new BaseFlowingFluid.Flowing(...)
6. new LiquidBlock(...)
7. new BucketItem(...)
8. RegisterClientExtensionsEvent で IClientFluidTypeExtensions を登録

つまり、block や item のように 1 個だけ登録して終わりではありません。
source と flowing が対になっている点が、通常アイテムや通常ブロックと違うところです。

## 5. 推奨する実装順

この順で進めてください。

1. 仕様整理
   液体名、色、温度、密度、粘性、半透明かどうか、バケツ可否を決める。
2. FluidType 登録
   既存の ModFluidTypes に追加し、共通の FluidType.Properties 補助メソッドへ寄せられるものは寄せる。
3. fluid 本体登録
   既存の ModFluids に追加し、source / flowing / liquid block / bucket をまとめて登録する。
4. mod への接続
   CobblestonexXCompressed で ModFluidTypes と ModFluids の register を呼ぶ。
5. client 表示登録
   CobblestonexXCompressedClient へ RegisterClientExtensionsEvent を追加し、still / flowing / overlay texture と tint を登録する。
6. render layer
   半透明が必要なら、FMLClientSetupEvent で liquid block に translucent を設定する。
7. クリエイティブタブ
   bucket を表示したいなら CobblestonexXCompressed の EXAMPLE_TAB へ追加する。
8. datagen
   ModItemModelProvider に bucket model 生成を追加し、ModEnglishLanguageProvider に fluid 名と bucket 名を追加する。
9. テクスチャ
   still / flowing PNG を assets 配下へ置く。
10. 確認
   compileJava、runData、runClient の順で確認する。

## 6. 変更対象になりやすいファイル

### 新規作成候補

- 必要なら fluid 専用の補助クラス
- 必要なら fluid tag provider

### 既存更新候補

- src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java

### リソース追加候補

- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_still.png
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_flow.png
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_still.png.mcmeta
- src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_flow.png.mcmeta
- 必要なら src/main/resources/assets/cobblestonexxcompressed/textures/block/fluid/<fluid_name>_overlay.png

## 7. 実装の分け方

### A. ModFluidTypes.java で持つもの

ここでは次を持たせると追いやすいです。

1. FLUID_TYPES の DeferredRegister
2. 共通の FluidType.Properties を組み立てる補助メソッド
3. 個別の FluidType 登録
4. descriptionId の設定
5. tint の基準色定数

sound、temperature、density、viscosity などの性質は、まず FluidType 側へ寄せてください。

### B. ModFluids.java で持つもの

ここでは次をまとめます。

1. FLUIDS の DeferredRegister
2. 必要なら fluid 用 block register
3. 必要なら fluid 用 item register
4. BaseFlowingFluid.Properties の生成
5. source fluid 登録
6. flowing fluid 登録
7. LiquidBlock 登録
8. BucketItem 登録

fluid block は通常ブロックとは違い、loot table や BlockItem を基本的に持ちません。
そのため、通常ブロックと同じ追加手順だと思わないでください。

### C. CobblestonexXCompressed.java で行うこと

ここでは次を行います。

1. ModFluidTypes の register 呼び出し
2. ModFluids の register 呼び出し
3. bucket を独自タブへ表示するなら EXAMPLE_TAB へ追加

既存の item / block / menu と同じように、modEventBus への登録接続を忘れないでください。

### D. CobblestonexXCompressedClient.java で行うこと

ここでは次を行います。

1. RegisterClientExtensionsEvent を購読する
2. event.registerFluidType(...) で IClientFluidTypeExtensions を登録する
3. getStillTexture() と getFlowingTexture() を返す
4. 必要なら getOverlayTexture() を返す
5. tint を返す
6. 半透明表示が必要なら FMLClientSetupEvent 内で ItemBlockRenderTypes.setRenderLayer(..., RenderType.translucent()) を設定する

この登録をしないと、既存のタンク GUI や反応槽 GUI で色表示が合わないことがあります。

## 8. datagen の進め方

このプロジェクトは item model と lang を datagen へ寄せる方針です。
液体追加時も、まず provider 側を更新してください。

### A. item model

bucket item model は、用途に応じて構成を分けてください。

1. ワールド上の fluid と同じ見た目で十分な場合
2. bucket の中身だけ tier ごとに別見た目へしたい場合

この repo の molten_compressed_cobblestone では 2 の要件があり、
現在の基準実装では layered item model を使っています。

方針は次のとおりです。

1. ModItemModelProvider に bucket 専用の補助メソッドを追加する
2. parent は minecraft:item/generated を使う
3. layer0 は minecraft:item/bucket を使う
4. layer1 は item/bucket/<bucket_item_name>_fluid の overlay PNG を使う

この方式なら、bucket の fluid 部分だけ tier ごとに個別 PNG へ差し替えられます。

補足:

- fluid_container は FluidType 単位の見た目切り替えには使えます
- ただし bucket の中身だけを tier ごとに別 texture にしたい用途には向きません
- bucket 専用差分が必要な場合は layered item model を優先してください

### B. 言語ファイル

最低限、次の表示名を追加してください。

1. fluid 名
2. bucket 名

fluid 名は、FluidType の descriptionId と一致するキーを使ってください。
これを合わせておくと、タンク GUI の hover 表示も自然になります。

### C. fluid tag

今回の repo には fluid tag provider はまだありません。
fluid tag が必要になったときだけ、新しい provider を追加してください。

例えば次のケースで必要になります。

1. 複数液体をタグでひとまとめに扱いたい
2. レシピ側で特定液体群を許可したい
3. 他 mod 連携で共通 fluid tag を参照したい

不要なら、最初の 1 種類の液体追加では無理に増やさないでください。

## 9. テクスチャと見た目の注意

- still と flowing の 2 枚を基本にすること
- animated fluid texture を使うなら、PNG だけでなく .png.mcmeta も必ず対応させること
- 透過ピクセルを使うなら translucent 設定を忘れないこと
- tint を texture に焼き込むか、client extension で色を足すかを最初に決めること
- 元テクスチャの色が強いと tint は思ったより効かないことがあるため、必要なら明るめの無彩色寄り texture を別で用意すること
- overlay が不要なら無理に持たないこと
- バケツ見た目を tier ごとに強く分けたい場合は、item/bucket 配下に fluid overlay PNG を置くこと
- bucket 用だけ別見た目が必要ない場合だけ fluid_container 系の構成を検討すること

### 今回の実装で確定した見た目の知見

molten_compressed_cobblestone の実装では、最初に vanilla lava texture をそのまま使ったため、
tint を変えても「少し暗くなっただけ」に見えやすい状態になりました。

そのため、この repo では次の順で切り分けるのを推奨します。

1. まず IClientFluidTypeExtensions の tint 値が正しく返っているか確認する
2. 色変化が弱いなら、texture 側の元色が強すぎないか確認する
3. animated texture を差し替えたら .png.mcmeta も忘れずに置く

特に .png.mcmeta が無いと、縦長の fluid texture が 1 枚絵として解釈され、
「細かい texture がたくさん並んで見える」状態になります。

また、bucket 見た目については次の点も確定しています。

1. fluid_container は bucket 専用 texture を FluidStack ごとに切り替える用途には使えない
2. bucket ごとの差分を確実に出したいなら、item model を layered へ切り替える方が安全
3. overlay PNG を複数 tier で揃えるときは、1 枚の基準画像を recolor script で生成するとズレを減らしやすい
4. 基準画像だけ手作業の色味が残ると、その tier だけ色設計がずれるため、基準 tier 自身も同じ生成ルールで作り直した方がよい

## 10. 実装時の具体チェックリスト

### 追加前

- source / flowing の 2 つが必要なことを理解しているか
- FluidType と fluid 本体を分けて考えているか
- bucket と liquid block の登録先を決めたか
- 半透明が必要か決めたか
- fluid 名の翻訳キーをどうするか決めたか

### 追加後

- FluidType が登録されているか
- source fluid が登録されているか
- flowing fluid が登録されているか
- LiquidBlock が登録されているか
- BucketItem が登録されているか
- modEventBus へ正しく register されているか
- client extension が登録されているか
- tank 画面で fluid 名と色が正しく見えるか
- reaction chamber の fluid 表示が壊れていないか
- custom fluid texture を使うなら still / flow の .png.mcmeta も置かれているか
- tint が弱いとき、texture の元色が強すぎるだけではないか
- bucket item model が生成されているか
- runData 後に generated resources が更新されているか
- 紫黒テクスチャや白いバケツ表示になっていないか
- バケツで汲めるか、設置できるか

## 11. 実装上の注意

- まずは既存の molten_compressed_cobblestone を基準に、1 種類ずつ追加すること
- ModBlocks や ModItems に無理に散らさず、fluid 関連を近い場所へ寄せること
- クライアント専用処理を common 側へ混ぜないこと
- block の表示名と fluid の表示名がずれないようにすること
- fluid block に通常ブロックと同じ loot / BlockItem を前提にしないこと
- datagen に寄せられるものは provider 側へ寄せること
- fluid tag provider は必要になってから追加すること
- tint の見た目調整は、色定数だけでなく texture 側も含めて考えること
- animated texture を追加したら、サイズだけでなく .png.mcmeta の有無まで確認すること

## 12. この skill を使ったときの進め方

この skill を呼び出したら、次の順で進めてください。

1. ユーザーに、追加したい液体の用途を簡潔に確認する
2. その液体がワールド設置用か、機械内利用中心かを分ける
3. 必要な要素を A と B の分類で整理する
4. 新規作成ファイルと既存更新ファイルを列挙する
5. まず既存の ModFluidTypes と ModFluids に追加する
6. その直後に compileJava で確認する
7. client 側を足したら runClient で見た目確認する
8. datagen を更新したら runData を実行する

## 13. このワークスペースで優先する確認コマンド

- .\\gradlew.bat compileJava
- .\\gradlew.bat runData
- .\\gradlew.bat processResources
- .\\gradlew.bat runClient

液体追加では、compileJava だけでは texture、bucket model、lang、render layer の不備を見落としやすいです。
そのため、見た目が絡む場合は runClient まで進める前提で考えてください。

## 14. 既存コードから学ぶポイント

### タンクと GUI

CobblestoneTankBlockEntity は FluidStack と IFluidHandler を使っており、
CobblestoneTankScreen は IClientFluidTypeExtensions から tint を読んで描画します。
つまり、液体を追加したあとに最初に確認すべきゲーム内 UI は tank 系です。

### レシピ側

CobblestoneReactionChamberRecipe は FluidStack をそのままレシピ条件に使っています。
追加した液体を機械レシピへ使いたい場合は、まず fluid 登録を完成させ、そのあとでレシピ JSON 側へ広げると切り分けしやすいです。

### datagen 側

この repo は item model と lang を provider 側へ寄せる流れです。
液体バケツも同じく provider で扱う方が、main/resources と generated/resources の責務が混ざりません。

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
