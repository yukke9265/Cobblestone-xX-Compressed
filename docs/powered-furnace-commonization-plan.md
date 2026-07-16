# Powered Furnace 共通化作業計画

この文書は、Cobblestone Powered Furnace を既存の powered machine 共通基底へ段階的に移行するための作業計画です。

## 1. 目的

次の 3 クラスで重複している処理を、既存の共通基底へ寄せます。

1. BlockEntity: CobblestonePoweredFurnaceBlockEntity
2. Menu: CobblestonePoweredFurnaceMenu
3. Screen: CobblestonePoweredFurnaceScreen

これにより、Crusher / Extreme Compressor と同じ設計レイヤーに揃え、今後の機械追加時の実装コストを下げます。

## 2. 対象と非対象

### 対象

1. powered machine 系共通基底への継承切り替え
2. ContainerData の共通領域利用
3. GUI の共通描画・共通ボタン処理への移行
4. 既存挙動と同等の動作を保つための微調整

### 非対象

1. Assembly/Chemical 系との統合
2. fluid automation を含む新しい共通基底の追加
3. 全機械の automation handler の全面再設計

## 3. 実施ステップ

### Step A: Menu 共通化

1. CobblestonePoweredFurnaceMenu の継承先を PoweredMachineMenuBase へ変更
2. stillValid / quickMoveStack / プレイヤーインベントリ追加の重複を削除
3. Furnace 固有差分のみ残す
- スロット定義
- 投入優先順位
- automation button 配列
- JEI カテゴリ

### Step B: Screen 共通化

1. CobblestonePoweredFurnaceScreen の継承先を PoweredMachineScreenBase へ変更
2. 共通化済みの描画・ボタンロジックを基底へ委譲
3. Furnace 固有差分のみ残す
- 背景/進捗テクスチャ
- レイアウト座標
- JEI カテゴリ

### Step C: BlockEntity 共通化

1. CobblestonePoweredFurnaceBlockEntity の継承先を PoweredMachineBlockEntityBase へ変更
2. tick 骨格・進捗/電力同期・保存読込を基底方式へ寄せる
3. Furnace 固有差分として次を実装
- findMatchingRecipe
- canProcessRecipe / shouldResetProgress
- getRecipeProcessingTime / getRecipeCobblestonePowerPerTick
- finishProcessing
- スロット index と IItemHandler 公開

## 4. 互換性・挙動の確認観点

1. GUI が開ける
2. start/stop が機能する
3. power 不足時の進捗挙動が意図どおりか
4. 出力詰まり時の進捗リセットが意図どおりか
5. Shift+クリック搬入が壊れていないか
6. auto export / automation ボタンが機能するか
7. JEI レシピ転送とクリック領域が機能するか

## 5. リスクと対策

### リスク 1: 進捗リセット条件の変化

- 内容: 旧実装では電力不足で進捗が 0 になる場面があり、基底移行後は待機扱いになる可能性がある
- 対策: 仕様として受け入れるか確認し、必要なら shouldResetProgress で明示制御する

### リスク 2: Data index ずれ

- 内容: ContainerData の index が崩れると GUI 表示やボタン挙動が壊れる
- 対策: 基底の getPoweredMachineDataCount / getPoweredMachineCommonData を利用して手書き index を減らす

### リスク 3: Menu の quickMove 優先順位変更

- 内容: 共通化時に投入順が変わると体感操作が変わる
- 対策: 既存優先順位を moveStackToMachine へそのまま移植する

## 6. 完了条件

1. compileJava が通る
2. Powered Furnace の Menu / Screen / BlockEntity が共通基底へ移行済み
3. 主要挙動チェック項目を手動確認できる状態になっている

## 7. 次の共通化タスク

Powered Furnace 共通化の次は、次の順で進めるのが安全です。

### Task 1: powered machine 系の automation handler 重複削減

対象:

1. CobblestoneCrusherBlockEntity
2. CobblestonePoweredFurnaceBlockEntity
3. 必要なら CobblestoneExtremeCompressorBlockEntity

作業内容:

1. input / cobblestone input / output / in-out の匿名 IItemHandler 実装を棚卸しする
2. すでに存在する SingleSlotAutomationHandler と MultiSlotAutomationHandler へ寄せられる部分を整理する
3. それでも残る差分だけを各 BlockEntity に残す

完了条件:

1. 挙動を変えずに匿名クラスの重複量が減っている
2. getAutomationItemHandler の読みやすさが上がっている

### Task 2: powered machine 系 Menu の差分最小化

対象:

1. CobblestoneCrusherMenu
2. CobblestonePoweredFurnaceMenu
3. CobblestoneExtremeCompressorMenu

作業内容:

1. getProgress / getMaxProgress / getStoredCobblestonePower のような共通 getter 群を点検する
2. PoweredMachineMenuBase へ移せる補助処理があれば移し、機械固有 index 依存部分だけを残す
3. quickMove 優先順位の差分が明確になるように、moveStackToMachine の責務を維持する

完了条件:

1. Menu ごとの差分が「投入優先順位」と「機械固有データ」にほぼ限定される
2. JEI 転送定義が既存どおり機能する

### Task 3: Assembly/Chemical 系向けの別系統共通化の設計着手

対象:

1. CobblestoneAssemblyMachineBlockEntity
2. CobblestoneChemicalReactorBlockEntity
3. fluid automation を持つ機械群

作業内容:

1. powered machine 基底へ無理に統合せず、fluid を含む系統の共通責務を整理する
2. item automation と fluid automation の index 配置ルールを設計メモとして固定する
3. 最初は設計文書のみ作成し、実装は最小単位で分割する

完了条件:

1. 新しい系統メモが docs 配下に追加されている
2. 最初に移行する 1 機械が明確になっている

## 8. 次サイクルの実施順

1. Task 1 を小さく実装して compileJava を通す
2. Task 2 で Menu 側の見通しを揃える
3. Task 3 の設計メモを起こして、次回以降の実装単位を固定する
