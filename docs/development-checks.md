# 開発時の確認手順

この文書では、このプロジェクトで変更後に**何をどう確認するとよいか**を整理します。

プロジェクトの instructions と `build.gradle` の内容から見ると、基本の確認候補は次の 3 つです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`
3. `gradlew.bat runData`

ただし、毎回すべてを実行すると時間がかかるため、変更内容に応じて使い分けるのが現実的です。

## 1. 一番基本になる確認

### `gradlew.bat build`

用途:

- Java のコンパイル確認
- resources を含めたビルド確認
- まず大きな崩れがないかを見る

向いている場面:

- Java コードを変更したとき
- JSON や lang を追加したあと
- 一通りまとめて確認したいとき

## 2. ゲーム内表示を確認したいとき

### `gradlew.bat runClient`

用途:

- クリエイティブタブに出るか
- モデルやテクスチャが正しいか
- レシピが作業台で機能するか
- ブロック配置やドロップが正しいか

向いている場面:

- アイテム追加後
- ブロック追加後
- レシピ追加後
- 見た目や表示名を確認したいとき

## 3. data generator を使うとき

### `gradlew.bat runData`

用途:

- data generator の出力更新
- `src/generated/resources` の再生成

このプロジェクトは現時点では datagen をまだ本格利用していませんが、`build.gradle` に data run の設定自体はあります。

そのため、将来的に次のようなものを自動生成し始めたら、このコマンドが重要になります。

- レシピ
- loot table
- blockstates
- models
- tags

## 4. 変更内容ごとのおすすめ確認

### アイテムだけ追加したとき

おすすめ順:

1. `gradlew.bat build`
2. `gradlew.bat runClient`

### レシピだけ追加したとき

おすすめ順:

1. `gradlew.bat build`
2. `gradlew.bat runClient`

JSON の文法ミスや ID 間違いは、実際にゲーム内で作業台を開くと気付きやすいです。

### ブロックを追加したとき

おすすめ順:

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ブロックは、配置、破壊、インベントリ表示まで確認した方が安全です。

### datagen 関連を追加したとき

おすすめ順:

1. `gradlew.bat runData`
2. `gradlew.bat build`
3. `gradlew.bat runClient`

## 5. よくある不具合と見る場所

### アイテム名が翻訳キーのまま出る

見る場所:

- `lang/en_us.json`
- 翻訳キーの prefix が `item.` または `block.` と合っているか

### 紫黒テクスチャになる

見る場所:

- `models/item/`
- `models/block/`
- `textures/item/`
- `textures/block/`

モデル内の参照名と PNG の名前が一致しているかを見ます。

### レシピが出ない

見る場所:

- `data/<modid>/recipe/`
- 出力先アイテムの登録名
- 材料の ID

### ブロックを壊しても落ちない

見る場所:

- `data/<modid>/loot_table/blocks/`

## 6. 作業のたびに全部確認しないための考え方

確認コストを下げたいときは、次のように考えると進めやすいです。

- Java を触ったらまず `build`
- 見た目やレシピを触ったら `runClient`
- datagen を触ったら `runData`

これで、必要以上に重い確認を毎回走らせずに済みます。

## 7. 最低限の確認テンプレート

変更後に迷ったら、まずは次の順で十分です。

1. `gradlew.bat build`
2. 必要なら `gradlew.bat runClient`

datagen を導入したあとだけ、間に `gradlew.bat runData` を入れる形にすると整理しやすいです。
