# Data Generator 導入手順

この文書では、このプロジェクトに**data generator を導入するときの考え方と最初の進め方**を整理します。

今のプロジェクトでは、レシピや loot table などを手書き JSON で管理する前提が分かりやすいです。
ただし `build.gradle` にはすでに `runData` と `src/generated/resources` の設定があるため、土台はあります。

## 1. data generator を入れる理由

手書き JSON は少数なら分かりやすいですが、圧縮段階が増えると次の問題が出やすくなります。

- ファイル数が一気に増える
- 命名ずれを起こしやすい
- 同じような JSON を何十個も管理することになる

data generator を使うと、Java 側で規則を 1 か所にまとめて、そこから JSON を自動生成できます。

## 2. このプロジェクトですでにある前提

`build.gradle` には次の前提があります。

1. `src/generated/resources` が resources に含まれている
2. `gradlew.bat runData` 用の run 設定がある
3. 生成時に `src/main/resources` を既存リソースとして参照する設定がある

つまり、**datagen 用の Java クラスを追加すれば、実行基盤はすでにある**状態です。

## 3. どこから導入するとよいか

最初の導入対象としておすすめなのは次の順です。

1. recipe
2. loot table
3. blockstates / models
4. tags

理由は、圧縮系 Mod では recipe と loot table が増えやすく、重複パターンも多いからです。

## 4. 導入前に決めておくべきこと

datagen は「規則を自動化する」仕組みです。
そのため、先にルールが決まっていないと逆に書きにくくなります。

最低限、次の 3 つは先に決めておくと進めやすいです。

1. 登録名の規則
2. 圧縮段階の命名規則
3. どの種類のファイルを手書きのまま残すか

たとえば次のような規則です。

- `compressed_cobblestone`
- `double_compressed_cobblestone`
- `triple_compressed_cobblestone`

## 5. 最初の導入単位として recipe を勧める理由

recipe は、形がほぼ同じ JSON を大量に作りやすいです。

たとえば圧縮レシピと展開レシピは、段階が増えても次の規則がほぼ共通です。

- 入力が 9 個で 1 段上がる
- 出力が 9 個で 1 段下がる
- ファイル名規則もそろえやすい

このため、最初の datagen 対象として効果が出やすいです。

## 6. 導入時の基本構成

このプロジェクトに datagen を入れるなら、まずは専用の package を 1 つ作るのが分かりやすいです。

例:

- `src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/`

この中に、役割ごとにクラスを分けていく形が管理しやすいです。

例:

- recipe provider
- loot table provider
- blockstate / item model provider
- tag provider

## 7. 実際の導入手順のおすすめ

最初は次の順が安全です。

1. 既存の手書き JSON をそのまま残す
2. recipe だけ datagen 化する
3. `gradlew.bat runData` で生成結果を確認する
4. 生成された JSON が期待どおりなら、手書き recipe を整理する
5. 次に loot table や tags を移す

最初から全部を一気に移すより、1 種類ずつ切り替えた方が失敗箇所を追いやすいです。

## 8. `src/main/resources` と `src/generated/resources` の使い分け

このプロジェクトでは、次の使い分けにすると分かりやすいです。

- 手書きファイル: `src/main/resources`
- datagen 出力: `src/generated/resources`

この分け方にしておくと、あとから見たときに「どれが人間の編集対象か」を判断しやすいです。

## 9. 導入時の注意

### 生成物と手書きファイルを二重管理しない

同じ ID の recipe や loot table が手書きと生成の両方にあると、後で混乱しやすくなります。

### 先に命名規則を固める

命名規則が途中で変わると、provider 側も生成物側もまとめて直す必要が出ます。

### 最初は範囲を狭くする

recipe だけ、loot table だけ、のように狭い単位で導入する方が確認しやすいです。

## 10. 導入後の確認手順

おすすめは次の順です。

1. `gradlew.bat runData`
2. `gradlew.bat build`
3. `gradlew.bat runClient`

この順にすると、生成、ビルド、ゲーム内確認の順で問題を切り分けられます。

## 11. 今の段階での実践的な結論

このプロジェクトでは、まだレシピ数が少ないうちは手書き JSON のままで問題ありません。
ただし、多段圧縮を本格的に増やすなら、recipe と loot table から datagen を導入する価値が高いです。

つまり、今のおすすめは次の通りです。

1. 少数の機能追加は手書きで進める
2. 段階数や素材数が増え始めたら datagen へ移行する
3. 最初の移行対象は recipe にする
