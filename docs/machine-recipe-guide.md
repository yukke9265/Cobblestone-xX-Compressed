# 機械レシピの datagen 登録手順

この文書では、このプロジェクトで機械が読む独自レシピを datagen へ登録する方法を、現在の実装に合わせてまとめます。

ここでいう機械レシピは、作業台で使う minecraft:crafting_shaped や minecraft:crafting_shapeless ではありません。Cobblestone Furnace や Crusher など、BlockEntity が内部で検索して使う独自 RecipeType のことです。

## 1. 登録の入口

機械レシピの datagen 入口は次の 2 か所です。

- src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java

流れは次の順です。

1. CobblestonexXCompressed で ModDatagen::gatherData をイベントバスへ登録する
2. ModDatagen.gatherData でサーバー向け provider として ModRecipeProvider を登録する
3. ModRecipeProvider.buildRecipes で通常クラフトと機械レシピをまとめて出力する

つまり、機械レシピを datagen へ増やしたいときに直接触る場所は、ほぼ ModRecipeProvider です。

## 2. 実際に機械レシピを出している場所

機械レシピの定義と出力は次に集約されています。

- src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/recipe/ModRecipeProvider.java

このクラスでは、機械ごとに次の 2 段構えで整理されています。

1. ファイル上部に xxxRecipeDefinition の配列を置く
2. buildXxxRecipes と saveXxxRecipe で RecipeOutput へ流す

この形にしてあるので、既存機械へレシピを 1 件追加するだけなら、たいていは配列へ 1 行足せば終わります。

## 3. 現在 datagen 対応している機械

現在、ModRecipeProvider.buildRecipes から datagen されている機械レシピは次の 9 系統です。

1. Cobblestone Furnace
2. Cobblestone Extreme Compressor
3. Cobblestone Crusher
4. Cobblestone Mixer
5. Cobblestone Melter
6. Cobblestone Assembly Machine
7. Cobblestone Chemical Reactor
8. Cobblestone Dissolution Chamber
9. Cobblestone Crystallization Chamber

それぞれの JSON 出力先は data/cobblestonexxcompressed/recipe/<machine_name>/... になります。

例:

- data/cobblestonexxcompressed/recipe/cobblestone_furnace/cobblestone_to_stone.json
- data/cobblestonexxcompressed/recipe/cobblestone_assembly_machine/netherite_processor.json

## 4. RecipeType はあるが datagen 未接続の機械

次の機械は RecipeType と RecipeSerializer の登録はありますが、現時点では ModRecipeProvider.buildRecipes からは出力していません。

1. Cobblestone Centrifuge
2. Cobblestone Laser Drill
3. Cobblestone Reaction Chamber
4. Cobblestone Fluid Mixer

これらを datagen 対応したい場合は、次の 3 つを ModRecipeProvider へ追加します。

1. 定義配列
2. buildXxxRecipes メソッド
3. buildRecipes からの呼び出し

## 5. 既存機械へレシピを 1 件追加する手順

既存機械に新しいレシピを追加する時は、次の順が一番分かりやすいです。

1. ModRecipeProvider の対応する Definition 配列を探す
2. new XxxRecipeDefinition(...) を 1 件追加する
3. 必要なら ingredient や ItemStack や FluidStack の値を調整する
4. gradlew.bat runData を実行して generated/resources を更新する

たとえば Melter へ 1 件追加するなら、MELTER 用の配列へ new MelterRecipeDefinition(...) を足します。その後は既存の buildCobblestoneMelterRecipes が saveCobblestoneMelterRecipe を経由して JSON を出します。

## 6. 新しい機械系統を datagen 対応する手順

機械そのものが新規の場合は、配列を足すだけでは足りません。次の順で見ると追いやすいです。

1. Recipe クラスを作る
2. 必要なら専用 RecipeInput を作る
3. ModRecipeTypes へ RecipeType を登録する
4. ModRecipeSerializers へ RecipeSerializer を登録する
5. BlockEntity 側でその RecipeType を検索する
6. ModRecipeProvider に Definition 配列を作る
7. buildXxxRecipes と saveXxxRecipe を作る
8. buildRecipes から呼ぶ
9. 必要なら JEI 登録を追加する

このプロジェクトでは、機械レシピ JSON を手書きで main/resources に置くより、ModRecipeProvider から出す流れに寄せた方が追跡しやすいです。

## 7. saveXxxRecipe の役割

機械レシピは、通常クラフトのように ShapedRecipeBuilder や ShapelessRecipeBuilder を使わず、Recipe オブジェクトをそのまま RecipeOutput.accept(...) へ渡しています。

たとえば Furnace 系では、次の流れです。

1. saveCobblestoneFurnaceRecipe が CobblestoneFurnaceRecipe を new する
2. modRecipeId("cobblestone_furnace/...") を作る
3. output.accept(...) へ流す

この構成なので、JSON の shape を変えたい時は Recipe クラス側の CODEC と STREAM_CODEC を見ます。レシピ件数だけ増やしたい時は datagen 側の Definition 配列を見ます。

## 8. どのファイルを見ればよいか

機械レシピを追う時に見る場所は次の通りです。

- 定義追加: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/recipe/ModRecipeProvider.java
- type 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeTypes.java
- serializer 登録: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModRecipeSerializers.java
- 実際のレシピ構造: src/main/java/com/yukke9265/cobblestone_xx_compressed/recipe/
- 生成結果: src/generated/resources/data/cobblestonexxcompressed/recipe/

## 9. 出力先の見方

通常クラフトと違い、機械レシピは advancement を自動で付けていません。出力されるのは主に recipe 配下の JSON です。

一方、ShapedRecipeBuilder や ShapelessRecipeBuilder を使う通常クラフトは、recipe JSON に加えて advancement/recipes 側の解除条件 JSON も一緒に出ます。

この違いを知っておくと、generated/resources を見た時に「なぜ片方だけ advancement があるのか」で迷いにくくなります。

## 10. 確認方法

機械レシピの追加後は、まず次で datagen を更新します。

1. gradlew.bat runData

その後、次を確認します。

1. src/generated/resources/data/cobblestonexxcompressed/recipe/<machine_name>/ に JSON が出たか
2. JSON の type が想定どおりか
3. 対応する BlockEntity がその RecipeType を検索しているか
4. JEI 表示が必要な機械なら JEI 側にも並ぶか

## 11. 迷った時の最短ルート

初心者向けには、まず Cobblestone Furnace を見本にするのが一番追いやすいです。

理由は次の通りです。

1. 入力が単純
2. 出力が単純
3. datagen 側の save メソッドも短い
4. 他の機械へ横展開しやすい

複数入力や fluid 入力のある機械を触る前に、Furnace と Crusher と Melter の 3 つを順に見ると構造差が理解しやすくなります。
