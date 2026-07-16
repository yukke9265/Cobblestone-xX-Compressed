## 概要
共通化計画の進捗を記載するドキュメントです。現在の状況、次のステップ、課題などを整理し、次のアクションを明確にすることを目的としています。

## 進捗状況
- 共通化計画のドキュメントを作成
- Phase 0 のコード調査を完了し、Crusher、Extreme Compressor、Powered Furnace、Centrifuge、Mixer の現行仕様表を追加

## Phase 0 の確認状況
- コード上の仕様固定: 完了
- ゲーム内の手動確認: 未実施
- Phase 1 の開始条件: 手動確認後に、Crusher の automation handler だけを helper へ移行する

## 統一方針
- 意図しない機械間の挙動差は、共通化に合わせて仕様変更して統一する
- BlockEntity ID、inventory スロット番号、NBT キー、recipe type、JEI category ID は維持する
- Centrifuge と Mixer は、基底クラスへ移行する際に CP 不足時の progress reset を progress 維持へ変更する
- 詳細は [Phase 0 調査結果と挙動統一方針](./phase-0-findings.md) を参照

## Phase 1 の進捗
- `AutomationItemHandlerHelper` を追加し、挿入専用、抽出専用、挿入・抽出を制限した全スロット公開 handler を共通生成できるようにした
- Crusher、Extreme Compressor、Powered Furnace の 4 個ずつの匿名 `IItemHandler` を helper で生成する形へ置き換えた。公開する slot と automation mode の選択処理は変更していない
- `gradlew.bat compileJava` は、3 機械への展開後にも成功した
- 3 機械のゲーム内 automation 確認は未実施。`INPUT`、`COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT`、auto export、保存・再読込を確認する

## 次タスク: Phase 1 の手動回帰確認

対象は Crusher、Extreme Compressor、Powered Furnace の 3 機械です。今回の変更は item capability が返す handler だけであるため、各機械で次を確認します。

### automation

1. `INPUT` の面から、加工対象 item を投入できること
2. `INPUT` の面から、出力 item や upgrade を取り出せないこと
3. `COBBLESTONE_INPUT` の面から、丸石電力 item だけを投入できること
4. `OUTPUT` の面から、完成品を取り出せること
5. `OUTPUT` の面へ、item を投入できないこと
6. `IN_OUT` の面で、入力 slot へ投入でき、出力 slot から取り出せること
7. `IN_OUT` の面で、CP slot と upgrade slot に対して投入・取出ができないこと
8. 面の mode を変更した直後、隣接ホッパーまたは item pipe が新しい capability を取得すること
9. auto export のオン・オフで、出力搬出の有無が正しく切り替わること

### 通常動作と保存

1. GUI を開閉でき、Shift+クリックの投入先が変わらないこと
2. recipe を最後まで処理できること
3. CP 不足時に progress を維持し、CP 回復後に再開すること
4. 出力詰まりと停止時に progress が reset すること
5. ワールドを再起動しても inventory、automation mode、auto export 設定が残ること
6. Extreme Compressor は、途中まで蓄積した入力 item が保存され、停止時には出力 slot へ回収されること

## 手動確認完了後の次タスク

Phase 1 の確認が完了したら、Phase 2 の準備として Centrifuge の複数出力を扱う設計を開始します。

1. `PoweredMachineBlockEntityBase` に、単一出力の既定搬出を置き換えられる小さな hook を設計する
2. Centrifuge の出力 1、出力 2 をこの順番で搬出し、`OUTPUT_1` / `OUTPUT_2` の公開条件を維持する
3. Centrifuge を `PoweredMachineBlockEntityBase` へ移行する
4. CP 不足時の progress を reset する現行挙動を、標準 powered machine と同じ progress 維持へ意図的に変更する
5. Centrifuge のコンパイル、automation、2 出力の搬出順、CP 不足からの再開、保存・再読込を確認する

## Phase 2 の検討結果
- Centrifuge を最初の移行対象とする。CP、progress、upgrade、NBT、`ContainerData` の重複を `PoweredMachineBlockEntityBase` へ統一する
- 基底クラスは、単一出力の既定搬出を維持したまま、複数出力機械だけが override できる出力搬出 hook を追加する
- `AutomationItemHandlerHelper` は、複数出力を `OUTPUT` で公開する handler と、複数出力を抽出できる `IN_OUT` handler まで拡張する
- Centrifuge の CP 不足時の progress reset は、意図した仕様変更として progress 維持へ統一する
- 詳細な実装順と確認項目は [Phase 2: Centrifuge の複数出力共通化計画](./phase-2-centrifuge-plan.md) を参照

## Phase 2 の実装進捗
- `PoweredMachineBlockEntityBase` に、既定の単一出力搬出を持つ `pushOutputsToConfiguredSides()` と、指定スロットを搬出する `pushOutputSlotToConfiguredSides()` を追加した
- `AutomationItemHandlerHelper` に、複数スロット抽出専用 handler と、複数の挿入・抽出 slot を制限できる全体公開 handler を追加した
- Centrifuge を `PoweredMachineBlockEntityBase<CobblestoneCentrifugeRecipe>` へ移行した。CP、progress、upgrade、保存、`ContainerData` は基底クラスを使用する
- Centrifuge は出力 1、出力 2 の順に auto export し、`OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` の公開 slot を維持する
- CP 不足だけの場合は progress を維持し、CP 回復後に再開するよう変更した
- `gradlew.bat compileJava` は成功した
- ゲーム内の手動確認は未実施。Phase 2 計画の基本処理、automation、保存、UI、JEI の確認項目を実施する

## Phase 2 の Mixer 移行
- `AutomationItemHandlerHelper` に、指定した複数入力 slot へ順番に投入する `INPUT` 用 handler を追加した。Mixer は入力 1、入力 2 の順に投入する既存仕様を維持する
- Mixer を `PoweredMachineBlockEntityBase<CobblestoneMixerRecipe>` へ移行した。CP、progress、upgrade、保存、`ContainerData` は基底クラスを使用する
- `INPUT_1`、`INPUT_2`、`COBBLESTONE_INPUT`、`OUTPUT`、`IN_OUT` の公開 slot と、2 入力の recipe 照合・消費量は Mixer 固有の処理として維持する
- CP 不足だけの場合は progress を維持し、CP 回復後に再開するよう変更した。入力不一致、出力詰まり、停止、recipe 不在では progress を reset する
- `gradlew.bat compileJava` は成功した
- ゲーム内の手動確認は未実施。2 入力の順次投入、個別入力面、CP 不足からの再開、出力搬出、保存・再読込を確認する

## Phase 2 の Laser Drill 移行
- Laser Drill は Centrifuge と同じ 1 入力・2 出力構成のため、`PoweredMachineBlockEntityBase<CobblestoneLaserDrillRecipe>` への移行対象とする
- 入力 slot は触媒であり、レシピ完了時に消費しない。この点と 2 種類の独立した確率出力だけを Laser Drill 固有の処理として残す
- `OUTPUT`、`OUTPUT_1`、`OUTPUT_2`、`IN_OUT` の公開 slot と、出力 1 から出力 2 の auto export 順は維持する
- CP 不足時の progress reset は、他の powered machine と同じ progress 維持・CP 回復後の再開へ意図的に統一する
- 詳細な実装手順とゲーム内確認項目は [Phase 2: Laser Drill の複数出力共通化計画](./phase-2-laser-drill-plan.md) を参照
- Laser Drill を `PoweredMachineBlockEntityBase<CobblestoneLaserDrillRecipe>` へ移行した。CP、progress、upgrade、保存、`ContainerData` は基底クラスを使用する
- 入力触媒を消費しない完成処理、独立した確率による 2 出力、個別出力面は Laser Drill 固有の処理として維持した
- `gradlew.bat compileJava` は成功した
- ゲーム内確認は完了。触媒保持、2 出力の搬出、automation、CP 不足からの再開を確認した
- 次の候補は Enchanter。2 入力の照合だけを固有処理に残せばよく、基底クラスの拡張なしで移行できる

## Phase 3 の Enchanter 移行
- Enchanter は、tool と enchanted book の 2 入力を照合する powered machine として `PoweredMachineBlockEntityBase<CobblestoneEnchanterRecipe>` へ移行する
- item 種別で入力先を振り分ける `INPUT` handler、エンチャント評価、評価結果に応じた CP/t、2 入力の消費は Enchanter 固有の処理として維持する
- 高コストのエンチャントで 1 tick 分の CP を保持できるよう、既存どおり通常上限と必要 CP 容量の大きい方を最大 CP とする
- CP 不足時の progress reset は、他の powered machine と同じ progress 維持・CP 回復後の再開へ意図的に統一する
- 詳細な実装順とゲーム内確認項目は [Phase 3: Enchanter の powered machine 共通化計画](./phase-3-enchanter-plan.md) を参照
- `PoweredMachineBlockEntityBase` の energized cube 倍率取得を protected helper とし、Enchanter の特殊な最大 CP 容量計算でも同じ倍率規則を使用できるようにした
- Enchanter を `PoweredMachineBlockEntityBase<CobblestoneEnchanterRecipe>` へ移行した。CP、progress、upgrade、保存、`ContainerData`、単一出力の auto export は基底クラスを使用する
- `INPUT` の item 種別による tool / enchanted book の振り分け、`INPUT_1` / `INPUT_2`、エンチャント評価、2 入力の消費、高コスト時の飽和 CP/t 計算は Enchanter 固有の処理として維持した
- CP 不足中の GUI 用 CP/t は 0 と表示する。これは、実際に CP を消費している tick だけを表示する共通 powered machine の仕様に統一したもの
- `gradlew.bat compileJava` は成功した
- ゲーム内の手動確認は未実施。2 入力の面別投入、エンチャント競合、CP 不足からの再開、高コスト時の容量、出力搬出、保存・再読込、GUI、JEI を確認する

## Phase 1 に向けた整理
- Crusher、Extreme Compressor、Powered Furnace は、単一入力、CP 入力、単一出力、IN_OUT の handler 構造が同じ
- Centrifuge は複数出力、Mixer は複数入力のため、Phase 1 の変更対象には含めない
- Centrifuge と Mixer の独自 `tick()` は、CP 不足時にも progress を reset する。標準 powered machine への移行は、この差分を先に仕様として判断してから行う