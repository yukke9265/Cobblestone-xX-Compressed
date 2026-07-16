# Phase 0 調査結果と挙動統一方針

## 決定

機械ごとの意図しない挙動差は、可能な限り共通化します。既存ワールド互換性を守るため、BlockEntity ID、inventory のスロット番号、NBT キー、recipe type、JEI category ID は維持します。一方で、処理停止条件や CP 不足時の進捗などの実行時挙動は、共通化の目的に照らして仕様変更してよいものとします。

仕様変更を行う場合は、対象機械、移行前後の挙動、確認項目をこのフォルダの仕様表と進捗記録に残します。これにより「意図しない回帰」と「意図した統一」を区別します。

## 統一する基準挙動

標準 powered machine の `PoweredMachineBlockEntityBase` を基準にします。

| 状況 | 統一後の挙動 |
| --- | --- |
| CP 不足のみ | `progress` を維持し、CP 回復後に続きから再開する。 |
| 入力が recipe に一致しない | `progress` を reset する。 |
| 出力スロットが詰まっている | `progress` を reset する。 |
| 機械を停止する | `progress` を reset する。 |
| recipe が見つからない | `progress` と `maxProgress` を reset する。 |

## 独自実装の共通化候補

| 優先度 | 対象 | 小さく始める作業 | 統一で得られるもの | 注意点 |
| --- | --- | --- | --- | --- |
| 1 | Crusher、Extreme Compressor、Powered Furnace、Centrifuge、Mixer | 単一スロット挿入・抽出用 `IItemHandler` helper | automation の入出力制約を同じ実装へ集約する | 公開するスロットと mode の意味は各機械に残す。 |
| 2 | Centrifuge | 複数出力を扱う powered machine 用の小さな export hook を追加し、基底クラスへ移行する | CP、progress、保存、同期、tick 骨格の重複を減らす | CP 不足で進捗を reset する現行挙動を、進捗維持へ意図的に変更する。 |
| 3 | Mixer | Centrifuge の結果を使って基底クラスへ移行する | 2 入力以外の CP / progress / 保存処理を統一する | 2 入力の照合、投入順、消費量は固有処理として残す。CP 不足時は進捗維持へ変更する。 |
| 4 | Laser Drill | Centrifuge と同じ複数出力用 hook を利用できるか Phase 0 仕様表で確認する | 同系統の tick 重複を減らす | 実装前に slot・recipe・搬出順を固定する。 |
| 対象外 | Cobblestone Furnace | 現状維持 | - | 燃焼式であり、powered machine 基底には入れない。 |

## Centrifuge と Mixer の仕様変更

両機械の独自 `tick()` は、CP 不足を「処理不能」として扱い、途中の `progress` を reset します。これは標準 powered machine と異なります。

移行時は、CP 不足だけでは `progress` を reset しない仕様へ統一します。入力不一致、出力詰まり、停止、recipe 不在で reset する条件は維持します。

この変更は、途中進捗を保持する方向の改善ですが、既存のプレイ感に影響するため、各機械で CP を途中で枯渇させて再充電する手動確認を必須とします。

## 実施順

1. Phase 1 で標準 powered machine の automation handler helper を追加する。
2. Centrifuge 用に、指定順で複数出力を搬出できる hook を設計する。
3. Centrifuge を基底クラスへ移行し、CP 不足時の進捗維持を確認する。
4. Mixer を移行し、2 入力の automation と CP 不足時の進捗維持を確認する。
5. Laser Drill の仕様表を作り、同じ hook の適用可否を判断する。