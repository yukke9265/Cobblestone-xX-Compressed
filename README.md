
# Cobblestone xX Compressed

Minecraft 1.21.1 / NeoForge 1.21.1 向けの mod です。

丸石圧縮系アイテムと、それを材料や内部エネルギーとして使う機械を追加しています。現在の実装には、圧縮丸石系アイテム、丸石系の加工機、Cobblestone Powered Furnace、Cobblestone FE Generator などが含まれます。

## 開発環境

- Java 21
- Minecraft 1.21.1
- NeoForge 1.21.1

## 主な確認コマンド

```bat
gradlew.bat compileJava
gradlew.bat runClient
gradlew.bat runData
gradlew.bat build
```

配布用 jar は `build/libs` に出力されます。バージョン番号は `gradle.properties` の `mod_version` です。Cursor では **Terminal: Run Build Task**（Ctrl+Shift+B）でも同じ `build` を実行できます。

`runData` は専用の `run-data` ディレクトリを使う設定にしてあり、通常プレイ用の `run` と分離しています。

## リソース生成

データ生成の出力先は `src/generated/resources` です。

デバッグ起動は `bin/main` ではなく、Gradle の `build/classes/java/main` と `build/resources/main` を mod として読みます。`bin/main` を使うと、言語サーバーの出力が空のときに `is not a valid mod file` で落ちます。

## 補足

- JEI は開発時の任意依存として設定しています。
- Flux Networks との long energy 互換は mod 側で対応しています。

## ライセンス

このリポジトリのコードは同梱の `LICENSE` に従います。
