package mod.fricativemelon.suckermod.setup;

import mod.fricativemelon.suckermod.blocks.ModBlocks;
import mod.fricativemelon.suckermod.blocks.SuckerBlockScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

	@Override
	public World getClientWorld() {
		return Minecraft.getInstance().world; 
	}

	@Override
	public void init() {
		ScreenManager.registerFactory(ModBlocks.PLACER.container, SuckerBlockScreen::new);
		ScreenManager.registerFactory(ModBlocks.HARVESTER.container, SuckerBlockScreen::new);
		ScreenManager.registerFactory(ModBlocks.MOVER.container, SuckerBlockScreen::new);
	}

	@Override
	public ClientPlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
