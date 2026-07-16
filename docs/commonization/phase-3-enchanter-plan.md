# Phase 3: Enchanter の powered machine 共通化計画

## 目的

`CobblestoneEnchanterBlockEntity` が重複して実装している CP、progress、upgrade、tick、NBT、`ContainerData` を `PoweredMachineBlockEntityBase<CobblestoneEnchanterRecipe>` へ統一します。

Enchanter はツールとエンチャント本の 2 入力機械です。エンチャント可否の評価、評価結果に応じた CP/t、2 入力の消費だけを機械固有の責務として残します。

## 互換性として維持するもの

- BlockEntity ID、block ID、recipe type、JEI category ID
- inventory の slot 番号
  - ツール入力: 0
  - エンチャント本入力: 1
  - CP: 2
  - 出力: 3
  - acceleration chip: 4
  - energized cube: 5
- NBT キー: `progress`、`maxProgress`、`storedCobblestonePower`、`isAvailable`、`inventory`、automation 設定
- `ContainerData` の 0 から 14 までの index と意味
- `INPUT`、`INPUT_1`、`INPUT_2`、`COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT` の automation 公開ルール
- `INPUT` 面で、エンチャント本は本入力へ、それ以外の有効な tool はツール入力へ振り分ける既存ルール
- エンチャント評価、エンチャントの競合判定、結果 item、CP/t の算出
- 2 入力を 1 個ずつ消費して結果を出力する完成処理

## 共通化する処理

- CP の吸収、上限 clamp、消費
- progress と max progress の更新
- acceleration chip と energized cube の倍率
- 停止状態とブロックの ON/OFF 状態
- NBT の共通部分と inventory の保存・復元
- `ContainerData` の共通領域
- 単一出力の auto export

## Enchanter 固有の容量要件

通常の最大 CP は energized cube の倍率を掛けた 16,384 CP です。

ただし Enchanter は、高コストのエンチャントを 1 tick 分以上消費できる必要があります。そのため既存どおり、評価済みレシピの CP/t と acceleration 倍率を掛けた値を必要容量として計算し、通常上限より大きい場合はその値を最大 CP とします。

この計算で基底の energized cube 倍率を再利用できるよう、倍率取得は基底から継承先へ公開します。

## 意図した挙動統一

旧 Enchanter は CP が不足すると `canProcess()` が false となり、progress を reset していました。

移行後は他の powered machine と揃え、入力、レシピ、エンチャント評価、出力先が有効で CP だけが不足している場合には progress を維持します。CP が回復すると途中から再開します。

入力不足、レシピ不一致、エンチャント競合、出力詰まり、停止操作では progress を reset します。

また、CP が不足しているときの GUI 用の現在消費量は、共通基底の仕様に合わせて `0` とします。実際に CP を消費している tick だけ CP/t を表示します。

## 実装手順

1. `PoweredMachineBlockEntityBase` の energized cube 倍率取得を、継承先が再利用できる protected helper にする
2. Enchanter を `PoweredMachineBlockEntityBase<CobblestoneEnchanterRecipe>` へ移行する
3. 通常の単一 slot handler と `IN_OUT` handler を `AutomationItemHandlerHelper` で共通生成する
4. tool と enchanted book を item 種別で振り分ける `INPUT` handler は Enchanter 固有として残す
5. 評価結果から出力可否、CP/t、必要容量、完成処理を実装する
6. 既存の 15 要素の `ContainerData` を基底の共通 helper で同期する
7. `compileJava` の後、ゲーム内で下記を確認する

## 手動確認項目

1. `INPUT` 面で tool と enchanted book が正しい入力 slot に振り分けられること
2. `INPUT_1` と `INPUT_2` がそれぞれ tool / book だけを受け付けること
3. `COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT` の公開 slot が変わらないこと
4. エンチャント競合や不正な本では開始しないこと
5. 完成時に tool と book が 1 個ずつ消費され、評価結果が出力されること
6. CP 不足時に progress を維持し、CP 回復後に再開すること
7. 出力詰まり、入力変更、停止時に progress が reset すること
8. 高コストのエンチャントで 1 tick 分の CP を保持できること
9. auto export、GUI、JEI、Shift+クリック、world の保存・再読込、upgrade の倍率が正しく動作すること
