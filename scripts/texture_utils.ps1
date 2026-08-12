Set-StrictMode -Version Latest

Add-Type -AssemblyName System.Drawing

function ConvertTo-DrawingColor {
    param(
        [string]$Hex
    )

    $hex = $Hex.TrimStart('#')
    $red = [Convert]::ToInt32($hex.Substring(0, 2), 16)
    $green = [Convert]::ToInt32($hex.Substring(2, 2), 16)
    $blue = [Convert]::ToInt32($hex.Substring(4, 2), 16)

    return [System.Drawing.Color]::FromArgb(255, $red, $green, $blue)
}

function Test-IsOuterOutlinePixel {
    param(
        [System.Drawing.Bitmap]$Bitmap,
        [int]$X,
        [int]$Y
    )

    $center = $Bitmap.GetPixel($X, $Y)
    if ($center.A -eq 0) {
        return $false
    }

    foreach ($offset in @(@(-1, 0), @(1, 0), @(0, -1), @(0, 1))) {
        $neighborX = $X + $offset[0]
        $neighborY = $Y + $offset[1]

        if ($neighborX -lt 0 -or $neighborY -lt 0 -or $neighborX -ge $Bitmap.Width -or $neighborY -ge $Bitmap.Height) {
            return $true
        }

        $neighbor = $Bitmap.GetPixel($neighborX, $neighborY)
        if ($neighbor.A -eq 0) {
            return $true
        }
    }

    return $false
}

function Test-PixelMapSymbols {
    param(
        [string[]]$PixelMap,
        [hashtable]$ColorBySymbol,
        [int]$Width = 16,
        [int]$Height = 16
    )

    $unknownSymbols = New-Object 'System.Collections.Generic.HashSet[string]'

    for ($y = 0; $y -lt $PixelMap.Count; $y++) {
        $row = $PixelMap[$y]

        if ($row.Length -ne $Width) {
            throw "Pixel map row $y has length $($row.Length); expected $Width."
        }

        for ($x = 0; $x -lt $Width; $x++) {
            $symbol = $row.Substring($x, 1)

            if (-not $ColorBySymbol.ContainsKey($symbol)) {
                [void]$unknownSymbols.Add($symbol)
            }
        }
    }

    if ($PixelMap.Count -ne $Height) {
        throw "Pixel map has $($PixelMap.Count) rows; expected $Height."
    }

    if ($unknownSymbols.Count -gt 0) {
        $symbolList = ($unknownSymbols | Sort-Object) -join ', '
        throw "Pixel map uses undefined symbols: $symbolList. Add them to ColorBySymbol or fix the map."
    }
}

function New-PixelMapBitmap {
    param(
        [string[]]$PixelMap,
        [hashtable]$ColorBySymbol,
        [int]$Width = 16,
        [int]$Height = 16
    )

    Test-PixelMapSymbols -PixelMap $PixelMap -ColorBySymbol $ColorBySymbol -Width $Width -Height $Height

    $bitmap = New-Object System.Drawing.Bitmap($Width, $Height)

    for ($y = 0; $y -lt $Height; $y++) {
        $row = $PixelMap[$y]

        for ($x = 0; $x -lt $Width; $x++) {
            $symbol = $row.Substring($x, 1)
            $hex = $ColorBySymbol[$symbol]

            if ($null -eq $hex) {
                $bitmap.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 255, 255, 255))
            }
            else {
                $bitmap.SetPixel($x, $y, (ConvertTo-DrawingColor -Hex $hex))
            }
        }
    }

    return $bitmap
}

function Save-BitmapPng {
    param(
        [System.Drawing.Bitmap]$Bitmap,
        [string]$OutputPath
    )

    $outputDirectory = Split-Path -Parent $OutputPath
    if (-not (Test-Path $outputDirectory)) {
        New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
    }

    $Bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
}

function Repair-ItemTextureBase {
    param(
        [string]$BaseTexturePath,
        [string]$OutlineColor = '#525252'
    )

    $bitmapFileHandle = [System.Drawing.Bitmap]::FromFile($BaseTexturePath)
    $bitmap = New-Object System.Drawing.Bitmap($bitmapFileHandle)
    $bitmapFileHandle.Dispose()

    try {
        for ($y = 0; $y -lt $bitmap.Height; $y++) {
            for ($x = 0; $x -lt $bitmap.Width; $x++) {
                $color = $bitmap.GetPixel($x, $y)

                # 白背景や薄いノイズは透明にする。
                if ($color.A -lt 32 -or ($color.R -ge 240 -and $color.G -ge 240 -and $color.B -ge 240)) {
                    $bitmap.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 255, 255, 255))
                }
            }
        }

        for ($y = 0; $y -lt $bitmap.Height; $y++) {
            for ($x = 0; $x -lt $bitmap.Width; $x++) {
                if (Test-IsOuterOutlinePixel -Bitmap $bitmap -X $x -Y $y) {
                    $bitmap.SetPixel($x, $y, (ConvertTo-DrawingColor -Hex $OutlineColor))
                }
            }
        }

        Save-BitmapPng -Bitmap $bitmap -OutputPath $BaseTexturePath
        Write-Output "repaired base: $BaseTexturePath"
    }
    finally {
        $bitmap.Dispose()
    }
}

function Apply-TextureAccentPixels {
    param(
        [string]$TexturePath,
        [object[]]$AccentPixels
    )

    $bitmapFileHandle = [System.Drawing.Bitmap]::FromFile($TexturePath)
    $bitmap = New-Object System.Drawing.Bitmap($bitmapFileHandle)
    $bitmapFileHandle.Dispose()

    try {
        foreach ($accent in $AccentPixels) {
            $x = [int]$accent.X
            $y = [int]$accent.Y
            $color = $bitmap.GetPixel($x, $y)

            if ($color.A -eq 0) {
                continue
            }

            if (Test-IsOuterOutlinePixel -Bitmap $bitmap -X $x -Y $y) {
                continue
            }

            $bitmap.SetPixel($x, $y, (ConvertTo-DrawingColor -Hex $accent.Color))
        }

        Save-BitmapPng -Bitmap $bitmap -OutputPath $TexturePath
        Write-Output "applied accent pixels: $TexturePath"
    }
    finally {
        $bitmap.Dispose()
    }
}

function Get-TextureOpaqueBounds {
    param(
        [System.Drawing.Bitmap]$Bitmap
    )

    $minX = $Bitmap.Width
    $minY = $Bitmap.Height
    $maxX = -1
    $maxY = -1
    $opaqueCount = 0
    $internalHoleCount = 0

    for ($y = 0; $y -lt $Bitmap.Height; $y++) {
        for ($x = 0; $x -lt $Bitmap.Width; $x++) {
            $color = $Bitmap.GetPixel($x, $y)

            if ($color.A -eq 0) {
                continue
            }

            $opaqueCount++

            if ($x -lt $minX) { $minX = $x }
            if ($y -lt $minY) { $minY = $y }
            if ($x -gt $maxX) { $maxX = $x }
            if ($y -gt $maxY) { $maxY = $y }

            if (-not (Test-IsOuterOutlinePixel -Bitmap $Bitmap -X $x -Y $y)) {
                continue
            }

            # 外枠ピクセルの隣に透明がある = 正常。内側の透明穴は bbox 内で外枠以外の透明。
        }
    }

    # 内側の透明穴: bbox 内で上下左右すべてが不透明な透明ピクセル
    if ($maxX -ge 0) {
        for ($y = $minY; $y -le $maxY; $y++) {
            for ($x = $minX; $x -le $maxX; $x++) {
                $color = $Bitmap.GetPixel($x, $y)

                if ($color.A -ne 0) {
                    continue
                }

                $hasOpaqueTop = $false
                $hasOpaqueRight = $false
                $hasOpaqueBottom = $false
                $hasOpaqueLeft = $false

                if ($y -gt $minY -and $Bitmap.GetPixel($x, $y - 1).A -ne 0) {
                    $hasOpaqueTop = $true
                }

                if ($x -lt $maxX -and $Bitmap.GetPixel($x + 1, $y).A -ne 0) {
                    $hasOpaqueRight = $true
                }

                if ($y -lt $maxY -and $Bitmap.GetPixel($x, $y + 1).A -ne 0) {
                    $hasOpaqueBottom = $true
                }

                if ($x -gt $minX -and $Bitmap.GetPixel($x - 1, $y).A -ne 0) {
                    $hasOpaqueLeft = $true
                }

                if ($hasOpaqueTop -and $hasOpaqueRight -and $hasOpaqueBottom -and $hasOpaqueLeft) {
                    $internalHoleCount++
                }
            }
        }
    }

    return [PSCustomObject]@{
        MinX = $minX
        MinY = $minY
        MaxX = $maxX
        MaxY = $maxY
        Width = if ($maxX -ge 0) { $maxX - $minX + 1 } else { 0 }
        Height = if ($maxY -ge 0) { $maxY - $minY + 1 } else { 0 }
        OpaqueCount = $opaqueCount
        InternalHoleCount = $internalHoleCount
    }
}

function Write-TextureDump {
    param(
        [string]$Path,
        [switch]$SummaryOnly
    )

    if (-not (Test-Path $Path)) {
        throw "Texture not found: $Path"
    }

    $bitmapFileHandle = [System.Drawing.Bitmap]::FromFile($Path)
    $bitmap = New-Object System.Drawing.Bitmap($bitmapFileHandle)
    $bitmapFileHandle.Dispose()

    try {
        $bounds = Get-TextureOpaqueBounds -Bitmap $bitmap

        Write-Output "=== $Path ==="

        if (-not $SummaryOnly) {
            for ($y = 0; $y -lt $bitmap.Height; $y++) {
                $row = ''

                for ($x = 0; $x -lt $bitmap.Width; $x++) {
                    $color = $bitmap.GetPixel($x, $y)

                    if ($color.A -eq 0) {
                        $row += '.... '
                    }
                    else {
                        $row += ('{0:X2}{1:X2}{2:X2} ' -f $color.R, $color.G, $color.B)
                    }
                }

                Write-Output ('{0,2}: {1}' -f $y, $row)
            }
        }

        if ($bounds.MaxX -ge 0) {
            Write-Output ("bbox: ({0},{1})-({2},{3}) = {4}x{5}, opaque={6}, internal_holes={7}" -f `
                $bounds.MinX, $bounds.MinY, $bounds.MaxX, $bounds.MaxY, `
                $bounds.Width, $bounds.Height, $bounds.OpaqueCount, $bounds.InternalHoleCount)
        }
        else {
            Write-Output 'bbox: empty'
        }
    }
    finally {
        $bitmap.Dispose()
    }
}

function New-OreColorsPalette {
    param(
        [string]$OutputPath,
        [string[]]$PaletteColors
    )

    $bitmap = New-Object System.Drawing.Bitmap($PaletteColors.Count, 1)

    try {
        for ($index = 0; $index -lt $PaletteColors.Count; $index++) {
            $bitmap.SetPixel($index, 0, (ConvertTo-DrawingColor -Hex $PaletteColors[$index]))
        }

        Save-BitmapPng -Bitmap $bitmap -OutputPath $OutputPath
        Write-Output "created palette: $OutputPath"
    }
    finally {
        $bitmap.Dispose()
    }
}

function Get-VanillaItemDominantColors {
    param(
        [string]$TexturePath,
        [int]$TopCount = 10
    )

    if (-not (Test-Path $TexturePath)) {
        throw "Texture not found: $TexturePath"
    }

    $bitmapFileHandle = [System.Drawing.Bitmap]::FromFile($TexturePath)
    $bitmap = New-Object System.Drawing.Bitmap($bitmapFileHandle)
    $bitmapFileHandle.Dispose()

    $colors = @{}

    try {
        for ($y = 0; $y -lt $bitmap.Height; $y++) {
            for ($x = 0; $x -lt $bitmap.Width; $x++) {
                $color = $bitmap.GetPixel($x, $y)

                if ($color.A -le 200) {
                    continue
                }

                $key = '{0},{1},{2}' -f $color.R, $color.G, $color.B

                if ($colors.ContainsKey($key)) {
                    $colors[$key]++
                }
                else {
                    $colors[$key] = 1
                }
            }
        }
    }
    finally {
        $bitmap.Dispose()
    }

    return $colors.GetEnumerator() |
        Sort-Object Value -Descending |
        Select-Object -First $TopCount |
        ForEach-Object {
            $parts = $_.Key.Split(',')
            $red = [int]$parts[0]
            $green = [int]$parts[1]
            $blue = [int]$parts[2]

            [PSCustomObject]@{
                Red = $red
                Green = $green
                Blue = $blue
                Hex = '#{0:X2}{1:X2}{2:X2}' -f $red, $green, $blue
                Count = $_.Value
            }
        }
}
