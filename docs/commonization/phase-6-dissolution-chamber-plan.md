# Phase 6: Dissolution Chamber の複数流体タンク共通化計画

## 目的

`CobblestoneDissolutionChamberBlockEntity` の CP、progress、upgrade、tick、NBT を `PoweredMachineBlockEntityBase<CobblestoneDissolutionChamberRecipe>` へ統一します。

Dissolution Chamber は item と fluid を入力し、fluid を出力する機械です。入力・出力タンク、fluid capability、GUI の fluid container 操作、fluid の auto export は機械固有の責務として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - 入力: 0
  - CP: 1
  - acceleration chip: 2
  - energized cube: 3
- 入力・出力タンクの最大容量: 各 64,000 mB
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`isAvailable`、`inventory`、`inputFluid`、`inputFluidAmount`、`outputFluid`、`outputFluidAmount`、automation 設定
- `ContainerData` の 0 から 30 までの index と意味
- item automation: `INPUT`、`COBBLESTONE_INPUT`、`IN_OUT`
- fluid automation: `INPUT`、`OUTPUT`、`IN_OUT`
- 入力・出力タンクの GUI container 操作
- 出力 fluid の auto export

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と item inventory の保存・復元

## 意図した挙動統一

旧 Dissolution Chamber は、CP が不足すると `canProcess()` が false となり、progress を reset していました。

移行後は他の powered machine と揃え、item、入力 fluid、recipe、出力 fluid タンクが有効で CP だけが不足している場合には progress を維持します。CP が回復すると途中から再開します。

item 不足、入力 fluid 不足、recipe 不一致、出力 fluid の種類不一致または容量不足、停止操作では progress を reset します。

## 実装内容

1. `PoweredMachineBlockEntityBase<CobblestoneDissolutionChamberRecipe>` へ移行する
2. item 入力、CP 入力、`IN_OUT` の item handler を `AutomationItemHandlerHelper` で生成する
3. 入力・出力タンク、fluid capability、container 操作、クライアント同期を維持する
4. item 出力がないため、基底の item auto export は no-op にする
5. `onAutoExportFluid()` で既存の fluid 出力搬出を実行する
6. `ContainerData` は fluid automation を含む helper を使い、既存の 31 要素を維持する

## 手動確認項目

1. GUI を開き、CP、progress、入力・出力 fluid、item / fluid の automation mode、auto export が同期されること
2. `INPUT`、`COBBLESTONE_INPUT`、`IN_OUT` の item automation が従来どおり動作すること
3. fluid `INPUT` から入力タンクへ投入できること
4. fluid `OUTPUT` から出力タンクの fluid だけを取り出せること
5. fluid `IN_OUT` が入力タンクへの投入と出力タンクからの取り出しを行えること
6. auto export のオン・オフで出力 fluid の搬出が切り替わること
7. 両タンクに異なる fluid が混ざらず、各 64,000 mB を超えて蓄積しないこと
8. GUI で input / output fluid container をそれぞれ投入・充填できること
9. CP 不足時に progress を維持し、CP 回復後に再開すること
10. item 不足、入力 fluid 不足、出力 fluid の種類不一致・容量不足、停止時に progress が reset すること
11. Shift+クリック、保存・再読込、upgrade の倍率、JEI が正しく動作すること