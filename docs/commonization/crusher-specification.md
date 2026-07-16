# Crusher 現行仕様

## BlockEntity とレシピ

| 項目 | 現行仕様 |
| --- | --- |
| BlockEntity | `CobblestoneCrusherBlockEntity`。`PoweredMachineBlockEntityBase<CobblestoneCrusherRecipe>` を継承する。 |
| tick の入口 | `CobblestoneCrusherBlock#getTicker()` から基底クラスの `tick()` を呼ぶ。 |
| recipe type | `COBBLESTONE_CRUSHER`。1 個の `Ingredient` を 1 個の結果へ加工する。 |
| CP | recipe の `totalCobblestonePower` と `cobblestonePowerPerTick` を使用する。処理時間は recipe 側で算出する。基本最大容量は 4,000 CP。 |

## inventory

| 番号 | 定数 | 用途 | item の受入条件 |
| --- | --- | --- | --- |
| 0 | `INPUT_SLOT_INDEX` | 加工入力 | item handler 上は任意。Shift+クリック時は Crusher recipe の入力に一致する item だけを優先する。 |
| 1 | `POWER_SLOT_INDEX` | 丸石電力 | 丸石電力値を持つ item のみ。 |
| 2 | `OUTPUT_SLOT_INDEX` | 加工結果 | 挿入不可。 |
| 3 | `ACCELERATION_SLOT_INDEX` | 加速チップ | 加速チップのみ、1 個まで。 |
| 4 | `ENERGIZED_CUBE_SLOT_INDEX` | 容量キューブ | Energized Cube のみ、1 個まで。 |

## progress と automation

- 処理は、有効状態・一致する recipe・出力可能・必要 CP がそろったときに進む。
- CP 不足だけの場合は `progress` を維持する。
- recipe が見つからない、停止する、または出力先に結果を積めない場合は `progress` を reset する。
- `INPUT` はスロット 0 へ挿入専用、`COBBLESTONE_INPUT` はスロット 1 へ挿入専用、`OUTPUT` はスロット 2 から抽出専用である。
- `IN_OUT` は 5 スロットを公開するが、挿入できるのはスロット 0、抽出できるのはスロット 2 だけである。
- auto export はスロット 2 を `OUTPUT` と `IN_OUT` に設定した面へ毎 tick 搬出する。

## 保存・同期・画面

- NBT: `progress`、`maxProgress`、`storedCobblestonePower`、`isAvailable`、`inventory`、6 面の `*AutomationMode` / `*FluidAutomationMode`、`autoExportEnabled`。
- `ContainerData`: 0=`progress`、1=`maxProgress`、2-3=蓄積 CP、4-5=最大 CP、6-11=面ごとの automation mode、12-13=現在の CP 消費量、14=auto export。
- Menu / Screen / JEI: `CobblestoneCrusherMenu`、`CobblestoneCrusherScreen`、`CobblestoneCrusherRecipeCategory`。JEI transfer の入力範囲はスロット 0 の 1 枠。

## 手動確認

未実施。Phase 1 へ進む前に、GUI、Shift+クリック、全 automation mode、auto export、保存・再読込を確認する。