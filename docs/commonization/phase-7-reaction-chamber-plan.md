# Phase 7: Reaction Chamber の複合入力共通化計画

## 目的

`CobblestoneReactionChamberBlockEntity` の CP、progress、upgrade、tick、NBT を `PoweredMachineBlockEntityBase<CobblestoneReactionChamberRecipe>` へ統一します。

Reaction Chamber は 2 種類の item と fluid を入力し、item を出力する機械です。fluid タンク、fluid capability、GUI の fluid container 操作、2 入力の recipe 照合だけを機械固有の責務として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - 入力 1: 0
  - 入力 2: 1
  - CP: 2
  - 出力: 3
  - acceleration chip: 4
  - energized cube: 5
- 流体タンクの最大容量: 64,000 mB
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`storedFluidAmount`、`isAvailable`、`inventory`、`storedFluid`、automation 設定
- `ContainerData` の 0 から 25 までの index と意味
- item automation: `INPUT`、`INPUT_1`、`INPUT_2`、`COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT`
- fluid automation: `INPUT`、`OUTPUT`、`IN_OUT`
- GUI の fluid container 操作
- item 出力の auto export

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と item inventory の保存・復元
- item 出力の auto export

## 意図した挙動統一

旧 Reaction Chamber は、CP が不足すると `canProcess()` が false となり、progress を reset していました。

移行後は他の powered machine と揃え、2 種類の item、fluid、recipe、item 出力先が有効で CP だけが不足している場合には progress を維持します。CP が回復すると途中から再開します。

item 不足、fluid 不足、recipe 不一致、出力詰まり、停止操作では progress を reset します。

fluid は入力材料です。旧実装にあった入力 fluid の auto export は廃止し、auto export は item 出力だけを対象にします。fluid `OUTPUT` / `IN_OUT` capability による明示的な取り出しは維持します。

## 実装内容

1. `PoweredMachineBlockEntityBase<CobblestoneReactionChamberRecipe>` へ移行する
2. `INPUT` の入力 1、入力 2 の順次投入、`INPUT_1`、`INPUT_2`、CP 入力、item 出力、`IN_OUT` を `AutomationItemHandlerHelper` で生成する
3. fluid タンク、fluid capability、container 操作、クライアント同期を維持する
4. `ContainerData` は fluid automation を含む helper を使い、既存の 26 要素を維持する

## 手動確認項目

1. GUI を開き、CP、progress、fluid 量、item / fluid の automation mode、auto export が同期されること
2. `INPUT` が入力 1、入力 2 の順に item を投入すること
3. `INPUT_1` と `INPUT_2` が対応する入力 slot だけに投入すること
4. `COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT` の item automation が従来どおり動作すること
5. fluid `INPUT` から流体を投入でき、`OUTPUT` と `IN_OUT` から流体を明示的に取り出せること
6. auto export が item 出力だけを搬出し、入力 fluid を搬出しないこと
7. GUI の fluid indicator で container から投入・container へ充填できること
8. 異なる fluid が混ざらず、64,000 mB を超えて蓄積しないこと
9. CP 不足時に progress を維持し、CP 回復後に再開すること
10. item 不足、fluid 不足、出力詰まり、停止時に progress が reset すること
11. Shift+クリック、保存・再読込、upgrade の倍率、JEI が正しく動作すること