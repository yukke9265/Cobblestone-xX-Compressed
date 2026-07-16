# Powered Furnace 現行仕様

## BlockEntity とレシピ

| 項目 | 現行仕様 |
| --- | --- |
| BlockEntity | `CobblestonePoweredFurnaceBlockEntity`。`PoweredMachineBlockEntityBase<CobblestonePoweredFurnaceRecipe>` を継承する。 |
| tick の入口 | `CobblestonePoweredFurnaceBlock#getTicker()` から基底クラスの `tick()` を呼ぶ。 |
| recipe type | `COBBLESTONE_POWERED_FURNACE`。1 入力・1 出力の炉系 recipe。 |
| CP | recipe ごとの総 CP / CP per tick を使う。基本最大容量は 1,000 CP。 |

## inventory と automation

スロットは 0=入力、1=丸石電力、2=出力、3=加速チップ、4=容量キューブである。受入条件、各 upgrade の上限、automation handler の公開内容、auto export の対象は Crusher と同一である。

- CP 不足だけの場合は `progress` を維持する。
- recipe 消失、停止、出力詰まりでは `progress` を reset する。
- `INPUT` は 0 へ挿入専用、`COBBLESTONE_INPUT` は 1 へ挿入専用、`OUTPUT` は 2 から抽出専用である。
- `IN_OUT` は全スロットを公開するが、0 への挿入と 2 からの抽出だけを許可する。auto export はスロット 2 を `OUTPUT` / `IN_OUT` 面へ搬出する。

## 保存・同期・画面

- NBT と `ContainerData` は Crusher と同一である。
- Menu / Screen / JEI: `CobblestonePoweredFurnaceMenu`、`CobblestonePoweredFurnaceScreen`、`CobblestonePoweredFurnaceRecipeCategory`。JEI transfer の入力範囲はスロット 0 の 1 枠。

## 手動確認

未実施。GUI、Shift+クリック、JEI transfer、全 automation mode、CP 不足時の進捗維持、出力詰まり時の reset、保存・再読込を確認する。