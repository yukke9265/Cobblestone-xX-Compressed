# Phase 8: Assembly Machine の複合入力共通化計画

## 目的

Assembly Machine を `PoweredMachineBlockEntityBase<CobblestoneAssemblyMachineRecipe>` へ移行し、CP、progress、停止、保存、`ContainerData`、item auto export の重複を共通化する。

## 維持する仕様

- item slot 0-5 は6入力、slot 6 はCP、slot 7 は出力、slot 8-9 はupgradeのままとする
- `inputFluid` と `inputFluidAmount` のNBT保存形式を維持する
- Menu の26要素の `ContainerData` 順序を維持する
- item の `INPUT` はslot 0から5への順次投入、`INPUT_1` はslot 0から2、`INPUT_2` はslot 3から5への順次投入を維持する
- fluid は `INPUT` で投入、`IN_OUT` で投入・取出を許可する
- item output は `OUTPUT` と `IN_OUT` 面へauto exportする

## 共通化する処理

- CPの蓄積、消費、上限、upgrade倍率
- progressの更新とCP不足時の保持
- start/stop、ブロックのON状態、停止時のprogress reset
- inventory、automation mode、CP、progressの保存と復元
- item・fluid automationを含む`ContainerData`の共通領域
- item outputのauto export

## 固有処理として残すもの

- 6種類までのitem入力とfluid入力を照合するrecipe検索
- recipeごとの入力slot対応を使ったitem消費
- input fluid tank、fluid capability、fluid container操作
- fluid量とfluid IDの5個の同期値

## 確認項目

1. GUIを開閉でき、CP、progress、fluid量、automation表示が同期される
2. `INPUT`、`INPUT_1`、`INPUT_2` の面から意図した入力groupへ順番にitemを投入できる
3. `COBBLESTONE_INPUT` からCP itemだけを投入できる
4. `OUTPUT` と `IN_OUT` から完成品を取り出せ、auto exportを切り替えられる
5. fluid `INPUT` から投入でき、`IN_OUT` から投入・取出できる
6. 6 item入力とfluid入力を使うrecipeを最後まで処理できる
7. CP不足ではprogressを保持し、材料不足、fluid不足、出力詰まり、停止ではprogressをresetする
8. ワールド再起動後もinventory、input fluid、automation mode、auto export、CP、progressが保持される