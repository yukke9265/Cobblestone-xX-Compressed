# クラフトレシピの登録手順

この文書では、このプロジェクトで作業台用クラフトレシピを登録する方法を、現在の datagen 構成に合わせて説明します。

今のプロジェクトでは、クラフトレシピも JSON を手書きするのではなく、Java の datagen から src/generated/resources へ出すのが標準です。

## 1. 登録の入口

クラフトレシピの datagen 入口も、機械レシピと同じく次の流れです。

1. CobblestonexXCompressed で ModDatagen::gatherData を登録する
2. ModDatagen.gatherData で ModRecipeProvider を server provider として登録する
3. ModRecipeProvider.buildRecipes で ShapedRecipeBuilder と ShapelessRecipeBuilder を使って出力する

つまり、クラフトレシピを増やす時も、まず触る場所は ModRecipeProvider です。

## 2. 現在のクラフトレシピ登録場所

通常クラフトは次のメソッド群で登録しています。

- buildCompressedCobblestoneRecipes
- buildCompressedCobblestoneSingularityRecipes
- buildCobblestoneGeneratorRecipes
- buildCobblestoneMachineCasingRecipes
- buildCobblestoneTankRecipes
- buildGemRecipes
- buildCobblestoneRodRecipes
- buildCobblestoneMotorRecipes
- buildCobblestoneAccelerationChipRecipes
- buildCobblestoneEnergizedCubeRecipes

この並びは ModRecipeProvider.buildRecipes の前半にまとまっています。

## 3. 何を使って出しているか

通常クラフトでは、Minecraft 標準の builder を使っています。

1. 配置付きレシピは ShapedRecipeBuilder
2. 並び順を問わないレシピは ShapelessRecipeBuilder

この 2 つは save(...) を呼ぶと、recipe JSON だけでなく解除条件用の advancement JSON も一緒に生成します。

## 4. 追加手順

既存カテゴリへクラフトレシピを 1 件追加する時は、次の流れが簡単です。

1. ModRecipeProvider の対応する buildXxxRecipes を開く
2. ShapedRecipeBuilder か ShapelessRecipeBuilder を追加する
3. unlockedBy(...) で解除条件を付ける
4. save(output, modRecipeId("...")) で保存する
5. gradlew.bat runData を実行する

## 5. shaped の書き方

配置を固定したいときは ShapedRecipeBuilder を使います。

考え方は次の通りです。

1. pattern で見た目を決める
2. define で文字に素材を割り当てる
3. unlockedBy で解除条件を決める
4. save でレシピ ID を決める

このプロジェクトの compressed cobblestone では、2x2 の丸石を pattern に並べています。

## 6. shapeless の書き方

順番を問わない変換では ShapelessRecipeBuilder を使います。

たとえば圧縮解除のように、1 個を前段階へ戻す処理は shapeless の方が読みやすいです。

このプロジェクトでは、compressed_cobblestone_to_cobblestone や tier の戻しレシピがその例です。

## 7. 生成先

runData 実行後の出力先は次です。

- src/generated/resources/data/cobblestonexxcompressed/recipe/
- src/generated/resources/data/cobblestonexxcompressed/advancement/recipes/

recipe 配下に本体の JSON が出て、advancement/recipes 配下に解除条件が出ます。

## 8. レシピ ID の決まり方

save(output, modRecipeId("compressed_cobblestone")) のように渡した文字列が、そのまま modid 付きレシピ ID の末尾になります。

つまり、compressed_cobblestone を渡すと次になります。

1. レシピ ID: cobblestonexxcompressed:compressed_cobblestone
2. 出力ファイル: data/cobblestonexxcompressed/recipe/compressed_cobblestone.json

## 9. 追加先の選び方

どの buildXxxRecipes に書くべきか迷った時は、レシピの目的で分けると追いやすいです。

1. 圧縮ブロック関係なら buildCompressedCobblestoneRecipes
2. gem 関係なら buildGemRecipes
3. machine casing 関係なら buildCobblestoneMachineCasingRecipes
4. generator 関係なら buildCobblestoneGeneratorRecipes

新しい系統が増えて既存メソッドに入れにくい時は、buildXxxRecipes を 1 つ増やして buildRecipes から呼ぶ形で分ける方が後で追いやすいです。

## 10. 今のプロジェクトでの標準形

初心者向けに見るなら、まず次の 3 パターンを見ると分かりやすいです。

1. 2x2 shaped: compressed cobblestone
2. shapeless 戻し: compressed cobblestone から cobblestone へ戻すレシピ
3. 3x3 shaped: machine casing や tank のレシピ

この 3 つが読めれば、大半の通常クラフトは追えます。

## 11. 動かない時の確認ポイント

次を順に確認すると切り分けしやすいです。

1. ModRecipeProvider.buildRecipes からその buildXxxRecipes が呼ばれているか
2. save(...) のレシピ ID が重複していないか
3. unlockedBy(...) の条件に使っている ItemLike が正しいか
4. runData 後に generated/resources 側へ JSON が出ているか
5. 出力アイテムや入力素材が登録済みか

## 12. 手書き JSON を使うかどうか

Minecraft 自体は手書き JSON でも動きますが、このプロジェクトでは datagen に寄せた方が管理しやすいです。

理由は次の通りです。

1. tier 違いを Java 側でまとめて増やしやすい
2. generated/resources を見れば出力結果を確認しやすい
3. 命名や解除条件の揺れを減らしやすい

新しいクラフトレシピを追加する時は、まず ModRecipeProvider へ書く方針で進めてください。
