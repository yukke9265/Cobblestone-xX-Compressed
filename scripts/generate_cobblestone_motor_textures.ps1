$configPath = Join-Path $PSScriptRoot 'configs\cobblestone_motor_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'

& $sharedScriptPath -ConfigPath $configPath