Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'texture_utils.ps1')

function New-CrushedRawOreBaseTexture {
    param(
        [string]$OutputPath
    )

    # 粉砕原石: 角ばった欠片が数個あるシルエット（ダストの山とは別形状）。
    # 記号は必ず $colorBySymbol に登録する（未定義記号は透明になる）。
    $pixelMap = @(
        '................',
        '................',
        '......EE88......',
        '.....EE8866.....',
        '....55EE88B555..',
        '...55EE88668855.',
        '..55EE886E6E8855',
        '..55886E886E6655',
        '.55EEA66E8866855',
        '.55EE6E8886E6655',
        '..55EE886E6E5555',
        '...5555EE6E5555.',
        '....55555555....',
        '................',
        '................',
        '................'
    )

    $colorBySymbol = @{
        '.' = $null
        '5' = '#525252'
        '6' = '#616161'
        '8' = '#888788'
        'E' = '#6E6D6D'
        'A' = '#A6A6A6'
        'B' = '#B5B5B5'
    }

    $bitmap = New-PixelMapBitmap -PixelMap $pixelMap -ColorBySymbol $colorBySymbol

    try {
        Save-BitmapPng -Bitmap $bitmap -OutputPath $OutputPath
        Write-Output "created base: $OutputPath"
    }
    finally {
        $bitmap.Dispose()
    }
}

$textureDirectory = Join-Path $PSScriptRoot '..\src\main\resources\assets\cobblestonexxcompressed\textures\item\crushed_raw_ore'
$baseTexturePath = Join-Path $textureDirectory 'crushed_raw_ore.png'
$palettePath = Join-Path $PSScriptRoot 'configs\OreColors.png'
$configPath = Join-Path $PSScriptRoot 'configs\crushed_raw_ore_texture_config.psd1'
$copperAccentConfigPath = Join-Path $PSScriptRoot 'configs\crushed_raw_ore_copper_accents.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'
$copperOutputPath = Join-Path $textureDirectory 'crushed_raw_copper.png'

# index 0: 基準灰（TierColors と同じ）、index 1: raw_copper の代表色。
New-OreColorsPalette -OutputPath $palettePath -PaletteColors @(
    '#B3B1AF'
    '#C4704A'
)

New-CrushedRawOreBaseTexture -OutputPath $baseTexturePath
Repair-ItemTextureBase -BaseTexturePath $baseTexturePath
& $sharedScriptPath -ConfigPath $configPath

$copperAccents = Import-PowerShellDataFile -Path $copperAccentConfigPath
Apply-TextureAccentPixels -TexturePath $copperOutputPath -AccentPixels $copperAccents
