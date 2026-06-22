# 機械共通化設計メモ

この文書では、このプロジェクトで機械用の共通クラスを増やしていくときの方針をまとめます。

対象は主に次のような機械です。

1. Crusher 系
2. Extreme Compressor 系
3. 今後追加する、同じ見た目と処理骨格を持つ powered machine 系

最初から全機械を 1 本の巨大な基底クラスへ入れるのではなく、**似ている系統ごとに 1 段ずつ共通化する**ことを前提にします。

## 1. 先に結論

このプロジェクトでは、機械共通化を次の 3 層で進めるのが安全です。

1. 全機械共通
2. powered machine 系共通
3. 各機械固有

具体的には次のイメージです。

- 全機械共通: [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/BaseBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/BaseBlockEntity.java)
- powered machine 共通 Menu: [src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java)
- powered machine 共通 Screen: [src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java)
- powered machine 共通 BlockEntity: [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java)

この方針にすると、**共通クラスは処理の流れだけを持ち、継承先は差分だけを書く**構成にできます。

## 2. 現在の到達点

現在は、Menu / Screen に加えて、BlockEntity についても powered machine 系の土台を追加済みです。

追加済みの共通クラス:

1. [src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java)
2. [src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java)
3. [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java)

すでにこの土台へ移行したクラス:

1. [src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneCrusherMenu.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneCrusherMenu.java)
2. [src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneExtremeCompressorMenu.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneExtremeCompressorMenu.java)
3. [src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneCrusherScreen.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneCrusherScreen.java)
4. [src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneExtremeCompressorScreen.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/CobblestoneExtremeCompressorScreen.java)
5. [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java)
6. [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneExtremeCompressorBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneExtremeCompressorBlockEntity.java)

まだ未移行の powered machine 系 BlockEntity はありますが、最初の共通基底はすでに導入済みです。

## 3. Base に残す責務

共通クラスに入れるのは、**機械が違っても流れがほぼ同じもの**だけに限定します。

### 3-1. BaseBlockEntity の責務

[src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/BaseBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/BaseBlockEntity.java) には、全機械共通の自動搬出入まわりだけを残します。

具体的には次です。

1. 面ごとの automation mode 保持
2. fluid automation mode 保持
3. auto export の保持
4. capability invalidate
5. automation 設定の保存と読込
6. automation copy data の作成と適用

ここには、レシピ処理や progress 計算は入れません。

### 3-2. PoweredMachineMenuBase の責務

[src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/PoweredMachineMenuBase.java) には、powered machine 系で毎回同じになる Menu の流れを持たせます。

具体的には次です。

1. stillValid の共通判定
2. プレイヤーインベントリとホットバー追加
3. Shift クリック移動の共通骨格
4. JEI 転送定義の共通組み立て
5. BlockPos から BlockEntity を引き直す共通 helper

### 3-3. PoweredMachineScreenBase の責務

[src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/screen/PoweredMachineScreenBase.java) には、powered machine 系 GUI の共通描画と共通操作を持たせます。

具体的には次です。

1. start/stop ボタン
2. auto export ボタン
3. automation panel
4. automation ボタンの更新
5. progress bar 描画
6. cobblestone power bar 描画
7. 共通 tooltip
8. automation 凡例
9. JEI clickable area の共通組み立て

## 4. 継承先に残す責務

継承先には、**機械ごとに意味が変わる差分だけ**を残します。

### 4-1. Menu に残す差分

1. ContainerData の index 意味づけ
2. スロット構成
3. Shift クリック時の投入優先順位
4. JEI カテゴリ ID
5. 個別機械だけが持つ追加 getter

例:

- Crusher では、入力、power、output、upgrade への投入順を持つ
- Extreme Compressor では、上記に加えて入力アイテム名と必要個数表示用 getter を持つ

### 4-2. Screen に残す差分

1. 背景テクスチャ
2. progress bar テクスチャ
3. レイアウト座標
4. JEI カテゴリ ID
5. 個別追加ラベル

例:

- Crusher は標準 powered machine の座標を返すだけで済む
- Extreme Compressor は入力アイテム名と必要個数を追加描画する

## 5. 追加した BlockEntity 基底の方針

追加した基底は PoweredMachineBlockEntityBase です。

対象はまず次の 2 クラスに絞ります。

1. [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneCrusherBlockEntity.java)
2. [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneExtremeCompressorBlockEntity.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneExtremeCompressorBlockEntity.java)

この 2 つは、次の流れがかなり似ています。

1. CP を保持する
2. レシピを探す
3. progress を進める
4. 完了したら出力する
5. auto export を試す
6. upgrade slot を持つ

### 5-1. PoweredMachineBlockEntityBase に持たせたもの

1. progress
2. maxProgress
3. storedCobblestonePower
4. isAvailable
5. long の lower / upper 同期 helper 利用
6. tick の共通骨格
7. saveAdditional / loadAdditional の共通部分
8. ContainerData の共通領域
9. auto export 用の共通搬出 helper
10. automation mode から handler を選ぶ共通 helper

### 5-2. 継承先へ残した abstract / hook

1. findMatchingRecipe()
2. canProcessRecipe()
3. shouldResetProgress()
4. getRecipeProcessingTime()
5. getRecipeCobblestonePowerPerTick()
6. finishProcessing()
7. onRecipeStateChanged()
8. onUnavailableTick()
9. getPowerSlotIndex()
10. getOutputSlotIndex()

ここで大事なのは、**tick 全体を abstract にしない**ことです。

tick の流れ自体は共通にできます。

1. 動作可能か確認する
2. 充電を試す
3. レシピを探す
4. 加工可能なら progress を進める
5. 完了したら出力を作る
6. auto export が有効なら搬出を試す

この流れだけを Base に置き、判定と加工結果だけを継承先に残す方が追いやすいです。

### 5-3. progress リセット条件の実装方針

今回の実装では、progress を捨てる条件を次のように整理しました。

1. 材料不一致のときは reset する
2. 出力詰まりのときは reset する
3. 停止操作のときは reset する
4. power 不足のときは reset せず待機する

この方針は [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java](../src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/PoweredMachineBlockEntityBase.java) の tick 骨格へ反映済みです。

特に、power 不足だけで progress を毎回 0 に戻すと、高い CP/t のレシピが低段階の丸石電力アイテムで事実上進まなくなることがあります。
そのため、共通基底では「加工条件は満たしているが 1 tick 分の電力だけ足りない」状態を待機扱いにしました。

## 6. ContainerData の整理案

powered machine 系では、ContainerData の先頭並びをある程度固定すると、Menu 側も BlockEntity 側も読みやすくなります。

候補は次です。

1. progress
2. maxProgress
3. storedPower lower
4. storedPower upper
5. maxStoredPower lower
6. maxStoredPower upper
7. 機械固有の追加データ...
8. automation modes...
9. currentPowerRate lower
10. currentPowerRate upper
11. autoExport

実装では、共通領域 0 - 5 の直後に機械固有データを置き、その後ろへ automation と currentPowerRate と autoExport を並べています。

たとえば Extreme Compressor なら、次を固有領域へ置きます。

1. stored item count
2. required item count
3. stored item id

この形にしておくと、共通 Menu で扱える部分が増えます。

## 7. automation handler の整理案

Crusher 系では、automation 用 IItemHandler がかなり似ています。

現状の代表例:

1. inputAutomationHandler
2. cobblestoneInputAutomationHandler
3. outputAutomationHandler
4. automationAccessHandler

現時点の実装では、handler そのものの生成 utility までは切り出していません。
ただし、automation mode から「input / cobblestone input / output / in-out」のどれを返すかという選択処理だけは、PoweredMachineBlockEntityBase へ共通化しています。

将来的にさらに重複が増えたら、次の条件だけを与えて生成する helper を検討します。

1. どの実スロットを見せるか
2. insert を許可するか
3. extract を許可するか
4. isItemValid をどこへ委譲するか

この形なら、機械ごとの差分は保ちつつ、匿名クラスの重複を減らせます。

## 8. 今はやらない方がよい共通化

次は、今の段階では避けた方が安全です。

1. Furnace 系と powered machine 系を同じ BlockEntity 基底へ入れる
2. fluid machine まで同じ BlockEntity 基底へ入れる
3. 全機械の ItemStackHandler を 1 つの基底で完全共通化する
4. Recipe 型まで強いジェネリクスで共通化する

理由は、差分を無理に抽象化すると、初心者が追いにくくなるからです。

## 9. 実装順のおすすめ

次の順で進めると、壊れたときに原因を追いやすいです。

1. PoweredMachineBlockEntityBase を追加する
2. progress / power / isAvailable / tick 骨格だけを移す
3. Crusher を移行する
4. Extreme Compressor を移行する
5. compileJava で確認する
6. 必要なら automation handler utility を切り出す

このうち 1 - 5 は実施済みです。

## 10. 確認方法

この設計を実装に移したときは、まず次で確認します。

1. .\gradlew.bat compileJava

実装後の初回確認として、compileJava は通過済みです。

その後、必要に応じて次を確認します。

1. .\gradlew.bat runClient
2. Crusher と Extreme Compressor の GUI が開くか
3. power 不足時に progress が維持されたまま再開できるか
4. 停止操作、材料不一致、出力詰まり時に progress が正しく破棄されるか
5. progress bar と power bar が崩れていないか
6. Shift クリック移動が壊れていないか
7. auto export と automation ボタンが動くか

## 11. この文書の使い方

今後 powered machine 系を共通化するときは、次の順で判断します。

1. その差分は「流れの違い」か「値の違い」か
2. 値の違いなら Base へ移して abstract にしない
3. 意味が変わるなら abstract にする
4. fluid や複数入力まで含むなら、別系統 Base を検討する

この文書は、まず Crusher 系を安全に整理するためのメモです。
将来 Chemical Reactor 系や fluid machine 系を整理するときは、別の系統メモを追加する前提で考えます。
