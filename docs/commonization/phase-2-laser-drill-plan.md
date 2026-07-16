# Phase 2: Laser Drill の複数出力共通化計画

## 目的

`CobblestoneLaserDrillBlockEntity` が個別に持つ CP、progress、upgrade、tick、NBT、`ContainerData` の処理を、`PoweredMachineBlockEntityBase<CobblestoneLaserDrillRecipe>` へ統一します。

Laser Drill は Centrifuge と同じ 1 入力・2 出力構成ですが、入力を消費しない触媒として扱う点だけが異なります。このため、共通基底には処理の進行だけを任せ、触媒を残す完成処理と確率出力は Laser Drill 固有の処理として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - 入力触媒: 0
  - CP: 1
  - 出力 1: 2
  - 出力 2: 3
  - acceleration chip: 4
  - energized cube: 5
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`isAvailable`、`inventory`、automation 設定
- `ContainerData` の 0 から 14 までの index と意味
- `INPUT`、`COBBLESTONE_INPUT`、`OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` の automation 公開ルール
- auto export の出力順: 出力 1、出力 2
- 入力触媒をレシピ完了時に消費しない仕様
- それぞれ独立した確率による 2 種類の出力判定

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と inventory の保存・復元
- `ContainerData` の共通領域
- 出力 slot を指定した auto export

## 意図した挙動統一

旧 Laser Drill は CP が不足すると `canProcess()` が false となり、進行中の progress を reset していました。

移行後は他の powered machine と揃え、材料・レシピ・出力先が有効で CP だけが不足している場合には progress を維持します。CP が回復すると、その progress から処理を再開します。

入力触媒の不在またはレシピ不一致では recipe が見つからないため、progress と max progress を reset します。出力が詰まった場合と停止操作時も progress を reset します。

## 実装手順

1. `AutomationItemHandlerHelper` を使い、既存の automation 公開 slot を維持する
2. `PoweredMachineBlockEntityBase` を継承し、CP と progress の重複実装を削除する
3. `finishProcessing()` に、入力触媒を消費せず確率出力だけを追加する既存処理を移す
4. `pushOutputsToConfiguredSides()` を override し、出力 1、出力 2 の順で搬出する
5. 既存の data index と同じ 15 要素を、基底の `getPoweredMachineDataCount(0)` で同期する
6. `compileJava` の後、ゲーム内で下記を確認する

## 手動確認項目

1. 入力触媒が完了後も残ること
2. 2 出力の確率判定と空 result の扱いが従来どおりであること
3. `OUTPUT` が両出力を、`OUTPUT_1` / `OUTPUT_2` がそれぞれの出力だけを公開すること
4. `IN_OUT` が入力への投入と両出力からの抽出だけを許可すること
5. auto export が出力 1、出力 2 の順に動作すること
6. CP 不足時に progress を維持し、CP 回復後に再開すること
7. 出力詰まり、停止、入力触媒の変更または除去で progress が reset すること
8. GUI、Shift+クリック、JEI、world の保存・再読込、upgrade の倍率が正しく動作すること
