package com.sucker.suckermod.setup;

import com.sucker.suckermod.blocks.ModBlocks;
import com.sucker.suckermod.blocks.SuckerBlockScreen;

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
		ScreenManager.registerFactory(ModBlocks.SUCKERBLOCK_CONTAINER, SuckerBlockScreen::new);		
	}

	@Override
	public ClientPlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
