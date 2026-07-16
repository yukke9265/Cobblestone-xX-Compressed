# Extreme Compressor 現行仕様

## BlockEntity とレシピ

| 項目 | 現行仕様 |
| --- | --- |
| BlockEntity | `CobblestoneExtremeCompressorBlockEntity`。`PoweredMachineBlockEntityBase<CobblestoneExtremeCompressorRecipe>` を継承する。 |
| tick の入口 | `CobblestoneExtremeCompressorBlock#getTicker()` から基底クラスの `tick()` を呼ぶ。 |
| recipe type | `COBBLESTONE_EXTREME_COMPRESSOR`。同じ入力 item を recipe の必要個数まで内部に蓄積して、1 個の結果を作る。 |
| CP | recipe ごとの総 CP / CP per tick を使う。基本最大容量は 33,554,432,000 CP。 |

## inventory

スロットは Crusher と同じく、0=入力、1=丸石電力、2=出力、3=加速チップ、4=容量キューブである。入力は item handler 上では任意、出力は挿入不可、upgrade は各 1 個までである。

## progress と automation

- 入力が recipe に一致し、すでに蓄積した item と同じ item の場合に進行できる。必要個数を蓄積済みの場合は、出力可能であることも必要になる。
- CP 不足だけの場合は `progress` を維持する。入力不一致、蓄積済みかつ出力詰まり、停止、recipe 消失時には `progress` を reset する。
- `INPUT`=0 へ挿入専用、`COBBLESTONE_INPUT`=1 へ挿入専用、`OUTPUT`=2 から抽出専用、`IN_OUT`=全スロット公開だが 0 への挿入と 2 からの抽出だけを許可する。
- auto export はスロット 2 を `OUTPUT` / `IN_OUT` の面へ搬出する。

## 保存・同期・画面

- 共通 NBT は Crusher と同じ。固有 NBT は `storedInputItemCount`、`currentRequiredItemCount`、`storedInputTemplate`。
- `ContainerData`: 0=`progress`、1=`maxProgress`、2-3=蓄積 CP、4-5=最大 CP、6=蓄積数、7=必要数、8=蓄積 item ID、9-14=automation mode、15-16=現在の CP 消費量、17=auto export。
- Menu / Screen / JEI: `CobblestoneExtremeCompressorMenu`、`CobblestoneExtremeCompressorScreen`、`CobblestoneExtremeCompressorRecipeCategory`。JEI transfer の入力範囲はスロット 0 の 1 枠。

## 手動確認

未実施。通常加工に加え、途中蓄積中の停止・再開、停止時の入力回収、保存・再読込を確認する。