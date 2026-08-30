$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'
$textureUtilsPath = Join-Path $PSScriptRoot 'texture_utils.ps1'

. $textureUtilsPath

$configFiles = @(
    'cobblestone_armor_helmet_texture_config.psd1'
    'cobblestone_armor_chestplate_texture_config.psd1'
    'cobblestone_armor_leggings_texture_config.psd1'
    'cobblestone_armor_boots_texture_config.psd1'
    'cobblestone_armor_layer_1_texture_config.psd1'
    'cobblestone_armor_layer_2_texture_config.psd1'
)

foreach ($configFile in $configFiles) {
    $configPath = Join-Path $PSScriptRoot ('configs\' + $configFile)
    $configDirectory = Split-Path -Parent $configPath
    $config = Import-PowerShellDataFile -Path $configPath
    $textureDirectory = [System.IO.Path]::GetFullPath((Join-Path $configDirectory $config.TextureDirectory))
    $baseTexturePath = Join-Path $textureDirectory $config.BaseTextureFileName

    Repair-ItemTextureBase -BaseTexturePath $baseTexturePath
    & $sharedScriptPath -ConfigPath $configPath
}
