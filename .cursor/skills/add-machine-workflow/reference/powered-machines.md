# Powered Machine 実装規約

## 対象の判断

次の条件を満たす機械は `PoweredMachineBlockEntityBase<R>` を継承する。

- Cobblestone Power (CP) を蓄積してrecipe加工に消費する
- recipeの進行度と完了処理を持つ
- stop、upgrade、automation、auto exportのうち1つ以上を使用する

燃料を燃やす Furnace、発電だけを行う Generator、貯蔵・移送だけを行う Tankは対象外である。最初に、入出力の構成が近い既存機械を1台選ぶ。

| 構成 | 参照例 |
| --- | --- |
| item 1入力・1出力 | Cobblestone Crusher |
| 複数item入力・出力 | Cobblestone Mixer、Cobblestone Centrifuge |
| itemとfluidの複合 | Cobblestone Reaction Chamber |
| 複数item・複数fluid | Cobblestone Chemical Reactor |
| fluidのみ | Cobblestone Fluid Mixer |

## 基底classへ任せる処理

以下を継承先で重複実装しない。

- CPの蓄積、消費、最大容量、progress、start / stop、ブロックON状態
- upgrade倍率の適用
- 共通状態のNBT保存・読込
- item automation mode、auto export、共通 `ContainerData`
- `ContainerData` のlong値のlower / upper int同期

継承先では、次のhookへ固有処理を実装する。

- recipe検索と処理可能判定
- 入力item・fluidの照合と消費
- item・fluidの出力生成
- tank構成、fluid capability、container操作
- 複数出力の搬出順、fluid auto export

## 処理停止時の規則

CP不足だけではprogressをresetしない。CPが回復したら同じrecipeの処理を再開する。

次の場合はprogressをresetする。

- recipeが見つからない、または入力が一致しない
- itemまたはfluidの入力が不足する
- item出力が詰まる
- fluid出力の種類が一致しない、または容量が足りない
- 機械が停止している

特殊な理由がなければこの規則を変更しない。

## Automation

- 面ごとの設定は `BaseBlockEntity` のautomation modeを使用する
- item handlerは `AutomationItemHandlerHelper` を優先する
- `IN_OUT` は実在するinventory slot数を公開し、slot数を偽装しない
- 入力・出力が複数ある場合は、どのslotへ投入し、どの順に搬出するかを明示する
- item出力がない機械は `getOutputSlotIndex()` を `-1` にし、`pushOutputsToConfiguredSides()` をno-opにする
- fluid出力は `onAutoExportFluid()` で `OUTPUT` / `IN_OUT` 面への搬出を実装する

## 同期と互換性

既存機械を拡張・移行する場合、次を変更しない。

- BlockEntity ID、inventory slot番号、NBTキー
- recipe typeとserializer、JEI category ID
- Menuが読む `ContainerData` のindexと要素数

fluid automationを持つmachineでは、`getPoweredMachineDataCount(..., true)`、`getPoweredMachineCommonData(..., true)`、`setPoweredMachineCommonData(..., true)` を使用する。`ContainerData` はintだけを同期するため、CPなどのlong値は `LongDataHelper` を使ってlower / upper intへ分割する。

## 最低限の確認

1. `gradlew.bat compileJava` を実行する
2. GUIがある場合は、Menuの `ContainerData` 要素数とindexを確認する
3. automationがある場合は、各面の投入・取出・auto exportをゲーム内で確認する
4. fluidがある場合は、tankの入出力、container操作、fluid auto exportを確認する
5. CP不足でprogressを維持し、入力不足・出力詰まり・停止ではresetすることを確認する