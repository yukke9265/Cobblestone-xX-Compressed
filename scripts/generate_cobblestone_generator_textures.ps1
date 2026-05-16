$configPaths = @(
    'configs/cobblestone_generator_s_texture_config.psd1'
    'configs/cobblestone_generator_m_texture_config.psd1'
    'configs/cobblestone_generator_l_texture_config.psd1'
)

foreach ($configPath in $configPaths) {
    & (Join-Path $PSScriptRoot 'generate_recolored_textures.ps1') -ConfigPath $configPath
}