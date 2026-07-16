## Overview
このワークスペースは Minecraft Mod 開発用です。

## Environment
- Java 21
- Minecraft 1.21.1
- NeoForge 1.21.1

## Response Style
- 回答は日本語で、簡潔にまとめる。
- 先に結論を示し、必要な補足だけを短く足す。
- 余計な前置き、重複した説明、長い言い換えは避ける。
- ユーザーは Java 初心者として扱い、説明は順序立てて分かりやすくする。

## Comments and Code Style
- コード内コメントは日本語で書く。
- コメントは目的、前提、結果が分かる最小限の量を入れる。
- 難しい構文や過度な抽象化は避け、素直な制御フローと分かりやすい変数名を優先する。
- lambda、Stream API、複雑なジェネリクス、短縮記法は、明確な利点がある場合だけ使う。

## Implementation Policy
- 既存の流れ、命名、構造をできるだけ維持する。
- 変更は小さく分け、原因と結果が追いやすい差分にする。
- 役割ごとに小さなメソッドへ分け、入口と出口を明確にする。
- エラー時は、どの条件で失敗したかを明示する。

## NeoForge / Minecraft Notes
- 実装は Minecraft 1.21.1 と NeoForge 1.21.1 を基準にする。
- Java 21 は使ってよいが、読みやすさが上がる場合だけ新しい構文を使う。
- client 専用処理と server 共通処理は混ぜない。
- 登録、イベント購読、リソース配置、datagen 出力先の違いに注意する。
- 生成物は src/generated/resources に置く前提で扱う。

## Verification
- 変更後は、影響が最小の方法で確認する。
- 基本コマンドは gradlew.bat build、gradlew.bat runClient、gradlew.bat runData。
- 重い場合は、対象範囲のコンパイルや整合性確認を優先する。
- 実装方針の確認は、既存コードの流れに合わせることを優先する。
