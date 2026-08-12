param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [ValidateSet('Mid', 'Bright', 'Highlight', 'List')]
    [string]$Mode = 'List',

    [int]$TopCount = 10
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$utilsPath = Join-Path $PSScriptRoot 'texture_utils.ps1'
. $utilsPath

$resolvedPath = $Path
if (-not [System.IO.Path]::IsPathRooted($Path)) {
    $resolvedPath = [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

if ($Mode -eq 'List') {
    Write-Output "=== dominant colors: $resolvedPath ==="
    Get-VanillaItemDominantColors -TexturePath $resolvedPath -TopCount $TopCount |
        ForEach-Object {
            $luminance = Get-TextureLuminance ([System.Drawing.Color]::FromArgb(255, $_.Red, $_.Green, $_.Blue))
            Write-Output ("{0} count={1} lum={2:N1}" -f $_.Hex, $_.Count, $luminance)
        }
    return
}

$sample = Get-ItemPaletteColor -TexturePath $resolvedPath -Mode $Mode -TopCount $TopCount
Write-Output "=== palette sample ($Mode): $resolvedPath ==="
Write-Output ("picked={0} count={1} lum={2:N1}" -f $sample.Hex, $sample.Count, $sample.Luminance)
