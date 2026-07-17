# Phase 5: Crystallization Chamber の流体入力共通化計画

## 目的

`CobblestoneCrystallizationChamberBlockEntity` の CP、progress、upgrade、tick、NBT を `PoweredMachineBlockEntityBase<CobblestoneCrystallizationChamberRecipe>` へ統一します。

Crystallization Chamber は fluid を入力し、item を出力する機械です。流体タンク、fluid capability、GUI の fluid container 操作だけを機械固有の責務として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - CP: 0
  - 出力: 1
  - acceleration chip: 2
  - energized cube: 3
- 流体タンクの最大容量: 64,000 mB
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`storedFluidAmount`、`isAvailable`、`inventory`、`storedFluid`、automation 設定
- `ContainerData` の 0 から 23 までの index と意味
- item automation: `COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT`
- fluid automation: `INPUT`、`OUTPUT`、`IN_OUT`
- GUI から fluid container をタンクへ移す、またはタンクから満たす操作
- item 出力の auto export

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と item inventory の保存・復元
- item 出力の auto export

## 意図した挙動統一

旧 Crystallization Chamber は、CP が不足すると `canProcess()` が false となり、progress を reset していました。

移行後は他の powered machine と揃え、流体、recipe、item 出力先が有効で CP だけが不足している場合には progress を維持します。CP が回復すると途中から再開します。

流体不足、recipe 不一致、出力詰まり、停止操作では progress を reset します。

## 実装内容

1. `PoweredMachineBlockEntityBase<CobblestoneCrystallizationChamberRecipe>` へ移行する
2. CP 入力、item 出力、`IN_OUT` の item handler を `AutomationItemHandlerHelper` で生成する
3. 流体タンク、fluid capability、container 操作、クライアント同期を維持する
4. `ContainerData` は fluid automation を含む helper を使い、既存の 24 要素を維持する
5. `compileJava` の後、ゲーム内で下記を確認する

## 手動確認項目

1. GUI を開き、CP、progress、流体量、item / fluid の automation mode、auto export が同期されること
2. `COBBLESTONE_INPUT` から CP item を投入できること
3. `OUTPUT` から完成 item を取り出せ、auto export で搬出されること
4. fluid `INPUT` から流体を投入でき、`OUTPUT` と `IN_OUT` から取り出せること
5. GUI の fluid indicator で container から投入・container へ充填できること
6. 異なる fluid が混ざらず、64,000 mB を超えて蓄積しないこと
7. CP 不足時に progress を維持し、CP 回復後に再開すること
8. 流体不足、出力詰まり、停止時に progress が reset すること
9. Shift+クリック、保存・再読込、upgrade の倍率、JEI が正しく動作すること