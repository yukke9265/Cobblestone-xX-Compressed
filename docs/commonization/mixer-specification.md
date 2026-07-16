# Mixer 現行仕様

## BlockEntity とレシピ

| 項目 | 現行仕様 |
| --- | --- |
| BlockEntity | `CobblestoneMixerBlockEntity`。`BaseBlockEntity` を直接継承し、独自の `tick()` を実装する。 |
| tick の入口 | `CobblestoneMixerBlock#getTicker()` から `tick()` を呼ぶ。 |
| recipe type | `COBBLESTONE_MIXER`。2 個の `SizedIngredient` を 1 個の結果へ加工する。入力位置は recipe の一致結果に従い、必要個数を消費する。 |
| CP | recipe ごとの総 CP / CP per tick を使う。基本最大容量は 16,000 CP。 |

## inventory と automation

| 番号 | 用途 |
| --- | --- |
| 0 | 第 1 入力 |
| 1 | 第 2 入力 |
| 2 | 丸石電力 |
| 3 | 出力（挿入不可） |
| 4 | 加速チップ（1 個） |
| 5 | 容量キューブ（1 個） |

- `INPUT` は 0、次に 1 へ順番に挿入する 2 スロット handler を公開する。
- `INPUT_1` は 0 へ、`INPUT_2` は 1 へ、`COBBLESTONE_INPUT` は 2 へ、それぞれ挿入専用で公開する。
- `OUTPUT` は 3 から抽出専用、`IN_OUT` は全スロットを公開するが、0・1 への挿入と 3 からの抽出だけを許可する。
- 現行の独自 `tick()` では、CP 不足も含めて処理を進められない場合に `progress` を reset する。停止、recipe 不在、入力不一致、出力詰まりでも reset する。この挙動は標準 powered machine と異なるため、基底クラスへ移行する場合は別の仕様変更として確認が必要である。
- auto export はスロット 3 を `OUTPUT` / `IN_OUT` の面へ搬出する。

## 保存・同期・画面

- NBT と `ContainerData` は Centrifuge と同一である。
- Menu / Screen / JEI: `CobblestoneMixerMenu`、`CobblestoneMixerScreen`、`CobblestoneMixerRecipeCategory`。機械スロットは 0-5。

## 手動確認

未実施。2 入力の自動振り分け、個別入力面、recipe の入力順入れ替え、出力搬出、保存・再読込を確認する。

## 共通化時の変更予定

`PoweredMachineBlockEntityBase` への移行時に、CP 不足だけでは `progress` を reset しない標準 powered machine の挙動へ統一する。2 入力の投入順、個別入力面、recipe の入力照合と消費量は Mixer 固有の仕様として維持する。