
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
```

`runData` は専用の `run-data` ディレクトリを使う設定にしてあり、通常プレイ用の `run` と分離しています。

## リソース生成

データ生成の出力先は `src/generated/resources` です。

VS Code / Java 実行時に generated resources が見えないことがあるため、このプロジェクトでは `processResources` / `classes` / `runData` の後に `bin/main` へ同期する設定を入れています。

## 補足

- JEI は開発時の任意依存として設定しています。
- Flux Networks との long energy 互換は mod 側で対応しています。

## ライセンス

このリポジトリのコードは同梱の `LICENSE` に従います。
