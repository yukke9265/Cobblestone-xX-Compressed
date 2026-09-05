$sideConfigPath = Join-Path $PSScriptRoot 'configs\cobblestone_drawer_texture_config.psd1'
$frontConfigPath = Join-Path $PSScriptRoot 'configs\cobblestone_drawer_front_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'

# 通常面と正面は手描きベースを別々に再色します。正面へ取っ手を自動合成しません。
# 同じセッションで共有スクリプトを2回呼ぶと2回目だけが残ることがあるため、別プロセスにします。
Write-Output 'recolor: cobblestone_drawer.png'
& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $sharedScriptPath -ConfigPath $sideConfigPath
if ($LASTEXITCODE -ne 0) {
    throw "side recolor failed: $LASTEXITCODE"
}

Write-Output 'recolor: cobblestone_drawer_front.png'
& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $sharedScriptPath -ConfigPath $frontConfigPath
if ($LASTEXITCODE -ne 0) {
    throw "front recolor failed: $LASTEXITCODE"
}
