# GUI 作成手順

この文書では、このプロジェクトで機械ブロック用の GUI を作るときの基本的な流れをまとめます。

Minecraft / NeoForge の GUI は、単に画面クラスを 1 つ作れば終わりではありません。
基本的には **サーバー側の Menu** と **クライアント側の Screen** を分けて作ります。

今のプロジェクトには、設定画面用のクライアント extension point が [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java) にあります。
ただし、機械ブロック用の Menu / Screen はまだ入っていません。

そのため、この文書では **BlockEntity 付き機械に GUI を追加する最小構成** を前提に説明します。

## 1. まず役割を分ける

GUI まわりは、次の 4 つに分けると理解しやすいです。

### Block

- 右クリックの入口
- 画面を開くきっかけ

### BlockEntity

- 中身のデータ
- 進行度
- インベントリ内容

### Menu

- サーバー側のスロット構成
- プレイヤーインベントリとの接続
- GUI と中身データの橋渡し

### Screen

- クライアント側の見た目
- テクスチャ描画
- 進行バー表示
- タイトルやラベル表示

つまり、**見た目は Screen、処理とスロット構成は Menu** と覚えると分かりやすいです。

## 2. 最低限必要になるクラス

機械 GUI を 1 つ作るとき、最低限でも次のような構成になります。

1. `BlockEntity`
2. `Menu` クラス
3. `MenuType` の登録クラス
4. `Screen` クラス
5. クライアントでの Screen 登録

必要に応じて、さらに次も増えます。

1. `ContainerData`
2. 独自スロット
3. 進行バー表示
4. Shift クリック移動処理

## 3. BlockEntity が先に必要

GUI は見た目だけ先に作っても、実際の中身がないと意味を持ちません。

そのため、このプロジェクトでは先に次を用意しておく前提が自然です。

1. [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java) のような BlockEntity
2. 必要なら保存処理
3. GUI で見せたい値

たとえば GUI で燃焼時間を表示したいなら、BlockEntity 側に `burnTime` が必要です。

## 4. Menu クラスの役割

Menu クラスは、通常 `AbstractContainerMenu` を継承して作ります。

ここで主にやることは次の通りです。

1. 自分の機械スロットを追加する
2. プレイヤーインベントリスロットを追加する
3. `stillValid(...)` を実装する
4. 必要なら `quickMoveStack(...)` を実装する
5. 必要なら `ContainerData` を受け取る

### Menu の最小イメージ

```java
public class CobblestoneFurnaceMenu extends AbstractContainerMenu {
    public CobblestoneFurnaceMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.COBBLESTONE_FURNACE_MENU.get(), containerId);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
```

最初はこのくらい小さく始めて、あとでスロットを足す方が分かりやすいです。

## 5. `MenuType` の登録

Menu を開くには `MenuType` の登録が必要です。

NeoForge 1.21.x では、`IMenuTypeExtension.create(...)` を使う形が分かりやすいです。

### MenuType 登録例

```java
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, CobblestonexXCompressed.MODID);

    public static final Supplier<MenuType<CobblestoneFurnaceMenu>> COBBLESTONE_FURNACE_MENU =
        MENU_TYPES.register(
            "cobblestone_furnace",
            () -> IMenuTypeExtension.create(CobblestoneFurnaceMenu::new)
        );
}
```

### ここで大事なこと

- `Registries.MENU` で登録する
- Menu クラスのコンストラクタと対応させる
- `CobblestonexXCompressed` 側でイベントバス登録する

## 6. Menu コンストラクタが 2 つ必要になることが多い

実際の機械 GUI では、Menu は 2 系統のコンストラクタを持つことが多いです。

1. クライアント側で受け取る簡易コンストラクタ
2. サーバー側で BlockEntity やデータを渡す本体コンストラクタ

理由は、画面を開くときにネットワーク越しで必要データを受け取るためです。

最初は「クライアント用は `FriendlyByteBuf` から座標を読む」「本体側で BlockEntity を取る」という形を覚えると分かりやすいです。

## 7. `ContainerData` の役割

GUI で燃焼時間や進行度を見せたい場合、`ContainerData` が便利です。

これは Menu と Screen の間で、整数値を同期するための仕組みです。

たとえば次のような値に向いています。

- `burnTime`
- `progress`
- `maxProgress`

複雑なオブジェクトではなく、まず整数同期に使うものと考えると分かりやすいです。

## 8. Screen クラスの役割

見た目側は、通常 `AbstractContainerScreen<T>` を継承して作ります。

ここで主にやることは次の通りです。

1. 背景テクスチャを描く
2. タイトルやラベルを描く
3. 進行バーや燃焼バーを描く
4. 必要ならボタンなどを置く

### Screen の最小イメージ

```java
public class CobblestoneFurnaceScreen extends AbstractContainerScreen<CobblestoneFurnaceMenu> {
    public CobblestoneFurnaceScreen(CobblestoneFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }
}
```

最初は `renderBg(...)` に背景だけ置くところから始めるのがおすすめです。

## 9. Screen の登録

クライアント側では、MenuType と Screen を結び付ける登録が必要です。

NeoForge 1.21.x では `RegisterMenuScreensEvent` を使う形が分かりやすいです。

### Screen 登録例

```java
@SubscribeEvent
static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(ModMenuTypes.COBBLESTONE_FURNACE_MENU.get(), CobblestoneFurnaceScreen::new);
}
```

この処理はクライアント専用側に置きます。

今のプロジェクトなら、[src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java) に追加していく構成が自然です。

## 10. ブロック右クリックで GUI を開く

Block 側では、右クリック時に Menu を開く入口が必要です。

考え方としては次の流れです。

1. サーバー側で BlockEntity を取得する
2. その BlockEntity に対応する MenuProvider を使う
3. プレイヤーへ画面を開かせる

最初は「右クリックすると画面が開く」だけを目標にすると進めやすいです。

### このプロジェクトで入口を足す場所

今の炉ブロックは [src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneFurnaceBlock.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneFurnaceBlock.java) にあります。

そのため、GUI の入口もこのクラスへ追加していくのが自然です。

### 1.21.1 でまず意識するメソッド

Block の右クリック入口としては、次の use 系メソッドを意識します。

- `useWithoutItem(...)`
- `useItemOn(...)`

機械ブロックの GUI を開きたいだけなら、まずは `useWithoutItem(...)` を使う形で十分です。

これは「ブロック自体への使用」に近い入口で、ブロック右クリック時の処理を書きやすいです。

### Block 側でやること

Block 側の入口では、主に次を行います。

1. クライアント側なら成功だけ返す
2. サーバー側なら BlockEntity を取る
3. その BlockEntity が正しい型か確認する
4. `MenuProvider` を用意する
5. `ServerPlayer#openMenu(...)` で開く

### なぜクライアント側とサーバー側を分けるのか

GUI を本当に開く処理はサーバー側で行います。

Screen はクライアント専用ですが、どの Menu を開くかの決定と同期はサーバー側が持ちます。

そのため、Block 側では `level.isClientSide` で分岐する形が基本です。

### 最小イメージ

```java
@Override
protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (level.isClientSide) {
        return InteractionResult.SUCCESS;
    }

    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!(blockEntity instanceof CobblestoneFurnaceBlockEntity furnaceBlockEntity)) {
        return InteractionResult.PASS;
    }

    if (player instanceof ServerPlayer serverPlayer) {
        serverPlayer.openMenu(furnaceBlockEntity, pos);
    }

    return InteractionResult.CONSUME;
}
```

最初はこの形を基準にすると分かりやすいです。

### このコードで何をしているか

#### `level.isClientSide`

クライアント側では、本当に Menu を作って開くのではなく「この操作は通る」という結果だけ返します。

#### `level.getBlockEntity(pos)`

その座標にある BlockEntity を取ります。

#### `instanceof CobblestoneFurnaceBlockEntity`

自分の期待する機械 BlockEntity かどうかを確認します。

#### `serverPlayer.openMenu(...)`

サーバー側からクライアントへ GUI を開く要求を出します。

NeoForge 1.21.x では `ServerPlayer#openMenu(...)` が、GUI を開く中心の呼び出しになります。

### `openMenu(...)` の第 1 引数に何を渡すか

ここで必要なのは `MenuProvider` です。

`MenuProvider` は、次の 2 つを持つ役割です。

1. 画面タイトルの名前
2. `createMenu(...)` で実際の Menu を作る処理

つまり「このブロックを開くと、どの Menu をどう作るか」を渡すための窓口です。

### いちばん分かりやすい構成

初心者向けには、BlockEntity 自体に `MenuProvider` を持たせる構成が分かりやすいです。

この形だと、Block から BlockEntity を取ったあと、そのまま `openMenu(...)` に渡しやすくなります。

つまり流れはこうです。

1. Block が BlockEntity を取る
2. BlockEntity が `MenuProvider` として Menu を作る
3. Screen は登録済みの `MenuType` から自動で結び付く

### BlockEntity 側で必要になること

BlockEntity 側では、最終的に次を用意する必要があります。

1. `getDisplayName()`
2. `createMenu(int, Inventory, Player)`

たとえば概念的には次のようになります。

```java
@Override
public Component getDisplayName() {
    return Component.translatable("block.cobblestonexxcompressed.cobblestone_furnace");
}

@Override
public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    return new CobblestoneFurnaceMenu(containerId, playerInventory);
}
```

この部分があると、Block 側では「取得した BlockEntity を openMenu に渡す」だけで済みます。

### 座標を渡す理由

`serverPlayer.openMenu(menuProvider, pos)` のように座標も渡す形にしておくと、クライアント側 Menu の生成時にその位置情報を使いやすくなります。

とくに、クライアント側コンストラクタで対象 BlockEntity を引き直したい場合に重要です。

### 入口で `Menu` を直接 new しない理由

Block 側で直接 `new CobblestoneFurnaceMenu(...)` をしたくなることがありますが、入口では通常それをしません。

理由は、Block の役割は「開くきっかけ」を作ることだからです。

実際の Menu 作成は `MenuProvider#createMenu(...)` 側へ寄せた方が役割がはっきりします。

整理すると次の分担です。

- Block: 右クリックを受ける
- BlockEntity または MenuProvider: Menu を作る
- Screen 登録: MenuType に対する見た目を結び付ける

### `InteractionResult` は何を返すか

最初の入口では、次の考え方で十分です。

1. クライアント側では `InteractionResult.SUCCESS`
2. サーバー側で開けたら `InteractionResult.CONSUME`
3. 対象外なら `InteractionResult.PASS`

これで、右クリック操作を自分のブロックが処理したことを分かりやすく表せます。

### よくあるミス

#### クライアント側でも開こうとする

実際の GUI オープンはサーバー側で行います。

#### BlockEntity を確認せずに cast する

型が違ったときにクラッシュしやすくなります。

#### Block 側で処理を抱え込みすぎる

Menu の生成や表示名まで全部 Block に書くと責務が混ざります。

#### Screen を直接開こうとする

機械 GUI は Screen を直接出すのではなく、Menu を開いた結果として Screen が紐付きます。

### このプロジェクトでの実装順

入口を作るときは、次の順が安全です。

1. `CobblestoneFurnaceMenu` を作る
2. `ModMenuTypes` で登録する
3. `CobblestoneFurnaceBlockEntity` に `MenuProvider` 相当の処理を持たせる
4. `CobblestoneFurnaceScreen` を作って登録する
5. 最後に [src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneFurnaceBlock.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/block/CobblestoneFurnaceBlock.java) へ入口を足す

この順にすると、「開くはずなのに MenuType がない」「Screen がない」「BlockEntity が作れない」といった切り分けがしやすいです。

## 11. GUI 用テクスチャの配置

通常の機械 GUI では、背景テクスチャ PNG を 1 枚用意します。

たとえば次のような配置にすると分かりやすいです。

- `src/main/resources/assets/cobblestonexxcompressed/textures/gui/cobblestone_furnace.png`

Screen 側では、このテクスチャを `renderBg(...)` で描画します。

## 12. 最初の GUI は「背景だけ」でよい

初心者のうちは、いきなり全部そろえない方が安全です。

おすすめ順は次の通りです。

1. MenuType を登録する
2. 空の Menu を作る
3. 空の Screen を作る
4. 背景だけ表示する
5. そのあとでスロットを足す
6. 最後に進行バーや燃焼バーを足す

## 13. スロット追加は Screen ではなく Menu 側

GUI を作り始めると、見た目側にスロットを置きたくなりやすいですが、スロット構成は Menu 側です。

つまり、役割はこう分けます。

- Menu: どこに何スロットあるか
- Screen: どう見せるか

ここを混同しないと整理しやすいです。

## 14. Shift クリック移動は後回しでよい

本格的な GUI では `quickMoveStack(...)` が必要になりますが、最初から実装しなくても構いません。

まず確認したいのは次の 2 点です。

1. GUI が開くか
2. 背景が表示されるか

そのあとでスロット移動を足す方が切り分けしやすいです。

## 15. このプロジェクトでのおすすめ順

今の構成から GUI へ進むなら、次の順が安全です。

1. `CobblestoneFurnaceBlockEntity` に GUI で見せる値を決める
2. `ModMenuTypes` を作る
3. `CobblestoneFurnaceMenu` を作る
4. `CobblestoneFurnaceScreen` を作る
5. クライアント側で `RegisterMenuScreensEvent` 登録を足す
6. ブロック右クリックで開くようにする
7. そのあとでスロットや進行バーを足す

## 16. よくあるミス

### Screen だけ作って Menu を作っていない

見た目だけでは機械 GUI として動きません。

### MenuType を登録していない

クラスがあっても開けません。

### Screen 登録をクライアント専用側へ置いていない

クライアント専用処理は [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressedClient.java) 側へ寄せる方が安全です。

### スロット処理と描画処理を混同する

処理は Menu、見た目は Screen と分けるのが基本です。

### いきなり複雑な進行バーや複数スロットへ進む

まずは背景だけ表示する最小形を通した方が速いです。

## 17. まず作るならどんな GUI がよいか

初心者向けには、次の順が分かりやすいです。

1. タイトルと背景だけの GUI
2. 進行バー 1 本だけある GUI
3. スロット 1 個だけ持つ GUI
4. プレイヤーインベントリ付き GUI

独自かまどの完全版は、そのあとで十分です。

## 18. 確認順

GUI を追加したら、次の順で確認すると切り分けしやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. 右クリックでクラッシュせず開くか
2. 背景が表示されるか
3. タイトルが表示されるか
4. 進行バーや燃焼バーが意図どおり動くか

## 19. GUI の見た目は Screen 側で組み立てる

ここから先は、実際に「どう見せるか」を Screen 側で作っていきます。

見た目づくりでまず意識するのは次の 4 つです。

1. 背景画像の大きさ
2. GUI 全体の表示位置
3. タイトルやラベルの位置
4. 進行バーやボタンの位置

### まず使う値

`AbstractContainerScreen` では、次の値をよく使います。

- `imageWidth`
- `imageHeight`
- `leftPos`
- `topPos`
- `titleLabelX`
- `titleLabelY`
- `inventoryLabelX`
- `inventoryLabelY`

最初は「背景画像のサイズを決め、その中央に GUI を置くための基準値」として見ると分かりやすいです。

### 最小構成のイメージ

```java
public class CobblestoneFurnaceScreen extends AbstractContainerScreen<CobblestoneFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "cobblestonexxcompressed",
        "textures/gui/cobblestone_furnace.png"
    );

    public CobblestoneFurnaceScreen(CobblestoneFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }
}
```

`176 x 166` は、バニラ系 GUI でよく使われる基本サイズです。

もちろん必須ではありませんが、最初はこのサイズに合わせると座標を考えやすいです。

## 20. 背景とラベルの描画

Screen 側でよく触るメソッドは次の 3 つです。

1. `render(...)`
2. `renderBg(...)`
3. `renderLabels(...)`

役割はこう分けると整理しやすいです。

- `render(...)`: 画面全体の描画の入口
- `renderBg(...)`: 背景画像や進行バー
- `renderLabels(...)`: タイトルや文字

### 背景描画の基本形

```java
@Override
protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    int x = this.leftPos;
    int y = this.topPos;

    guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
}
```

ここで `leftPos` と `topPos` を使うのは、GUI が画面中央へ配置された位置を基準にするためです。

### ラベル描画の考え方

タイトルやラベルは、背景画像そのものではなく文字として描きます。

`renderLabels(...)` を明示的に override しなくても動くことはありますが、表示位置を細かく調整したいときは使うと整理しやすいです。

```java
@Override
protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
}
```

## 21. 進行バーや燃焼バーの見た目を作る

機械 GUI らしさは、背景の上に小さな部品を重ねていくと出しやすいです。

たとえば次のようなものです。

1. 燃焼バー
2. 進行バー
3. 稼働中ランプ
4. 数値テキスト

### 進行バーの基本手順

1. 背景画像の中にバー用の絵を用意する
2. Menu から現在値を取得する
3. 最大値に対する割合を計算する
4. `blit(...)` で必要なぶんだけ描く

たとえば横向きのバーなら、次のようなイメージです。

```java
@Override
protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    int x = this.leftPos;
    int y = this.topPos;

    guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

    int progressWidth = this.menu.getScaledProgress();
    if (progressWidth > 0) {
        guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 0, progressWidth, 16);
    }
}
```

このとき大事なのは、進行バーの値計算をできるだけ Menu 側へ寄せることです。

つまり役割はこうです。

- Menu: 現在値と最大値から描画用の長さを返す
- Screen: 返ってきた長さで見た目を描く

## 22. ボタンや入力欄などの機能は `init()` から足す

背景だけでは足りなくなってきたら、次は GUI 部品を追加します。

よく使うのは次のような部品です。

1. `Button`
2. `EditBox`
3. チェック用の切り替え部品
4. 独自描画の小さなクリック領域

これらは Screen の `init()` で追加することが多いです。

### ボタン追加の最小イメージ

```java
@Override
protected void init() {
    super.init();

    this.addRenderableWidget(
        Button.builder(Component.literal("Start"), button -> {
            this.onStartButtonPressed();
        }).bounds(this.leftPos + 120, this.topPos + 20, 40, 20).build()
    );
}
```

### なぜ `init()` で置くのか

`init()` が呼ばれる時点では、`leftPos` と `topPos` が確定しています。

そのため、背景に対する相対位置でボタンを置きやすくなります。

## 23. ボタンを押したときに何を変えるか

ここで初心者が混乱しやすい点があります。

**見た目の Screen から、サーバーの中身を直接変えるわけではない** という点です。

たとえば「開始ボタン」を押したいとき、本当に変えたいのは次のどれかです。

1. BlockEntity の状態
2. Menu が持つ操作状態
3. サーバーへ送る操作要求

つまり、ボタンはきっかけであって、実際の中身変更はサーバー側へ渡す必要があります。

### 最初の段階で安全な考え方

まずは次の順で考えると整理しやすいです。

1. ボタンを押したら Screen 側で反応する
2. 次に Menu や別の通信手段でサーバーへ知らせる
3. サーバー側で BlockEntity の状態を変える
4. 変化した値を Menu 経由で Screen が表示する

この流れを崩して、Screen だけで状態変更まで完結させようとすると、シングルでは動いても同期で困りやすいです。

## 24. Menu と Screen と BlockEntity の結び付け方

ここが GUI 全体でいちばん大事な部分です。

結び付けは、次の順でつながります。

1. Block が右クリックを受ける
2. BlockEntity を取得する
3. BlockEntity が `MenuProvider` として Menu を作る
4. `MenuType` に応じて Screen が作られる
5. Screen が Menu の値を見て描画する

図にすると、次の流れです。

`Block -> BlockEntity(MenuProvider) -> MenuType -> Menu -> Screen`

### 実際にどこで結び付くのか

#### 1. Block と BlockEntity の結び付き

Block の `newBlockEntity(...)` で、そのブロック専用の BlockEntity を返します。

#### 2. BlockEntity と Menu の結び付き

BlockEntity の `createMenu(...)` が、開く Menu を決めます。

#### 3. Menu と Screen の結び付き

クライアント側の `RegisterMenuScreensEvent` で、`MenuType` と `Screen` を登録します。

#### 4. Screen と見た目の結び付き

Screen の `renderBg(...)` や `init()` で、背景、進行バー、ボタンを組み立てます。

## 25. 値の同期は Menu を通す

見た目と中身を結び付けるとき、初心者向けには「Screen は Menu を通して値を見る」と覚えるのが安全です。

たとえば進行度を表示したいなら、次の流れにします。

1. BlockEntity が本当の値を持つ
2. Menu がその値を受け取る
3. Screen が Menu の getter を読む

### よくある形

```java
public int getScaledProgress() {
    int progress = this.containerData.get(0);
    int maxProgress = this.containerData.get(1);

    if (progress == 0 || maxProgress == 0) {
        return 0;
    }

    return progress * 24 / maxProgress;
}
```

このように、Screen が直接 BlockEntity を触るより、Menu が中継役になる形の方が責務が分かれます。

## 26. 画面側でやってよいことと、やらない方がよいこと

### Screen 側でやってよいこと

1. 背景を描く
2. ラベルを描く
3. バーを描く
4. ボタンを置く
5. マウスホバー時の説明を出す

### Screen 側でやらない方がよいこと

1. BlockEntity の本体データを直接変更する
2. 保存処理を持つ
3. サーバー専用ロジックを持つ
4. スロット定義を持つ

見た目クラスに処理本体まで詰め込まないことが、後で読みやすくする一番のコツです。

## 27. この先に GUI を育てる順番

今の炉 GUI をこれから広げるなら、次の順で進めると安全です。

1. 背景画像を表示する
2. タイトル位置を整える
3. 進行バーを 1 本出す
4. 稼働中かどうかを文字かランプで見せる
5. ボタンを 1 個足す
6. 入力や設定変更をサーバー側へ渡す仕組みを足す
7. 最後に複数スロットや Shift クリック移動へ進む

最初から全部盛りにするより、1 つずつ通す方が原因と結果を追いやすいです。

## 28. この段階で確認したいこと

見た目と機能を足したら、次の順で確認すると切り分けしやすいです。

1. GUI が開くか
2. 背景位置がずれていないか
3. タイトルやラベルが重なっていないか
4. 進行バーが値に応じて伸びるか
5. ボタンを押したときにクラッシュしないか
6. シングルプレイでも値が戻ったり食い違ったりしないか

ここまで通れば、「GUI が開く段階」から「GUI を使って機械を操作する段階」へ進めます。
