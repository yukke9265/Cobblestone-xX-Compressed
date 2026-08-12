param(
    [string]$ReferencePath,
    [string]$OutputPath,
    [string]$Name,

    [double]$MinAverageRatio = 0.7,
    [double]$MinLightPct = 5.0,

    [switch]$FailOnWarning
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'texture_utils.ps1')

function Resolve-TexturePath {
    param([string]$Path)

    if ([System.IO.Path]::IsPathRooted($Path)) {
        return $Path
    }

    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

if ($ReferencePath -and $OutputPath) {
    $pairs = @(
        @{
            Name = if ($Name) { $Name } else { [System.IO.Path]::GetFileNameWithoutExtension($OutputPath) }
            ReferencePath = (Resolve-TexturePath $ReferencePath)
            OutputPath = (Resolve-TexturePath $OutputPath)
        }
    )
}
else {
    throw 'Specify -ReferencePath and -OutputPath.'
}

$comparison = Compare-TextureBrightness -Pairs $pairs -MinAverageRatio $MinAverageRatio -MinLightPct $MinLightPct

Write-Output '=== brightness compare (dark<85 / mid 85-170 / light>=170) ==='
Write-Output ("thresholds: avg_ratio>={0}, light_pct>={1}" -f $MinAverageRatio, $MinLightPct)
Write-Output ''

foreach ($result in $comparison.Results) {
    $status = if ($result.Warnings) { "WARN($($result.Warnings))" } else { 'OK' }
    Write-Output ("[{0}] {1}" -f $status, $result.Name)
    Write-Output ("  reference avg={0}  dark/mid/light={1}" -f $result.ReferenceAverage, $result.ReferenceDarkMidLight)
    Write-Output ("  output    avg={0}  dark/mid/light={1}  outline={2}%" -f $result.OutputAverage, $result.OutputDarkMidLight, $result.OutputOutlinePct)
    Write-Output ("  delta={0}  ratio={1}" -f $result.AverageDelta, $result.AverageRatio)
    Write-Output ''
}

if ($comparison.HasWarning) {
    Write-Output 'One or more pairs are darker than the reference thresholds.'
    if ($FailOnWarning) {
        exit 1
    }
}
