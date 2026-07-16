# 機械共通化 Phase 0 / Phase 1 実施計画

このフォルダは、機械共通化を小さく安全に進めるための作業メモを置く場所です。

最初の対象は、すでに共通基盤を利用している次の 3 機械です。

1. Cobblestone Crusher
2. Cobblestone Extreme Compressor
3. Cobblestone Powered Furnace

この 3 機械は [PoweredMachineBlockEntityBase](../../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java) を継承しているため、レシピ処理や GUI の大きな変更をせず、automation handler の重複から安全に整理できます。

関連する全体方針は、[機械共通化 長期ロードマップ](../machine-commonization-roadmap.md) を参照してください。

Phase 0 で確認した現行仕様の差分と、今後それを統一する方針は、[Phase 0 調査結果と挙動統一方針](./phase-0-findings.md) を参照してください。

Phase 2 で Centrifuge を複数出力 powered machine として移行する設計は、[Phase 2: Centrifuge の複数出力共通化計画](./phase-2-centrifuge-plan.md) を参照してください。

---

## Phase 0: 現在の仕様を固定する

### 目的

共通化前の動作を記録し、移行後に何が変わったかを確認できるようにします。

この作業を先に行わないと、コードの見た目は整理できても、次のような意図しない変更を見逃す可能性があります。

1. 出力を取り出せる面が変わる
2. Shift+クリック時の投入先が変わる
3. power 不足時に progress が失われる
4. 出力詰まり時の progress の扱いが変わる
5. 既存ワールドの inventory や automation 設定を読めなくなる

### 対象機械

最初は次の 5 機械を比較します。

| 機械 | この段階で調べる理由 |
| --- | --- |
| Crusher | 標準的な 1 入力・1 出力 powered machine の基準になる |
| Extreme Compressor | upgrade と追加の機械固有データを持つ |
| Powered Furnace | 炉系レシピだが powered machine 基盤を利用している |
| Centrifuge | 複数出力を持つ次の移行候補になる |
| Mixer | 複数入力を持つ次の移行候補になる |

### 作成する仕様比較表

`docs/commonization` 配下に、機械ごとの仕様を記録する表を作成します。1 機械につき、最低限次を記録します。

| 確認項目 | 記録する内容 |
| --- | --- |
| BlockEntity | クラス名、継承先、tick の入口 |
| inventory | スロット番号、用途、受け入れ可能な item |
| recipe | recipe type、入力、出力、消費数、CP/t、総 CP |
| progress | 開始条件、維持条件、reset 条件 |
| automation | `INPUT`、`COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT` ごとに公開するスロット |
| auto export | 対象の出力スロット、搬出する順番 |
| 永続化 | NBT キー、inventory の保存方法 |
| 同期 | `ContainerData` の index と意味 |
| GUI / JEI | Menu、Screen、JEI category、recipe transfer の入力スロット |
| 手動確認 | GUI、Shift+クリック、automation、保存読み込みの結果 |

### 進め方

1. Crusher の仕様表を作る
2. Extreme Compressor と Powered Furnace に同じ表を作る
3. 3 機械で共通な automation handler の形を比較する
4. Centrifuge と Mixer の表を作り、複数入出力で追加になる差分を確認する
5. 表に不足がなければ Phase 1 へ進む

### Phase 0 の完了条件

次をすべて満たしたら完了です。

1. 対象 5 機械の仕様表がある
2. 各機械のスロット番号と automation 公開ルールが分かる
3. progress を reset する条件が分かる
4. 既存の `ContainerData` index と NBT キーが記録されている
5. Phase 1 で共通化する部分と、固有処理として残す部分を説明できる

---

## Phase 1: 標準 powered machine の automation handler を整理する

### 目的

Crusher、Extreme Compressor、Powered Furnace にある `IItemHandler` の重複を減らします。

このフェーズでは、レシピ判定、CP 計算、progress、Menu、Screen、JEI、NBT キーを変えません。目的は「同じ handler を作る記述」を減らし、各機械の入出力ルールを読みやすくすることです。

### 共通化する対象

次のような処理を対象にします。

1. 指定スロットへ挿入だけを許可する handler
2. 指定スロットから取り出しだけを許可する handler
3. CP 用スロットだけを受け入れる handler
4. `IN_OUT` 用に、各スロットの既存ルールへ委譲する handler
5. `AutomationMode` から input / CP input / output / in-out handler を選ぶ処理

最後の選択処理はすでに [PoweredMachineBlockEntityBase](../../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java) の `getConfiguredAutomationItemHandler()` にあります。そのため、最初は handler の生成側だけを小さく整理します。

### 共通化しない対象

次は機械固有の仕様なので、helper へ隠しません。

1. スロット番号そのもの
2. 入力として受け入れる item の判定
3. 出力 item を取り出せる条件
4. `IN_OUT` で許可する slot の組み合わせ
5. item を完成させる処理
6. recipe の検索と一致判定

### 実装の形

新しい helper は、複雑な継承や汎用的すぎるジェネリクスを使いません。

呼び出し側から、次を明示して渡せる形にします。

1. 対象の `ItemStackHandler`
2. 公開するスロット番号
3. 挿入を許可するか
4. 取り出しを許可するか
5. item を許可するか判定する処理

これにより、BlockEntity 側では「どの handler を何のために公開するか」が読み取れる状態を保ちます。

### 実施順

#### Step 1: Crusher の handler を確認する

1. input
2. cobblestone input
3. output
4. in/out 全体アクセス

この 4 つについて、現在の insert / extract / item 判定を仕様表と照合します。

#### Step 2: helper を追加する

1. helper は新規クラスとして追加する
2. 既存機械の動作はまだ変更しない
3. helper 単体で、insert / extract のルールを読めるようにする

#### Step 3: Crusher だけを移行する

1. Crusher の匿名 `IItemHandler` を helper に置き換える
2. `getAutomationItemHandler()` が返す handler を変更しない
3. `compileJava` を実行する
4. ゲーム内で hopper または item pipe を使い、全 automation mode を確認する

#### Step 4: Extreme Compressor へ展開する

1. Crusher と同じ部分だけ helper を利用する
2. 必要な固有ルールは BlockEntity に残す
3. GUI、upgrade、automation、保存を確認する

#### Step 5: Powered Furnace へ展開する

1. 炉固有の入力・出力ルールを維持する
2. Shift+クリックと JEI transfer を確認する
3. power 不足、出力詰まり、停止時の progress を確認する

### Phase 1 の確認項目

#### コンパイル

1. `gradlew.bat compileJava` が通る

#### automation

各対象機械で次を確認します。

1. `INPUT` 面へ正しい入力 item を挿入できる
2. `INPUT` 面から出力 item を抜き取れない
3. `COBBLESTONE_INPUT` 面へ CP item を挿入できる
4. `OUTPUT` 面から完成品を取り出せる
5. `OUTPUT` 面へ item を挿入できない
6. `IN_OUT` 面が移行前と同じスロットを公開する
7. 面の mode を変更した直後、隣接パイプが新しい capability を取得する
8. auto export のオン・オフで搬出挙動が正しく変わる

#### 通常の機械動作

1. GUI を開閉できる
2. Shift+クリックの投入先が変わらない
3. recipe を処理できる
4. CP 不足時は progress を維持する
5. 入力不一致、出力詰まり、停止時は必要に応じて progress を reset する
6. ワールド再起動後に inventory と automation 設定が残る

### Phase 1 の完了条件

次をすべて満たしたら完了です。

1. Crusher、Extreme Compressor、Powered Furnace の handler 重複が減っている
2. 各 BlockEntity から入出力ルールを読み取れる
3. 既存の automation 挙動、NBT、`ContainerData`、GUI、JEI が変わっていない
4. `compileJava` と手動確認が完了している
5. Centrifuge / Mixer の移行時に不足する helper が明確になっている

---

## Phase 1 完了後の次の作業

次は Centrifuge を 1 台だけ対象にして、複数出力の auto export と output handler を安全に共通化できるか確認します。その結果を使って Mixer の複数入力へ進みます。
