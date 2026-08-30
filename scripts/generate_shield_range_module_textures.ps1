$configPath = Join-Path $PSScriptRoot 'configs\shield_range_module_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'

& $sharedScriptPath -ConfigPath $configPath
