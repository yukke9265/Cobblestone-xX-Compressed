# NBT / CustomData 取扱手順

この文書では、このプロジェクトで NBT 系データをどう扱うかをまとめます。

Minecraft では昔から「NBT」という言い方がよく使われますが、Minecraft 1.21.1 / NeoForge 1.21.1 では、対象によって扱い方が少し分かれます。

- BlockEntity の永続保存: `CompoundTag` を使う
- ItemStack の追加データ: `DataComponents` と `CustomData` も意識する

そのため、この文書では **BlockEntity の保存** と **ItemStack の追加データ** を分けて説明します。

## 1. まず NBT で何を保存するのか

NBT で保存したくなる代表例は次のようなものです。

- 機械の進行度
- 燃焼時間
- 内部エネルギー量
- 独自フラグ
- アイテムごとのカスタム値
- 名前や説明とは別の内部状態

今のプロジェクトなら、たとえば [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java) に進行度や ON/OFF 関連の値を追加したくなったときが、最初の NBT 利用候補です。

## 2. `CompoundTag` が基本

NBT を Java 側で扱うときは、基本的に `CompoundTag` を使います。

これは「キーと値の入れ物」です。

たとえば次のようなイメージです。

```java
tag.putInt("progress", this.progress);
tag.putBoolean("is_active", this.isActive);
```

読み込む側では次のように取り出します。

```java
this.progress = tag.getInt("progress");
this.isActive = tag.getBoolean("is_active");
```

## 3. BlockEntity での基本形

BlockEntity の永続保存では、主に次の 2 つを使います。

1. `saveAdditional(CompoundTag tag, HolderLookup.Provider registries)`
2. `loadAdditional(CompoundTag tag, HolderLookup.Provider registries)`

NeoForge 1.21.x では、この `HolderLookup.Provider` 引数付きの形を使います。

## 4. BlockEntity 側の保存例

たとえば `progress` と `isActive` を持つ場合、次のように書けます。

```java
@Override
protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);

    tag.putInt("progress", this.progress);
    tag.putBoolean("is_active", this.isActive);
}
```

### ここで大事なこと

- 先に `super.saveAdditional(...)` を呼ぶ
- 自分の値を分かりやすいキー名で保存する
- キー名は途中で変えない方が安全

## 5. BlockEntity 側の読み込み例

読み込みは次のように書けます。

```java
@Override
protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);

    this.progress = tag.getInt("progress");
    this.isActive = tag.getBoolean("is_active");
}
```

### 注意

- 先に `super.loadAdditional(...)` を呼ぶ
- 保存側と同じキー名で読む
- キーが存在しない場合の初期値も意識する

`getInt(...)` や `getBoolean(...)` は、キーがなければ既定値寄りの結果になりますが、設計上「存在前提」なのか「なくてもよい」のかは自分で決めておく方が安全です。

## 6. 保存しただけでは不十分なことがある

BlockEntity の値を変えたとき、**ワールド保存対象として変更があったことを知らせる**必要があります。

その基本が `setChanged()` です。

### 例

```java
this.progress = this.progress + 1;
this.setChanged();
```

### なぜ必要か

これを呼ばないと、値は変わっていても保存対象として扱われず、ワールド再読み込み後に戻ることがあります。

## 7. 今のプロジェクトで最初に足しやすい例

今ある [src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestoneFurnaceBlockEntity.java) は、1 秒ごとに ON/OFF を切り替えるだけです。

ここへ最初に足すなら、たとえば次のような値が分かりやすいです。

- `tick_counter`
- `is_active`
- `stored_heat`

最初は整数 1 個か真偽値 1 個だけ保存するところから始めると理解しやすいです。

## 8. BlockEntity の保存サンプル全体像

たとえば次のような形です。

```java
public class CobblestoneFurnaceBlockEntity extends BaseBlockEntity {
    private int progress;
    private boolean isActive;

    public CobblestoneFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_FURNACE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putBoolean("is_active", this.isActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.isActive = tag.getBoolean("is_active");
    }
}
```

## 9. ItemStack 側は 1.21.1 で少し考え方が違う

昔の感覚では「アイテムにも NBT を直接付ける」と考えがちですが、1.21.1 では ItemStack 側は `DataComponents` を意識した方が整理しやすいです。

特に追加のカスタムデータを持たせたい場合は、`DataComponents.CUSTOM_DATA` と `CustomData` が関係します。

つまり、今のバージョンでは次の感覚で考えると分かりやすいです。

- BlockEntity 保存: `CompoundTag` で `saveAdditional(...)` / `loadAdditional(...)`
- ItemStack の追加データ: `CustomData` を通して扱う

## 10. ItemStack にカスタムデータを持たせる考え方

ItemStack 側では、`DataComponents.CUSTOM_DATA` に `CustomData` を載せる形が使えます。

たとえば「このアイテムが何段圧縮か」を持たせたいなら、内部的に次のようなキーを置く考え方ができます。

- `compression_level`
- `owner_name`
- `charge`

## 11. ItemStack 側の更新例

`CustomData.update(...)` を使うと、既存データを更新しやすいです。

```java
CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
    tag.putInt("compression_level", 3);
});
```

### この書き方の意味

- 対象 ItemStack の `CUSTOM_DATA` を更新する
- まだデータがなければ作る
- `CompoundTag` にキーを書き込む

## 12. ItemStack 側の固定セット例

まとめて値を入れたいなら、`CompoundTag` を作って `CustomData.set(...)` する形もあります。

```java
CompoundTag tag = new CompoundTag();
tag.putInt("compression_level", 3);
tag.putBoolean("glowing", true);

CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
```

## 13. ItemStack 側で読むときの考え方

読むときは、`CUSTOM_DATA` を取り出して中の `CompoundTag` を見る流れになります。

発想としては次の順です。

1. `CUSTOM_DATA` が付いているか確認する
2. 中のキーがあるか確認する
3. 必要な型で読む

この部分は、最初は「書き込み側を先に決める」方が混乱しにくいです。

## 14. `getPersistentData()` という選択肢

BlockEntity には `getPersistentData()` もあります。

これは「自分用のカスタム永続データ置き場」として使える `CompoundTag` です。

### 向いている場面

- 少量の独自メモを追加したいとき
- 既存保存処理を大きく増やしたくないとき

ただし、初心者にはまず `saveAdditional(...)` / `loadAdditional(...)` の流れを理解する方が分かりやすいです。
そのあとで補助的に使うのがおすすめです。

## 15. キー名の付け方

NBT / CustomData のキー名は、あとで見返して意味が分かる名前にします。

おすすめ:

- `progress`
- `burn_time`
- `is_active`
- `compression_level`

避けたい例:

- `a`
- `tmp1`
- `flag`

途中で名前を変えると古いワールドデータとの整合が崩れやすいので、最初に決めたらあまり変えない方が安全です。

## 16. よくあるミス

### `saveAdditional(...)` だけ書いて `loadAdditional(...)` を書いていない

保存できても再読込時に戻せません。

### `loadAdditional(...)` のキー名が保存側と違う

値が読み戻せず、既定値に見えることがあります。

### 値を変えたのに `setChanged()` を呼んでいない

ワールド再読み込み後に値が消えたように見える原因になります。

### BlockState で持つべきものまで NBT に入れてしまう

ON/OFF や向きのようにブロック状態として見た目に直結するものは、まず BlockState で持つべきかを考えます。

### ItemStack 側で 1.21.1 の `CustomData` を意識していない

古い感覚のまま「全部 NBT で直接」という理解だけだと、今の API と少しずれます。

## 17. 何を BlockState に持たせて、何を NBT に持たせるか

迷ったら、次のように切り分けると分かりやすいです。

### BlockState 向き

- 向き
- 点灯 / 消灯
- 開閉
- モデル切り替えに直結する状態

### BlockEntity NBT 向き

- 進行度
- エネルギー量
- 内部インベントリ
- 保存したい内部フラグ

つまり、**見た目に直結するものは BlockState、中身は BlockEntity NBT** が基本です。

## 18. このプロジェクトで最初に試すなら

初心者のうちは、まず次の順がおすすめです。

1. `CobblestoneFurnaceBlockEntity` に `progress` を 1 つ足す
2. `saveAdditional(...)` と `loadAdditional(...)` を実装する
3. tick 内で値を増やして `setChanged()` を呼ぶ
4. ワールドを開き直して値が残ることを確認する

この 1 回を通すと、NBT 保存の流れがかなり分かりやすくなります。

## 19. 確認順

NBT / CustomData を追加したら、次の順で確認すると切り分けしやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. 値が動作中に変わるか
2. ワールド再読み込み後も残るか
3. ItemStack 側ならスタック移動後も値が維持されるか
