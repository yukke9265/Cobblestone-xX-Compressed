# ドキュメント一覧

このフォルダには、この Mod に機能を追加するときの作業手順をまとめています。

- [アイテム追加手順](./item-addition.md)
- [レシピ設定手順](./recipe-setup.md)
- [ブロック追加手順](./block-addition.md)
- [リソース配置ルール](./resource-layout.md)
- [開発時の確認手順](./development-checks.md)
- [タグ追加手順](./tag-setup.md)
- [Data Generator 導入手順](./datagen-introduction.md)
- [色違い・Tier違い素材の増やし方](./variant-content-guide.md)
- [多段圧縮アイテム設計ガイド](./multi-stage-compression-guide.md)
- [食料アイテム設定手順](./food-item-setup.md)
- [ブロック設定手順](./block-property-setup.md)
- [状態付きブロック手順](./block-state-guide.md)
- [機械系ブロック手順](./machine-block-guide.md)
- [機能ブロック用レシピ作成手順](./machine-recipe-guide.md)
- [JEI 機械レシピ連携手順](./jei-machine-recipe-guide.md)
- [発光圧縮ブロック実装例](./glowing-compressed-block-example.md)
- [BlockEntity 設定手順](./blockentity-setup.md)
- [NBT / CustomData 取扱手順](./nbt-guide.md)
- [ItemStack 基本ガイド](./itemstack-guide.md)
- [GUI 作成手順](./gui-guide.md)
- [インベントリ付き GUI 手順](./inventory-gui-guide.md)
- [Cobblestone Furnace のホッパー・アイテムパイプ対応手順](./hopper-item-pipe-guide.md)
- [クライアントとサーバーの基本](./client-server-basics.md)

現在のプロジェクトでは、アイテム登録の入口が主に次の 2 か所にあります。

- `src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java`
- `src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java`

このうち、**新しい通常アイテムを増やす作業は `ModItems.java` に寄せる**のが分かりやすいです。
`CobblestonexXCompressed.java` は Mod 全体の初期化やイベント登録の役割が中心なので、個別アイテムの定義まで大量に置くと追いにくくなります。

また、このプロジェクトではレシピ用のデータ生成コードがすでに入っています。
通常クラフトと機械レシピは `ModRecipeProvider` から `src/generated/resources/data/<modid>/recipe/` へ出すのが現在の標準です。

手書き JSON も Minecraft 自体は読めますが、既存コードに合わせるならまず datagen 側へ追加してください。

圧縮段階が増える設計にする場合は、登録名、レシピ、表示名の命名規則を先に決めてから増やすと管理しやすくなります。

食料アイテムを作る場合は、`Item.Properties().food(new FoodProperties.Builder()...)` を使うのが基本です。

ブロックの性質は、主に `BlockBehaviour.Properties.of()...` に設定をつないで決めます。

向きや点灯状態のようなブロック固有の状態を持たせる場合は、`BlockStateProperties` と独自 `Block` クラスを使う構成が必要になります。

さらに、ブロックごとの中身や進行状況を持たせる場合は `BlockEntity` を使う構成が必要になります。

永続データの保存や読み込みをしたい場合は、BlockEntity 側の `saveAdditional(...)` / `loadAdditional(...)` や、1.21.1 の ItemStack 側では `CustomData` も意識する必要があります。

機械の画面を作る場合は、サーバー側の `Menu` とクライアント側の `Screen` を分けて実装します。
