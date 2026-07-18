# Powered Machine 共通化まとめ

## 結論

CPを消費して加工する全14機械を `PoweredMachineBlockEntityBase` へ統一した。

- 標準 item加工: Crusher、Extreme Compressor、Powered Furnace
- 複数 item入出力: Centrifuge、Mixer、Laser Drill、Enchanter
- item / fluid複合: Melter、Crystallization Chamber、Dissolution Chamber、Reaction Chamber、Assembly Machine、Chemical Reactor、Fluid Mixer

各機械のrecipe照合、item / fluidの消費・生成、タンク構成、GUI固有表示は継承先に残し、共通する状態管理だけを基底へ集約している。

## 共通基盤の責務

| 部品 | 共通化した処理 |
| --- | --- |
| `BaseBlockEntity` | 面ごとのitem / fluid automation、auto export設定、capability更新 |
| `PoweredMachineBlockEntityBase` | CPの蓄積・消費、progress、start / stop、ブロックON状態、upgrade倍率、NBT、`ContainerData`、item / fluid auto export入口 |
| `AutomationItemHandlerHelper` | 挿入専用、抽出専用、複数slot順次投入、制限付き全slot公開handler |

## 統一した仕様

1. CP不足だけではprogressをresetせず、CP回復後に処理を再開する
2. 入力不一致、入力不足、出力詰まり、出力fluidの容量・種類不一致、停止、recipe消失ではprogressをresetする
3. `ContainerData` の既存index、inventory slot番号、NBTキー、BlockEntity ID、recipe type、JEI IDは維持する
4. itemまたはfluidの出力が複数ある機械は、既存の出力順を明示的なhookで維持する

## 機械ごとの固有処理

| 機械の構成 | 継承先に残した主な処理 |
| --- | --- |
| 複数item入力・出力 | 入力slotの照合、出力slotの順序、確率出力、触媒保持 |
| fluid入力・出力 | tankの種類・容量、capability公開、container操作、fluid出力順 |
| 特殊recipe | Enchanterのエンチャント評価、Extreme Compressorの入力蓄積、Assembly Machineの6入力照合 |

## 完了したフェーズ

| フェーズ | 対象 | 主な成果 |
| --- | --- | --- |
| 0 | 仕様調査 | 既存契約と統一方針を記録 |
| 1 | 標準powered 3機械 | automation handler helperを導入 |
| 2 | Centrifuge、Mixer、Laser Drill | 複数item入出力と出力順を共通化 |
| 3 | Enchanter | 特殊CP容量を保った基底移行 |
| 4-7 | Melter、Crystallization、Dissolution、Reaction | fluidを持つ機械の同期と搬出を共通化 |
| 8-10 | Assembly、Chemical Reactor、Fluid Mixer | 複合入力・複数tankのpowered基盤移行 |

## 検証状況

- 各移行時に `gradlew.bat compileJava` が成功している
- Chemical Reactor と Fluid Mixer はゲーム内確認が完了している
- それ以前のフェーズの詳細な確認項目と結果は、各フェーズ計画書および `PROGRESS.md` を参照する

## 今後の扱い

powered machine のBlockEntity共通化は完了とする。今後の機械追加では、最も近い既存機械を1台だけ参照し、`PoweredMachineBlockEntityBase` のhookへ固有処理を実装する。

UI、JEI、datagenのさらなるテンプレート化は別の改善作業として扱い、既存のBlockEntity移行と混在させない。