param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [switch]$SummaryOnly
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$utilsPath = Join-Path $PSScriptRoot 'texture_utils.ps1'
. $utilsPath

$resolvedPath = $Path
if (-not [System.IO.Path]::IsPathRooted($Path)) {
    $resolvedPath = [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

Write-TextureDump -Path $resolvedPath -SummaryOnly:$SummaryOnly
