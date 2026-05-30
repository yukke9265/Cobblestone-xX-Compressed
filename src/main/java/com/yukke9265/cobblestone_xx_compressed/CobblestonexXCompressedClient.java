package com.yukke9265.cobblestone_xx_compressed;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluidTypes;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCrusherScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCentrifugeScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneAssemblyMachineScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneChemicalReactorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneDissolutionChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneEnchanterScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneExtremeCompressorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFEGeneratorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFluidMixerScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneLaserDrillScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneMelterScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneMixerScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestonePoweredFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneReactionChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCrystallizationChamberScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneTankScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.StoneBreakSimulatorScreen;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

// このクラスは専用サーバーでは読み込まれません。ここからクライアント側コードへアクセスしても安全です。
@Mod(value = CobblestonexXCompressed.MODID, dist = Dist.CLIENT)
// EventBusSubscriber を使うと、クラス内の @SubscribeEvent 付き static メソッドを自動登録できます
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID, value = Dist.CLIENT)
public class CobblestonexXCompressedClient {
    public CobblestonexXCompressedClient(ModContainer container) {
        // NeoForge がこの mod の設定画面を作成できるようにします。
        // 設定画面は Mods 画面で mod を選び、config を押すと開けます。
        // 設定項目の翻訳を en_us.json に追加するのを忘れないでください。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // クライアント側のセットアップ処理
        CobblestonexXCompressed.LOGGER.info("HELLO FROM CLIENT SETUP");
        CobblestonexXCompressed.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(() -> {
            // 水系液体は透過ピクセルを持つ water ベース texture を使うため、
            // liquid block を translucent で描画する必要があります。
            for (ModFluids.WaterBasedFluid fluid : ModFluids.WaterBasedFluid.values()) {
                ItemBlockRenderTypes.setRenderLayer(fluid.getFluidEntry().getFluidBlock().get(), RenderType.translucent());
            }
        });
    }

    @SubscribeEvent
    static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // Mekanism と同じく RegisterClientExtensionsEvent で FluidType ごとの描画情報を明示登録します。
        // bucket の液体色もこの経路を使うため、ここで tier ごとの tint を確実に分けます。
        ModFluidTypes.registerClientExtensions(event);
    }

    @SubscribeEvent
    static void resisterScreens(RegisterMenuScreensEvent event) {
        // ここで、スクリーンとメニューの関連付けを行います。これにより、特定のメニューが開かれたときに対応するスクリーンが表示されるようになります。
        event.register(ModMenuType.COBBLESTONE_FURNACE_MENU.get(), CobblestoneFurnaceScreen::new);
        event.register(ModMenuType.COBBLESTONE_POWERED_FURNACE_MENU.get(), CobblestonePoweredFurnaceScreen::new);
        event.register(ModMenuType.COBBLESTONE_EXTREME_COMPRESSOR_MENU.get(), CobblestoneExtremeCompressorScreen::new);
        event.register(ModMenuType.COBBLESTONE_CRUSHER_MENU.get(), CobblestoneCrusherScreen::new);
        event.register(ModMenuType.COBBLESTONE_FE_GENERATOR_MENU.get(), CobblestoneFEGeneratorScreen::new);
        event.register(ModMenuType.COBBLESTONE_CENTRIFUGE_MENU.get(), CobblestoneCentrifugeScreen::new);
        event.register(ModMenuType.COBBLESTONE_LASER_DRILL_MENU.get(), CobblestoneLaserDrillScreen::new);
        event.register(ModMenuType.COBBLESTONE_MIXER_MENU.get(), CobblestoneMixerScreen::new);
        event.register(ModMenuType.STONE_BREAK_SIMULATOR_MENU.get(), StoneBreakSimulatorScreen::new);
        event.register(ModMenuType.COBBLESTONE_MELTER_MENU.get(), CobblestoneMelterScreen::new);
        event.register(ModMenuType.COBBLESTONE_ASSEMBLY_MACHINE_MENU.get(), CobblestoneAssemblyMachineScreen::new);
        event.register(ModMenuType.COBBLESTONE_ENCHANTER_MENU.get(), CobblestoneEnchanterScreen::new);
        event.register(ModMenuType.COBBLESTONE_CHEMICAL_REACTOR_MENU.get(), CobblestoneChemicalReactorScreen::new);
        event.register(ModMenuType.COBBLESTONE_REACTION_CHAMBER_MENU.get(), CobblestoneReactionChamberScreen::new);
        event.register(ModMenuType.COBBLESTONE_CRYSTALLIZATION_CHAMBER_MENU.get(), CobblestoneCrystallizationChamberScreen::new);
        event.register(ModMenuType.COBBLESTONE_DISSOLUTION_CHAMBER_MENU.get(), CobblestoneDissolutionChamberScreen::new);
        event.register(ModMenuType.COBBLESTONE_FLUID_MIXER_MENU.get(), CobblestoneFluidMixerScreen::new);
        event.register(ModMenuType.COBBLESTONE_TANK_MENU.get(), CobblestoneTankScreen::new);
    }
}
