param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

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

Write-Output "=== dominant colors: $resolvedPath ==="
Get-VanillaItemDominantColors -TexturePath $resolvedPath -TopCount $TopCount |
    ForEach-Object {
        Write-Output ("{0} count={1}" -f $_.Hex, $_.Count)
    }
