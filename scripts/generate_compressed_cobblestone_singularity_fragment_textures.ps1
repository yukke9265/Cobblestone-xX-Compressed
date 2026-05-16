$configPath = Join-Path $PSScriptRoot 'configs\compressed_cobblestone_singularity_fragment_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'

& $sharedScriptPath -ConfigPath $configPath