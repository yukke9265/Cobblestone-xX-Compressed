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

# 色ソースは1か所に集約。パレットは Get-ItemPaletteColor -Mode Bright で自動取得。
$oreSourcePaths = [ordered]@{
    copper   = 'D:\1.21.1\assets\minecraft\textures\item\raw_copper.png'
    gold     = 'D:\1.21.1\assets\minecraft\textures\item\raw_gold.png'
    iron     = 'D:\1.21.1\assets\minecraft\textures\item\raw_iron.png'
    lead     = 'D:\Mekanism\src\main\resources\assets\mekanism\textures\item\raw_lead.png'
    osmium   = 'D:\Mekanism\src\main\resources\assets\mekanism\textures\item\raw_osmium.png'
    tin      = 'D:\Mekanism\src\main\resources\assets\mekanism\textures\item\raw_tin.png'
    uranium  = 'D:\Mekanism\src\main\resources\assets\mekanism\textures\item\raw_uranium.png'
    naquadah = 'D:\Mekanism-Extras\src\main\resources\assets\mekanism_extras\textures\item\raw_naquadah.png'
}

$paletteColors = @('#B3B1AF')
foreach ($oreName in $oreSourcePaths.Keys) {
    $sourcePath = $oreSourcePaths[$oreName]
    if (-not (Test-Path $sourcePath)) {
        throw "Source texture not found for ${oreName}: $sourcePath"
    }

    $sample = Get-ItemPaletteColor -TexturePath $sourcePath -Mode Bright
    Write-Output ("sampled ${oreName}: $($sample.Hex) lum=$($sample.Luminance)")
    $paletteColors += $sample.Hex
}

New-OreColorsPalette -OutputPath $palettePath -PaletteColors $paletteColors

New-CrushedRawOreBaseTexture -OutputPath $baseTexturePath
Repair-ItemTextureBase -BaseTexturePath $baseTexturePath
& $sharedScriptPath -ConfigPath $configPath

$copperAccents = Import-PowerShellDataFile -Path $copperAccentConfigPath
Apply-TextureAccentPixels -TexturePath $copperOutputPath -AccentPixels $copperAccents

$brightnessPairs = @()
foreach ($oreName in $oreSourcePaths.Keys) {
    $brightnessPairs += @{
        Name = "crushed_raw_$oreName"
        ReferencePath = $oreSourcePaths[$oreName]
        OutputPath = Join-Path $textureDirectory "crushed_raw_$oreName.png"
    }
}

$comparison = Compare-TextureBrightness -Pairs $brightnessPairs
Write-Output '=== brightness compare ==='
foreach ($result in $comparison.Results) {
    $status = if ($result.Warnings) { "WARN($($result.Warnings))" } else { 'OK' }
    Write-Output ("[{0}] {1} ref={2} out={3} ratio={4} light={5}" -f `
        $status, $result.Name, $result.ReferenceAverage, $result.OutputAverage, $result.AverageRatio, $result.OutputDarkMidLight)
}

if ($comparison.HasWarning) {
    Write-Output 'Brightness warning: consider brighter palette sampling or more base highlights.'
}
