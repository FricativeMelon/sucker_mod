package mod.fricativemelon.suckermod.setup;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

	@Override
	public World getClientWorld() {
		throw new IllegalStateException("Only run this on the client!"); 
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClientPlayerEntity getClientPlayer() {
		throw new IllegalStateException("Only run this on the client!"); 
	}
}
