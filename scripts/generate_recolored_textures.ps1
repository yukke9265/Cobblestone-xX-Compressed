param(
    [Parameter(Mandatory = $true)]
    [string]$ConfigPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

function Get-Luminance {
    param(
        [System.Drawing.Color]$Color
    )

    return (0.2126 * $Color.R) + (0.7152 * $Color.G) + (0.0722 * $Color.B)
}

function ConvertTo-ByteValue {
    param(
        [double]$Value
    )

    if ($Value -lt 0) {
        return 0
    }

    if ($Value -gt 255) {
        return 255
    }

    return [int][Math]::Round($Value)
}

function New-TintedColor {
    param(
        [System.Drawing.Color]$SourceColor,
        [System.Drawing.Color]$ReferenceColor,
        [System.Drawing.Color]$TargetColor
    )

    # Keep fully transparent pixels unchanged.
    if ($SourceColor.A -eq 0) {
        return [System.Drawing.Color]::FromArgb(0, 255, 255, 255)
    }

    # Keep the faint highlight pixel unchanged for a more stable result.
    if ($SourceColor.A -lt 16) {
        return $SourceColor
    }

    $referenceLuminance = [Math]::Max((Get-Luminance $ReferenceColor), 1.0)
    $sourceLuminance = Get-Luminance $SourceColor
    $brightnessRatio = $sourceLuminance / $referenceLuminance

    $newRed = ConvertTo-ByteValue ($TargetColor.R * $brightnessRatio)
    $newGreen = ConvertTo-ByteValue ($TargetColor.G * $brightnessRatio)
    $newBlue = ConvertTo-ByteValue ($TargetColor.B * $brightnessRatio)

    return [System.Drawing.Color]::FromArgb($SourceColor.A, $newRed, $newGreen, $newBlue)
}

function Get-PaletteColor {
    param(
        [System.Drawing.Bitmap]$PaletteBitmap,
        [int]$X
    )

    $paletteColor = $PaletteBitmap.GetPixel($X, 0)

    if ($paletteColor.A -eq 0) {
        throw "Palette pixel x=$X is transparent. Check the top row color order."
    }

    return $paletteColor
}

function Resolve-ConfigPath {
    param(
        [string]$Path,
        [string]$ScriptRootPath
    )

    if ([System.IO.Path]::IsPathRooted($Path)) {
        return $Path
    }

    return [System.IO.Path]::GetFullPath((Join-Path $ScriptRootPath $Path))
}

$resolvedConfigPath = Resolve-ConfigPath -Path $ConfigPath -ScriptRootPath $PSScriptRoot
$configDirectory = Split-Path -Parent $resolvedConfigPath

if (-not (Test-Path $resolvedConfigPath)) {
    throw "Config file not found: $resolvedConfigPath"
}

$config = Import-PowerShellDataFile -Path $resolvedConfigPath
$textureDirectory = Resolve-ConfigPath -Path $config.TextureDirectory -ScriptRootPath $configDirectory
$baseTexturePath = Join-Path $textureDirectory $config.BaseTextureFileName
$paletteTexturePath = $null
$variantOutputNames = $config.VariantOutputNames
$referencePaletteIndex = $config.ReferencePaletteIndex
$variantPaletteIndices = $null

if ($config.ContainsKey('PaletteTexturePath')) {
    $paletteTexturePath = Resolve-ConfigPath -Path $config.PaletteTexturePath -ScriptRootPath $configDirectory
}
else {
    $paletteTexturePath = Join-Path $textureDirectory $config.PaletteTextureFileName
}

if ($config.ContainsKey('VariantPaletteIndices')) {
    $variantPaletteIndices = $config.VariantPaletteIndices
}

if (-not (Test-Path $baseTexturePath)) {
    throw "Base texture not found: $baseTexturePath"
}

if (-not (Test-Path $paletteTexturePath)) {
    throw "Palette texture not found: $paletteTexturePath"
}

$baseBitmapFileHandle = [System.Drawing.Bitmap]::FromFile($baseTexturePath)
$paletteBitmapFileHandle = [System.Drawing.Bitmap]::FromFile($paletteTexturePath)

try {
    # Clone the images into memory so the source files are no longer locked.
    # This allows regenerating a texture into the same path as the base image.
    $baseBitmap = New-Object System.Drawing.Bitmap($baseBitmapFileHandle)
    $paletteBitmap = New-Object System.Drawing.Bitmap($paletteBitmapFileHandle)
}
finally {
    $baseBitmapFileHandle.Dispose()
    $paletteBitmapFileHandle.Dispose()
}

try {
    $requiredPaletteWidth = $variantOutputNames.Count + $referencePaletteIndex + 1

    if ($null -ne $variantPaletteIndices) {
        if ($variantPaletteIndices.Count -ne $variantOutputNames.Count) {
            throw "VariantPaletteIndices count must match VariantOutputNames count."
        }

        $requiredPaletteWidth = ([int]($variantPaletteIndices | Measure-Object -Maximum).Maximum) + 1
    }

    if ($paletteBitmap.Width -lt $requiredPaletteWidth) {
        throw "Palette texture is too narrow. It needs at least $requiredPaletteWidth columns."
    }

    # Read the base color from the configured top-row index.
    # Each variant keeps the source brightness ratio to preserve shading.
    $referenceColor = Get-PaletteColor -PaletteBitmap $paletteBitmap -X $referencePaletteIndex

    for ($variantIndex = 0; $variantIndex -lt $variantOutputNames.Count; $variantIndex++) {
        $paletteX = $referencePaletteIndex + $variantIndex + 1

        if ($null -ne $variantPaletteIndices) {
            $paletteX = [int]$variantPaletteIndices[$variantIndex]
        }

        $targetColor = Get-PaletteColor -PaletteBitmap $paletteBitmap -X $paletteX
        $outputFileName = $variantOutputNames[$variantIndex] + '.png'
        $outputPath = Join-Path $textureDirectory $outputFileName
        $resolvedOutputPath = [System.IO.Path]::GetFullPath($outputPath)
        $resolvedBaseTexturePath = [System.IO.Path]::GetFullPath($baseTexturePath)

        $outputBitmap = New-Object System.Drawing.Bitmap($baseBitmap.Width, $baseBitmap.Height)

        try {
            for ($y = 0; $y -lt $baseBitmap.Height; $y++) {
                for ($x = 0; $x -lt $baseBitmap.Width; $x++) {
                    $sourceColor = $baseBitmap.GetPixel($x, $y)
                    $generatedColor = New-TintedColor -SourceColor $sourceColor -ReferenceColor $referenceColor -TargetColor $targetColor
                    $outputBitmap.SetPixel($x, $y, $generatedColor)
                }
            }

            if ($resolvedOutputPath -eq $resolvedBaseTexturePath) {
                $temporaryOutputPath = $outputPath + '.tmp'
                $outputBitmap.Save($temporaryOutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
                Remove-Item -Path $outputPath -Force
                Move-Item -Path $temporaryOutputPath -Destination $outputPath
            }
            else {
                $outputBitmap.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
            }

            Write-Output "generated: $outputFileName"
        }
        finally {
            $outputBitmap.Dispose()
        }
    }
}
finally {
    $baseBitmap.Dispose()
    $paletteBitmap.Dispose()
}