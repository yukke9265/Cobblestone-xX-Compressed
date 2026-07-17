# Phase 4: Melter の流体出力共通化計画

## 目的

`CobblestoneMelterBlockEntity` が個別に実装している CP、progress、upgrade、tick、NBT の共通部分を `PoweredMachineBlockEntityBase<CobblestoneMelterRecipe>` へ統一します。

Melter は item を入力し、fluid を出力する機械です。流体タンク、流体 capability、流体 container との操作、fluid の auto export は Melter 固有の責務として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - 入力: 0
  - CP: 1
  - acceleration chip: 2
  - energized cube: 3
- 流体タンクの最大容量: 64,000 mB
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`storedFluidAmount`、`isAvailable`、`inventory`、`storedFluid`、automation 設定
- `ContainerData` の 0 から 25 までの index と意味
- item automation: `INPUT`、`COBBLESTONE_INPUT`
- fluid automation: `OUTPUT`
- 流体 container を GUI 上で満たす操作
- 流体の auto export と、出力面へ流体だけを公開する capability

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と item inventory の保存・復元

## 基底クラスの最小拡張

`PoweredMachineBlockEntityBase` に `onAutoExportFluid()` hook を追加します。既定実装は何もしません。

Melter だけがこの hook を override し、`OUTPUT` の fluid automation 面へ流体を搬出します。Melter には item 出力 slot がないため、item の既定 auto export は no-op として override します。

これにより既存の item powered machine の動作を変えず、流体出力機械だけが明示的に流体搬出を有効化できます。

## 意図した挙動統一

旧 Melter は CP が不足すると `canProcess()` が false となり、progress を reset していました。

移行後は他の powered machine と揃え、材料、レシピ、流体出力先が有効で CP だけが不足している場合には progress を維持します。CP が回復すると途中から再開します。

入力不足、レシピ不一致、流体タンクの種類不一致または容量不足、停止操作では progress を reset します。

## 実装手順

1. 基底に流体 auto export 用の空 hook を追加する
2. Melter を `PoweredMachineBlockEntityBase<CobblestoneMelterRecipe>` へ移行する
3. item 入力と CP 入力の handler を `AutomationItemHandlerHelper` で生成する
4. 流体タンク、fluid capability、container 操作、クライアント同期を維持する
5. `onAutoExportFluid()` で既存の流体搬出処理を呼び出す
6. 既存の 26 要素の `ContainerData` index を維持する
7. `compileJava` の後、ゲーム内で下記を確認する

## 手動確認項目

1. `INPUT`、`COBBLESTONE_INPUT` の item automation が従来どおり動作すること
2. `OUTPUT` の fluid automation 面でのみ流体を取り出せること
3. auto export のオン・オフが流体搬出を切り替えること
4. 流体 container を GUI から満たせること
5. 異なる fluid が混ざらず、64,000 mB を超えて蓄積しないこと
6. CP 不足時に progress を維持し、CP 回復後に再開すること
7. 流体出力詰まり、入力除去、停止時に progress が reset すること
8. GUI、JEI、Shift+クリック、保存・再読込、upgrade の倍率が正しく動作すること
