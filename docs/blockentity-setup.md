# BlockEntity 設定手順

この文書では、このプロジェクトで BlockEntity を追加するときの基本的な流れをまとめます。

BlockEntity は、**ブロック 1 個ごとに中身を持たせたいとき**に使います。
たとえば次のような場合です。

- 中にアイテムを保存したい
- 処理進行度を持ちたい
- 毎 tick で何か動かしたい
- エネルギー量や燃焼時間を持ちたい

今のプロジェクトには状態付きブロックの例として [src/main/java/com/yukke9265/cobblestone_xx_compressed/RotatingBlock.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/RotatingBlock.java) と [src/main/java/com/yukke9265/cobblestone_xx_compressed/OnOffBlock.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/OnOffBlock.java) があります。
一方で、BlockEntity 本体はまだ入っていません。登録用の [src/main/java/com/yukke9265/cobblestone_xx_compressed/ModEntityType.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModEntityType.java) も、現時点では空です。

そのため、この文書では**今のプロジェクトに BlockEntity を最初から追加する前提**で説明します。

## 1. まず Block と BlockEntity の役割を分ける

最初にここを分けて考えると理解しやすいです。

### Block 側の役割

- ワールド上の見た目
- 向きや点灯状態などの BlockState
- 右クリック時の入口
- BlockEntity を作る入口

### BlockEntity 側の役割

- そのブロック固有の中身
- インベントリ
- 燃焼時間や進行度
- tick 処理
- 保存と読み込み

つまり、**見た目と状態は Block、内部データは BlockEntity** と分けるのが基本です。

## 2. BlockEntity が必要になる場面

普通のブロックや、向きだけ持つブロックなら BlockEntity は不要です。

たとえば次のようなものは Block だけで足ります。

- 単純な建材ブロック
- 発光するだけのブロック
- 向きだけ持つブロック
- ON/OFF を見た目だけで切り替えるブロック

逆に、次のようなものは BlockEntity を使うことが多いです。

- かまど風の処理ブロック
- 中にアイテムを入れられる機械
- 時間経過で動く装置
- 独自データを保存するブロック

## 3. 最低限必要になる要素

BlockEntity を使うブロックを 1 つ作る場合、最低限でも次の要素が必要です。

1. `BlockEntity` を継承したクラス
2. `BlockEntityType` の登録
3. `EntityBlock` を実装するブロッククラス
4. `newBlockEntity(...)` の実装

必要に応じて、さらに次も増えます。

1. tick 処理
2. 保存 / 読み込み
3. GUI
4. メニュー同期

## 4. このプロジェクトでの登録クラス

今のプロジェクトには [src/main/java/com/yukke9265/cobblestone_xx_compressed/ModEntityType.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModEntityType.java) がありますが、中身は空です。

この用途なら、実際には `EntityType` という名前より **`ModBlockEntities`** のようにした方が分かりやすいです。

ただし、今の流れに合わせるなら次のどちらかで進められます。

1. `ModEntityType.java` を BlockEntity 登録クラスとして使う
2. `ModBlockEntities.java` へ名前を分けて新設する

初心者には 2 の方が意味が明確なのでおすすめです。

## 5. BlockEntity クラスの最小形

まずは、中身をまだ持たない最小 BlockEntity を作ると分かりやすいです。

例:

```java
public class SimpleMachineBlockEntity extends BlockEntity {
    public SimpleMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIMPLE_MACHINE.get(), pos, state);
    }
}
```

### このコードで何をしているか

- `BlockEntity` を継承している
- 位置 `BlockPos` と状態 `BlockState` を受け取る
- どの `BlockEntityType` に属するかを `super(...)` に渡している

## 6. `BlockEntityType` の登録

次に、その BlockEntity を登録します。

例:

```java
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CobblestonexXCompressed.MODID);

    public static final Supplier<BlockEntityType<SimpleMachineBlockEntity>> SIMPLE_MACHINE =
        BLOCK_ENTITY_TYPES.register(
            "simple_machine",
            () -> BlockEntityType.Builder.of(
                SimpleMachineBlockEntity::new,
                ModBlocks.SIMPLE_MACHINE_BLOCK.get()
            ).build(null)
        );
}
```

### ここで大事なこと

- `Registries.BLOCK_ENTITY_TYPE` を使って登録する
- `Builder.of(...)` の中に BlockEntity のコンストラクタ参照を渡す
- どのブロックに対応する BlockEntity かもここで指定する

## 7. Mod 本体で登録する

`ModItems` や `ModBlocks` と同じく、イベントバスへ登録が必要です。

たとえば [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java) で、次のように登録します。

```java
ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
```

これを忘れると、Java クラスを書いてもゲームへ登録されません。

## 8. Block 側は `EntityBlock` を実装する

BlockEntity を持つブロックは、通常 `EntityBlock` を実装する形にします。

例:

```java
public class SimpleMachineBlock extends Block implements EntityBlock {
    public SimpleMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SimpleMachineBlockEntity(pos, state);
    }
}
```

### 何をしているか

- このブロックは BlockEntity を持つ、と宣言する
- 置かれたときにどの BlockEntity を作るかを `newBlockEntity(...)` で返す

## 9. `registerSimpleBlock(...)` ではなく独自ブロック登録にする

BlockEntity を持つブロックでは、`registerSimpleBlock(...)` よりも独自ブロッククラスを直接生成する形が分かりやすいです。

例:

```java
public static final DeferredBlock<Block> SIMPLE_MACHINE_BLOCK = BLOCKS.register(
    "simple_machine_block",
    () -> new SimpleMachineBlock(
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
    )
);
```

## 10. tick 処理を持たせたい場合

BlockEntity を使う理由の 1 つが tick 処理です。

その場合は BlockEntity 側に static tick メソッドを用意し、Block 側の `getTicker(...)` で返します。

考え方の流れは次の通りです。

1. BlockEntity 側に毎 tick 実行したい処理を書く
2. Block 側でその ticker を返す

最初は「数秒ごとに ON/OFF が切り替わるだけ」のような簡単な処理が分かりやすいです。

## 11. 保存したいデータがある場合

BlockEntity の大きな役割は、ブロックごとのデータ保存です。

たとえば次のような値を持てます。

- `progress`
- `burnTime`
- `storedEnergy`
- `itemCount`

これらは NBT に保存 / 読み込みする形で扱います。

最初は次の流れだけ理解すれば十分です。

1. フィールドを用意する
2. 保存メソッドで書き出す
3. 読み込みメソッドで戻す

## 12. GUI は BlockEntity 導入後でよい

BlockEntity を作ると、つい GUI まで一気に進めたくなりますが、最初は分けた方が安全です。

おすすめ順は次の通りです。

1. BlockEntity を作る
2. 正しく生成されることを確認する
3. データを 1 つだけ保存できるようにする
4. tick 処理を足す
5. 最後に GUI へ進む

## 13. このプロジェクトでの最初の題材

今の構成なら、最初の BlockEntity 題材としては次のようなものが向いています。

1. 右クリックで ON/OFF が変わるだけの簡易機械
2. 一定時間だけ点灯するブロック
3. アイテムを 1 個だけ保持する簡易保管ブロック

本格的な独自かまどや複雑な機械は、そのあとで十分です。

## 14. よくあるミス

### BlockEntity クラスだけ作って登録していない

`BlockEntityType` の登録とイベントバス登録がないと動きません。

### Block 側で `EntityBlock` を実装していない

BlockEntity を持つつもりでも、入口がなければ生成されません。

### `newBlockEntity(...)` を実装していない

このメソッドを返さないと、ブロック配置時に BlockEntity が作られません。

### いきなり GUI から作り始める

BlockEntity の基礎がまだ固まっていない段階では、切り分けが難しくなります。

## 15. 確認順

BlockEntity を追加したら、次の順で確認すると分かりやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. ブロックを置いたときにエラーが出ないか
2. 右クリックや tick 処理が動くか
3. ワールド再読み込み後もデータが残るか

## 16. この docs を読んだあとに進むなら

次の段階としては、次の順がおすすめです。

1. `ModBlockEntities` を作る
2. 中身を持たない最小 BlockEntity を 1 つ作る
3. `EntityBlock` な独自ブロックを 1 つ作る
4. そのあとで tick や GUI を足す

この順なら、今のプロジェクトの理解を崩さずに BlockEntity へ進めます。
