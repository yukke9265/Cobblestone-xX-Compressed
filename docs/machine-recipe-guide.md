# 機能ブロック用レシピ作成手順

この文書では、このプロジェクトで**機能ブロックが使う独自レシピ**をどのように作るかをまとめます。

ここでいうレシピは、作業台で使う `minecraft:crafting_shaped` や `minecraft:crafting_shapeless` ではありません。
たとえば次のような、**機能ブロックの内部処理で使うレシピ**を指します。

- 独自かまどで入力アイテムを焼く
- 圧縮機で素材を加工する
- 粉砕機でアイテムを別のアイテムへ変える
- 特定のブロックや内部スロットを条件にして出力を作る

## 1. まず知っておくべきこと

機能ブロック用レシピは、**JSON を 1 つ置くだけでは動きません**。

作業台レシピは Minecraft 側がすでに処理方法を知っていますが、独自機械のレシピは Mod 側で「どう読み込み、どう照合し、どう結果を返すか」を作る必要があります。

NeoForge 1.21 系では、最低でも次の 3 つが必要です。

1. `Recipe`
2. `RecipeType`
3. `RecipeSerializer`

さらに、機械の入力形式によっては次も必要です。

- `RecipeInput`
- BlockEntity 側のレシピ検索処理
- 必要ならクライアント同期用の仕組み

## 2. このプロジェクトの今の状態

現在のこのプロジェクトには、機械っぽい構成として次があります。

- `CobblestoneFurnaceBlockEntity`
- `CobblestoneFurnaceMenu`
- GUI 関連クラス

ただし、今の `CobblestoneFurnaceBlockEntity` は**独自 RecipeType を使って入力アイテムを処理する形にはまだなっていません**。

つまり、機能ブロック用レシピを本格的に使いたい場合は、次の 2 段階で考えると分かりやすいです。

1. レシピ基盤を Java 側に作る
2. その基盤で読む JSON を `data/<modid>/recipe/` に追加する

## 3. 作業台レシピとの違い

通常のクラフトレシピでは、Minecraft がすでに次を持っています。

- どの `type` をどう読むか
- どの入力形式で照合するか
- どの画面で使うか

一方で機能ブロック用レシピでは、Mod 側で次を決めます。

- 何を入力とするか
- 何を出力とするか
- 処理時間を持つか
- 経験値を持つか
- どのブロックがそのレシピを使うか

そのため、**「まず Java でルールを作り、そのあと JSON で中身を増やす」** という順になります。

## 4. 最小構成はどう考えるか

初心者向けには、まず次のような最小構成が分かりやすいです。

- 入力 1 スロット
- 出力 1 スロット
- 燃料なし、または別で管理
- 入力アイテム 1 個に対して結果 1 種類

この形なら、入力は `SingleRecipeInput` を使えることが多いです。

つまり、最初から独自 `RecipeInput` を作らなくても、
**「機械の入力スロット 0 に入っている ItemStack を 1 つの入力として扱う」** という構成で始めやすいです。

## 5. 先に決めるべきレシピ仕様

コードを書く前に、まずレシピ 1 件が何を持つかを決めます。

たとえば独自かまどなら、次のような項目がよくあります。

- 入力材料
- 出力アイテム
- 処理時間
- 経験値

例としては、次のような設計です。

- `ingredient`: 受け付ける入力
- `result`: 出力アイテム
- `processing_time`: 何 tick で完了するか
- `experience`: 完了時に得る経験値

この「JSON に書く項目」が、そのまま `Recipe` クラスのフィールド候補になります。

## 6. 必要になる主なクラス

機能ブロック用レシピを追加するときは、だいたい次のようなクラス構成になります。

### Recipe 本体

たとえば `CobblestoneFurnaceRecipe` のようなクラスです。

ここでは主に次を持ちます。

- 入力条件
- 出力結果
- 処理時間などの追加情報
- `matches(...)`
- `assemble(...)`
- `getType()`
- `getSerializer()`

`matches(...)` は「この入力でこのレシピが使えるか」を判定する場所です。

`assemble(...)` は「このレシピで何を返すか」を作る場所です。

### RecipeType 登録

たとえば `cobblestone_furnace` という独自タイプを登録します。

これは「このレシピはどの種類として検索するか」を表します。

### RecipeSerializer 登録

これは JSON とネットワーク同期の変換を担当します。

1.21.1 系では、`MapCodec` と `StreamCodec` を使う形になります。

### 必要なら RecipeInput

入力が単純に ItemStack 1 個なら `SingleRecipeInput` を使いやすいです。

逆に次のような場合は独自 `RecipeInput` を考えます。

- 複数スロットを同時に見たい
- ブロックの状態も入力に含めたい
- アイテム以外の情報も条件にしたい

## 7. 実装の流れ

初心者向けには、次の順で実装すると整理しやすいです。

1. JSON の項目を決める
2. `Recipe` クラスを作る
3. `RecipeType` を登録する
4. `RecipeSerializer` を登録する
5. BlockEntity からレシピ検索する
6. 最後に JSON を追加する

この順にすると、「読み込み基盤がないのに JSON だけ増やす」状態を避けられます。

## 8. BlockEntity 側でレシピを使う流れ

機能ブロックでは、実際にレシピを使うのは BlockEntity 側です。

流れはおおむね次のようになります。

1. 入力スロットの中身を取り出す
2. `RecipeInput` を作る
3. `RecipeManager` で一致するレシピを探す
4. 見つかったら進行を進める
5. 完了時に入力を減らし、出力へ結果を入れる

重要なのは、**レシピ判定はサーバー側で行う**ことです。

クライアント側で処理結果を決めると、マルチプレイで同期ずれを起こしやすくなります。

## 9. レシピ検索のイメージ

機械の入力が 1 スロットだけなら、考え方は次のようになります。

```java
ItemStack inputStack = this.itemStackHandler.getStackInSlot(0);
SingleRecipeInput input = new SingleRecipeInput(inputStack);

Optional<RecipeHolder<CobblestoneFurnaceRecipe>> recipeHolder =
    level.recipeAccess().getRecipeFor(ModRecipeTypes.COBBLESTONE_FURNACE.get(), input, level);
```

このあとで、レシピが見つかっているかを確認し、
見つかったなら `assemble(...)` の結果を出力スロットへ入れます。

## 10. JSON を置く場所

機能ブロック用レシピの JSON も、通常レシピと同じく次へ置きます。

`src/main/resources/data/cobblestonexxcompressed/recipe/`

たとえば `cobblestone_furnace/iron_from_cobblestone.json` のように、
サブフォルダを切って整理すると後で見返しやすいです。

例:

- `src/main/resources/data/cobblestonexxcompressed/recipe/cobblestone_furnace/stone_to_smooth_stone.json`
- `src/main/resources/data/cobblestonexxcompressed/recipe/compressor/cobblestone_to_dense_cobblestone.json`

## 11. JSON 例

たとえば独自かまど用に、入力 1 個を別アイテムへ変えるレシピなら、次のような形にできます。

```json
{
  "type": "cobblestonexxcompressed:cobblestone_furnace",
  "ingredient": {
    "item": "minecraft:cobblestone"
  },
  "result": {
    "id": "minecraft:stone",
    "count": 1
  },
  "processing_time": 200,
  "experience": 0.1
}
```

ここで大事なのは `type` です。

この値は、**自分で登録した RecipeSerializer の ID** と一致している必要があります。

## 12. `Recipe` クラスでやること

`Recipe` クラスは、JSON を Java の処理に変換する中心です。

最低限、次を考えます。

- 入力条件をどう持つか
- 出力をどう返すか
- そのレシピの種類は何か
- どの serializer で読まれるか

入力 1 個の単純な機械なら、`Ingredient` と `ItemStack` を持つ形が分かりやすいです。

### `matches(...)`

ここでは、入力スロットの中身がそのレシピに合うかを判定します。

最初は次くらいの単純な考え方で十分です。

- スロット 0 のアイテムが `ingredient` に一致するか

### `assemble(...)`

ここでは、完成品の `ItemStack` を返します。

返すときは、元の結果をそのまま返すのではなく、**必要なら copy を返す**意識を持った方が安全です。

## 13. serializer でやること

1.21.1 系では、serializer は「JSON から読む方法」と「同期用に送る方法」を持ちます。

つまり次の 2 つです。

- `MapCodec`
- `StreamCodec`

ここで JSON の各項目と、`Recipe` クラスの各フィールドを結びます。

初心者のうちはここが一番重く見えやすいですが、考え方は単純です。

1. JSON の各項目を列挙する
2. それをコンストラクタ引数へ流す
3. 同じ内容をネットワーク送信用にも定義する

## 14. RecipeInput はいつ独自実装が必要か

次のような機械なら、独自 `RecipeInput` を作らなくても進めやすいです。

- 入力スロット 1 個だけを見る
- アイテムだけを条件にする

この場合は `SingleRecipeInput` で十分なことが多いです。

一方で次のような場合は、独自 `RecipeInput` を考えた方が自然です。

- 入力スロットが 2 個以上ある
- アイテムの並びを区別したい
- ブロック状態や液体量も入力条件にしたい

つまり、**機械の入力の形が既存クラスで表せないとき**に独自 `RecipeInput` を作ります。

## 15. クライアント同期について

重要なのは、レシピの本判定はサーバー側で行うことです。

ただし、次のような機能を入れたい場合はクライアント側も意識します。

- GUI で有効レシピ候補を見せたい
- レシピ本のような表示をしたい
- クライアント側で入力候補チェックをしたい

NeoForge では、必要に応じて特定の `RecipeType` をクライアントへ同期する仕組みもあります。

ただし最初の 1 台目の機械では、**まずサーバー側だけで正しく加工できる状態**を目標にした方が安全です。

## 16. Data Generator は必須ではない

このプロジェクトでは、通常レシピと同じく、最初は JSON を直接追加する形で十分です。

機能ブロック用レシピも、まずは次で問題ありません。

- Java 側で独自レシピ基盤を作る
- JSON を `src/main/resources/data/<modid>/recipe/` に直接置く

レシピ数が増えてから、必要なら datagen 用の `RecipeBuilder` を追加する流れで十分です。

## 17. このプロジェクトでのおすすめ構成

この Mod の今の構成に合わせるなら、たとえば次のような分け方が追いやすいです。

- `recipe/CobblestoneFurnaceRecipe.java`
- `recipe/ModRecipeSerializers.java`
- `recipe/ModRecipeTypes.java`
- `blockentity/CobblestoneFurnaceBlockEntity.java` に検索処理を追加

もし 1 つの登録クラスにまとめたいなら、初心者のうちは `ModRecipes` のように 1 か所へ寄せても構いません。

大事なのは、次の役割が混ざりすぎないことです。

- レシピ定義
- レジストリ登録
- 機械の進行処理

## 18. 実装順のおすすめ

この順が一番分かりやすいです。

1. まず入力 1 個、出力 1 個の単純な仕様にする
2. `Recipe` / `RecipeType` / `RecipeSerializer` を作る
3. BlockEntity から `getRecipeFor(...)` で検索する
4. 1 件だけ JSON を作る
5. 処理時間や出力スロット判定を足す
6. 最後に GUI 表示やクライアント同期を考える

## 19. よくあるミス

### JSON だけ作って、Java 側の基盤を作っていない

独自機械レシピではこれでは動きません。

### `type` の ID が登録名と一致していない

serializer の登録 ID と JSON の `type` がずれていると読み込まれません。

### クライアント側で処理を完結させようとする

結果の確定はサーバー側で行う方が安全です。

### 出力スロットの空き確認をしていない

レシピが一致しても、出力先へ置けないなら加工を進めるべきではありません。

### `SingleRecipeInput` で十分なのに、最初から複雑な独自入力を作る

最初の 1 台目は、できるだけ単純な入力構造で始めた方が理解しやすいです。

## 20. 最初の 1 台目としておすすめの形

初心者向けには、まず次のような機械が向いています。

- 入力 1
- 出力 1
- レシピ条件は `Ingredient` だけ
- 結果は `ItemStack`
- 処理時間だけ追加

この形なら、独自レシピの仕組みを理解しながら、BlockEntity 側の進行処理にも無理なくつなげられます。

## 21. 関連ドキュメント

- BlockEntity の基本は [blockentity-setup.md](./blockentity-setup.md)
- GUI の基本は [gui-guide.md](./gui-guide.md)
- インベントリ付き GUI は [inventory-gui-guide.md](./inventory-gui-guide.md)
- 機械ブロック全体の考え方は [machine-block-guide.md](./machine-block-guide.md)
- 通常のクラフトレシピは [recipe-setup.md](./recipe-setup.md)

## 22. まとめ

機能ブロック用レシピは、次の順で考えると整理しやすいです。

1. その機械が何を入力に取り、何を出力するか決める
2. `Recipe` / `RecipeType` / `RecipeSerializer` を作る
3. BlockEntity 側で `RecipeManager` から検索する
4. 最後に JSON を追加して内容を増やす

つまり、**機能ブロック用レシピは「JSON を置く作業」より先に、「その JSON を理解できる Java 側の仕組みを作る作業」が必要**です。

この順を守ると、独自かまどや圧縮機のような機械を後から増やしても整理しやすくなります。
