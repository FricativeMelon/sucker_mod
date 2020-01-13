package mod.fricativemelon.suckermod.items;

import mod.fricativemelon.suckermod.SuckerMod;
import mod.fricativemelon.suckermod.blocks.RotaterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import static mod.fricativemelon.suckermod.blocks.RotaterBlock.rotateBlock;

public class RotaterItem extends AbstractWandItem {
	public RotaterItem() {
		super(new Properties()
			.group(SuckerMod.setup.itemGroup).maxDamage(240));
		setRegistryName("rotateritem");
	}

	@Override
	protected BlockState effect(World world, BlockPos pos, BlockState state) {
		return RotaterBlock.rotateBlock(world, pos, state);
	}

}
