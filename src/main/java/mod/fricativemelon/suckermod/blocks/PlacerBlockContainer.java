package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlacerBlockContainer extends SuckerBlockContainer {
    public PlacerBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player) {
        super(id, world, pos, inv, player, ModBlocks.PLACERBLOCK_CONTAINER);
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.PLACERBLOCK;
    }
}
