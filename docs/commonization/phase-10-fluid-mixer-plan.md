# Phase 10: Fluid Mixer の複数タンク共通化計画

## 目的

Fluid Mixer を `PoweredMachineBlockEntityBase<CobblestoneFluidMixerRecipe>` へ移行し、CP、progress、停止、保存、`ContainerData`を共通化する。

## 維持する仕様

- item slot 0はCP、slot 1-2はupgradeのままとする
- `inputFluid1`、`inputFluid2`、`outputFluid` と各AmountのNBT保存形式を維持する
- Menu の36要素の `ContainerData` 順序を維持する
- item は `COBBLESTONE_INPUT` からCP itemだけを投入でき、`IN_OUT` を含めてitemの投入・取出は許可しない
- fluid は `INPUT` で2入力tankへ順次投入し、`INPUT_1` と `INPUT_2` では指定tankへ投入する
- fluid は `OUTPUT` と `IN_OUT` から出力tankを取り出せる
- auto exportは出力fluidだけを `OUTPUT` と `IN_OUT` 面へ搬出する

## 共通化する処理

- CPの蓄積、消費、上限、upgrade倍率
- progressの更新とCP不足時の保持
- start/stop、ブロックのON状態、停止時のprogress reset
- inventory、automation mode、CP、progressの保存と復元
- item・fluid automationを含む`ContainerData`の共通領域

## 固有処理として残すもの

- 2種類のfluid入力を照合するrecipe検索
- recipeに対応する入力tankからのfluid消費と出力tankへの生成
- 3 fluid tank、fluid capability、fluid container操作
- 3 tankの量・上限・fluid IDを表す15個の同期値
- item 出力を持たないため、item auto exportを無効化する処理

## 仕様統一

- CP不足だけではprogressをresetせず、CP回復後に続きから再開する
- 入力fluid不足、recipe不一致、出力fluidの種類不一致または容量不足、停止ではprogressをresetする

## 確認項目

1. GUIを開閉でき、CP、progress、3 tankのfluid量、automation表示が同期される
2. `COBBLESTONE_INPUT` からCP itemだけを投入でき、`IN_OUT` を含めてitemを移動できない
3. fluidの `INPUT`、`INPUT_1`、`INPUT_2`、`OUTPUT`、`IN_OUT` が正しく公開される
4. 2種類のfluidを使うrecipeを最後まで処理できる
5. auto exportのオン・オフで、出力fluidだけの搬出が正しく切り替わる
6. CP不足ではprogressを保持し、fluid不足、出力容量不足、停止ではprogressをresetする
7. ワールド再起動後もCP、3 tank、automation mode、auto export、progressが保持される