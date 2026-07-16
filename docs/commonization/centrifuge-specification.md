# Centrifuge 現行仕様

## BlockEntity とレシピ

| 項目 | 現行仕様 |
| --- | --- |
| BlockEntity | `CobblestoneCentrifugeBlockEntity`。`BaseBlockEntity` を直接継承し、独自の `tick()` を実装する。 |
| tick の入口 | `CobblestoneCentrifugeBlock#getTicker()` から `tick()` を呼ぶ。 |
| recipe type | `COBBLESTONE_CENTRIFUGE`。1 入力から、確率付きの第 1 / 第 2 結果をそれぞれ作る。 |
| CP | recipe ごとの総 CP / CP per tick を使う。基本最大容量は 64,000 CP。 |

## inventory と automation

| 番号 | 用途 |
| --- | --- |
| 0 | 入力 |
| 1 | 丸石電力 |
| 2 | 第 1 出力（挿入不可） |
| 3 | 第 2 出力（挿入不可） |
| 4 | 加速チップ（1 個） |
| 5 | 容量キューブ（1 個） |

- `INPUT` は 0 へ挿入専用、`COBBLESTONE_INPUT` は 1 へ挿入専用、`OUTPUT` は 2 と 3 から抽出専用である。
- `OUTPUT_1` は 2 のみ、`OUTPUT_2` は 3 のみを抽出専用で公開する。
- `IN_OUT` は全スロットを公開するが、0 への挿入、2 と 3 からの抽出だけを許可する。
- 現行の独自 `tick()` では、CP 不足も含めて処理を進められない場合に `progress` を reset する。停止、recipe 不在、出力詰まりでも reset する。この挙動は標準 powered machine と異なるため、基底クラスへ移行する場合は別の仕様変更として確認が必要である。
- auto export は出力 1 を `OUTPUT` / `OUTPUT_1` / `IN_OUT`、続けて出力 2 を `OUTPUT` / `OUTPUT_2` / `IN_OUT` の面へ搬出する。

## 保存・同期・画面

- NBT: `progress`、`maxProgress`、`storedCobblestonePower`、`isAvailable`、`inventory`、automation mode、fluid automation mode、`autoExportEnabled`。
- `ContainerData`: 0=`progress`、1=`maxProgress`、2-3=蓄積 CP、4-5=最大 CP、6-11=automation mode、12-13=現在の CP 消費量、14=auto export。
- Menu / Screen / JEI: `CobblestoneCentrifugeMenu`、`CobblestoneCentrifugeScreen`、`CobblestoneCentrifugeRecipeCategory`。機械スロットは 0-5。

## 手動確認

未実施。二つの出力を個別に搬出できること、両出力が詰まった場合の reset、保存・再読込を確認する。

## 共通化時の変更予定

`PoweredMachineBlockEntityBase` への移行時に、CP 不足だけでは `progress` を reset しない標準 powered machine の挙動へ統一する。出力 1、出力 2 の順序、および `OUTPUT_1` / `OUTPUT_2` の公開ルールは維持する。