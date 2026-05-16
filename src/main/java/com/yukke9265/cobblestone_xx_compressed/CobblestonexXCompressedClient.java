package com.yukke9265.cobblestone_xx_compressed;

import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCrusherScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneCentrifugeScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFEGeneratorScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneMixerScreen;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestonePoweredFurnaceScreen;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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
    }

    @SubscribeEvent
    static void resisterScreens(RegisterMenuScreensEvent event) {
        // ここで、スクリーンとメニューの関連付けを行います。これにより、特定のメニューが開かれたときに対応するスクリーンが表示されるようになります。
        event.register(ModMenuType.COBBLESTONE_FURNACE_MENU.get(), CobblestoneFurnaceScreen::new);
        event.register(ModMenuType.COBBLESTONE_POWERED_FURNACE_MENU.get(), CobblestonePoweredFurnaceScreen::new);
        event.register(ModMenuType.COBBLESTONE_CRUSHER_MENU.get(), CobblestoneCrusherScreen::new);
        event.register(ModMenuType.COBBLESTONE_FE_GENERATOR_MENU.get(), CobblestoneFEGeneratorScreen::new);
        event.register(ModMenuType.COBBLESTONE_CENTRIFUGE_MENU.get(), CobblestoneCentrifugeScreen::new);
        event.register(ModMenuType.COBBLESTONE_MIXER_MENU.get(), CobblestoneMixerScreen::new);
    }
}
