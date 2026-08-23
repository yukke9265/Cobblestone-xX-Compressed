$configPath = Join-Path $PSScriptRoot 'configs\cobblestone_pickaxe_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'
$textureUtilsPath = Join-Path $PSScriptRoot 'texture_utils.ps1'

. $textureUtilsPath

$configDirectory = Split-Path -Parent $configPath
$config = Import-PowerShellDataFile -Path $configPath
$textureDirectory = [System.IO.Path]::GetFullPath((Join-Path $configDirectory $config.TextureDirectory))
$baseTexturePath = Join-Path $textureDirectory $config.BaseTextureFileName

# 頭・持ち手をまとめて再色するため、ベースを整えてから共通再色を呼びます。
Repair-ItemTextureBase -BaseTexturePath $baseTexturePath

& $sharedScriptPath -ConfigPath $configPath
