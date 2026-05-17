package com.yukke9265.cobblestone_xx_compressed;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yukke9265.cobblestone_xx_compressed.compat.flux.FluxNetworkCompat;
import com.yukke9265.cobblestone_xx_compressed.datagen.ModDatagen;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluidTypes;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// ここの値は META-INF/neoforge.mods.toml 内のエントリと一致している必要があります
@Mod(CobblestonexXCompressed.MODID)
public class CobblestonexXCompressed {
    // どこからでも参照できるように mod id を共通の場所で定義します
    public static final String MODID = "cobblestonexxcompressed";
    // slf4j ロガーを直接参照します
    public static final Logger LOGGER = LogUtils.getLogger();
    // "cobblestonexxcompressed" 名前空間で登録される Block 用の Deferred Register を作成します
    //public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // "cobblestonexxcompressed" 名前空間で登録される Item 用の Deferred Register を作成します
    //public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // "cobblestonexxcompressed" 名前空間で登録される CreativeModeTab 用の Deferred Register を作成します
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

        // modアイテム用に、戦闘タブの後ろへ配置される "cobblestonexxcompressed:example_tab" という ID のクリエイティブタブを作成します
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cobblestonexxcompressed")) // CreativeModeTab のタイトル用言語キーです
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.COBBLESTONE_BREAD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // この mod 専用タブには、通常版から最終 tier まで順番が分かるように並べます。
                // tier 一覧は ModItems.TierCobblestoneBread に集約したので、
                // ここではその順番をそのまま使って並べます。
                output.accept(ModItems.COMPRESSED_COBBLESTONE_ITEM.get());
                for (ModItems.TierCompressedCobblestoneItem tier : ModItems.TierCompressedCobblestoneItem.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COMPRESSED_STONE_ITEM.get());
                for (ModItems.TierCompressedStoneItem tier : ModItems.TierCompressedStoneItem.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_MACHINE_CASING_ITEM.get());
                for (ModItems.TierCobblestoneMachineCasingItem tier : ModItems.TierCobblestoneMachineCasingItem.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_TANK_ITEM.get());
                for (ModItems.TierCobblestoneTankItem tier : ModItems.TierCobblestoneTankItem.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get());
                for (ModItems.TierCompressedCobblestoneSingularityBit tier : ModItems.TierCompressedCobblestoneSingularityBit.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get());
                for (ModItems.TierCompressedCobblestoneSingularityFragment tier : ModItems.TierCompressedCobblestoneSingularityFragment.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY.get());
                for (ModItems.TierCompressedCobblestoneSingularity tier : ModItems.TierCompressedCobblestoneSingularity.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_GEM.get());
                for (ModItems.TierCobblestoneGem tier : ModItems.TierCobblestoneGem.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.AQUAMARINE_SHARD.get());
                output.accept(ModItems.TOPAZ_SHARD.get());
                output.accept(ModItems.RUBY_SHARD.get());
                output.accept(ModItems.SAPPHIRE_SHARD.get());
                for (ModItems.GemDust dust : ModItems.GemDust.values()) {
                    output.accept(dust.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_DUST.get());
                for (ModItems.TierCobblestoneDust tier : ModItems.TierCobblestoneDust.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_DIRTY_DUST.get());
                for (ModItems.TierCobblestoneDirtyDust tier : ModItems.TierCobblestoneDirtyDust.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_MIXED_DUST.get());
                for (ModItems.TierCobblestoneMixedDust tier : ModItems.TierCobblestoneMixedDust.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.DIRTY_MIXTURE.get());
                output.accept(ModItems.COAL_DUST.get());
                output.accept(ModItems.COBBLESTONE_WIRE.get());
                for (ModItems.TierCobblestoneWire tier : ModItems.TierCobblestoneWire.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_ROD.get());
                for (ModItems.TierCobblestoneRod tier : ModItems.TierCobblestoneRod.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_MOTOR.get());
                for (ModItems.TierCobblestoneMotor tier : ModItems.TierCobblestoneMotor.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_CIRCUIT.get());
                for (ModItems.TierCobblestoneCircuit tier : ModItems.TierCobblestoneCircuit.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_PROCESSOR.get());
                for (ModItems.TierCobblestoneProcessor tier : ModItems.TierCobblestoneProcessor.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_CIRCUIT_PACKAGE.get());
                for (ModItems.TierCobblestoneCircuitPackage tier : ModItems.TierCobblestoneCircuitPackage.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_BREAD.get());
                for (ModItems.TierCobblestoneBread tier : ModItems.TierCobblestoneBread.values()) {
                    output.accept(tier.getItem().get());
                }
                output.accept(ModItems.COBBLESTONE_FURNACE_ITEM.get());
                output.accept(ModItems.COBBLESTONE_POWERED_FURNACE_ITEM.get());
                output.accept(ModItems.COBBLESTONE_CRUSHER_ITEM.get());
                output.accept(ModItems.COBBLESTONE_FE_GENERATOR_ITEM.get());
                output.accept(ModItems.COBBLESTONE_CENTRIFUGE_ITEM.get());
                output.accept(ModItems.COBBLESTONE_LASER_DRILL_ITEM.get());
                output.accept(ModItems.COBBLESTONE_MIXER_ITEM.get());
                output.accept(ModItems.COBBLESTONE_MELTER_ITEM.get());
                output.accept(ModItems.COBBLESTONE_REACTION_CHAMBER_ITEM.get());
                output.accept(ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getBucketItem().get());
                for (ModFluids.TierMoltenCompressedCobblestone tier : ModFluids.TierMoltenCompressedCobblestone.values()) {
                    output.accept(tier.getFluidEntry().getBucketItem().get());
                }
                output.accept(ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getBucketItem().get());
                for (ModFluids.TierMoltenDirtyCompressedCobblestone tier : ModFluids.TierMoltenDirtyCompressedCobblestone.values()) {
                    output.accept(tier.getFluidEntry().getBucketItem().get());
                }
                for (ModItems.TierCobblestoneGeneratorItem generatorItem : ModItems.TierCobblestoneGeneratorItem.values()) {
                    output.accept(generatorItem.getItem().get());
                }

            }).build());

    // この mod クラスのコンストラクタは、mod 読み込み時に最初に実行されるコードです。
    // FML は IEventBus や ModContainer のような一部の引数型を認識して自動で渡します。
    public CobblestonexXCompressed(IEventBus modEventBus, ModContainer modContainer) {
        // mod 読み込み用に commonSetup メソッドを登録します
        modEventBus.addListener(this::commonSetup);

        // BlockEntity の在庫をホッパーやアイテムパイプへ公開する capability を登録します。
        // これを入れないと、GUI で見えていても外部自動化からは炉の中身へ触れません。
        modEventBus.addListener(this::registerCapabilities);

        // data generator 実行時に使う provider 群の登録入口です。
        // 実行時にだけ呼ばれ、通常のゲーム起動では JSON 生成処理は走りません。
        modEventBus.addListener(ModDatagen::gatherData);

        // タブが登録されるように Deferred Register を mod イベントバスへ登録します
        CREATIVE_MODE_TABS.register(modEventBus);

        //ModItemsクラスのアイテムも登録します
        ModItems.ITEMS.register(modEventBus);

        //ModBlocksクラスのブロックも登録します
        ModBlocks.BLOCKS.register(modEventBus);

        // 独自液体は FluidType、fluid 本体、液体ブロック、バケツをそれぞれ登録します。
        // 分けて登録しておくと、今後 tier 版を増やすときにも依存関係を追いやすくなります。
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModFluids.FLUID_BLOCKS.register(modEventBus);
        ModFluids.FLUID_ITEMS.register(modEventBus);

        //ModBlockEntitiesクラスのブロックエンティティも登録します
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);

        // メニュータイプを登録します。
        // これを忘れると、クライアントが画面とメニューを結び付ける時点で
        // cobblestone_furnace_menu が未登録のままになり、起動時クラッシュします。
        ModMenuType.MENU_TYPES.register(modEventBus);

        // 機械ブロック用の独自レシピを読み込めるように、
        // RecipeType と RecipeSerializer を登録します。
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        // サーバーなど、関心のあるゲームイベントを受け取るためにこのクラス自身を登録します。
        // これは *この* クラス (CobblestonexXCompressed) がイベントへ直接応答する場合にだけ必要です。
        // 下の onServerStarting() のような @SubscribeEvent 付きメソッドがないなら、この行は不要です。
        NeoForge.EVENT_BUS.register(this);

        // FML が設定ファイルを作成して読み込めるように、この mod の ModConfigSpec を登録します
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 共通セットアップ処理
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Cobblestone Furnace の BlockEntity に、外部機械向け item capability を登録します。
        // 面ごとのルールは BlockEntity 側へ寄せてあり、
        // 上面と側面は入力、底面は出力、という形で handler を返します。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_FURNACE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // Cobblestone Powered Furnace は crusher と同じ 3 スロット構成です。
        // 加工素材と CP 素材を搬入し、完成した石だけを搬出できます。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_POWERED_FURNACE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // Cobblestone Crusher も同じように item capability を公開します。
        // 入力面では加工対象と CP 素材を搬入し、出力面では完成品だけを取り出せます。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_CRUSHER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // Cobblestone FE Generator は丸石系アイテムを 1 スロット受け取り、
        // 内部の CP バッファと FE バッファを使って発電します。
        // item capability では入力専用スロットだけを公開します。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_FE_GENERATOR_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // 通常の FE 互換は NeoForge の EnergyStorage capability で公開します。
        // ここは int 上限の API なので、内部 long バッファは BlockEntity 側で丸めます。
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ModBlockEntities.COBBLESTONE_FE_GENERATOR_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getEnergyStorage(side)
        );

        // Flux Networks が存在する場合だけ独自 long capability も追加します。
        // 依存を必須にしないため、capability 本体は反射で取得します。
        FluxNetworkCompat.registerBlockEntity(event, ModBlockEntities.COBBLESTONE_FE_GENERATOR_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getFluxEnergyCapability(side));

        // Cobblestone Centrifuge は入力 1 枠、CP 投入 1 枠、出力 2 枠を持ちます。
        // 側面設定に応じて、入力専用、全出力、出力1専用、出力2専用、CP 投入を切り替えます。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_CENTRIFUGE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // Cobblestone Laser Drill も入力 1 枠、CP 投入 1 枠、出力 2 枠を持ちます。
        // Centrifuge と同じ面設定を使い、入力・出力・CP 搬入を外部自動化へ公開します。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_LASER_DRILL_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        // Cobblestone Mixer も同じ考え方で item capability を公開します。
        // 加工素材 2 スロットと CP スロットは搬入し、出力面では完成品だけを搬出します。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_MIXER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_MELTER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_MELTER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getFluidHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_REACTION_CHAMBER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_REACTION_CHAMBER_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getFluidHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_TANK_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );

        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_TANK_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getFluidHandler(side)
        );

        // Cobblestone Generator は GUI を持たず、1 出力スロットだけを全面出力で公開します。
        // さらに BlockEntity 自身も毎 tick 隣接インベントリへ押し出すので、
        // ホッパーやパイプのどちらでも同じように完成品を受け取れます。
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.COBBLESTONE_GENERATOR_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.getAutomationItemHandler(side)
        );
    }

    // SubscribeEvent を使うと、Event Bus に呼び出すメソッドを自動検出させられます
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // サーバー起動時の処理
        LOGGER.info("HELLO from server starting");
    }
}
