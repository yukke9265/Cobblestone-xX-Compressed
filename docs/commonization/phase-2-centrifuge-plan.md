# Phase 2: Centrifuge の複数出力共通化計画

## 目的

Phase 2 では、Centrifuge を最初の複数出力 powered machine として
`PoweredMachineBlockEntityBase` へ移行します。

目的は、Centrifuge が独自に持つ CP 吸収、進捗計算、upgrade 効果、tick、NBT、
`ContainerData` の重複をなくすことです。複数出力、個別出力面、出力搬出順は
Centrifuge 固有の仕様として残します。

## 現状と統一する仕様

| 項目 | 現在の Centrifuge | Phase 2 後 |
| --- | --- | --- |
| 基底クラス | `BaseBlockEntity` | `PoweredMachineBlockEntityBase<CobblestoneCentrifugeRecipe>` |
| CP 吸収・容量・upgrade | 独自実装 | powered machine 基底へ統一 |
| progress 更新 | 独自 `tick()` | powered machine 基底へ統一 |
| CP 不足時 | progress を reset | progress を維持し、CP 回復後に再開 |
| 出力 1 / 出力 2 | 独自の順番付き搬出 | 指定順は維持し、共通 helper を使用 |
| `OUTPUT_1` / `OUTPUT_2` | 個別 handler を返す | 公開条件を維持 |
| NBT、スロット番号、recipe type、JEI | 現行 | 変更しない |

CP 不足時の進捗維持は、意図した仕様変更です。入力不一致、出力詰まり、停止、recipe 不在では従来どおり progress を reset します。

## 最小の基底クラス拡張

基底クラスに汎用的すぎる継承構造や record は追加しません。次の小さな hook と helper を追加します。

### 1. 出力搬出 hook

現在、基底クラスの `tick()` は単一出力用の `pushOutputToConfiguredSides()` を直接呼びます。これを、継承先が置き換えられる `pushOutputsToConfiguredSides()` に変更します。

- 既定実装は、現在の単一出力搬出をそのまま実行する。
- Centrifuge はこの hook を override し、出力スロット 1、出力スロット 2 の順に搬出する。
- 各スロットの搬出処理は、基底に追加する `pushOutputSlotToConfiguredSides(int outputSlotIndex, AutomationMode... allowedModes)` を利用する。

この形なら Crusher、Extreme Compressor、Powered Furnace は変更せず、Centrifuge だけが複数出力の順番を明示できます。

### 2. 複数スロット用 item handler helper

`AutomationItemHandlerHelper` に、次の 2 種類を追加します。

| helper | 用途 | Centrifuge での利用 |
| --- | --- | --- |
| 複数スロット抽出専用 handler | 指定順の複数 output を、1 つの `OUTPUT` capability として公開する | スロット 2、3 を公開 |
| 複数挿入・複数抽出を制限した全体公開 handler | `IN_OUT` で 0 へ挿入し、2・3 から抽出できるようにする | 全 6 スロットを閲覧可能にして入出力だけ制限 |

`INPUT`、`COBBLESTONE_INPUT`、`OUTPUT_1`、`OUTPUT_2` は既存の単一スロット helper を使用します。`OUTPUT` の複数出力と `IN_OUT` の複数抽出だけを追加対象にします。

## Centrifuge に残す処理

次の内容は機械固有のため、基底クラスへ移しません。

1. `COBBLESTONE_CENTRIFUGE` recipe の検索
2. 出力スロット 1 と 2 の両方へ結果を積めるかの判定
3. 確率付きの第 1 / 第 2 結果の作成
4. `OUTPUT_1` と `OUTPUT_2` の automation routing
5. 出力 1、出力 2 の順での auto export

## 実装順

1. `PoweredMachineBlockEntityBase` に出力搬出 hook とスロット単位の搬出 helper を追加する。既存の標準 powered machine の挙動を変えない。
2. `AutomationItemHandlerHelper` に複数出力・複数抽出向けの helper を追加する。
3. Centrifuge の automation handler を helper へ移行する。公開 mode と slot は変更しない。
4. Centrifuge を `PoweredMachineBlockEntityBase<CobblestoneCentrifugeRecipe>` へ移行し、独自の CP、progress、tick、保存、同期処理を削除する。
5. Centrifuge 固有の recipe 判定、2 出力生成、指定順搬出を hook と override で実装する。
6. `ContainerData` を `machineSpecificDataCount = 0` の powered machine 共通実装へ移す。index 0-14 は現行と同じ値・同じ意味を維持する。
7. コンパイル後、既存ワールドを含めて手動確認する。

## 互換性

維持するものは、BlockEntity ID、6 個の inventory slot 番号、NBT キー、recipe type、Menu / Screen / JEI category ID、`ContainerData` の index 0-14 です。

変更するものは、CP 不足時の progress の扱いだけです。Centrifuge は CP が必要量未満の間に進捗を失わず、CP が回復した tick から途中の進捗を再開します。

## 検証項目

### 基本処理

1. `gradlew.bat compileJava` が通る。
2. 既存ワールドの Centrifuge を読み込める。
3. CP を十分に供給すると recipe を完了し、確率付きの 2 出力を正しい slot に入れる。
4. CP を途中で不足させても progress を維持し、CP 回復後に再開する。
5. 入力を取り除く、出力を詰まらせる、停止する、recipe がなくなる場合は progress を reset する。

### automation と auto export

1. `INPUT` はスロット 0 への挿入だけを許可する。
2. `COBBLESTONE_INPUT` はスロット 1 への挿入だけを許可する。
3. `OUTPUT` はスロット 2、3 から抽出できる。
4. `OUTPUT_1` はスロット 2 だけ、`OUTPUT_2` はスロット 3 だけから抽出できる。
5. `IN_OUT` はスロット 0 へ挿入でき、スロット 2、3 から抽出できる。CP と upgrade slot は移動できない。
6. auto export はスロット 2 を処理してからスロット 3 を処理する。

### 保存と UI

1. GUI の progress、CP、automation mode、auto export の表示が正しい。
2. Shift+クリックの投入先が変わらない。
3. セーブ・再読込後も inventory、progress、CP、automation mode、auto export、停止状態を復元できる。
4. JEI の recipe、2 出力、確率表示、クリック領域が変わらない。

## Phase 2 の完了条件

1. Centrifuge が powered machine 基底を利用している。
2. CP 不足時に progress を維持する。
3. 複数出力の公開・搬出順が明示され、既存の意図を維持している。
4. `ContainerData` と NBT の既存契約を維持している。
5. コンパイルと上記の手動確認が完了している。