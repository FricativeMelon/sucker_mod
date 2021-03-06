package mod.fricativemelon.suckermod;

import io.netty.buffer.Unpooled;
import mod.fricativemelon.suckermod.blocks.*;
import mod.fricativemelon.suckermod.items.PipeItem;
import mod.fricativemelon.suckermod.items.RotaterItem;
import mod.fricativemelon.suckermod.setup.ClientProxy;
import mod.fricativemelon.suckermod.setup.IProxy;
import mod.fricativemelon.suckermod.setup.ModSetup;
import mod.fricativemelon.suckermod.setup.ServerProxy;
import mod.fricativemelon.suckermod.world.dimension.DepthsDimension;
import net.minecraft.block.Block;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/*
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.Collectors;
*/

// The value here should match an entry in the META-INF/mods.toml file
@Mod("suckermod")
public class SuckerMod
{
	
	public static IProxy proxy = DistExecutor.runForDist(
			() -> () -> new ClientProxy(),
			() -> () -> new ServerProxy());
	
	public static ModSetup setup = new ModSetup();
	
    // Directly reference a log4j logger.
    //private static final Logger LOGGER = LogManager.getLogger();

    public SuckerMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        //MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        setup.init();
        proxy.init();
    }
/*
    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
*/
    
    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            ModBlocks.PLACER.setBlock(event, PlacerBlock::new);
            ModBlocks.HARVESTER.setBlock(event, HarvesterBlock::new);
            ModBlocks.MOVER.setBlock(event, MoverBlock::new);
            ModBlocks.ROTATER.setBlock(event, RotaterBlock::new);
            ModBlocks.HARVESTER_ARM.setBlock(event, HarvesterArmBlock::new);

            /*event.getRegistry().register(new PlacerBlock());
            event.getRegistry().register(new HarvesterBlock());
            event.getRegistry().register(new RotaterBlock());
            event.getRegistry().register(new HarvesterArmBlock());*/

        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Item.Properties properties = new Item.Properties()
                    .group(setup.itemGroup);
            ModBlocks.PLACER.setItem(event, properties);
            ModBlocks.HARVESTER.setItem(event, properties);
            ModBlocks.MOVER.setItem(event, properties);
            ModBlocks.ROTATER.setItem(event, properties);

            /*event.getRegistry().register(new BlockItem(ModBlocks.PLACERBLOCK, properties)
                    .setRegistryName("placerblock"));
            event.getRegistry().register(new BlockItem(ModBlocks.HARVESTERBLOCK, properties)
                    .setRegistryName("harvesterblock"));*/
            /*event.getRegistry().register(new BlockItem(ModBlocks.ROTATERBLOCK, properties)
                    .setRegistryName("rotaterblock"));*/

            event.getRegistry().register(new PipeItem());
            event.getRegistry().register(new RotaterItem());

        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            ModBlocks.PLACER.setTile(event, PlacerBlockTile::new);
            ModBlocks.HARVESTER.setTile(event, HarvesterBlockTile::new);
            ModBlocks.MOVER.setTile(event, MoverBlockTile::new);

            /*event.getRegistry().register(TileEntityType.Builder.create(PlacerBlockTile::new, ModBlocks.PLACERBLOCK)
                    .build(null).setRegistryName("placerblock"));
            event.getRegistry().register(TileEntityType.Builder.create(HarvesterBlockTile::new, ModBlocks.HARVESTERBLOCK)
                    .build(null).setRegistryName("harvesterblock"));*/
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            ModBlocks.PLACER.setContainer(event, (windowId, inv, data) ->
                    new PlacerBlockContainer(windowId, data.readBlockPos(), inv));
            ModBlocks.HARVESTER.setContainer(event, (windowId, inv, data) ->
                    new HarvesterBlockContainer(windowId, data.readBlockPos(), inv));
            ModBlocks.MOVER.setContainer(event, (windowId, inv, data) ->
                    new MoverBlockContainer(windowId, data.readBlockPos(), inv));
            /*event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new HarvesterBlockContainer(windowId, SuckerMod.proxy.getClientWorld(), pos, inv, SuckerMod.proxy.getClientPlayer());
            }).setRegistryName("harvesterblock"));*/
        }
    }
}
