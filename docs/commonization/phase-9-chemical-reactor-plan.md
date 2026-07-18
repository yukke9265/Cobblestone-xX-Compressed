# Phase 9: Chemical Reactor の複合入出力共通化計画

## 目的

Chemical Reactor を `PoweredMachineBlockEntityBase<CobblestoneChemicalReactorRecipe>` へ移行し、CP、progress、停止、保存、`ContainerData`、item auto export の重複を共通化する。

## 維持する仕様

- item slot 0-1 は入力、slot 2 はCP、slot 3-4 は出力、slot 5-6 はupgradeのままとする
- `inputFluid1`、`inputFluid2`、`outputFluid1`、`outputFluid2` と各AmountのNBT保存形式を維持する
- Menu の41要素の `ContainerData` 順序を維持する
- item の `INPUT` はslot 0から1への順次投入、`INPUT_1` と `INPUT_2` は各入力slotだけへの投入を維持する
- item outputは `OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` 面へ、出力1から出力2の順にauto exportする
- fluid は `INPUT`、`INPUT_1`、`INPUT_2` で入力を受け、`OUTPUT`、`OUTPUT_1`、`OUTPUT_2` で出力を取り出せるようにする
- fluid auto exportは出力tank 1から出力tank 2の順に実行する

## 共通化する処理

- CPの蓄積、消費、上限、upgrade倍率
- progressの更新とCP不足時の保持
- start/stop、ブロックのON状態、停止時のprogress reset
- inventory、automation mode、CP、progressの保存と復元
- item・fluid automationを含む`ContainerData`の共通領域
- item outputのauto export

## 固有処理として残すもの

- 2種類のitem入力と2種類のfluid入力を照合するrecipe検索
- recipeに対応する入力slot・tankからの消費
- item 2出力、fluid 2出力の容量・種類確認と生成
- 4 fluid tank、fluid capability、fluid container操作
- 4 tankの量・上限・fluid IDを表す20個の同期値

## 仕様統一

- CP不足だけではprogressをresetせず、CP回復後に続きから再開する
- item不足、fluid不足、出力itemの詰まり、出力fluidの種類不一致または容量不足、停止、recipe不一致ではprogressをresetする

## 確認項目

1. GUIを開閉でき、CP、progress、4 tankのfluid量、automation表示が同期される
2. itemの `INPUT`、`INPUT_1`、`INPUT_2`、`COBBLESTONE_INPUT`、`OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` が正しく公開される
3. fluidの `INPUT`、`INPUT_1`、`INPUT_2`、`OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` が正しく公開される
4. item 2入力・fluid 2入力を使うrecipeを最後まで処理できる
5. item 2出力とfluid 2出力が、指定面およびauto exportで出力1から2の順に搬出される
6. CP不足ではprogressを保持し、材料不足、fluid不足、出力詰まり、停止ではprogressをresetする
7. ワールド再起動後もinventory、4 tank、automation mode、auto export、CP、progressが保持される