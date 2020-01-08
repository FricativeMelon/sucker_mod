package com.sucker.suckermod;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.sucker.suckermod.blocks.*;
import com.sucker.suckermod.items.*;
import com.sucker.suckermod.setup.*;
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
    
    public static <T extends IForgeRegistryEntry<T>> void register(
    		final RegistryEvent.Register<T> event, T t, String name) {
    	event.getRegistry().register(t.setRegistryName(new ResourceLocation(name)));
    }
    
    public static <T extends IForgeRegistryEntry<T>> void register(
    		final RegistryEvent.Register<T> event, T t) {
    	event.getRegistry().register(t);
    }
    
    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        /*@SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new SuckerBlock());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Item.Properties properties = new Item.Properties()
                    .group(setup.itemGroup);
            event.getRegistry().register(new BlockItem(ModBlocks.SUCKERBLOCK, properties).setRegistryName("suckerblock"));
            event.getRegistry().register(new PipeItem());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(SuckerBlockTile::new, ModBlocks.SUCKERBLOCK).build(null).setRegistryName("suckerblock"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new SuckerBlockContainer(windowId, SuckerMod.proxy.getClientWorld(), pos, inv, SuckerMod.proxy.getClientPlayer());
            }).setRegistryName("suckerblock"));
        }*/
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            // register a new block here
        	SuckerMod.register(event, new SuckerBlock());
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            // register a new block here
        	Item.Properties properties = new Item.Properties().group(setup.itemGroup);
        	SuckerMod.register(event, new BlockItem(ModBlocks.SUCKERBLOCK, properties), "suckerblock");
        	SuckerMod.register(event, new PipeItem());
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        	SuckerMod.register(event, TileEntityType.Builder.create(SuckerBlockTile::new,
        			ModBlocks.SUCKERBLOCK).build(null), "suckerblock");
        }
        
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        	SuckerMod.register(event, IForgeContainerType.create(
        			(windowId, inv, data) -> {
        				BlockPos pos = data.readBlockPos();
        				return new SuckerBlockContainer(windowId,
        						SuckerMod.proxy.getClientWorld(), 
        						pos, inv,
        						SuckerMod.proxy.getClientPlayer());
        			}),"suckerblock");
        }
        
    }
}
