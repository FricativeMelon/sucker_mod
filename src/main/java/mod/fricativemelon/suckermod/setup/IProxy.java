package mod.fricativemelon.suckermod.setup;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
	World getClientWorld();
	
	ClientPlayerEntity getClientPlayer();
	
	void init();
}
