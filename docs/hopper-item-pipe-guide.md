# Cobblestone Furnace のホッパー・アイテムパイプ対応手順

この文書では、既にある Cobblestone Furnace を
ホッパーやアイテムパイプのような**外部自動搬入出**へ対応させる流れをまとめます。

ここでやりたいことは、単に GUI を開いて手で出し入れできるようにすることではありません。
**ブロックの外から機械の中身へアクセスできる入口を用意する**ことが目的です。

このプロジェクトでは、主に次のクラスが対象になります。

- [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java)
- [src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java)
- [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java)

## 1. まず結論

今の Cobblestone Furnace は、内部には `ItemStackHandler` を持っています。
そのため、**機械の中にアイテムを保存する土台自体はあります**。

ただし、ホッパーやアイテムパイプがその在庫を見つけるための
**NeoForge の item capability 公開処理** がまだありません。

そのため、現状では次の状態です。

- GUI からは見える
- Menu からはスロットを触れる
- しかし外部搬入出の機械からは見えない

つまり、足りないのは「インベントリ本体」ではなく、
**外部に見せる窓口**です。

## 2. どこが不足しているのか

現在の [src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java) には、次があります。

- 入力スロット 0
- 出力スロット 1
- `ItemStackHandler` による在庫管理
- NBT 保存 / 読み込み
- tick による加工処理

一方で、次はまだありません。

1. `Capabilities.ItemHandler.BLOCK` を登録する処理
2. 面ごとにどのスロットを見せるかのルール
3. 自動搬入では入れられるが、自動搬出では取り出せる、という制御

特に大事なのは、
[src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java) で出力スロットに `mayPlace(false)` を付けていても、
**それは GUI 上の手動操作を制御しているだけ** だという点です。

ホッパーやパイプは Menu を通らず、capability を通してアクセスします。
そのため、GUI 側の制御だけでは自動搬入出ルールになりません。

## 3. 先に決めるべき搬入出ルール

実装より先に、どの面から何を許可するかを決めます。

Cobblestone Furnace なら、まずは次のルールが分かりやすいです。

1. 上面: 入力スロットへ投入できる
2. 側面: 入力スロットへ投入できる
3. 底面: 出力スロットから取り出せる

この形にすると、バニラかまどに近い感覚で使えます。

もし仕様をもっと単純にしたいなら、次の形でも構いません。

1. 上面だけ入力
2. 底面だけ出力
3. 側面は何もしない

初心者向けには、まずはこのどちらかに固定した方が追いやすいです。

## 4. 実装の考え方

実装の考え方は、次の 2 段に分けると分かりやすいです。

### 内部在庫

- `itemStackHandler`
- GUI
- 加工処理
- 保存 / 読み込み

### 外部公開用ハンドラ

- 上面用ハンドラ
- 側面用ハンドラ
- 底面用ハンドラ
- 必要なら side が `null` のときのハンドラ

つまり、**本体の在庫は 1 つだけ** にして、
外から見える形だけを分けるのが安全です。

## 5. 一番分かりやすい実装方針

このプロジェクトでは、`ItemStackHandler` をそのまま capability として返すのではなく、
**自動搬入出専用の薄いラッパー**を作るのが分かりやすいです。

理由は単純です。

- 入力スロットには挿入だけ許可したい
- 出力スロットには取り出しだけ許可したい
- 面によって見せるスロットを変えたい

単なる 0 番スロット / 1 番スロットの公開だけでは、
「出力スロットへ挿入できてしまう」などの抜けが出やすくなります。

## 6. BlockEntity 側で追加したいもの

[src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/blockentity/CobblestoneFurnaceBlockEntity.java) に、次の考え方を追加します。

1. 入力専用ハンドラ
2. 出力専用ハンドラ
3. 必要なら全面アクセス用ハンドラ
4. 向きではなく接続面に応じて返す `getAutomationItemHandler(Direction side)` のようなメソッド

例えば考え方は次の通りです。

```java
private IItemHandler createInputAutomationHandler() {
    return new IItemHandlerModifiable() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            itemStackHandler.setStackInSlot(INPUT_SLOT_INDEX, stack);
        }
    };
}
```

出力側も同じ考え方で、今度は

- `insertItem(...)` は常にそのまま返して拒否
- `extractItem(...)` だけ通す

という形にします。

ここでは少し長く見えますが、やっていることは単純です。

- 入力側は「入れるだけ」
- 出力側は「出すだけ」

です。

## 7. `RangedWrapper` だけで済ませない方がよい理由

NeoForge にはスロット範囲を切り出すラッパーもあります。

ただし今回の炉では、単にスロット番号を分けるだけでなく、
**挿入と取り出しの向きまで分けたい** です。

そのため、初心者向けには次の方が安全です。

1. 入力用は挿入だけ許可するラッパー
2. 出力用は取り出しだけ許可するラッパー

この方が、後で見返したときに挙動を追いやすくなります。

## 8. 面ごとに返すハンドラを決める

次に、どの面から問い合わせが来たかで返すハンドラを変えます。

考え方は次のようにしておくと分かりやすいです。

```java
public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
    if (side == null) {
        return automationAccessHandler;
    }

    if (side == Direction.DOWN) {
        return outputAutomationHandler;
    }

    return inputAutomationHandler;
}
```

この例では、

- `null`: 汎用アクセス
- `DOWN`: 出力専用
- それ以外: 入力専用

として扱っています。

`null` をどうするかは mod 間連携次第ですが、
最初は「自動化ルールを守る汎用ハンドラ」を返す方が無難です。

## 9. capability 登録が本体

実際にホッパーやパイプから見えるようにする本体は、
**RegisterCapabilitiesEvent で block item capability を登録すること** です。

[src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java) か、
もしくは capability 登録専用クラスを用意して、そこでイベント登録します。

考え方は次のようになります。

```java
modEventBus.addListener(this::registerCapabilities);

private void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.ItemHandler.BLOCK,
        ModBlockEntities.COBBLESTONE_FURNACE_BLOCK_ENTITY.get(),
        (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
    );
}
```

ここでやっていることは、
「Cobblestone Furnace の BlockEntity に対して、外部から item handler を問い合わせられたら、対応するハンドラを返す」
という登録です。

これがない限り、内部に `ItemStackHandler` を持っていても外部機械には見えません。

## 10. `CobblestonexXCompressed` に追加する場合の流れ

今の構成に合わせるなら、
[src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java) のコンストラクタで次を追加する形が分かりやすいです。

1. `RegisterCapabilitiesEvent` 用の listener を追加する
2. 専用メソッド `registerCapabilities(...)` を作る
3. その中で Cobblestone Furnace の item capability を登録する

登録処理を別クラスへ分けてもよいですが、最初は Mod 本体に置いた方が追いやすいです。

## 11. GUI 側は基本そのままでよい

[src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/menu/CobblestoneFurnaceMenu.java) は、今回の目的では大きく変えなくてよい可能性が高いです。

理由は、今回追加したいのは GUI ではなく外部搬入出だからです。

ただし、次の点は理解しておくと混乱しません。

1. `SlotItemHandler` は GUI スロットの見え方と手動操作の制御
2. capability はホッパーやパイプからの自動操作の制御

この 2 つは役割が別です。

## 12. レシピ判定との関係

入力スロットに何でも入れられると、無効アイテムが詰まることがあります。

そのため、入力用 automation handler では、できれば
`isItemValid(...)` だけでなく `insertItem(...)` 側でも
レシピに使えないものを拒否できるとより安全です。

最低限でも、次のどちらかは入れておきたいです。

1. 入力スロットの `isItemValid(...)` を独自に絞る
2. レシピ判定に使えるアイテムだけ受け付ける補助メソッドを作る

最初は 2 より 1 の方が実装しやすいです。

## 13. 取り出し側で気を付けること

出力スロットは、自動搬出では取り出せても、
自動搬入では入れられないようにします。

つまり出力用 handler は、次の挙動に固定します。

1. `insertItem(...)` は拒否
2. `extractItem(...)` は許可

ここを曖昧にすると、ホッパーが完成品スロットへアイテムを押し込めてしまい、
炉の状態が崩れやすくなります。

## 14. まずはキャッシュを後回しにする

面ごとの handler は毎回 new しても作れますが、
最初は読みやすさ優先でよいです。

もし後で気になるなら、BlockEntity に次のようなフィールドを持たせて再利用できます。

1. `inputAutomationHandler`
2. `outputAutomationHandler`
3. `automationAccessHandler`

ただし、最初から最適化を入れすぎるより、
まず動作をはっきりさせる方が重要です。

## 15. 実装順のおすすめ

初心者向けには、次の順で進めると切り分けしやすいです。

1. BlockEntity に入力用 handler と出力用 handler を作る
2. `getAutomationItemHandler(Direction side)` を作る
3. `RegisterCapabilitiesEvent` で item capability を登録する
4. `runClient` でホッパー投入と搬出を確認する
5. 必要なら入力制限を厳しくする

この順なら、どこで動かなくなったかを追いやすいです。

## 16. 確認手順

確認は次の順が分かりやすいです。

1. 上にホッパーを置いて入力アイテムが入るか
2. 下にホッパーを置いて完成品が抜けるか
3. 完成品スロットへ外部から押し込めないか
4. 無効アイテムを押し込んだときに詰まらないか

もしアイテムパイプ系 mod でも確認するなら、
同じ 4 点を見れば十分です。

## 17. 詰まりやすいポイント

### capability を登録していない

内部に `ItemStackHandler` があっても、外からは見えません。

### GUI の `mayPlace(false)` だけで満足してしまう

それは GUI の話で、自動搬入出の制御ではありません。

### 返す handler が内部在庫を無制限に見せている

そのまま返すと、出力スロットへの挿入まで許してしまうことがあります。

### 面ごとのルールを決めずに実装し始める

どの面から何を許すかを決めてから書いた方が、後で修正しやすいです。

## 18. 今回の炉で最初に目指す完成形

Cobblestone Furnace なら、まずは次の完成形を目指すと扱いやすいです。

1. 上面と側面からは入力だけできる
2. 底面からは出力だけ取れる
3. GUI で見えるスロット構成と意味が一致している
4. 手動操作と自動操作のルールが分かれていても矛盾しない

この状態までできれば、ホッパーだけでなく、
多くのアイテムパイプ系 mod からも扱いやすい炉になります。
